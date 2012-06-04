<?php

////////////////////////////////////////////////////////////////////
// Populates table splist_genlist_combined with all species x genus
// combinations in database. Retrieves all combinations within each 
// source, then combines all distinct combinations among all sources.
////////////////////////////////////////////////////////////////////

echo "Populating splist_genlist_combined:\r\n";

////////////////////////////////////////////////////////////////////
// Create temporary tables to hold all species nameID x genus nameID 
// combinations from all sources
////////////////////////////////////////////////////////////////////

$sql = "DROP TABLE IF EXISTS temp_sg_all;";
$msg_error = "failed to drop previous table temp_sg_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
$sql = "
	CREATE TABLE temp_sg_all (
	speciesNameID INTEGER UNSIGNED NOT NULL,
	specificEpithet VARCHAR(200) NOT NULL,
	genusNameID INTEGER UNSIGNED DEFAULT NULL,
	genus  VARCHAR(100) DEFAULT NULL,
	rightIndex INTEGER UNSIGNED DEFAULT NULL,
	leftIndex INTEGER UNSIGNED DEFAULT NULL,
	INDEX (rightIndex),
	INDEX (leftIndex),
	INDEX (speciesNameID),
	INDEX (specificEpithet),
	INDEX (genusNameID),
	INDEX (genus)
	);
";
$msg_error = "failed to create table temp_sg_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

////////////////////////////////////////////////////////////////////
// Set up loop to toggle though all sources
////////////////////////////////////////////////////////////////////

foreach ($src_array as $src) {

	echo "  Inserting species for source='$src'...";
		
	// get sourceID
	$sql="
		SELECT sourceID
		FROM source
		WHERE sourceName='$src';	
	";
	$sourceID=sql_get_first_result($sql,'sourceID');
		
	////////////////////////////////////////////////////////////////////
	// Create temporary table to hold species for this source only
	////////////////////////////////////////////////////////////////////
			
	// Create temporary table to hold species for this source only
	$sql = "DROP TABLE IF EXISTS temp_sg_source;";
	$msg_error = "failed to drop previous table temp_sg_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	$sql = "
		CREATE TABLE temp_sg_source LIKE temp_sg_all;
	";
	$msg_error = "failed to create table temp_sg_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	////////////////////////////////////////////////////////////////////	
	// Insert species classified by this source, plus genus
	// IF the immediate parent is a genus
	////////////////////////////////////////////////////////////////////
	$sql = "
		INSERT INTO temp_sg_source (
		genusNameID,
		genus,
		speciesNameID, 
		specificEpithet, 
		leftIndex, 
		rightIndex
		)
		SELECT DISTINCT 
		g.nameID,
		g.genus,
		SPECIES_ID, 
		SPECIES, 
		leftIndex, 
		rightIndex
		FROM splist s JOIN classification c
		ON s.SPECIES_ID=c.nameID
		JOIN 
		(
			SELECT n.nameID, scientificName AS genus
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE nameRank='genus' AND ns.sourceID=$sourceID
		) AS g
		ON c.parentNameID=g.nameID		
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert species linke to genus as immediate parent!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

	////////////////////////////////////////////////////////////////////
	// Insert species classified by this source, where immediate parent 
	// is NOT a genus. For these species, genera will have to be added
	// in the next step using tree traversal
	// The extra nameRank conditions of the subquery speed it up
	////////////////////////////////////////////////////////////////////
	
	$sql = "
		INSERT INTO temp_sg_source (
		speciesNameID, 
		specificEpithet, 
		leftIndex, 
		rightIndex
		)
		SELECT DISTINCT 
		SPECIES_ID, 
		SPECIES, 
		leftIndex, 
		rightIndex
		FROM splist s JOIN classification c
		ON s.SPECIES_ID=c.nameID
		JOIN 
		(
			SELECT n.nameID
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE nameRank<>'genus' 
			AND nameRank<>'species'
			AND nameRank<>'subspecies'
			AND nameRank<>'variety'
			AND nameRank<>'forma'
			AND ns.sourceID=$sourceID
		) AS g
		ON c.parentNameID=g.nameID		
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert species not linked to genus as immediate parent!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
	////////////////////////////////////////////////////////////////////
	// For the remainder, the slow way, using left and right indexes
	////////////////////////////////////////////////////////////////////

	echo "  Completing genera...";
	$sql = "
		UPDATE temp_sg_source s,
		(
			SELECT DISTINCT g.GENUS_ID, g.GENUS, leftIndex, rightIndex
			FROM classification c JOIN genlist g
			ON c.nameID=g.GENUS_ID
			WHERE sourceID=$sourceID
		) AS g
		SET 
		s.genusNameID=g.GENUS_ID,
		s.genus=g.GENUS
		WHERE s.genusNameID IS NULL
		AND s.leftIndex > g.leftIndex AND s.rightIndex < g.rightIndex;	
	";
	$msg_error = "failed to retrieve genera by left/right indexes!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	
	////////////////////////////////////////////////////////////////////
	// Now insert all into the combined temporary species-genus table
	////////////////////////////////////////////////////////////////////

	$sql = "
		INSERT INTO temp_sg_all
		SELECT * from temp_sg_source
		WHERE genusNameID IS NOT NULL AND genus IS NOT NULL;
	";
	$msg_error = "failed to insert results in table temp_sg_all!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

	// Drop temporary table for source
	$sql = "DROP TABLE IF EXISTS temp_sg_source;";
	$msg_error = "failed to drop previous table temp_sg_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));	
}

////////////////////////////////////////////////////////////////////
// Now insert all into the combined gf table
////////////////////////////////////////////////////////////////////

echo "  Saving genus x species combinations to table `splist_genlist_combined`...";

$sql = "
	INSERT INTO splist_genlist_combined (
		species_id,
		species,
		genus_species,
		genus_id,
		genus
	)
	SELECT DISTINCT
	speciesNameID,
	specificEpithet,
	CONCAT_WS(' ',genus,specificEpithet),
	genusNameID,
	genus
	FROM temp_sg_all;
";
$msg_error = "failed to save results to splist_genlist_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

// drop temp table
$sql = "DROP TABLE IF EXISTS temp_sg_all;";
$msg_error = "failed to drop table temp_sg_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
