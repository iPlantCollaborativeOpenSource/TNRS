<?php
// flags as hybrid any taxon with space-delimited "x" in name
// Hopefully hybrids are already flagged in source data
// However, this will mop up the most obvious cases, if missed

echo "  Flagging hybrids...";
$sql="
	UPDATE $tbl_staging 
	SET $is_hybrid=1
	WHERE $name_fld LIKE '% x %' OR $name_fld LIKE 'X %';
";
$msg_error="Failed to flag hybrids!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
