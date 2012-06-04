<?php
// Parse name components of any remaining unparsed names

echo "  Parsing missing taxon name components:\r\n";

// Remove any unnecessary rank indicators for taxa species and higher
// Weirdly, returned by Tropicos API for taxa such as family, genus, species
echo "   Removing unnecessary rank indicators...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$rank_ind_fld`=NULL,
	`$otherEpithet_fld`=NULL
	WHERE 
	`$name_fld` NOT LIKE '% %' OR $rank_fld='species'
	;
";
$msg_error="\r\nFailed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Move epithet of infraspecific taxa to correct field 
// and set otherEpithet field to NULL
// Assumes that epithets of taxa below rank of species,
// AND epithets of supraspecific taxa requiring a rank indicator (e.g., subgenera, tribes)
// are all in field otherEpithet, AND that the rank indicator is already present
// in the rank_indicator field.
// this step mainly applies to names from tropicos
echo "    Removing infraspecific taxa from 'otherEpithet' column...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$infraepithet_fld`=`$otherEpithet_fld`,
	`$otherEpithet_fld`=NULL
	WHERE `$genus_fld` IS NOT NULL
	AND `$specific_epithet_fld` IS NOT NULL
	AND `$name_fld` LIKE '% % % %'
	AND `$otherEpithet_fld` IS NOT NULL
	;
";
$msg_error="\r\nFailed to parse infraspecific ranks and epithet!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Fix true trinomials erroneously ranked as species
// Not comprehensive but catches majority of cases
// The name will still be incorrectly parsed, but this should be fixed
// by parsing step below. Setting infraspecificEpithet to NULL ensures
// that name will be re-parsed
echo "   Fixing incorrectly-ranked trinomials...";
$sql="
	UPDATE $tbl_staging 
	SET `$rank_fld`=
	CASE 
	WHEN `$name_fld` LIKE '% var. %' THEN 'variety' 
	WHEN `$name_fld` LIKE '% variety %' THEN 'variety' 
	WHEN `$name_fld` LIKE '% subsp. %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% ssp. %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% subspecies %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% fo. %' THEN 'forma' 
	WHEN `$name_fld` LIKE '% forma %' THEN 'forma' 
	END,
	`$infraepithet_fld`=NULL
	WHERE 
	$rank_fld='species' 
	AND `$name_fld` LIKE '% % % %' AND `$name_fld` NOT LIKE '% % % % %' 
	AND ($is_hybrid=0 OR $is_hybrid IS NULL)
	AND (
	`$name_fld` LIKE '% var. %' 
	OR `$name_fld` LIKE '% variety %'
	OR `$name_fld` LIKE '% subsp. %' 
	OR `$name_fld` LIKE '% ssp. %'
	OR `$name_fld` LIKE '% subspecies %'
	OR `$name_fld` LIKE '% fo. %'
	OR `$name_fld` LIKE '% forma %'
	)	
	;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Fix true quadrinomials erroneously ranked as species
// Not comprehensive but catches majority of cases
// The name will still be incorrectly parsed, but this should be fixed
// by parsing step below. Setting infraspecificEpithet and 
// infraspecificEpithet2 to NULL ensures that name will be re-parsed
echo "   Fixing incorrectly-ranked quadrinomials...";
$sql="
	UPDATE $tbl_staging 
	SET `$rank_fld`=
	CASE 
	WHEN `$name_fld` LIKE '% var. %' THEN 'variety' 
	WHEN `$name_fld` LIKE '% variety %' THEN 'variety' 
	WHEN `$name_fld` LIKE '% subsp. %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% ssp. %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% subspecies %' THEN 'subspecies' 
	WHEN `$name_fld` LIKE '% fo. %' THEN 'forma' 
	WHEN `$name_fld` LIKE '% forma %' THEN 'forma' 
	END,
	`$infraepithet_fld`=NULL,
	`$infraepithet_fld2`=NULL
	WHERE 
	$rank_fld='species' 
	AND `$name_fld` LIKE '% % % % % %' AND `$name_fld` NOT LIKE '% % % % % % %' 
	AND ($is_hybrid=0 OR $is_hybrid IS NULL)
	AND (
	`$name_fld` LIKE '% var. %' 
	OR `$name_fld` LIKE '% variety %'
	OR `$name_fld` LIKE '% subsp. %' 
	OR `$name_fld` LIKE '% ssp. %'
	OR `$name_fld` LIKE '% subspecies %'
	OR `$name_fld` LIKE '% fo. %'
	OR `$name_fld` LIKE '% forma %'
	)	
	;
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Parse unparsed genera
echo "   Fixing genera...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$genus_fld`=`$name_fld`
	WHERE 
	$rank_fld='genus' 
	AND `$genus_fld` IS NULL 
	AND ($is_hybrid=0 OR $is_hybrid IS NULL);
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Parse unparsed species
echo "   Parsing species...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$genus_fld`=strSplit($name_fld,' ',1),
	`$specific_epithet_fld`=TRIM( REPLACE($name_fld,strSplit($name_fld,' ',1),'') ),
	`$rank_ind_fld`=NULL,
	`$infraepithet_fld`=NULL,
	`$rank_ind_fld2`=NULL,
	`$infraepithet_fld2`=NULL 	
	WHERE 
	$rank_fld='species' 
	AND (`$genus_fld` IS NULL OR `$specific_epithet_fld` IS NULL)
	AND ($is_hybrid=0 OR $is_hybrid IS NULL);
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Parse unparsed trinomials
echo "   Parsing trinomials...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$genus_fld`=strSplit($name_fld,' ',1),
	`$specific_epithet_fld`=strSplit($name_fld,' ',2),
	`$rank_ind_fld`=strSplit($name_fld,' ',3),
	`$infraepithet_fld`=strSplit($name_fld,' ',4)
	WHERE $rank_fld<>'species' 
	AND `$name_fld` LIKE '% % % %' AND `$name_fld` NOT LIKE '% % % % % %' 
	AND (`$genus_fld` IS NULL OR `$specific_epithet_fld` IS NULL OR `$infraepithet_fld` IS NULL)
	AND ($is_hybrid=0 OR $is_hybrid IS NULL);
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

// Parse unparsed quadrinomials
echo "   Parsing quadrinomials...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$genus_fld`=strSplit($name_fld,' ',1),
	`$specific_epithet_fld`=strSplit($name_fld,' ',2),
	`$rank_ind_fld`=strSplit($name_fld,' ',3),
	`$infraepithet_fld`=strSplit($name_fld,' ',4),
	`$rank_ind_fld2`=strSplit($name_fld,' ',5),
	`$infraepithet_fld2`=strSplit($name_fld,' ',6)	
	WHERE $rank_fld<>'species'
	AND `$name_fld` LIKE '% % % % % %' 
	AND (`$genus_fld` IS NULL OR `$specific_epithet_fld` IS NULL OR `$infraepithet_fld` IS NULL
		OR `$infraepithet_fld2` IS NULL)
	AND ($is_hybrid=0 OR $is_hybrid IS NULL);
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

?>
