<?php

//////////////////////////////////////////////////////////
// Import names & synonymy from this source into MySQL
// Imports raw data file(s), does any source-specific
// restructuring, then loads to staging table
//////////////////////////////////////////////////////////

// Important! Import settings for this source
include "params.inc";	

//////////////////////////////////////////////////////////
// Import raw data file 
//////////////////////////////////////////////////////////

echo "\r\nImporting '$sourceName' taxonomy:\r\n";

// create empty import table
include "create_raw_data_tables.inc";

// import text files to raw data tables
include "import.inc";

// Create staging table
include "create_staging_table.inc";

// Add names & synonymy to staging table
include "add_names.inc";

//////////////////////////////////////////////////////////

?>
