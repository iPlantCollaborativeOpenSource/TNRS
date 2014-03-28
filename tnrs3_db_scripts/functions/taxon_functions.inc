<?php
// functions for manipulating and formating taxa
// Includes functions for accessing and building hierarchical
// taxon tables indexed via modified tree traversal

function format_taxon($taxon, $author, $rank) {
	//italicizes latin name if rank of genus or below
	//author can be null
	if ((trim($author))!="") $author = " ".$author; 

	if ($rank=='genus'||$rank=='species'||$rank=='infra1'||$rank=='infra2') {
		$result =  "<i>".$taxon."</i>".$author;
	} else {
		$result =  $taxon.$author;
	}

	return($result);
}

function get_family($taxon_id) {
	//accepts PK of a taxon and returns family, rank is <= family
	// returns message "[>1 family found!]" if >1 family found

	//get attributes of taxon
	$SQL = "SELECT left_index, right_index FROM taxon WHERE taxon_id=$taxon_id;";
	// $SQL = "SELECT taxon_id from taxon WHERE taxon_id<100;";
	//print "SQL=".$SQL."<br />";
	$find_taxon = (mysql_query($SQL));
	//print "taxon_id=".$taxon_id."<br />";
	//print "find_taxon=".$find_taxon."<br />";
	$num_rows = mysql_num_rows($find_taxon);
	//print "num_rows=".$num_rows."<br />";
	if ($num_rows < 1) {
		die("");
	}

	$row = mysql_fetch_row($find_taxon);
	$l_ind = $row[0];
	$r_ind = $row[1];

	if ($l_ind!=null AND $r_ind!=null) {
		$sql="SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = 'family'";
		$find_fam = (mysql_query($sql));
		$num_rows = mysql_num_rows($find_fam);
	
		if ($num_rows > 1) {
			$fam="[>1 family found!]";
		} elseif ($num_rows<1) {
			$fam=NULL;
		} else {
			$row = mysql_fetch_row($find_fam);
			$fam = trim($row[0]);
		}
	} else {
		$fam=NULL;
	}
	
	return($fam);
}

function get_family_by_taxon($taxon) {
	//accepts (text) taxon name and returns family, if rank is <= family
	// returns message "[>1 family found!]" if >1 family found

	//get indices of taxon
	$SQL = "SELECT left_index, right_index FROM taxon WHERE taxon='$taxon';";
	$find_taxon = (mysql_query($SQL));
	$num_rows = mysql_num_rows($find_taxon);
	if ($num_rows < 1) {
		die("");
	}

	$row = mysql_fetch_row($find_taxon);
	$l_ind = $row[0];
	$r_ind = $row[1];

	$find_fam = (mysql_query("SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = 'family'"));
	$num_rows = mysql_num_rows($find_fam);

	if ($num_rows > 1) {
		$fam="[>1 family found!]";
	} elseif ($num_rows<1) {
		$fam=NULL;
	} else {
		$row = mysql_fetch_row($find_fam);
		$fam = trim($row[0]);
	}
	
	return($fam);
}

function get_first_family($taxon_id) {
	// accepts PK of a taxon and returns family, rank is <= family
	// returns 'nearest' family (highest left_index) if >1 family found

	//get left and right indices of taxon
	$SQL = "SELECT left_index, right_index FROM taxon WHERE taxon_id=$taxon_id;";
	$find_taxon = (mysql_query($SQL));
	$num_rows = mysql_num_rows($find_taxon);
	
	if ($num_rows < 1) {
		die("");
	}
	
	$row = mysql_fetch_row($find_taxon);
	$l_ind = $row[0];
	$r_ind = $row[1];

	// get family
	$SQL_find_fam = "SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = 'family';";
	if (!($find_fam = (mysql_query($SQL_find_fam)))) die(); //die( $SQL_find_fam . "\r\n\r\n" . mysql_error());
	$num_rows = mysql_num_rows($find_fam);

	if ($num_rows > 1) {
		$SQL_find_fam = "SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = 'family' ORDER BY left_index ASC;";
		$find_fam = (mysql_query($SQL_find_fam));
		while ($row = mysql_fetch_array($find_fam, MYSQL_ASSOC)) {
			$fam = $row{'taxon'};
		}
		//$fam="[>1 family found!]";
	} elseif ($num_rows<1) {
		$fam=NULL;
	} else {
		$row = mysql_fetch_row($find_fam);
		$fam = $row[0];
	}
	
	return($fam);
}

