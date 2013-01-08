<?php
require_once(dirname(__FILE__) . '/../../api/config.php');
require_once(dirname(__FILE__) . "/../../api/classes/class.gni_splitter.php");
require_once(dirname(__FILE__) . "/../../api/classes/class.authormatch.php");

class TestAuthorMatch extends UnitTestCase
{
	function testGrabbingAuthorYears()
	{
		$splitter = new Splitter(null, 'Aus bus Linnaeus, Smith 2009');
		$authorship = $splitter->authors_years;
		$this->assertIsA($authorship, 'Array');
		$this->assertEqual($authorship['authors'][0], 'Linnaeus');
		$this->assertEqual($authorship['years'][0], '2009');
	}

	function testAuthorMatch()
	{
		$name_string1 = 'Aus bus Linnaeus, Smith. 2009';
		$name_string2 = 'Aus bus Lïnn';
		$info_1 = new Splitter(null,$name_string1);
		$info_2 = new Splitter(null,$name_string2);
		
		$score = AuthorMatch::compare_authorities($info_1->authors_years, $info_2->authors_years);
	}

	function testRunTestsFromFile()
	{
		$file = file(dirname(__FILE__) . "/../test_data_files/author_comparisons.txt");
		foreach($file as $line => $test_case)
		{
			if(!preg_match("/^\s*#/", $test_case) && preg_match("/^([^\|]+)\|([^\|]+)\|([^\|]+)\|([^\|]+)$/", $test_case, $arr))
			{
				$name_string1 = trim($arr[1]);
				$name_string2 = trim($arr[2]);
				$test_result = trim($arr[3]);
				$test_score = trim($arr[4]);

				if($test_result == 'false') $test_result = false;
				else $test_result = true;
				
				$info_1 = new Splitter(null,$name_string1);
				$info_2 = new Splitter(null,$name_string2);
				$res = AuthorMatch::compare_authorities($info_1->authors_years, $info_2->authors_years, null, false);
				echo $res . ' "' . $name_string1 . '" "' . $name_string2 . '" <br/>';
				$this->assertTrue($test_result == $res, "$name_string1 `match` $name_string2 should be $test_result on line (". ($line+1) .")");
			}
		}

	}
}

?>
