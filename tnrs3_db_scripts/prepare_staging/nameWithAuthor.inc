<?php
// populates nameWithAuthor field
// needed for joining to tnrs_results
// during loading to core db

echo "  Populating nameWithAuthor...";
$sql="
	UPDATE `$tbl_staging` 
	SET nameWithAuthor=TRIM(
		CONCAT_WS(
			' ',
			$name_fld,
			IFNULL($author_fld,'')
		)
	)
	;
";
$msg_error="failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
