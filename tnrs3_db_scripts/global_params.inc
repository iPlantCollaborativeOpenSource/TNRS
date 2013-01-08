<?php

//////////////////////////////////////////////////
//////////////////////////////////////////////////
// Loading scripts for TNRS core database
//
// DB version: 3.5.2
// Last DB revision date: 23 May 2012
// By: Brad Boyle
// Contact: bboyle@email.arizona.edu
// TNRS website: http://tnrs.iplantcollaborative.org/
//
// Key changes:
// - support for multiple sources of names,
//   synonymy and classifications
//  
// Everything you need to set is here and in local
// params.inc file in each import directory
//
// Read the readme files in this directory and 
// all main subdirectories!
//////////////////////////////////////////////////
//////////////////////////////////////////////////

//////////////////////////////////////////////////
// Taxonomy sources
//
// CRITICAL!!!
// Include in this array the name of every source
// you will be importing into the TNRS database
// Import scripts and data for each source MUST
// be their own directory called "import_[$sourceName]",
// for example, "import_tropicos". Source names 
// below MUST match the parameter $sourceName 
// as specified in params.inc in import subdirectory
// for that source. 
//////////////////////////////////////////////////

// The follow configuration is for a test build using three example taxonomic
// sources provided:
$src_array=array(
	"usda_example",
	"dwc_example",
	"tropicos_example",
	"gcc_example"
);

//////////////////////////////////////////////////
// Replace database? (true/false)
//
// Set to true to replace database entirely
// all tables will be dropped and replaced with new,
// empty tables. Application will automatically prompt
// for confirmation if this option is set to true.
//
// Set to false keep existing database and
// add (if new) or replace (if already present) only
// the source(s) listed in $src_array.
//
//////////////////////////////////////////////////

$replace_db = true;

//////////////////////////////////////////////////
// Include paths and filenames
//////////////////////////////////////////////////

// Temporary PHP memory limit
// Increase this amount if scripts in 
// taxamatch_tables/ subdirectory throw
// memory overflow error
ini_set("memory_limit","3000M");

$local_utilities_path="functions/";
$global_utilities_path="functions/";

include $global_utilities_path."functions.inc";
include $global_utilities_path."taxon_functions.inc";
include $global_utilities_path."sql_functions.inc";
$timer_on=$global_utilities_path."timer_on.inc";
$timer_off=$global_utilities_path."timer_off.inc";

// Taxamatch utilities
require_once $local_utilities_path."class.nearmatch.php";
require_once $local_utilities_path."class.normalize.php";

//////////////////////////////////////////////////
// Db connection info
//////////////////////////////////////////////////

// enter host here
// I recommend running as localhost
$HOST = "localhost";
// Explicit host name for display at runtime
$HOST=="localhost"?$HOSTNAME=exec('hostname -f'):$HOSTNAME=$HOST;
$USER = "tnrs_root";
$PWD = "rooter";
// Enter whatever your database will be called below.
// Database will be created if it doesn't exist (assuming you have full permissions)
$DB = "tnrs_test"; 

//////////////////////////////////////////////////
// Backup options
//////////////////////////////////////////////////

// Save intermediate tables to separate backup database?
// This will save copies of raw data tables
// and staging tables (if requested; see below)
// to database $DB_BACKUP
// A useful option for troubleshooting and validation
$use_db_backup=true;

// The following database will be used to store backup
// copies of particular tables. If database does not
// exist, it will be created; if it does exist, it will
// be replaced. Therefore, be VERY careful how you name
// this database!
$DB_BACKUP = "tnrs_test_backup";	// for testing

// Save a backup copy of staging table? This 
// prevents staging table for current source
// from being lost when a new source is loaded
// If set=true, be sure to set the name of the
// backup staging table, $tbl_staging_bak
// (see below)
$backup_staging=true;

// Save a mysqldump of entire database after loading each source?
// Will be saved to $BACKUP_DIR 
// Name of all sources in db are appended to
// mysqldump file, plus '.sql' suffix
$backup_by_source = true;

// directory, relative to this script, for saving database backups
$BACKUP_DIR = "backups/";

//////////////////////////////////////////////////
// Error-reporting & runtime behavior
//////////////////////////////////////////////////

// echos error messages to screen
$echo_on = true;		

// aborts entire script on error
$die_on_fail = true;		

// default success and fail messages
$msg_success = "done\r\n";
$msg_fail = "failed!\r\n";

