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
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`,
	nomenclaturalStatusRemarks
	)
	SELECT
	nameID,
	parentNameID,
	IF(rank='family',fullName, TRIM(CONCAT_WS(' ',
		IFNULL(genus,''),
		IFNULL(specificEpithet,''),
		IFNULL(infraspecificRank,''),
		IFNULL(infraspecificEpithet,'')))),
	IF(infraspecificEpithet IS NOT NULL,infraspecificAuthors,IF(specificEpithet IS NOT NULL,speciesAuthors,genusAuthors)),
	rank,
	IF(
	nameID_char IS NULL, '$name_url_bad', CONCAT('$name_url_base',SUBSTRING(nameID_char,5))
	),
	GCCLSID,
	NULL,
	genus,
	specificEpithet,
	infraspecificRank,
	NULL,
	NULL,
	acceptedFamily,
	taxonomicStatus,
	acceptedNameID,
	nomenclaturalRemarks
	FROM `$tbl_names_raw`;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));



?>
