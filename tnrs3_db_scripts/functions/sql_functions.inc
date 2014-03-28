<?php

function sql_select_file_from_host($host, $user, $pwd, $db,  $sql_select, $outfile, $echo_on) {
	// runs select statement on remote $host and dumps result to text file
	// $outfile can be just file name (write to working directory) or contain path as well
		
	$command = "mysql --user=$user --password=$pwd --database=$db --host=$host -e \"$sql_select\"  > $outfile";
	if ($echo_on) echo $command."\r\n";
	$result = system($command);

	if ($result=="fail") {
		return FALSE;
	} else {
		return TRUE;
	}

}
 
function sql_append_from_file ($host, $user, $pwd, $db, $columns, $infile, $echo_on) {
	// appends records to table in database $db on $host from local text file $infile
	// Appends to table with same name as text file, minus the extension
	
	$command = "mysqlimport --replace --force --local --compress --ignore-lines=1 --user=$user --password=$pwd --host=$host --fields-optionally-enclosed-by=\"\'\" --columns=$columns $db $infile";
	if ($echo_on) {
		echo "\r\n\r\n$command\r\n\r\n";
	}
	$result = system($command);

	if ($result=="fail") {
		return FALSE;
	} else {
		return TRUE;
	}

}

function sql_append_from_file_local ($user, $pwd, $db, $columns, $infile, $echo_on) {
	// appends records to table in database $db on $host from local text file $infile
	// Appends to table with same name as text file, minus the extension
	
	$command = "mysqlimport --replace --force --local --compress --ignore-lines=1 --user=$user --password=$pwd --columns=$columns $db $infile";
	if ($echo_on) {
		echo "\r\n\r\n$command\r\n\r\n";
	}
	if (system($command)===false) {
		return false;
	} else {
		return true;
	}

}

function get_remote_table($tbl, $host1, $user1, $pwd1, $db1, $user2, $pwd2, $db2,$echo_this) {
	// copies table from db1 on host1 to db2 on localhost.
	// New table gets same name.
	// Will fail if tbl already exists in db2.
	$command = "mysqldump --opt --host=$host1 --user=$user1 --password=$pwd1 -B $db1 --tables $tbl | mysql --compress --user=$user2 --password=$pwd2 $db2";
	if ($echo_this) {
		echo "\r\n\r\n$command\r\n\r\n";
	}
	$result = system($command);

	if ($result=="fail") {
		return FALSE;
	} else {
		return TRUE;
	}
}

function copy_table_remote ($tbl, $host1, $user1, $pwd1, $db1, $host2, $user2, $pwd2, $db2,$echo_this) {
	// copies table from db1 on host1 to db2 on host2.
	// New table gets same name.
	// Will fail if tbl already exists in db2.
	$command = "mysqldump --opt --host=$host1 --user=$user1 --password=$pwd1 -B $db1 --tables $tbl | mysql --compress --host=$host2 --user=$user2 --password=$pwd2 $db2";
	if ($echo_this) {
		echo "\r\n\r\n$command\r\n\r\n";
	}
	$result = system($command);

	if ($result=="fail") {
		return FALSE;
	} else {
		return TRUE;
	}
}

function send_table($tbl, $local_user, $local_pwd, $local_db, $remote_host, $remote_user, $remote_pwd, $remote_db, $echo_this) {
	// copies table from db1 on localhost to db2 on (remote) host2
	// New table gets same name.
	// Will fail if tbl already exists in db2, so need to check separately
	$command = "mysqldump --opt --user=$local_user --password=$local_pwd -B $local_db --tables $tbl | mysql --compress --host=$remote_host --user=$remote_user --password=$remote_pwd $remote_db";
	if ($echo_this) {
		echo "\r\n\r\n$command\r\n\r\n";
	}
	$result = system($command);

	if ($result=="fail") {
		return FALSE;
	} else {
		return TRUE;
	}
}

