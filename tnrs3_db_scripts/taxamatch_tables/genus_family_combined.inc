<?php

////////////////////////////////////////////////////////////////////
// Populates table genlist_famlist_combined with all genus x family
// combinations in database.
// Does this by retrieving all genus x family combinations within 
// each source, then combining all distinct combinations among all 
// sources.
////////////////////////////////////////////////////////////////////

echo "Populating genlist_famlist_combined:\r\n";

// Create temporary tables to hold all genus nameID x family nameID 
// combinations from all sources
$sql = "DROP TABLE IF EXISTS temp_gf_all;";
$msg_error = "failed to drop previous table temp_gf_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
$sql = "
	CREATE TABLE temp_gf_all (
	genusNameID INTEGER UNSIGNED NOT NULL,
	genus VARCHAR(100) NOT NULL,
	familyNameID INTEGER UNSIGNED DEFAULT NULL,
	family VARCHAR(100) DEFAULT NULL,
	rightIndex INTEGER UNSIGNED DEFAULT NULL,
	leftIndex INTEGER UNSIGNED DEFAULT NULL,
	INDEX (rightIndex),
	INDEX (leftIndex),
	INDEX (genusNameID),
	INDEX (genus),
	INDEX (familyNameID),
	INDEX (family)
	);
";
$msg_error = "failed to create table temp_gf_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));


// Set up loop to toggle though all sources
// For each source, retrieve family associated with each genus
foreach ($src_array as $src) {
	echo "  Inserting genera for source='$src'...";

	// get sourceID
	$sql="
		SELECT sourceID
		FROM source
		WHERE sourceName='$src';	
	";
	$sourceID=sql_get_first_result($sql,'sourceID');
	
	// Create temporary table to hold genera for this source only
	$sql = "DROP TABLE IF EXISTS temp_gf_source;";
	$msg_error = "failed to drop previous table temp_gf_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	$sql = "
		CREATE TABLE temp_gf_source LIKE temp_gf_all;
	";
	$msg_error = "failed to create table temp_gf_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

	// Insert genera classified by this source
	// Also insert family ID IF family is the immediate parent
	// genera without a family as the immediate parent 
	// will have to be mopped up in the next step using tree traversal
		$sql = "
		INSERT INTO temp_gf_source (
		familyNameID,
		family,		
		genusNameID, 
		genus, 
		rightIndex, 
		leftIndex
		)
		SELECT DISTINCT 
		f.nameID,
		f.family,		
		GENUS_ID, 
		GENUS, 
		rightIndex, 
		leftIndex
		FROM genlist g JOIN classification c
		ON g.GENUS_ID=c.nameID
		LEFT JOIN (
			SELECT n.nameID, scientificName AS family
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE n.nameRank='family' AND ns.sourceID=$sourceID		
		) AS f
		ON c.parentNameID=f.nameID
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert genera!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
	echo "  Completing families...";
	// For the remainder, the slow way, using left and right indexes
	$sql = "
		UPDATE temp_gf_source g,
		(
			SELECT DISTINCT f.FAMILY_ID, f.FAMILY, leftIndex, rightIndex
			FROM classification c JOIN famlist f
			ON c.nameID=f.FAMILY_ID
			WHERE sourceID=$sourceID
		) AS f
		SET 
		g.familyNameID=f.f.FAMILY_ID,
		g.family=f.FAMILY
		WHERE g.familyNameID IS NULL
		AND g.leftIndex > f.leftIndex AND g.rightIndex < f.rightIndex	
	";
	$msg_error = "failed to retrieve families by left/right indexes!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	
	// Now insert all into the combined gf table
	$sql = "
		INSERT INTO temp_gf_all
		SELECT DISTINCT * from temp_gf_source
		WHERE familyNameID IS NOT NULL AND family IS NOT NULL;
	";
	$msg_error = "failed to insert results in table temp_gf_all!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	// Drop temporary table for source
	$sql = "DROP TABLE IF EXISTS temp_gf_source;";
	$msg_error = "failed to drop previous table temp_gf_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));	
	
}

echo "  Saving all genus x family combinations to table `genlist_famlist_combined1`...";
// Now insert all into the combined gf table
$sql = "
	INSERT INTO genlist_famlist_combined (
		genus_id,
		genus,
		family_genus,
		family_id,
		family	
	)
	SELECT DISTINCT
	genusNameID,
	genus,
	CONCAT_WS(' ',family,genus),
	familyNameID,
	family
	FROM temp_gf_all;
";
$msg_error = "failed to save results to genlist_famlist_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
// drop temp table
$sql = "DROP TABLE IF EXISTS temp_gf_all;";
$msg_error = "failed to drop table temp_gf_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
