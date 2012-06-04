<?php
// uses left and right indices to populate key taxa fields
// ignores hybrids

echo "  Extracting higher taxa:\r\n";

// Define ranks to extract
// Value to the left of delimiter is value of rank as it appears in the "Rank" column of table
// Value to right of delimiter is the name of the column containing taxa at that rank
$rank_arr=array(
	"subclass|$subclass_fld",
	"family|$family_fld"
);

// Do for current rank
foreach ($rank_arr as $rankstr) {
	$rankstrs=explode("|",$rankstr);
	$rank=$rankstrs[0];
	$ranked_fld=$rankstrs[1];

	echo "    $ranked_fld...";

	// Get all taxa at current rank
	$sql="
	SELECT DISTINCT $left_index_fld as l_ind, $right_index_fld as r_ind, $name_fld as higherTaxon
	FROM $tbl_staging
	WHERE $rank_fld=\"$rank\"
	;";

	$rows = mysql_query($sql);
	$num_rows = mysql_num_rows($rows);
	$curr_row=0;
	
	if ($num_rows>0) {
		while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
			$l_ind=$row{'l_ind'};
			$r_ind=$row{'r_ind'};
			$higherTaxon=$row{'higherTaxon'};
			$curr_row++;

			echo "  \r    $ranked_fld...$curr_row of $num_rows";

			// Update all descendent taxa
			$sql2=" UPDATE `$tbl_staging`
				SET `$ranked_fld`=\"$higherTaxon\"
				WHERE `$left_index_fld`>=$l_ind AND `$right_index_fld`<=$r_ind AND `$ranked_fld` IS NULL ;
			";
			
			$msg_error="\r\nFailed to update $ranked_fld column!\r\n";
			if (sql_execute($sql2,FALSE,$echo_on,'',$msg_error));
		}
	}
	echo "  \r    $ranked_fld...done       \r\n";
}



?>
