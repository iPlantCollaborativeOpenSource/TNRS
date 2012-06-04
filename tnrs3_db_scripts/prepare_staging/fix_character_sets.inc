<?php
// alter params.inc to turn this on
// should convert any improper characters sets  to utf-8
// To save time, works only on the author column, which 
// is the only field likely to contain extended characters

if ($fix_chars=true) {
	echo "  Converting utf8 & ascii codes:\r\n";

	$authorVerbatimFld=$author_fld."_verbatim";

	echo "    Preparing columns...";

	// add verbatim author column
	$sql="
		ALTER TABLE `$tbl_staging`
		ADD COLUMN `$authorVerbatimFld` VARCHAR(100) DEFAULT NULL AFTER `$author_fld`,
		ADD INDEX `".$tbl_staging."_$authorVerbatimFld`(`$authorVerbatimFld`);
	";
	$msg_error="failed to add column `$authorVerbatimFld`";
	if (sql_execute($sql,TRUE,$echo_on,"",$msg_error));
 	
	// populate verbatim author column
	$sql="
		UPDATE `$tbl_staging`
		SET `$authorVerbatimFld`=`$author_fld`;
	";
	$msg_error="failed to populate column `$authorVerbatimFld`";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
 
	///////// Convert utf8 & ascii codes to text ///////

	// Loop through table
	// Select only records not yet scrubbed
	$query="
		SELECT DISTINCT `$authorVerbatimFld`
		FROM $tbl_staging;
	";
	$result=mysql_query($query);
	$num_rows=mysql_numrows($result);
	$currRow=0;

	if ($num_rows>0) {
		while ($row=mysql_fetch_array($result, MYSQL_ASSOC)) {
			$authorVerbatim=$row{$authorVerbatimFld};
			$str=trim($authorVerbatim);
			
			$currRow++;
			echo "  \r                                                                            ";
			echo "  \r    Processing row $currRow of $num_rows";

			// skip records with dangerous punctuation
			if (stristr($str,chr(63)) || stristr($str,chr(34)) || stristr($str,chr(39))) {	// ?,",'
			
			} else {

				// Populate revised author field
				$trans = new Latin1UTF8(); //convert utf8 codes
				$author_utf8Conv=$trans->mixed_to_latin1($str); // convert iso/ascii codes
				$authorScrubbed=iso2text($author_utf8Conv);  
			
				// Update only if value has changed
				if ($authorVerbatim<>$authorScrubbed) {
					$sql="UPDATE `$tbl_staging`
						SET $author_fld='$authorScrubbed'
						WHERE $authorVerbatimFld='$authorVerbatim';
						";
					$msg_error="Failed to update $author_fld for $authorVerbatimFld=$authorVerbatim!";
						if (sql_execute($sql,TRUE,$echo_on,'',$msg_error));
				}
			}
		}
	}
	echo "  \r                                                                            ";
	echo "  \r    Processing row $currRow of $num_rows\r\n";

	echo "    Restoring columns...";
	$sql="
		ALTER TABLE `$tbl_staging` 
		DROP COLUMN `$authorVerbatimFld`,
		DROP INDEX `".$tbl_staging."_$authorVerbatimFld`;
	";
	$msg_error="failed to drop column `".$tbl_staging."_$authorVerbatimFld`";
	if (sql_execute($sql,TRUE,$echo_on,$msg_success,$msg_error));
}

?>
