<?php

// Insert species
echo "Inserting species...";
$msg_error = "Failed to insert species!";
$sql = "INSERT INTO `splist`(
	SPECIES_ID,
	GENUS_ORIG,
	SPECIES,
	AUTHORITY
	)
	SELECT
	nameID,
	genus,
	specificEpithet,
	scientificNameAuthorship
	FROM name
	WHERE nameRank='species'
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
