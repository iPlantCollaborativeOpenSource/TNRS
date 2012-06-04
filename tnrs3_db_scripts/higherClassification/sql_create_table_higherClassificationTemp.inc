<?php

////////////////////////////////////////////////////////////////
// Creates table higherClassification, which holds all names
// in each classification source, plus all names at level of 
// species and below from all other sources. Populates field
// family according to that source.
////////////////////////////////////////////////////////////////

$sql_create_tables="

DROP TABLE IF EXISTS higherClassificationTemp;

CREATE TABLE higherClassificationTemp (
  classificationSourceID INTEGER UNSIGNED NOT NULL,
  nameID INTEGER UNSIGNED NOT NULL,
  parentNameID INTEGER UNSIGNED DEFAULT NULL,
  scientificName VARCHAR(250) DEFAULT NULL ,
  canonicalName VARCHAR(250) DEFAULT NULL ,
  nameRank VARCHAR(25) DEFAULT NULL,
  nameParts INTEGER(1) DEFAULT NULL,
  familyNameID INTEGER UNSIGNED NOT NULL,
  family VARCHAR(100) DEFAULT NULL,
  joinMethod VARCHAR(50) DEFAULT NULL,
  INDEX higherClassificationTemp_classificationSourceID(classificationSourceID),
  INDEX higherClassificationTemp_nameID(nameID),
  INDEX higherClassificationTemp_parentNameID(parentNameID),
  INDEX higherClassificationTemp_scientificName(scientificName),
  INDEX higherClassificationTemp_canonicalName(canonicalName),
  INDEX higherClassificationTemp_nameRank(nameRank),
  INDEX higherClassificationTemp_nameParts(nameParts),  
  INDEX higherClassificationTemp_familyNameID(familyNameID),  
  INDEX higherClassificationTemp_family(family),
  INDEX higherClassificationTemp_joinMethod(joinMethod)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";

?>
