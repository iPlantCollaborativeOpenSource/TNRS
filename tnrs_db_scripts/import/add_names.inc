<?php
// Insert names from raw import table into staging table

echo "Adding names to staging table `$tbl`...";
$msg_error = "Failed!";
$sql="
	INSERT INTO `$tbl` (
	`$id_fld`,
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT
	`$id_fld`,
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	FROM `$tbl_names_raw`;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
