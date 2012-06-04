<?php
// Create mysql tables identical in structure to raw text file
// This versions assume single file containing both names and synonymy

echo "  Creating table `$tbl_names_raw`...";

$msg_error="failed to drop previous table!";
$sql="DROP TABLE IF EXISTS `$tbl_names_raw`;";
if (sql_execute($sql,$die_on_fail,false,"",$msg_error));

$msg_error="failed to create table!\r\n";
$sql="
CREATE TABLE `$tbl_names_raw` (
`$id_fld` INTEGER(11) UNSIGNED NOT NULL,
`$parent_id_fld`  INTEGER(11) UNSIGNED DEFAULT NULL,
`$name_fld` VARCHAR(250) NOT NULL,
`$author_fld` VARCHAR(100) DEFAULT NULL,
`$rank_fld` VARCHAR(50) DEFAULT NULL,
`$source_url_fld` VARCHAR(250) DEFAULT NULL,
`$is_hybrid` INTEGER(1) UNSIGNED NOT NULL,
`$genus_fld` VARCHAR(100) DEFAULT NULL,
`$specific_epithet_fld` VARCHAR(100) DEFAULT NULL,
`$rank_ind_fld` VARCHAR(50) DEFAULT NULL,
`$otherEpithet_fld` VARCHAR(100) DEFAULT NULL,
`$subclass_fld` VARCHAR(50) DEFAULT NULL,
`$family_fld` VARCHAR(50) DEFAULT NULL,
`$acceptance_fld` VARCHAR(1) DEFAULT NULL,
`$accepted_id_fld`  INTEGER(11) UNSIGNED DEFAULT NULL,
PRIMARY KEY(`$id_fld`),
INDEX ".$tbl."_$parent_id_fld($parent_id_fld),
INDEX ".$tbl."_$name_fld($name_fld),
INDEX ".$tbl."_$author_fld($author_fld),
INDEX ".$tbl."_$rank_fld($rank_fld),
INDEX ".$tbl."_$source_url_fld($source_url_fld),
INDEX ".$tbl."_$is_hybrid($is_hybrid),
INDEX ".$tbl."_$genus_fld($genus_fld),
INDEX ".$tbl."_$specific_epithet_fld($specific_epithet_fld),
INDEX ".$tbl."_$otherEpithet_fld($otherEpithet_fld),
INDEX ".$tbl."_$family_fld($family_fld),
INDEX ".$tbl."_$accepted_id_fld($accepted_id_fld),
INDEX ".$tbl."_$acceptance_fld($acceptance_fld)
)  ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));
	
?>

