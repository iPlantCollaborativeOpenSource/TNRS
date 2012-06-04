Imports Global Compositae Checklist.

Once import has been completed and the staging table populated, subsequent steps (indexing
of staging table, error-checking and normalization to core db tables) 
are universal and do not require any source-specific customizations.

Files to be imported MUST be in subdirectory data/

___________________________________________________

Example file gccExample.csv in subdirectory data/ is the top few lines of raw gcc dump file. 
This file is included only to show the structure of the original gcc raw file; it will 
probably NOT load correctly as parent-child adjacencies (nesting of species within genera, 
varieties within species) are not complete. 

