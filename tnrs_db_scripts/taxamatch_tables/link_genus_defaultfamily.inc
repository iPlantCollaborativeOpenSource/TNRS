<?php

// Links genus to default family by populating DEFAULT_FAMILY_ID column
// Core db field name `$fld_default_family` is set in file params.inc

/*
// For testing only
$msg_error = "Failed to clear previous family values!";
$sql = "UPDATE `$tbl_gen` SET DEFAULT_FAMILY_ID=NULL, DEFAULT_FAMILY=NULL;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));
 */

// Populate DEFAULT_FAMILY_ID, DEFAULT_FAMILY for genera linked to families 
// having exactly 1 entry in table `name` (i.e., no homonyms)
// Families with >1 entry are retrieved using the slower left/right index method below
// Families with 0 entries (value in `defaultFamily` field in `name` table does not 
// have its own entry as `scientificName`)
// are more complex, will require adding new family to core db; NOT YET IMPLEMENTED
echo "Populating DEFAULT_FAMILY_ID for genera linked to non-homonym families...";
$msg_error = "Failed to populate DEFAULT_FAMILY_ID for non-homonym families!";

$sql = "UPDATE `$tbl_gen` AS g INNER JOIN name AS g2 INNER JOIN (
	SELECT scientificName AS fam, count(nameID) as records
	FROM name
	WHERE nameRank='family'
	GROUP BY scientificName
	HAVING records=1
	) AS f
	INNER JOIN name AS f2
	ON g.GENUS_ID=g2.nameID AND g2.$fld_default_family=f.fam AND f.fam=f2.scientificName
	SET 
	g.DEFAULT_FAMILY_ID=f2.nameID, g.DEFAULT_FAMILY=f2.scientificName
	WHERE g2.nameRank='genus' AND f2.nameRank='family';
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Mop up remaining unlinked genera using left and right indices
// Slow, but thorough & safe
echo "Linking genera to families via left & right indices...";

// Get ID of source of default classification
// NEEDS IMPROVEMENT!
// Nb. If have >1 source this method may miss some names
// Ideally, should toggle through each source in db (from best to worst) 
// until all names have been indexed
$sql="SELECT sourceID FROM `source` WHERE `sourceName`='$sourceName_primary'";
$sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve 'sourceID' from table `source`");

// Get list of unlinked species and their left and right indices
// Probably don't need the 'NOT NULL' conditions, just being careful...
$sql="
	SELECT g.GENUS_ID, t.rightIndex, t.leftIndex
	FROM `$tbl_gen` g INNER JOIN (
		SELECT n.nameID, rightIndex, leftIndex
		FROM name AS n INNER JOIN classification as c
		ON n.nameID=c.nameID
		WHERE c.sourceID=$sourceID
	) AS t
	ON g.GENUS_ID=t.nameID
	WHERE g.DEFAULT_FAMILY_ID IS NULL AND t.leftIndex IS NOT NULL and t.rightIndex IS NOT NULL;
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
$curr_row=0;

// For each genus, get family
if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$genus_id=$row{'GENUS_ID'};
		$genus_l_ind=$row{'leftIndex'};
		$genus_r_ind=$row{'rightIndex'};
		$curr_row++;
		echo "\r                                                                                ";
		echo "\rLinking genera to families via left & right indices...$curr_row of $num_rows";

		// get ID of genus for this species
		$sql="
			SELECT n.nameID AS familyID, n.scientificName as family
			FROM name AS n INNER JOIN classification AS c
			ON n.nameID=c.nameID
			WHERE c.sourceID=$sourceID AND c.leftIndex<$genus_l_ind AND c.rightIndex>$genus_r_ind AND n.nameRank='family';
		";
		
		$fam_arr=explode(",",sql_get_cols($sql,'familyID,family'));
		$family_id=$fam_arr[0];
		if ($family_id=="") $family_id=NULL;
		$family=$fam_arr[1];
		if ($family=="") $family=NULL;
		// Gets first record only, just in case. Possibly should have more intelligent 
		// way of dealing with case where >1 parent taxon with nameRank='family'
		// ...not sure how likely this is

		// Update species record
		if ($family_id>0) {
			$sql="
				UPDATE `$tbl_gen`
				SET DEFAULT_FAMILY_ID=$family_id, DEFAULT_FAMILY='$family'
				WHERE GENUS_ID=$genus_id;
			";
		}
		$msg_error="Failed to update DEFAULT_FAMILY_ID for GENUS_ID=$genus_id!";
		if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	}
}

echo "\r                                                                                ";
echo "\rLinking genera to families via left & right indices...$curr_row of $num_rows\r\n";

?>
