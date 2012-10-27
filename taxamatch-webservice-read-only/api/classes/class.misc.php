<?php

/**
 * select_source
 * Selects the source from the config file and creates the db object
 * @param string $source
 * @return mixed $db database object
 */

function select_source( $source, $classification ) {
	$db = null;
	global $authorities;
	// $s = $authorities[$source];
	$s = $authorities[DB_NAME];
	switch( $s['db_type'] ) {
		case 'mysql':
			require_once('class.queries.mysql.php');
			$connection_string = sprintf("server=%s; database=%s; username=%s; password=%s;", $s['host'], $s['db_name'], $s['username'], $s['pass'] );	
			$conn = new MysqliDatabase($connection_string);
			$conn->set_charset("utf8");
			$db = new Queries( $conn, $source, $classification );
			break;
	}
	
	return( $db );
}

function getSources () {
	global $authorities;
	$op = array();
	if(count($authorities)) {
		foreach($authorities as $key => $val_array) {
		$op[] = array('id' => $val_array['id'], 'name' => $val_array['name'], 'value' => $key, 'description' => $val_array['description']);
		}
	}
	return $op;
}

/**    
	Calculate a precise time difference.
	@param string $start result of microtime()
	@param string $end result of microtime(); if NULL/FALSE/0/'' then it's now
	@return flat difference in seconds, calculated with minimum precision loss
*/
function microtime_diff( $start, $end=NULL ) {
	if( !$end ) {
			$end= microtime();
	}
	list($start_usec, $start_sec) = explode(" ", $start);
	list($end_usec, $end_sec) = explode(" ", $end);
	$diff_sec= intval($end_sec) - intval($start_sec);
	$diff_usec= floatval($end_usec) - floatval($start_usec);
	return floatval( $diff_sec ) + $diff_usec;
} 

function str_ireplace_first ($search, $replace, $subject) {
	$search_array=array();
	if (is_array($search)) {
		$search_array=$search;
	} else {
		$search_array[]=$search;
	}	
	foreach ($search_array as $target) {
		if ($target) {
			$pos = stripos($subject, $target);
			if ($pos !== false) {
				$subject=substr_replace($subject, $replace, $pos, strlen($target));
				break;
			}
		}
	}
	return $subject;
}

?>
