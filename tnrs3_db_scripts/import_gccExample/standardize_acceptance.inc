<?php

/////////////////////////////////////////////////////////////////
// Purpose:
// Fixes value "Unplaced" in column `acceptance`. "Unplaced"
// has nothing to do with taxonomic acceptance, but means only
// that the name has not been placed at the tribe level.
// The value should be set to either "Accepted", "Synonym"
// or NULL, depending on the value in the acceptedNameID column
// Note: currently, allowed values for `acceptance` are:
// Accepted, Synonymy, Invalid, Illegitimate, or NULL
/////////////////////////////////////////////////////////////////

echo "Standardizing column `acceptance`...";
$sql="
	UPDATE `$tbl_names_raw`
	SET taxonomicStatus='Synonym'
	WHERE taxonomicStatus='Unplaced' 
	      AND (acceptedNameID_char IS NOT NULL AND acceptedNameID_char<>'')
              AND acceptedNameID_char<>nameID_char; 
";
$msg_error="failed to set value='Synonym'!";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

$sql="
	UPDATE `$tbl_names_raw`
	SET taxonomicStatus='Accepted'
	WHERE taxonomicStatus='Unplaced' 
	      AND (acceptedNameID_char IS NOT NULL AND acceptedNameID_char<>'')
              AND acceptedNameID_char=nameID_char; 
";
$msg_error="failed to set value='Accepted'!";
if (sql_execute($sql,$die_on_fail,$echo_on,'',$msg_error));

$sql="
	UPDATE `$tbl_names_raw`
	SET taxonomicStatus=NULL
	WHERE taxonomicStatus='Unplaced' 
              AND (acceptedNameID_char IS NULL OR acceptedNameID_char='');
";
$msg_error="failed to set value=NULL!";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
