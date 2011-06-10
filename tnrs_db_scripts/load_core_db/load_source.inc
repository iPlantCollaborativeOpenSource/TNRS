<?php
// Checks if source for names being added is already in table `source`; if not, adds new record

if ($echo_on) echo "  Updating source information...";
$sql="SELECT COUNT(*) AS sources FROM `source` WHERE `sourceName`='$sourceName' OR `sourceUrl`='$sourceUrl';";
$records=sql_get_first_result($sql,'sources');
If ($records===false) {
	echo "ERROR: Offending SQL:\r\n$sql\r\n";
	echo "Records=".$records."\r\n";
	die("failed query on table `source`");
}

If ($records==0) {
	// Add entry
	$sql_source="INSERT INTO `source`(
		`sourceName`,
		`sourceUrl`
		)
		SELECT
		'$sourceName',
		'$sourceUrl'
		;";
	$msg_error="Failed to insert new record for sourceName='Tropicos' in table `source`!\r\n";
	if (sql_execute($sql_source,TRUE,$echo_on,"",$msg_error));
} elseif ($records>1) {
	// > 1 entry for this source; issue warning and quit
	die("Warning: >1 entry for `sourceName`='$sourceName' in table `source`");
}

// Get ID for this source
$sql="SELECT sourceID FROM `source` WHERE `sourceName`='$sourceName' OR `sourceUrl`='$sourceUrl'";
$sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve 'sourceID' from table `source`");
echo "$msg_success";
?>
