<?php

// for testing only
echo "Clearing table `$tbl_comb_tq`...";
$msg_error="failed!\r\n";
$sql="DELETE FROM $tbl_comb_tq;";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Populate combined quadrinomial and trinomial table
echo "Populating `$tbl_comb_tq`...";
$msg_error="failed!\r\n";
$sql="
	INSERT INTO `$tbl_comb_tq`(
		infra2_id,
		infra2,
		rank2,
		infra2_norank,
		infra1_infra2,
		infra1_infra2_norank,
		infra1_id,
		infra1,
		rank1,
		infra1_norank
	)
	SELECT
		i2.infra2_id,
		i2.infra2,
		i2.rank,
		i2.infra2_norank,
		TRIM(CONCAT_WS(' ',i2.infra1_orig,i2.infra2)),
		TRIM(CONCAT_WS(' ',i1.infra1_norank,i2.infra2_norank)),
		i1.infra1_id,
		i2.infra1_orig,
		i1.rank,
		i1.infra1_norank
	FROM $tbl_infra2 i2 INNER JOIN $tbl_infra1 i1
	ON i2.infra1_id=i1.infra1_id;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