function db_connect($host,$user,$pwd,$db,$echo_on) {
	// Opens connection to $db on $host
	// If fails returns FALSE, else returns db host
	// The 4th and 5th parameters enable use of LOCAL option
	// in LOAD DATA INFILE command (workaround of MySQL bug)

	// connect to database server; $HOST as specified in config.inc
	if (!($dbh = mysql_connect($host,$user,$pwd,FALSE,128))) {
		if ($echo_on) {
			echo "\r\nCould not connect to MySQL on $host.\r\n";
			return FALSE;
		}
	}

	// connect to db
	if (!mysql_select_db($db,$dbh)) {
		if ($echo_on) echo "\r\nCould not connect to '$db' database.\r\n";
		return FALSE;
	}
	
	// success! Return the $dbh so main script cal shut down connection when needed
	return $dbh;
}

function copy_tbl($src_tbl, $dest_tbl, $src_db, $dest_db) {
// copies tbl from one database to another, on same host
// Assumes host and both db connections have already been established
// WARNING: will delete original version of table $dest_tbl in $dest_db if it exists!

	$sql="DROP TABLE IF EXISTS $dest_db.$dest_tbl;";
	if (!mysql_query($sql)) {
		//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
		Return FALSE; 
	}

	$sql = "CREATE TABLE $dest_db.$dest_tbl LIKE $src_db.$src_tbl;";
	if (!mysql_query($sql)) {
		//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
		Return FALSE; 
	}

	$sql = "INSERT INTO $dest_db.$dest_tbl SELECT * FROM $src_db.$src_tbl;";
	if (!mysql_query($sql)) {
		//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
		Return FALSE; 
	}
	
	Return TRUE;
}

function copy_tbl_warn($src_tbl, $dest_tbl, $src_db, $dest_db, $warn) {
// copies tbl from one database to another, on same host
// Assumes host and both db connections have already been established
// Does not delete original table in $src_db
// This 'safe' version asks for confirmation before deleting 
// original version of table $dest_tbl in $dest_db
// Requires: exists_table()

	if (exists_table_in_db($dest_tbl,$dest_db)) {
		// Delete it first

		$msg = "\r\nTable `$dest_tbl` already exists in database `$dest_db`! Replace it? (Y/N):";
		fwrite(STDOUT, $msg);
		$response = trim(fgets(STDIN));

		if (!(strtolower($response)=="y"||strtolower($response)=="yes")) {
			Return FALSE;	
		} else {
			echo "\r\n";
		}

		$sql="DROP TABLE $dest_db.$dest_tbl;";
		if (!mysql_query($sql)) {
			//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
			Return FALSE; 
		}
	}

	$sql = "CREATE TABLE $dest_db.$dest_tbl LIKE $src_db.$src_tbl;";
	if (!mysql_query($sql)) {
		//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
		Return FALSE; 
	}

	$sql = "INSERT INTO $dest_db.$dest_tbl SELECT * FROM $src_db.$src_tbl;";
	if (!mysql_query($sql)) {
		//Return "ERROR: Offending SQL: \r\n$sql\r\n".mysql_error()."\r\n";
		Return FALSE; 
	}
	
	Return TRUE;
}


function move_tbl($src_tbl, $dest_tbl, $src_db, $dest_db) {
// move tbl from one database to another, on same host
// Assumes host and both db connections have already been established
// WARNING: will delete original version of table $dest_tbl if it already exists!
// WARNING: deletes $src_tbl in $src_db

	if (!mysql_query("DROP TABLE IF EXISTS $dest_db.$dest_tbl;")) {
		Return FALSE; 
	}

	$SQL_CREATE = "CREATE TABLE $dest_db.$dest_tbl LIKE $src_db.$src_tbl;";

	if (!mysql_query($SQL_CREATE)) {
		Return FALSE; 
	}

	$SQL_INSERT = "INSERT INTO $dest_db.$dest_tbl SELECT * FROM $src_db.$src_tbl;";

	if (!mysql_query($SQL_INSERT)) {
		Return FALSE; 
	}

	if (!mysql_query("DROP TABLE IF EXISTS $src_db.$src_tbl;")) {
		Return FALSE; 
	}	
	
	Return TRUE;
}


