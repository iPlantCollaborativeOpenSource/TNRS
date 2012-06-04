<?php

// Insert genera
// This will insert all genera, regardless of whether or not they
// contain child taxa (species or lower).
// I think this is OK
echo "Inserting genera...";
$msg_error = "Failed to insert genera!";
$sql = "INSERT INTO `$tbl_gen`(
	GENUS_ID,
	GENUS,
	AUTHORITY
	)
	SELECT
	nameID,
	scientificName,
	scientificNameAuthorship
	FROM name
	WHERE nameRank='genus'
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
