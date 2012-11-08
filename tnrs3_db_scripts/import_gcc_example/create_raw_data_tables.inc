<?php

///////////////////////////////////////////////////////////////////////
// Purpose:
// Create mysql tables identical in structure to raw text file dump
// of source "Global Compositae Checklist"
///////////////////////////////////////////////////////////////////////

/*
// Clean up quirk of the GCC dump
// NULL is inconsistently sprinkled throughout file
echo "Removing NULLs in text file...";
$cmd="sed -n 's/NULL//g' $namesfile";
system($cmd);
$cmd="sed -n 's/ N U L L //g' $namesfile";
system($cmd);
$cmd="sed -n 's/N U L L//g' $namesfile";
system($cmd);
echo $msg_success;
*/

// Create the empty table
echo "Creating table `$tbl_names_raw`...";

$msg_error="failed to drop previous table!";
$sql="DROP TABLE IF EXISTS `$tbl_names_raw`;";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

$msg_error="failed to create table!\r\n";
$sql="
CREATE TABLE `$tbl_names_raw` (
nameID_char VARCHAR(250) DEFAULT NULL,
GCCLSID VARCHAR(250) DEFAULT NULL,
ConceptLSID VARCHAR(250) DEFAULT NULL,
acceptedFamily VARCHAR(50) DEFAULT NULL,
family VARCHAR(50) DEFAULT NULL,
fullName VARCHAR(250) DEFAULT NULL,
rank VARCHAR(25) DEFAULT NULL,
genusHybridMarker VARCHAR(25) DEFAULT NULL,
genus VARCHAR(50) DEFAULT NULL,
genusBasionymAuthors VARCHAR(150) DEFAULT NULL,
genusAuthors VARCHAR(150) DEFAULT NULL,
speciesHybridMarker VARCHAR(25) DEFAULT NULL,
specificEpithet VARCHAR(100) DEFAULT NULL,
speciesBasAuthors VARCHAR(150) DEFAULT NULL,
speciesAuthors VARCHAR(150) DEFAULT NULL,
infraspecificRank VARCHAR(25) DEFAULT NULL,
infraspecificEpithet VARCHAR(100) DEFAULT NULL,
infraspecificBasAuthors VARCHAR(150) DEFAULT NULL,
infraspecificAuthors VARCHAR(150) DEFAULT NULL,
basionymAuthors VARCHAR(150) DEFAULT NULL,
primaryAuthors VARCHAR(150) DEFAULT NULL,
publication VARCHAR(250) DEFAULT NULL,
collation VARCHAR(250) DEFAULT NULL,
page VARCHAR(25) DEFAULT NULL,
date VARCHAR(25) DEFAULT NULL,
reference VARCHAR(250) DEFAULT NULL,
basionymID VARCHAR(250) DEFAULT NULL,
replacedSynonymID VARCHAR(250) DEFAULT NULL,
nomenclaturalRemarks VARCHAR(250) DEFAULT NULL,
taxonomicStatus VARCHAR(25) DEFAULT NULL,
acceptedNameID_char VARCHAR(250) DEFAULT NULL,
acceptedName VARCHAR(250) DEFAULT NULL,
parentNameID_char VARCHAR(250) DEFAULT NULL,
source VARCHAR(250) DEFAULT NULL,
originalID VARCHAR(250) DEFAULT NULL,
lastModifiedInSourceAvailable VARCHAR(12) DEFAULT NULL,
lastModifiedInSource VARCHAR(250) DEFAULT NULL,
dateExported VARCHAR(25) DEFAULT NULL,
ipniID VARCHAR(250) DEFAULT NULL,
IPNIContributions VARCHAR(250) DEFAULT NULL,
GCCDataProviders VARCHAR(250) DEFAULT NULL,
notes VARCHAR(250) DEFAULT NULL,
INDEX (nameID_char),
INDEX (acceptedNameID_char),
INDEX (parentNameID_char),
INDEX (rank),
INDEX (fullName),
INDEX (acceptedFamily),
INDEX (genus),
INDEX (specificEpithet),
INDEX (infraspecificRank),
INDEX (infraspecificEpithet)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
?>