function get_parent_by_id($taxon_id, $parent_rank, $kingdom) {
	// accepts taxon_id and returns parent taxon (with id=taxon_id) at rank is =parent_rank
	// kingdom parameter circumvents problem of homonyms

	// Get right and left indices of lowest permissible parent (e.g., kingdom)
	$SQL = "SELECT right_index, left_index FROM taxon WHERE taxon='$kingdom';";
	$kingdom_indices = (mysql_query($SQL));
	$num_rows = mysql_num_rows($kingdom_indices);
	//	if ($num_rows!=1) die("Failed to retrieve single pair of indices for kingdom='$kingdom'.\r\nnum_rows=$num_rows\r\n"); 
	if ($num_rows!=1) die();

	while ($row = mysql_fetch_array($kingdom_indices, MYSQL_ASSOC)) {
		$kingdom_l_ind = $row{'left_index'};
		$kingdom_r_ind = $row{'right_index'};
	}
//	echo "kingdom_l_ind=$kingdom_l_ind, kingdom_r_ind=$kingdom_r_ind\r\n";

	//	if (is_null($kingdom_l_ind) OR is_null($kingdom_r_ind)) die("Failed to retrieve kingdom indices.\r\n".mysql_error()."\r\n");
	if (is_null($kingdom_l_ind) OR is_null($kingdom_r_ind)) die();

	// get attributes of taxon
	// kingdom restriction will prevent retrieving >1 taxon if have homonyms in different kingdoms
	$SQL = "SELECT left_index, right_index FROM taxon WHERE taxon_id=$taxon_id AND (left_index>$kingdom_l_ind AND right_index<=$kingdom_r_ind);";
//	echo "SQL='$SQL'\r\n";
	$taxon_indices = (mysql_query($SQL));
	$num_rows = mysql_num_rows($taxon_indices);
	//	if ($num_rows < 1) die("Failed to retrieve indices for taxon=$taxon_id.\r\n".mysql_error()."\r\n");
	if ($num_rows < 1) die();
//	echo "num_rows=$num_rows\r\n";

	// this assumes only one set of index for $taxon_id (i.e., no homonyms within kingdom)
	// if have >taxon_id, will not fail, but will use only indices from first record
	$row = mysql_fetch_row($taxon_indices);
	$l_ind = $row[0];
	$r_ind = $row[1];

	$find_parent = (mysql_query("SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = '$parent_rank'"));
	$num_rows = mysql_num_rows($find_parent);

	if ($num_rows > 1) {
		$parent="[>1 parent taxon found!]";
	} elseif ($num_rows<1) {
		//$parent="NULL";
	} else {
		$row = mysql_fetch_row($find_parent);
		$parent = trim($row[0]);
	}
	
	return($parent);
}


