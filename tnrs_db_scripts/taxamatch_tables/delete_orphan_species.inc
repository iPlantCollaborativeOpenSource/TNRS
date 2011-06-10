<?php

// NOTE: this compiles a list (table) of species not linked to any
// genus, then deletes them to avoid anomalies. Currently there are many.
// What seems to be happening is that the following types of
// species are being deleted:

// 1) species not linked to any genus
// 2) species linked to >1 genus entry

// Type 1 names might be resolved by parsing genus from species name and 
// adding to genus table and name table in core db
// Type 2 names could be resovled by:
// a) Linking to *accepted* genus if exactly 1 accepted genus, or
// b) Linking randomly to one accepted genus if >1 accepted genus, or
// c) Linking randomly to one genus with no taxonomic opinion if no accepted genus, or
// d) Linking ramdomly to one synonymous genus if no accepted genus and no genus without taxonomic opinion

// Implementing the above changes should eliminate orphan genera

// Delete any species still missing a FK to genus
// Not sure if this is necessary; seems prudent to avoid anomalies
echo "Checking for orphan species...";
// First find out how many
$sql="
	SELECT COUNT(*) AS orphans
	FROM `$tbl_sp`
	WHERE GENUS_ID IS NULL;
";
$orphans=sql_get_first_result($sql,'orphans');

if ($orphans==0) {
	echo "none found...done\r\n";
} else {
	echo "found $orphans\r\n";


	// Generate table of orphan species
	echo "Generating table of orphan species...";
	
	$sql="DROP TABLE IF EXISTS `taxamatch_orphan_species`;";
	$msg_error="Failed to drop previous orphan species table!";
	if (sql_execute($sql,FALSE,$echo_on,"",$msg_error));

	$sql="CREATE TABLE `taxamatch_orphan_species`
		SELECT * 
		FROM `$tbl_sp`
		WHERE GENUS_ID IS NULL;
		";
	$msg_error="Failed to generate orphan species table!";
	if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_error));

	// delete from orphans from species table
	echo "Deleting orphan species...";
	$sql="DELETE FROM `$tbl_sp` WHERE GENUS_ID IS NULL;";
	$msg_error="Failed to delete orphan species!";
	if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_error));
}

?>
