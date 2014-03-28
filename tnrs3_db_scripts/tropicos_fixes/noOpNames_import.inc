<?php

///////////////////////////////////////////////////////////////
// Create table of manually-downloaded accepted names
// for names previously return as "No opinion" by 
// ComputedAcceptance
///////////////////////////////////////////////////////////////

echo "Creating table of `noOpNames`...";
$sql="
DROP TABLE IF EXISTS noOpNames;
CREATE TABLE noOpNames (
tropicosNameID INTEGER(11) UNSIGNED NOT NULL,
acceptedTaxonAuthor VARCHAR(255) DEFAULT NULL
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";
sql_execute_multiple($sql);
echo $done;

echo "Importing file of 'No Opinion' names...";
$sql="
LOAD DATA LOCAL INFILE '/home/boyle/tnrs_data/tropicos/noOpinionNamesTake2/results/noOpNamesResults.txt'
INTO TABLE noOpNames
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES;
";
sql_execute_multiple($sql);
echo $done;

echo "Setting empty strings to Null in column acceptedTaxonAuthor...";
$sql="
UPDATE noOpNames
SET acceptedTaxonAuthor=NULL
WHERE TRIM(acceptedTaxonAuthor)='';
";
sql_execute_multiple($sql);
echo $done;

///////////////////////////////////////////////////////////////
// Add some new FK fields and index everything
///////////////////////////////////////////////////////////////

echo "Indexing table `noOpNames`...";
$sql="
ALTER TABLE noOpNames
ADD PRIMARY KEY (tropicosNameID),
ADD COLUMN nameID INTEGER(11) UNSIGNED DEFAULT NULL AFTER tropicosNameID,
ADD COLUMN acceptedNameID INTEGER(11) UNSIGNED DEFAULT NULL,
ADD INDEX (nameID),
ADD INDEX (acceptedNameID),
ADD INDEX (acceptedTaxonAuthor);
";
sql_execute_multiple($sql);
echo $done;

?>