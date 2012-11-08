<?php

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
// Purpose:
// For every source flagged as higher classification 
// (isHigherClassification=1), rebuilds classification, adding 
// ALL species and infraspecific taxa in database, recalculating 
// left and right indexes, and repopulating the denormalized 
// column 'family'.
//
// Attempts to join species from other sources by first by genus, 
// then by infraspecific taxon (occasionally a subspecies will 
// match even when a species does not), then by family. Includes 
// various checks for homonyms. Names which cannot be joined by 
// family are added to the root, and column family is left null.
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////
// Main script
//////////////////////////////////////////////////

echo "Rebuilding higher classifications:\r\n\r\n";

//////////////////////////////////////////////////
// Get ID and name of higher classification sources
//////////////////////////////////////////////////
$sql="
	SELECT sourceID, sourceName 
	FROM source
	WHERE isHigherClassification=1;	
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
If ($num_rows=0) die("No higher classification sources in database.\r\n");

// NOT IN rank filter to speed up queries of infraspecific taxa:
$higherTaxonRank="
	'class',
	'division',
	'family',
	'genus',
	'kingdom',
	'order',
	'phylum',
	'root',
	'section',
	'series',
	'species',
	'subclass',
	'subdivision',
	'subfamily',
	'subgenus',
	'subkingdom',
	'suborder',
	'subphylum',
	'subsection',
	'subseries',
	'subtribe',
	'superclass',
	'superdivision',
	'superorder',
	'supersection',
	'supertribe',
	'tribe'
";


//////////////////////////////////////////////////
// Create new higherClassification table
//////////////////////////////////////////////////

echo "  Creating table higherClassification...";	
include "sql_create_table_higherClassification.inc";
sql_execute_multiple($sql_create_tables);

