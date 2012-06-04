<?php
// Adds unique name+author combinations from name staging table
// Duplicate name+authors in staging table may cause problems? (not sure...check)

// Tag names already in core db

// Add temporary FK to table `name`, linking
// to original records in staging table
$fk_staging_nameID="FK_nameStaging_nameID";
$indname="name_".$fk_staging_nameID;

echo "  Adding temporary FK to table `name`...";
$sql="
	ALTER TABLE `name`
	ADD COLUMN `$fk_staging_nameID` INTEGER UNSIGNED DEFAULT NULL,
	ADD INDEX `$indname`(`$fk_staging_nameID`);
";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

echo "  Indexing existing names...";

//////////////////////////////////////
// Exact matching
//////////////////////////////////////

// Names, authors, ranks, EXACT
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$name_fld=n.scientificName AND t.$author_fld=n.scientificNameAuthorship AND t.$rank_fld=n.nameRank
	SET 
	t.$fk_fld=n.nameID,
	n.$fk_staging_nameID=t.$id_fld
	;
";
$msg_error="Failed to index name+author+rank already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Names, authors, EXACT
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$name_fld=n.scientificName AND t.$author_fld=n.scientificNameAuthorship
	SET
	t.$fk_fld=n.nameID,
	n.$fk_staging_nameID=t.$id_fld
	WHERE t.$fk_fld IS NULL;
";
$msg_error="Failed to index name+author already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Names+ranks without authors, EXACT
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$name_fld=n.scientificName AND t.$rank_fld=n.nameRank
	SET
	t.$fk_fld=n.nameID,
	n.$fk_staging_nameID=t.$id_fld
	WHERE t.$author_fld IS NULL AND n.scientificNameAuthorship IS NULL AND t.$fk_fld IS NULL;
";
$msg_error="Failed to index names already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));

// Names without authors, EXACT
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$name_fld=n.scientificName
	SET
	t.$fk_fld=n.nameID,
	n.$fk_staging_nameID=t.$id_fld
	WHERE t.$author_fld IS NULL AND n.scientificNameAuthorship IS NULL AND t.$fk_fld IS NULL;
";
$msg_error="Failed to index names already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

//////////////////////////////////////
// Fuzzy matching
//////////////////////////////////////

$sql="
	UPDATE `$tbl_staging` AS s INNER JOIN `$tnrs_results` AS r INNER JOIN name AS n
	ON s.$fk_fld=r.nameStaging_nameID and r.tnrs_nameID=n.nameID
	SET 
	s.$fk_fld=n.nameID,
	n.$fk_staging_nameID=s.$id_fld
	WHERE  t.$fk_fld IS NULL;
	;
";
$msg_error="Failed to index name+author+rank already in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));


// Add names NOT already in core db
echo "  Adding new names...";
$sql="
	INSERT INTO `name`(
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
		$fk_staging_nameID
	)
	SELECT
		$name_fld,
		$author_fld,
		$rank_fld,
		TRIM(CONCAT_WS(' ',$name_fld,IFNULL($author_fld,''))),
		$genus_fld,
		$specific_epithet_fld,
		$rank_ind_fld,
		$infraepithet_fld,
		$rank_ind_fld2,
		$infraepithet_fld2,
		$otherEpithet_fld,
		$is_hybrid,
		$id_fld
	FROM `$tbl_staging`
	WHERE $fk_fld IS NULL;
";
$msg_error="Failed to add new names to table `name` in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Index newly-added names in staging table:
echo "  Indexing newly-added names...";
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `name` AS n
	ON t.$id_fld=n.$fk_staging_nameID
	SET t.$fk_fld=n.nameID
	WHERE t.$fk_fld IS NULL
	;
";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Update FK to parentNameIDs
// All names should have FKs now, so join by FK
echo "  Updating links to parent nameIDs in staging table...";
$sql="
	UPDATE `$tbl_staging` AS t INNER JOIN `$tbl_staging` AS t2 INNER JOIN `name` AS n
	ON t.$parent_id_fld=t2.$id_fld AND t2.$fk_fld=n.nameID
	SET t.$parent_fk_fld=n.nameID;
";
$msg_error="Failed to update FKs to parent names in core db!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

// Remove temporary FK from table `name`
echo "  Removing temporary FK from table `name`...";
$sql="
	ALTER TABLE `name`
	DROP COLUMN `$fk_staging_nameID`,
	DROP INDEX `$indname`;
";
$msg_error="Failed!\r\n";
if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

?>
