<?php
// Everything you need to set is here and in global_params.inc

include "../global_params.inc";		// most parameters here

// Names of files you will be importing
// Include path to file in name if not in same directory as this script
$path="";
//$namesfile = $path."tropicos_api_pinaceae_example.txt";
$namesfile = $path."tropicos_api_pinaceae_example.txt";

// names of destination mysql table
$tbl_names_raw="names_raw";

// Import parameters for text file
// Needed for 'LOAD DATA INFILE' statement
// Set any variable to empty string to remove entirely
$local = " LOCAL ";	// LOCAL keyword
$fields_terminated_by = " FIELDS TERMINATED BY ',' ";
// $fields_terminated_by = " FIELDS TERMINATED BY '\t' ";
$optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
//$optionally_enclosed_by = "";
$lines_terminated_by = " LINES TERMINATED BY '\n' ";
$ignore_lines = " IGNORE 1 LINES ";	// Ignore header line?

////////////////////////////////////////////////////////////////////////////////////

// confirmation message
$confmsg="\r\nImport file '$namesfile' of names and synonymy from source='$sourceName' to database `$DB`? (Y/N):";

?>
