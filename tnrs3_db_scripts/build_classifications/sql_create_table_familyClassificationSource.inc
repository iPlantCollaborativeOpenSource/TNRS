<?php

////////////////////////////////////////////////////////////////
// Creates table of all families in current higher classification 
// source. Used to flag "safe" families which either (a) have no 
// homonyms, or (b) have homonyms, but one and only 1 name is 
// flagged as "Accepted". Safe families can be used to join new 
// species to classification based on name alone, without worrying 
// about authority.
////////////////////////////////////////////////////////////////

$sql_create_tables="

DROP TABLE IF EXISTS familyClassificationSource;

CREATE TABLE `familyClassificationSource` (
familyNameID INTEGER DEFAULT NULL,
family VARCHAR(50) DEFAULT NULL,
leftIndex INTEGER UNSIGNED DEFAULT NULL,
rightIndex INTEGER UNSIGNED DEFAULT NULL,
hasHomonyms INTEGER(1) DEFAULT 0,
isOnlyAccepted INTEGER(1) DEFAULT 0,
INDEX (familyNameID),
INDEX (family),
INDEX (leftIndex),
INDEX (rightIndex),
INDEX (hasHomonyms),
INDEX (isOnlyAccepted)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

";

?>
