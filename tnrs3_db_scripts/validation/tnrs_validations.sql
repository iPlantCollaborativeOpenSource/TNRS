###############################################################################
# general counts of records by source
###############################################################################

# visually inspect sources
SELECT * 
FROM source;

# count names per source
SELECT s.sourceID, sourceName, COUNT(*) as names
FROM source s join name_source ns
ON s.sourceID=ns.sourceID
GROUP BY s.sourceID, s.sourceID;

# count classification events by source
# should be same as number of names
SELECT sourceID, COUNT(*) as names
FROM classification 
GROUP BY sourceID;

# count taxonomic opinions
# these numbers will be < # of names per source
SELECT sourceID, COUNT(*) as names
FROM synonym 
GROUP BY sourceID;

###############################################################################
# check for null family in table classification
###############################################################################

# check family field in table classification
SELECT IF(family IS NULL,'NULL','Not NULL') AS famIsNull, COUNT(*)
FROM classification
GROUP BY famIsNull;

# show ranks of name with null family
# should all be higher than family
SELECT sourceName, nameRank, COUNT(DISTINCT n.nameID) as namesNoFamily
FROM classification c JOIN name n JOIN name_source ns JOIN source s
ON c.nameID=n.nameID AND n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE c.family IS NULL
GROUP BY sourceName, nameRank;

# Count names by source where family field is NULL in table classification
SELECT sourceName, IF(family IS NULL, "NULL", "NOT NULL") AS familyNull,
COUNT(DISTINCT c.nameID) AS names
FROM source s JOIN classification c JOIN name n 
ON s.sourceID=c.sourceID AND c.nameID=n.nameID
GROUP BY sourceName, familyNull;

###############################################################################
# check acceptance codes
###############################################################################

# Check acceptance codes
SELECT acceptance, count(*) 
FROM synonym
GROUP BY acceptance;

# show source and count of names for which no opinion provided
# in synonym table
# WARNING will be slow due to left join
SELECT sourceName, COUNT(DISTINCT n.nameID) AS names
FROM source s JOIN name_source ns JOIN name n 
ON s.sourceID=ns.sourceID AND ns.nameID=n.nameID
LEFT JOIN synonym sy
ON n.nameID=sy.nameID
WHERE sy.nameID IS NULL
GROUP BY sourceName;

###############################################################################
# check for missing parsed name components
###############################################################################

# Check for missing genus
# ignoring hybrids
SELECT COUNT(*)
FROM name
WHERE genus IS NULL and nameRank='genus'
AND (isHybrid=0 OR isHybrid IS NULL);

# Check for missing specific epithets
# ignoring hybrids
SELECT COUNT(*)
FROM name
WHERE specificEpithet IS NULL and nameRank='species'
AND (isHybrid=0 OR isHybrid IS NULL);

# Check for missing trinomial epithets
# ignoring hybrids
SELECT COUNT(*)
FROM name
WHERE nameRank<>'species'
AND infraspecificEpithet IS NULL AND scientificName LIKE "% % % %"
AND (isHybrid=0 OR isHybrid IS NULL);

# Check for missing quadrinomial epithets
# ignoring hybrids
SELECT COUNT(*)
FROM name
WHERE nameRank<>'species'
AND infraspecificEpithet2 IS NULL AND scientificName LIKE "% % % % % %"
AND (isHybrid=0 OR isHybrid IS NULL);

# count by source the number of names with missing name components
SELECT sourceName AS source, COUNT(DISTINCT n.nameID) AS names
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (
(nameRank='genus' AND genus IS NULL) 
OR (nameRank='species' AND (genus IS NULL OR specificEpithet IS NULL)) 
OR (scientificName LIKE "% % % %" AND nameRank<>'species' AND (genus IS NULL OR specificEpithet IS NULL 
OR infraspecificEpithet IS NULL)) 
OR (scientificName LIKE "% % % % % %" AND nameRank<>'species' AND (genus IS NULL OR specificEpithet IS NULL 
OR infraspecificEpithet IS NULL OR infraspecificEpithet2 IS NULL))
) AND 
(isHybrid=0 OR isHybrid IS NULL)
GROUP BY sourceName;

