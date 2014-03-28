<?php
// Create statement for generic staging table
// Called from inside import script specific to each source

echo "\r\nCreating staging table `$tbl_staging`...";

$msg_error="failed to drop previous staging table!";
$sql_drop="DROP TABLE IF EXISTS `$tbl_staging`;";
if (sql_execute($sql_drop,$die_on_fail,$echo_on,"",$msg_error));

$msg_error="failed to create table `$tbl_staging`!";
$sql_create="
CREATE TABLE `$tbl_staging` (
`$id_fld` INTEGER(11) UNSIGNED NOT NULL,
`$parent_id_fld` INTEGER(11) UNSIGNED DEFAULT NULL,
`$name_fld` VARCHAR(250) NOT NULL,
`$author_fld` VARCHAR(100) DEFAULT NULL,
`nameWithAuthor` VARCHAR(255) DEFAULT NULL,
`$rank_fld` VARCHAR(50) DEFAULT NULL,
`rankGroup` VARCHAR(50) DEFAULT NULL,
`$source_url_fld` VARCHAR(250) DEFAULT NULL,
`$lsid_fld` VARCHAR(500) DEFAULT NULL,
`$is_hybrid` INTEGER(1) UNSIGNED NOT NULL,
`$genus_fld` VARCHAR(100) DEFAULT NULL,
`$specific_epithet_fld` VARCHAR(100) DEFAULT NULL,
`$rank_ind_fld` VARCHAR(12) DEFAULT NULL,
`$infraepithet_fld` VARCHAR(100) DEFAULT NULL,
`$rank_ind_fld2` VARCHAR(12) DEFAULT NULL,
`$infraepithet_fld2` VARCHAR(100) DEFAULT NULL,
`$otherEpithet_fld` VARCHAR(100) DEFAULT NULL,
`$subclass_fld` VARCHAR(100) DEFAULT NULL,
`$family_fld` VARCHAR(100) DEFAULT NULL,
`$fk_fld` INTEGER UNSIGNED DEFAULT NULL,
`$parent_fk_fld` INTEGER UNSIGNED DEFAULT NULL,
`$left_index_fld` INTEGER(11) UNSIGNED DEFAULT NULL,
`$right_index_fld` INTEGER(11) UNSIGNED DEFAULT NULL,
`$acceptance_fld` VARCHAR(50) DEFAULT NULL,
`$accepted_id_fld` INTEGER(11) UNSIGNED DEFAULT NULL,
`nomenclaturalStatusRemarks` VARCHAR(100) DEFAULT NULL,
`parent` VARCHAR(250) NOT NULL,
`grandparent` VARCHAR(250) NOT NULL,
PRIMARY KEY(`$id_fld`),
INDEX `".$tbl."_$parent_id_fld`(`$parent_id_fld`),
INDEX `".$tbl."_$name_fld`(`$name_fld`),
INDEX `".$tbl."_$author_fld`(`$author_fld`),
INDEX `".$tbl."_nameWithAuthor`(`nameWithAuthor`),
INDEX `".$tbl."_$rank_fld`(`$rank_fld`),
INDEX `".$tbl."_rankGroup`(`rankGroup`),
INDEX `".$tbl."_$source_url_fld`(`$source_url_fld`),
INDEX `".$tbl."_$is_hybrid`(`$is_hybrid`),
INDEX `".$tbl."_$genus_fld`(`$genus_fld`),
INDEX `".$tbl."_$specific_epithet_fld`(`$specific_epithet_fld`),
INDEX `".$tbl."_$rank_ind_fld`(`$rank_ind_fld`),
INDEX `".$tbl."_$infraepithet_fld`(`$infraepithet_fld`),
INDEX `".$tbl."_$infraepithet_fld2`(`$infraepithet_fld2`),
INDEX `".$tbl."_$rank_ind_fld2`(`$rank_ind_fld2`),
INDEX `".$tbl."_$otherEpithet_fld`(`$otherEpithet_fld`),
INDEX `".$tbl."_$subclass_fld`(`$subclass_fld`),
INDEX `".$tbl."_$family_fld`(`$family_fld`),
INDEX `".$tbl."_$fk_fld`(`$fk_fld`),
INDEX `".$tbl."_$parent_fk_fld`(`$parent_fk_fld`),
INDEX `".$tbl."_$left_index_fld`(`$left_index_fld`),
INDEX `".$tbl."_$right_index_fld`(`$right_index_fld`),
INDEX `".$tbl."_$acceptance_fld`(`$acceptance_fld`),
INDEX `".$tbl."_$accepted_id_fld`(`$accepted_id_fld`),
INDEX `".$tbl."_nomenclaturalStatusRemarks`(`nomenclaturalStatusRemarks`),
INDEX `".$tbl."_parent`(`parent`),
INDEX `".$tbl."_grandparent`(`grandparent`)
)  ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
if (sql_execute($sql_create,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
