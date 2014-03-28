<?php

include "params.inc";	// everything you need to set is here and in global_params.inc

// Confirm basic options 

$proceed=responseYesNoDie($msg_confirm);
if ($proceed===false) die("\r\nOperation cancelled\r\n");

// Check for required files
include_once "check_required_files.inc";

// Start timer and connect to mysql
echo "\r\n********* Begin operation *********\r\n\r\n";
include $timer_on;
$dbh = mysql_connect($HOST,$USER,$PWD,FALSE,128);
if (!$dbh) die("\r\nCould not connect to MySQL!\r\n");

$sql="USE `$DB`";
sql_execute_multiple($sql);

///////////////////////////////////////
// Populate acceptedNameID for some
// synonymous names wrongly labeled as
// "No Opinion" by Computed Acceptance
//
// Requires as input the result
// of a manual download obtained by a 
// Tropicos insider! See required
// file in directory data/
///////////////////////////////////////

include_once "noOpNames_import.inc";
include_once "noOpNames_prepare.inc";
include_once "noOpName_update.inc";

///////////////////////////////////////
// Transfer taxonomic status and links to 
// accepted name for species belonging to
// a non-accepted genus but wrongly labeled
// by ComputedAcceptance as "Accepted"
///////////////////////////////////////
include_once "link_to_acceptedNames.inc";

//////////////////////////////////////////////////////////////////
// Close connection and report total time elapsed 
//////////////////////////////////////////////////////////////////

mysql_close($dbh);
include $timer_off;
$msg = "\r\nTotal time elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
echo $msg . "\r\n\r\n"; 

?>