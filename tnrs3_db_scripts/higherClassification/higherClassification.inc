<?php

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
// Purpose:
// For every source flagged as higher classification, adds all
// other names at level of genus and below and determines their 
// family (text only) according to that classification. 
// Names for which family cannot be determined are added with family
// field left blank.
// Populates table higherClassification 
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////
// Main script
//////////////////////////////////////////////////

// Useful NOT IN rank filter to speed up queries of infraspecific taxa:
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

include "sql_create_table_higherClassification.inc";
sql_execute_multiple($sql_create_tables);

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

$src_no=1;

//////////////////////////////////////////////////
// Do for each higher classification source
//////////////////////////////////////////////////
while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
	$classificationSourceID=$row{'sourceID'};
	$classificationSourceName=$row{'sourceName'};

	echo "Classification source = $classificationSourceName:\r\n";
	echo "  Extracting names for this classification...";

	//////////////////////////////////////////////////
	// Create new temp higherClassification table
	//////////////////////////////////////////////////
	
	include "sql_create_table_higherClassificationTemp.inc";
	sql_execute_multiple($sql_create_tables);

	//////////////////////////////////////////////////
	// Inserts all names for this source into table
	// higherClassification
	//////////////////////////////////////////////////	
	
	// Insert current classification records
	$sql="
		INSERT INTO higherClassificationTemp 
		(
			classificationSourceID,
			nameID,
			parentNameID,
			family,
			joinMethod		
		)
		SELECT 
			$classificationSourceID,
			nameID,
			parentNameID,
			family,
			'source'		
		FROM `classification`
		WHERE sourceID=$classificationSourceID;
	";
	$msg_error="Failed insert records!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);
	
	// Update details
	$sql="
		UPDATE higherClassificationTemp h JOIN classification c JOIN name n
		ON h.nameID=c.nameID AND c.nameID=n.nameID
		SET 
		h.parentNameID=c.parentNameID,
		h.scientificName=n.scientificName,
		h.canonicalName=n.scientificName,
		h.nameRank=n.nameRank,
		h.nameParts=n.nameParts;
	";
	$msg_error="Failed to update name details!\r\n";
	sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
	
	// Fix canonical name
	$sql="
		UPDATE higherClassificationTemp h JOIN name n
		ON h.nameID=n.nameID
		SET 
		canonicalName=CASE
		WHEN n.nameParts=3 THEN 
		CONCAT_WS(' ',genus,specificEpithet,infraspecificEpithet) 
		WHEN n.nameParts=4 THEN 
		CONCAT_WS(' ',genus,specificEpithet,infraspecificEpithet,infraspecificEpithet2)
		END
		WHERE h.nameParts>2 AND n.nameParts>2
	";
	$msg_error="Failed to update name & rank in table newClassification!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);	

	//////////////////////////////////////////////////
	// Save classification source names to final table
	//////////////////////////////////////////////////

	echo "  Saving current classification to final table...";
	$sql ="
		INSERT INTO higherClassification 
		(
			classificationSourceID,
			nameID,
			familyNameID,
			family,
			joinMethod
		)
		SELECT
			classificationSourceID,
			nameID,
			familyNameID,
			family,
			joinMethod
		FROM higherClassificationTemp";
	$msg_error="Failed!\r\n";
	sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);	
	
	//////////////////////////////////////////////////
	// Construct summary tables of genera and families
	// Used to check for homonyms, etc., to decide if 
	// safe to update by joining on name alone
	//////////////////////////////////////////////////
	
	//////////////////////////////////////////////////
	// `genusClassificationSource` 
	//
	// For each genus, determines if homonyms exist, 
	// if genus is accepted, if genus is ONLY accepted 
	// genus (for that genus name).
	//////////////////////////////////////////////////

	// Build both tables (families and genera)		
	include "sql_create_table_genusClassificationSource.inc";
	sql_execute_multiple($sql_create_tables);

	echo "  Analyzing genera in source $classificationSourceName...";

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
			SELECT n.nameID AS familyID, scientificName AS familyName, 
			leftIndex, rightIndex
			FROM name n JOIN classification c
			ON n.nameID=c.nameID
			WHERE n.nameRank='family'
			AND c.sourceID=$classificationSourceID
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
	// Create table `familyClassificationSource`
	//
	// For each family, determines if homonyms exist, 
	// if family is ONLY accepted family (for that 
	// family name).
	// Purpose of table is to determine "safe"
	// families that can be joined by name alone
	//////////////////////////////////////////////////

	// Build family table	
	include "sql_create_table_familyClassificationSource.inc";
	sql_execute_multiple($sql_create_tables);

	echo "  Analyzing families in source $classificationSourceName...";

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
	// For each other name source, get all names
	// and attempt to determine family according
	// to current classification source. 
	////////////////////////////////////////////////// 
	
	$sql_other_sources="
		SELECT sourceID, sourceName 
		FROM source
		WHERE sourceID<>$classificationSourceID;	
	";
	$rows_other_sources = mysql_query($sql_other_sources);

	//////////////////////////////////////////////////
	// Loop through sources
	//////////////////////////////////////////////////
	
	while ($rowNameSource=mysql_fetch_array($rows_other_sources, MYSQL_ASSOC)) {
		$nameSourceID=$rowNameSource{'sourceID'};
		$nameSourceName=$rowNameSource{'sourceName'};

		echo "  Name source = $nameSourceName:\r\n";

		//////////////////////////////////////////////////
		// Create table `sourceNames` and add all names
		// at rank of genus and below for this source
		//////////////////////////////////////////////////
		
		include "sql_create_table_sourceNames.inc";
		sql_execute_multiple($sql_create_tables);
		
		# Insert genera for this source
		echo "    Extracting genera...";
		$sql="
			INSERT INTO sourceNames 
			(
				nameID,
				nameRank,
				nameParts,
				genus_id,
				genus
			)
			SELECT DISTINCT
				GENUS_ID,
				'genus',
				1,
				GENUS_ID, 
				GENUS
			FROM name_source ns JOIN genlist g
			ON ns.nameID=g.GENUS_ID
			WHERE ns.sourceID=$nameSourceID;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);		
		
		# Insert species for this source
		echo "    Extracting species...";
		$sql="
			INSERT INTO sourceNames 
			(
				nameID,
				nameRank,
				nameParts,
				genus_id,
				genus,
				species_id,
				species		
			)
			SELECT 
				species_id,
				'species',
				2,
				genus_id, 
				genus,
				species_id,
				species
			FROM name_source ns JOIN splist_genlist_combined sg JOIN name_source ns2
			ON ns.nameID=sg.genus_id AND sg.species_id=ns2.nameID
			WHERE ns.sourceID=$nameSourceID AND ns2.sourceID=$nameSourceID 
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
		
		# Insert trinomials for this source
		echo "    Extracting trinomials...";
		$sql="
			INSERT INTO sourceNames 
			(
				nameID,
				nameRank,
				nameParts,
				genus_id,
				genus,
				species_id,
				species,	
				trinomial_id,
				trinomial,
				trinomial_norank
			)
			SELECT 
				ts.infra1_id,
				'trinomial',
				3,
				genus_id, 
				genus,
				sg.species_id,
				sg.species,
				ts.infra1_id,
				species_infra1,
				species_infra1_norank
			FROM name_source ns JOIN splist_genlist_combined sg 
			JOIN name_source ns2 JOIN infra1list_splist_combined ts 
			JOIN name_source ns3
			ON ns.nameID=sg.genus_id AND sg.species_id=ns2.nameID
			AND ns2.nameID=ts.species_id AND ts.infra1_id=ns3.nameID
			WHERE ns.sourceID=$nameSourceID AND ns2.sourceID=$nameSourceID 
			AND ns3.sourceID=$nameSourceID; 
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
			
		# Insert trinomials for this source
		echo "    Extracting quadrinomials...";
		$sql="
			INSERT INTO sourceNames 
			(
				nameID,
				nameRank,
				nameParts,
				genus_id,
				genus,
				species_id,
				species,	
				trinomial_id,
				trinomial,
				trinomial_norank,
				quadrinomial_id,
				quadrinomial,
				quadrinomial_norank
			)
			SELECT 
				ts.infra1_id,
				'quadrinomial',
				3,
				genus_id, 
				genus,
				sg.species_id,
				sg.species,
				ts.infra1_id,
				species_infra1,
				species_infra1_norank,
				infra2_id,
				infra1_infra2,
				infra1_infra2_norank
			FROM name_source ns JOIN splist_genlist_combined sg 
			JOIN name_source ns2 JOIN infra1list_splist_combined ts 
			JOIN name_source ns3 JOIN infra2list_infra1list_combined qt
			JOIN name_source ns4
			ON ns.nameID=sg.genus_id AND sg.species_id=ns2.nameID
			AND ns2.nameID=ts.species_id AND ts.infra1_id=ns3.nameID
			AND ns3.nameID=qt.infra1_id AND qt.infra2_id=ns4.nameID
			WHERE ns.sourceID=$nameSourceID AND ns2.sourceID=$nameSourceID 
			AND ns3.sourceID=$nameSourceID AND ns4.sourceID=$nameSourceID; 
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		// Update current family
		echo "  Updating current families...";
		$sql="
			UPDATE sourceNames n JOIN genlist_famlist_combined gf 
			JOIN name_source ns 
			ON n.genus_id=gf.genus_id AND gf.family_id=ns.nameID
			SET 
			n.family_id=gf.family_id,
			n.family=gf.family
			WHERE ns.sourceID=$sourceID;		
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		//////////////////////////////////////////////////
		// Delete any names already in classification source
		//////////////////////////////////////////////////

		echo "  Removing names already in classification source...";
		$sql="
			UPDATE sourceNames n JOIN higherClassificationTemp h 
			ON n.nameID=h.nameID
			SET n.inSource=1;
		";
		$msg_error="Failed to mark names already in source!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_fail);	
				
		$sql="
			DELETE FROM sourceNames 
			WHERE inSource=1;
		";
		$msg_error="Failed to delete names already in source!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
				
		//////////////////////////////////////////////////
		// Attempt to "discover" family classification
		// in new source by working *up* the taxonomic hierarchy
		// for each name, on the assumption that at match
		// at a lower taxonomic level will be more likely
		// to disambiguate homonyms at higher levels
		//////////////////////////////////////////////////
				
		//////////////////////////////////////////////////
		// Quadrinomials
		//////////////////////////////////////////////////
		
		echo "  Retrieving new family by quadrinomial nameID...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.quadrinomial_id=h.nameID 
			SET 
			n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='quad nameID'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		echo "  Retrieving new family by quadrinomial name...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.quadrinomial=h.scientificName 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='quad name'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
		
		echo "  Retrieving new family by quadrinomial canonical name...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.quadrinomial_norank=h.canonicalName 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='quad canonical name'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
		
		//////////////////////////////////////////////////
		// Trinomials
		//////////////////////////////////////////////////
		
		echo "  Retrieving new family by trinomial nameID...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.trinomial_id=h.nameID 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='tri nameID'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		echo "  Retrieving new family by trinomial name...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.trinomial=h.scientificName 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='tri name'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
		
		echo "  Retrieving new family by trinomial canonical name...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.trinomial_norank=h.canonicalName 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='tri canonical name'
			WHERE h.nameRank NOT IN ($higherTaxonRank) 
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		//////////////////////////////////////////////////
		// Species
		//////////////////////////////////////////////////
		
		echo "  Retrieving new family by species nameID...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.species_id=h.nameID 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='species nameID'
			WHERE  h.nameRank='species'
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	

		echo "  Retrieving new family by species name...";
		$sql ="
			UPDATE sourceNames n JOIN higherClassificationTemp h
			ON n.species=h.scientificName 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='species name'
			WHERE h.nameRank='species'
			AND n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail);	
		
		//////////////////////////////////////////////////
		// Genera
		// This is trickier because of homonyms
		//////////////////////////////////////////////////
		
		echo "  Retrieving new family by genus...";
		$sql ="
			UPDATE sourceNames n JOIN genusClassificationSource h
			ON n.genus_id=h.genusNameID 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='genus nameID'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		sql_execute($sql,TRUE,$echo_on,'',$msg_fail);	
		
		// no homonyms names first
		$sql ="
			UPDATE sourceNames n JOIN genusClassificationSource h
			ON n.genus=h.genus 
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='genus no homonyms'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL
			AND h.hasHomonyms=0;
		";
		$msg_error="Failed to retrieve family by genus (no homonyms)!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);
		
		// Only one accepted genus names 
		$sql ="
			UPDATE sourceNames n JOIN genusClassificationSource h
			ON n.genus=h.genus
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='accepted genus'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL
			AND h.isOnlyAccepted=1;
		";
		$msg_error="Failed to retrieve family by accepted genus!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);
					
		// Genus + family
		$sql ="
			UPDATE sourceNames n JOIN genusClassificationSource h
			ON n.genus=h.genus  AND n.family=h.family
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='genus+family'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL
			AND h.genusFamilyUnique=1;
		";
		$msg_error="Failed to retrieve family by accepted genus!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);

		//////////////////////////////////////////////////
		// Family
		//////////////////////////////////////////////////
		
		echo "  Retrieving new family based original family...";

		// Family nameID
		$sql ="
			UPDATE sourceNames n JOIN familyClassificationSource h
			ON n.family_id=h.familyNameID
			SET n.newFamilyID=h.familyNameID,
			n.newFamily=h.family,
			n.joinMethod='family nameID'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		$msg_error="Failed to retrieve family by family nameID!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);		

		// Family name, no homonyms
		$sql ="
			UPDATE sourceNames n JOIN familyClassificationSource h
			ON n.family=h.family
			SET n.newFamilyID=familyNameID,
			n.newFamily=h.family,
			n.joinMethod='family no homonyms'
			WHERE n.newFamily IS NULL
			AND h.hasHomonyms=0;
		";
		$msg_error="Failed to retrieve family by family name!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Accepted family name
		$sql ="
			UPDATE sourceNames n JOIN familyClassificationSource h
			ON n.family=h.family
			SET n.newFamilyID=familyNameID,
			n.newFamily=h.family,
			n.joinMethod='accepted family'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL
			AND h.isOnlyAccepted=1;
		";
		$msg_error="Failed to retrieve family by family name!\r\n";
		sql_execute($sql,TRUE,$echo_on,'',$msg_error);	
		
		// Family name
		$sql ="
			UPDATE sourceNames n JOIN familyClassificationSource h
			ON n.family=h.family
			SET n.newFamilyID=NULL,
			n.newFamily=h.family,
			n.joinMethod='family name'
			WHERE n.newFamily IS NULL AND h.family IS NOT NULL;
		";
		$msg_error="Failed to retrieve family by family name!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);			

		//////////////////////////////////////////////////
		// Save results for this source
		//////////////////////////////////////////////////
	
		echo "  Saving results...";
		$sql ="
			INSERT INTO higherClassification 
			(
				classificationSourceID,
				nameID,
				familyNameID,
				family,
				joinMethod
			)
			SELECT
				$classificationSourceID,
				nameID,
				newFamilyID,
				newFamily,
				joinMethod
			FROM sourceNames";
		$msg_error="Failed to update families for genera in classification source!\r\n";
		sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error);					
	}
		
}

echo "  Removing temporary tables...";
$sql="
	DROP TABLE IF EXISTS higherClassificationTemp;
	DROP TABLE IF EXISTS sourceNames;
	DROP TABLE IF EXISTS genusClassificationSource;
	DROP TABLE IF EXISTS familyClassificationSource;
";
sql_execute_multiple($sql);
echo $msg_success;

?>