function delete_tbl($tbl, $db) {
	// Deletes table $tbl in database $db, if it exists
	// Note that returns TRUE even if table doesn't exist
	// Assumes host and db connections have been established
	// Database name must be included; avoids accidentally deleting
	// table with the same name in wrong database

	if (!mysql_query("DROP TABLE IF EXISTS $db.$tbl;")) {
		Return FALSE; 
	} else {
		Return TRUE;
	}
}

function sql_add_field($tbl,$fldname,$fldtype) {
	// Adds column $fldname to $tbl
	// $fldtype must be a valid mysql data type specification
	// Assumes host and db connections have been established

	$SQL = "ALTER TABLE $tbl ADD COLUMN $fldname $fldtype;";
	
	if (!mysql_query($SQL)) {
		Return FALSE; 
	} else {
		Return TRUE;
	}	
}

function sql_add_indexed_field($tbl,$fldname,$fldtype) {
	// Adds indexed column $fldname to $tbl
	// $fldtype must be a valid & complete mysql column specification
	// Will drop index if it already exists
	// However, assumes field does not already exist
	// Be sure to check first if not sure

	$indname=$tbl."_".$fldname;
	// the following will drop index if it exists:
	drop_index($tbl,$indname);
	$SQL = "
		ALTER TABLE $tbl 
		ADD COLUMN $fldname $fldtype,
		ADD INDEX $indname($fldname)
		;
	";
	
	if (!mysql_query($SQL)) {
		Return false; 
	} else {
		Return true;
	}
}

function sql_remove_field($tbl,$fldname) {
	// Remove column $fldname from $tbl
	// Assumes host and db connections have been established

	$SQL = "ALTER TABLE $tbl DROP COLUMN $fldname;";
	
	if (!mysql_query($SQL)) {
		Return FALSE; 
	} else {
		Return TRUE;
	}	
}

function exists_table($tbl) {
	// Returns TRUE if table $tbl exists in current db
	// Requires existing connection to db
	$sql="SHOW TABLES LIKE '$tbl';";
	$tables = (mysql_query($sql));
	$num_rows = mysql_num_rows($tables);
	if ($num_rows < 1) {
		return FALSE;
	} else {
		return TRUE;
	}
}

function exists_table_in_db($tbl,$db) {
	// Returns TRUE if table exists in current $db 
	// Requires existing connection to this server
	$sql="SHOW TABLES IN `$db` LIKE '$tbl';";
	$tables = (mysql_query($sql));
	$num_rows = mysql_num_rows($tables);
	if ($num_rows < 1) {
		return FALSE;
	} else {
		return TRUE;
	}
}		

function sql_exists_column($tbl,$column) {
	// Returns TRUE if column exists, FALSE if it does not
	if (!exists_table($tbl)) {
		return false;
	} else {
		$cols=sql_field_list($tbl);
		foreach ($cols as $col) {
			if ($column==$col) return true;	
		}
		return false;
	}
}

function exists_index($tbl_name,$index_name) {
	// Returns true if index exists in table,
	// else false
	
	$sql="SHOW INDEX FROM `$tbl_name` WHERE Key_name = '$index_name';";
	$results = (mysql_query($sql));
	$num_rows = mysql_num_rows($results);
	if ($num_rows < 1) {
		return FALSE;
	} else {
		return TRUE;
	}
}

function drop_index($tbl_name,$index_name) {
	// Drop index if it exists
	// Returns true if successful, otherwise false
	// If index does not exist, also returns true
	
	if (exists_index($tbl_name,$index_name)) {
		$sql_drop_index="DROP INDEX $index_name ON $tbl_name;";
		$msg_error="Failed to drop index `$index_name` on table `$tbl_name`!\r\n";
		if (sql_execute($sql,TRUE,$echo_on,'',$msg_error)) return true;
	} else {
		return true;
	}
}

