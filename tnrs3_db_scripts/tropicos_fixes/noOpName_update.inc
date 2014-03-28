<?php

echo "Transfering accepted nameIDs to TNRS database:\r\n";

echo "  Marking names already in table `synonym`...";
$sql="
UPDATE noOpNames n JOIN synonym s
ON n.nameID=s.nameID
SET n.inSynonym=1
WHERE s.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "  Marking names in table `synonym` already linked to accepted name...";
$sql="
UPDATE noOpNames n JOIN synonym s
ON n.nameID=s.nameID
SET n.status='already present'
WHERE s.sourceID=1 AND s.acceptedNameID IS NOT NULL;
";
sql_execute_multiple($sql);
echo $done;

echo "  Transfering new acceptedNameIDs to table `synonym`...";
$sql="
UPDATE synonym s JOIN noOpNames n
ON s.nameID=n.nameID
SET
s.acceptance=IF(s.acceptance IS NULL,'Synonym',s.acceptance),
s.acceptedNameID=n.acceptedNameID,
n.status='transferred to synonym'
WHERE s.sourceID=1 
AND s.acceptedNameID IS NULL
AND s.acceptance<>'Accepted'
AND n.status IS NULL;
";
sql_execute_multiple($sql);
echo $done;

echo "Transfering new names to table `synonym`:\r\n";

echo "  Inserting new synonyms...";
$sql="
UPDATE noOpNames
SET status='Added to synonym'
WHERE inSynonym=0;
INSERT INTO synonym (
sourceID, 
nameID,
acceptedNameID, 
acceptance
)
SELECT
1, 
nameID,
acceptedNameID, 
'Synonym'
FROM noOpNames
WHERE inSynonym=0
AND nameID<>acceptedNameID;
";
sql_execute_multiple($sql);
echo $done;

echo "  Inserting new accepted names...";
$sql="
INSERT INTO synonym (
sourceID, 
nameID,
acceptedNameID, 
acceptance
)
SELECT
1, 
nameID,
NULL, 
'Accepted'
FROM noOpNames
WHERE inSynonym=0
AND nameID=acceptedNameID;
";
sql_execute_multiple($sql);
echo $done;

echo "Removing table noOpNames...";
$sql="
DROP TABLE IF EXISTS noOpNames;
";
sql_execute_multiple($sql);
echo $done;

?>