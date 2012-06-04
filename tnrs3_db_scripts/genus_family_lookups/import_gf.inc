<?php
// Imports raw text file to mysql
// $local, $fields_terminated_by, $optionally_enclosed_by, $lines_terminated_by and $ignore_lines
// are set in parameters file

foreach($tbls as $basename) {
	$file=$basename.$plaintextext;
	$tbl=$tbl_prefix.$basename;

	// Import names
	echo "Importing file '$file'...";
	$msg_error = "Failed!\r\n";
	$sql = "LOAD DATA $local INFILE '$download_dir$file' 
		INTO TABLE $tbl 
		$fields_terminated_by 
		$optionally_enclosed_by 
		$lines_terminated_by 
		$ignore_lines;
		";
	if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
/*
	// Standardize NULLS
	echo "Converting empty strings to NULL...";
	if (!(empty_string_to_null($tbl))) die("Error\r\n.");
	echo "done\r\n";
 */
}

?>
