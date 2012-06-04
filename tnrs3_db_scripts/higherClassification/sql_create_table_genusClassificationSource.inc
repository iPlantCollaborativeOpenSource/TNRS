<?php

////////////////////////////////////////////////////////////////
// Creates table of all genera in current higher classification 
// source. Used to flag "safe" genera which either (a) have no 
// homonyms, or (b) have homonyms, but one and only 1 name is 
// flagged as "Accepted". Safe genera can be used to join new 
// species to classification based on name alone, without worrying 
// about authority.
////////////////////////////////////////////////////////////////

$sql_create_tables="

DROP TABLE IF EXISTS genusClassificationSource;

CREATE TABLE `genusClassificationSource` (
genusNameID INTEGER DEFAULT NULL,
genus VARCHAR(50) DEFAULT NULL,
leftIndex INTEGER UNSIGNED DEFAULT NULL,
rightIndex INTEGER UNSIGNED DEFAULT NULL,
familyNameID INTEGER DEFAULT NULL,
family VARCHAR(50) DEFAULT NULL,
hasHomonyms INTEGER(1) DEFAULT 0,
isOnlyAccepted INTEGER(1) DEFAULT 0,
genusFamilyUnique INTEGER(1) UNSIGNED NOT NULL DEFAULT 1,
INDEX (genusNameID),
INDEX (genus),
INDEX (leftIndex),
INDEX (rightIndex),
INDEX (familyNameID),
INDEX (family),
INDEX (hasHomonyms),
INDEX (isOnlyAccepted),
INDEX (genusFamilyUnique)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";

?>
