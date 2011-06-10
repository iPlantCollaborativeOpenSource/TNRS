<?php
// Imports raw dump of names & synonymy from single source MySQL tables

include "params.inc";	// everything you need to set is here and in global_params.inc

// Confirm with user before starting
if (confirm($confmsg)) {
	echo "\r\nBegin operation\r\n\r\n";
}

// start time and connect to database
include $timer_on;
if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die();

////////////// Import raw data file //////////////////////

// create empty staging table
include_once "create_raw_data_tables.inc";

// import text files to raw data tables
include_once "import.inc";

// Create staging table
 include_once "create_staging_table.inc";

// Add names & synonymy to staging table
include_once "add_names.inc";

// Add names & synonymy to staging table
include_once "zero_id_set_null.inc";

// Create error table
include_once "create_error_table.inc";

// Flag errors (bad nameIDs, parentNameIDs, acceptedNameIDs)
include_once "flag_errors.inc";

// Remove orphan parentNameIDs and acceptedNameIDs from staging table
include_once "fix_errors.inc";

//////////////////////////////////////////////////////////

mysql_close($dbh);
include_once $timer_off;
$msg = "\r\nTime elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
if  ($echo_on) echo $msg . "\r\n\r\n"; 

?>
