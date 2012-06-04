<?php
// generates right and left indices
// for hierarchical searches

if ($echo_on) echo "  Performing tree traversal:\r\n";

// Add root
// PK MUST be integer; modify if not
if ($add_root) {
	echo "    Adding root...";
	// get next value of PK
	$sql = "SELECT MAX($id_fld) AS max_id FROM $tbl_staging;";
	$max_id = sql_get_first_result($sql,'max_id');
	//echo "  \r  \nmax_id=$max_id\r\n";
      	$root_id = $max_id + 1; 

	// Add root record
	$sql="INSERT INTO $tbl_staging(`$id_fld`,`$name_fld`,`$rank_fld`)
		SELECT $root_id, '$root_name', '$root_rank';";
	$msg_error = "Failed to add root record!";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
}

// get root id and do some error checking
$sql = "SELECT `$id_fld` FROM `$tbl_staging` WHERE `$rank_fld`='$root_rank';";
if (!($result=mysql_query($sql))) die("Failed to get id of root.\r\n");
$num_rows = mysql_num_rows($result);
if ($num_rows<1) {
	die("Error: no root");
} elseif ($num_rows>1) {
	die("Error: more than one root");
} else {
	$row = mysql_fetch_row($result);
	$root_id=$row[0];
}

if ($preroot) {
	// Add node above root for linking orphan taxa (no parentTaxonID)
	// Indexing algorithm may fail if have multiple taxa linked to root.
	echo "    Adding pre-root...";

	// get next value of PK
	$sql = "SELECT MAX($id_fld) AS max_id FROM $tbl_staging;";
	$max_id = sql_get_first_result($sql,'max_id');
	//echo "  \r  \nmax_id=$max_id\r\n";
      	$preroot_id = $max_id+1; 

	// Add root record
	$sql="INSERT INTO $tbl_staging(`$id_fld`,`$parent_id_fld`,`$name_fld`,`$rank_fld`)
		SELECT $preroot_id, $root_id, 'preroot', 'preroot';";
	$msg_error = "Failed to add preroot record!";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

	// Fix all NULL parentIDs by setting to prerootID 
	// (except root itself --> must have NULL parentID)
	$sql = "UPDATE $tbl_staging 
		SET `$parent_id_fld`=$preroot_id
		WHERE `$parent_id_fld` IS NULL AND $id_fld<>$root_id;";
	$msg_error = "Failed to set NULL `$parent_id_fld` to PK of preroot!";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));
}

// Populate left and right indexes using recursive tree traversal
// Begins at root, traverses tree and returns to root
$ind = 0; 	// starting value of index
if (!($r_ind=get_index($tbl_staging, $root_id, $ind, $id_fld, $parent_id_fld, $left_index_fld, $right_index_fld))) {
	echo "  Indexing error!\r\n";
}

if ($preroot) {
	// Remove root and set preroot to root, and left index to 1
	// this means there will be no left index=2...c'est la vie

	echo "    Removing pre-root...";
	// Delete root
	$sql="DELETE FROM $tbl_staging
		WHERE `$id_fld`=$root_id;";
	$msg_error = "Failed to delete temporary root!";
	if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

	// Reset preroot to root
	$sql="UPDATE $tbl_staging
		SET `$rank_fld`='$root_rank', `$name_fld`='$root_name', `$parent_id_fld`=NULL, `$left_index_fld`=1
		WHERE `$id_fld`=$preroot_id;";
	$msg_error = "Failed to reset preroot to root!";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
}

?>
