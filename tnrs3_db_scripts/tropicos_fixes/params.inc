<?php

//////////////////////////////////////////////////
// Include paths and filenames
//////////////////////////////////////////////////

$local_utilities_path="../functions/";
$global_utilities_path="../../global_utilities/";

include $global_utilities_path."functions.inc";
include $global_utilities_path."taxon_functions.inc";
include $global_utilities_path."sql_functions.inc";
$timer_on=$global_utilities_path."timer_on.inc";
$timer_off=$global_utilities_path."timer_off.inc";

//////////////////////////////////////////////////
// Db connection info
//////////////////////////////////////////////////

$HOST = "localhost";
// Explicit host name for display at runtime
$HOST=="localhost"?$HOSTNAME=exec('hostname -f'):$HOSTNAME=$HOST;
$USER = "";
$PWD = "";
$DB = "";

//////////////////////////////////////////////////
// Application parameters
//////////////////////////////////////////////////

$tropicosSourceID=1;

$msg_confirm="
Repair problems with source 'tropicos' (sourceID=$tropicosSourceID)\r\n
in TNRS database version $DB? Enter 'Yes' to proceed, or 'No' to cancel: ";

// Required input file
$dataDir = "data/";
$noOpNamesFile="noOpinionNamesResults_example.txt";

$done="done\r\n";

?>