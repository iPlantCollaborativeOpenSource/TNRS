<?php
// Final cleanups of core database
// Run this after all sources have been loaded

echo "Performing final cleanup of core tables:\r\n";

// Remove temporary FK from table `name`
echo "  Removing temporary FK from table `name`...";
$sql="
	ALTER TABLE name 
	DROP COLUMN FK_nameStaging_nameID,
	DROP COLUMN defaultFamily;
";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>