function get_parent($taxon, $parent_rank, $kingdom) {
	// accepts taxon name and returns parent taxon at rank is =parent_rank
	// kingdom parameter circumvents problem of homonyms

	$parent = NULL;

	// Get right and left indices of lowest permissible parent (e.g., kingdom)
	$SQL = "SELECT right_index, left_index FROM taxon WHERE taxon='$kingdom';";
	$kingdom_indices = (mysql_query($SQL));
	$num_rows = mysql_num_rows($kingdom_indices);
	//
	if ($num_rows!=1) die(); 

	while ($row = mysql_fetch_array($kingdom_indices, MYSQL_ASSOC)) {
		$kingdom_l_ind = $row{'left_index'};
		$kingdom_r_ind = $row{'right_index'};
	}
//	echo "kingdom_l_ind=$kingdom_l_ind, kingdom_r_ind=$kingdom_r_ind\r\n";

	//	if (is_null($kingdom_l_ind) OR is_null($kingdom_r_ind)) die("Failed to retrieve kingdom indices.\r\n".mysql_error()."\r\n");
	if (is_null($kingdom_l_ind) OR is_null($kingdom_r_ind)) die();

	// get attributes of taxon
	// kingdom restriction will prevent retrieving >1 taxon if have homonyms in different kingdoms
	$SQL = "SELECT left_index, right_index FROM taxon WHERE taxon='$taxon' AND (left_index>$kingdom_l_ind AND right_index<=$kingdom_r_ind);";
//	echo "SQL='$SQL'\r\n";
	$taxon_indices = (mysql_query($SQL));
	$num_rows = mysql_num_rows($taxon_indices);
	//	if ($num_rows < 1) die("Failed to retrieve indices for taxon=$taxon.\r\n".mysql_error()."\r\n");
	if ($num_rows < 1) die();
//	echo "num_rows=$num_rows\r\n";

	// this assumes only one set of index for $taxon_id (i.e., no homonyms within kingdom)
	// if have >taxon_id, will not fail, but will use only indices from first record
	$row = mysql_fetch_row($taxon_indices);
	$l_ind = $row[0];
	$r_ind = $row[1];

	$find_parent = (mysql_query("SELECT taxon FROM taxon WHERE left_index<$l_ind AND right_index>$r_ind AND rank = '$parent_rank'"));
	$num_rows = mysql_num_rows($find_parent);

	if ($num_rows > 1) {
		$parent="[>1 parent taxon found!]";
	} elseif ($num_rows<1) {
		//$parent=NULL;
	} else {
		$row = mysql_fetch_row($find_parent);
		$parent = trim($row[0]);
	}
	
	return($parent);
}

function format_region($region_id) {
	//accepts PK of a region
	//if region is a country, returns country name in caps
	//if political division with rank = poldiv1, then returns region name, with country name pre-pended, in caps.
	//if political divisino with rank = poldiv2 or below, returns region name, with poldiv1 and country name pre-pended, in caps.
	//otherwise, returns name of region

	//verify poldiv exists before proceeding
	$find_region = (mysql_query("SELECT region_id, region_name, region_rank, l_ind, r_ind, region_label from region WHERE region_id=$region_id"));
	$num_rows = mysql_num_rows($find_region);
	if ($num_rows < 1) {
		die("");
	} 

	//get attributes of region
	$row = mysql_fetch_row($find_region);
	$region_id = $row[0];
	$region = trim($row[1]);
	$rank = $row[2];
	$l_ind = $row[3];
	$r_ind = $row[4];
	$label = $row[5];
	//echo $region_id.", ".$region.", ".$rank."<br>";

	//apply appropriate formatting
	if ($rank=='country') {
		$region_formatted = strtoupper($region);
	} elseif ($rank=='poldiv1') {
		//get country
		$find_country = (mysql_query("SELECT region_name FROM region WHERE l_ind<$l_ind AND r_ind>$r_ind AND region_rank = 'country'"));
		$num_rows = mysql_num_rows($find_country);
		
		if ($num_rows !=1) {
			$region_formatted = strtoupper($region);
		} else {
			$row = mysql_fetch_row($find_country);
			$country = trim($row[0]).", ";
			$region_formatted = strtoupper($country).$region;
		}

	} elseif ($rank=='poldiv2' || $rank=='poldiv3' || $rank=='poldiv4') {
		//get country
		$find_country = (mysql_query("SELECT region_name FROM region WHERE l_ind<$l_ind AND r_ind>$r_ind AND region_rank = 'country'"));
		$num_rows = mysql_num_rows($find_country);	
		if ($num_rows !=1) {
			$country = "";
		} else {
			$row = mysql_fetch_row($find_country);
			$country = trim($row[0]).", ";
		}

		//get poldiv1
		$find_poldiv1 = (mysql_query("SELECT region_name FROM region WHERE l_ind<$l_ind AND r_ind>$r_ind AND region_rank = 'poldiv1'"));
		$num_rows = mysql_num_rows($find_poldiv1);	
		if ($num_rows !=1) {
			$poldiv1 = "";
		} else {
			$row = mysql_fetch_row($find_poldiv1);
			$poldiv1 = trim($row[0]).", ";
		}

		//make formatted region display
		//include label after name of region
		$region_formatted = strtoupper($country).$poldiv1.$region." ".$label;

	} else {
		$region_formatted = $region;
	}

	return($region_formatted);
}

