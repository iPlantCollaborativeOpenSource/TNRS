<?php
/**
* Taxamatch-Webservice PHP v1.0.0
* @author Michael Giddens
* @link http://www.silverbiology.com
*/

require_once('class.normalize.php');
require_once('class.nearmatch.php');
require_once('class.damerau_levenshtein_mod.php');

class AuthorMatch
{

	public function score_author_comparison($original_author_words1, $unique_authors1, $original_author_words2, $unique_authors2, $year_difference, $threshold = 50, $boolean = false)
	{
		$count_before1 = count($original_author_words1);
		$count_before2 = count($original_author_words2);
		$count_before = $count_before1 + $count_before2;
		$count_after1 = count($unique_authors1);
		$count_after2 = count($unique_authors2);
		$count_after = $count_after1 + $count_after2;

		$score = null;
			if($count_after1==0 && $count_after2==0)
		{
		//authors were identical
		if(!is_null($year_difference) && $year_difference == 0) $score = 100; //years are equal
		elseif(!is_null($year_difference) && $year_difference == 1) $score = 54; //years off by 1
		elseif(!is_null($year_difference) && $year_difference > 1) $score = 0; //years off by more than one
		elseif(is_null($year_difference)) $score = 99; //Same authors, at least one has no year
		else $score = 0;
		}
		elseif($count_after1==0 || $count_after2==0)
		{
		//one set of author words was entirely included in the other
		if(!is_null($year_difference) && $year_difference == 0) $score = 91; //years are equal
		elseif(!is_null($year_difference) && $year_difference == 1) $score = 51; //years off by 1
		elseif(!is_null($year_difference) && $year_difference > 1) $score = 0; //years off by more than one
		elseif(is_null($year_difference)) $score = 90; //Same authors, at least one has no year
		else $score = 0;
		}else
		{
		$score = (1-round(($count_after/$count_before),2)) * 100;
		if($score>=0 && (is_null($year_difference) || (!is_null($year_difference) && $year_difference == 0))) $score = $score; //Similar authors, same year
		else $score = 0;
		}


		if($boolean)
		{
			if($score < $threshold) $score = 0;
			if($score) return true;
			else return false;
		}else
		{
			return $score;
		}
	}

	public static function compare_authorities($authorship1, $authorship2, $threshold = 50, $boolean = true)
	{
		$author_words1 = $authorship1['authors'];
		$years1 = $authorship1['years'];

		$author_words2 = $authorship2['authors'];
		$years2 = $authorship2['years'];
		list($unique_authors1, $unique_authors2) = self::remove_duplicate_authors($author_words1, $author_words2);
    $year_difference = self::compare_years($years1, $years2);

		return self::score_author_comparison($author_words1, $unique_authors1, $author_words2, $unique_authors2, $year_difference, $threshold, $boolean);
	}

	public static function remove_duplicate_authors($author_words1, $author_words2)
	{
		$unique_authors1 = $author_words1;
		$unique_authors2 = $author_words2;
    //print_r($author_words1);
    //print_r($author_words2);
		foreach($author_words1 as $key1 => $author1)
		{
			$author1_matches = false;
			$author1 = Normalize::normalize_author_string($author1);
			foreach($author_words2 as $key2 => $author2)
				{
				$author2_matches = false;
				$author2 = Normalize::normalize_author_string($author2);

				if($author1 == $author2)
				{
				  //echo '$1';
					$author1_matches = true;
					$author2_matches = true;
				}elseif(preg_match("/^".preg_quote($author1, "/")."/i", $author2))
				{
				  //echo '$2';
					$author1_matches = true;
				}elseif(preg_match("/^".preg_quote($author2, "/")."/i", $author1))
				{
				  //echo '$3';
					$author2_matches = true;
				}

				// equal or one is contained in the other, so consider it a match for both terms
				if((strlen($author1)>=3 && $author1_matches) || (strlen($author2)>=3 && $author2_matches) || $author1 == $author2)
				{
				  //echo '$4';
					unset($unique_authors1[$key1]);
					unset($unique_authors2[$key2]);
				} elseif($author1_matches)
				{
				  //echo '$5';
					// author1 was abbreviation of author2
					unset($unique_authors1[$key1]);
				}elseif($author2_matches)
				{
				  //echo '$6';
				// author1 was abbreviation of author2
					unset($unique_authors2[$key2]);
				}else
				{
				  //echo '$7';
					// no match or abbreviation so try a fuzzy match
					// $max_length = max(strlen($author1), strlen($author2));
					// $lev = levenshtein($author1, $author2);
					// if(($lev/$max_length) <= .167)
					$match = self::match_author_words($author1, $author2);
					if ($match['match'])
					{
					  //echo '$8';
						unset($unique_authors1[$key1]);
						unset($unique_authors2[$key2]);
					}
				}
			}
			reset($author_words2);
		}
		return array($unique_authors1, $unique_authors2);
	}

	public static function compare_years($years1, $years2)
	{
		if(count($years1) == 0 && count($years2) == 0) return 0;
		if(count($years1) == 1 && count($years2) == 1)
		{
			$year1 = $years1[0];
			$year2 = $years2[0];

			if(is_numeric($year1) && is_numeric($year2))
			{
				return abs($year1 - $year2);
			}
		}

		return null;
	}
	
	public static function match_author_words($author1, $author2) {
		$match = $phonetic_match = false;
		$nm = new NearMatch();
		$author1_phonetic = $nm->near_match($author1);
		$author2_phonetic = $nm->near_match($author2);
		$author1_length = strlen($author1);
		$author2_length = strlen($author2);

		$ed = DamerauLevenshteinMod::distance($author1, $author2, 2, 3);
		// add the author post-filter
		// min. 51% "good" chars
		// first char must match for ED 2+
		if( ($ed <= 3 && ( min( $author1_length, $author2_length ) > ( $ed * 2 ))
					&& ( $ed < 2 || ( substr($author1,0,1) == substr($author2,0,1) ) ) )    
				|| ($author1_phonetic == $author2_phonetic) ) {
			$match = true;

			if($author1_phonetic == $author2_phonetic) $phonetic_match = true;
		}
		return array(
			'match' => $match, 
			'phonetic_match' => $phonetic_match, 
			'edit_distance' => $ed);
	}
}
?>
