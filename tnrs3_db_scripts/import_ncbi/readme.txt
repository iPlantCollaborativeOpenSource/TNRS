Imports and loads ncbi names and classification to the staging table (nameStaging), in preparation for additional indexing and import to TNRS core database. Processing steps specific to this source are performed by scripts in this directory. Subsequent, source-independent manipulations of the staging table are performed by scripts in directory prepare_staging/.

This process is much more complicated for NCBI taxonomy than for other sources. Peculiarities of the NCBI taxonomic model result in poor mapping to the TNRS nomenclatural model (basically, NCBI does not have a nomenclatural model). Difficulties include (1) separate records for name and name+author, (2) concatenated name and author, (3) names lack unique IDs (only nodes have IDs; note lack of PK in table `names`), (4) inconsistent implementation of model (the name+authority version of a name is usually classified as name_class='authority', but occasionally appears as name_class='synonym', even name_class='scientific name'. You can't predict with certainty.

This script requires two set of files to run:
(1) a complete set of ncbi taxonomy files, as extracted from taxdump.tar.gz on their ftp site (ftp://ftp.ncbi.nih.gov/pub/taxonomy/)
(2) a text file consisting of the results of parsing all unique names from the field `name_txt` in the ncbi table `names`. A file of these names should be processed using the TNRS-GNI parse-only option. Currently this step is not automated, so you will need to bulk process the names with the TNRS and manually copy the downloaded results file to the same directory where you put the raw ncbi files (as downloaded from their ftp site). 

Place all ncbi taxonomy files, plus the parsed names file, in the same directory. Set their names and path in the parameters file (params.inc). After that, these scripts will do the rest.

The result of this processing is the partially prepared table `nameStaging`.
