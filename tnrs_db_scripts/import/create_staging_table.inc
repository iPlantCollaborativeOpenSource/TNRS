<?php
// Create mysql tables identical in structure to raw text files
// This versions assume separate name and synonym tables

echo "Dropping previous staging table...";
$msg_error="failed to drop table `$tbl`!";
$sql="DROP TABLE IF EXISTS `$tbl`";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

$sql="
CREATE TABLE `$tbl` (
`$id_fld` INTEGER(11) UNSIGNED NOT NULL,
`$parent_id_fld`  INTEGER(11) UNSIGNED DEFAULT NULL,
`$name_fld` VARCHAR(250) NOT NULL,
`$author_fld` VARCHAR(100) DEFAULT NULL,
`$rank_fld` VARCHAR(50) DEFAULT NULL,
`$source_url_fld` VARCHAR(250) DEFAULT NULL,
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
`$acceptance_fld` VARCHAR(1) DEFAULT NULL,
`$accepted_id_fld` INTEGER(11) UNSIGNED DEFAULT NULL,
`parent` VARCHAR(250) NOT NULL,
`grandparent` VARCHAR(250) NOT NULL,
PRIMARY KEY(`$id_fld`),
INDEX `".$tbl."_$parent_id_fld`(`$parent_id_fld`),
INDEX `".$tbl."_$name_fld`(`$name_fld`),
INDEX `".$tbl."_$author_fld`(`$author_fld`),
INDEX `".$tbl."_$rank_fld`(`$rank_fld`),
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
INDEX `".$tbl."_parent`(`parent`),
INDEX `".$tbl."_grandparent`(`grandparent`)
)  ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
echo "Creating staging table `$tbl`...";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
