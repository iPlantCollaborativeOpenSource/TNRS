<?php
// Insert families
// This will insert all families, regardless of whether or not they
// are accepted or contain child taxa (genera or lower).
echo "Inserting families...";
$msg_error = "Failed to insert families!";
$sql = "INSERT INTO `$tbl_fam`(
	FAMILY_ID,
	FAMILY,
	AUTHORITY
	)
	SELECT
	nameID,
	scientificName,
	scientificNameAuthorship
	FROM name
	WHERE nameRank='family'
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
