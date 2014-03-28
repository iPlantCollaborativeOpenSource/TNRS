<?php

////////////////////////////////////////////////////////////////////////
// For source='Tropicos', extracts list of all "orphan" species which:
// (a) are linked to synonymous genus, and
// (b) the synonymous genus is linked to an accepted genus
// Under the accepted genus, it then checks for species which 
// have identical epithet and authority as the orphon species and 
// are marked as "Accepted'.
// Orphan species are then linked to the accepted species, and added
// to the synonym table if they are not already there
////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////
// Make table of tropicos orphan species
////////////////////////////////////////////////////////////////////

// Make table of all Tropicos species, then populate acceptance and any
// links to accepted names
echo "Creating table `orphanSpecies`...";
$sql="
DROP TABLE IF EXISTS orphanSpecies;
CREATE TABLE orphanSpecies
SELECT DISTINCT n.nameID, scientificName AS species, specificEpithet, scientificNameAuthorship AS author, 
CAST(NULL AS CHAR(50)) AS speciesAcceptance, CAST(NULL AS UNSIGNED) AS speciesAcceptedNameID,
genus
FROM name n JOIN name_source ns
ON n.nameID=ns.nameID
WHERE ns.sourceID=1 AND n.nameRank='species';
ALTER TABLE orphanSpecies
ADD INDEX(speciesAcceptance);
";
sql_execute_multiple($sql);
echo $done;

echo "Transferring acceptance and accepteNameIDs from table synonym...";
$sql="
UPDATE orphanSpecies o JOIN synonym s 
ON o.nameID=s.nameID
SET o.speciesAcceptance=s.acceptance, o.speciesAcceptedNameID=s.acceptedNameID
WHERE s.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "Deleting species which are either accepted, or are  linked to an accepted name...";
$sql="
DELETE FROM orphanSpecies
WHERE speciesAcceptance='Accepted' OR speciesAcceptedNameID IS NOT NULL;
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Add and index additional columns
// Should be faster now that we have dumped unneeded species
////////////////////////////////////////////////////////////////////

echo "Adding and indexing additional columns...";
$sql="
ALTER TABLE orphanSpecies
ADD COLUMN leftIndex INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN rightIndex INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN genusNameID INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN genusNameIDUpdateMethod VARCHAR(25) DEFAULT NULL,
ADD COLUMN genusAcceptance VARCHAR(50) DEFAULT NULL,
ADD COLUMN acceptedGenusNameID INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN acceptedGenus VARCHAR(50) DEFAULT NULL,
ADD COLUMN acceptedGenusLeftIndex INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN acceptedGenusRightIndex INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN acceptedSpeciesNameID INTEGER(10) UNSIGNED DEFAULT NULL,
ADD COLUMN acceptedSpeciesAcceptance VARCHAR(50) DEFAULT NULL,
ADD COLUMN finalAcceptedSpeciesNameID INTEGER(10) UNSIGNED DEFAULT NULL,
ADD PRIMARY KEY(nameID),
ADD INDEX(species),
ADD INDEX(specificEpithet),
ADD INDEX(author),
ADD INDEX(speciesAcceptedNameID),
ADD INDEX(genus),
ADD INDEX(leftIndex),
ADD INDEX(rightIndex),
ADD INDEX(genusNameID),
ADD INDEX(genusNameIDUpdateMethod),
ADD INDEX(genusAcceptance),
ADD INDEX(acceptedGenusNameID),
ADD INDEX(acceptedGenus),
ADD INDEX(acceptedGenusLeftIndex),
ADD INDEX(acceptedGenusRightIndex),
ADD INDEX(acceptedSpeciesNameID),
ADD INDEX(acceptedSpeciesAcceptance),
ADD INDEX(finalAcceptedSpeciesNameID);
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Gather the information on these species,
// their parent genera, and linked accepted genera
////////////////////////////////////////////////////////////////////

echo "Getting genus nameID for species with genus as immediate parent...";
$sql="
UPDATE orphanSpecies o JOIN classification c JOIN name n
ON o.nameID=c.nameID AND c.parentNameID=n.nameID
SET o.genusNameID=c.parentNameID,
genusNameIDUpdateMethod='immediate parent'
WHERE c.sourceID=1 AND n.nameRank='genus';
";
sql_execute_multiple($sql);
echo $done;

echo "Getting species right and left indexes...";
$sql="
UPDATE orphanSpecies o JOIN classification c
ON o.nameID=c.nameID
SET 
o.leftIndex=c.leftIndex,
o.rightIndex=c.rightIndex
WHERE c.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "Looking up ID of genus for remainder using left and right indices...";
$sql="
UPDATE orphanSpecies o,
(
SELECT n.nameID, leftIndex, rightIndex
FROM classification c JOIN name n
ON c.nameID=n.nameID
WHERE n.nameRank='genus'
AND c.sourceID=1
) AS g
SET 
o.genusNameID=g.nameID,
genusNameIDUpdateMethod='tree traversal indexes'
WHERE g.leftIndex<o.leftIndex AND g.rightIndex>o.rightIndex
AND o.genusNameID IS NULL;
";
sql_execute_multiple($sql);
echo $done;

echo "Getting acceptance and acceptedNameID (if any) of the parent genus...";
$sql="
UPDATE orphanSpecies o JOIN synonym s
ON o.genusNameID=s.nameID
SET 
o.genusAcceptance=s.acceptance,
o.acceptedGenusNameID=s.acceptedNameID
WHERE s.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "If genus is accepted, repeat its nameID as acceptedGenusNameID...";
$sql="
UPDATE orphanSpecies
SET acceptedGenusNameID=genusNameID
WHERE genusAcceptance='Accepted';
";
sql_execute_multiple($sql);
echo $done;

