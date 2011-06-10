<?php
// Load default classification for ranked taxa
// if this source is to be used as default
// Condition which checks for default classification
// is in main script

echo "  Loading default classifications...";

// Update default classification information for this source
// Will insert default accepted family for all taxa below level of family
// For families need additional step, see below

// Clear previous defaults
$sql="
	UPDATE `name`
	SET
	$default_subclass_fld=NULL,
	$default_family_fld=NULL
	;
";
$msg_error="Failed to clear previous default classifications!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

$sql="
	UPDATE `name` AS d INNER JOIN `$tbl` AS t
	ON d.nameID=t.$fk_fld
	SET
	d.$default_subclass_fld=t.$subclass_fld,
	d.$default_family_fld=t.$family_fld
	;
";
$msg_error="Failed to update default classifications!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Updates accepted family name for synonym families 
$sql="
	UPDATE `name` AS n INNER JOIN `synonym` AS s INNER JOIN `name` as n2
	ON n.nameID=s.nameID AND s.acceptedNameID=n2.nameID
	SET
	n.$default_family_fld=n.scientificName
	WHERE
	n.nameRank='family' and n2.nameRank='family'
	;
";
$msg_error="Failed to update accepted family!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
