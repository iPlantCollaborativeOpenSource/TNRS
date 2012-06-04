<?php
// Clears FK columns in staging table
// only necessary if using existing staging table

echo "  Clearing index columns...";
$msg_error = "Failed to clear index columns in $tbl_staging";
$sql="UPDATE $tbl_staging SET 
	$fk_fld=NULL, 
	$parent_fk_fld=NULL;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
 

?>
