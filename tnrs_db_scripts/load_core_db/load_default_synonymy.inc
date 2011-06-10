<?php
// Updates default synonymy fields in table `name`
// Will only run if this source is set as $default=TRUE
// in params.inc.
// Condition testing default status is in main script

echo "  Updating default synonymy in table `name`...";
$sql="
	UPDATE `name` AS n INNER JOIN `synonym` as s
	ON n.nameID=s.nameID
	SET
	n.$default_acceptance_fld=s.acceptance,
	n.$default_accepted_id_fld=s.acceptedNameID;
";
$msg_error="Failed!";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
