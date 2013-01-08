<?php

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
// Purpose:
//
// (1) Joins all names in database to higher taxa in
// every taxonomy source flagged as higher classification.
// (2) Creates denormalized column in table name of
// higher taxa (as set in global_params.inc) according
// to each higher classification
//
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

echo "Building combined classifications for all sources:\r\n\r\n";

// If >1 source, build merged classifications
$sql="SELECT sourceID FROM source;";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
If ($num_rows>1){

	//////////////////////////////////////////////////////////////////
	// For each taxonomic source in database flagged as 
	// higher classification (source.isHigherClassification=1)
	// add species and infraspecific taxa from all other sources,
	// joining by genus, or failing that, by family
	// For each new name, populates denormalized column `family`
	// with family according to classification source. Finally,
	// rebuilds left and right indices for all names 
	//////////////////////////////////////////////////////////////////
	include_once "build_classifications.inc";
	// The following is the hack version 
	// which populates family fields only
	// without rebuilding classifications
	// Temporary!
	//include_once "build_classifications_hack.inc";


}



?>
