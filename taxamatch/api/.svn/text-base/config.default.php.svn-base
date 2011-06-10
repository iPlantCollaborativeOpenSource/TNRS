<?php

	/**
	* Config File
	*/

	$authorities = array(
		'tm_sample' => array(
			  'db_type' => 'mysql'
			, 'username'=> '{username}'
			, 'pass' => '{pass}'
			, 'db_name'=> '{db}'
			, 'name' => 'Taxamatch Sample'
			, 'id' => 1
			, 'host' => 'localhost'
		)
	);

	// CONSTANTS
	define('TAXAMATCH_URL','http://{url_to_api}/api/taxamatch.php');
	define('CACHE_PATH', '{path_to_cache_folder}/cache/');

$xml_str = <<<EOT
<xml>
</xml>
EOT;

	define('XML_STRING', $xml_str);

	define('NAME_PARSER', 'gni'); // determines whether 3rd party chopping or taxamatch chopping is to be employed to chop the search text : values : "gni" | "taxamatch"
	define('CHOP_OVERLOAD', false); // whether chopping method is to be overloaded with some other method : values : true | false
	
	define('PROFILE', true);
	define('DEBUG', true);
	
	
	function total_time_elapsed()
	{
		static $time;
		if(!isset($time)) $time = microtime(true);
		return (string) round(microtime(true)-$time, 6);
	}

	function time_elapsed()
    {
		static $time;
		
		if(!isset($time)) $time = 0;
		$elapsed = (string) round(microtime(true)-$time, 6);
		$time = microtime(true);
		return $elapsed;
	}
	
	function profile($str)
	{
		if(!defined('PROFILE') || !PROFILE) return;
		echo time_elapsed().": $str\n";
	}
	
	function debug($str)
	{
		if(!defined('DEBUG') || !DEBUG) return;
		echo total_time_elapsed().": $str\n";
	}

?>