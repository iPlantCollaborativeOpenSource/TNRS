# Misc queries for manually cleaning up DB prior to use
# Which ones you run will depend on which cleanup options were or 
# were not used during loading
# These queries are run automatically if you activate script 
# autocleanup.php

# drop raw ncbi data tables
DROP TABLE IF EXISTS names_parsed;
DROP TABLE IF EXISTS ncbi_citations;
DROP TABLE IF EXISTS ncbi_delnodes;
DROP TABLE IF EXISTS ncbi_division;
DROP TABLE IF EXISTS ncbi_gencode;
DROP TABLE IF EXISTS ncbi_merged;
DROP TABLE IF EXISTS ncbi_names;
DROP TABLE IF EXISTS ncbi_nodes;

# move raw data tables (except for ncbi) to backup db
DROP TABLE IF EXISTS tnrs3_5_backup.gcc_raw;
CREATE TABLE tnrs3_5_backup.gcc_raw LIKE gcc_raw;
INSERT INTO tnrs3_5_backup.gcc_raw SELECT * FROM gcc_raw;
DROP TABLE gcc_raw;

DROP TABLE IF EXISTS tnrs3_5_backup.tropicos_raw;
CREATE TABLE tnrs3_5_backup.tropicos_raw LIKE tropicos_raw;
INSERT INTO tnrs3_5_backup.tropicos_raw SELECT * FROM tropicos_raw;
DROP TABLE tropicos_raw;

DROP TABLE IF EXISTS tnrs3_5_backup.usda_raw;
CREATE TABLE tnrs3_5_backup.usda_raw LIKE usda_raw;
INSERT INTO tnrs3_5_backup.usda_raw SELECT * FROM usda_raw;
DROP TABLE usda_raw;

# Alternatively, just drop them:
DROP TABLE IF EXISTS gcc_raw;
DROP TABLE IF EXISTS tropicos_raw;
DROP TABLE IF EXISTS usda_raw;

# drop last version of staging table
DROP TABLE IF EXISTS nameStaging;

# Get rid of extra taxamatch tables in case you didnt delete them earlier
DROP TABLE IF EXISTS `infra1list`;
DROP TABLE IF EXISTS `infra2list`;
DROP TABLE IF EXISTS `splist`;

# DROP "forward-compatible" tables not currently used
DROP TABLE IF EXISTS fuzzyMatch;
DROP TABLE IF EXISTS name_lexicalGroup;
DROP TABLE IF EXISTS lexicalGroup;

# Move metadata tables about processsing results & errors
# to backup database, and drop them from core db

DROP TABLE IF EXISTS tnrs3_5_backup.error_table;
CREATE TABLE tnrs3_5_backup.error_table LIKE error_table;
INSERT INTO tnrs3_5_backup.error_table SELECT * FROM error_table;
DROP TABLE error_table;
