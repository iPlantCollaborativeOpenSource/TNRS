<?php
require_once(dirname(__FILE__) . '/../../api/config.php');

require_once(dirname(__FILE__) . "/../../api/classes/class.damerau_levenshtein_mod.php");

class TestDamerauLevenshteinMod extends UnitTestCase
{
	
	function testRunTestsFromFile()
	{
		$file = file(dirname(__FILE__) . "/../test_data_files/damerau_levenshtein_mod.txt");
		foreach($file as $line => $test_case)
		{
			if(!preg_match("/^\s*#/", $test_case) && preg_match("/^([^\|]+)\|([^\|]+)\|([^\|]+)\|([^\|]+)\|([^\|]+)$/", $test_case, $arr))
			{
				$test_value_1 = $arr[1];
				$test_value_2 = $arr[2];
				$test_max_distance = trim($arr[3]);
				$test_block_size = trim($arr[4]);
				$test_result = trim($arr[5]);
				
				if ($test_result == 'null') $test_result = null;

				$res = DamerauLevenshteinMod::distance($test_value_1, $test_value_2, $test_block_size, $test_max_distance);
				//echo $res . ' "' . $test_value_1 . '" "' . $test_value_2 . '" <br/>';
				echo $res . "/$test_result ### $test_value_1, $test_value_2, $test_block_size, $test_max_distance<br/>";
				$this->assertTrue($test_result == $res); // "$test_value_1 with $test_value_2, block_size $test_block_size and max_distance $test_max_distance should give $test_result on line (". ($line+1) .")");
			}
		}

	}
}

?>
