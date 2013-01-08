<?php
// populates field rankGroup
// used to distinguish family, genus and species (including subspecific taxa)
// from everything else
// This is not the best method; risks missing some new subspecific ranks
// not included in list below, or including as speciesAndBelow names at 
// one of the ranks below which are actually above a species (for example,
// rank='unknown'.
// Probably better to use left and right indices to get all names which 
// are nested in names with rank='species'. 
// Under construction...

echo "  Populating column `rankGroup`...";
$sql="
	UPDATE $tbl_staging
	SET rankGroup=
	CASE
	WHEN nameRank='genus' THEN 'genus'
	WHEN nameRank='family' THEN 'family'
	WHEN (nameRank IN(
		'species',
		'subspecies',
		'subsp.',
		'ssp.',
		'sbsp.',
		'variety',
		'var.',
		'forma',
		'f.',
		'fo.',
		'subspecific unranked',
		'subforma',
		'subvariety',
		'section',
		'proles',
		'conva',
		'nothosubspecies',
		'race',
		'cv',
		'monstr',
		'lusus',
		'nothovariety',
		'microgen',
		'subproles',
		'grex',
		'nothoforma',
		'grex var',
		'mut',
		'sublusus',
		'nm',
		'subspecioid',
		'ecad',
		'sport',
		'group',
		'rasse',
		'agsp',
		'cultivated',
		'nid',
		'nothomorph'
	)) THEN 'speciesAndBelow'
	ELSE NULL
	END
	;
";
$msg_error="failed!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
