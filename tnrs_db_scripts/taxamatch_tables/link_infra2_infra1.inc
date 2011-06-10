<?php
// Links quadrinomials to trinomials by populating infra_id column

// Populate infra_id with nameIDs of trinomials having exactly 1 entry in table `name` (i.e., no homonyms)
// Infraspecific taxa with >1 entry are retrieved using the slower left/right index method below
// Infraspecific taxa with 0 entries are more complex, will require adding new species to core db; NOT YET IMPLEMENTED
echo "Linking quadrinomials to non-homonym trinomials...";
$msg_error = "error!";

$sql = "UPDATE `$tbl_infra2` AS q INNER JOIN (
	SELECT scientificName AS infrasp, count(nameID) as records
	FROM name
	WHERE nameParts=3 AND (isHybrid=0 OR isHybrid IS NULL)
	GROUP BY scientificName
	HAVING records=1
	) AS t
	INNER JOIN name AS n
	ON q.infra1_orig=t.infrasp AND t.infrasp=n.scientificName
	SET q.infra1_id=n.nameID
	WHERE n.nameParts=3 AND (n.isHybrid=0 OR n.isHybrid IS NULL);
	";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

// Mop up remaining unlinked quadrinomials using left and right indices
// Slow, but thorough & safe
echo "Linking remaining quadrinomials...";

// Get ID of source whose classification we will use (most likely tropicos)
// NEEDS IMPROVEMENT!
// Nb. If have >1 source this method may miss some names
// Ideally, should toggle through each source in db (from best to worst) 
// until all names have been indexed
$sql="SELECT sourceID FROM `source` WHERE `sourceName`='$sourceName_primary'";
$sourceID=sql_get_first_result($sql,'sourceID');
If ($sourceID===false or $sourceID==NULL) die("failed retrieve 'sourceID' from table `source`");

// Get list of unlinked quadrinomial taxa and their left and right indices
// Probably don't need the 'NOT NULL' conditions, just being careful...
$sql="
	SELECT DISTINCT s.infra2_id, t.rightIndex, t.leftIndex
	FROM `$tbl_infra2` s INNER JOIN (
		SELECT n.nameID, rightIndex, leftIndex
		FROM name AS n INNER JOIN classification as c
		ON n.nameID=c.nameID
		WHERE c.sourceID=$sourceID
	) AS t
	ON s.infra2_id=t.nameID
	WHERE s.infra2_id IS NULL AND t.leftIndex IS NOT NULL and t.rightIndex IS NOT NULL;
";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);
$curr_row=0;

// For each quadrinomial, get trinomial
if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$infra2_id=$row{'infra2_id'};
		$l_ind=$row{'leftIndex'};
		$r_ind=$row{'rightIndex'};
		$curr_row++;
		echo "\r                                                                        ";
		echo "\rLinking remaining quadrinomials...$curr_row of $num_rows";

		// get ID of trinomial for this quadrinomial
		$sql="
			SELECT n.nameID AS infra1ID
			FROM name AS n INNER JOIN classification AS c
			ON n.nameID=c.nameID
			WHERE c.sourceID=$sourceID AND c.leftIndex<$l_ind AND c.rightIndex>$r_ind AND n.nameParts=3 AND (n.isHybrid=0 OR n.isHybrid IS NULL);
		";
		//$infra1ID=sql_get_first_result($sql,'infra1ID'); 
		$infra1ID=sql_get_first_result_bynum($sql,0);
		// Gets first record only, just in case. Possibly should have more intelligent 
		// way of dealing with case where >1 parent taxon with nameRank='genus'
		// ...not sure how likely this is

		// Update species record
		if ($infra1ID>0) {
			$sql="
				UPDATE `$tbl_infra2`
				SET infra1_id=$infra1ID
				WHERE infra2_id=$infra2_id;
			";
		}
		$msg_error="Failed to update infra1_id for infra2_id=$infra2_id!";
		if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
	}
}

echo "\r                                                                       ";
echo "\rLinking remaining quadrinomials...$curr_row of $num_rows\r\n";

?>
