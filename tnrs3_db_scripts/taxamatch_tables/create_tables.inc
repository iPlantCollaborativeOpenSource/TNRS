<?php
// Creates taxamatch tables

echo "Creating taxamatch tables...";

$script = "taxamatch_tables/drop_tables.sql";
$command = "mysql -u $USER --password=$PWD $DB < $script";
system($command);

$script = "taxamatch_tables/create_tables.sql";
$command = "mysql -u $USER --password=$PWD $DB < $script";
system($command);
echo "done\r\n";

?>
