<?php
// Checks if source for names being added is already in table `source`; if not, adds new record
// Also determines source for default synonymy and classification

if ($echo_on) echo "  Updating source information...";
$sql="SELECT COUNT(*) AS sources FROM `source` WHERE `sourceName`='$sourceName';";
$records=sql_get_first_result($sql,'sources');
If ($records===false) {
	echo "ERROR: Offending SQL:\r\n$sql\r\n";
	echo "Records=".$records."\r\n";
	die("failed query on table `source`");
}

// Get source parameters, set in params.inc for this source
$isHigherClassification=($is_higher_classification===true?1:0);
$isDefault=($is_default===true?1:0);
	
if ($records>1) {
	// > 1 entry for this source; issue warning and quit
	die("Warning: >1 entry for `sourceName`='$sourceName' in table `source`");
} else {	

	if ($isDefault==1) {		
		// Can only have one default source. Since source being added 
		// will be default, reset isDefault=zero for all sources
		$sql_reset="UPDATE source SET isDefault=0;";
		$msg_error="Failed to reset isDefault in table `source`!\r\n";
		if (sql_execute($sql_reset,TRUE,$echo_on,"",$msg_error));
	}
		
	if ($records==0) {		
		// Add new record for this source
		$sql_source="INSERT INTO `source`(
			sourceName,
			sourceNameFull,
			sourceUrl,
			dateAccessed,
			isDefault,
			isHigherClassification,
			warning
			)
			SELECT
			'$sourceName',
			'$sourceNameFull',
			'$sourceUrl',
			'$dateAccessed',
			$isDefault,
			$isHigherClassification,
			$source_warning
			;";
		$msg_error="Failed to insert new record for sourceName='$sourceName' in table `source`!\r\n";
		if (sql_execute($sql_source,TRUE,$echo_on,"",$msg_error));
	} else {
		// update existing record for this source
		$sql="
			UPDATE source
			SET
			sourceNameFull='$sourceNameFull',
			sourceUrl='$sourceUrl',
			dateAccessed='$dateAccessed',
			isDefault=$isDefault,
			isHigherClassification=$isHigherClassification,
			warning=$source_warning
			WHERE `sourceName`='$sourceName';
		";
	}
}
echo $msg_success;

// Get ID for current source
$sql="SELECT sourceID FROM `source` WHERE `sourceName`='$sourceName'";
$sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve 'sourceID' from table `source`");
echo "    Currently loading source='$sourceName' (sourceID=$sourceID)\r\n";

// Get default source ID and name
// Needed for loading taxamatch tables

// Get sourceID
$sql="SELECT sourceID FROM source WHERE isDefault=1;";
$default_sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve default sourceID from table `source`");

// Get sourceName
$sql="SELECT sourceName FROM source WHERE isDefault=1;";
$default_source_name=sql_get_first_result($sql,'sourceName');
if ($sourceID===false or $sourceID==NULL) echo "WARNING: failed retrieve default source name from table `source`";

// Display result
if ($echo_on) echo "    Default taxonomic source='$default_source_name' (sourceID=$default_sourceID)\r\n";


?>
