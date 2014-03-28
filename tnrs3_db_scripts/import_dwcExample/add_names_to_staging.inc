<?php
// Insert names from raw import table into staging table

echo "Adding names to staging table `$tbl_staging`...";
$msg_error = "Failed!";
$sql="
	INSERT INTO `$tbl_staging` (
	`$id_fld`,
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$lsid_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	infraspecificEpithet,
	rankIndicator2,
	infraspecificEpithet2,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT
	nameID,
	parentNameID,
	scientificName,
	scientificNameAuthorship,
	rank,
	taxonUri,
	lsid,
	NULL,
	genus,
	specificEpithet,
	infraspecificRank,
	infraspecificEpithet,
	infraspecificRank2,
	infraspecificEpithet2,
	family,
	taxonomicStatus,
	acceptedNameID
	FROM `$tbl_names_raw`;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));



?>
