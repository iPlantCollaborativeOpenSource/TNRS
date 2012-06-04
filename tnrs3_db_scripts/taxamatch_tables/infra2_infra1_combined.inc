<?php

////////////////////////////////////////////////////////////////////
// Populates table infra2list_infra1list_combined with all species x 
// subspecies (trinomial) combinations in database.
// Does this by retrieving all combinations within each source, 
// then combining all distinct combinations among all sources.
////////////////////////////////////////////////////////////////////

echo "Populating infra2list_infra1list_combined:\r\n";


////////////////////////////////////////////////////////////////////
// Create temporary tables to hold all quadrinomial x 
// trinomial nameID combinations from all sources
////////////////////////////////////////////////////////////////////

$sql = "DROP TABLE IF EXISTS temp_qt_all;";
$msg_error = "failed to drop previous table temp_qt_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
$sql = "
	CREATE TABLE temp_qt_all (
		infra2NameID INTEGER UNSIGNED NOT NULL,
		infra1NameID INTEGER UNSIGNED DEFAULT NULL,
		rightIndex INTEGER UNSIGNED DEFAULT NULL,
		leftIndex INTEGER UNSIGNED DEFAULT NULL,
		INDEX (rightIndex),
		INDEX (leftIndex),
		INDEX (infra2NameID),
		INDEX (infra1NameID)
	);
";
$msg_error = "failed to create table temp_qt_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

////////////////////////////////////////////////////////////////////
// Set up loop to toggle though all sources
////////////////////////////////////////////////////////////////////

foreach ($src_array as $src) {
	echo "  Inserting quadrinomials for source='$src'...";

	////////////////////////////////////////////////////////////////////
	// Retrieve trinomials associated with each quadrinomial
	////////////////////////////////////////////////////////////////////
	
	// get sourceID
	$sql="
		SELECT sourceID
		FROM source
		WHERE sourceName='$src';	
	";
	$sourceID=sql_get_first_result($sql,'sourceID');
	
	// Create temporary table to hold quadrinomials for this source only
	$sql = "DROP TABLE IF EXISTS temp_qt_source;";
	$msg_error = "failed to drop previous table temp_qt_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	$sql = "
		CREATE TABLE temp_qt_source LIKE temp_qt_all;
	";
	$msg_error = "failed to create table temp_qt_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	// Insert quadrinomials classified by this source
	// Also insert trinomial ID where trinomial is immediate parent
	// Other trinomials will be mopped up by next step using
	// tree traversal
		$sql = "
		INSERT INTO temp_qt_source (
		infra1NameID,
		infra2NameID, 
		rightIndex, 
		leftIndex
		)
		SELECT DISTINCT 
		t.nameID,
		infra2_id, 
		rightIndex, 
		leftIndex
		FROM infra2list q JOIN classification c
		ON q.infra2_id=c.nameID
		LEFT JOIN 
		(
			SELECT n.nameID
			FROM name n JOIN name_source ns
			ON n.nameID=ns.nameID
			WHERE n.nameParts=3 AND ns.sourceID=$sourceID		
		) as t
		ON c.parentNameID=t.nameID
		WHERE c.sourceID=$sourceID;
	";
	$msg_error = "failed to insert quadrinomials!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

	echo "  Completing trinomials...";	
	// For the remainder, the slow way, using left and right indexes
	$sql = "
		UPDATE temp_qt_source q,
		(
			SELECT DISTINCT infra1_id, leftIndex, rightIndex
			FROM classification c JOIN infra1list t
			ON c.nameID=t.infra1_id
			WHERE sourceID=$sourceID
		) AS t
		SET 
		q.infra1NameID=t.infra1_id
		WHERE q.infra1NameID IS NULL
		AND q.leftIndex > t.leftIndex AND q.rightIndex < t.rightIndex	
	";
	$msg_error = "failed to retrieve trinomial by left/right indexes!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
	
	// Now insert all into the combined quadrinomial-trinomial table
	$sql = "
		INSERT INTO temp_qt_all
		SELECT * from temp_qt_source
		WHERE infra1NameID IS NOT NULL;
	";
	$msg_error = "failed to insert results in table temp_qt_all!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	
	
	// Drop temporary table for source
	$sql = "DROP TABLE IF EXISTS temp_qt_source;";
	$msg_error = "failed to drop previous table temp_qt_source!\r\n";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));		
}

////////////////////////////////////////////////////////////////////
// Now insert all names into the combined quadrinomial-quadrinomial table
////////////////////////////////////////////////////////////////////

echo "  Saving quadrinomial x trinomial combinations to table infra2list_infra1list_combined...";
$sql = "
	INSERT INTO infra2list_infra1list_combined (
		infra2_id,
		infra1_id 
	)
	SELECT DISTINCT
		infra2NameID,
		infra1NameID 
	FROM temp_qt_all;
";
$msg_error = "failed to save results to infra2list_infra1list_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

// Update remaining fields by joining to infra2list:
$sql = "
	UPDATE infra2list_infra1list_combined qt JOIN infra2list q
	ON qt.infra2_id=q.infra2_id 
	SET
	qt.infra2=q.infra2,
	qt.infra2_norank=q.infra2_norank,
	qt.rank2=q.rank,
	qt.infra1_infra2=TRIM(CONCAT_WS(' ',q.infra1_orig,q.infra2)),
	qt.infra1_infra2_norank=TRIM(CONCAT_WS(' ',strSplit(q.infra1_orig,' ',1),strSplit(q.infra1_orig,' ',2),strSplit(q.infra1_orig,' ',4),q.infra2_norank)),
	qt.infra1=q.infra1_orig;
";
$msg_error = "failed to save results to infra2list_infra1list_combined!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));	

// drop temp table
$sql = "DROP TABLE IF EXISTS temp_qt_all;";
$msg_error = "failed to drop table temp_qt_all!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
