<?php

// Insert trinomials
echo "Inserting trinomials...";
$msg_error = "error!";
$sql = "INSERT INTO `infra1list`(
	infra1_id,
	species_orig,
	infra1,
	infra1_norank,
	rank,
	authority
	)
	SELECT
	nameID,
	CONCAT_WS(' ',genus,specificEpithet),
	CONCAT_WS(' ',IFNULL(rankIndicator,''),infraspecificEpithet),
	infraspecificEpithet,
	rankIndicator,
	scientificNameAuthorship
	FROM name
	WHERE nameParts=3 AND (isHybrid=0 OR isHybrid IS NULL)
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
