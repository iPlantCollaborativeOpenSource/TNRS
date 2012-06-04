<?php
// Creates table containing original IDs of names 
// and details of any errors discovered during loading

echo "  Creating error table...";

$error_tbl="error_table";

// Drop previous error table, if any
$sql="DROP TABLE IF EXISTS `$error_tbl`;";
$msg_error="Failed to drop previous table `$error_tbl`!\r\n";
if (sql_execute($sql,FALSE,$echo_on,"",$msg_error));

// create empty table
$sql="
	CREATE TABLE `$error_tbl` (
		`$id_fld` INTEGER(11) UNSIGNED NOT NULL,
		`$srcTbl` VARCHAR(50) NOT NULL,
		`$has_errors` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
		`$is_orphan_parentNameID` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
		`$is_null_parentNameID` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
		`$is_orphan_acceptedNameID` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
		`$is_orphan_synonym_nameID` INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,		
		`$errmsg` VARCHAR(250) DEFAULT NULL,
		INDEX `".$error_tbl."_".$id_fld."`(`$id_fld`),
		INDEX `".$error_tbl."_".$has_errors."`(`$has_errors`),
		INDEX `".$error_tbl."_".$srcTbl."`(`$srcTbl`),
		INDEX `".$error_tbl."_".$is_orphan_parentNameID."`(`$is_orphan_parentNameID`),
		INDEX `".$error_tbl."_".$is_null_parentNameID."`(`$is_null_parentNameID`),
		INDEX `".$error_tbl."_".$is_orphan_acceptedNameID."`(`$is_orphan_acceptedNameID`),
		INDEX `".$error_tbl."_".$is_orphan_synonym_nameID."`(`$is_orphan_synonym_nameID`),
		INDEX `".$error_tbl."_".$errmsg."`(`$errmsg`)
		) 
		ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
	";
$msg_error="Failed to create table `$error_tbl`!\r\n";
if (sql_execute($sql,FALSE,$echo_on,"",$msg_error));


// Populate with all names
// Will delete any error-free names after loading completed
$sql="
	INSERT INTO `$error_tbl` (
		`$id_fld`,
		`$srcTbl`
	)
	SELECT 
	`$id_fld`,
	'names'
	FROM 
	`$tbl_staging`
	;
	";
$msg_error="Failed to populate table `$error_tbl`!\r\n";
if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_error));

?>
