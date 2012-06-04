<?php

///////////////////////////////////////////////////////////////////////
// Purpose:
// Create mysql tables identical in structure to raw text file dump
// from USDA Plants
///////////////////////////////////////////////////////////////////////

// Create the empty table
echo "Creating table `$tbl_names_raw`...";

$msg_error="failed to drop previous table!";
$sql="DROP TABLE IF EXISTS `$tbl_names_raw`;";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

$msg_error="failed to create table!\r\n";

// The large number of indexes will slow down the initial insert slightly,
// but are needed to speed up the large number queries involving 
// this table. Basically anything involved in a join, a where clause
// of some other filtering criteria (such as a condition in a CONCAT)
// gets indexed
$sql="
CREATE TABLE `$tbl_names_raw` (
AcceptedSymbol VARCHAR(12) DEFAULT NULL,
SynonymSymbol VARCHAR(12) DEFAULT NULL,
ScientificName VARCHAR(250) DEFAULT NULL,
HybridGenusIndicator VARCHAR(5) DEFAULT NULL,
Genus VARCHAR(50) DEFAULT NULL,
HybridSpeciesIndicator VARCHAR(5) DEFAULT NULL,
Species VARCHAR(100) DEFAULT NULL,
SubspeciesPrefix VARCHAR(12) DEFAULT NULL,
HybridSubspeciesIndicator VARCHAR(5) DEFAULT NULL,
Subspecies VARCHAR(100) DEFAULT NULL,
VarietyPrefix VARCHAR(12) DEFAULT NULL,
HybridVarietyIndicator VARCHAR(5) DEFAULT NULL,
Variety VARCHAR(100) DEFAULT NULL,
SubvarietyPrefix VARCHAR(12) DEFAULT NULL,
Subvariety VARCHAR(100) DEFAULT NULL,
FormaPrefix VARCHAR(12) DEFAULT NULL,
Forma VARCHAR(100) DEFAULT NULL,
GeneraBinomialAuthor VARCHAR(150) DEFAULT NULL,
TrinomialAuthor VARCHAR(150) DEFAULT NULL,
QuadrinomialAuthor VARCHAR(150) DEFAULT NULL,
QuestionableTaxonIndicator VARCHAR(25) DEFAULT NULL,
Parents VARCHAR(150) DEFAULT NULL,
Genus2 VARCHAR(50) DEFAULT NULL,
Family VARCHAR(50) DEFAULT NULL,
FamilySymbol VARCHAR(12) DEFAULT NULL,
Kingdom VARCHAR(50) DEFAULT NULL,
INDEX (AcceptedSymbol),
INDEX (SynonymSymbol),
INDEX (ScientificName),
INDEX (Family),
INDEX (Genus),
INDEX (Species),
INDEX (Subspecies),
INDEX (Variety),
INDEX (Subvariety),
INDEX (Forma),
INDEX (GeneraBinomialAuthor),
INDEX (TrinomialAuthor),
INDEX (QuadrinomialAuthor),
INDEX (HybridGenusIndicator),
INDEX (HybridSpeciesIndicator),
INDEX (HybridSubspeciesIndicator),
INDEX (HybridVarietyIndicator)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
?>