function sql_execute($sql,$die_on_fail,$echo_on,$msg_success,$msg_error) {
	// Executes sql statement
	// Returns TRUE if success
	// Generates success or error message if echo on

	if (!mysql_query($sql)) {
		echo "$msg_error.\r\n
			Offending SQL: '$sql'\r\n
			MYSQL error: ".mysql_error()."\r\n\r\n";
		if ($die_on_fail) {
			die(); 
		} else {
			return FALSE;
		}
	} else {
		if ($echo_on) echo $msg_success;
		return TRUE;
	}
}

function sql_field_type_list($tbl,$delim) {
// Returns a unidimensional array containing the name and type of each field in table $tbl
// Name and type separated by $delim

$fld_list_sql = mysql_query("SHOW COLUMNS FROM $tbl;");

while ($row = mysql_fetch_array($fld_list_sql, MYSQL_ASSOC)) {
	$fld  = $row{'Field'};
	$typ = $row{'Type'};
	$fld_typ[] = $fld . $delim . $typ;
}

return $fld_typ;

}

function sql_field_list($tbl) {
	// Returns a unidimensional array containing the name of each field in table $tbl

	$fld_list_sql = mysql_query("SHOW COLUMNS FROM $tbl;");

	while ($row = mysql_fetch_array($fld_list_sql, MYSQL_ASSOC)) {
		$fld  = $row{'Field'};
		$flds[] = $fld;
	}

	return $flds;

}

function fix_quotes($tbl) {
	// Converts any double quotes to paired single quotes
	// Helps avoid all sorts of nasty errors
	// Returns error message is table doesn't exist
	// If no records, just returns true

	if (exists_table($tbl)===false) return "Failed to fix quotes: table '$tbl' doesn't exist!";
	if (sql_returns_records($tbl)===false) return true;
	
	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=REPLACE(`$fld`,\"\\\"\",\"''\") WHERE `$fld` LIKE \"%\\\"%\";";	
		$msg_fail="Failed to replace double quotes in $tbl.  ";
		if (sql_execute($sql,true,true,'',$msg_fail));
	}
	return true;
}

function replace_double_quotes($tbl,$delim) {
	// Converts any double quotes to $delim
	// Helps avoid all sorts of nasty errors
	// Make sure you chose a nice innocuous delimiter, no special characters
	
	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=REPLACE(`$fld`,\"\\\"\",\"$delim\") WHERE `$fld` LIKE \"%\\\"%\";";	
		$msg_fail="Failed to replace double quotes in $tbl.  ";
		if (sql_execute($sql,true,true,'',$msg_fail));
	}
	return true;
}

function sql_replace($tbl,$find,$replace) {
	// Replace every instance of string $find with string $replace in table $tbl
	// String $find and $replace must not contain any special characters
	// such as double quotes, or funtion will fail!
	// Only works on char-type fields (checks for this).
	// Use carefully, backup first! This will affect entire table!
	// See also function sql_replace_exact()
	
	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=REPLACE(`$fld`,'$find','$replace') WHERE `$fld` LIKE \"%$find%\";";	
		$msg_fail="Failed to replace string '$find' in table '$tbl'.  ";
		if (sql_execute($sql,true,true,'',$msg_fail));
	}
	return true;
}

function sql_replace_exact($tbl,$find,$replace) {
	// For table $tbl, finds every instance of field contents= string '$find'
	// and replaces with '$replace' in table $tbl
	// Strings $find and $replace must not contain any special characters
	// such as double quotes, or funtion will fail!
	// Only works on char-type fields (checks for this).
	// Use carefully, backup first! This will affect entire table!
	// See also function sql_replace()
	
	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=\"$replace\" WHERE  `$fld`=\"$find\";";
		$msg_fail="Failed to replace all instances of '$find' with '$replace' in table `$tbl`!  ";
		if (sql_execute($sql,true,true,'',$msg_fail));
	}
	return true;
}


