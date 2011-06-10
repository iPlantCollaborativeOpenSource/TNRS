<?php
// Populates taxamatch tables with names from TNRS core database
//
// Brad Boyle (bboyle@email.arizona.edu)
// 21 February 2011

include "params.inc";	// everything you need to set is here and dbconfig.inc

// Confirm with user before starting
if (confirm($confmsg)) {
	echo "\r\nBegin operation\r\n\r\n";
	include $timer_on;
}

// connect to database
if (!($dbh=db_connect($HOST,$USER,$PWD,$DB,$echo_on))) die();

include_once "create_tables.inc"; 		// Create empty mysql tables
include_once "insert_families.inc"; 		// Populate family table
include_once "insert_genera.inc";		// Populate genus table
include_once "link_genus_defaultfamily.inc"; 	// Populates default FAMILY_ID field in genus table
include_once "insert_species.inc";		// Populate species table
include_once "link_species_genus.inc"; 		// Populates GENUS_ID field in species table
include_once "insert_infra1.inc";		// Populate trinomial table
include_once "link_infra1_species.inc"; 	// Populates species_id field in infra1list table
include_once "insert_infra2.inc";		// Populate quadrinomial table
include_once "link_infra2_infra1.inc"; 		// Populates infra1_id field in infra2list table
include_once "delete_orphan_species.inc"; 	// Deletes species not linked to genus 
						// and makes table of orphan species
include_once "species_genus_combined.inc"; 	// Populates combined genus+species table
include_once "genus_family_combined.inc"; 	// Populates combined family+genus table 
						// and default family only
include_once "fg_alternate.inc"; 		// Loads alternative family+genus classifications
include_once "infra1_species_combined.inc"; 	// Populates combined trinomial + species table

include_once "infra2_infra1_combined.inc"; 	// Populates combined quadrinomial + trinomial table

mysql_close($dbh);
include_once $timer_off;
$msg = "\r\nTime elapsed: " . $tsecs . " seconds.\r\n"; 
$msg = $msg . "********* Operation completed " . $curr_time . " *********";
if  ($echo_on) echo $msg . "\r\n\r\n"; 

?>
