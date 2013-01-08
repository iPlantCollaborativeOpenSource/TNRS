<?php

/**
 * Install Authority
 *
 */
	set_time_limit(0);
	ini_set("memory_limit","200M");

	include_once('./config.php');
	require_once('./classes/class.mysqli_database.php');
	require_once('./classes/class.taxamatch.php');
	require_once('./classes/class.nearmatch.php');
	require_once('./classes/class.normalize.php');

	$expected=array(
		'name'
	);
	
	// Initialize allowed variables
	foreach ($expected as $formvar)
		$$formvar = (isset(${"_$_SERVER[REQUEST_METHOD]"}[$formvar])) ? ${"_$_SERVER[REQUEST_METHOD]"}[$formvar]:NULL;

	// checks for authorities/config file
	if($name != '' && file_exists('../authorities/' . $name)) {
		include('../authorities/' . $name);
		if(count($config)) {
			foreach($config as $key => $value) {
				$$key = $value;
			}

			// database connection
			$db = select_source($table_name);

			// checks for csv file and creates tables with the postfix
			$postfix = '_' . $table_name;
			if(file_exists('../authorities/' . $filename)) {
				createTables($postfix, $db);

				// import data from the csv
				$handle = fopen('../authorities/' . $filename, "r");
	
				$master = array();
				$genus_query_array = array();
				$author_array = array();
				
				// Skip first row
				$data = fgetcsv($handle, 1000, ",");

				while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
					
					// Handling Auth abbrevations
					if( trim($data[$authority]) != '') {
						$query = sprintf("INSERT INTO auth_abbrev%s (`AUTH_ABBR`, `AUTH_FULL`) VALUES ('%s','%s')"
						, mysql_escape_string($postfix)
						, mysql_escape_string($data[$authority])
						, mysql_escape_string($data[$authority])
						);
						$db->query($query);
						unset($query);
					}

					if(strtolower($data[3]) == 'genus') {
						// Create Genus
						$master[$data[0]] = $data[2];
						$genus_id_desc = $data[$genus_id];
						$genus_desc    = $data[$genus];
						$authority_desc = $data[$authority];
						$genus_length = strlen($genus_desc);

						$norm = new Normalize();
						$nm = new NearMatch();
						$search_genus_name = $norm->normalize( $genus_desc );
						$near_match_genus = $nm->near_match( $genus_desc );
		
						$query = sprintf("INSERT INTO `genlist%s` (`GENUS_ID`, `GENUS`, `AUTHORITY`, `GEN_LENGTH`, `NEAR_MATCH_GENUS`, `SEARCH_GENUS_NAME`) VALUES  ('%s','%s','%s',%s,'%s','%s')"
						, mysql_escape_string($postfix)
						, mysql_escape_string($genus_id_desc)
						, mysql_escape_string($genus_desc)
						, mysql_escape_string($authority_desc)
						, mysql_escape_string($genus_length)
						, mysql_escape_string($near_match_genus)
						, mysql_escape_string($search_genus_name)
						);
						$db->query($query);
					} elseif (trim($data[3]) == '') {

						// Used to slow down the script for shared hosted sites
						usleep(20000);
						
						// Create Species
						$species_id_desc = $data[$species_id];
						$species_desc = $data[$species];
						$species_length = strlen($species_desc);
						$genus_id_desc = $data[1];
						$genus_desc = $master[$data[1]];
						

						$norm = new Normalize();
						$nm = new NearMatch();
						$search_species_name = $norm->normalize( $species_desc );
						$near_match_species = $nm->near_match( $species_desc );
	
						$authority_desc =  $data[$authority];
		
						$query = sprintf("INSERT INTO `splist%s` (`SPECIES_ID`, `GENUS_ORIG`, `SPECIES`, `GENUS_ID`, `AUTHORITY`, `SP_LENGTH`, `NEAR_MATCH_SPECIES`, `SEARCH_SPECIES_NAME`) VALUES ('%s','%s','%s','%s','%s',%s,'%s','%s') "
						, mysql_escape_string($postfix)
						, mysql_escape_string($species_id_desc)
						, mysql_escape_string($genus_desc)
						, mysql_escape_string($species_desc)
						, mysql_escape_string($genus_id_desc)
						, mysql_escape_string($authority_desc)
						, mysql_escape_string($species_length)
						, mysql_escape_string($near_match_species)
						, mysql_escape_string($search_species_name)
						);
						
						$db->query($query);

					}
					unset($query);
					
				}// end while
				fclose($handle);
				
				print round( memory_get_usage() * 0.0009) . "KB - Final Memory Used<br>";
				
			}
		}
		
	} else {
		print ' A valid name parameter has to be supplied.';
	}

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
		`AUTH_FULL` varchar(200) NOT NULL,
	  UNIQUE KEY `AUTH_ABBR` (`AUTH_ABBR`,`AUTH_FULL`)		
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `genlist%s` (
		`GENUS_ID` varchar(150) NOT NULL,
		`GENUS` varchar(50) default NULL,
		`AUTHORITY` varchar(150) default NULL,
		`GEN_LENGTH` int(2) default NULL,
		`NEAR_MATCH_GENUS` varchar(50) default NULL,
		`SEARCH_GENUS_NAME` varchar(50) default NULL,
		KEY `GENUS_ID` (`GENUS_ID`),
  	KEY `GEN_LENGTH` (`GEN_LENGTH`),
	  KEY `NEAR_MATCH_GENUS` (`NEAR_MATCH_GENUS`)
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `genus_id_matches%s` (
		`genus_id` varchar(150) NOT NULL,
		`genus` varchar(50) default NULL,
		`genus_ed` int(11) default NULL,
		`phonetic_flag` char(1) default NULL
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		$query = sprintf("CREATE TABLE IF NOT EXISTS `species_id_matches%s` (
		`species_id` varchar(150) NOT NULL,
		`genus_species` varchar(50) default NULL,
		`genus_ed` int(11) default NULL,
		`species_ed` int(11) default NULL,
		`gen_sp_ed` int(11) default NULL,
		`phonetic_flag` char(1) default NULL
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
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
		 KEY `GENUS_ID` (`GENUS_ID`),
		 KEY `SP_LENGTH` (`SP_LENGTH`),
  	 KEY `NEAR_MATCH_SPECIES` (`NEAR_MATCH_SPECIES`)
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;", mysql_escape_string($postfix));
		$db->query($query);
		
		return true;
	}
	

?>