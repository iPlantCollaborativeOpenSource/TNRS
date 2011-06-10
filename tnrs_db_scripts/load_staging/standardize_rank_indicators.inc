<?php
// Standardizes rank indicators for infraspecific taxa
// Infraspecific taxa should have been parsed and rank
// indicator field populated prior to this step

echo "Standardizing infraspecific rank indicators:";

$sql="
	SELECT DISTINCT `$rank_ind_fld` as rank
	FROM `$tbl`
	WHERE `$rank_ind_fld` IS NOT NULL;
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
$n=1;

if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$rank=$row{'rank'};
		$rankStd=stdRankIndicator($rank);
		echo "\r                                                               ";
		echo "\rStandardizing infraspecific rank indicators...$n of $num_rows";

		// Update rank
		$sql_update_rank="
			UPDATE `$tbl` 
			SET 
			`$rank_ind_fld`='$rankStd'
			WHERE `$rank_ind_fld`='$rank';
		";
		$msg_error="\r\nFailed to update $rank_ind_fld for rank='$rank'!\r\n";
		if (sql_execute($sql_update_rank,FALSE,$echo_on,'',$msg_error));
		$n++;

	}
}

echo "\r                                                               ";
echo "\rStandardizing infraspecific rank indicators...done\r\n";


?>
