<?php
// Everything you need to set is here and in local params.inc file
// in folder for each main script

//////////////////////////////////////
// Include paths and filenames
//////////////////////////////////////

$utilities_path="";

// functions and utilities
include $utilities_path."functions.inc";
include $utilities_path."taxon_functions.inc";
include $utilities_path."sql_functions.inc";
$timer_on=$utilities_path."timer_on.inc";
$timer_off=$utilities_path."timer_off.inc";

// Error-reporting & behavior at runtime
$echo_on = true;		// echos error messages to screen
$die_on_fail = true;		// aborts entire script on error
$msg_success = "done\r\n";

//////////////////////////////////////
// Db configuration
/////////////////////////////////////
$HOSTNAME = ""; // Short name for progress message only; optional
// The next four parameters are required
$HOST = "";
$USER = "";
$PWD = "";
$DB = "";

//////////////////////////////////////
// Taxonomy source parameters
//////////////////////////////////////
// MUST SET FOR EVERY IMPORT

// Source information
/*
$sourceName="Tropicos";
$sourceUrl="http://www.tropicos.org/";
$date_accessed="2010-08-18";		// date of access of raw data
 */
$sourceName="Pines example";		// Name which uniquely identifies this source within database; required
$sourceUrl="http://www.tropicos.org/";	// base url of source, if applicable(optional)
$date_accessed="2010-06-03";		// date of access of raw data (optional)


$is_default=true;		// Set=true (NO QUOTES!) if wish to use this 
				// source as default higher classification 
				// & synonymy, else set=false. Be careful:
				// will wipe out previous default classification

// Replace previous records for this source?
// VERY IMPORTANT
// Set=true to replace all previous records for this source.
// Set=false to keep existing records and add new records only.
$replace=true;

// Repair extended character codes?
// Set=true if authority field contains extended 
// character set (accented) characters that are 
// rendering improperly.
// Recommended, but takes longer
$fix_chars=true;

//////////////////////////////////////
// END Taxonomy source parameters
//////////////////////////////////////

//////////////////////////////////////
// Tree traversal parameters
//////////////////////////////////////
$root_name = "root";		// name of root
$root_rank = "root";		// name of root rank
$add_root = TRUE;		// Set FALSE if table already contains root record
$preroot = true;		// Adds temporary node above root during indexing
				// Must set=TRUE if any taxa lack parentID in 
				// original data ("orphan taxa") otherwise 
				// traversal will fail

//////////////////////////////////////
// Table & column names
//////////////////////////////////////
// In general, will not need to change these

//////////////////////////////////////
// Staging tables
//////////////////////////////////////
$tbl = "nameStaging";		// main staging table
$tbl_syn = "synonymStaging";	// temporary staging table for synonymy only
				// Used only if synonyms in separate table
				// from names in raw data
$id_fld = "nameID";		// name of ID field (PK) for this table
$parent_id_fld = "parentNameID";	// name of parentID field
$source_url_fld="nameUri";		// field containing source url for name
$rank_fld = "nameRank";		// taxonomic rank field
$name_fld = "scientificName";		// field containing taxon name
$author_fld = "scientificNameAuthorship";		// field containing authority
$subclass_fld="subclass";
$family_fld="family";
$genus_fld="genus";
$specific_epithet_fld="specificEpithet";
$rank_ind_fld="rankIndicator";			// Abbreviated rank indicator of trinomial
$infraepithet_fld="infraspecificEpithet";	// Trinomial epithet
$rank_ind_fld2="rankIndicator2";		// Quadrinomial rank indicator
$infraepithet_fld2="infraspecificEpithet2";	// Quadrinomial epithet
$otherEpithet_fld="otherEpithet";		// epithets of tribes, sections, etc.
$subclass_fld="subclass";		// holds family, if provided by source
$family_fld="family";			// holds subclass, if provided by source
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

//////////////////////////////////////
// Error table
//////////////////////////////////////
$error_tbl ="error_table";
$srcTbl="source_table";
$has_errors="has_errors";
$is_orphan_parentNameID="is_orphan_parentNameID";
$is_orphan_acceptedNameID="is_orphan_acceptedNameID";	
$is_orphan_synonym_nameID="is_orphan_synonym_nameID";	
$is_null_parentNameID="is_null_parentNameID";
$errmsg="error_message";	

//////////////////////////////////////
// Default classification 
// column names in `name` table
// in core db
//////////////////////////////////////
$default_subclass_fld="defaultSubclass";
$default_family_fld="defaultFamily";
$default_acceptance_fld="defaultAcceptance";		
$default_accepted_id_fld="defaultAcceptedNameID";

//////////////////////////////////////
// END Table & column names
//////////////////////////////////////

?>
