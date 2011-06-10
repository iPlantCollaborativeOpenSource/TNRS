<?php
require_once(dirname(__FILE__) . '/../../api/config.php');
require_once(dirname(__FILE__) . "/../../api/classes/class.taxamatch.php");

class TestTaxaMatch extends UnitTestCase
{
	function setUp()
	{
		$this->tm = new Taxamatch();
	}
	
	function testMatchGenera()
	{
		$g1 = 'Plantago';
		$g2 = 'Plantagoh';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 1);
		
		$g1 = 'Plantago';
		$g2 = 'This shouldnt match';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);
		
		
		$g1 = 'Plantagi';
		$g2 = 'Plantagy';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 1);
		
		//edit distance 1 makes match
		$g1 = 'Xantheri';
		$g2 = 'Pantheri';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 1);

		//Phonetic match works if ED/length is ok 
		$g1 = 'Xantherrri';
		$g2 = 'Zantherrry';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 2);
		$this->assertTrue($match['match']);

		//First letter is the same and distance is 2 should match, no phonetic match, if ed/min_genus_length is ok
		$g1 = 'Xantheeeerii';
		$g2 = 'Xantheeeerrr';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 2);

		//First letter is the same and distance is 3 should match, no phonetic match (if words are long enough)
		$g1 = 'Xantheeeeeeeeeeriii';
		$g2 = 'Xantheeeeeeeeeerrrr';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 3);

		//Should not match if one of words is shorter than 2x edit distance and distance is 2 or 3
		$g1 = 'Xant';
		$g2 = 'Xanthe';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 2);

		//Should not match if edit distance > 3 and no phonetic match
		$g1 = 'Xantheriiii';
		$g2 = 'Xantherrrrr';
		$match = $this->tm->match_genera($g1, $g2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);

	}
	
	function testMatchSpeciesEpithets()
	{
		//Exact match
		$s1 = 'major';
		$s2 = 'major';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 0);
		
		//Phonetic match always works
		$s1 = 'xanteriiiiiiii';
		$s2 = 'zantereeeeeeee';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);

		//Distance 4 matches if first 3 chars are the same and genera strings are long enough
		$s1 = 'majoooooooooooooooooorrrrr';
		$s2 = 'majooooooooooooooooooraaaa';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);

		//Should not match if Distance 4 matches and first 3 chars are not the same
		$s1 = 'majorrrrr';
		$s2 = 'marorraaa';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);

		//Distance 2 or 3 matches if first 1 char is the same
		$s1 = 'morrrrrrrrrrr';
		$s2 = 'morrrrrrrraaa';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 3);

		//Should not match if Distance 2 or 3 and first 1 char is not the same
		$s1 = 'morrrr';
		$s2 = 'torraa';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 3);

		//Distance 1 will match anywhere
		$s1 = 'major';
		$s2 = 'rajor';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 1);
		
		//Distance 1 will match anywhere
		$s1 = 'major';
		$s2 = 'rajor';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 1);
		
		//Will not match if length is less then twice of the edit distance
		$s1 = 'marrr';
		$s2 = 'maaaa';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 3);		
		
		//Will not match if length is less then twice of the edit distance
		$s1 = 'paludosa';
		$s2 = 'paleacea';
		$match = $this->tm->match_species_epithets($s1,$s2);
		$this->assertFalse($match['phonetic_match']);
		$this->assertTrue($match['edit_distance'] == 4);		
		$this->assertFalse($match['match']);
  }
	
	function testMatchBionomials()
	{
		//No trobule case
		$genus_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$species_epithet_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 2);

		//Will not match if either genus or sp. epithet dont match
		$genus_match = array('match' => false, 'phonetic_match' => false, 'edit_distance' => 1);
		$species_epithet_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 2);
		$genus_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$species_epithet_match = array('match' => false, 'phonetic_match' => false, 'edit_distance' => 1);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertFalse($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 2);
		
		//Should not match if binomial edit distance > 4 NOTE: EVEN with full phonetic match
		$genus_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 3);
		$species_epithet_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 2);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertFalse($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 5);
		
		//Should not have phonetic match if one of the components does not match phonetically
		$genus_match = array('match' => true, 'phonetic_match' => false, 'edit_distance' => 1);
		$species_epithet_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 2);
		$genus_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 1);
		$species_epithet_match = array('match' => true, 'phonetic_match' => false, 'edit_distance' => 1);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertTrue($match['match']);
		$this->assertFalse($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 2);
		
		//edit distance should be equal the sum of of edit distances
		$genus_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 2);
		$species_epithet_match = array('match' => true, 'phonetic_match' => true, 'edit_distance' => 2);
		$match = $this->tm->match_matches($genus_match, $species_epithet_match);
		$this->assertTrue($match['match']);
		$this->assertTrue($match['phonetic_match']);
		$this->assertEqual($match['edit_distance'], 4);
	}

	function testRunTestsFromFile()
	{
		$file = file(dirname(__FILE__) . "/../test_data_files/taxamatch.txt");
		foreach($file as $line => $test_case)
		{
			if(!preg_match("/^\s*#/", $test_case) && preg_match("/^([^\|]+)\|([^\|]+)\|([^\|]+)$/", $test_case, $arr))
			{
				$test_value_1 = trim($arr[1]);
				$test_value_2 = trim($arr[2]);
				$test_result = trim($arr[3]);

				if($test_result == 'false') $test_result = false;
				else $test_result = true;

				$res = $this->tm->name_strings_match($test_value_1, $test_value_2);
				echo $res . ' "' . $test_value_1 . '" "' . $test_value_2 . '" <br/>';
				$this->assertTrue($test_result == $res, "$test_value_1 `match` $test_value_2 should be $test_result on line (". ($line+1) .")");
			}
		}
	}
}

?>
