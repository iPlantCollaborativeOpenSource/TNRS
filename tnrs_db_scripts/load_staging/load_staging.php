<?php
// Load raw data files of names & synonymy from single source
// into single flattened staging table
//
// Checsk and indexes staging table, including tree traversal

include "params.inc";	// everything you need to set is here 

// Confirm with user before starting
if (confirm($confmsg)) {
	echo "\r\nBegin operation\r\n\r\n";
	include $timer_on;
}

// connect to database
if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die();

////////////// Load, check and index staging table //////////////////////

// Fix character set issues, if necessary
// Can turn this step off in params.inc
// by setting $fix_chars=false
include_once "fix_character_sets.inc";	

// Flag hybrids so can exclude in later steps
include_once "fix_hybrid_x.inc";

// Flag hybrids so can exclude in later steps
include_once "flag_hybrids.inc";

// Set to NULL any orphan values of parentID
include_once "delete_orphan_parentIDs.inc";

// Flag any records with parentID=NULL
include_once "flag_null_parentID.inc";

// Attempt to link to parent any record with parentID=NULL
// Parses "parent" part of name string
// And joins to parent string to discover ID
include_once "find_parent.inc";

// populate right and left indices via modified tree traversal
include_once "index_taxa.inc";

// Parse name components for infraspecific taxa
// Assumes that genera and specific epithets
// already parsed in source data
// Need to add extra parsing step if this is
// not true
include_once "parse_infraspecific_taxa.inc";

// Standardize infraspecific rank indicators
// Infraspecific name components must have been parsed prior to this step
include_once "standardize_rank_indicators.inc";

// Extract higher taxa and atomic name components for remaining taxa
include_once "higher_taxa.inc";
 
// Update error table (remove any names without errors
include_once "update_error_table.inc";

echo "Table `$tbl` successfully created!\r\n";

//////////////////////////////////////////////////////////////

mysql_close($dbh);
include_once $timer_off;
$msg = "\r\nTime elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
if  ($echo_on) echo $msg . "\r\n\r\n"; 

?>
