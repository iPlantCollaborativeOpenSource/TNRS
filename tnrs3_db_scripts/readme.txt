Files in this directory and subdirectories build the TNRS 3 database

Release date: 1 June 2012
Application url: http://tnrs.iplantcollaborative.org/

Each major step in this process is performed by files in a single directory. Within each 
directory, one script (with extension .php) calls all the others (extensions .inc). 
The steps are as follows:

1. create_tnrs_core.php (in create_tnrs_core/)
- Creates empty tnrs database
2. import.php (in import_*/) 
- Imports raw data for an individual taxonomic source into MySQL and performs initial 
loading to the staging table
- Steps specific to an individual source are in this directory
3. prepare_staging.php (in prepare_staging/)
- Finishes structuring and populating staging table. 
- Universal operations, no specific to any source, are in this subdirectory
4. load_core_db.php (in load_core_db/)
- Normalizes the contents of the staging table to the core database
5. make_genus_family_lookups (in genus_family_lookups/)
- Builds lookup tables of current and historic genus-in-family classifications, based on
GRIN taxonomy website
6. taxamatch_tables.php (in taxamatch_tables/)
- Denormalizes names in core database into lookup tables used by TaxaMatch fuzzy
matching application
7. build_classifications.php (in build_classifications/)
- Builds table 'higherClassification', containing family classification for all
names from all sources according to an individual source

For a build from multiple sources, step 1 is run ONCE. Steps 2-4 are run for EACH 
source. Finally, steps 5-6 are run ONCE.

This entire process is automated by the master script load_tnrs.php, which calls
all the others. Before running this script, you MUST set critical parameters in 
global_params.inc. Also, set source-specific parameters in params.inc in the import 
directory for each source. See instructions in load_tnrs.php, global_params.inc, the
individual params.inc files in each import subdirectory, and readme files.

An individual source can be refreshed without rebuilding the entire database by loading
source only and setting $replace_db=false (in global_params.inc). This will run 
steps 2-7 above, replacing only names linked uniquely to the source in question. 
For a faster replace, set $replace=false in params.inc for the source being refreshed. 
Only entirely new names from that source will be added. Existing names (and metadata
such as source urls and date of access) will not be changed.

Dependencies:
- Custom PHP functions are in subdirectory functions/
- Custom MySQL function strSplit() must be present in your installation of MySQL. This 
function will be automatically installed if not already present. Alternatively, you can
use contents of file strSplit_function.sql to install this function manually
- perl utility dbf_dump is needed to extract downloaded GRIN taxonomy files to plain
text  (step 5). See readme in genus_family_lookups for details.

Questions?
Brad Boyle
bboyle@email.arizona.edu