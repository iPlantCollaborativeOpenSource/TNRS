<?php
// Imports & normalizes names & synonymy from
// to staging table into TNRS core database
//
// Depending on option $replace (true/false; see params.inc), will either 
// (a) replace existing records linked only to this source, plus add new names, or
// (b) add new names only.
// Assumes empty database already exists, and 
// staging table has already been populated

echo "\r\nLoading staging table to core db:\r\n";

////////////// Load tnrs results  //////////////////////

if ($fuzzy_match==true) {
	// Only need to do this if intend to fuzzy match
	include "load_tnrs_results.inc";
}

////////////// Load staging table to core db //////////////////////

// Update source information and get sourceID (needed for later steps)
include 'load_source.inc';

// Delete previous records
// Tag records linked ONLY to current source, then delete those records
if ($replace===true) {
	include "clear_previous.inc";
}

// Clear FKs in staging table
include "clear_fks.inc";
 
// Load names
// Indexes names already in db
// Appends any new names
// Corrects parent name links
include 'load_names.inc';

// Index name components
include 'index_name_parts.inc';

// Load source-specific name attributes
include 'load_name_sources.inc';

// Load source-specific synonymy
include 'load_synonymy.inc';

// Load source-specific classification
// For now, adds classification for all
// names in new source, including names already
// in database, as long as they are also in new source)
include 'load_classification.inc';

// Remove staging table
// include 'cleanup.inc';

?>