//////////////////////////////////////////////////
// Higher taxon parameters
//////////////////////////////////////////////////

// Column separated array of higher taxa represented by denormalized 
// columns in table `name`. One column is added for EACH source flagged 
// isHigherClassification=true. Name of column is formed as follows:
// [higher taxon rank]_[higher classification source name]. E.g., 
// family_tropicos.
$higher_taxon_array=array(
	"family"
);

//////////////////////////////////////////////////
// Name resolution parameters
//////////////////////////////////////////////////

// Set fuzzy_match=true to resolve source names using tnrs.
// Requires file of results of matching source names against
// the TNRS database itself.
// Currently, this option only results in population of 
// the field `fuzzyMatchNameID` in table `name`. 
// Names matching *exactly* to names already
// in database are indexed rather than added. All other names are 
// added. In future, may find a way of indexing rather than adding
// fuzzy-matched names; however, this is dangerous as it disrupts
// child-parent adjacency among source names.
// Currently, to use this option you need to select all distinct
// names in source and submit to TNRS; csv results file should be
// made available to this script. In future, will automate
// entire process using web service. 
// If you set fuzzy_match=true, you MUST set the name and 
// path of the results file, below. 
// Setting fuzzy_match=false will skip import and normalization
// of the tnrs_results file, and leave column `fuzzyMatchNameID` NULL.
// The remaining name resolution parameters will be ignored.
// If you set fuzzy_match=true, you must set the remaining name
// resolution parameters
$fuzzy_match=false;

// name of csv file containing results of 
// processing source names using tnrs
$tnrs_results_file="tnrs_results.csv";	// made up example; your file name here

// path relative to load_core_db/, where 
// scripts accessing this file run:
$tnrs_results_path="tnrs_results_raw/";

// name of mysql table:
$tnrs_results="tnrs_results";

// Minimum overall match score required for name to be considered a match
$tnrs_overall_score_min=0.9;	

// Minimum score for author part of name (only applies to submitted names with authors)
$tnrs_author_score_min=0.6; 
	
// Import parameters for tnrs_results file
// To remove an option entirely, set it=" ",
// do not just delete the variable entirely
$tr_local = " LOCAL ";	// LOCAL keyword
$tr_fields_terminated_by = " FIELDS TERMINATED BY ',' ";
//$tr_optionally_enclosed_by = " ";
$tr_optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
//tr_lines_terminated_by = " LINES TERMINATED BY '\n' ";
$tr_ignore_lines = " IGNORE 1 LINES ";	// set 1 to ignore header line

//////////////////////////////////////////////////
// Name resolution parameters
//////////////////////////////////////////////////

// Set fuzzy_match=true to resolve source names using tnrs.
// Requires file of results of matching source names against
// the TNRS database itself.
// Currently, this option only results in population of 
// the field `fuzzyMatchNameID` in table `name`. 
// Names matching *exactly* to names already
// in database are indexed rather than added. All other names are 
// added. In future, may find a way of indexing rather than adding
// fuzzy-matched names; however, this is dangerous as it disrupts
// child-parent adjacency among source names.
// Currently, to use this option you need to select all distinct
// names in source and submit to TNRS; csv results file should be
// made available to this script. In future, will automate
// entire process using web service. 
// If you set resolve_names=true, you MUST set the name and 
// path of the results file, below. 
// Setting fuzzy_match=false will skip import and normalization
// of the tnrs_results file, and leave column `fuzzyMatchNameID` NULL.
// The remaining name resolution parameters will be ignored.
// If you set fuzzy_match=true, you must set the remaining name
// resolution parameters
$fuzzy_match=false;

// name of csv file containing results of 
// processing source names using tnrs
$tnrs_results_file="tnrs_results.csv";	// made up example; your file name here

// path relative to load_core_db/, where 
// scripts accessing this file run:
$tnrs_results_path="tnrs_results_raw/";

// name of mysql table:
$tnrs_results="tnrs_results";

// Minimum overall match score required for name to be considered a match
$tnrs_overall_score_min=0.9;	

// Minimum score for author part of name (only applies to submitted names with authors)
$tnrs_author_score_min=0.6; 	

