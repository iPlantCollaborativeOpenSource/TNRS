<?php

echo "  Altering table...";
$msg_error="Error!\r\n";
$sql="
	ALTER TABLE name 
	ADD COLUMN nameParts INTEGER(1) DEFAULT NULL AFTER nameRank,
	ADD INDEX name_nameParts(nameParts),
	ADD INDEX name_infraspecificEpithet(infraspecificEpithet),
	ADD INDEX name_rankIndicator(rankIndicator),
	ADD INDEX name_infraspecificEpithet2(infraspecificEpithet2),
	ADD INDEX name_infraspecificRank2(infraspecificRank2)
;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));


?>
