<?php
// Removes any error-free names from error table

echo "  Updating error table...";
$sql="
	DELETE FROM $error_tbl
	WHERE $has_errors=0;
	";
$msg_error="operation failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
