<?php
// Links trinomials to species by populating species_id column

echo "Setting species_id=NULL...";
$msg_error = "error!";
$sql = "UPDATE `$tbl_infra1` 
	SET species_id=NULL;
";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Populate species_id with nameIDs of species having exactly 1 entry in table `name` (i.e., no homonyms)
// Species with >1 entry are retrieved using the slower left/right index method below
// Species with 0 entries (value in `species` field in `name` table does not have its own entry as `scientificName`)
// are more complex, will require adding new species to core db; NOT YET IMPLEMENTED
echo "Linking trinomials to non-homonym species...";
$msg_error = "error!";

$sql = "UPDATE `$tbl_infra1` AS i INNER JOIN (
	SELECT scientificName AS sp, count(nameID) as records
	FROM name
	WHERE nameRank='species'
	GROUP BY scientificName
	HAVING records=1
	) AS s
	INNER JOIN name AS n
	ON i.species_orig=s.sp AND s.sp=n.scientificName
	SET i.species_id=n.nameID
	WHERE n.nameRank='species';
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Mop up remaining unlinked trinomials using left and right indices
// Slow, but thorough & safe
echo "Linking remaining trinomials...";

// Get ID of source whose classification we will use (most likely tropicos)
// NEEDS IMPROVEMENT!
// Nb. If have >1 source this method may miss some names
// Ideally, should toggle through each source in db (from best to worst) 
// until all names have been indexed
$sql="SELECT sourceID FROM `source` WHERE `sourceName`='$sourceName_primary'";
$sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve 'sourceID' from table `source`");

// Get list of unlinked trinomial taxa and their left and right indices
// Probably don't need the 'NOT NULL' conditions, just being careful...
$sql="
	SELECT DISTINCT s.infra1_id, t.rightIndex, t.leftIndex
	FROM `$tbl_infra1` s INNER JOIN (
		SELECT n.nameID, rightIndex, leftIndex
		FROM name AS n INNER JOIN classification as c
		ON n.nameID=c.nameID
		WHERE c.sourceID=$sourceID
	) AS t
	ON s.infra1_id=t.nameID
	WHERE s.species_id IS NULL AND t.leftIndex IS NOT NULL and t.rightIndex IS NOT NULL;
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
$curr_row=0;

// Remove index on species_id to speed things up 
echo "\r                                                                       ";
echo "\rLinking remaining trinomials...dropping index";
$msg_error = "failed to drop index `$tbl_infra1`.`speciesd_id`!";
$sql = "ALTER TABLE `$tbl_infra1`
	DROP INDEX `infra1list_species_id`
	;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

// For each trinomial, get species
if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$infra1_id=$row{'infra1_id'};
		$l_ind=$row{'leftIndex'};
		$r_ind=$row{'rightIndex'};
		$curr_row++;
		echo "\r                                                                       ";
		echo "\rLinking remaining trinomials...$curr_row of $num_rows";

		// get ID of species for this trinomial
		$sql="
			SELECT n.nameID AS speciesID
			FROM name AS n INNER JOIN classification AS c
			ON n.nameID=c.nameID
			WHERE c.sourceID=$sourceID AND c.leftIndex<$l_ind AND c.rightIndex>$r_ind AND n.nameRank='species';
		";
		// $speciesID=sql_get_first_result($sql,'speciesID'); 
		$speciesID=sql_get_first_result_bynum($sql,0);

		// Gets first record only, just in case. Possibly should have more intelligent 
		// way of dealing with case where >1 parent taxon with nameRank='genus'
		// ...not sure how likely this is

		// Update species record
		if ($speciesID>0) {
			$sql="
				UPDATE `$tbl_infra1`
				SET species_id=$speciesID
				WHERE infra1_id=$infra1_id;
			";
		}
		$msg_error="Failed to update species_id for infra1_id=$infra1_id!";
		if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	}
}

echo "\r                                                                                                   ";
echo "\rLinking remaining trinomials...$curr_row of $num_rows\r\n";

echo "Restoring indexes...";
$msg_error = "failed to restore index `$tbl_infra1`.`speciesd_id`!";
$sql = "ALTER TABLE `$tbl_infra1`
	ADD INDEX `infra1list_species_id`(`species_id`)
	;
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