function convert_utf8($tbl) {
	// converts any non-utf8 data to utf8
	// Also converts any double quotes to paired single quotes
	// prior to doing this, so do not need to call function
	// 'fix_quotes' if use this one
	
	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=REPLACE(`$fld`,\"\\\"\",\"''\") WHERE `$fld` LIKE \"%\\\"%\";";	
		$msg_fail="Failed to replace double quotes in $tbl.  ";
		if (sql_execute($sql,true,true,'',$msg_fail));

		// Toggle through each distinct string in $fld
		$sql = "SELECT DISTINCT `$fld` FROM $tbl ORDER BY `$fld` ASC;";
		$result = mysql_query($sql);

		while ($row = mysql_fetch_assoc($result)) {
			$str = $row{$fld};
		
			//if (!(is_utf8($str))) {
				// convert to utf8
				$str_utf8 = utf8_encode($str);
				// Update all instances of this string in field $fld
				$sql_update_str = "UPDATE $tbl SET `$fld`=\"$str_utf8\" WHERE `$fld`=\"$str\";";
	
				if (!mysql_query($sql_update_str)) {
					$msg_fail = "Failed to convert `$fld`='$str' to utf8.\r\n" . mysql_error() . "\r\n";
					if (sql_execute($sql_update_str,true,true,'',$msg_fail));
				}
			//}
		}
	}

	return true;
}


function empty_string_to_null($tbl) {
	// Converts empty strings to NULL for all columns in table

	if (exists_table($tbl)===false) return "Failed to fix quotes: table '$tbl' doesn't exist!";
	$test_sql="SELECT * FROM `$tbl`;";
	//if (sql_returns_records($test_sql)) return true;
	if (sql_returns_records($test_sql)===false) return true;

	$field_type_list = sql_field_type_list($tbl,"|");

	// make new $fld_list array of char-type fields only
	foreach ($field_type_list as $field_type) {
		$fldtyp = explode("|",$field_type);
		$fld = $fldtyp[0];
		$typ = $fldtyp[1];
		if (strpos($typ,"char")) $fld_list[]=$fld;
	}
	foreach ($fld_list as $fld) {
		$sql = "UPDATE $tbl SET `$fld`=NULL WHERE TRIM(`$fld`)='';";
		$msg_fail="Failed to convert empty strings to NULL in table `$tbl`.  ";
		if (sql_execute($sql,true,true,'',$msg_fail));
	}
	return true;
}

function is_select_query($sql) {
	// Returns true if query is simple select,
	// False if not
	// Also directly detects action queries

	$is_select=false;
	$sql=trim($sql);
	
	if (strpos($sql,"select")==0) $is_select=true;

	if (stristr($sql,"delete from") 
		or stristr($sql,"update") 
		or stristr($sql,"create table") 
		or stristr($sql,"insert into")) $is_select=false;

	return $is_select;
}

function sql_get_first_result($sql,$field_name) {
	// Returns value of $field in first row of $sql
	// Used for cases where you know query will return only one result
	
	if (!($result=mysql_query($sql))) {
		return false;
	} else {
		$num_rows = mysql_num_rows($result);
		if ($num_rows==0) {
			return NULL;
		} else {
			while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
				$first_result  = $row{$field_name};
				return $first_result;
			}
		}
	}
}

function sql_get_first_result_bynum($sql,$col) {
	// returns column $col of first row of result of $sql
	// $col must be an integer; starts at 0
	$result = mysql_query($sql);
	if (!$result) {
	    echo 'Could not run query: ' . mysql_error();
	    return false;
	}
	$row = mysql_fetch_row($result);
	return $row[$col];
}

function sql_returns_records($sql) {
	// returns true if sql returns records, else false
	$result = mysql_query($sql);
	if (!$result) {
	    die ('Could not run query: ' . mysql_error() . '\r\n');
	}
	$num_rows = mysql_num_rows($result);
	if ($num_rows>0){
		return true;
	}else{
		return false;
	}
}

