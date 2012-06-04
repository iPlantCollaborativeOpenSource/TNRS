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
  nameID INTEGER UNSIGNED NOT NULL,
  familyNameID INTEGER UNSIGNED NOT NULL,
  family VARCHAR(100) DEFAULT NULL,
  joinMethod VARCHAR(50) DEFAULT NULL,
  INDEX higherClassification_classificationSourceID(classificationSourceID),
  INDEX higherClassification_nameID(nameID),
  INDEX higherClassification_familyNameID(familyNameID),  
  INDEX higherClassification_family(family),
  INDEX higherClassification_joinMethod(joinMethod)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";

?>
