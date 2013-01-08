<?php

/////////////////////////////////////////////////////////////////
// Purpose:
// Updates integer acceptedNameID and parentNameID 
// in raw names table
/////////////////////////////////////////////////////////////////

echo "Updating acceptedNameID...";
$sql="
	UPDATE `$tbl_names_raw` syn JOIN  `$tbl_names_raw` acc
	ON syn.acceptedNameID_char=acc.nameID_char
	SET syn.acceptedNameID=acc.nameID
	WHERE syn.acceptedNameID_char<>syn.nameID_char;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

echo "Updating parentNameID...";
$sql="
	UPDATE `$tbl_names_raw` child JOIN  `$tbl_names_raw` parent
	ON child.parentNameID_char=parent.nameID_char
	SET child.parentNameID=parent.nameID;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// The remaining steps extract family from the column "family"
// and add it to the list of names, and link any orphan genera to the
// appropriate family
// Can only do this if you are sure family names are not already
// in the classification. Also may get weird results if taxa above
// rank of genus are included
echo "Extracting families...";
$sql="
	INSERT INTO `$tbl_names_raw` (
	family,
	scientificName,
	rank,
	taxonomicStatus
	)
	SELECT DISTINCT
	family,
	family,
	'family',
	'Accepted'
	FROM `$tbl_names_raw`
	WHERE family IS NOT NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

echo "Linking orphan genera to families...";

$sql="
	UPDATE `$tbl_names_raw` g JOIN `$tbl_names_raw` f
	ON g.family=f.scientificName
	SET g.parentNameID=f.nameID
	WHERE g.rank='genus' 
		AND f.rank='family' 
		AND (g.parentNameID IS NULL OR g.parentNameID=g.nameID) ;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

?>