function sql_get_cols ($sql,$field_names) {
	// Returns values of in first row of result of $sql
	// for columns as specified in comma-delimited array $field_names
	// Values concatenated as single string delimited by commas

	$delim=",";

	if (!($result=mysql_query($sql))) {
		return false;
	} else {
		$num_rows = mysql_num_rows($result);
		if ($num_rows==0) {
			return null;
		} elseif (is_null($field_names)) {
			return null;
		} else {
			while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
				$colnames=explode("$delim",$field_names);
				foreach($colnames as $key => $colname) {
					$value=trim($row{$colname}); // strip surrounding whitespace from column names
					$results  = $results.$delim.$value;
				}
				// Trim initial delimiter
				if (strpos($results,$delim)===0) $results=preg_replace("/$delim/","",$results,1);
				return $results;
			}
		}
	}
}


function sql_get_results($sql,$field_name,$delim) {
	// Returns a delimited string containing all values in column $fld
	// returned by query $sql, separated by delimiter $delim

	if ($delim==null) $delim=",";	// Comma is default delimiter
	if (!($result=mysql_query($sql))) {
		die("Error - Offending SQL:\r\n$sql\r\n".mysql_error()."\r\n");
	} else {
		$num_rows = mysql_num_rows($result);
		if ($num_rows==0) {
			return NULL;
		} else {
			while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
				$results  = $results.$delim.$row{$field_name};
			}
			return $results;
		}
	}
}

function is_utf8($str) {
    // returns TRUE is string is valid utf8
    $c=0; $b=0;
    $bits=0;
    $len=strlen($str);
    for($i=0; $i<$len; $i++){
        $c=ord($str[$i]);
        if($c > 128){
            if(($c >= 254)) return false;
            elseif($c >= 252) $bits=6;
            elseif($c >= 248) $bits=5;
            elseif($c >= 240) $bits=4;
            elseif($c >= 224) $bits=3;
            elseif($c >= 192) $bits=2;
            else return false;
            if(($i+$bits) > $len) return false;
            while($bits > 1){
                $i++;
                $b=ord($str[$i]);
                if($b < 128 || $b > 191) return false;
                $bits--;
            }
        }
    }
    return true;
}

function alter_collation_utf8($tbl) {
	// get table collation
	if(!($collation=get_collation($tbl))) die("Could not retrieve collation for table '$tbl'\r\n");

	// if not utf8, convert
	if (!(stristr($collation, "utf8"))) {
		$sql = "ALTER TABLE `$tbl` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;";

		if ($result=mysql_query($sql)) {
			return 1;
		} else {
			return 0;
		}
	} else {
		return 1;
	}
}

function get_collation($tbl) {
	// returns collation of $tbl
	$sql = "SHOW TABLE STATUS LIKE '$tbl';";
	if($collation=sql_get_first_result($sql,'Collation')) {
		return $collation;
	} else {
		return false;
		//die("ERROR!!!");
	}
}

function rename_tbl($tbl1,$tbl2) {
	// Renames table from $tbl1 to $tbl2
	// Checks that $tbl1 exists and $tbl2 does not exist
	// If either condition is not satisfied 
	// echos error message and returns FALSE
	// Otherwise completes operation and returns TRUE

	error_reporting(E_ALL ^ E_NOTICE);	
	If (!exists_table($tbl1)) {
		$error_msg = "Error: Can't rename '$tbl1' to '$tbl2'. Table '$tbl1' does not exist.\r\n";
		echo $error_msg;
		return false;
	} elseif (exists_table($tbl2)) {
		$error_msg = "Error: Can't rename '$tbl1' to '$tbl2'. Table '$tbl2' already exists.\r\n";
		echo $error_msg;
		return false;
	} else {
		$sql = "RENAME TABLE `$tbl1` TO `$tbl2`;";
		if (!mysql_query($sql)) {
			$error_msg = "Error: Failed to rename '$tbl1' to '$tbl2'.\r\nOffending SQL: $sql.\r\n";
			echo $error_msg;
			return false;
		} else {
			return true;
		}
	}
}

