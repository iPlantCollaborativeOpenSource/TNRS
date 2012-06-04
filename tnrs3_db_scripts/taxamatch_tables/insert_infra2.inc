<?php

// Insert quadrinomials
echo "Inserting quadrinomials...";
$msg_error = "error!";
$sql = "INSERT INTO `infra2list`(
	infra2_id,
	infra1_orig,
	infra2,
	infra2_norank,
	rank,
	authority
	)
	SELECT 
	nameID,
	TRIM(CONCAT_WS(' ',genus,specificEpithet,IFNULL(rankIndicator,''),infraspecificEpithet)),
	TRIM(CONCAT_WS(' ',IFNULL(infraspecificRank2,''),infraspecificEpithet2)),
	infraspecificEpithet2,
	infraspecificRank2,
	scientificNameAuthorship
	FROM name
	WHERE nameParts=4 AND (isHybrid=0 OR isHybrid IS NULL)
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
