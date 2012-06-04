<?php
// Removes from core db all records related only to this source

Echo "  Removing previous names linked only to this source:\r\n";

// Create temporary table of names to delete
echo "    Creating temporary table of records to delete...";
$tmp_tbl="delete_names";

$msg_error="failed to drop previous table!\r\n";
$sql="DROP TABLE IF EXISTS `$tmp_tbl`;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// populate with all names for this source
$msg_error="failed to create table!\r\n";
$sql="
	CREATE TABLE $tmp_tbl
	SELECT DISTINCT
	n.nameID, 
	CAST(1 AS UNSIGNED INTEGER) AS `del`
	FROM name n INNER JOIN name_source ns
	ON n.nameID=ns.nameID 
	WHERE ns.sourceID=$sourceID;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Index temporary table
$msg_error="failed to index table!\r\n";
$sql="
	ALTER TABLE $tmp_tbl
	ADD INDEX `dn_nameID`(`nameID`),
	ADD INDEX `dn_del`(`del`)
	";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Mark records linked to other sources
echo "    Marking records linked to other sources...";

echo "\r                                                             ";
echo "\r    Marking records linked to other sources...`name_source`";
$msg_error="failed to link via table name_source!\r\n";
$sql="
	UPDATE $tmp_tbl d INNER JOIN name_source ns
	ON d.nameID=ns.nameID
	SET d.del=0
	WHERE ns.sourceID<>$sourceID;
	";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                             ";
echo "\r    Marking records linked to other sources...`synonym`";
$msg_error="failed to link via table synonym!\r\n";
$sql="
	UPDATE $tmp_tbl d INNER JOIN synonym s
	on d.nameID=s.nameID
	SET d.del=0
	WHERE s.sourceID<>$sourceID;
	";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                             ";
echo "\r    Marking records linked to other sources...`classification`";
$msg_error="failed to link via table classification!\r\n";
$sql="
	UPDATE $tmp_tbl d INNER JOIN classification c
	on d.nameID=c.nameID
	SET d.del=0
	WHERE c.sourceID<>$sourceID;
	";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));
echo "\r                                                             ";
echo "\r    Marking records linked to other sources...done`\r\n";

// Delete all records from this source in name_source, 
// classification and synonym:
echo "    Deleting names and related records...`name_source`";
$msg_error="failed to delete from table name_source!\r\n";
$sql="
	DELETE FROM name_source
	WHERE sourceID=$sourceID;
	";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                             ";
echo "\r    Deleting names and related records...`classification`";
$msg_error="failed to del from table classification!\r\n";
$sql="
	DELETE FROM classification 
	WHERE sourceID=$sourceID;
	";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                             ";
echo "\r    Deleting names and related records...`synonym`";
$msg_error="failed to del from table synonym!\r\n";
$sql="
	DELETE FROM synonym 
	WHERE sourceID=$sourceID;
	";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// From table name, delete only records for names
// linked uniquely to this source and no other
echo "\r                                                             ";
echo "\r    Deleting names and related records...`name`";
$msg_error="failed to del from table name!\r\n";
$sql="
	DELETE n.*
	FROM name n INNER JOIN $tmp_tbl d
	on n.nameID=d.nameID
	WHERE d.del=1;
	";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));
echo "\r                                                             ";
echo "\r    Deleting names and related records...done\r\n";

echo "    Dropping temporary table...";
$msg_error="failed to drop temporary table!\r\n";
$sql="DROP TABLE IF EXISTS `$tmp_tbl`;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
