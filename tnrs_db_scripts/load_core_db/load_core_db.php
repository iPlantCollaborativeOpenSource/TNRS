<?php
// Imports & normalizes names & synonymy from
// to staging table into TNRS core database
//
// Depending on option $replace (true/false; see params.inc), will either 
// (a) replace existing records linked only to this source, plus add new names, or
// (b) add new names only.
// Assumes empty database already exists, and 
// staging table has already been populated

include "params.inc";	// everything you need to set is here and dbconfig.inc

// Confirm with user before starting
if (confirm($confmsg)) {
	echo "\r\nBegin operation\r\n\r\n";
	include $timer_on;
}

// connect to database
if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die();

////////////// Load staging table to core db //////////////////////

echo "\r\nLoading contents of `$tbl` to core db:\r\n";

// Update source information and get sourceID (needed for later steps)
include_once 'load_source.inc';

// Delete previous records
// Tag records linked ONLY to current source, then delete those records
if ($replace===true) {
	include_once "clear_previous.inc";
}

// Clear FKs in staging table
// Allows loading from staging table that has been
// used previously to load to core db
// Not necessary if do clean reload
include_once "clear_fks.inc";
 
// Load names
include_once 'load_names.inc';

// Index name components
include_once 'index_name_parts.inc';

// Load source-specific name attributes
include_once 'load_name_sources.inc';

// Load source-specific synonymy
include_once 'load_synonymy.inc';

// Load default synonymy, if applicable
if ($is_default===true) {
	include_once 'load_default_synonymy.inc';
}

// Load source-specific classification
include_once 'load_classification.inc';

// Load default classification
if ($is_default===true) {
	include_once 'load_default_classification.inc';
}

// Remove staging table
// include_once 'cleanup.inc';

mysql_close($dbh);
include_once $timer_off;
$msg = "\r\nTime elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
if  ($echo_on) echo $msg . "\r\n\r\n"; 

?>
