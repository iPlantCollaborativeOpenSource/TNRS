<?php

// Insert quadrinomials
echo "Inserting quadrinomials...";
$msg_error = "error!";
$sql = "INSERT INTO `$tbl_infra2`(
	infra2_id,
	infra1_orig,
	infra2,
	infra2_norank,
	rank,
	authority
	)
	SELECT
	nameID,
	CONCAT_WS(' ',IFNULL(genus,''),IFNULL(specificEpithet,''),IFNULL(rankIndicator,''),IFNULL(infraspecificEpithet,'')),
	scientificName,
	CONCAT_WS(' ',IFNULL(genus,''),IFNULL(specificEpithet,''),IFNULL(infraspecificEpithet,''),IFNULL(infraspecificEpithet2,'')),
	infraspecificRank2,
	scientificNameAuthorship
	FROM name
	WHERE nameParts=4 AND (isHybrid=0 OR isHybrid IS NULL)
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
