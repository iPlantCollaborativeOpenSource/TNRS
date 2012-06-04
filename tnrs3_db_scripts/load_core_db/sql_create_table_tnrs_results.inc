<?php
// creates table holding results of tnrs processing
// of names in this taxonomic source

$sql_create_tables="
CREATE TABLE $tnrs_results (
	`group` integer(11) unsigned default null,
	nameSubmitted varchar(255) not null,
	overallScore decimal(3,2) default null,
	nameMatched varchar(255) default null,
	nameMatchedScore decimal(3,2) default null,     
	nameMatchedAuthor varchar(255) default null,
	nameMatchedUrl varchar(500) default null,
	authorMatched varchar(255) default null,
	authorMatchedScore decimal(3,2) default null, 
	familyMatched varchar(100) default null,
	familyMatchedScore decimal(3,2) default null,
	genusMatched varchar(100) default null,
	genusMatchedScore decimal(3,2) default null,
	speciesMatched varchar(100) default null,
	speciesMatchedScore decimal(3,2) default null,
	infraspecific1Rank varchar(25) default null,
	infraspecific1Matched varchar(100) default null,
	infraspecific1MatchedScore decimal(3,2) default null,
	infraspecific2Rank varchar(25) default null,
	infraspecific2RankMatched varchar(25) default null,   
	infraspecific2RankMatchedScore decimal(3,2) default null,
	annotation varchar(50) default null,
	unmatchedTerms varchar(255) default null,
	selected varchar(25) default null,
	acceptance varchar(25) default null,
	acceptedFamily varchar(100) default null,
	acceptedName varchar(255) default null,
	acceptedNameAuthor varchar(255) default null,   
	acceptedNameUrl varchar(500) default null
) TYPE=MYISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
";


?>
