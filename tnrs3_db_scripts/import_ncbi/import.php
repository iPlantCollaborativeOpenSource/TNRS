<?php

/////////////////////////////////////////////////////////////////////////////////////
// Purpose: 
// Import raw dump of names & synonymy from single source into MySQL 
// This script is customized for ncbi
/////////////////////////////////////////////////////////////////////////////////////

include "params.inc";	

////////////// Import raw data  //////////////////////

echo "Building raw data tables...";
include "sql_drop_tables.inc";
if (sql_execute_multiple($sql_drop_tables));
include "sql_create_tables.inc";
if (sql_execute_multiple($sql_create_tables));
echo $msg_success;

// Import data from dump files into the raw ncbi tables
include "import_ncbi.inc";

////////////// Import names_parsed  //////////////////////
// Import separate file containing the raw names parsed into
// name and author using the TNRS-GNI name parser.
// See readme file for detailis
//
echo "Creating table $names_parsed...";
include "sql_names_parsed_drop_table.inc";
if (sql_execute_multiple($sql_drop_tables));
include "sql_names_parsed_create_table.inc";
if (sql_execute_multiple($sql_create_tables));
echo $msg_success;

include "names_parsed_import.inc";
include "names_parsed_alter.inc";	// checks and restructures names_parsed table
include "names_parsed_update.inc";	// populate nameMinusAuthor field in names_parsed table

////////////// Populate staging table //////////////////////

// Create staging table
include "create_staging_table.inc";

// Add names & synonymy to staging table
include "add_names_to_staging.inc";

// Clean up
if ($remove_raw_tables) {
	echo "Dropping raw data tables...";
	include "sql_drop_tables.inc";
	if (sql_execute_multiple($sql_drop_tables));
echo $msg_success;
}

echo "\r\n";

//////////////////////////////////////////////////////////

?>
