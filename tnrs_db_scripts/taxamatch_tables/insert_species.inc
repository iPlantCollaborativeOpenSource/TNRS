<?php

// Insert species
echo "Inserting species...";
$msg_error = "Failed to insert species!";
$sql = "INSERT INTO `$tbl_sp`(
	SPECIES_ID,
	GENUS_ORIG,
	SPECIES,
	AUTHORITY
	)
	SELECT
	nameID,
	genus,
	scientificName,
	scientificNameAuthorship
	FROM name
	WHERE nameRank='species'
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
