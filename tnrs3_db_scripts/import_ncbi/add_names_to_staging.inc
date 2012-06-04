<?php

/////////////////////////////////////////////////////////////////////////////////////
// Purpose: 
// Insert names from raw data tables in staging table
// This script is customized for ncbi, which has raw data in multiple tables
/////////////////////////////////////////////////////////////////////////////////////

// add accepted names directly from names table
// Use node IDs (tax_id) as nameIDs
// ncbi uses the canonical version of the name as "accepted", so author is missing at this stage
// name components will need additional parsing later
// accepted names are identified by name_status='scientific name'
echo "Adding accepted names to staging table `$tbl_staging`...";
$sql="
	INSERT INTO `$tbl_staging` (
	`$id_fld`,
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT
	d.tax_id,
	d.parent_tax_id,
	n.name_txt,
	'',
	d.rank,
	CONCAT('$source_base_url',d.tax_id),
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	'Accepted',
	NULL
	FROM `$tbl_names` as n inner join `$tbl_nodes` as d inner join `$tbl_division` as v
	ON n.tax_id=d.tax_id AND d.division_id=v.division_id
	WHERE n.name_class='scientific name' AND v.division_name='Plants';
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Add name components to names of genera and species or below
echo "Updating name components for accepted names...";
$sql="
	update `$tbl_staging` s inner join (
		select distinct tax_id, p.*
		from `$tbl_names` n inner join `$names_parsed` p
		on n.name_txt=p.ScientificName_submitted
		where n.name_class='scientific name' and (p.rankGroup='genus' or p.rankGroup='species and below') and p.division='plants'
	) as n
	on s.nameID=n.tax_id and s.scientificName=n.ScientificName_submitted
	SET 
	s.`$author_fld`=n.Author_submitted,
	s.genus=n.Genus_submitted,
	s.specificEpithet=n.SpecificEpithet_submitted,
	s.rankIndicator=n.InfraspecificRank_submitted,
	s.infraspecificEpithet=n.Infraspecific1Epithet_submitted,
	s.rankIndicator2=n.Infraspecific2Rank_submitted,
	s.infraspecificEpithet2=n.Infraspecific2Epithet_submitted
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail))

// Add authors to names, where these can be unambiguously assigned
// Essentially, this can only be done for genera and species or below
echo "Updating authors for accepted names...";
$sql="
	update `$tbl_staging` s inner join (
		select distinct tax_id, p.*
		from `$tbl_names` n inner join `$names_parsed` p
		on n.name_txt=p.ScientificName_submitted
		where n.name_class='authority' and (p.rankGroup='genus' or p.rankGroup='species and below') and p.division='plants'
	) as n
	on s.nameID=n.tax_id and s.scientificName=n.nameMinusAuthor
	SET 
	s.`$author_fld`=n.Author_submitted
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Change PK to auto_increment to accommodate synonyms, which do not have their own id
echo "Changing PK of staging table to auto_increment...";
$msg_error = "Failed!";
$sql="
	ALTER TABLE `$tbl_staging`
	MODIFY COLUMN `$id_fld` INTEGER(11) UNSIGNED NOT NULL AUTO_INCREMENT
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Add synonyms without authors, for higher taxa only
// note that nameID is no longer added, as it is not auto_increment
echo "Adding synonyms for higher taxa...";
$sql="
	INSERT INTO `$tbl_staging` (
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT DISTINCT
	p.accepted_tax_id,
	n.name_txt,
	'',
	'unknown',
	CONCAT('$source_base_url',n.tax_id),
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	'Synonym',
	p.accepted_tax_id
	FROM `$names_parsed`  p INNER JOIN `$tbl_names` n
	ON p.ScientificName_submitted=n.name_txt
	WHERE n.name_class<>'scientific name' AND  n.name_class<>'authority' AND  n.name_class not like '%common name'
       	AND p.division='Plants' AND (p.rankGroup='supergeneric' OR p.rankGroup='subgeneric' OR p.rankGroup='no rank');
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Add synonyms with authors, for taxa at rank of genus and lower only
// note that nameID is auto_ncrement
echo "Adding synonyms (name only) for taxa at rank of species and below...";
$sql="
	INSERT INTO `$tbl_staging` (
	`$parent_id_fld`,
	`$name_fld`,
	`$author_fld`,
	`$rank_fld`,
	`$source_url_fld`,
	`$is_hybrid`,
	`$genus_fld`,
	`$specific_epithet_fld`,
	`$rank_ind_fld`,
	`$otherEpithet_fld`,
	`$subclass_fld`,
	`$family_fld`,
	`$acceptance_fld`,
	`$accepted_id_fld`
	)
	SELECT DISTINCT
	p.accepted_tax_id,
	n.name_txt,
	'',
	'unknown',
	CONCAT('$source_base_url',n.tax_id),
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	'Synonym',
	p.accepted_tax_id
	FROM `$names_parsed` p INNER JOIN `$tbl_names` n
	ON p.ScientificName_submitted=n.name_txt
	WHERE n.name_class<>'scientific name' AND n.name_class<>'authority' AND n.name_class NOT LIKE '%common name' AND p.Author_submitted IS NOT NULL 
	AND p.division='Plants' AND (p.rankGroup='genus' OR p.rankGroup='species or lower');
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Add authors to synonym names at rank of genera and species or below, where available
echo "Updating authors for synonyms at rank of genus and below...";
$sql="
	update `$tbl_staging` s inner join (
		select distinct n.tax_id, p.*
		from `$names_parsed` p INNER JOIN `$tbl_names` n
		ON p.ScientificName_submitted=n.name_txt
		WHERE n.name_class<>'scientific name' AND n.name_class<>'authority' AND n.name_class NOT LIKE '%common name' AND p.Author_submitted IS NOT NULL 
		AND p.division='Plants' AND (p.rankGroup='genus' OR p.rankGroup='species or lower')
	) as n
	on s.nameID=n.tax_id and s.scientificName=n.nameMinusAuthor
	set s.`$author_fld`=n.Author_submitted,
	s.genus=n.Genus_submitted,
	s.specificEpithet=n.SpecificEpithet_submitted,
	s.rankIndicator=n.InfraspecificRank_submitted,
	s.infraspecificEpithet=n.Infraspecific1Epithet_submitted,
	s.rankIndicator2=n.Infraspecific2Rank_submitted,
	s.infraspecificEpithet2=n.Infraspecific2Epithet_submitted
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Reparse ALL species
// This is necessary due to numerous "junk" species in ncbi
// For example, 'Riedelia aff. wrayii SBG 83-203'. Everything after 
// the genus should be dumped to specificEpithet field.
// These names are parsed incorrectly by GNA parser
echo "Re-parsing incorrectly parsed species...";
$sql="
	UPDATE $tbl_staging 
	SET 
	`$genus_fld`=strSplit($name_fld,' ',1),
	`$specific_epithet_fld`=TRIM( REPLACE($name_fld,strSplit($name_fld,' ',1),'') ),
	`$rank_ind_fld`=NULL,
	`$infraepithet_fld`=NULL,
	`$rank_ind_fld2`=NULL,
	`$infraepithet_fld2`=NULL	
	WHERE 
	$rank_fld='species' 
	AND ($is_hybrid=0 OR $is_hybrid IS NULL);
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Drop PK
echo "Removing auto_increment from PK of staging table...";
$sql="
	ALTER TABLE `$tbl_staging`
	MODIFY COLUMN `$id_fld` INTEGER(11) UNSIGNED NOT NULL
	;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

?>
