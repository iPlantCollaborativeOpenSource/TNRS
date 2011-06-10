<?php

// Populate combined species & genus table
echo "Populating combined genus+species table `$tbl_comb_gs`...";
// NOTE: this inserts *species* plus associated genera only; 
// does NOT insert additional genera not associated with species
// also note: no authorities. Is this correct???
$sql="
	INSERT INTO `$tbl_comb_gs`(
		species_id,
		species,
		genus_species,
		genus_id,
		genus
	)
	SELECT
	SPECIES_ID,
	SPECIES,
	TRIM(CONCAT_WS(' ',GENUS_ORIG,SPECIES)),
	GENUS_ID,
	GENUS_ORIG
	FROM
	$tbl_sp
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
