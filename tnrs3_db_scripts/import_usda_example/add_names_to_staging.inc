<?php
// Insert names from raw import table into staging table

echo "Adding names to staging table `$tbl_staging`...";
$msg_error = "Failed!";
$sql="
	INSERT INTO `$tbl_staging` (
	`$id_fld`,
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$infraepithet_fld`,
	`$rank_ind_fld2`,
	`$infraepithet_fld2`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT
	nameID,
	parentNameID,
	ScientificName,
	IF(parentName='Plantae',NULL,
		IF(QuadrinomialAuthor IS NOT NULL, QuadrinomialAuthor, 
			IF(Subspecies IS NOT NULL OR Variety IS NOT NULL OR Subvariety IS NOT NULL 
				OR Forma IS NOT NULL,TrinomialAuthor,GeneraBinomialAuthor))),
	IF(parentName='Plantae','family',
		IF(Forma IS NOT NULL,'forma',
			IF(Subvariety IS NOT NULL,'subvariety',
				IF(Variety IS NOT NULL,'variety',
					IF(Subspecies IS NOT NULL,'subspecies',
						IF(Species IS NOT NULL,'species',
							IF(Genus IS NOT NULL,'genus','unknown'))))))),
	IF(AcceptedSymbol IS NULL, '$name_url_bad', CONCAT('$name_url_base',AcceptedSymbol)),
	IF(HybridGenusIndicator IS NOT NULL 
		OR HybridSpeciesIndicator IS NOT NULL 
		OR HybridSubspeciesIndicator IS NOT NULL
		OR HybridVarietyIndicator IS NOT NULL,1,0),
	Genus,
	Species,
	IF(Subspecies IS NOT NULL,'subsp.',
		IF(Variety IS NOT NULL,'var.',NULL)),
	IF(Subspecies IS NOT NULL,Subspecies,
		IF(Variety IS NOT NULL,Variety,NULL)),
	IF(Subspecies IS NOT NULL AND Variety IS NOT NULL,'var.',
		IF(Subspecies IS NOT NULL AND Subvariety IS NOT NULL,'subvar.',
			IF(Subspecies IS NOT NULL AND Forma IS NOT NULL,'fo.',
				IF(Variety IS NOT NULL AND Subvariety IS NOT NULL,'subvar.',
					IF(Variety IS NOT NULL AND Forma IS NOT NULL,'fo.',NULL))))),				
	IF(Subspecies IS NOT NULL AND Variety IS NOT NULL,Variety,
		IF(Subspecies IS NOT NULL AND Subvariety IS NOT NULL,Subvariety,
			IF(Subspecies IS NOT NULL AND Forma IS NOT NULL,Forma,
				IF(Variety IS NOT NULL AND Subvariety IS NOT NULL,Subvariety,
					IF(Variety IS NOT NULL AND Forma IS NOT NULL,Forma,NULL))))),
	NULL,
	NULL,
	Family,
	acceptance,
	acceptedNameID
	FROM `$tbl_names_raw`;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
