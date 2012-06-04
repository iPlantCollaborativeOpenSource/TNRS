<?php
// converts any encoded hybrid "x" characters to a 
// simple plain ascii "x"
// Safer!

$encoded_x="×";
$plain_x="x";

echo "  Standarizing hybrid 'x'...";
$sql="
	UPDATE `$tbl_staging` 
	SET `$name_fld`=REPLACE(`$name_fld`,'$encoded_x','$plain_x')
	;
";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));


?>