###############################################################################
# Check for missing parents in taxamatch tables
###############################################################################

# the Poa annua check
# Poa annua on tropicos has an intervening infrageneric taxon between it and
# the genus Poa. An error was preventing such species from laoding.
SELECT species_id, genus_id, genus_species 
FROM splist_genlist_combined 
WHERE genus_species LIKE 'Poa ann%';

# A more detailed check
# should return same number of records as above
# Checks species where species source and genus source are the same
SELECT s.sourceName AS speciesSourceName, species_id, genus_id, genus_species, 
s2.sourceName AS genusSourceName 
FROM source s JOIN name_source ns JOIN splist_genlist_combined sg
JOIN name_source ns2 JOIN source s2
ON s.sourceID=ns.sourceID AND ns.nameID=sg.species_id 
AND sg.genus_id=ns2.nameID AND ns2.sourceID=s2.sourceID
AND s.sourceID=s2.sourceID
WHERE genus_species LIKE 'Poa ann%'
ORDER BY s.sourceID;

###############################################################################
# Check for names from one source linked only to accepted name in different source
# Note: this will be very slow for large, multi-source database
###############################################################################

SELECT syn.sourceName, COUNT(DISTINCT syn.nameID) AS mislinkedNames
FROM 
(
SELECT syn.nameID, syn.sourceID, sourceName
FROM synonym syn JOIN source s
ON syn.sourceID=s.sourceID
WHERE syn.acceptedNameID IS NOT NULL
) AS syn
LEFT JOIN
(
SELECT syn.nameID AS synNameID, syn.sourceID AS synSourceID,
acc.nameID AS accNameID, s.sourceID AS accSourceID, sourceName AS accNameSource
FROM synonym syn JOIN synonym acc JOIN name_source ns JOIN source s
ON syn.acceptedNameID=acc.nameID AND acc.nameID=ns.nameID AND ns.sourceID=s.sourceID
) AS acc
ON syn.nameID=acc.synNameID AND syn.sourceID=acc.synSourceID
WHERE syn.nameID IS NULL;

###############################################################################
# Checks for missing parsed name components, as well as anomalies
# in the column 'nameParts'
###############################################################################

# Check nameParts for species
# Should always=2, never NULL
SELECT nameParts, COUNT(*) AS names
FROM name
WHERE nameRank='species' 
GROUP BY nameParts;

# Display value(s) of namePart for trinomials
SELECT nameParts, COUNT(*) AS names
FROM name
WHERE (scientificName LIKE "% % % %" AND scientificName NOT LIKE "% % % % % %")
AND nameRank<>'species'
AND (isHybrid=0 OR isHybrid IS NULL) 
GROUP BY nameParts;

# Display value(s) of namePart for quadrinomials
SELECT nameParts, COUNT(*) AS names
FROM name
WHERE (scientificName LIKE "% % % % % %")
AND nameRank<>'species'
AND (isHybrid=0 OR isHybrid IS NULL) 
GROUP BY nameParts;

# count trinomials with incorrect nameParts values by sources
SELECT sourceName, COUNT(*) AS trinomials
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (scientificName LIKE "% % % %" AND scientificName NOT LIKE "% % % % % %")
AND nameRank<>'species'
AND (isHybrid=0 OR isHybrid IS NULL) 
AND nameParts<>3
GROUP BY sourceName
LIMIT 25;

# count quadrinomials with incorrect nameParts values by sources
SELECT sourceName, COUNT(*) AS quadrinomials
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (scientificName LIKE "% % % % % %")
AND nameRank<>'species'
AND (isHybrid=0 OR isHybrid IS NULL) 
AND nameParts<>4
GROUP BY sourceName
LIMIT 25;

###############################################################################
# Check for duplicate names
###############################################################################

