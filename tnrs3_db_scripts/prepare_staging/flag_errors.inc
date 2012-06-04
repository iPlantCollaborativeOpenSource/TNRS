<?php
// Update error table with:
// (1) names with invalid parentNameID links
// (2) synonymys with invalids nameIDs or acceptedNameIDs

echo "  Checking for errors:\r\n";

// Flag names with orphan parent nameID
echo "    Orphan parentNameIDs...";
$msg_error = "Failed!";
$sql="
	UPDATE `$error_tbl` e INNER JOIN (
		SELECT n.`$id_fld`
		FROM `$tbl_staging` p RIGHT JOIN `$tbl_staging` n
		ON p.`$id_fld`=n.`$parent_id_fld`
		WHERE p.`$id_fld` IS NULL AND n.`$parent_id_fld` IS NOT NULL AND n.`$parent_id_fld`<>0
	) AS n
	ON e.`$id_fld`=n.`$id_fld`
	SET
	e.`$has_errors`=1,
	e.`$is_orphan_parentNameID`=1;

";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Flag names with orphan parent nameID
echo "    Missing parentNameIDs...";
$msg_error = "Failed!";
$sql="
	UPDATE `$error_tbl` e INNER JOIN `$tbl_staging` n
	ON e.`$id_fld`=n.`$id_fld`
	SET
	e.`$has_errors`=1,
	e.`$is_null_parentNameID`=1
	WHERE n.$parent_id_fld IS NULL or n.$parent_id_fld=0
	;

";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Flag synonyms with good nameID but orphan accepted nameID
echo "    Orphan acceptedNameIDs...";
$msg_error = "Failed!";
$sql="
	UPDATE `$error_tbl` e INNER JOIN (
		SELECT s.`$id_fld`
		FROM `$tbl_staging` n RIGHT JOIN `$tbl_staging` s
		ON n.`$id_fld`=s.`$accepted_id_fld`
		WHERE n.`$id_fld` IS NULL AND s.`$accepted_id_fld` IS NOT NULL AND s.`$accepted_id_fld`<>0
	) AS s
	ON e.`$id_fld`=s.`$id_fld`
	SET
	e.`$has_errors`=1,
	e.`$is_orphan_acceptedNameID`=1;

";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