// Get text version of genus name (without author)
// Turns out this is needed for detecting orth. vars. if
// the species is linked directly to the "correct" genus,
// but that genus is different from its immediate parent.
// For example, species of Hyeronima are linked to parent (genus) Hieronyma!
echo "Get text version of genus name (without author)...";
$sql="
UPDATE orphanSpecies o JOIN name n
ON o.acceptedGenusNameID=n.nameID
SET acceptedGenus=n.scientificName;
";
sql_execute_multiple($sql);
echo $done;

echo "Populating left and right indexes of accepted genus...";
$sql="
UPDATE orphanSpecies o JOIN classification c
ON o.acceptedGenusNameID=c.nameID
SET o.acceptedGenusLeftIndex=c.leftIndex,
o.acceptedGenusRightIndex=c.rightIndex
WHERE c.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Link to equivalent species in accepted genus
////////////////////////////////////////////////////////////////////

// For each species with an accepted genus, find all the child species of that accepted
// genus; if any match exactly the epithet and author of the (orphan) species,
// use it's nameID as the potential accepted species
// this is slow but thorough...go for coffee

echo "Finding all child species of accepted genus (sloooow)...";
$sql="
UPDATE orphanSpecies o,
(
SELECT n.nameID, specificEpithet, scientificNameAuthorship, 
leftIndex, rightIndex
FROM name n JOIN classification c
ON n.nameID=c.nameID
WHERE c.sourceID=1
AND n.nameRank='species'
) AS s
SET o.acceptedSpeciesNameID=s.nameID
WHERE o.specificEpithet=s.specificEpithet
AND o.author=s.scientificNameAuthorship
AND o.acceptedGenusLeftIndex<s.leftIndex
AND o.acceptedGenusRightIndex>s.rightIndex;
";
sql_execute_multiple($sql);
echo $done;

echo "Updating actual acceptance for the putative accepted species...";
$sql="
UPDATE orphanSpecies o JOIN synonym s
ON o.acceptedSpeciesNameID=s.nameID
SET o.acceptedSpeciesAcceptance=s.acceptance;
";
sql_execute_multiple($sql);
echo $done;

// for accepted species, their nameID becomes final nameID
echo "Updating nameID for accepted species...";
$sql="
UPDATE orphanSpecies
SET finalAcceptedSpeciesNameID=acceptedSpeciesNameID
WHERE acceptedSpeciesAcceptance='Accepted';
";
sql_execute_multiple($sql);
echo $done;

echo "Getting ID of accepted name for synonyms...";
$sql="
UPDATE orphanSpecies o JOIN synonym s
ON o.acceptedSpeciesNameID=s.nameID
SET o.finalAcceptedSpeciesNameID=s.acceptedNameID
WHERE s.acceptedNameID IS NOT NULL;
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Add indexes to distinguish between names that need to be added
// and names that merely require an update in table synonym
////////////////////////////////////////////////////////////////////

echo "Indexing names already in table synonym...";
$sql="
ALTER TABLE orphanSpecies
ADD COLUMN inSynonym INT(1) UNSIGNED DEFAULT 0,
ADD COLUMN nameType VARCHAR(50) DEFAULT NULL,
ADD COLUMN status VARCHAR(50) DEFAULT NULL,
ADD INDEX(inSynonym),
ADD INDEX(nameType),
ADD INDEX(status);
UPDATE orphanSpecies o JOIN synonym s
ON o.nameID=s.nameID
SET inSynonym=1
WHERE s.sourceID=1;
";
sql_execute_multiple($sql);
echo $done;

echo "Labeling orthographic variants...";
$sql="
UPDATE orphanSpecies
SET nameType='Orth. var.'
WHERE 
(acceptedGenusNameID<>genusNameID OR genus<>acceptedGenus)
AND finalAcceptedSpeciesNameID IS NOT NULL;
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Update names already in synonym table
////////////////////////////////////////////////////////////////////

// update names already in synonym table
// only add links to names not labeled as Accepted
// use existing acceptance code
echo "Updating links for names already in table synonym...";
$sql="
UPDATE synonym s JOIN orphanSpecies o
ON s.nameID=o.nameID
SET 
s.acceptedNameID=o.finalAcceptedSpeciesNameID,
o.status='updated in table synonym'
WHERE s.sourceID=1
AND s.acceptance<>'Accepted'
AND o.finalAcceptedSpeciesNameID IS NOT NULL
AND o.nameType='orth. var.'
AND inSynonym=1;
";
sql_execute_multiple($sql);
echo $done;

////////////////////////////////////////////////////////////////////
// Add new names not already in synonym table
////////////////////////////////////////////////////////////////////

// insert new synonyms, plus links to accepted names
echo "Inserting new names into table synonym...";
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
finalAcceptedSpeciesNameID,
IF(acceptedSpeciesNameID=finalAcceptedSpeciesNameID,'Orth. var.','Synonym')
FROM orphanSpecies
WHERE inSynonym=0
AND nameType='orth. var.'
AND finalAcceptedSpeciesNameID IS NOT NULL;
";
sql_execute_multiple($sql);
echo $done;

// clean up
echo "Removing  table orphanSpecies...";
$sql="
DROP TABLE IF EXISTS orphanSpecies;
";
//sql_execute_multiple($sql);
echo $done;


?>