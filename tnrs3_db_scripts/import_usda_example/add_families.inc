<?php

/////////////////////////////////////////////////////////////////
// Purpose:
// Extracts and adds family names
/////////////////////////////////////////////////////////////////

echo "Extracting family names...";
$sql="
	INSERT INTO `$tbl_names_raw` (
	ScientificName,
	parentName,
	acceptance,
	Family
	)
	SELECT DISTINCT 
	Family,
	'Plantae',
	'Accepted',
	Family
	FROM `$tbl_names_raw`
	WHERE Family IS NOT NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));


?>
