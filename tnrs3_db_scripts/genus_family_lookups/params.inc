<?php


/////////////////////////////////////////////////////////////////
// Names of lookup tables to be created
/////////////////////////////////////////////////////////////////

$fg='gf_lookup';	//  name of new genus+family table
$faf='family_acceptedFamily_lookup';	//  name of new genus+family table

/////////////////////////////////////////////////////////////////
// Download option
// Set to true to refresh taxonomy data. Set to false if
// taxonomy tables are already present and do not need to be 
// refreshed
/////////////////////////////////////////////////////////////////

$download=true;

/////////////////////////////////////////////////////////////////
// Special DB configurations
// Avoid using generic `$DB` parameter anywhere in these scripts
// due to danger of deleting main TNRS database (see $DB_NEW, below)
/////////////////////////////////////////////////////////////////

// New temporary database where GRIN taxonomy tables will be loaded 
// and the new lookup tables created and populated.
// WARNING: this database will be created, deleting any existing 
// database with same name!
$DB_NEW = "tnrs_lookup_tables_temp";

// Destination database where completed lookup tables `genus_family_lookup` 
// and `faf_lookup` will be copied when complete. Will normally be
// the same as $DB, as set in global_params.
$DB_FINAL=$DB;

// Set the following to true to delete the temporary database
// when finished
$drop_db_on_finish=true;

/////////////////////////////////////////////////////////////////
// Name and source url of taxonomy used to build lookup tables
/////////////////////////////////////////////////////////////////

$source_name="grin";
$source_url="http://www.ars-grin.gov/misc/tax/";	

/////////////////////////////////////////////////////////////////
// Names of downloaded files and location they will be stored
// Raw files are dbf files, so need to be converted to plain
// test using dbf_dump (see download.inc for details)
// Currently only `genus` is used, but may use `family` in future
// to add to table of family-acceptedFamily pairs
/////////////////////////////////////////////////////////////////

$ext="";					// File extension after unzipping  
$plaintextext=".txt";		// Extension after converting dbf file to text
$tbl_prefix="";				// add this to base name for mysql tables
$this_dir="genus_family_lookups/";		// Directory of this script
$download_dir=$this_dir."download/";	// path to downloaded files
$remove_raw_tables=false;	// set true to delete the raw data tables when finished

// Base names for files and raw data tables
// add prefix to tables, ext to files
$family="family";
$genus="genus";

/////////////////////////////////////////////////////////////////
// Names of MySQL tables to which raw taxonomy file will be 
// loaded.
/////////////////////////////////////////////////////////////////

$tbl_family=$tbl_prefix.$family;
$tbl_genus=$tbl_prefix.$genus;

// Array for importing files into table
// table and file name must be same except for extenstion ($ext)
$tbls = array(
  $family,
  $genus,
);

/////////////////////////////////////////////////////////////////
// Parameters for importing raw taxonomy files using MySQL
// LOAD DATA INFILE command.
// Every attribute must have a value; therefore, to remove an 
// from the command, set its parameter to empty string; don't 
// just comment out the parameter.
/////////////////////////////////////////////////////////////////

// LOCAL keyword
// Must be present if host is localhost
$local = " LOCAL ";	

// Field terminator
$fields_terminated_by = " FIELDS TERMINATED BY '@' ";

// Is text optionally enclosed in quotes?
//$optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
$optionally_enclosed_by = "";

// Line terminator
//$lines_terminated_by = " LINES TERMINATED BY '\t|\n' ";
$lines_terminated_by = " LINES TERMINATED BY '\n' ";

// Set to ignore one or more header lines
//$ignore_lines = " IGNORE 1 LINES ";	// Use this if single line header
$ignore_lines = " ";					// Use this if no header


?>
