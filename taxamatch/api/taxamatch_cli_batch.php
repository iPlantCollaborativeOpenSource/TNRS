<?php
	setlocale(LC_CTYPE, "en_US.UTF-8");
	require_once('config.php');
	require_once('classes/class.tnrs_aggregator.php');

	$options = getopt("f:s:n");
	$file=$options["f"];
	$source=$options["s"];

	$handle = fopen($file, "r");
	if(!isset($options["n"])) {fputcsv(STDOUT, TnrsAggregator::$field);}
	while (($field = fgetcsv($handle, 1000, ",")) !== FALSE) {
		if (!isset($field[0]) || $field[0] == '') {
			continue;
		}
		$cmd="/usr/bin/php /var/www/html/taxamatch-webservice-read-only/api/taxamatch_cli.php -s $source -i " . escapeshellarg($field[0]);
		echo `$cmd`;
	}
?>
