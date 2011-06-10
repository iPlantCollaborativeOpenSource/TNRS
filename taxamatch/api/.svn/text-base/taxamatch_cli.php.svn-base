<?php
	ini_set("memory_limit","1000M");

	require_once('config.php');
	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.misc.php');
	require_once('classes/class.taxamatch.php');
	require_once('classes/class.tnrs_aggregator.php');

	$options = getopt("i:s:");
	$input=$options["i"];
	$source=$options["s"];
	$search_mode="";
	$cache="";

	$db = select_source( $source );

	$tm = new Taxamatch($db);
	$tm->set('debug_flag','');
	$tm->set('output_type','');
	$tm->set('cache_flag','');
	$tm->set('cache_path',CACHE_PATH);
	$tm->set('name_parser',NAME_PARSER);
	$tm->set('chop_overload',CHOP_OVERLOAD);
	$ta=new TnrsAggregator($db);
	//$name=mb_convert_encoding($input, 'UTF-8');
	$name=$input;
	if ( $tm->process( $name, $search_mode, $cache )) {
		$tm->generateResponse($cache);
	}
	$ta->aggregate($tm);
	$result=$ta->getData();
	foreach ($result as $re) {
		fputcsv(STDOUT, $re);
	}
?>
