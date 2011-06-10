<?php

// Insert trinomials
echo "Inserting trinomials...";
$msg_error = "error!";
$sql = "INSERT INTO `$tbl_infra1`(
	infra1_id,
	species_orig,
	infra1,
	infra1_norank,
	rank,
	authority
	)
	SELECT
	nameID,
	CONCAT_WS(' ',IFNULL(genus,''),IFNULL(specificEpithet,'')),
	scientificName,
	CONCAT_WS(' ',IFNULL(genus,''),IFNULL(specificEpithet,''),IFNULL(infraspecificEpithet,'')),
	rankIndicator,
	scientificNameAuthorship
	FROM name
	WHERE nameParts=3 AND (isHybrid=0 OR isHybrid IS NULL)
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
