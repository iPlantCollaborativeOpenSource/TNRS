<?php

/**
 * Install Authority
 *
 */
	ini_set("memory_limit","256M");
	set_time_limit(0);
	
	include_once('./config.php');
	include_once('./classes/class.nearmatch.php');
	include_once('./classes/class.normalize.php');
	require_once('./classes/class.mysqli_database.php');

	$expected=array(
			'name'
		,	'filename'
	);
	
	// Initialize allowed variables
	foreach ($expected as $formvar)
		$$formvar = (isset(${"_$_SERVER[REQUEST_METHOD]"}[$formvar])) ? ${"_$_SERVER[REQUEST_METHOD]"}[$formvar]:NULL;

	// checks for authorities/config file
	if($name != '' && $filename != '' && file_exists('../authorities/' . $filename)) {
		include('../authorities/' . $filename);

		if(count($config)) {
			foreach($config as $key => $value) {
				$$key = $value;
			}

			// database connection
			$db = select_source($table_name);

			// checks for csv file and creates tables with the postfix
			$postfix = '_' . $table_name;
			if(file_exists('../authorities/' . $sourcefile)) {
				createTables($postfix, $db);

				// import data from the csv
				$first = true;
				$sp_index = 1;
				$handle = fopen('../authorities/' . $sourcefile, "r");
				while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
					if($first) {
						$first = false;
						continue;
					}

					$norm = new Normalize();
					$nm = new NearMatch();

					$genus_desc = $data[$genus];
					$gen_length_desc = strlen($genus_desc);

#					$genus_array = json_decode(file_get_contents(TAXAMATCH_URL . '?cmd=normalize&str=' . $genus_desc),true);
#					$search_genus_name_desc = $genus_array['data'];
					$search_genus_name_desc = $norm->normalize( $genus_desc );
					
#					$near_match_genus_array = json_decode(file_get_contents(TAXAMATCH_URL . '?cmd=near_match&str=' . $genus_desc),true);
#					$near_match_genus_desc = $near_match_genus_array['data'];
					$near_match_genus_desc = $nm->near_match( $genus_desc );
					
					$species_desc = $data[$species];
					$sp_length_desc = strlen($species_desc);
					
#					$species_array = json_decode(file_get_contents(TAXAMATCH_URL . '?cmd=normalize&str=' . $species_desc),true);
#					$search_species_name_desc = $species_array['data'];
					$search_species_name_desc = $norm->normalize( $species_desc );
					
#					$near_match_species_array = json_decode(file_get_contents(TAXAMATCH_URL . '?cmd=near_match&str=' . $species_desc),true);
#					$near_match_species_desc = $near_match_species_array['data'];
					$near_match_species_desc = $nm->near_match( $species_desc );

					if ($authority_abbr) {
						$authority_abbr_desc = $data[$authority_abbr];
						$authority_desc = $data[$authority];
						$query = sprintf(" INSERT INTO auth_abbrev%s (`AUTH_ABBR`, `AUTH_FULL`) VALUES ('%s','%s') "
							, mysql_escape_string($postfix)
							, mysql_escape_string($authority_abbr_desc)
							, mysql_escape_string($authority_desc)
							);
						$db->query($query);
					} else {
						$authority_desc = $data[$authority];
					}

					if ($genus_id != -1) {
						$genus_id_desc = $data[$genus_id];
					} else {
						$genus_id_desc = strtolower($genus_desc);
					}
					
					if ( $species_id != -1) {
						$species_id_desc = $data[$species_id];
					} else {
						$species_id_desc = $sp_index++;
					}

					$query = sprintf("INSERT INTO `genlist%s` (`GENUS_ID`, `GENUS`, `AUTHORITY`, `GEN_LENGTH`, `NEAR_MATCH_GENUS`, `SEARCH_GENUS_NAME`) VALUES ('%s', '%s', '%s',%s,'%s','%s') ;"
						, mysql_escape_string($postfix)
						, mysql_escape_string($genus_id_desc)
						, mysql_escape_string($genus_desc)
						, mysql_escape_string($gen_authority_desc)
						, mysql_escape_string($gen_length_desc)
						, mysql_escape_string($near_match_genus_desc)
						, mysql_escape_string($search_genus_name_desc)
						);
#print $query . "<br>";					
					$db->query($query);
	
					$query = sprintf("INSERT INTO `splist%s` (`SPECIES_ID`, `GENUS_ORIG`, `SPECIES`, `GENUS_ID`, `AUTHORITY`, `SP_LENGTH`, `NEAR_MATCH_SPECIES`, `SEARCH_SPECIES_NAME`) VALUES ('%s','%s','%s','%s','%s',%s,'%s','%s') "
						, mysql_escape_string($postfix)
						, mysql_escape_string($species_id_desc)
						, mysql_escape_string($genus_desc)
						, mysql_escape_string($species_desc)
						, mysql_escape_string($genus_id_desc)
						, mysql_escape_string($authority_desc)
						, mysql_escape_string($sp_length_desc)
						, mysql_escape_string($near_match_species_desc)
						, mysql_escape_string($search_species_name_desc)
						);
#print $query . "<br>";					
					$db->query($query);
#if ($i++ == 5) exit();
				}
				fclose($handle);
			} else {
				print "Data file does not exist to load into taxamatch table.";
			}
		}
		
	} else {
		if ($filename == '') {
			print "A mapping file needs to exist in the authorities folder to install the csv file.<br><br>";
		}
		print 'A valid name parameter has to be supplied. Look at your config file in <br/>your authorities array and based on which one you wish to load, add this value after install_authority.php?name={name}<br/>Example may be install_authority.php?name=sample';
	}

	# print completed
	print json_encode( array(success => true) );

	function select_source( $source ) {
		global $authorities;
		$s = $authorities[$source];
		switch( $s['db_type'] ) {
			case 'mysql':
				$connection_string = sprintf("server=%s; database=%s; username=%s; password=%s;", $s['host'], $s['db_name'], $s['username'], $s['pass'] );
				$conn = new MysqliDatabase($connection_string);
				break;
		}
		return( $conn );
	}


	function createTables($postfix,$db) {

		$query = sprintf("CREATE TABLE IF NOT EXISTS `auth_abbrev%s` (
		`AUTH_ABBR` varchar(200) NOT NULL,
		`AUTH_FULL` varchar(200) NOT NULL
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `genlist%s` (
		`GENUS_ID` varchar(150) NOT NULL,
		`GENUS` varchar(50) default NULL,
		`AUTHORITY` varchar(150) default NULL,
		`GEN_LENGTH` int(2) default NULL,
		`NEAR_MATCH_GENUS` varchar(50) default NULL,
		`SEARCH_GENUS_NAME` varchar(50) default NULL,
		PRIMARY KEY (`GENUS_ID`),
		KEY `NEAR_MATCH_GENUS` (`NEAR_MATCH_GENUS`),
		KEY `GEN_LENGTH` (`GEN_LENGTH`),
  	KEY `SEARCH_GENUS_NAME` (`SEARCH_GENUS_NAME`),
  	KEY `GEN_LENGTH_NAME` (`GEN_LENGTH`, `SEARCH_GENUS_NAME`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `genus_id_matches%s` (
		`genus_id` varchar(150) NOT NULL,
		`genus` varchar(50) default NULL,
		`genus_ed` int(11) default NULL,
		`phonetic_flag` char(1) default NULL
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `species_id_matches%s` (
		`species_id` varchar(150) NOT NULL,
		`genus_species` varchar(50) default NULL,
		`genus_ed` int(11) default NULL,
		`species_ed` int(11) default NULL,
		`gen_sp_ed` int(11) default NULL,
		`phonetic_flag` char(1) default NULL
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `splist%s` (
		`SPECIES_ID` varchar(150) NOT NULL,
		`GENUS_ORIG` varchar(50) default NULL,
		`SPECIES` varchar(60) default NULL,
		`GENUS_ID` varchar(150) default NULL,
		`AUTHORITY` varchar(150) default NULL,
		`SP_LENGTH` int(2) default NULL,
		`NEAR_MATCH_SPECIES` varchar(60) default NULL,
		`SEARCH_SPECIES_NAME` varchar(60) default NULL,
		PRIMARY KEY (`SPECIES_ID`),
  	UNIQUE KEY `GENUS_ORIG` (`GENUS_ORIG`, `SPECIES`, `AUTHORITY`),
		KEY `NEAR_MATCH_SPECIES` (`NEAR_MATCH_SPECIES`),
		KEY `SP_LENGTH` (`SP_LENGTH`),
		KEY `GENUS_ID` (`GENUS_ID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		return true;
	}
	

?>