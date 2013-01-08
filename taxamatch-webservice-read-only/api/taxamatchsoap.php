<?php
	
	/**
	 * Taxamatch-Webservice PHP v1.0.0
	 * @author Michael Giddens
	 * @link http://www.silverbiology.com
	 **/
	 error_reporting(E_ALL & ~E_NOTICE);
#	ini_set( "display_errors", 0);

	@require_once('config.php');
	$program_start = microtime();


	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.misc.php');
	

	function getStockQuote($symbol) {
			return 4.50;
	}

	function normalize($string) {
		require_once('classes/class.normalize.php');
		$norm = new Normalize();
		$norm->set('debug_flag', false);
		$data = $norm->normalize( $string );
		return($data);
	}
	
	require('nusoap/lib/nusoap.php');
	$server = new soap_server();
	$server->configureWSDL('stockserver', 'urn:stockquote');
	$server->register(
			"getStockQuote"
		,	array('symbol' => 'xsd:string')
		,	array('return' => 'xsd:decimal')
		,	'urn:stockquote'
		,	'urn:stockquote#getStockQuote');
	
	$server->configureWSDL('taxamatchserver', 'urn:taxamatch');
	$server->register(
			"normalize"
		,	array('string' => 'xsd:string')
		,	array('return' => 'xsd:string')
		,	'urn:taxamatch'
		,	'urn:taxamatch#normalize');
	
	
	$HTTP_RAW_POST_DATA = isset($HTTP_RAW_POST_DATA) ? $HTTP_RAW_POST_DATA : '';
	$server->service($HTTP_RAW_POST_DATA);

/*


	$expected=array(
		  'cmd'
		, 'str'
		, 'str2'
		, 'word_type'
		, 'output'
		, 'search_mode'
		, 'debug'
		, 'source'
		, 'strip_ending'
		, 'normalize'
		, 'cache'
		,	'layout'
	); 
	
	// Initialize allowed variables
	foreach ($expected as $formvar)
		$$formvar = (isset(${"_$_SERVER[REQUEST_METHOD]"}[$formvar])) ? urldecode(${"_$_SERVER[REQUEST_METHOD]"}[$formvar]):NULL;

	$source = (trim($source) == '' ? 'test1' : $source);
	$cache = (trim($cache) == 'true') ? true: false;

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
			$db = select_source( $source );
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

		case 'taxamatch':
			require_once('classes/class.taxamatch.php');
			if($cache) {
				$output = 'rest';
			}
			$db = select_source( $source );
			$data = array();
			$names = preg_split("/[\r\n;,]+/", $str);
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
				$data = array_merge($data, $tm->getData($layout));
			}
			}

			$debug = $tm->debug;
			if($output == 'xml') {
				$data = $tm->getXML();
			}
			break;

		case 'sources':
			$sources = getSources();
			$data = $sources;
			break;

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
*/
	
?>