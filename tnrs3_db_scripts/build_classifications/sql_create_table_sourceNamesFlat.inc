<?php

///////////////////////////////////////////////////////
// Creates temporary table to hold all species from 
// current source prior to joining to higher classification.
// Also stores original genus and family, new genus and
// family, and additional information needed to decide 
// whether to join by infraspecific taxon, by genus, or 
// by family
///////////////////////////////////////////////////////

$sql_create_tables="

DROP TABLE IF EXISTS sourceNamesFlat;

CREATE TABLE sourceNamesFlat (
nameID
parentNameID
nameRank
nameParts
family_id
family
genus_id
genus
species_id
species
trinomial_id
trinomial
trinomial_norank
quadrinomial_id
quadrinomial
quadrinomial_norank   
joinMethod VARCHAR(255) DEFAULT NULL,
INDEX sourceNamesFlat_nameID(nameID),
INDEX sourceNamesFlat_parentNameID(parentNameID),
INDEX sourceNamesFlat_nameRnank(nameRnank),
INDEX sourceNamesFlat_nameParts(nameParts),
INDEX sourceNamesFlat_family_id(family_id),
INDEX sourceNamesFlat_family(family),
INDEX sourceNamesFlat_genus_id(genus_id),
INDEX sourceNamesFlat_genus(genus),
INDEX sourceNamesFlat_species_id(species_id),
INDEX sourceNamesFlat_species(species),
INDEX sourceNamesFlat_trinomial_id(trinomial_id),
INDEX sourceNamesFlat_trinomial(trinomial),
INDEX sourceNamesFlat_trinomial_norank(trinomial_norank),
INDEX sourceNamesFlat_quadrinomial_id(quadrinomial_id),
INDEX sourceNamesFlat_quadrinomial(quadrinomial),
INDEX sourceNamesFlat_quadrinomial_norank(quadrinomial_norank),
INDEX sourceNamesFlat_joinMethod(joinMethod)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";
?>
