<?php
// Adds unique name+author combinations from name staging table
// Duplicate name+authors in staging table may cause problems? (not sure...check)

// Tag names already in core db

///////////////////////////////////////////////////////
// Add temporary FK to table `name`, linking
// to original records in staging table
// if it already exists, be sure to clear it
///////////////////////////////////////////////////////
$fk_staging_nameID="FK_nameStaging_nameID";
$indname="name_".$fk_staging_nameID;

if (exists_index('name',$indname)==false && sql_exists_column('name',$fk_staging_nameID)==false) {
	echo "  Adding temporary FK to table `name`...";
	$sql="
		ALTER TABLE `name`
		ADD COLUMN `$fk_staging_nameID` INTEGER UNSIGNED DEFAULT NULL,
		ADD INDEX `$indname`(`$fk_staging_nameID`);
	";
	$msg_error="Failed!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
} else {
	// Set FK column to null
	// Critical if loading multiple sources
	echo "  Clearing FK in table `name`...";
	$sql="
		UPDATE `name`
		SET $fk_staging_nameID=NULL;
	";
	$msg_error="Failed!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
}

////////////////////////////////////////////////////////
// set nulls to empty strings to simplify joins
////////////////////////////////////////////////////////

echo "  setting nulls to empty strings...";
// should simplify joins
// staging table:
$sql="
	UPDATE `$tbl_staging`
	SET $author_fld='' 
	WHERE $author_fld IS NULL;
";
$msg_error="Failed to set nulls to empty strings for `$tbl_staging`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// name:
$sql="
	UPDATE name 
	SET scientificNameAuthorship='' 
	WHERE scientificNameAuthorship IS NULL;
";
$msg_error="Failed to set nulls to empty strings for `name`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

////////////////////////////////////////////////////////
// Index names already in database
////////////////////////////////////////////////////////

echo "  Indexing existing names...";
// Names, authors, ranks
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$name_fld=n.scientificName 
	AND t.$author_fld=n.scientificNameAuthorship 
	AND t.$rank_fld=n.nameRank
	SET 
	t.$fk_fld=n.nameID,
	n.$fk_staging_nameID=t.$id_fld
	;
";
$msg_error="Failed to index name+author+rank already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

////////////////////////////////////////////////////////
// Add new names
////////////////////////////////////////////////////////

// Add names NOT already in core db
echo "  Adding new names...";
$sql="
	INSERT INTO `name`(
		originalSourceID,
		scientificName,
		scientificNameAuthorship,
		nameRank,
		scientificNameWithAuthor,
		genus,
		specificEpithet,
		rankIndicator,
		infraspecificEpithet,
		infraspecificRank2,
		infraspecificEpithet2,
		otherEpithet,
		isHybrid,
		`$fk_staging_nameID`
	)
	SELECT
		$sourceID,
		`$name_fld`,
		`$author_fld`,
		`$rank_fld`,
		TRIM(CONCAT_WS(' ',`$name_fld`,IFNULL(`$author_fld`,''))),
		`$genus_fld`,
		`$specific_epithet_fld`,
		`$rank_ind_fld`,
		`$infraepithet_fld`,
		`$rank_ind_fld2`,
		`$infraepithet_fld2`,
		`$otherEpithet_fld`,
		`$is_hybrid`,
		`$id_fld`
	FROM `$tbl_staging`
	WHERE `$fk_fld` IS NULL;
";
$msg_error="Failed to add new names to table `name` in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

////////////////////////////////////////////////////////
// Transfer new nameIDs to staging table
////////////////////////////////////////////////////////

echo "  Indexing newly-added names...";
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$id_fld=n.$fk_staging_nameID
	SET t.$fk_fld=n.nameID
	WHERE t.$fk_fld IS NULL
	;
";
//echo "SQL=\n$sql\n";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

////////////////////////////////////////////////////////
// Update FK to parentNameIDs
// All names should have FKs now, so join by FK
////////////////////////////////////////////////////////

echo "  Updating links to parent nameIDs in staging table...";
$sql="
	UPDATE `$tbl_staging` AS t JOIN `$tbl_staging` AS t2 JOIN `name` AS n
	ON t.$parent_id_fld=t2.$id_fld AND t2.$fk_fld=n.nameID
	SET t.$parent_fk_fld=n.nameID;
";
//echo "SQL=\n$sql\n";
$msg_error="Failed to update FKs to parent names in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

if ($fuzzy_match==true) {
	echo "  Linking fuzzy-matched names to existing names...";
	// Update any newly-added names with ID of existing
	// name to which they match fuzzily

	$sql="
		UPDATE name as n INNER JOIN `$tbl_staging` AS s INNER JOIN `$tnrs_results` AS r
		ON n.nameID=s.$fk_fld and s.nameID=r.nameStaging_nameID
		SET 
		n.fuzzyMatchNameID=r.tnrs_nameID
		WHERE 
		n.fuzzyMatchNameID IS NULL AND r.overallScore>=$tnrs_overall_score_min AND
		(
			(r.authorMatched IS NOT NULL AND r.authorMatchedScore>=$tnrs_author_score_min) 
			OR
			(r.authorMatched IS NULL)
		)
		;
	";
	$msg_error="Failed to index name+author+rank already in core db!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
}

////////////////////////////////////////////////////////
// restore nulls
////////////////////////////////////////////////////////

echo "  setting empty strings back to null...";
// staging table:
$sql="
	UPDATE `$tbl_staging` 
	SET $author_fld=NULL 
	WHERE $author_fld='';
";
$msg_error="Failed to reset empty strings to nulls for `$tbl_staging`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// name:
$sql="
	UPDATE name 
	SET scientificNameAuthorship=NULL
	WHERE scientificNameAuthorship='';
";
$msg_error="Failed to reset empty strings to nulls for `name`!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
