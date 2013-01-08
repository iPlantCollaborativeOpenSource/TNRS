Files in this directory build complete classifications based on every source flagged "isHigherClassification"=1 (true) in table `source`. Joins species and subspecies from all other sources by genus, or failing that, by family (adding genus if joined by family). Left and right indexes are rebuilt for all taxa in classification using modified tree traversal. The new classification containing all names in the database (except those that could not be joined) replaces the previous classification for that source.

Denormalized columns containing family (plus other higher taxa if desired) for that classification are then added to table `name`. The column is named for the higher taxon, plus a suffix for the source (e.g., family_tropicos).

Assumes core db has been created, staging table loaded and indexed, and the core database created; see separate scripts for these steps.

Main script: build_classifications.php, calls all others.
All parameters set in local params.inc file in this directory and global_params.inc.

STILL TO BE DONE:
rename load_default_taxa.inc to add_denormalized_higherTaxa.inc, and modify to add default taxon columns for EACH classification source.
