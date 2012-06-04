<?php

echo "Transferring lookup tables from `$DB_NEW` to `$DB_FINAL`:\r\n";

echo "  `$faf`...";

$msg_error="failed to drop new table `$DB_FINAL`.`$faf`!\r\n";
$sql="
DROP TABLE IF EXISTS `$DB_FINAL`.`$faf`;
";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$msg_error="failed to create new table `$DB_FINAL`.`$faf` LIKE `$DB_NEW`.`$faf`!\r\n";
$sql="
CREATE TABLE `$DB_FINAL`.`$faf` LIKE `$DB_NEW`.`$faf`;
";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$msg_error="failed to insert into table `$DB_FINAL`.`$faf`!\r\n";
$sql="
INSERT INTO `$DB_FINAL`.`$faf` SELECT * FROM `$DB_NEW`.`$faf`;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

echo "  `$fg`...";

$msg_error="failed to drop new table `$DB_FINAL`.`$fg`!\r\n";
$sql="
DROP TABLE IF EXISTS `$DB_FINAL`.`$fg`;
";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$msg_error="failed to create new table `$DB_FINAL`.`$fg` LIKE `$DB_NEW`.`$fg`!\r\n";
$sql="
CREATE TABLE `$DB_FINAL`.`$fg` LIKE `$DB_NEW`.`$fg`;
";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$msg_error="failed to insert into table `$DB_FINAL`.`$fg`!\r\n";
$sql="
INSERT INTO `$DB_FINAL`.`$fg` SELECT * FROM `$DB_NEW`.`$fg`;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>