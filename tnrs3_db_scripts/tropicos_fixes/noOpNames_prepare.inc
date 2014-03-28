<?php

echo "Preparing table `noOpNames`:\r\n";

echo "  Populating FKs...";
$sql="
UPDATE noOpNames non JOIN name_source ns
ON non.tropicosNameID=ns.nameSourceOriginalID
SET non.nameID=ns.nameID
WHERE ns.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "  Standardizing hybrid x...";
$sql="
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'×','x');
";
sql_execute_multiple($sql);
echo $done;

echo "  Fixing garbled accents in table `noOpNames`...";
$sql="
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'Náb?lek','Nábĕlek');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'S?vul.','Săvul.');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'Pan?u','Panţu');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'?elak.','Čelak.');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'D?de?ek','Dĕdeček');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'Hada?','Hadač');
UPDATE noOpNames
SET acceptedTaxonAuthor=REPLACE(acceptedTaxonAuthor,'Szyszy?.','Szyszył.');
";
sql_execute_multiple($sql);
echo $done;

echo "  Fixing garbled accents in table `name`...";
$sql="
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'L\'HÃ©r.','L\'Hér.'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'L\'HÃ©r.','L\'Hér.')
WHERE scientificNameAuthorship LIKE '%L\'HÃ©r.%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'L\'Her.','L\'Hér.'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'L\'Her.','L\'Hér.')
WHERE scientificNameAuthorship LIKE '%L\'Her.%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Äƒ','ă'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Äƒ','ă')
WHERE scientificNameAuthorship LIKE '%Äƒ%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Å£','ţ'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Å£','ţ')
WHERE scientificNameAuthorship LIKE '%Å£%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Ä•','ĕ'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Ä•','ĕ')
WHERE scientificNameAuthorship LIKE '%Ä•%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'ÄŒ','Č'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'ÄŒ','Č')
WHERE scientificNameAuthorship LIKE '%ÄŒ%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Ä','č'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Ä','č')
WHERE scientificNameAuthorship LIKE '%Ä%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Å‚','ł'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Å‚','ł')
WHERE scientificNameAuthorship LIKE '%Å‚%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Hadač & Á. Löve','Hadač & Á. Löve'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Hadač & Á. Löve','Hadač & Á. Löve')
WHERE scientificNameAuthorship LIKE '%Hadač & Á. Löve%';
UPDATE name 
SET 
scientificNameAuthorship=REPLACE(scientificNameAuthorship,'Dĕdeček','Dĕdeček'),
scientificNameWithAuthor=REPLACE(scientificNameWithAuthor,'Dĕdeček','Dĕdeček')
WHERE scientificNameAuthorship LIKE '%Dĕdeček%';
";
sql_execute_multiple($sql);
echo $done;

echo "  Retrieving nameID for correct name by joining on name+author...";
$sql="
UPDATE noOpNames non JOIN name n JOIN name_source ns
ON non.acceptedTaxonAuthor=n.scientificNameWithAuthor AND n.nameID=ns.nameID
SET non.acceptedNameID=n.nameID
WHERE ns.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "  Deleting records without links to accepted names from table `noOpNames`...";
$sql="
DELETE FROM noOpNames 
WHERE acceptedNameID IS NULL;
";
sql_execute_multiple($sql);
echo $done;

echo "  Adding additional columns to table `noOpNames`...";
$sql="
ALTER TABLE noOpNames
ADD COLUMN inSynonym INT(1) UNSIGNED DEFAULT 0,
ADD COLUMN status VARCHAR(25) DEFAULT NULL,
ADD INDEX(inSynonym),
ADD INDEX(status);
";
sql_execute_multiple($sql);
echo $done;

?>