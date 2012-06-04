<?php
// populates extra columns added to names_parsed
// by previous script names_parsed_alter.inc

// Populate nameMinusAuthors
echo "Populating column nameMinusAuthor in $names_parsed...";
$sql="
	UPDATE `$names_parsed`
	SET nameMinusAuthor=TRIM(
		CONCAT_WS(
			' ',
			IFNULL(Genus_submitted,''),
			IFNULL(SpecificEpithet_submitted,''),
			IFNULL(InfraspecificRank_submitted,''),
			IFNULL(Infraspecific1Epithet_submitted,''),
			IFNULL(Infraspecific2Rank_submitted,''),
			IFNULL(Infraspecific2Epithet_submitted,'')
		)
	);
";
$msg_error="error!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "Marking improperly parsed names...";
// Any Family name with a detected author is parsed incorrectly; mark these to avoid adding
//
$sql="
	UPDATE `$names_parsed` p INNER JOIN `$tbl_names` n
	ON p.ScientificName_submitted=n.name_txt
	SET is_parse_error=1
	WHERE 
	(Family_submitted is not null and (Genus_submitted is not null or Author_submitted is not null))
	;
";
$msg_error="error!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "Updating division...";
// Redundant but helpful for speeding up queries
$sql="
	UPDATE `$names_parsed` p INNER JOIN `$tbl_names` n INNER JOIN `$tbl_nodes` d INNER JOIN `$tbl_division` v
	ON p.ScientificName_submitted=n.name_txt and n.tax_id=d.tax_id and d.division_id=v.division_id
	SET p.division='plants'
	WHERE 
	v.division_name='Plants'
	;
";
$msg_error="error!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "Updating link to accepted name...";
// Redundant but helpful for speeding up queries
$sql="
	UPDATE `$names_parsed` p INNER JOIN `$tbl_names` n INNER JOIN `$tbl_nodes` d INNER JOIN `$tbl_division` v
	ON p.ScientificName_submitted=n.name_txt and n.tax_id=d.tax_id and d.division_id=v.division_id
	SET p.accepted_tax_id=n.tax_id
	WHERE 
	v.division_name='Plants'
	;
";
$msg_error="error!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "Classifying rank groups...";
// Classifies names into above genus, genus, below genus and above species, 
// species and below, and no rank
// Not that this classification refers to the accepted name's rank only
// Necessary for adding names in later steps
$sql="
	UPDATE `$names_parsed` p INNER JOIN `$tbl_names` n INNER JOIN `$tbl_nodes` d 
	ON p.ScientificName_submitted=n.name_txt and n.tax_id=d.tax_id
	SET p.rankGroup=
	CASE
	WHEN d.rank='class' OR d.rank='family' OR d.rank='infraclass' OR d.rank='infraorder'
	OR d.rank='kingdom' OR d.rank='order' OR d.rank='parvorder' OR d.rank='phylum'
	OR d.rank='subclass' OR d.rank='subfamily' OR d.rank='subkingdom' or d.rank='suborder'
	OR d.rank='subphylum' OR d.rank='subtribe' OR d.rank='superclass' OR d.rank='superfamily' 
	OR d.rank='superkingdom' OR d.rank='superorder' OR d.rank='superphylum' OR d.rank='tribe'
		THEN 'supergeneric'
	WHEN d.rank='genus'
		THEN 'genus'
	WHEN d.rank='subgenus' OR d.rank='species group'
		THEN 'subgeneric'
	WHEN d.rank='forma' OR d.rank='species' OR d.rank='species subgroup' OR d.rank='subspecies'
	OR d.rank='varietas'
		THEN 'species and below'
	WHEN d.rank='no rank'
		THEN 'no rank'
	END
	;
";
$msg_error="error!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// index the newly-populated fields
echo "Indexing...";
$sql="
	alter table `$names_parsed`
	add index `".$names_parsed."_is_parse_error`(is_parse_error),
	add index `".$names_parsed."_nameminusauthor`(nameminusauthor),
	add index `".$names_parsed."_rankgroup`(rankgroup),
	add index `".$names_parsed."_division`(division),
	add index `".$names_parsed."_accepted_tax_id`(accepted_tax_id)
";
$msg_error="failed to index columns in table `$names_parsed`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
