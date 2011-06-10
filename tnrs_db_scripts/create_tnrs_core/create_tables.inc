<?php

echo "Dropping previous tables...";
$sql = "drop_tables.sql";
$command = "mysql -h $HOST -u $USER --password=$PWD $DB < $sql";
system($command);
echo $msg_success;

echo "Creating tables...";
$sql = "create_tables.sql";
$command = "mysql -h $HOST -u $USER --password=$PWD $DB < $sql";
system($command);
echo $msg_success;

?>
