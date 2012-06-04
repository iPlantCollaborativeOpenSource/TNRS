<?php

//////////////////////////////////////////////////////////
// Removes unneeded or temporary tables after database
// rebuild is complete. Some are moved to backup db, 
// others are dropped altogether
//////////////////////////////////////////////////////////

echo "Cleaning up...";

// Drop raw ncbi tables
// Hard-wired because I'm lazy
// Delete if not applicable
$sql="
	DROP TABLE IF EXISTS names_parsed;
	DROP TABLE IF EXISTS ncbi_citations;
	DROP TABLE IF EXISTS ncbi_delnodes;
	DROP TABLE IF EXISTS ncbi_division;
	DROP TABLE IF EXISTS ncbi_gencode;
	DROP TABLE IF EXISTS ncbi_merged;
	DROP TABLE IF EXISTS ncbi_names;
	DROP TABLE IF EXISTS ncbi_nodes;
";
sql_execute_multiple($sql);

// Backup or drop remaining raw data tables
if ($use_db_backup && isset($DB_BACKUP)) {
	// move raw data tables (except for ncbi) to backup db
	
	foreach ($src_array as $src) {
		
		if ($src<>'ncbi') {
			$sql="
				DROP TABLE IF EXISTS ".$DB_BACKUP.".".$src."_raw;
				CREATE TABLE ".$DB_BACKUP.".".$src."_raw LIKE ".$src."_raw;
				INSERT INTO ".$DB_BACKUP.".".$src."_raw SELECT * FROM ".$src."_raw;
				DROP TABLE ".$src."_raw;
			";
			sql_execute_multiple($sql);
		}
		
	}
	
} else {
	// Just drop them
	
	foreach ($src_array as $src) {
		$sql="
			DROP TABLE IF EXISTS ".$src."_raw;
		";
		sql_execute_multiple($sql);
	}
	
}

// drop last version of staging table
$sql="
	DROP TABLE IF EXISTS nameStaging;
";
sql_execute_multiple($sql);

// Get rid of extra taxamatch tables if you didnt delete them earlier
$sql="
	DROP TABLE IF EXISTS `infra1list`;
	DROP TABLE IF EXISTS `infra2list`;
	DROP TABLE IF EXISTS `splist`;
";
sql_execute_multiple($sql);

// DROP "forward-compatible" tables currently not used
$sql="
	DROP TABLE IF EXISTS fuzzyMatch;
	DROP TABLE IF EXISTS name_lexicalGroup;
	DROP TABLE IF EXISTS lexicalGroup;
";
sql_execute_multiple($sql);

// Drop error report table
// Currently this is source-specific, and pertains to the 
// last source loaded. Consider saving a separate version
// for each source in backup db. Some day...
$sql="
	DROP TABLE IF EXISTS error_table;
";
sql_execute_multiple($sql);

echo "done\r\n\r\n";

?>
