<?php
	
	/**
	 * Taxamatch-Webservice PHP v1.0.0
	 * @author Michael Giddens
	 * @link http://www.silverbiology.com
	 **/
	 error_reporting(E_ALL & ~E_NOTICE);
#	ini_set( "display_errors", 0);

	@require_once('config.php');
	set_time_limit(0);
	ini_set("memory_limit","128M");
	$program_start = microtime();
	
	$expected=array(
		  'cmd'
		, 'str'
		, 'str2'
		, 'word_type'
		, 'output'
		, 'search_mode'
		, 'debug'
		, 'source'
		, 'classification'
		, 'strip_ending'
		, 'normalize'
		, 'cache'
		, 'layout'
		, 'block_limit'
		, 'max_distance'
		, 'parse_only'
	); 
	
	// Initialize allowed variables
	foreach ($expected as $formvar)
		$$formvar = (isset(${"_$_SERVER[REQUEST_METHOD]"}[$formvar])) ? urldecode(${"_$_SERVER[REQUEST_METHOD]"}[$formvar]):NULL;

	//$source = (trim($source) == '' ? 'test1' : $source);
	$source = explode(",", $source);
	//$classification = isset($classification) ? $classification : DEFAULT_CLASSIFICATION;
	$cache = (trim($cache) == 'true') ? true: false;
	$parse_only = (trim($parse_only) == 'true') ? true: false;

	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.misc.php');

	switch( $cmd ) {

		case 'normalize':
			require_once('classes/class.normalize.php');
			$norm = new Normalize();
			$norm->set('debug_flag', $_REQUEST['debug']);
			$data = $norm->normalize( $str );
			if($output == 'xml') {
				$data = $norm->getXML();
			}
			$debug = $norm->debug;
			break;

		case 'normalize_auth':
			require_once('classes/class.normalize.php');
			$db = select_source( $source, $classification);
			$norm = new Normalize( $db );
			$norm->set('post_fix', '_'.$source);
			$norm->set('source', $source);
			$data = $norm->normalize_auth( $str );
			if($output == 'xml') {
				$data = $norm->getXML();
			}
			$debug = $norm->debug;
			break;

		case 'treat_word':
			require_once('classes/class.nearmatch.php');
			$nm = new NearMatch();
			$strip_ending = ($strip_ending == '' ? 0 : $strip_ending);
			$normalize = ($normalize == '' ? 0 : $normalize);
			$data = $nm->treat_word( $str, $strip_ending, $normalize );
			if($output == 'xml') {
				$data = $nm->getXML();
			}
			$debug = $nm->debug;
			break;

		case 'near_match':
			require_once('classes/class.nearmatch.php');
			$nm = new NearMatch();
			$data = $nm->near_match( $str );
			if($output == 'xml') {
				$data = $nm->getXML();
			}
			$debug = $nm->debug;
			break;

		case 'compare_auth':
			require_once('classes/class.taxamatch.php');
			$tm = new Taxamatch();
			$data = $tm->compare_auth( $str, $str2 );
			if($output == 'xml') {
				$data = $tm->getXML();
			}
			$debug = $tm->debug;
			break;

		case 'ngram':
			require_once('classes/class.taxamatch.php');
			$tm = new Taxamatch();
			$data = $tm->ngram( $str, $str2 );
			if($output == 'xml') {
				$data = $tm->getXML();
			}
			$debug = $tm->debug;
			break;

		case 'mdld':
			require_once('classes/class.damerau_levenshtein_mod.php');
			$mdld = new DamerauLevenshteinMod();
			$data = $mdld->mdld_php($str, $str2, 10, 1);
			break;
			
		case 'taxamatch':
			require_once('classes/class.taxamatch.php');
			if($cache) {
				$output = 'rest';
			}
			$db = select_source( $source, $classification );
			$data = array();
			$names = preg_split("/[\r\n;]+/", $str);
			if (is_array($names)) {
			foreach( $names as $name ) {
				$tm = new Taxamatch($db);
				$tm->set('debug_flag',$debug);
				$tm->set('output_type',strtolower($output));
				$tm->set('cache_flag',$authorities[$source]['cache_flag']);
				$tm->set('cache_path',CACHE_PATH);
				$tm->set('name_parser',NAME_PARSER);
				$tm->set('chop_overload',CHOP_OVERLOAD);
				
				if ( $tm->process( $name, $search_mode, $cache )) {
					$tm->generateResponse($cache);
				}
				//$data = array_merge($data, (array)($tm->getData($layout)));
				$data[]=$tm->getData($layout);
			}
			}

			$debug = $tm->debug;
			if($output == 'xml') {
				$data = $tm->getXML();
			}
			break;

		case 'tnrs_taxamatch':
			require_once('classes/class.taxamatch.php');
			require_once('classes/class.tnrs_aggregator.php');
			if($cache) {
				$output = 'rest';
			}
			$db = select_source( $source, $classification );
			$data = array();
			$names = preg_split("/[\r\n;]+/u", $str);
			if (is_array($names)) {
			foreach( $names as $name ) {
				$tm = new Taxamatch($db);
				$tm->set('debug_flag',$debug);
				$tm->set('output_type',strtolower($output));
				$tm->set('cache_flag',$authorities[$source]['cache_flag']);
				$tm->set('cache_path',CACHE_PATH);
				$tm->set('name_parser',NAME_PARSER);
				$tm->set('chop_overload',CHOP_OVERLOAD);
				$tm->set('parse_only', $parse_only);

				if ( $tm->process( $name, $search_mode, $cache )) {
					$tm->generateResponse($cache);
				}
				$ta=new TnrsAggregator($db);
				$ta->aggregate($tm);
				$data[]=$ta->getData();
			}
			}

			//$debug = $tm->debug;
			//if($output == 'xml') {
			//	$data = $tm->getXML();
			//}
			break;

		case 'sources':
			$db = select_source( $source, $classification );
			$data = $db->getSources();
			break;

		//case 'sources':
		//	$sources = getSources();
		//	$data = $sources;
		//	break;

		default:
			header('Content-type: application/json');
			print_c( json_encode( array( 'success' => true, array('msg' => 'Unknown Task', 'code' => 1 ) ) ) );
			exit();
			break;

	}

	switch( strtolower($output) ) {
		case 'rest':
			print $data;
			break;
		case 'xml':
		  header ("content-type: text/xml");
			print $data;
			break;
		default:
			$program_end = microtime();
			header('Content-type: application/json');
			if ($_REQUEST['debug'] == 1) {
				print_c( @json_encode( array( 'success' => true, 'duration'=>microtime_diff($program_start, $program_end), 'cache' => $cache, 'data' => $data, 'debug' => $debug ) ) );
			} else {
				print_c( @json_encode( array( 'success' => true, 'duration'=>microtime_diff($program_start, $program_end), 'cache' => $cache, 'data' => $data ) ) );
			}
			break;
	}
	
	/**
	 * Function print_c (Print Callback)
	 * This is a wrapper function for print that will place the callback around the output statement
	 **/
	function print_c( $str ) {
		if ( isset( $_REQUEST['callback'] ) ) {
			$cb = $_REQUEST['callback'] . '(' . $str . ')';
		} else {
			$cb = $str;
		}
		
		print $cb;
	}

/*	function getError($errcode) {
		$ar = array(
			101 => 'Search String \'str\' not given'
		);
	}*/
	
?>
