<?php
// loads csv file of tnrs name resolution results into mysql
// Adds indexed columns for linking to`nameStaging`
// and `name`(core db), and populates.
// Used to flag names already in core db during loading

echo "Loading TNRS results for source `$sourceName`:";
echo "  Creating table $tnrs_results...";
if (delete_tbl($tnrs_results, $DB));
include "sql_create_table_tnrs_results.inc";
$msg_error="failed to create table `$tnrs_results`!";
if (sql_execute($sql_create_tables,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "  Importing file $tnrs_results_file to table $tnrs_results...";
$msg_error = "failed!\r\n";
$sql = "LOAD DATA $tr_local INFILE '$tnrs_results_path$tnrs_results_file' 
	INTO TABLE $tnrs_results 
	$tr_fields_terminated_by 
	$tr_optionally_enclosed_by 
	$tr_lines_terminated_by 
	$tr_ignore_lines;
";
//echo "\r\n$sql\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Standardize NULLS
echo "  Converting empty strings to NULL...";
if (!(empty_string_to_null($tnrs_results))) die("Error\r\n.");
echo "done\r\n";

// Add indexes
echo "  Indexing...";
$sql="
	alter table `$tnrs_results`
	add column `tnrs_nameID` integer(11) unsigned default null,
	add column `nameStaging_nameID` integer(11) unsigned default null,
 	add index `".$tnrs_results."_tnrs_nameID`(tnrs_nameID),
	add index `".$tnrs_results."_nameStaging_nameID`(nameStaging_nameID),
	add index `".$tnrs_results."_nameSubmitted`(nameSubmitted),
	add index `".$tnrs_results."_nameMatched`(nameMatched),
	add index `".$tnrs_results."_nameMatchedAuthor`(nameMatchedAuthor),
	add index `".$tnrs_results."_overallScore`(overallScore)
	";
$msg_error="failed to index table `$tnrs_results`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Link to names in nameStaging
echo "  Linking to staging table...";
$sql="
	update `$tnrs_results` r inner join `$tbl_staging` s
	on r.nameSubmitted=s.nameWithAuthor
	set r.nameStaging_nameID=s.nameID
	";
$msg_error="failed to link `$tnrs_results` to names in `$tbl_staging`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Link to names in core db
echo "  Linking to core database...";
// name+author
$sql="
	update `$tnrs_results` r inner join `name` n
	on r.nameMatched=n.scientificName and r.nameMatchedAuthor=n.scientificNameAuthorship
	set r.tnrs_nameID=n.nameID;
	";
$msg_error="failed to link `$tnrs_results` to names in table `name` (using name+author)!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

// name, no author
$sql="
	update `$tnrs_results` r inner join `name` n
	on r.nameMatched=n.scientificName
	set r.tnrs_nameID=n.nameID
	where r.nameMatchedAuthor is null and n.scientificNameAuthorship is null and r.tnrs_nameID is null;
	";
$msg_error="failed to link `$tnrs_results` to names in table `name` (using name only)!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
