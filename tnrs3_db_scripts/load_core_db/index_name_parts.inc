<?php
// Detect & indexes uninomials, binomials, trinomials, quadrinomials
// Important index for operations that process these name separately

echo "  Indexing name parts...";
$msg_error="Failed to set `name`.`namePart`=NULL!\r\n";
$sql="
	UPDATE name SET nameParts=NULL;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                  ";
echo "\r  Indexing name parts...uninomials";
$msg_error="Failed to index uninomials!\r\n";
$sql="
	UPDATE name
	SET nameParts=1
	WHERE scientificName NOT LIKE '% %';
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                  ";
echo "\r  Indexing name parts...binomials...";
$msg_error="failed to index binomials!\r\n";
$sql="
	UPDATE name
	SET nameParts=2
	WHERE nameParts IS NULL 
	AND ( (scientificName LIKE '% %' AND scientificName NOT LIKE '% % %') OR nameRank='species')
	;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                  ";
echo "\r  Indexing name parts...trinomials...";
$msg_error="failed to index trinomials!\r\n";
$sql="
	UPDATE name
	SET nameParts=3
	WHERE nameParts IS NULL 
	AND nameRank<>'species'
	AND (infraspecificEpithet IS NOT NULL AND infraspecificEpithet<>'') 
	AND (infraspecificEpithet2 IS NULL OR infraspecificEpithet2='')
	;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                  ";
echo "\r  Indexing name parts...quadrinomials...";
$msg_error="failed to index quadrinomials!\r\n";
$sql="
	UPDATE name
	SET nameParts=4
	WHERE nameParts IS NULL 
	AND nameRank<>'species'
	AND (infraspecificEpithet IS NOT NULL AND infraspecificEpithet<>'') 
	AND (infraspecificEpithet2 IS NOT NULL AND infraspecificEpithet2<>'')
	;
";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

echo "\r                                                  ";
echo "\r  Indexing name parts...done\r\n";

?>
