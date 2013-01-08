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

DROP TABLE IF EXISTS speciesCurrentSource;

CREATE TABLE speciesCurrentSource (
nameSourceID INTEGER DEFAULT NULL,
nameID INTEGER DEFAULT NULL,
species VARCHAR(150) DEFAULT NULL,
parentNameID INTEGER DEFAULT NULL,
rightIndex INTEGER UNSIGNED DEFAULT NULL,
leftIndex INTEGER UNSIGNED DEFAULT NULL,
linkedInfraID INTEGER UNSIGNED DEFAULT NULL,
genusNameID INTEGER DEFAULT NULL,
genus VARCHAR(50) DEFAULT NULL,
hasHomonymsGenus INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
familyNameID INTEGER DEFAULT NULL,
family VARCHAR(50) DEFAULT NULL,
hasHomonymsFamily INTEGER(1) UNSIGNED NOT NULL DEFAULT 0,
genusFamilyUnique INTEGER(1) UNSIGNED NOT NULL DEFAULT 1,
newGenusNameID INTEGER DEFAULT NULL,
newFamilyNameID INTEGER DEFAULT NULL,
newFamily VARCHAR(50) DEFAULT NULL,
hasChildren INTEGER(1) DEFAULT 0,
joinMethod VARCHAR(255) DEFAULT NULL,
INDEX speciesCurrentSource_nameSourceID(nameSourceID),
INDEX speciesCurrentSource_nameID(nameID),
INDEX speciesCurrentSource_species(species),
INDEX speciesCurrentSource_parentNameID(parentNameID),
INDEX speciesCurrentSource_rightIndex(rightIndex),
INDEX speciesCurrentSource_leftIndex(leftIndex),
INDEX speciesCurrentSource_linkedInfraID(linkedInfraID),
INDEX speciesCurrentSource_genusNameID(genusNameID),
INDEX speciesCurrentSource_genus(genus),
INDEX speciesCurrentSource_hasHomonymsGenus(hasHomonymsGenus),
INDEX speciesCurrentSource_familyNameID(familyNameID),
INDEX speciesCurrentSource_family(family),
INDEX speciesCurrentSource_hasHomonymsFamily(hasHomonymsFamily),
INDEX speciesCurrentSource_genusFamilyUnique(genusFamilyUnique),
INDEX speciesCurrentSource_newGenusNameID(newGenusNameID),
INDEX speciesCurrentSource_newFamilyNameID(newFamilyNameID),
INDEX speciesCurrentSource_hasChildren(hasChildren),
INDEX speciesCurrentSource_joinMethod(joinMethod)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";
?>
