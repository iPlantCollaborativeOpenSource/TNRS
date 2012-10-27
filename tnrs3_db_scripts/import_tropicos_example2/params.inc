<?php

//////////////////////////////////////////////////
// Parameters specific to this source
//////////////////////////////////////////////////

// sourceName
// Name which uniquely identifies this source within database
// Many operations, including table names, depend on this parameter
// REQUIRED
// MUST BE UNIQUE
// NO SPACES
// MAX 50 characters
$sourceName="tropicos_example2";

// Longer, more descriptive name for source
// Can have spaces; not used in any scripts
// after adding to table `source`
$sourceNameFull="Tropicos example 2";

// Primary url for this source (optional)
$sourceUrl="http://www.yahoo.com";

// date of access of raw data
// Use MySQL format date: yyyy-mm-dd
$dateAccessed="2012-01-02";	

// NOT LONGER USED
// KEEP FOR NOW UNTIL I REMOVE THIS COLUMN AND ALL REFERENCES FROM DB
// Use as default classification for building taxamatch tables
// If you set this to true, all other sources will be
// set to false
$is_default=false;	

// Set to 1 if there are any issues/warnings associated with this database
// For now the further details of the warning are handled by the ui; may
// eventually add those details to the db
$source_warning=0;

// Use this source as higher classification for all names in database?
// If true, all names in database will be joined by genus (or failing genus, by family)
// to higher taxonomic categories (genus and above) for this source
// Core database can hold multiple higher classifications;
// therefore will not affect existing classification systems.
$is_higher_classification=true;

// Replace previous records for this source?
// VERY IMPORTANT
// Set=true to replace all previous records for this source.
// Set=false to keep existing records (name+author) and add new records only.
$replace=true;

// Repair extended character codes, converting to utf8?
// Set=true if authority field contains extended character set (accented) characters
// Only set to false if you are sure this data source contains only utf8 characters
$fix_chars=true;

//////////////////////////////////////////////////
// Raw data file parameters
//////////////////////////////////////////////////

// Names of raw data file you will be importing
// Path to file must include the directory of this script
$path="import_tropicos_example2/data/";
$namesfile = "tropicos_pinaceae2.txt";
$namesfileandpath = $path.$namesfile;

// names of destination table in MySQL
$tbl_names_raw=$sourceName."_raw";

// Import parameters for text file
// Needed for 'LOAD DATA INFILE' statement
// Set variable to empty string to remove entirely, do not just comment out line
$local = " LOCAL ";	// LOCAL keyword
//$fields_terminated_by = " FIELDS TERMINATED BY ',' ";
$fields_terminated_by = " FIELDS TERMINATED BY '\t' ";
//$optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
$optionally_enclosed_by = "";
$lines_terminated_by = " LINES TERMINATED BY '\r' ";
$ignore_lines = " IGNORE 1 LINES ";	// Ignore header line?

////////////////////////////////////////////////////////////////////////////////////

?>