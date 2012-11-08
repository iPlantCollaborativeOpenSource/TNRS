Imports example file of taxonomic data, as extracted from Tropicos web service (see:
http://services.tropicos.org/). See file tropicos_api_extract_example.csv for an example 
of the extract schema. These scripts will import any file conforming to this schema 
(assuming no anomalies in the  nameID-parentNameID links). Note that nameID, parentNameID, 
and acceptedNameID MUST be integers to use this template. For details of other columns, 
see detailed descriptions in readme file under import_dwcExample/. If you importing DwC 
format data, please use template in directory import_dwcExample, rather than this one.

Once import has been completed and the staging table populated, subsequent steps (indexing
of staging table, error-checking and normalization to core db tables) 
are universal and do not require any source-specific customizations.

Files to be imported must be in directory data/.

Specific steps:

(1) Creates raw data table(s) for importing
(2) Creates main staging table
(3) Imports raw text files of names and synonyms into MySQL (raw data tables)
(4) Adds these names and synonyms from raw data table to main staging table

Master scripts: import.php. Calls all others.
Parameters: in params.inc; also see global_params.inc

