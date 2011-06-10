<?php
// Links species to genera by populating GENUS_ID column

// Populate GENUS_ID with nameIDs of genera having exactly 1 entry in table `name` (i.e., no homonyms)
// Genera with >1 entry are retrieved using the slower left/right index method below
// Genera with 0 entries (value in `genus` field in `name` table does not have its own entry as `scientificName`)
// are more complex, will require adding new genus to core db; NOT YET IMPLEMENTED
echo "Populating genus nameID using `genus` field of species...";
$msg_error = "Failed to retrieve genus nameID using `genus` field!";

$sql = "UPDATE `$tbl_sp` AS s INNER JOIN (
	SELECT scientificName AS gen, count(nameID) as records
	FROM name
	WHERE nameRank='genus'
	GROUP BY scientificName
	HAVING records=1
	) AS g
	INNER JOIN name AS n
	ON s.GENUS_ORIG=g.gen AND g.gen=n.scientificName
	SET s.GENUS_ID=n.nameID
	WHERE n.nameRank='genus';
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Mop up remaining unlinked species using left and right indices
// Slow, but thorough & safe
echo "Linking species to genera via left and right indices...";

// Get ID of source whose classification we will use (most likely tropicos)
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
	SELECT s.SPECIES_ID, t.rightIndex, t.leftIndex
	FROM `$tbl_sp` s INNER JOIN (
		SELECT n.nameID, rightIndex, leftIndex
		FROM name AS n INNER JOIN classification as c
		ON n.nameID=c.nameID
		WHERE c.sourceID=$sourceID
	) AS t
	ON s.SPECIES_ID=t.nameID
	WHERE s.GENUS_ID IS NULL AND t.leftIndex IS NOT NULL and t.rightIndex IS NOT NULL;
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
$curr_row=0;

// Remove index on GENUS_ID to speed things up 
echo "\r                                                                                ";
echo "\rLinking species to genera via left and right indices...dropping index";
$msg_error = "failed to drop index on `$tbl_sp`.`GENUS_ID`!";
$sql = "ALTER TABLE `$tbl_sp`
	DROP INDEX `splist_GENUS_ID_NDX`
	;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));


// For each species, get genus
if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$species_id=$row{'SPECIES_ID'};
		$species_l_ind=$row{'leftIndex'};
		$species_r_ind=$row{'rightIndex'};
		$curr_row++;
		echo "\r                                                                                ";
		echo "\rLinking species to genera via left and right indices...$curr_row of $num_rows";

		// get ID of genus for this species
		$sql="
			SELECT n.nameID AS genusID
			FROM name AS n INNER JOIN classification AS c
			ON n.nameID=c.nameID
			WHERE c.sourceID=$sourceID AND c.leftIndex<$species_l_ind AND c.rightIndex>$species_r_ind AND n.nameRank='genus';
		";
		$genus_id=sql_get_first_result($sql,'genusID'); 
		// Gets first record only, just in case. Possibly should have more intelligent 
		// way of dealing with case where >1 parent taxon with nameRank='genus'
		// ...not sure how likely this is

		// Update species record
		if ($genus_id>0) {
			$sql="
				UPDATE `$tbl_sp`
				SET GENUS_ID=$genus_id
				WHERE SPECIES_ID=$species_id;
			";
		}
		$msg_error="Failed to update GENUS_ID for SPECIES_ID=$species_id!";
		if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	}
}

echo "\r                                                                                ";
echo "\rLinking species to genera via left and right indices...$curr_row of $num_rows\r\n";

echo "Restoring indexes...";
$msg_error = "failed to restore index `$tbl_sp`.`GENUS_ID`!";
$sql = "ALTER TABLE `$tbl_sp`
	ADD INDEX `splist_GENUS_ID_NDX`(`GENUS_ID`)
	;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

?>
