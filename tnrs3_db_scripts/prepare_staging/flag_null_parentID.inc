<?php
// creates integer column containing 1 if parentID missing, 0 if present
// This speeds things up a bit...

echo "  Flagging records with NULL parentNameID...";

// Add new column to record null parentID
$sql = "ALTER TABLE `$tbl_staging`
	ADD COLUMN `$null_parent_fld` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX  `$null_parent_fld`(`$null_parent_fld`)
	;";
$msg_error = "Failed to add column `$null_parent_fld`!";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

// Populate it...
$sql = "UPDATE `$tbl_staging` 
	SET `$null_parent_fld`=1
	WHERE `$parent_id_fld` IS NULL;";
$msg_error = "Failed to update column `$null_parent_fld`!";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
