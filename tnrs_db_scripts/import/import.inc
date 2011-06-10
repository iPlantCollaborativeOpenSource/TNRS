<?php
// Imports raw text file to mysql
// $local, $fields_terminated_by, $optionally_enclosed_by, $lines_terminated_by and $ignore_lines
// are set in parameters file

// Import names
echo "Importing file '$namesfile'...";
$msg_error = "Failed!\r\n";
$sql = "LOAD DATA $local INFILE '$namesfile' 
	INTO TABLE $tbl_names_raw 
	$fields_terminated_by 
	$optionally_enclosed_by 
	$lines_terminated_by 
	$ignore_lines;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Standardize NULLS
echo "Converting empty strings to NULL...";
if (!(empty_string_to_null($tbl_names_raw))) die("Error\r\n.");
echo "done\r\n";

?>
