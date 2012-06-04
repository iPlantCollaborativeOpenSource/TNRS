<?php
// For each name in source table, loads recursive FK to parent taxon
// plus right and left indices used for hierarchical searches
// Right and left indices are built using tree traversal
// of the staging table, prior to populating core db

echo "  Loading classification:\r\n";

// Load taxon-parentTaxon links and left/right indices
// into table `classification` for this source 
echo "    Loading classification for current source...";
$sql="
	INSERT INTO `classification`(
		sourceID,
		nameID,
		parentNameID,
		family,
		rightIndex,
		leftIndex
		)
		SELECT
		$sourceID,
		$fk_fld,
		$parent_fk_fld,
		IF(family IS NULL OR family='',NULL,family),
		$right_index_fld,
		$left_index_fld
		FROM `$tbl_staging`;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Populate family classification for each name, 
// according to this source 
echo "    Loading family classifications for current source...";
$sql="
	UPDATE classification AS n,
	(
		SELECT n.scientificName, c.leftIndex,c. rightIndex
		FROM name n JOIN classification c
		ON n.nameID=c.nameID
		WHERE c.sourceID=$sourceID
		AND n.nameRank='family'
	) AS f
	SET n.family=f.scientificName
	WHERE n.sourceID=$sourceID 
	AND n.family IS NULL
	AND f.leftIndex<=n.leftIndex AND f.rightIndex>=n.rightIndex;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

?>
