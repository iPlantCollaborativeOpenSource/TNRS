<?php
// Populates synonym table

$sourceID=1;

echo "  Populating table `synonym`...";
// Insert all synonym records for this source
// Inserts names with acceptance opinion, or acceptedNameID link, or both
// Only populates acceptance field; acceptedNameID populated in next step
$sql="
	INSERT INTO `synonym` (
	sourceID,
	nameID,
	acceptance
	)
	SELECT 
	$sourceID,
	$fk_fld,
	$acceptance_fld
	FROM `$tbl`
	WHERE $fk_fld IS NOT NULL AND ($accepted_id_fld IS NOT NULL OR $acceptance_fld IS NOT NULL);
";
$msg_error="Failed to insert into table `synonym`!";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Populate acceptedNameID
$sql="
	UPDATE `synonym` syn INNER JOIN `$tbl` s INNER JOIN `$tbl` s2
	ON syn.nameID=s.$fk_fld and s.$accepted_id_fld=s2.$id_fld
	SET 
	syn.acceptedNameID=s2.$fk_fld
	WHERE s2.$fk_fld IS NOT NULL;
";
$msg_error="Failed to update `acceptedNameID` in table `synonym`!";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
