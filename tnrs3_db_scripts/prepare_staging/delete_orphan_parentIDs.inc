<?php
// Sets to NULL any orphan parentNameIDs linking to non-existent PKs.
// These nasty buggers will cause all sort of headaches
// if you don't get rid of them.
// 'find_parent.inc' attempts to fix the problem by parsing and linking
//
// These problem records should already have been flagged in error table
// during import phase

echo "  Deleting orphan parent name IDs...";

// set problem parentIDs NULL
$sql="
	UPDATE $tbl_staging as t INNER JOIN (
		SELECT a.$id_fld as ID
		FROM $tbl_staging AS a LEFT JOIN $tbl_staging AS b
		ON a.$parent_id_fld=b.$id_fld
		WHERE b.$id_fld IS NULL
	) as t2
	ON t.$id_fld=t2.ID
	SET t.$parent_id_fld=NULL
	";
$msg_error="Failed to delete orphan parentIDs!\r\n";
if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_error));

?>
