<?php
// Final cleanups of core database
// Run this after all sources have been loaded

echo "Performing final cleanup of core tables:\r\n";

// Remove temporary FK from table `name`
echo "  Removing temporary FKs from table `name`...";

if (sql_exists_column("name","FK_nameStaging_nameID"===true)) {
	$sql="
		ALTER TABLE name 
		DROP COLUMN FK_nameStaging_nameID;
	";
	$msg_error="Failed!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));
	echo "\r\nRemoved FK_nameStaging_nameID!\r\n";
}

if (sql_exists_column("name","defaultFamily"===true)) {
	$sql="
		ALTER TABLE name 
		DROP COLUMN defaultFamily;
	";
	$msg_error="Failed!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));
	echo "\r\nRemoved defaultFamily!\r\n";
}

echo $msg_success;

?>