<?php

// for testing only
echo "Clearing table `$tbl_comb_st`...";
$msg_error="failed!\r\n";
$sql="DELETE FROM $tbl_comb_st;";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Populate combined trinomial & species table
echo "Populating `$tbl_comb_st`...";
$msg_error="failed!\r\n";
// NOTE: this inserts *trinomials* plus associated species only; 
// does NOT insert additional trinomials not associated with species
$sql="
	INSERT INTO `$tbl_comb_st`(
		infra1_id,
		infra1,
		infra1_norank,
		rank,
		species_infra1,
		species_infra1_norank,
		species_id,
		species
	)
	SELECT
		infra1_id,
		infra1,
		infra1_norank,
		rank,
		TRIM(CONCAT_WS(' ',species_orig,infra1)),
		TRIM(CONCAT_WS(' ',species_orig,infra1_norank)),
		species_id,
		species_orig
	FROM $tbl_infra1
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