//////////////////////////////////////////////////
// Do for each higher classification source
//////////////////////////////////////////////////
while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
	$classificationSourceID=$row{'sourceID'};
	$classificationSourceName=$row{'sourceName'};

	echo "  Classification source = $classificationSourceName:\r\n";
	echo "    Extracting higher classification...";

	//////////////////////////////////////////////////
	// Create temporary table `newClassification`
	// Holds results of classification rebuild, used
	// to replace entire classification for this source 
	// at end of process
	//////////////////////////////////////////////////
	
	$sql ="DROP TABLE IF EXISTS newClassification;";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	$sql="CREATE TABLE `newClassification` LIKE `classification`;
	";
	$msg_error="Failed to create table newClassification!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);

	// Insert current classification records
	$sql="
		INSERT INTO `newClassification` 
		SELECT * 
		FROM `classification`
		WHERE sourceID=$classificationSourceID;
	";
	$msg_error="Failed to insert original values into newClassification!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// add columns for taxonomic name & rank; we'll need these shortly
	$sql="
		ALTER TABLE newClassification
		ADD COLUMN nameRank VARCHAR(25) DEFAULT NULL,
		ADD COLUMN scientificName VARCHAR(250) DEFAULT NULL,
		ADD INDEX (nameRank),
		ADD INDEX (scientificName)
	";
	$msg_error="Failed to alter table newClassification!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
	
	// Update name, rank, and join method.
	$sql="
		UPDATE newClassification c JOIN name n
		ON c.nameID=n.nameID
		SET 
		c.scientificName=n.scientificName,
		c.nameRank=n.nameRank,
		joinMethod='inSource';
	";
	$msg_error="Failed to update name & rank in table newClassification!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);	

	//////////////////////////////////////////////////
	// Create table `genusClassificationSource` of all 
	// genera in classification source:
	//
	// For each genus, populates name and ID of genus, 
	// name and ID of family, 
	// determines if homonyms exist, if genus is accepted,
	// if genus is ONLY accepted genus (for that genus name).
	// Purpose of table is to help determine "safe"
	// genera that can be joined by name alone
	//////////////////////////////////////////////////

	// Build both tables (families and genera)		
	include "sql_create_table_genusClassificationSource.inc";
	sql_execute_multiple($sql_create_tables);

	echo "    Creating table of genera in source $classificationSourceName...";

	// Insert all genera
	$sql="
		INSERT INTO genusClassificationSource (
		genusNameID,
		genus,
		rightIndex,
		leftIndex		
		)
		SELECT 
		n.nameID,
		n.scientificName,
		c.rightIndex,
		c.leftIndex	
		FROM classification c JOIN name n
		ON c.nameID=n.nameID
		WHERE n.nameRank='genus' AND c.sourceID=$classificationSourceID;
	";
	$msg_error="Failed to insert genera from classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// get their families
	$sql="
		UPDATE genusClassificationSource g,
		(
			SELECT 
			nameID AS familyID, scientificName AS familyName, 
			leftIndex, rightIndex
			FROM newClassification
			WHERE nameRank='family'
		) AS f
		SET 
		g.familyNameID=f.familyID, 
		g.family=f.familyName
		WHERE  
		f.leftIndex < g.leftIndex AND f.rightIndex > g.rightIndex;	
	";
	$msg_error="Failed to update families for genera in classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
	
	// flag genera with homonyms
	$sql="
		UPDATE genusClassificationSource g JOIN 
		(
			SELECT genus, count(*) as tot
			FROM genusClassificationSource
			GROUP BY genus
			HAVING tot>1
		) AS h
		ON g.genus=h.genus
		SET 
		g.hasHomonyms=1;
	";
	$msg_error="Failed to flag classification source genera with homonyms!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// flag accepted genera
	$sql="
		UPDATE genusClassificationSource g JOIN 
		(
			SELECT s.nameID
			FROM synonym s JOIN genusClassificationSource n
			ON s.nameID=n.genusNameID
			WHERE s.sourceID=$classificationSourceID 
			AND (s.acceptance='A' OR s.acceptance='Accepted')
		) AS a
		ON g.genusNameID=a.nameID
		SET 
		g.isOnlyAccepted=1;
	";
	$msg_error="Failed to accepted genera in classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// reset flag to 0 if >1 name flagged as accepted for this genus name
	$sql="
		UPDATE genusClassificationSource g JOIN 
		(
			SELECT genus, count(*) as tot
			FROM synonym s JOIN genusClassificationSource g
			ON s.nameID=g.genusNameID
			WHERE s.sourceID=$classificationSourceID 
			AND (s.acceptance='A' OR s.acceptance='Accepted')
			GROUP BY genus
			HAVING tot>1
		) AS a
		ON g.genus=a.genus
		SET g.isOnlyAccepted=0
		WHERE g.isOnlyAccepted=1
	";
	$msg_error="Failed to non-unique accepted genera in classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// flag genera where genus+family is not unique in classification source
	$sql="
		UPDATE genusClassificationSource g JOIN 
		(
			SELECT genus, family, count(*) as tot
			FROM genusClassificationSource
			GROUP BY genus, family
			HAVING tot>1
		) AS h
		ON g.genus=h.genus
		SET g.genusFamilyUnique=0;
	";
	$msg_error="Failed to flag genera where genus+family is not unique!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);
	
	//////////////////////////////////////////////////
	// Create table `familyClassificationSource` of  
	// all families in classification source:
	//
	// For each family, populates name and ID of family, 
	// determines if homonyms exist, if family is ONLY
	// accepted family (for that family name).
	// Purpose of table is to determine "safe"
	// families that can be joined by name alone
	//////////////////////////////////////////////////

	// Build family table	
	include "sql_create_table_familyClassificationSource.inc";
	sql_execute_multiple($sql_create_tables);

	echo "    Creating table of families in source $classificationSourceName...";

	// Insert all families
	$sql="
		INSERT INTO familyClassificationSource (
		familyNameID,
		family,
		rightIndex,
		leftIndex		
		)
		SELECT 
		n.nameID,
		n.scientificName,
		c.rightIndex,
		c.leftIndex	
		FROM classification c JOIN name n
		ON c.nameID=n.nameID
		WHERE n.nameRank='family' AND c.sourceID=$classificationSourceID;
	";
	$msg_error="Failed to insert families from classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// flag families with homonyms
	$sql="
		UPDATE familyClassificationSource f JOIN 
		(
			SELECT family, count(*) as tot
			FROM familyClassificationSource
			GROUP BY family
			HAVING tot>1
		) AS h
		ON f.family=h.family
		SET f.hasHomonyms=1;
	";
	$msg_error="Failed to flag classification source families with homonyms!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// flag accepted families
	$sql="
		UPDATE familyClassificationSource f JOIN 
		(
			SELECT f.familyNameID
			FROM synonym s JOIN familyClassificationSource f
			ON s.nameID=f.familyNameID
			WHERE s.sourceID=$classificationSourceID 
			AND (s.acceptance='A' OR s.acceptance='Accepted')
		) AS a
		ON f.familyNameID=a.familyNameID
		SET f.isOnlyAccepted=1;
	";
	$msg_error="Failed to accepted families in classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// reset flag to 0 if >1 accepted name for this family
	$sql="
		UPDATE familyClassificationSource f JOIN 
		(
			SELECT family, count(*) as tot
			FROM synonym s JOIN familyClassificationSource f
			ON s.nameID=f.familyNameID
			WHERE s.sourceID=$classificationSourceID 
			AND (s.acceptance='A' OR s.acceptance='Accepted')
			GROUP BY family
			HAVING tot=1
		) AS a
		ON f.family=a.family
		SET f.isOnlyAccepted=0;
	";
	$msg_error="Failed to accepted families in classification source!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);

	//////////////////////////////////////////////////
	// Join each remaining source to current
	// higher classification
	////////////////////////////////////////////////// 

	// Get all sources other than than current higher classification source.
	$sql_other_sources="
		SELECT sourceID, sourceName 
		FROM source
		WHERE sourceID<>$classificationSourceID;	
	";
	$rows_other_sources = mysql_query($sql_other_sources);

	// Loop through each source
	while ($rowNameSource=mysql_fetch_array($rows_other_sources, MYSQL_ASSOC)) {
		$nameSourceID=$rowNameSource{'sourceID'};
		$nameSourceName=$rowNameSource{'sourceName'};

		echo "    Name source = $nameSourceName:\r\n";

		//////////////////////////////////////////////////
		// Create table `speciesCurrentSource` consisting
		// of all species from current source
		//
		// Includes nameID of species, names and nameIDs
		// of family and genus for that species. In addition,
		// contains flag indicating whether
		// it is safe to join by infraspecific taxa, by
		// genus, or failing genus, by family. 
		// Prior to joining by name will attempt to join 
		// by nameID, which implies that (genus) name+author 
		// matches exactly to an existing genus in higher
		// classification.
		//////////////////////////////////////////////////

		echo "      Creating species table...";

		//////////////////////////////////////////////////
		// Create table `speciesCurrentSource` and add 
		// species
		//////////////////////////////////////////////////

		include "sql_create_table_speciesCurrentSource.inc";
		sql_execute_multiple($sql_create_tables);

		// Insert species from this source
		$sql="
			INSERT INTO `speciesCurrentSource` (
			nameSourceID,
			nameID,
			parentNameID,
			species,
			rightIndex,
			leftIndex,
			genus
			)
			SELECT  
			s.sourceID,
			n.nameID,
			c.parentNameID,
			scientificName,
			c.rightIndex,
			c.leftIndex,
			n.genus
			FROM name_source s JOIN name n JOIN classification c
			ON s.nameID=n.nameID AND n.nameID=c.nameID
			WHERE s.sourceID=$nameSourceID 
			AND c.sourceID=$nameSourceID 
			AND n.nameRank='species';
		";
		$msg_error="Failed to insert species for source=$nameSourceName!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);

		
		// Flag species with infraspecific taxa
		// this will save time later
		echo "      Flagging species with infraspecific taxa...";
		$sql="
			UPDATE `speciesCurrentSource` s JOIN 
			(
				SELECT DISTINCT parentNameID
				FROM classification c 
				WHERE c.sourceID=$nameSourceID
			) AS c
			ON s.nameID=c.parentNameID
			SET s.hasChildren=1;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);


		//////////////////////////////////////////////////
		// Add genus and family for each species
		//////////////////////////////////////////////////
		
		// Populate most genus names and IDs the quick way
		// Will miss those species linked to infrageneric taxa
		// Saves time to do this first
		echo "      Updating genusNameID via parentNameID..";
		$sql="
			UPDATE speciesCurrentSource s JOIN name n
			ON s.parentNameID=n.nameID
			SET 
			s.genusNameID=n.nameID,
			s.genus=n.scientificName
			WHERE n.nameRank='genus';	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		// Mop up remaining genera using right and left indices  
		echo "      Updating genusNameID using right and left indices...";
		$sql="
			UPDATE `speciesCurrentSource` sp,
			(
				SELECT n.nameID AS genusNameID, n.scientificName as genus, 
				c.leftIndex, c.rightIndex
				FROM name AS n JOIN classification AS c
				ON n.nameID=c.nameID
				WHERE c.sourceID=$nameSourceID AND n.nameRank='genus'
			) AS g
			SET 
			sp.genusNameID=g.genusNameID, 
			sp.genus=g.genus
			WHERE  
			sp.genusNameID IS NULL
			AND
			g.leftIndex < sp.leftIndex AND g.rightIndex > sp.rightIndex
			;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		// Populate family fields for all species using right and left indices  
		echo "      Updating familyName and familyNameID using right and left indices...";
		$sql="
			UPDATE `speciesCurrentSource` sp,
			(
				SELECT n.nameID AS familyID, n.scientificName AS family, 
				c.leftIndex, c.rightIndex
				FROM name AS n INNER JOIN classification AS c
				ON n.nameID=c.nameID
				WHERE c.sourceID=$nameSourceID 
				AND n.nameRank='family'
			) AS f
			SET sp.familyNameID=f.familyID, 
			sp.family=f.family
			WHERE  
			f.leftIndex < sp.leftIndex AND f.rightIndex > sp.rightIndex;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// Flag genera and families with homonyms 
		//////////////////////////////////////////////////

		// Flag genera with homonyms
		echo "      Flagging genera with homonyms...";
		$sql="
			UPDATE speciesCurrentSource s JOIN (
				SELECT n.scientificName as genus, count(*) as tot
				FROM name n JOIN name_source s
				ON s.nameID=n.nameID 
				WHERE s.sourceID=$nameSourceID AND n.nameRank='genus'
				GROUP BY n.scientificName
				HAVING tot>1
			) as g 
			ON s.genus=g.genus
			SET s.hasHomonymsGenus=1;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		// Flag families with homonyms
		echo "      Flagging families with homonyms...";
		$sql="
			UPDATE speciesCurrentSource s JOIN (
				SELECT n.scientificName as family, count(*) as tot
				FROM name n JOIN name_source s
				ON s.nameID=n.nameID 
				WHERE s.sourceID=$nameSourceID AND n.nameRank='family'
				GROUP BY n.scientificName
				HAVING tot>1
			) as g
			ON s.family=g.family
			SET s.hasHomonymsFamily=1;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// Extract infraspecific taxa  
		//////////////////////////////////////////////////
		
		echo "      Extracting infraspecific taxa for current source..";

		// Create table of all infraspecific taxa in current source
		$sql ="DROP TABLE IF EXISTS infraCurrentSource;";
		$msg_error="failed to drop table infraCurrentSource!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		$sql="
			CREATE TABLE infraCurrentSource LIKE classification;
		";
		$msg_error="Failed to create table infraCurrentSource!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
				
		// Insert all infraspecific taxa for species in current source
		$sql="
			INSERT INTO `infraCurrentSource` 
			SELECT DISTINCT c.*
			FROM classification AS c,
			(
			SELECT nameID, rightIndex, leftIndex
			FROM `speciesCurrentSource`
			WHERE hasChildren=1
			) AS sp, 
			(
			SELECT n.nameID, c.parentNameID, leftIndex, rightIndex
			FROM name AS n INNER JOIN classification AS c
			ON n.nameID=c.nameID
			WHERE n.nameRank NOT IN($higherTaxonRank)
			AND c.sourceID=$nameSourceID
			) AS infra
			WHERE c.nameID=infra.nameID
			AND c.sourceID=$nameSourceID
			AND infra.leftIndex > sp.leftIndex AND infra.rightIndex < sp.rightIndex
		";
		$msg_error="Failed to insert original values into newClassification!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);		
		
		// add columns for taxonomic name & rank
		$sql="
			ALTER TABLE infraCurrentSource
			ADD COLUMN nameRank VARCHAR(25) DEFAULT NULL,
			ADD COLUMN scientificName VARCHAR(250) DEFAULT NULL,
			ADD INDEX (nameRank),
			ADD INDEX (scientificName);	
		";
		$msg_error="Failed to alter table infraCurrentSource!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Update name, rank, and join method.
		$sql="
			UPDATE infraCurrentSource c JOIN name n
			ON c.nameID=n.nameID
			SET 
			c.scientificName=n.scientificName,
			c.nameRank=n.nameRank;
		";
		$msg_error="Failed to update name & rank in table newClassification!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$infraCurrentSource);			
	
		//////////////////////////////////////////////////
		// Flag infraspecific taxa in classification
		// source, and flag species linked to classification  
		// via infraspecific taxa 
		//////////////////////////////////////////////////

		echo "      Flagging species linked to classification via infraspecific taxa...";
		$sql="
			UPDATE 
			speciesCurrentSource s, 
			infraCurrentSource,
			(
				SELECT nameID, leftIndex, rightIndex
				FROM newClassification
				WHERE nameRank NOT IN ($higherTaxonRank)
			) AS infraClassificationSource
			SET 
			s.linkedInfraID=infraCurrentSource.nameID,
			s.joinMethod='infraID',
			s.newFamily=infraClassificationSource.family,
			infraCurrentSource.family=infraClassificationSource.family,		
			infraCurrentSource.joinMethod='inSource'
			WHERE s.hasChildren=1
			AND infraCurrentSource.nameID=infraClassificationSource.nameID 
			AND infraCurrentSource.leftIndex > s.leftIndex 
			AND infraCurrentSource.rightIndex < s.rightIndex;
		";
		sql_execute($sql,TRUE,$echo_on,'',$msg_fail);	
		
	
		///////////////////////////////////////////////////////////
		// Determine join method to be used to load each species
		// to classification; update ID of parent taxon needed for 
		// loading.
		//
		// Join methods & flags as follows, in this order:
		//
		// 2. 'inSourceSpeciesID': Exact match to species nameID
		// 3. 'childInSourceSpecies'; Infraspecific taxon match via
		//		nameID of species
		// 4. 'genusNameID': Exact match by genus nameID
		// 5. 'infraNameID': Exact match by infraspecific 
		// 		nameID
		// 6. 'genusNoHomonyms': text match by genus, no homonyms in
		//		either source
		// 7. 'genusAccepted': text match by genus, no homonyms
		//		in current source, genus in classification source has
		//		homonyms but is only accepted genus
		// 8. 'genusFamily': join by genus+family, where 
		//		combination is unique in both sources
		// 9. 'familyNameID': exact match by family
		// 10. 'family': text match by family, no homonyms in
		//		either source
		// 11. 'familyAccepted': text match by family, no
		//		homonyms in current source, homonyms in classification
		//		source, but classification family is only accepted
		//		family
		// 121. 'familyPossibleHomonyms': doesn't satisfy any above criteria, 
		//		but text family name still present in classification 
		//		source. Add by joining randomly to one of matching 
		//		families.
		// 13. 'noMatch': species can't be joined by any of above
		//		methods. Genus is joined to root. Family is left 
		//		blank.
		///////////////////////////////////////////////////////////


		//////////////////////////////////////////////////
		// 2. Join by species nameID
		//////////////////////////////////////////////////
		
		// Flag species species already in new classification
		echo "      Flagging species already in classification...";
		$sql="
			UPDATE `speciesCurrentSource` s JOIN newClassification c
			ON s.nameID=c.nameID
			SET s.joinMethod='inSource'
			s.newFamily=c.family;
		";
		$msg_error="failed to flag species in source='$nameSourceName' already in  classification '$classificationSourceName'!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);
				
		//////////////////////////////////////////////////
		// 3. Flag subspecies with species in classification
		//////////////////////////////////////////////////
				
		// While we're at it, flag any children of "inSource" infraspecific
		// taxa which are not themselves in source
		// we'll need to add those separately
		$sql="
			UPDATE 
			(
				SELECT leftIndex, rightIndex
				FROM infraCurrentSource 
				WHERE joinMethod='inSource'
			) AS parent,
			infraCurrentSource AS child
			SET 
			child.joinMethod='inSourceParentSpecies'
			child.family=s.newFamily
			WHERE child.joinMethod IS NULL
			AND child.leftIndex > parent.leftIndex 
			AND child.rightIndex < parent.rightIndex;			
		";
		$msg_error="Failed to flag children of infraspecific taxa linked to classification source!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);

		//////////////////////////////////////////////////
		// 4. Join by genus nameID
		//////////////////////////////////////////////////

		echo "      Joining species by genus nameID...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN genusClassificationSource g 
			ON s.genusNameID=g.genusNameID
			SET 
			s.newGenusNameID=g.genusNameID, 
			s.joinMethod='genusNameID',
			s.newFamilyNameID=g.familyNameID,
			s.newFamily=g.family
			WHERE s.joinMethod IS NULL;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);
		
		//////////////////////////////////////////////////
		// 5. Join by infraspecific taxon
		//////////////////////////////////////////////////

		echo "      Joining species by linked infraspecific taxa...";
		// This is tricky. Need to retrieve the nameID of the
		// ancestor genus of the linked infraspecific taxon. 
		// Right and left indexes MUST be retrieved from the 
		// instance of the name in classification source, NOT 
		// current source!
		$sql="	
			UPDATE speciesCurrentSource s, 
			(
				SELECT c.nameID, c.rightIndex, c.leftIndex
				FROM newClassification c JOIN infraCurrentSource i
				ON c.nameID=i.nameID
				WHERE i.joinMethod='inSource'
			) AS infra,
			(
				SELECT genusNameID, rightIndex, leftIndex
				FROM genusClassificationSource
			) AS g		
			SET 
			s.newGenusNameID=g.genusNameID, 
			s.newFamilyNameID=g.familyNameID,
			s.newFamily=g.family
			s.joinMethod='infraNameID'
			WHERE s.linkedInfraID IS NOT NULL
			AND s.linkedInfraID=infra.nameID
			AND infra.leftIndex > g.leftIndex
			AND infra.rightIndex < g.rightIndex
			AND s.joinMethod IS NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 3. Join by genus, no homonyms
		//////////////////////////////////////////////////

		echo "      Joining species by genus (no homonyms)...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN genusClassificationSource g
			ON s.genus=g.genus
			SET 
			s.newGenusNameID=g.genusNameID, 
			s.joinMethod='genusNoHomonyms',
			s.newFamilyNameID=g.familyNameID,
			s.newFamily=g.family
			WHERE s.hasHomonymsGenus=0 AND g.hasHomonyms=0
			AND s.joinMethod IS NULL;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 4. Join by accepted genus 
		//////////////////////////////////////////////////

		echo "      Joining species by accepted genus...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN genusClassificationSource g
			ON s.genus=g.genus
			SET 
			s.newGenusNameID=g.genusNameID, 
			s.joinMethod='genusAccepted',
			s.newFamilyNameID=g.familyNameID,
			s.newFamily=g.family
			WHERE s.hasHomonymsGenus=0 
			AND g.isOnlyAccepted=1
			AND s.joinMethod IS NULL;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 5. Join by genus (+ family)
		//////////////////////////////////////////////////

		echo "      Joining species by genus+family...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN genusClassificationSource g
			ON s.genus=g.genus AND s.family=g.family
			SET 
			s.newGenusNameID=g.genusNameID, 
			s.joinMethod='genusFamily',
			s.newFamilyNameID=g.familyNameID,
			s.newFamily=g.family
			WHERE s.genusFamilyUnique=1 
			AND s.joinMethod IS NULL;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 6. Join by family nameID
		//////////////////////////////////////////////////

		echo "      Joining species by family nameID...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN familyClassificationSource f
			ON s.familyNameID=f.familyNameID
			SET 
			s.newFamilyNameID=f.familyNameID,
			s.newFamily=f.family,
			s.newGenusNameID=s.genusNameID,
			s.joinMethod='familyNameID'
			WHERE s.joinMethod IS NULL;	
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 7. Join by family 
		//////////////////////////////////////////////////

		echo "      Joining species by family...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN familyClassificationSource f 
			ON s.family=f.family
			SET 
			s.newFamilyNameID=f.familyNameID, 
			s.newFamily=f.family,
			s.newGenusNameID=s.genusNameID,
			s.joinMethod='family'
			WHERE s.joinMethod IS NULL
			AND s.hasHomonymsFamily=0 AND f.hasHomonyms=0;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 8. Join by accepted family 
		//////////////////////////////////////////////////

		echo "      Joining species by accepted family...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN familyClassificationSource f 
			ON s.family=f.family
			SET 
			s.newFamilyNameID=f.familyNameID,
			s.newFamily=f.family, 
			s.newGenusNameID=s.genusNameID,
			s.joinMethod='familyAccepted'
			WHERE s.joinMethod IS NULL
			AND s.hasHomonymsFamily=0 AND f.isOnlyAccepted=1
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);
		
		//////////////////////////////////////////////////
		// 9. Join by family, despite possible homonym
		// 	  problems. For families with multiple entries
		//    (homonyms) will randomly assign one to this 
		//    species. Not the best, but better than nothing...
		//////////////////////////////////////////////////

		echo "      Joining species by family (possible homonyms)...";
		$sql="	
			UPDATE speciesCurrentSource s JOIN familyClassificationSource f 
			ON s.family=f.family
			SET 
			s.newFamilyNameID=f.familyNameID, 
			s.newFamily=f.family,
			s.newGenusNameID=s.genusNameID,
			s.joinMethod='familyPossibleHomonyms'
			WHERE s.joinMethod IS NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// 10. Can't match, join genus to node above root.
		//	  Could fail if have polytomy above root?
		// 	  This will cause name to display with no 
		//	  family in the tnrs. Perhaps better solution
		//	  would be to add a node "Incertae sedis" to 
		//    the entire TNRS database just above the root. 
		//	  Need to think about this...
		//////////////////////////////////////////////////

		//////////////////////////////////////////////////
		// Flag remainder of infraspecific taxa for adding, 
		// as long as they are ultimately child of a species 
		// to be added or already in source
		//////////////////////////////////////////////////
		
		echo "      Flagging remaining infraspecific taxa...";

		// Flag children of species in source
		$sql="
			UPDATE speciesCurrentSource s,
			infraCurrentSource i
			SET 
			i.joinMethod='speciesInSourceChild',
			i.parentNameID=s.nameID,
			i.family=s.newFamily		
			WHERE s.hasChildren=1
			AND s.joinMethod='inSource'
			AND i.joinMethod IS NULL
			AND i.leftIndex > s.leftIndex
			AND i.rightIndex < s.rightIndex	
		";
		$msg_error="Failed to flag children of species in classification source!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Flag children of species to be added
		$sql="
			UPDATE speciesCurrentSource s,
			infraCurrentSource i
			SET 
			i.joinMethod='speciesAddedChild',
			i.parentNameID=s.nameID,
			i.family=s.newFamily			
			WHERE s.hasChildren=1
			AND s.joinMethod<>'inSource'
			AND i.joinMethod IS NULL
			AND i.leftIndex > s.leftIndex
			AND i.rightIndex < s.rightIndex	
		";
		$msg_error="Failed to flag remaining infraspecific taxa!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);		

		//////////////////////////////////////////////////
		// Add species, plus genera and infraspecific 
		// taxa, where applicable. 
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// Insert new genera
		// These will be genera of 'noMatch' species and species 
		// joined by family only (not genus+family)
		// 
		// Rather complicated because of need to
		// extract duplicate genus records and  
		// update the family nameID.
		//////////////////////////////////////////////////

		echo "      Adding new genera...";
		
		// Create table of genera to add
		$sql ="DROP TABLE IF EXISTS generaToAdd;";
		$msg_error="failed to drop table generaToAdd!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		$sql="
			CREATE TABLE generaToAdd LIKE classification;
		";
		$msg_error="Failed to create table generaToAdd!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Add new genera linked by family or unmatched
		$sql = "
			INSERT INTO generaToAdd (
				sourceID,	
				nameID,
				parentNameID,
				family,
				joinMethod
			)	
			SELECT DISTINCT	
				$classificationSourceID,
				newGenusNameID,
				newFamilyNameID,
				family,
				joinMethod
			FROM speciesCurrentSource
			WHERE 
			joinMethod IN ('family','familyNameID','familyAccepted','familyPossibleHomonyms')
			OR joinMethod='noMatch';	
		";
		$msg_error="Failed to add new genera to table generaToAdd!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	

		// Create table of UNIQUE genera to add
		$sql ="DROP TABLE IF EXISTS generaToAddUnique;";
		$msg_error="failed to drop table generaToAddUnique!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		$sql="
			CREATE TABLE generaToAddUnique LIKE classification;
		";
		$msg_error="Failed to create table generaToAddUnique!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	

		// Extract unique genera
		$sql = "
			INSERT INTO generaToAddUnique (
				sourceID,	
				nameID
			)		
			SELECT DISTINCT
				sourceID,	
				nameID
			FROM generaToAdd;	
		";
		$msg_error="Failed to add unique genera to table generaToAddUnique!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
