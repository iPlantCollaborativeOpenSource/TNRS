<?
// Deletes orphan parentNameIDs and acceptedNameIDs
// from staging table

echo "Fixing errors in `$tbl`:\r\n";

echo "  Removing orphan parentNameIDs...";
$msg_error = "Failed!\r\n";
$sql="
	UPDATE `$tbl` n INNER JOIN `$error_tbl` e
	ON n.`$id_fld`=e.`$id_fld`
	SET 
	n.$parent_id_fld=NULL 
	WHERE 
	(e.$srcTbl='names' AND e.$is_orphan_parentNameID=1)
       	OR n.$parent_id_fld=0
	;
";
// the above where clause is overkill, but need to be sure to catch zeros
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "  Removing orphan acceptedNameIDs...";
$msg_error = "Failed!\r\n";
$sql="
	UPDATE `$tbl` n INNER JOIN `$error_tbl` e
	ON n.`$id_fld`=e.`$id_fld`
	SET 
	n.$accepted_id_fld=NULL 
	WHERE 
	(e.$srcTbl='names' AND e.$is_orphan_acceptedNameID=1)
       	OR n.$accepted_id_fld=0
	;
";
// the above where clause is overkill, but need to be sure to catch zeros
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
