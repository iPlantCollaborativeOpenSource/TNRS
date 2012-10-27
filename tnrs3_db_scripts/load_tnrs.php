<?php

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
// Build TNRS database from multiple sources 
//
// Can rebuild complete database, or update individual sources
// in existing database, depending on options set in parameters
// file (global_params.inc).
// This is the master script; calls all others
//
// Author: Brad Boyle
// Email: bboyle@email.arizona.edu OR ojalaquellueva@gmail.com
// TNRS DB version: 3.5
// TNRS version: 3.0
// Latest revision date: 25 May 2012
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// Parameters
//
// You MUST set critical parameters in this file, also in
// local params.inc file located in each import_[sourceName]/
// subdirectory
//
// For an example build of the TNRS, use sources
// tropicos_example, tropicos_example2, and usda_example
////////////////////////////////////////////////////////////
include "global_params.inc";

// Make list of sources, as specified in parameters file
$sources = "";
foreach ($src_array as $src) {
	$sources = $sources . $src . ", ";
}
$sources = substr_replace($sources, '', strlen($sources)-2,-1);

////////////////////////////////////////////////////////////
// Run preliminary checks and confirm operations
// All checks are made at beginning to avoid interrupting
// execution later on.
////////////////////////////////////////////////////////////

// Confirm basic parameters 
$db_backup_display=$use_db_backup===true?"\r\n  Backup database: $DB_BACKUP":"";
$replace_db_display=$replace_db?'Yes':'No';
$msg_proceed="
Rebuilding TNRS database with the following settings:\r\n
  Host: $HOSTNAME
  Main database: $DB
  Replace database: $replace_db_display".$db_backup_display."\r\n  Sources: $sources\r\n
Enter 'Yes' to proceed, or 'No' to cancel: ";
$proceed=responseYesNoDie($msg_proceed);
if ($proceed===false) die("\r\nOperation cancelled\r\n");

// Confirm replacement of entire database if requested
if ($replace_db) {
	$msg_conf_replace_db="\r\nPrevious database `$DB` will be deleted! Are you sure you want to proceed? (Y/N): ";
	$confirm_replace_db=responseYesNoDie($msg_conf_replace_db);
	if ($confirm_replace_db===false) die ("\r\nOperation cancelled\r\n");
}

if ($use_db_backup) {
	$msg_confirm_delete_db_backup="\r\nAlso replace previous backup database '".$DB_BACKUP."'? (Y/N): ";
	$confirm_delete_db_backup=responseYesNoDie($msg_confirm_delete_db_backup);
	if ($confirm_delete_db_backup===false) die ("\r\nOperation cancelled\r\n");		
}

// Check basic dependencies are present: directories, files
// This is currently pretty basic, but can be expanded as needed
include_once "check_dependencies.inc";

// Checks completed
// Start timer and connect to mysql
echo "\r\nBegin operation\r\n";
include $timer_on;
$dbh = mysql_connect($HOST,$USER,$PWD);
if (!$dbh) die("\r\nCould not connect to database!\r\n");

////////////////////////////////////////////////////////////
// Generate new empty database
////////////////////////////////////////////////////////////

if ($confirm_replace_db) {
	echo "\r\n#############################################\r\n";
	echo "Creating new database:\r\n\r\n";	
	
	// Drop and replace entire database
	echo "Dropping previous database `$DB`...";
	$sql_create_db="
		DROP DATABASE IF EXISTS `".$DB."`;
		CREATE DATABASE `".$DB."`;
		USE `".$DB."`;
	";
	sql_execute_multiple($sql_create_db);
	echo "done\r\n";
	
	// Replace core tables
	// If for some reason you want to keep non-core tables from
	// a previous database build (for example, fg_lookup)
	// while replacing the core tables, comment out the
	// preceding 'sql_execute_multiple' statement
	include_once "create_tnrs_core/create_tnrs_core.php";
}

// Re-connect to database, in case previous step skipped
$sql="USE `".$DB."`;";
sql_execute_multiple($sql);
	
// Check that required functions present in target db
// Install them if missing
// This step is obviously essential if database has been replaced
include_once "check_functions.inc";

////////////////////////////////////////////////////////////
// Create/replace backup database if requested
////////////////////////////////////////////////////////////

