<?php

////////////////////////////////////////////////////////////////
// Creates table higherClassification, which holds all names
// in each classification source, plus all names at level of 
// species and below from all other sources. Populates field
// family according to that source.
////////////////////////////////////////////////////////////////

$sql_create_tables="

DROP TABLE IF EXISTS higherClassification;

CREATE TABLE higherClassification (
  classificationSourceID INTEGER UNSIGNED NOT NULL,
  nameSourceID INTEGER UNSIGNED NOT NULL,
  nameID INTEGER UNSIGNED NOT NULL,
  parentNameID INTEGER UNSIGNED DEFAULT NULL,
  scientificName VARCHAR(250) DEFAULT NULL ,
  cononicalName VARCHAR(250) DEFAULT NULL ,
  nameRank VARCHAR(25) DEFAULT NULL,
  nameParts  INTEGER(1) DEFAULT NULL,
  family VARCHAR(100) DEFAULT NULL COMMENT 'family according to this classification source',
  joinMethod VARCHAR(50) DEFAULT NULL,
  INDEX higherClassification_classificationSourceID(classificationSourceID),
  INDEX higherClassification_nameSourceID(nameSourceID),
  INDEX higherClassification_nameID(nameID),
  INDEX higherClassification_parentNameID(parentNameID),
  INDEX higherClassification_name(scientificName),
  INDEX higherClassification_canonicalName(cononicalName),
  INDEX higherClassification_nameRank(nameRank),
  INDEX higherClassification_nameParts(nameParts),  
  INDEX higherClassification_family(family),
  INDEX higherClassification_joinMethod(joinMethod)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";

?>
