<?php

////////////////////////////////////////////////////////////////////
// Populates table infra1list_splist_combined with all species x 
// subspecies (trinomial) combinations in database.
// Does this by retrieving all combinations within each source, 
// then combining all distinct combinations among all sources.
////////////////////////////////////////////////////////////////////

echo "Populating infra1list_splist_combined:\r\n";

////////////////////////////////////////////////////////////////////
// Create temporary tables to hold all trinomial nameID x species
// nameID combinations from all sources
////////////////////////////////////////////////////////////////////

$sql = "DROP TABLE IF EXISTS temp_ts_all;";
$msg_error = "failed to drop previous table temp_ts_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
$sql = "
	CREATE TABLE temp_ts_all (
		infra1NameID INTEGER UNSIGNED NOT NULL,
		speciesNameID INTEGER UNSIGNED DEFAULT NULL,
		rightIndex INTEGER UNSIGNED DEFAULT NULL,
		leftIndex INTEGER UNSIGNED DEFAULT NULL,
		INDEX (rightIndex),
		INDEX (leftIndex),
		INDEX (infra1NameID),
		INDEX (speciesNameID)
	);
";
$msg_error = "failed to create table temp_ts_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

////////////////////////////////////////////////////////////////////
// Set up loop to toggle though all sources
////////////////////////////////////////////////////////////////////

foreach ($src_array as $src) {
	echo "  Inserting trinomials for source='$src'...";
		
	////////////////////////////////////////////////////////////////////
	// For each source, retrieve species associated with each trinomial
	////////////////////////////////////////////////////////////////////

	// get sourceID
	$sql="
		SELECT sourceID
		FROM source
		WHERE sourceName='$src';	
	";
	$sourceID=sql_get_first_result($sql,'sourceID');
	
	// Create temporary table to hold trinomials for this source only
	$sql = "DROP TABLE IF EXISTS temp_ts_source;";
	$msg_error = "failed to drop previous table temp_ts_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	$sql = "CREATE TABLE temp_ts_source LIKE temp_ts_all;";
	$msg_error = "failed to create table temp_ts_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	// Insert trinomials classified by this source, along with species
	// For trinomials where species is immediate parent
	$sql = "
		INSERT INTO temp_ts_source (
		speciesNameID,
		infra1NameID, 
		rightIndex, 
		leftIndex
		)
		SELECT DISTINCT 
		s.nameID,	
		infra1_id, 
		rightIndex, 
		leftIndex
		FROM infra1list i JOIN classification c
		ON i.infra1_id=c.nameID
		JOIN 
		(
			SELECT n.nameID
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE nameRank='species' AND ns.sourceID=$sourceID		
		) AS s
		ON c.parentNameID=s.nameID
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert trinomials with species as immediate parent!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

	// Insert trinomials classified by this source
	// Also insert species where species is immediate parent
	// Other species will be mopped up by next step using
	// tree traversal
	$sql = "
		INSERT INTO temp_ts_source (
		infra1NameID, 
		rightIndex, 
		leftIndex
		)
		SELECT DISTINCT 
		infra1_id, 
		rightIndex, 
		leftIndex
		FROM infra1list i JOIN classification c
		ON i.infra1_id=c.nameID
		JOIN 
		(
			SELECT n.nameID
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE nameRank<>'species' AND ns.sourceID=$sourceID		
		) AS s
		ON c.parentNameID=s.nameID
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert trinomials without species as immediate parent!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
	echo "  Completing species...";	
	// For the remainder, the slow way, using left and right indexes
	$sql = "
		UPDATE temp_ts_source t,
		(
			SELECT DISTINCT nameID, leftIndex, rightIndex
			FROM classification c JOIN splist s
			ON c.nameID=s.SPECIES_ID
			WHERE sourceID=$sourceID
		) AS s
		SET 
		t.speciesNameID=s.nameID
		WHERE t.speciesNameID IS NULL
		AND t.leftIndex > s.leftIndex AND t.rightIndex < s.rightIndex	
	";
	$msg_error = "failed to retrieve species by left/right indexes!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	
	// Now insert all into the combined trinomial-species table
	$sql = "
		INSERT INTO temp_ts_all
		SELECT * from temp_ts_source
		WHERE speciesNameID IS NOT NULL;
	";
	$msg_error = "failed to insert results in table temp_ts_all!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	// Drop temporary table for source
	$sql = "DROP TABLE IF EXISTS temp_ts_source;";
	$msg_error = "failed to drop previous table temp_ts_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));	
}

////////////////////////////////////////////////////////////////////
// Now insert all names into the combined trinomial-species table
////////////////////////////////////////////////////////////////////

echo "  Saving trinomial x species combinations to table infra1list_splist_combined...";	
$sql = "
	INSERT INTO infra1list_splist_combined (
		infra1_id,
		species_id 
	)
	SELECT DISTINCT
		infra1NameID,
		speciesNameID 
	FROM temp_ts_all;
";
$msg_error = "failed to save results to infra1list_splist_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

// Update remaining fields by joining to infra1list:
$sql = "
	UPDATE infra1list_splist_combined ts JOIN infra1list t
	ON ts.infra1_id=t.infra1_id
	SET
	ts.infra1=t.infra1,
	ts.infra1_norank=t.infra1_norank,
	ts.rank=t.rank,
	ts.species_infra1=TRIM(CONCAT_WS(' ',t.species_orig,t.infra1)),
	ts.species_infra1_norank=TRIM(CONCAT_WS(' ',t.species_orig,t.infra1_norank)),
	ts.species=t.species_orig;
";
$msg_error = "failed to save results to infra1list_splist_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

// drop temp table
$sql = "DROP TABLE IF EXISTS temp_ts_all;";
$msg_error = "failed to drop table temp_ts_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
