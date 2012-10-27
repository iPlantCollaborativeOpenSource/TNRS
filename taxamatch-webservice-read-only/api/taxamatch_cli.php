<?php
	ini_set("memory_limit","1000M");

	require_once('config.php');
	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.misc.php');
	require_once('classes/class.taxamatch.php');
	require_once('classes/class.tnrs_aggregator.php');

	$options = getopt("i:s:m:p:c:l:");
	$input=$options["i"];
	$source=$options["s"];
	//$classification=isset($options["l"]) ? $options["l"] : DEFAULT_CLASSIFICATION;
	$classification=isset($options["l"]) ? $options["l"] : "";
	$search_mode=isset($options["m"]) ? $options["m"] : "";
	$parse_only=isset($options["p"]) ? $options["p"] : "";
	$count=isset($options["c"]) ? $options["c"] : "";
	$cache="";

	$source = explode(",", $source);
	$db = select_source( $source, $classification);

	$tm = new Taxamatch($db);
	$tm->set('debug_flag','');
	$tm->set('output_type','');
	$tm->set('cache_flag','');
	$tm->set('cache_path',CACHE_PATH);
	$tm->set('name_parser',NAME_PARSER);
	$tm->set('chop_overload',CHOP_OVERLOAD);
	$tm->set('parse_only', $parse_only);
	$ta=new TnrsAggregator($db);
	//$name=mb_convert_encoding($input, 'UTF-8');
	$name=$input;
	if ( $tm->process( $name, $search_mode, $cache ) && ! $parse_only) {
		$tm->generateResponse($cache);
	}
	$ta->aggregate($tm);
	$result=$ta->getData();
	foreach ($result as $re) {
		if ($count) {
			array_unshift($re,$count);
		}
		fputcsv(STDOUT, $re);
	}
?>
