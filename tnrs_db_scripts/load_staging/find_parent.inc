<?php
// For taxa at species level or below with NULL parentID, 
// attempts to link to parent by parsing taxon name
// For trinomials, if can't find parent, will search for parent of parent (grandparent)
// If grandparent found, will add parent and link to grandparent

// Essentially, this is a hack to fix anomalies in the source data
// If source data are clean, won't need this

// Not certain if works properly for quadrinomials; 
// Trinomails work fine

echo "Updating missing parent taxa...";

// Get taxa with at least one space in name and no parentID
$sql="
SELECT `$id_fld` AS ID, `$name_fld` AS taxon, $rank_fld AS rank 
FROM `$tbl`
WHERE `$parent_id_fld` IS NULL AND `$name_fld` LIKE '% %';
";
//echo "\r\nSQL='$sql'\r\n";
$rows = mysql_query($sql);
$num_rows = mysql_num_rows($rows);

if ($num_rows>0) {
	while ($row=mysql_fetch_array($rows, MYSQL_ASSOC)) {
		$id=$row{'ID'};
		$taxon=$row{'taxon'};
		$child_rank=$row{'rank'};
		$parent='';
		$grandparent='';

		// parse appropriate number of tokens
		if (substr_count($taxon," ")==1) {
			// Species
			$parent=substr($taxon,0,strrpos($taxon,' ')); // parent is first substring
		} else {
			// Trinomials, quadrinomials, also possibly hybrids
			$str1=substr($taxon,0,strrpos($taxon,' '));	// substring minus last token
			$parent=substr($str1,0,strrpos($str1,' '));	// parent is substring prior to last two tokens
			$grandparent=substr($parent,0,strpos($parent,' '));	// parent of parent	
		}
		
		// for testing only
		// populate parent and grandparent strings for this taxon
		$sql_parsed_parents="UPDATE `$tbl` 
			SET 
			`parent`='$parent',
			`grandparent`='$grandparent'
			WHERE `$id_fld`=$id;";
		$msg_error="\r\nFailed to populate parsed parent fields for $name_fld='$taxon'!\r\n";
		if (sql_execute($sql_parsed_parents,FALSE,$echo_on,'',$msg_error));

		// Check number of parent taxon records
		$sql_parent="SELECT `$id_fld` AS parentID  FROM $tbl WHERE `$name_fld`='$parent';";
		$parent_rows=mysql_query($sql_parent);
		$num_parent_rows = mysql_num_rows($parent_rows);
		
		if ($num_parent_rows>0) {
			// Has one or more parents;
			// Get parentID and update for child taxon
		
			if ($num_parent_rows==1) {
				// No homonyms
				$parentID=sql_get_first_result($sql_parent,'parentID');
			} elseif ($num_parent_rows>1) {
				// Try using accepted name
				$sql_accepted_parent="
					SELECT `$id_fld` AS parentID 
					FROM $tbl
					WHERE `$name_fld`='$parent' AND `$acceptance_fld`='A'
					;";
				$parent_rows=mysql_query($sql_accepted_parent);
				$num_parent_rows = mysql_num_rows($parent_rows);
				
				if ($num_parent_rows>=1) {
					// >=1 accepted name
					// Grab first accepted parent name
					$parentID=sql_get_first_result($sql_accepted_parent,'parentID');
				} else {
					// No accepted names, grab first name at random...
					$parentID=sql_get_first_result($sql_parent,'parentID');
				}

			}
			
			// Populate parentID for child taxon
			$sql_update_parentID="UPDATE `$tbl` 
				SET 
				`$parent_id_fld`=$parentID
				WHERE `$id_fld`=$id;";
			$msg_error="\r\nFailed to update parentID for $name_fld='$taxon'!\r\n";
			if (sql_execute($sql_update_parentID,FALSE,$echo_on,'',$msg_error));
		} else {
			// No parent taxon, try grandparent

			$sql_grandparent="SELECT `$id_fld` AS grandparentID  FROM $tbl WHERE `$name_fld`='$grandparent';";

			$grandparent_rows=mysql_query($sql_grandparent);
			$num_grandparent_rows = mysql_num_rows($grandparent_rows);
			$grandparentID=null;
			
			// Get grandparentID
			
			if ($num_grandparent_rows==1) {
				// No homonyms
				$grandparentID=sql_get_first_result($sql_grandparent,'grandparentID');
			} elseif ($num_grandparent_rows>1) {

				// Try using accepted name
				$sql_accepted_grandparent="
					SELECT `$id_fld` AS grandparentID 
					FROM $tbl
					WHERE `$name_fld`='$grandparent' AND `$acceptance_fld`='A'
					;";
				$grandparent_rows=mysql_query($sql_accepted_grandparent);
				$num_grandparent_rows = mysql_num_rows($grandparent_rows);
				
				if ($num_grandparent_rows>=1) {
					// Grab first accepted name
					$grandparentID=sql_get_first_result($sql_accepted_grandparent,'grandparentID');
				} else {
					// No accepted names, grab first name at random
					$grandparentID=sql_get_first_result($sql_grandparent,'grandparentID');
				}



			}

			// Add missing parent plus link to grandparent
			$sql="SELECT MAX(`$id_fld`) AS `max_id` from `$tbl`;";	// Get next nameID
			$next_id = sql_get_first_result($sql,'max_id')+1;
			
			//if ($next_id>0) {
				// determine value for hybrid flag
				if (is_hybrid($parent)) {
					$hybrid_status=1;
				} else {
					$hybrid_status=0;

				}

				// Determine values for parentID and null_parentID flag
				if (is_null($grandparentID)|| is_null($grandparent) || $grandparent='') {
					$grandparentID='NULL';
					$is_null_parent=1;
				} else {
					$is_null_parent=0;
				}

				// Determine rank, if possible
				if (strpos($parent,' ')===false) {
					// Uninomial
					// try inferring from child rank
					if ($child_rank='species') {
						$rank="'genus'";
					} else {
						$rank='NULL';
					}
				} else {
					// Binomial (or lower? not sure)
					if (strpos($parent,' ')==strrpos($parent,' ')) {
						//if only one space, must be species
						$rank="'species'";
					} else {
						// can't be sure of rank if >1 space
						$rank='NULL';
					}
				}
				
				// Use this version for testing
				// add new record
				// This will add record for parent, even if grandparent
				// does not exist
				// In latter case, parentID will be null (ultimately
				// linked to root in later procedure)
				$sql_add_parent="INSERT INTO `$tbl`(
					`$id_fld`,
					`$parent_id_fld`,
					`$name_fld`,
					`$rank_fld`,
					`$is_hybrid`,
					`$null_parent_fld`,
					`parent`,
					`grandparent`
					)
					SELECT
					$next_id,
					$grandparentID,
					'$parent',
					$rank,
					$hybrid_status,
					$is_null_parent,
					'$grandparent',
					'ADDED'
					;
				";
			 

				$msg_error="\r\nFailed to add parent taxon for $name_fld='$taxon'!\r\n";
				if (sql_execute($sql_add_parent,FALSE,$echo_on,'',$msg_error));

				// Now populate parentID for child taxon
				$sql_update_parentID="UPDATE `$tbl` 
					SET 
					`$parent_id_fld`=$next_id
					WHERE `$id_fld`=$id;";
				$msg_error="\r\nFailed to update parentID for $name_fld='$taxon'!\r\n";
				if (sql_execute($sql_update_parentID,FALSE,$echo_on,'',$msg_error));
			//}
		}
	}
}
echo "done\r\n";

?>