# Check for duplicate names by source
SELECT sourceName, COUNT(*) AS duplicateNames
FROM 
(
SELECT sourceName, nameRank, scientificName, scientificNameAuthorship, COUNT(*) AS names
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
GROUP BY sourceName, nameRank, scientificName, scientificNameAuthorship
HAVING names>1
) AS n
GROUP BY sourceName;

###############################################################################
# More specific checks, if errors found by the above queries:
###############################################################################

# show by source names with missing name components
SELECT n.nameID, nameRank, scientificName, genus, specificEpithet, infraspecificEpithet,  infraspecificEpithet2
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (
(nameRank='genus' AND genus IS NULL) 
OR (nameRank='species' AND (genus IS NULL OR specificEpithet IS NULL)) 
OR (scientificName LIKE "% % % %" AND nameRank<>'species' AND (genus IS NULL OR specificEpithet IS NULL 
OR infraspecificEpithet IS NULL)) 
OR (scientificName LIKE "% % % % % %" AND nameRank<>'species' AND (genus IS NULL OR specificEpithet IS NULL 
OR infraspecificEpithet IS NULL OR infraspecificEpithet2 IS NULL))
) 
AND (isHybrid=0 OR isHybrid IS NULL)
AND sourceName="ncbi"
LIMIT 50;

# inspect names from specific source
# where family field is NULL in table classification
SELECT n.nameID, nameRank, scientificName
FROM source s JOIN classification c JOIN name n 
ON s.sourceID=c.sourceID AND c.nameID=n.nameID
WHERE family IS NULL AND sourceName='usda'
LIMIT 50;

# display trinomials with incorrect nameParts by specific source
SELECT sourceName, nameRank, scientificName, genus, specificEpithet, infraspecificEpithet,
infraspecificEpithet2
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (scientificName LIKE "% % % %" AND scientificName NOT LIKE "% % % % % %")
AND (isHybrid=0 OR isHybrid IS NULL) 
AND nameParts<>3
AND sourceName='ncbi'
LIMIT 20;

# Examine original anomalous trinomials from tropicos
SELECT nameID, scientificName, genus, specificEpithet, otherEpithet
FROM tropicos_raw
WHERE (scientificName LIKE "% % %")
AND (isHybrid=0 OR isHybrid IS NULL) 
AND (scientificName NOT LIKE "% x" AND scientificName NOT LIKE "% x %")
AND (scientificName LIKE "% var. %" OR scientificName LIKE "% subsp. %")
AND nameRank='species'
LIMIT 25;

# display quadrinomials with incorrect nameParts by specific source
SELECT sourceName, nameRank, scientificName, genus, specificEpithet, infraspecificEpithet, infraspecificEpithet2
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
WHERE (scientificName LIKE "% % % % % %")
AND (isHybrid=0 OR isHybrid IS NULL) 
AND nameParts<>4
AND sourceName='ncbi'
LIMIT 25;

# search for particular name by genus in all sources
SELECT n.nameID, s.sourceID, sourceName, scientificName, scientificNameAuthorship
FROM source s join name_source ns join name n
ON s.sourceID=ns.sourceID AND ns.nameID=n.nameID
WHERE scientificName LIKE "Zea%"
ORDER BY scientificName, sourceName;

# count species of Zea in tropicos
SELECT n.nameID, s.sourceID, sourceName, scientificName, scientificNameAuthorship
FROM source s join name_source ns join name n
ON s.sourceID=ns.sourceID AND ns.nameID=n.nameID
WHERE scientificName LIKE "Zea %" AND sourceName="tropicos" AND nameRank="species"
ORDER BY scientificName;

# Inspect duplicate names
SELECT sourceName, nameRank, scientificName, scientificNameAuthorship, COUNT(*) AS names
FROM name n JOIN name_source ns JOIN source s
ON n.nameID=ns.nameID AND ns.sourceID=s.sourceID
GROUP BY sourceName, nameRank, scientificName, scientificNameAuthorship
HAVING names>1
ORDER BY sourceName, nameRank, scientificName, scientificNameAuthorship;



