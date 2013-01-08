<?php
	ini_set("memory_limit","1000M");

	require_once('config.php');
	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.misc.php');
	require_once('classes/class.taxamatch.php');
	require_once('classes/class.tnrs_aggregator.php');

	$options = getopt("f:s:");
	$file=$options["f"];
	$source=$options["s"];
	$search_mode="";
	$cache="";

	$db = select_source( $source );

	$handle = fopen($file, "r");
	fputcsv(STDOUT, TnrsAggregator::$field);
	while (($field = fgetcsv($handle, 1000, ",")) !== FALSE) {
		if (!isset($field[0]) || $field[0] == '') {
			continue;
		}
		$tm = new Taxamatch($db);
		$tm->set('debug_flag','');
		$tm->set('output_type','');
		$tm->set('cache_flag','');
		$tm->set('cache_path',CACHE_PATH);
		$tm->set('name_parser',NAME_PARSER);
		$tm->set('chop_overload',CHOP_OVERLOAD);
		$ta=new TnrsAggregator($db);
		$name=mb_convert_encoding($field[0], 'UTF-8');
		if ( $tm->process( $name, $search_mode, $cache )) {
			$tm->generateResponse($cache);
		}
		$ta->aggregate($tm);
		$result=$ta->getData();
		foreach ($result as $re) {
			fputcsv(STDOUT, $re);
		}
		unset($ta);
		unset($tm);
	}
?>
