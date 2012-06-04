<?php
// Loads table $names_parsed (details set in params.inc)
// This table is result of parsing all names in the raw 
// data using the TNRS-GNI name parser.
// This step is required for loading ncbi taxonomy.
// Its purpose is to separate author from names, which 
// are concatenated in ncbi. Also, ncbi maintains
// nameWithAuthor and nameWithoutAuthor as separate entries.
// These must be merged into a single record in the TNRS database

echo "Importing file $names_parsed_file to table $names_parsed...";
$msg_error = "Failed!\r\n";
$sql = "LOAD DATA $local INFILE '$names_parsed_path$names_parsed_file' 
	INTO TABLE $names_parsed 
	$np_fields_terminated_by 
	$np_optionally_enclosed_by 
	$np_lines_terminated_by 
	$np_ignore_lines;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Standardize NULLS
echo "Converting empty strings to NULL...";
if (!(empty_string_to_null($names_parsed))) die("Error\r\n.");
echo "done\r\n"	

?>
