Taxamatch tables, version 6.2

Scripts in this function create and populate tables used by taxamatch.

Original taxamatch schema from Michael Gidden's PHP port of Tony Rees's 
Taxamatch
These scripts written by Brad Boyle, last modified 22 Feb. 2011

Tables have been revised to incorporate Edwin Skidmore's optimizations
Scripts have been revised to populate names using TNRS core database and to accommodate families.
This latest version (6) accommodates trinomials and quadrinomials, and includes alternate genus-family classifications

Please see parameters in params.inc, also global_params.inc in root script directory.

Version 3:
- corrected create_tables.sql: changed GENUS_ID to INT(11) DEFAULT NULL (was autoincrement previously; this caused serious problems)
- added code to generate table 'taxamatch_orphan_species'; contains species which script is unable to link unambiguously to a single genus. Useful for troubleshooting as such species are deleted from the main tables  (splist and splist_genlist_combined).

Version 4:
- table "famlist" added
- added index on column `SPECIES` in table `splist`. Not needed for taxamatch functionality, but speeds up population of GENUS_ID during building of taxamatch tables

Version 5:
- table `genlist_famlist_combined` added
- new columns added to table `genlist`: DEFAULT_FAMILY & DEFAULT_FAMILY_ID
- note additional indices on table `genlist`
- new function sql_get_cols() in sql_functions.inc

Version 6:
- Added tables: infra1list, infra2list, infra1list_splist_combined, infra2list_infra1list_combined
- Added additional genus-in-family classifications to table genlist_famlist_combined; this tables based on all genera and families listed in IPNI (2003 download) plus additional conserved family names, imported to db as csv file "fg_all_final.txt"
- Note that currently tables infra2list and infra2list_infra1list_combined not populated, as not quadrinomials exist in source taxonomy (from Tropicos, in table `name`). In future these tables will be populated as other taxonomy sources added to core db.

Version 6.1:
In table `infra1_splist_combined`:
   - added columns `infra1_noRank`, `species_infra1_noRank`
In table `infra2list_infar1list_combined`:
   - added columns `infra1_noRank`, `infra2_noRank`, `rank1`, `rank2`
   - dropped column `rank`
   - added column `infra1_infra2_noRank`, containing both infraspecific names WITHOUT rank indicators

Version 6.2:
Incorporates Jerry's scripts to finish populating taxamatch tables. For now, this is triggered by a separate script in this directory "taxamatch_tables_update.php". You must run this script as the final step to complete the build of the tnrs database. In next version, will roll the two taxamatch scripts into one.
