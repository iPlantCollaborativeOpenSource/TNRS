<?php

//////////////////////////////////////////////////////////////////////////
// Create new table family_acceptedFamily_lookup
// Populates with fully synonymous families and their accepted names
// Partially synonymous families not included; these should be resolved
// by means of genus-family synonymy using genus_family_lookup table
//////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////
// Add all unique family-acceptedFamily combinations
////////////////////////////////////////////////////////////////////////

echo "Populating table `$fg`...";
$sql="
	INSERT INTO `$faf`
		(family,acceptedFamily)
	VALUES
		('Cruciferae','Brassicaceae'),
		('Guttiferae','Clusiaceae'),
		('Umbelliferae','Apiaceae'),
		('Compositae','Asteraceae'),
		('Leguminosae','Fabaceae'),
		('Palmae','Arecaceae'),
		('Labiatae','Lamiaceae'),
		('Gramineae','Poaceae'),
		('Mimosaceae','Fabaceae'),
		('Papilionaceae','Fabaceae'),
		('Caesalpiniaceae','Fabaceae');
";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_fail));

?>
