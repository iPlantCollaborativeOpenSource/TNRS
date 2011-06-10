<?php
// Final clean up of any temporary tables

echo "Removing temporary tables...";
$sql="DROP TABLE IF EXISTS `$tbl`;";
$msg_error="Failed to drop table `$tbl`!";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
