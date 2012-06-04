<?php
// Sets NULL any instance of parentNameID=0 or acceptedNameID=0
// in staging table

echo "  Setting zero IDs to NULL `$tbl_staging`...";

$msg_error = "Failed ($parent_id_fld)!\r\n";
$sql="
	UPDATE `$tbl_staging`
	SET 
	$parent_id_fld=NULL 
	WHERE 
	$parent_id_fld=0
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

$msg_error = "Failed ($accepted_id_fld)!\r\n";
$sql="
	UPDATE `$tbl_staging`
	SET 
	$accepted_id_fld=NULL 
	WHERE 
       	$accepted_id_fld=0
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
