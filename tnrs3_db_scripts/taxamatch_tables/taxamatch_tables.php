<?php

////////////////////////////////////////////////////////////////////////////////////
// Purpose:
// Create tables used by TAXAMATCH & populates with names from TNRS core database
// This is the single master scripts; calls all others as includes (in this
// directory. Also requires global_params.inc and any functions or utilities
// called by the latter file. Assumes TNRS core database has been completely
// loaded with names and synonymy from all sources.
//
// By: Brad Boyle (bboyle@email.arizona.edu) & Jerry Lu <luj@cshl.edu>
// 21 February 2011
////////////////////////////////////////////////////////////////////////////////////

echo "Building taxamatch tables:\r\n\r\n";

// reconnect to database
$msg_err_db_connect="Failed to reconnect to database!\r\n\r\n";
if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die($msg_err_db_connect);

include "create_tables.inc"; 				// Create empty mysql tables
include "insert_families.inc"; 				// Families
include "insert_genera.inc";				// Genera 

// The next three tables are used only to build the combined tables:
include "insert_species.inc";				// species
include "insert_infra1.inc";				// trinomials
include "insert_infra2.inc";				// quadrinomials

// combination tables
include "genus_family_combined.inc"; 		// Combined family+genus table 
include "species_genus_combined.inc"; 		// Populates combined genus+species table
include "infra1_species_combined.inc"; 		// Populates combined trinomial + species table
include "infra2_infra1_combined.inc"; 		// Populates combined quadrinomial + trinomial table

// The following scripts by Jerry Lu complete populating taxamatch tables
include "update_genera.inc";
include "update_family.inc";
include "update_genus_family_combined.inc";
include "update_species.inc";
include "update_infra1.inc";
include "update_infra2.inc";
include "update_species_genus_combined.inc";
include "update_infra1_species_combined.inc";
include "update_infra2_infra1_combined.inc";

// Remove splist, infra1list, infra2list
echo "Removing temporary tables...";
$sql="
	DROP TABLE IF EXISTS `infra1list`;
	DROP TABLE IF EXISTS `infra2list`;
	DROP TABLE IF EXISTS `splist`;
";
sql_execute_multiple($sql);
echo $msg_success;

?>
