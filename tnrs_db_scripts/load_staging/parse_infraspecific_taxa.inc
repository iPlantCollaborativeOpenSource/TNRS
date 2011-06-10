<?php
// This assumes that epithets of taxa below rank of species,
// AND epithets of supraspecific taxa requiring a rank indicator (e.g., subgenera, tribes)
// are all in field otherEpithet, AND that the rank indicator is already present
// in the rank_indicator field.
// Also removes unnecessary rank indicator and "otherEpithet" (weirdly, returned
// by Tropicos API) for taxa such as family and genus

echo "Parsing taxon name components:\r\n";

// remove any unnecessary rank indicators
echo " Removing unnecessary rank indicators...";
$sql="
	UPDATE $tbl 
	SET 
	`$rank_ind_fld`=NULL,
	`$otherEpithet_fld`=NULL
	WHERE 
	`$name_fld` NOT LIKE '% %' OR $rank_fld='species'
	;
	";
$msg_error="\r\nFailed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));


// move epithet of infraspecific taxa to correct field 
// and set otherEpithet field to NULL
echo "  Infraspecific taxa...";
$sql="
	UPDATE $tbl 
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

?>
