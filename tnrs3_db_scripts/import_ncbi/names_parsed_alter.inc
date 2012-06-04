<?php
// Performs integrity checks, adds columns and indexes table `names_parsed`

// Check name field is unique
Echo "Checking integrity of table $names_parsed:\r\n";
Echo "  Checking duplication in name field...";
$sql="
	select count(*) as total
       	from 
	(
	select ScientificName_submitted, count(*) as tot
	from `$names_parsed`
	group by ScientificName_submitted
	having tot>1
	) as a;
";
$msg_error="Failed query:\r\n$sql\r\n";
if (($dupes=sql_get_first_result($sql,"total"))===false) {
	die($msg_error);
} elseif ($dupes>0) {
	// confirm that duplication is in name field only
	// if other columns contain duplicates then have multiple results for 
	// same name; quit and warn user
	echo "$dupes duplicates found\r\n";
	echo "  Checking different records for same name...";
	$sql="
		select count(*) as total
		from
		(
		  select distinct p.*
			from `$names_parsed` p join
			(
			select ScientificName_submitted, count(*) as tot
			from `$names_parsed`
			group by ScientificName_submitted
			having tot>1
			) as a
			on p.ScientificName_submitted=a.ScientificName_submitted
		) as b
		";
	$msg_error="Failed query:\r\n$sql\r\n";
	if (($unique_dupes=sql_get_first_result($sql,"total"))===false) {
		die($msg_error);
	} elseif ($unique_dupes>$dupes) {
		$r=$unique_dupes-$dupes;
		echo "$r records found!\r\nError: different results for same name in table `$names_parsed`; please check table\r\n";
		die();
	} else {
		// Purge redundant records
		
		echo "0 records found\r\n";
		Echo "  Removing duplicate records...";

		// Make temporary table of unique values of duplicated records
		$sql="drop table if exists dupes_temp;";
		$msg_error="failed to drop previous table dupes_temp!\r\n";
		if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));
		$sql="
			create table dupes_temp
			select *
			from
			(
			  select distinct p.*
				from `$names_parsed` p join
				(
				select ScientificName_submitted, count(*) as tot
				from `$names_parsed`
				group by ScientificName_submitted
				having tot>1
				) as a
				on p.ScientificName_submitted=a.ScientificName_submitted
			) as b
		";
		$msg_error="failed to create table`dupes_temp`!\r\n";
		if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

		// Delete duplicated records from main table
		$sql="
			delete p.*
			from `$names_parsed` p join dupes_temp d
			on p.ScientificName_submitted=d.ScientificName_submitted
		";
		$msg_error="failed to delete duplicate records from table`$names_parsed`!\r\n";
		if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

		// Re-add distinct versions of duplicated records
		$sql="insert into `$names_parsed` select * from dupes_temp;";
		$msg_error="failed to insert distinct records into table`$names_parsed`!\r\n";
		if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

		// clean up
		$sql="drop table if exists dupes_temp;";
		$msg_error="failed to drop temporary table dupes_temp!\r\n";
		if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));		
	}
} else {
	echo $msg_success;
}

// Add new columns
// rank group distinguishes genus and below, above genus and unranked
echo "Adding columns to table $names_parsed...";
$sql="
	ALTER TABLE `$names_parsed`
	ADD COLUMN is_parse_error INTEGER NOT NULL DEFAULT 0,
	ADD COLUMN nameMinusAuthor VARCHAR(255) DEFAULT NULL,
	ADD COLUMN rankGroup VARCHAR(25) DEFAULT NULL,
	ADD COLUMN division VARCHAR(25) DEFAULT NULL,
	ADD COLUMN accepted_tax_id INTEGER DEFAULT NULL
";
$msg_error="failed to add columns to table `$names_parsed`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));

echo "Indexing table $names_parsed...";
// Remove current index
$sql="
	ALTER TABLE `$names_parsed`
	DROP INDEX `".$names_parsed."_ScientificName_submitted`;
";
$msg_error="failed to drop index `".$names_parsed."_ScientificName_submitted` in table `$names_parsed`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,"",$msg_error));

// Add primary key
$sql="
	ALTER TABLE `$names_parsed`
	ADD PRIMARY KEY (ScientificName_submitted);
";
$msg_error="failed to add primary key to table `$names_parsed`!\r\n";
if (sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error));


?>
