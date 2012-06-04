<?php

////////////////////////////////////////////////////////////////
// Attempts to recover family of genera added by parsing of 
// species. Most such genera will not yet have a link to a 
// parent family. However, their children may have at least a 
// text family name. This can be used to discover the name ID of 
// the parent family of the genus, as long as the family
// does not have homonyms, or if it has homonyms, only one is 
// accepted. Other families...tough luck.
////////////////////////////////////////////////////////////////

// For taxa missing family, attempt up populate family by linking
// to child taxa. Need to do this several times to catch most extreme case where
// quadrinomial has family, but trinomial, species and genus do not
// 6 iterations allows for plenty of intercolated taxa
$iterations=6;
echo "  Updating missing family by linking to child taxon...";
$sql="
	UPDATE nameStaging parent JOIN nameStaging child
	ON parent.nameID=child.parentNameID
	SET parent.family=child.family
	WHERE parent.family IS NULL AND child.family IS NOT NULL;
";
for ($i=1;$i<=$iterations;$i++) {
	$msg_error="\r\nFailed to populate missing family; iteration=$i!\r\n";
	if ($i<$iterations) {
		(sql_execute($sql,FALSE,$echo_on,'',$msg_error));
	} else {
		(sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_error));
	}
}

// for genera with NULL parentID, use newly-found text family name
// to populate family. The last condition (AND f.parentNameID<>g.nameID) 
// prevents circular links
echo "    Linking genus to family nameID (non-homonym families)...";
$sql="
	UPDATE nameStaging g JOIN nameStaging f JOIN (
		SELECT scientificName, COUNT(*) AS fams
		FROM nameStaging 
		WHERE nameRank='family'
		GROUP BY scientificName
		HAVING fams=1
	) AS f2
	ON g.family=f.scientificName AND f.scientificName=f2.scientificName
	SET g.parentNameID=f.nameID
	WHERE f.nameRank='family' AND g.parentNameID IS NULL AND g.nameRank='genus'
	AND f.parentNameID<>g.nameID;
";
if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_fail));

// Same as above, attempting to mop up remainder by joining to families
// with homonyms, but only one accepted version
echo "    Linking genus to family nameID (accepted families)...";
$sql="
	UPDATE nameStaging g JOIN nameStaging f JOIN (
		SELECT scientificName, COUNT(*) AS fams
		FROM nameStaging 
		WHERE nameRank='family' 
		AND (acceptance='Accepted' OR acceptance='A')
		GROUP BY scientificName
		HAVING fams=1
	) AS f2
	ON g.family=f.scientificName AND f.scientificName=f2.scientificName
	SET g.parentNameID=f.nameID
	WHERE f.nameRank='family' AND g.parentNameID IS NULL AND g.nameRank='genus'
	AND f.parentNameID<>g.nameID;
";
if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_fail));

// ...and finally, a shameless hack
if ($src=="gcc") {
	echo "    Setting family='Asteraceae' for source='gcc'...";
	$sql="
		UPDATE nameStaging 
		SET family='Asteraceae';
	";
	if (sql_execute($sql,FALSE,$echo_on,$msg_success,$msg_fail));
}

?>