<?php
// Load source-specific attributes for each name
// to table 'name_source'
// These include external urls and IDs (PKs) of source db

echo "  Loading name sources...";

$sql="
	INSERT INTO `name_source`(
		nameID,
		sourceID,
		nameSourceUrl,
		lsid,
		nameSourceOriginalID,
		dateAccessed
		)
		SELECT
		n.nameID,
		$sourceID,
		t.$source_url_fld,
		t.lsid,
		t.$id_fld,
		'$dateAccessed'
		FROM `$tbl_staging` AS t INNER JOIN `name` AS n
		ON t.$fk_fld=n.nameID;
";
$msg_error="Failed to insert name sources!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
