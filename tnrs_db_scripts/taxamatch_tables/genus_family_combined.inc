<?php

// Populate combined family & genus table
echo "Populating combined family+genus table `$tbl_comb_fg`...";
// Insert genus plus associated canonical DEFAULT_FAMILY_ID
// Does NOT insert additional families not associated with genera.
// Done NOT insert alternative genus+family classifications
// Inserts text family only. Family IDs populated as separate steps, see below.
$sql="
	INSERT INTO `$tbl_comb_fg`(
		genus_id,
		genus,
		family_genus,
		family_id,
		family
	)
	SELECT
	GENUS_ID,
	GENUS,
	TRIM(CONCAT_WS(' ',DEFAULT_FAMILY, GENUS)),
	DEFAULT_FAMILY_ID,
	DEFAULT_FAMILY
	FROM
	$tbl_gen;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));



?>