function build_indices_region($parent_id, $left) {
	//populates left and right indices by a modified
	//tree-traversal algorithm
	//database & table hardwired for now

  	// the right value of this node is the left value + 1 
	$right = $left+1; 

	// get all children of this node 
	$result = mysql_query('SELECT region_id FROM region_test WHERE parent_region_id="'.$parent_id.'";'); 
	while ($row = mysql_fetch_array($result)) { 
		// recursive execution of this function for each 
		// child of this node 
		// $right is the current right value, which is 
		// incremented by the rebuild_tree function 
		$right = build_indices_region($row['region_id'], $right); 

		// we've got the left value, and now that we've processed 
		// the children of this node we also know the right value 
		mysql_query('UPDATE region_test SET l_ind='.$left.', r_ind='.$right.' WHERE region_id="'.$parent_id.'";'); 
		echo "updating!<br>";
		
		// return the right value of this node + 1 
		return $right+1; 
	} 
}

function build_indices_taxon($parent_id, $left) {
	//populates left and right indices by a modified
	//tree-traversal algorithm
	//database & table hardwired for now

  	// the right value of this node is the left value + 1 
	$right = $left+1; 

	// get all children of this node 
	$result = mysql_query('SELECT taxon_id FROM taxon_test WHERE parent_taxon_id="'.$parent_id.'";'); 
	while ($row = mysql_fetch_array($result)) { 
		// recursive execution of this function for each child of this node 
		// $right is the current right value, which is 
		// incremented by the rebuild_tree function 
		$right = build_indices_taxon($row['taxon_id'], $right); 

		// we've got the left value, and now that we've processed 
		// the children of this node we also know the right value 
		mysql_query('UPDATE taxon_test SET left_index='.$left.', right_index='.$right.' WHERE taxon_id="'.$parent_id.'";'); 
		echo "updating!<br>";
		
		// return the right value of this node + 1 
		return $right+1; 
	} 
}

