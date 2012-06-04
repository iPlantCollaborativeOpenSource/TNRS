<?php
////////////////////////////////////////////////////////////////
// Purpose:
// Backs up completed staging table. This prevents loss of 
// staging table for current source when new source is loaded
// Backup database and backup staging table name are set in 
// global_params.inc
////////////////////////////////////////////////////////////////

//$tbl_staging_bak=$tbl_staging."_".$sourceName."_".date('Y-m-d'); // Backup staging table name
$tbl_staging_bak=$tbl_staging."_".$sourceName; // Backup staging table name
								// Includes name of taxonomic source
echo "  Backing up staging table to `$DB_BACKUP`.`$tbl_staging_bak`...";
$sql="
DROP TABLE IF EXISTS `$DB_BACKUP`.`$tbl_staging_bak`;
";
$msg_error="failed to delete previous table `$DB_BACKUP`.`$tbl_staging_bak`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

$sql="
CREATE TABLE `$DB_BACKUP`.`$tbl_staging_bak` LIKE `$DB`.`$tbl_staging`;
";
$msg_error="failed to create table `$DB_BACKUP`.`$tbl_staging_bak`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

$sql="
INSERT INTO `$DB_BACKUP`.`$tbl_staging_bak` SELECT * FROM `$DB`.`$tbl_staging`
";
$msg_error="failed to insert into table `$DB_BACKUP`.`$tbl_staging_bak`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