function mysql_date($y,$m,$d,$y_min) {
	// Converts integer values for year, month and day
	// into mysql-formatted date string
	// returns NULL if year is null or invalid
	// returns valid year but unknown month or day if either of last two are invalid
	// Note: if $y_min specified, rejects dates older than $y_min
	// otherwise rejects dates older than 1000 AD

	if (is_null($y)) return NULL;

	// get integer values of current year, month, day
	$y_curr = (int) date('Y');
	$m_curr = (int) date('n');
	$d_curr = (int) date('j');

	// check if have valid value of y_min, otherwise assume 1600
	if (is_null($y_min)) {
		$y_min=1000;
	} elseif (is_int($y_min)) {
		if (!($y_min>1000 && $y_min<$y_curr)) $y_min=1000;
	} else {
		$y_min=1000;
	}

	// check each date component,
	// adding leading zeros as needed

	// year	
	if (strlen($y)<2 || strlen($y)>4 || strlen($y)==3) {
		// bad year value
		return NULL;
	} elseif (is_liberal_int($y)) {
		// it's an integer

		$y_int=(int)$y;
		$y_curr_2digit = (int) date('y');
		
		// 4-digit year must be no earlier than $y_min and no later than current year
		if ($y_int>=$y_min && $y_int<=$y_curr) {
			// valid 4-digit year
			// cast as string
			$y=(string)$y;

		} elseif (strlen($y)==2 && $y_int<=99 && $y_int>=0) {
			// 2-digit year
			// assume 1900s if > current 2-digit year,
			// else assume 2000s

			if ($y_int<=$y_curr_2digit) {
				$y='20'.(string)$y;
			} else {
				$y='19'.(string)$y;
			}

		} else {
			// not an integer; invalid year
			return NULL;
		}

	} else {
		// invalid year 
		return NULL;
	}

	// month	
	if (is_null($m)) {
		// NULL is OK; use zeros, =unknown month
		$m = '00';
	} elseif (is_liberal_int($m)) {
		// it's an integer

		$m_int=(int)$m;
		
		if ($m_int==0 || strlen($m)>2) {
			// unknown month
			$m = '00';
		} elseif ($m_int>0 && $m_int<10) {
			// valid single-digit month
			// Add leading zero if needed and cast as string
			$m = '0'.$m_int;

		} elseif ($m_int>=10 && $m_int<=12) {
			// valid 2-digit month
			// cast as string
			$m = (string)$m_int;
		} else {
			// invalid month; set to unknown
			$m='00';
		}

	} else {
		// invalid month; set to unknown
		$m='00';
	}

	// day	
	if (is_null($d)) {
		// NULL is OK; use zeros, =unknown day
		$d = '00';
	} elseif (is_liberal_int($d)) {
		// it's an integer

		$d_int=(int)$d;
		
		if ($d_int==0 || $m=='00' || strlen($d)>2) {
			// unknown day
			$d = '00';
		} elseif ($d_int>0 && $d_int<10) {
			// valid single-digit day
			// Add leading zero and cast as string
			$d = '0'.$d_int;
		} elseif ($d_int>=10 && $d_int<=31) {
			// valid 2-digit day
			// check that value is correct for month
			// cast as string
			$shortmonths = array(2,4,6,9,11);	// includes Feb; don't deal with leap years for now
			
			if ($d_int>30 && (in_array($m_int,$shortmonths))) {
				// day invalid for given month; treat as unknown
				$d = '00';
			} elseif ($d_int<10) {
				// single-digit day
				// add leading zero and cast as string
				$d = '0'.$d_int;
			} else {
				$d = (string)$d_int;
			}
		} else {
			// invalid day; set to unknown
			$d = '00';
		}

	} else {
		// invalid day; set to unknown
		$d = '00';
	}

	// Final check for current year with month beyond current month
	// Set back to previous century
	if ($y==$y_curr && ($m_int>$m_curr)) $y='19'.substr($y,-2);

	// Compose mysql date
	$thedate = $y.'-'.$m.'-'.$d;

	return $thedate;
}

