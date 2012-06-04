Files in this directory load names & synonymy from the staging table (`nameStaging`) into the core TNRS database. Assumes database tables have already been created (see create_tnrs_core), the source taxonomy imported to the staging table (see import_xxx/) and the stagingTable prepared and indexed (see prepare_staging/).

"load_core_db.php" replaces or updates existing records for a particular source, depending on the $replace parameter in global_params.inc

If adding a new source, I recommend you run all names to be added through the tnrs itself; the resulting output can be use to link variant spellings to existing names already in the core database. This adds fewer duplicate names than the default mode, which adds as new any names which is not an exact duplicate of an existing name. To activate this option, see "Name resolution parameters" in global_params.inc.

Directory tnrs_results_raw/ is for placing csv file resulting from scrubbing all names in source using TNRS. Obviously, you can place file anywhere, this is just to remind you to do it. If you wish to use this file, you MUST set $fuzzy_match=true in global_params.inc, and set related variables. If you set $fuzzy_match=false, related variables will be ignored and you will not be required to have a trns_result file.
 
Assumes core db has been created, and staging table loaded and indexed; see separate scripts for these steps.

Main script: load_core_db.php, calls all others.
All parameters set in local params.inc file in this directory and global_params.inc.