// Import parameters for tnrs_results file
// To remove an option entirely, set it=" ",
// do not just delete the variable entirely
$tr_local = " LOCAL ";	// LOCAL keyword
$tr_fields_terminated_by = " FIELDS TERMINATED BY ',' ";
//$tr_optionally_enclosed_by = " ";
$tr_optionally_enclosed_by = " OPTIONALLY ENCLOSED BY '\"' ";  // use if text surrounded by quotes
//tr_lines_terminated_by = " LINES TERMINATED BY '\n' ";
$tr_ignore_lines = " IGNORE 1 LINES ";	// set 1 to ignore header line

//////////////////////////////////////////////////
// Tree traversal parameters
//////////////////////////////////////////////////

// These parameters affect population of left and right indices
// used for ancestor/descendent searches

// name of root
$root_name = "root";		

// name of root rank
$root_rank = "root";	

// Set FALSE if table already contains root record
$add_root = TRUE;		

// Adds temporary node above root during indexing
// Must set=TRUE if any taxa lack parentID in 
// original data ("orphan taxa") otherwise 
// traversal will fail
$preroot = true;	

//////////////////////////////////////////////////
// Table & column names
//////////////////////////////////////////////////

// In general, should not need to change these
// I just made them variables because I kept changing 
// my mind about what they should be called. Also
// for some reason I find it easier to spell a variable 
// consistently than a column or table name. Go figure.
// Eventually will change to using actual names throughout
// and eliminate this section

// Staging table 
$tbl = "nameStaging";			// staging table
$tbl_staging=$tbl;				// alternative name used by some scripts (will standardize eventually...)

$tbl_syn = "synonymStaging";			// temporary staging table for synonymy only
						// Used only if synonyms in separate table
						// from names in raw data

$id_fld = "nameID";				// name of ID field (PK) for this table

$nomenclaturalCode_fld = "nomenclaturalCode";	// code governing this name; default
						// value set in import parameters for the source being loaded

$parent_id_fld = "parentNameID";		// name of parentID field
$source_url_fld="nameUri";			// field containing source url for name
$lsid_fld="lsid";					// LSID field
$rank_fld = "nameRank";				// taxonomic rank field
$name_fld = "scientificName";			// field containing taxon name
$author_fld = "scientificNameAuthorship";	// field containing authority
$nameAuthor_fld = "scientificNameWithAuthor";	// taxon name with authority concatenated
$subclass_fld="subclass";
$family_fld="family";
$genus_fld="genus";
$specific_epithet_fld="specificEpithet";
$rank_ind_fld="rankIndicator";			// Abbreviated rank indicator of trinomial
$infraepithet_fld="infraspecificEpithet";	// Trinomial epithet
$rank_ind_fld2="rankIndicator2";		// Quadrinomial rank indicator
$infraepithet_fld2="infraspecificEpithet2";	// Quadrinomial epithet
$otherEpithet_fld="otherEpithet";		// epithets of tribes, sections, etc.
$subclass_fld="subclass";			// holds family, if provided by source
$family_fld="family";				// holds subclass, if provided by source
$is_hybrid="isHybrid";				// 0,1 (NOT NULL) flag field for hybrid
$left_index_fld = "leftIndex";			// left index field
$right_index_fld = "rightIndex";		// right index field
$fk_fld = "FK_nameID";				// FK linking to name table in core db
$parent_fk_fld="FK_parentNameID";		// FK linking parent name in name table
$null_parent_fld = "isNullParentID";		// Temporary field for flagging null parentTaxonIDs

// The following two column names used for both main staging
// table and synonym staging table (if any)
$acceptance_fld="acceptance";			// name of acceptance/taxonomic status field
$accepted_id_fld="acceptedNameID";		// name of accepted name ID field

// Error table 
$error_tbl ="error_table";
$srcTbl="source_table";
$has_errors="has_errors";
$is_orphan_parentNameID="is_orphan_parentNameID";
$is_orphan_acceptedNameID="is_orphan_acceptedNameID";	
$is_orphan_synonym_nameID="is_orphan_synonym_nameID";	
$is_null_parentNameID="is_null_parentNameID";
$errmsg="error_message";	

// Taxamatch table names
$tbl_fam="famlist";
$tbl_gen="genlist";
$tbl_sp="splist";
$tbl_infra1="infra1list";
$tbl_infra2="infra2list";
$tbl_comb_fg="genlist_famlist_combined";
$tbl_comb_gs="splist_genlist_combined";
$tbl_comb_st="infra1list_splist_combined";
$tbl_comb_tq="infra2list_infra1list_combined";

?>
