Files in this directory load names & synonymy from the staging table (`nameStaging`) into the core TNRS database.

Replaces or merely updates existing records for a particular source, depending on the replace paramenter set in the parameter file.
 
Assumes core db has been created, and staging table loaded and indexed; see separate scripts for these steps.

Root script: load_names.php, calls all others.
All parameters set in params.inc; also global_params.inc in root script directory.
