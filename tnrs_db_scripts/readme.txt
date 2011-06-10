Files in this directory and subdirectories build the TNRS core database and table for the taxamatch application.

Each directory contains one master script (extension php) which calls all the others.

Global parameters are set in the single global_params.inc, in this directory. Also see local params.inc file in each subdirectory.

For a complete build of the database, scripts should be run in the following order:

create_tnrs_core.php (in create_tnrs_core/)
import.php (in import/) - this version loads custom names dump from Tropicos API
load_staging.php (in load_staging/)
load_core_db.php (in load_core_db/)
taxamatch_tables.php (in taxamatch_tables/)

Custom MySQL function strSplit() is required. Use contents of file strSplit.txt to add this function to your MySQL installation.


