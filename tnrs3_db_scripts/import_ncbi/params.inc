<?php
// Everything you need to set is here and in global_params.inc
// Parameters in this file are specific to this data source. 

//////////////////////////////////////////////////
// Source name, urls, and nomenclatural code
/////////////////////////////////////////////////

// Name which uniquely identifies this source within database
// Many operations, including table names, depend on this parameter
// REQUIRED
// MUST BE UNIQUE
// NO SPACES
// MAX 50 characters
$sourceName="ncbi";	

// Longer, more descriptive name for source
// Can have spaces; not used in any scripts
// after adding to table `source`
$sourceNameFull="NCBI Taxonomy";

// url of information page on this resource
$sourceUrl="http://www.ncbi.nlm.nih.gov/taxonomy";		

// date of access of raw data
$dateAccessed="2011-10-25";	

// NOT LONGER USED
// KEEP FOR NOW UNTIL I REMOVE THIS COLUMN AND ALL REFERENCES FROM DB
// Use as default classification for building taxamatch tables
// If you set this to true, all other sources will be
// set to false
$is_default=false;	

// Set to 1 if there are any issues/warnings associated with this database
// For now the further details of the warning are handled by the ui; may
// eventually add those details to the db
$source_warning=1;

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

// base url to combine with ID to retrieve information on an individual name
$source_base_url="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=";

// Nomenclatural code represented by this source
// If >1 for this source, then choose NULL
// Null values will be populated at end of
// process, based on classification
$default_nomenclaturalCode="ICBN";

//////////////////////////////////////////////////
// Names and location of raw data files
/////////////////////////////////////////////////

$ext=".dmp";			// All dump files should have same extension
$tbl_prefix="ncbi_";		// add this to base name for mysql tables
$path="import_ncbi/data/";			// path to dump files
$remove_raw_tables=false;	// set true to delete the raw data tables when finished

// Base names for files and raw data tables
// add prefix to tables, ext to files
$nodes="nodes";
$names="names";
$division="division";
$gencode="gencode";
$delnodes="delnodes";
$merged="merged";
$citations="citations";

// MySQL table names
$tbl_nodes=$tbl_prefix.$nodes;
$tbl_names=$tbl_prefix.$names;
$tbl_division=$tbl_prefix.$division;
$tbl_gencode=$tbl_prefix.$gencode;
$tbl_delnodes=$tbl_prefix.$delnodes;
$tbl_merged=$tbl_prefix.$merged;
$tbl_citations=$tbl_prefix.$citations;

// Array for importing files into table
// table and file name must be same except for extenstion ($ext)
$tbls = array(
  $nodes,
  $names,
  $division,
  $gencode,
  $delnodes,
  $merged,
  $citations
);

//////////////////////////////////////////////////
// Name and location of names_parsed file
//////////////////////////////////////////////////

// 'names_parsed' file is result of running all unique 
// names through the TNRS-GNI name parser
// This special step is unique to the loading process for ncbi
// and is REQUIRED to separate authors from names, which
// otherwise exist in concatenated form only
// The TNRS staging table requires names and authors in separate fields
// Also, ncbi has separate entries for name+author and name
// These must be merged into single records in the TNRS.
// This file takes a long time to prepare (~65 hours, Sept. 2011)
// Recommend placing in same directory as the raw ncbi data, but
// place it where you wish.
// Both raw ncbi data and names_parsed file must be ready to load ncbi
$names_parsed_file='ncbi_names_parsed.txt';  	// Name of the file
$names_parsed_path=$path; 			// Path to file
$names_parsed="names_parsed";			// Name of the mysql table

//////////////////////////////////////////////////
// Import parameters for raw data files
//////////////////////////////////////////////////

// Needed for 'LOAD DATA INFILE' statement
// To remove any attribute entirely, set the attribute's 
// variable to empty string; don't just comment out the variable
$local = " LOCAL ";	// LOCAL keyword
$fields_terminated_by = " FIELDS TERMINATED BY '\t|\t' ";
// $fields_terminated_by = " FIELDS TERMINATED BY '\t' ";
//$optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
$optionally_enclosed_by = "";
$lines_terminated_by = " LINES TERMINATED BY '\t|\n' ";
$ignore_lines = " ";	// Ignore header line?
//$ignore_lines = " IGNORE 1 LINES ";	// Ignore header line?

//////////////////////////////////////////////////
// Import parameters for names_parsed file
//////////////////////////////////////////////////

$np_local = " LOCAL ";	// LOCAL keyword
$np_fields_terminated_by = " FIELDS TERMINATED BY ',' ";
// $np_fields_terminated_by = " FIELDS TERMINATED BY '\t' ";
$np_optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
//$np_optionally_enclosed_by = "";
$np_lines_terminated_by = " LINES TERMINATED BY '\n' ";
//$np_ignore_lines = " ";	// Ignore header line?
$np_ignore_lines = " IGNORE 2 LINES ";	// Ignore header line?

?>