if ($use_db_backup && $confirm_delete_db_backup) {
	echo "Creating backup database `$DB_BACKUP`...";
	// Drop and replace entire backup database
	$sql_create_db="
		DROP DATABASE IF EXISTS `".$DB_BACKUP."`;
		CREATE DATABASE `".$DB_BACKUP."`;
	";
	sql_execute_multiple($sql_create_db);
	echo "done\r\n\r\n";
}

////////////////////////////////////////////////////////////
// Load taxonomic sources
////////////////////////////////////////////////////////////

$src_no=1;
$src_suffix = "";
foreach ($src_array as $src) {
	echo "\r\n#############################################\r\n";
	echo "Loading source #".$src_no.": '".$src."'\r\n\r\n";	
	$src_suffix .= "_".$src;
	
	// reset the clock for this source
	include $timer_off;
	$resettime = $endtime;

	include_once "import_".$src."/import.php";
	include "prepare_staging/prepare_staging.php";
	include "load_core_db/load_core_db.php";	
				
	if ($backup_by_source) {
		// Back up entire database after loading each source
		// Saves a compressed mysqldump of entire database to 
		// directory $DB_BACKUP
		$backup_file=$DB.$src_suffix;
		$backup_pathfile = $BACKUP_DIR.$backup_file;
		$tarfile = $backup_pathfile.".tar.gz";
		echo "\nBacking up db to $backup_pathfile...";
		$cmd="mysqldump --opt -u $USER --password=$PWD -B $DB > $backup_pathfile";
		system($cmd);
		$cmd = "tar -czf $tarfile $backup_pathfile";
		system($cmd);
		$cmd = "rm $backup_pathfile";
		system($cmd);
		echo "done\r\n";
	} 
	
	$src_no++;	
	// report time for this source
	include $timer_off;
	$elapsedtime = $endtime - $resettime;
	$tsecs = round($elapsedtime,2);	
	echo "\r\nProcessing time for '$src': " .$tsecs . " seconds\r\n\r\n";
}

//////////////////////////////////////////////////////////////////
// Final cleanup of temporary fields from core database
//////////////////////////////////////////////////////////////////

include_once "cleanup/tnrs_core_cleanup.inc";

////////////////////////////////////////////////////////////
// Produce new genus-in-family lookup tables
// based on GRIN genus-family taxonomy
////////////////////////////////////////////////////////////

include $timer_off;
$resettime = $endtime;

echo "\r\n#############################################\r\n";
include_once "genus_family_lookups/make_genus_family_lookups.php";

include $timer_off;
$elapsedtime = $endtime - $resettime;
$tsecs = round($elapsedtime,2);	
echo "\r\nProcessing time this step: " .$tsecs . " seconds\r\n\r\n";

////////////////////////////////////////////////////////////
// Create and populate taxamatch tables
////////////////////////////////////////////////////////////

include $timer_off;
$resettime = $endtime;

if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die("\r\nFailed to connect to database!\r\n");
echo "\r\n#############################################\r\n";
include_once "taxamatch_tables/taxamatch_tables.php";

include $timer_off;
$elapsedtime = $endtime - $resettime;
$tsecs = round($elapsedtime,2);	
echo "\r\nProcessing time this step: " .$tsecs . " seconds\r\n\r\n";

////////////////////////////////////////////////////////////
// For all taxonomic sources marked $isHigherClassification=1
// attempts to assign family for all names in database
////////////////////////////////////////////////////////////

include $timer_off;
$resettime = $endtime;

echo "\r\n#############################################\r\n";
include_once "higherClassification/higherClassification.php";

include $timer_off;
$elapsedtime = $endtime - $resettime;
$tsecs = round($elapsedtime,2);	
echo "\r\nProcessing time this step: " .$tsecs . " seconds\r\n\r\n";

//////////////////////////////////////////////////////////////////
// Remove any remaining temporary tables
//////////////////////////////////////////////////////////////////

echo "\r\n#############################################\r\n";
include_once "cleanup/autocleanup.php";

//////////////////////////////////////////////////////////////////
// Close connection and report total time elapsed 
//////////////////////////////////////////////////////////////////

mysql_close($dbh);
include $timer_off;
$msg = "\r\nTotal time elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
if  ($echo_on) echo $msg . "\r\n\r\n"; 

//////////////////////////////////////////////////////////////////
// End script
//////////////////////////////////////////////////////////////////

?>