function is_liberal_int($int){
	// Returns true either for numeric integers or 
	// strings that can be interpreted as integers,
	// including leading zeros
       
        // First check if it's a numeric value as either a string or number
        if(is_numeric($int) === TRUE){
           
            // It's a number, but it has to be an integer
            if((int)$int == $int){

                return TRUE;
               
            // It's a number, but not an integer, so we fail
            }else{
           
                return FALSE;
            }
       
        // Not a number
        }else{
       
            return FALSE;
        }
}

function mon_num($mon_str) {
	// Converts string months to numeric months

	if (is_null($mon_str) || $mon_str=='') {
		return null;
	}
	
	$mon_str=strtolower($mon_str);

	if ($mon_str=="jan" || $mon_str=="jan." || $mon_str=="january") {
		return 1;
	} elseif ($mon_str=="feb" || $mon_str=="feb." || $mon_str=="february") {
		return 2;
	} elseif ($mon_str=="mar" || $mon_str=="mar." || $mon_str=="March") {
		return 3;
	} elseif ($mon_str=="apr" || $mon_str=="apr." || $mon_str=="april") {
		return 4;
	} elseif ($mon_str=="may") {
		return 5;
	} elseif ($mon_str=="jun" || $mon_str=="jun." || $mon_str=="june") {
		return 6;
	} elseif ($mon_str=="jul" || $mon_str=="jul." || $mon_str=="july") {
		return 7;
	} elseif ($mon_str=="aug" || $mon_str=="aug." || $mon_str=="august") {
		return 8;
	} elseif ($mon_str=="sep" || $mon_str=="sep." || $mon_str=="september") {
		return 9;
	} elseif ($mon_str=="oct" || $mon_str=="oct." || $mon_str=="october") {
		return 10;
	} elseif ($mon_str=="nov" || $mon_str=="nov." || $mon_str=="november") {
		return 11;
	} elseif ($mon_str=="dec" || $mon_str=="dec." || $mon_str=="december") {
		return 12;
	} else {
		return NULL;
	}
}

function create_tables($create_sql,$drop_sql,$USER,$PWD,$DB){
	// executes drop then create sql scripts 
	// (actually, will execute any two sql scripts)
	// Note that a return value of 1=fail for the system function; 0=success

	$command = "mysql -u $USER --password=$PWD $DB < $drop_sql";
	$result=system($command,$retval);
	if ($retval==1) die("\r\nFailed to drop tables!\r\n");
	
	$command = "mysql -u $USER --password=$PWD $DB < $create_sql";
	$results=system($command,$retval);
	if ($retval==1) die("\r\nFailed to create tables!\r\n");

	return true;
}

/* Next version is better, but keep for now
function sql_execute_multiple_bak($sqlmultiple) {
	// executes one or more sql queries by exploding on ";"
	$queries = explode(';', $sqlmultiple);
	foreach($queries as $sql){
		$sql=$sql.";";
		mysql_query($sql);
	}
}
*/

function sql_execute_multiple($sqlmultiple) {
	// executes one or more sql queries by exploding on ";"
	// if any query fails, dies and reports error
	// Note that there no need to wrap this in an error handler; it 
	// automatically dies and report error when any query fails
	$queries = explode(';', $sqlmultiple);
	foreach($queries as $sql){
		$sql=trim($sql).";";
		
		if (trim($sql<>";")) {
			$result = mysql_query($sql);
			if (!$result) {
				$msg = 'Invalid query: ' . mysql_error() . "\n";
				$msg .= 'Offending SQL: ' . $sql;
				die($msg);
			} 
		}
	}
}

?>
