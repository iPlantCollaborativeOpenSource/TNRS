<?php

$sql_create_tables="
CREATE TABLE $names_parsed (
	ScientificName_submitted VARCHAR(255) NOT NULL,
	Family_submitted VARCHAR(255) DEFAULT NULL,
	Genus_submitted VARCHAR(255) DEFAULT NULL,
	SpecificEpithet_submitted VARCHAR(255) DEFAULT NULL,
	InfraspecificRank_submitted VARCHAR(255) DEFAULT NULL,
	Infraspecific1Epithet_submitted VARCHAR(255) DEFAULT NULL,
	Infraspecific2Rank_submitted VARCHAR(255) DEFAULT NULL,
	Infraspecific2Epithet_submitted VARCHAR(255) DEFAULT NULL,
	Author_submitted VARCHAR(255) DEFAULT NULL,
	Annotation  VARCHAR(25) DEFAULT NULL,
	INDEX `".$names_parsed."_ScientificName_submitted`(`ScientificName_submitted`)
) TYPE=MYISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
"

?>