/*
		// Mark genera already in classification
		// Shouldn't be any, but just in case...
		$sql="
			UPDATE generaToAddUnique g JOIN newClassification c
			ON g.nameID=c.nameID
			SET g.joinMethod='inSource';
		";
		$msg_error="Failed to mark genera already in classification!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
*/						
		// Update genera linked to known families
		$sql="
			UPDATE generaToAddUnique u JOIN generaToAdd g
			ON u.nameID=g.nameID
			SET 
				u.parentNameID=g.parentNameID,
				u.family=g.family,
				u.joinMethod=g.joinMethod
			WHERE 
			g.joinMethod IN ('family','familyNameID','familyAccepted','familyPossibleHomonyms')
			AND u.joinMethod IS NULL;					
		";
		$msg_error="Failed to mark new genera with families in source!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Update remainder: genera without families
		$sql="
			UPDATE generaToAddUnique 
			SET 
				parentNameID=NULL,
				family=NULL,
				joinMethod='noMatch'
			WHERE joinMethod IS NULL;					
		";
		$msg_error="Failed to update genera without families!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);			
		
		// Update species linked to genera in source
		// This may be necessary if some genera that have been
		// joined >1 way
		$sql="
			UPDATE speciesCurrentSource s JOIN generaToAddUnique u
			ON s.newGenusNameID=u.nameID
			SET 
				s.newFamilyNameID=u.parentNameID,
				s.family=u.family,
				s.joinMethod=u.joinMethod
			WHERE s.joinMethod<>u.joinMethod				
		";
		$msg_error="Failed to update families for genera in speciesCurrentSource!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);					

		// add new genera
		$sql="	
			INSERT INTO newClassification (
			sourceID,	
			nameID,
			parentNameID,
			family,
			nameRank,
			joinMethod
			)
			SELECT 
			sourceID,
			nameID,
			parentNameID,
			family,
			'genus',
			joinMethod
			FROM generaToAddUnique;	
		";
		$msg_error="Failed to add new genera!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);								
		
		//////////////////////////////////////////////////
		// Add new species
		//////////////////////////////////////////////////

		echo "      Adding species by genus nameID...";
		$sql="
			INSERT INTO newClassification (
			sourceID,	
			nameID,
			parentNameID,
			family,
			nameRank,
			joinMethod			)
			SELECT DISTINCT
			$classificationSourceID,
			nameID,
			newGenusNameID,
			newFamily,
			'species',
			joinMethod
			FROM speciesCurrentSource
			WHERE newGenusNameID IS NOT NULL AND joinMethod<>'inSource';
		";
		$msg_error="Failed!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);

		//////////////////////////////////////////////////
		// Add infraspecific taxa NOT already in source
		// WHERE condition excludes infraspecific taxa 
		// already in source. Note that children of such 
		// taxa ARE added. I think this is OK...
		//////////////////////////////////////////////////

		echo "      Adding infraspecific taxa...";
		$sql="
			INSERT INTO newClassification (
			sourceID,	
			nameID,
			parentNameID,
			family,
			nameRank,
			joinMethod
			)
			SELECT DISTINCT
			$classificationSourceID,
			nameID,
			parentNameID,
			family,
			nameRank,
			joinMethod
			FROM infraCurrentSource 
			WHERE joinMethod NOT LIKE 'inSource%';
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);
	}

	//////////////////////////////////////////////////
	// Build higher classification
	//////////////////////////////////////////////////

	$sql="
		INSERT INTO higherClassification (
		sourceID,
		nameID,
		parentNameID,
		family,
		joinMethod
		) 
		SELECT 
		sourceID,
		nameID,
		parentNameID,
		family,
		joinMethod
		FROM newClassification;
	";
	$msg_error="Failed to add new classification records!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);

}
/*
echo "    Removing temporary tables...";
$sql="
	DROP TABLE IF EXISTS newClassification;
	DROP TABLE IF EXISTS genusClassificationSource;
	DROP TABLE IF EXISTS familyClassificationSource;
	DROP TABLE IF EXISTS speciesCurrentSource;
	DROP TABLE IF EXISTS infraCurrentSource;
	DROP TABLE IF EXISTS generaToAdd;
	DROP TABLE IF EXISTS generaToAddUnique;
";
sql_execute_multiple($sql);
echo $msg_success;
*/
?>
