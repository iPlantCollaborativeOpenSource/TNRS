<?php
// For each name in source table, loads recursive FK to parent taxon
// plus right and left indices used for hierarchical searches
// Right and left indices are built using tree traversal
// of the staging table, prior to populating core db

echo "  Loading classification...";

$sql="
	INSERT INTO `classification`(
		sourceID,
		nameID,
		parentNameID,
		rightIndex,
		leftIndex
		)
		SELECT
		$sourceID,
		$fk_fld,
		$parent_fk_fld,
		$right_index_fld,
		$left_index_fld
		FROM `$tbl`;
";
$msg_error="Failed to load classification!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));



?>
