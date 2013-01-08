Files in this directory and subdirectories build the TNRS 3 database

Release date: 1 June 2012
Database version: 3.5.4
Database revision date: 22 October 2012
Application url: http://tnrs.iplantcollaborative.org/

I. Quick guide:

1. Test build:

A complete build of a TNRS database is performed by running the master script load_tnrs.php.
For a trial build of an example database (using a small sample of names from the Tropicos
API, USDA Plants, and the Global Compositae Checklist in Darwin Core format) enter your 
server and database information in the global parameters file (global_params.inc) and run 
load_tnrs.php. 

2. Production build:

For a production build using one or more of your own taxonomic sources, you will need to
create an import directory for each source (named "import_[sourceName]"), modeled after the
contents of one of the four example import directories provided. Your raw data must
be in the same format as the example data in the data/ directory; you may also need to 
adjust source-specific parameters in the params.inc file for that source. 

We recommend using as the template for all your sources the example provided in 
import_dwcExample. The readme file in import_dwcExample provides details on preparing your
source files, setting source-specific parameters (params.inc in each import directory) and
global parameters (global_params.inc).

II. Details:

Each major step in this process is performed by files in a single directory. Within each 
directory, one script (with extension .php) calls all the others (extensions .inc). 
The steps are as follows:

1. create_tnrs_core.php (in create_tnrs_core/)
- Creates empty tnrs database
2. import.php (in import_[sourceName]/) 
- Imports raw data for an individual taxonomic source into MySQL and performs initial 
loading to the staging table (nameStaging)
- Steps specific to an individual source are in this directory
3. prepare_staging.php (in prepare_staging/)
- Finishes structuring and populating staging table (nameStaging)
- These operations are universal, not source-specific
4. load_core_db.php (in load_core_db/)
- Normalizes the contents of the staging table to the core database
5. make_genus_family_lookups (in genus_family_lookups/)
- Builds lookup tables of current and historic genus-in-family classifications, based on
GRIN taxonomy website
6. taxamatch_tables.php (in taxamatch_tables/)
- Denormalizes names in core database into lookup tables used by TaxaMatch fuzzy
matching application
7. build_classifications.php (in build_classifications/)
- Builds table 'higherClassification', which classifies all names from all sources 
according to any source for which isHigherClassification=1 (set in params.inc for that
source)

For a build from multiple sources, step 1 is run ONCE. Steps 2-4 are run for EACH 
source. Finally, steps 5-6 are run ONCE.

This entire process is automated by the master script load_tnrs.php, which calls
all the others. Before running this script, you MUST set critical parameters in 
global_params.inc. Also, set source-specific parameters in params.inc in the import 
directory for each source. See instructions in load_tnrs.php, global_params.inc. For 
details on setting source-specific parameters, see the readme file in import_dwcExample/.

An individual source can be refreshed without rebuilding the entire database by loading
source only and setting $replace_db=false (in global_params.inc). This will run 
steps 2-7 above, replacing only names linked uniquely to the source in question. 
For a faster replace, set $replace=false in params.inc for the source being refreshed. 
Only entirely new names from that source will be added. Existing names (and metadata
such as source urls and date of access) will not be changed.

III. Dependencies

- Custom PHP functions are in subdirectory functions/
- Custom MySQL function strSplit() must be present in your installation of MySQL. This 
function will be automatically installed if not already present. Alternatively, you can
use contents of file strSplit_function.sql to install this function manually
- perl utility dbf_dump is needed to extract downloaded GRIN taxonomy files to plain
text  (step 5). See readme in genus_family_lookups for details.

IV. Changes

Version 3.5.4: 
1. Added more detailed instructions to this readme
2. Added Darwin Core import template (entire directory import_dwcExample and contents)
3. Fixed bug in load_tnrs.php which caused loading to fail if run-time 
option $replace_db set to "No".

Questions?
Brad Boyle
bboyle@email.arizona.edu