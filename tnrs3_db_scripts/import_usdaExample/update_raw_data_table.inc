<?php

/////////////////////////////////////////////////////////////////
// Purpose:
// (1) Extracts and populates acceptedNameIDs. This needs to be done
// in raw names table to take advantage of USDA accepted name codes
// which are not transferred to staging table.
// (2) Populate parentNameIDs for infraspecific taxa. This needs 
// to be done in raw names table to take advantage of authors
// of higher taxonomic name components; these are provided in 
// USDA download but are not transferred to staging table
// Other parentNameIDs will be populated by generic staging
// table scripts (see prepare_staging/)
/////////////////////////////////////////////////////////////////

echo "Updating acceptance for accepted names..";
$sql="
	UPDATE `$tbl_names_raw`
	SET acceptance='Accepted'
	WHERE SynonymSymbol IS NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

echo "Updating acceptedNameID..";
// This one is easy
// Just use accepted name symbol
// If only they'd been thoughtful enough to include child-parent links...
$sql="
	UPDATE `$tbl_names_raw` syn JOIN `$tbl_names_raw` acc
	ON syn.AcceptedSymbol=acc.AcceptedSymbol
	SET 
	syn.acceptedNameID=acc.nameID,
	syn.acceptance='Synonym'
	WHERE syn.SynonymSymbol IS NOT NULL AND acc.acceptance='Accepted';
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Update parentNameIDs for names with full authorship
// Other names will be handled later while updating the staging table
// But this step needs to be taken care of now to utilize
// the fact that USDA record authorhip of all name components

echo "Extracting parent names and authors for infraspecific taxa:\r\n";

// Populate parent name and author for quadrinomials
echo "  Quadrinomials...";
$sql="
	UPDATE `$tbl_names_raw` 
	SET 
		parentName=TRIM(REPLACE(ScientificName,CONCAT_WS(' ',strSplit(ScientificName,' ',5),strSplit(ScientificName,' ',6)),'')),
		parentAuthor=TrinomialAuthor
	WHERE QuadrinomialAuthor IS NOT NULL
	AND ( 
		(Subspecies IS NOT NULL AND (Variety IS NOT NULL OR Subvariety IS NOT NULL OR Forma IS NOT NULL))
		OR 
		(Variety IS NOT NULL AND (Subvariety IS NOT NULL OR Forma IS NOT NULL)) 
	);
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));
 
// Populate parent name and author for trinomials
echo "  Trinomials...";
$sql="
	UPDATE `$tbl_names_raw` 
	SET 
	parentName=TRIM(REPLACE(ScientificName,
		CONCAT_WS(' ',strSplit(ScientificName,' ',3),strSplit(ScientificName,' ',4)),'')),
	parentAuthor=GeneraBinomialAuthor
	WHERE TrinomialAuthor IS NOT NULL 
	AND QuadrinomialAuthor IS NULL
	AND (
		(Subspecies IS NOT NULL AND Variety IS NULL AND Subvariety IS NULL AND Forma IS NULL)
		OR 
		(Variety IS NOT NULL AND Subspecies IS NULL AND Subvariety IS NULL AND Forma IS NULL)
	);
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

echo "Populating parentNameIDs:\r\n";

// Update parentNameID for quadrinomials
echo "  Quadrinomials...";
$sql="
	UPDATE `$tbl_names_raw` child JOIN `$tbl_names_raw` parent
	ON child.parentName=parent.ScientificName AND child.parentAuthor=parent.TrinomialAuthor
	SET 
		child.parentNameID=parent.nameID
	WHERE child.QuadrinomialAuthor IS NOT NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Update parentNameID for trinomials
echo "  Trinomials...";
$sql="
	UPDATE `$tbl_names_raw` child JOIN `$tbl_names_raw` parent
	ON child.parentName=parent.ScientificName AND child.parentAuthor=parent.GeneraBinomialAuthor
	SET 
		child.parentNameID=parent.nameID
	WHERE child.TrinomialAuthor IS NOT NULL AND child.QuadrinomialAuthor IS NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

// Update parentNameID for genera (link to families)
echo "  Genera...";
$sql="
	UPDATE `$tbl_names_raw` child JOIN `$tbl_names_raw` parent
	ON child.family=parent.ScientificName
	SET child.parentNameID=parent.nameID
	WHERE parent.parentName='Plantae' 
	AND child.Genus IS NOT NULL AND child.Species IS NULL 
	AND child.Subspecies IS NULL AND child.Variety IS NULL 
	AND child.Subvariety IS NULL AND child.Forma IS NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_fail));

?>
