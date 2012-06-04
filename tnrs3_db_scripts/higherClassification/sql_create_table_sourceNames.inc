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

DROP TABLE IF EXISTS sourceNames;

CREATE TABLE sourceNames (
nameID INTEGER UNSIGNED NOT NULL,
nameRank VARCHAR(25) DEFAULT NULL,
nameParts INTEGER(1) UNSIGNED NOT NULL,
family_id INTEGER UNSIGNED NOT NULL,
family VARCHAR(50) DEFAULT NULL,
genus_id INTEGER UNSIGNED NOT NULL,
genus VARCHAR(100) DEFAULT NULL,
species_id INTEGER UNSIGNED NOT NULL,
species VARCHAR(150) DEFAULT NULL,
trinomial_id INTEGER UNSIGNED NOT NULL,
trinomial VARCHAR(250) DEFAULT NULL,
trinomial_norank VARCHAR(250) DEFAULT NULL,
quadrinomial_id INTEGER UNSIGNED NOT NULL,
quadrinomial VARCHAR(250) DEFAULT NULL,
quadrinomial_norank VARCHAR(250) DEFAULT NULL,
newFamilyID  INTEGER UNSIGNED NOT NULL,
newFamily VARCHAR(50) DEFAULT NULL,
inSource INTEGER(1) DEFAULT 0,
joinMethod VARCHAR(50) DEFAULT NULL,
INDEX sourceNames_nameID(nameID),
INDEX sourceNames_nameRaank(nameRank),
INDEX sourceNames_nameParts(nameParts),
INDEX sourceNames_family_id(family_id),
INDEX sourceNames_family(family),
INDEX sourceNames_genus_id(genus_id),
INDEX sourceNames_genus(genus),
INDEX sourceNames_species_id(species_id),
INDEX sourceNames_species(species),
INDEX sourceNames_trinomial_id(trinomial_id),
INDEX sourceNames_trinomial(trinomial),
INDEX sourceNames_trinomial_norank(trinomial_norank),
INDEX sourceNames_quadrinomial_id(quadrinomial_id),
INDEX sourceNames_quadrinomial(quadrinomial),
INDEX sourceNames_quadrinomial_norank(quadrinomial_norank),
INDEX newFamilyID(newFamilyID),
INDEX sourceNames_newFamily(newFamily),
INDEX sourceNames_inSource(inSource),
INDEX sourceNames_joinMethod(joinMethod)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";
?>
