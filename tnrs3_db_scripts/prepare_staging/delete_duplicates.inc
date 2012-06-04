<?php

//////////////////////////////////////////////////////////
// Removes duplicate records in staging table
//
// In theory should be simple: if name, author, and rank 
// are same, keep only one. However, if any of
// those names have children, or if the names is an accepted
// name for a synonym, the child-parent or name-acceptedName 
// links will be broken. For now, takes conservative approach 
// of only  deleting duplicates without children
//////////////////////////////////////////////////////////

echo "  Checking for duplicate names...";

$sql="DROP TABLE IF EXISTS duplicateNames;";
$msg_error="Failed to drop table duplicateNames!\r\n";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
$sql="
	CREATE TABLE duplicateNames(
	nameID INTEGER UNSIGNED NOT NULL,
	fullName VARCHAR(255) NOT NULL,
	hasChildren INT(1) DEFAULT 0,
	isAcceptedName  INT(1) DEFAULT 0,
	del INTEGER(1) NOT NULL DEFAULT 1,
	INDEX(nameID),
	INDEX(fullName),
	INDEX(hasChildren),
	INDEX(isAcceptedName),
	INDEX(del)
	);
";
$msg_error="Failed to create table duplicateNames!\r\n";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$sql="
	INSERT INTO duplicateNames(
	nameID,
	fullName
	)
	SELECT s.nameID, 
	CONCAT_WS('@',s.scientificName, IFNULL(s.scientificNameAuthorship,''), IFNULL(s.nameRank,'')) AS fullName
	FROM nameStaging s JOIN 
	(
		SELECT scientificName, scientificNameAuthorship, nameRank,
		COUNT(*) AS records
		FROM nameStaging
		GROUP BY scientificName, scientificNameAuthorship, nameRank
		HAVING records>1
	) AS d
	ON s.scientificName=d.scientificName
	AND s.scientificNameAuthorship=d.scientificNameAuthorship
	AND s.nameRank=d.nameRank;
";
$msg_error="Failed to insert duplicate names!\r\n";
if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

$sql="SELECT COUNT(*) AS recs FROM duplicateNames;";
if (sql_get_first_result($sql,'recs')==0) {
	echo "done\r\n";
} else {
	// check for children
	$sql="
		UPDATE duplicateNames d JOIN nameStaging parent JOIN 
		(
			SELECT DISTINCT parentNameID
			FROM nameStaging
		) AS child
		ON d.nameID=parent.nameID AND parent.nameID=child.parentNameID
		SET d.hasChildren=1;
	";
	$msg_error="Failed to update duplicate names with child taxa!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	
	// check for synonyms linking to this name
	$sql="
		UPDATE duplicateNames d JOIN nameStaging acc JOIN 
		(
			SELECT DISTINCT acceptedNameID
			FROM nameStaging
		) AS syn
		ON d.nameID=acc.nameID AND acc.nameID=syn.acceptedNameID
		SET d.isAcceptedName=1;
	";
	$msg_error="Failed to update duplicate names with child taxa!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

	// remove from list any names with child taxa or synonyms
	$sql="
		DELETE FROM duplicateNames
		WHERE hasChildren=1 OR isAcceptedName=1;
	";
	$msg_error="Failed to remove duplicate names with child taxa!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

	// NOW tag the lowest nameID within each fullName group
	$sql="
		UPDATE duplicateNames d JOIN (
			SELECT fullName, MIN(nameID) as firstNameID
			FROM duplicateNames
			GROUP BY fullName
		) AS d2
		ON d.nameID=d2.firstNameID
		SET d.del=0;	
	";
	$msg_error="Failed to tag first duplicate name in series!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	
	// Need to single names due to deletion of names with children
	$sql="
		UPDATE duplicateNames d JOIN (
			SELECT fullName, COUNT(*) AS tot
			FROM duplicateNames
			GROUP BY fullName
			HAVING tot=1
		) AS d2
		ON d.fullName=d2.fullName
		SET d.del=1;	
	";
	$msg_error="Failed to tag single remaining duplicates!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));	

	// delete names
	$sql="
		DELETE s.*
		FROM nameStaging s JOIN duplicateNames d
		ON s.nameID=d.nameID
		WHERE d.del=1;	
	";
	$msg_error="Failed to delete duplicate names!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));

	// tidy up
	$sql="DROP TABLE IF EXISTS duplicateNames;";
	$msg_error="Failed to drop table duplicateNames!\r\n";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));

}

?>