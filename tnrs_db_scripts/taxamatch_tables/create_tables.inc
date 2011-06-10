<?php
// Creates taxamatch tables

echo "Creating taxamatch tables...";

$script = "drop_tables.sql";
$command = "mysql -u $USER --password=$PWD $DB < $script";
system($command);

$script = "create_tables.sql";
$command = "mysql -u $USER --password=$PWD $DB < $script";
system($command);
echo "done\r\n";

?>
