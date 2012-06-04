<?php
	require_once('config.php');
	require_once('classes/class.tnrs_aggregator.php');

	$options = getopt("f:s:m:p:c:l:n");
	$file=$options["f"];
	$source=$options["s"];
	$classification=isset($options["l"]) ? " -l " . $options["l"] : "";
	$search_mode=isset($options["m"]) ? " -m " . $options["m"] : "";
	$parse_only=isset($options["p"]) ? " -p " . $options["p"] : "";
	$ct=isset($options["c"]) ? $options["c"] : "";

	$handle = fopen($file, "r");
	$header=TnrsAggregator::$field;
	if (isset($options["c"])) {
		array_unshift($header,"ID");
	}
	if(!isset($options["n"])) {fputcsv(STDOUT, $header);}
	while (($field = fgetcsv($handle, 1000, ",")) !== FALSE) {
		if (!isset($field[0]) || $field[0] == '') {
			continue;
		}
		$count=isset($options["c"]) ? " -c " . $ct++ : "";
		$cmd="/usr/bin/php /var/www/html/taxamatch-webservice-read-only.v3/api/taxamatch_cli.php -s $source -i " . escapeshellarg($field[0]) . $search_mode . $count . $parse_only . $classification;
		echo `$cmd`;
	}
?>