function get_index($tbl, $this_id, $ind, $id_name, $parent_id_name, $l_ind_name, $r_ind_name) {
	// Recursively increments left and right indices index via a modified tree-traversal algorithm
	// Initiate by sending id of root and ind=0
	// Sets left index and right index to NULL for all records on initial call (ie., when $ind=0)
	// This enable use of NULL check to detect circular references

	global $get_index_row, $get_index_tot_rows, $get_index_start_id;

	// reset both indices to null for all fields if this if initial call
	if ($ind==0) {
		$sql = "
			UPDATE `$tbl` 
			SET `$l_ind_name`=NULL, `$r_ind_name`=NULL;
		";
		
		if (!(mysql_query($sql))) die("Failed to reset left and right indices for table `$tbl`.\r\n".mysql_error().
			"\r\nOffending SQL: '$sql'\r\n");
		// get total records in table (for status message)
		$sql="
			SELECT `$id_name` FROM `$tbl`;
		";
		$result=mysql_query($sql);
		$get_index_row=1;
		$get_index_tot_rows = mysql_num_rows($result);
		$get_index_start_id=$this_id;
	}

	// display status message
	echo "\r    Indexing row $get_index_row of $get_index_tot_rows.";
	$get_index_row++;

	// perform NULL check for circular reference
	// left index should be NULL for this record
	$sql = "
		SELECT `$id_name` 
		FROM `$tbl` 
		WHERE `$id_name`=$this_id 
		AND (`$l_ind_name` IS NOT NULL);
	";	
	$result=mysql_query($sql);
	$num_rows = mysql_num_rows($result);
	if ($num_rows>0)  die("Error: circular reference at `$id_name`=$this_id.\r\n");

	// increment index and assign to left index
	$ind++;
	$l_ind = $ind;

	// update left index for this node
	$sql = "
		UPDATE 	`$tbl` 
		SET `$l_ind_name`=$l_ind 
		WHERE `$id_name`=$this_id;
	";
	if (!(mysql_query($sql))) die("Failed to update right index for $id_name=$this_id in table `$tbl`.\r\n".mysql_error()."\r\n");

	// check if this node has children
	if (has_children($this_id,$tbl,$id_name,$parent_id_name)) {
		// make array of child ids
		$child_ids = get_children($this_id, $tbl, $id_name, $parent_id_name);

		// request new index value from each child
		foreach ($child_ids as $child_id) {
			$ind = get_index($tbl, $child_id, $ind, $id_name, $parent_id_name, $l_ind_name, $r_ind_name);
		}
	}

	// increment index again and assign to right index
	$ind++;
	$r_ind = $ind;

	// update right index for this node and return new value of index
	$sql = "
		UPDATE 	`$tbl` 
		SET `$r_ind_name`=$r_ind 
		WHERE `$id_name`=$this_id;
	";
	if (!(mysql_query($sql))) die("Failed to update right index for $id_name=$this_id in table `$tbl`.\r\n".mysql_error()."\r\n");

	// If this is final call (back at root) print new line
	// Issue warning if not all records indexed
	if ($this_id==$get_index_start_id) {
		echo "\r\n";
		if ($get_index_row<$get_index_tot_rows) echo "    Warning: indexed rows < total rows in table.\r\n";
	}
	
	return $ind;
}

function get_children($parent_id, $tbl, $id_name, $parent_id_name) {
	// compiles a unidimensional array consisting of PKs of all child records
	// table must contain a recursive FK linking children to parents
	$sql = "
		SELECT `$id_name` AS child_id 
		FROM `$tbl` 
		WHERE `$parent_id_name`=$parent_id;
	";
	if ($result=mysql_query($sql)) {
		$num_rows = mysql_num_rows($result);
		if ($num_rows==0) {
			$children[]=0;
		} else {
			while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
				$children[]  = $row{'child_id'};
			}
		}
	}
	return $children;
}

function has_children($this_id, $tbl, $id_name, $parent_id_name) {
	// returns true if one or more records link to this record via recursive FK
	$sql = "
		SELECT `$id_name` 
		FROM `$tbl` 
		WHERE `$parent_id_name`=$this_id;
	";
	if ($result=mysql_query($sql)) {
		$num_rows = mysql_num_rows($result);
		if ($num_rows>0) {
			return true;
		} else {
			return false;
		}
	} else {
		return false;
	}
}

function is_hybrid($taxon) {
	// returns true if taxon appears to be hybrid name, else false
	// only works if hybrid x is standard alphabetic 'x', 
	// not multiplication 'x'

	$str=strtolower($taxon);

	// search for internal or initial hybrid x
	if(stristr($str, ' x ')<>false) {
		return true;
	} elseif (substr($str,0,2)=='x ') {
		return true;
	} else {
		return false;
	}
}

