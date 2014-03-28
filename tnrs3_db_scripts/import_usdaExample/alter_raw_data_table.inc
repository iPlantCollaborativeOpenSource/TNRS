<?php

/////////////////////////////////////////////////////////////////
// Purpose:
// Adds the following fields to the raw names table:
// nameID, parentNameID, acceptance, acceptedNameID
// Note that nameID is auto_increment
// The columns need to be added and populated prior to loading
// to staging table, due to odd structure of USDA Plants download
// format
/////////////////////////////////////////////////////////////////

echo "Altering raw data table...";
$sql="
	ALTER TABLE `$tbl_names_raw`
	ADD COLUMN nameID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT FIRST,
	ADD COLUMN parentNameID INTEGER UNSIGNED DEFAULT NULL AFTER nameID,
	ADD COLUMN acceptance VARCHAR(25) DEFAULT NULL AFTER parentNameID,
	ADD COLUMN acceptedNameID INTEGER UNSIGNED DEFAULT NULL AFTER acceptance,
	ADD COLUMN parentName VARCHAR(150) DEFAULT NULL,
	ADD COLUMN parentAuthor VARCHAR(150) DEFAULT NULL,	
	ADD INDEX `".$tbl_names_raw."_nameID`(nameID),
	ADD INDEX `".$tbl_names_raw."_parentNameID`(parentNameID),
	ADD INDEX `".$tbl_names_raw."_acceptance`(acceptance),
	ADD INDEX `".$tbl_names_raw."_acceptedNameID`(acceptedNameID),
	ADD INDEX `".$tbl_names_raw."_parentName`(parentName),
	ADD INDEX `".$tbl_names_raw."_parentAuthor`(parentAuthor);
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

?>
