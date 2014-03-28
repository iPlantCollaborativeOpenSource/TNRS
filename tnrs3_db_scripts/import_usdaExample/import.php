<?php
// Imports raw dump of names & synonymy from single source MySQL tables

include "params.inc";	// everything you need to set is here and in global_params.inc

////////////// Import raw data file //////////////////////

// create empty import table
include "create_raw_data_table.inc";

// import text files to raw data tables
include "import.inc";

// Alter raw names table
// Adds additional fields:
// nameID, parentNameID, acceptance, acceptedNameID
include "alter_raw_data_table.inc";

// Extract families
include "add_families.inc";

// Populate the newly added fields
include "update_raw_data_table.inc";

// Create staging table
include "create_staging_table.inc";

// Add names & synonymy to staging table
include "add_names_to_staging.inc";

//////////////////////////////////////////////////////////

?>