function stdRankIndicator($str) {
	// Transforms rank indicator in taxon names
	// to standarized abbreviated form
	$arr=array(
		'agsp'=>'agsp.',
		'agsp.'=>'agsp.',
		'convar.'=>'convar.',
		'convar'=>'convar.',
		'cult.'=>'cv.',
		'cult'=>'cv.',
		'cultivar'=>'cv.',
		'cv'=>'cv.',
		'cv..'=>'cv.',
		'cv'=>'cv.',
		'fo.'=>'fo.',
		'fo'=>'fo.',
		'f.'=>'fo.',
		'forma'=>'fo.',
		'grex'=>'grex',
		'lusus'=>'lusus',
		'monstr'=>'monstr.',
		'nohtosubsp'=>'nothosubsp.',
		'nothogen'=>'nothogen.',
		'nothomorph'=>'nothomorph',
		'nothosect.'=>'nothosect.',
		'nothosect'=>'nothosect.',
		'nothoser.'=>'nothoser.',
		'nothoser'=>'nothoser.',
		'nothosubgen.'=>'nothosubgen.',
		'nothosubgen'=>'nothosubgen.',
		'nothosbgen'=>'nothosubgen.',
		'nothosbgen'=>'nothosubgen.',
		'nothosubsp.'=>'nothosubsp.',
		'nothosubsp.'=>'nothosubsp.',
		'nothosbsp.'=>'nothosubsp.',
		'nothosbsp.'=>'nothosubsp.',
		'nothossp.'=>'nothosubsp.',
		'nothossp'=>'nothosubsp.',
		'nothovar.'=>'nothovar.',
		'nothovar'=>'nothovar.',
		'proles'=>'proles',
		'race'=>'race',
		'rasse'=>'race',
		'sect.'=>'sect.',
		'sect'=>'sect.',
		'ser'=>'ser.',
		'ser.'=>'ser.',
		'sport'=>'sport',
		'stirps'=>'stirps',
		'subfo.'=>'subfo.',
		'subfo'=>'subfo.',
		'subf.'=>'subfo.',
		'subf'=>'subfo.',
		'subforma.'=>'subfo.',
		'sbfo.'=>'subfo.',
		'sbforma'=>'subfo.',
		'subgen.'=>'subgen.',
		'subgen'=>'subgen.',
		'subsect'=>'subsect.',
		'subsect.'=>'subsect.',
		'subser.'=>'subser.',
		'subser'=>'subser.',
		'subsp'=>'subsp.',
		'subsp.'=>'subsp.',
		'sbsp.'=>'subsp.',
		'sbsp'=>'subsp.',
		'ssp.'=>'subsp.',
		'ssp'=>'subsp.',
		'subspecies'=>'subsp.',
		'substirps'=>'substirps',
		'subvar.'=>'subvar.',
		'subvar'=>'subvar.',
		'supersect.'=>'supersect.',
		'var'=>'var.',
		'var.'=>'var.',
		'variety'=>'var.'
	);
 return strtr($str,$arr);
}

function isRankIndicator($str) {
	// Returns true if $str is a taxonomic rank indicator
	$arr=array(
		'agsp',
		'agsp.',
		'convar.',
		'convar',
		'cult.',
		'cult',
		'cultivar',
		'cv',
		'cv..',
		'cv',
		'fo.',
		'fo',
		'f.',
		'forma',
		'grex',
		'lusus',
		'monstr',
		'nohtosubsp',
		'nothogen',
		'nothomorph',
		'nothosect.',
		'nothosect',
		'nothoser.',
		'nothoser',
		'nothosubgen.',
		'nothosubgen',
		'nothosbgen',
		'nothosbgen',
		'nothosubsp.',
		'nothosubsp.',
		'nothosbsp.',
		'nothosbsp.',
		'nothossp.',
		'nothossp',
		'nothovar.',
		'nothovar',
		'proles',
		'race',
		'rasse',
		'sect.',
		'sect',
		'ser',
		'ser.',
		'sport',
		'stirps',
		'subfo.',
		'subfo',
		'subf.',
		'subf',
		'subforma.',
		'sbfo.',
		'sbforma',
		'subgen.',
		'subgen',
		'subsect',
		'subsect.',
		'subser.',
		'subser',
		'subsp',
		'subsp.',
		'sbsp.',
		'sbsp',
		'ssp.',
		'ssp',
		'subspecies',
		'substirps',
		'subvar.',
		'subvar',
		'supersect.',
		'var',
		'var.',
		'variety'
	);
 if (in_array($str,$arr)) {
 	return true;
 } else {
 	return false;
 }
}


?>
