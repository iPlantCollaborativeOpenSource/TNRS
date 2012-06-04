<?php
 
class DamerauLevenshteinMod {

	/**
  * Function: distance
  * Purpose: this function either uses mdld algorithm written in C represented by distance_utf function from module mdld.so, or the same algorithm writtein in php.
  * Inputs: string1 as $ustr1, string2 as ustr2, numeric limit of length of transposed block to be searched for as block_size, maximum edit distance after which calculations abort (for performance increase)
  * Outputs: computed edit distance between the input stings (0=identical, 1..n = amount of editing events required to make strings identical)
  * @param string $ustr1
  * @param string $ustr2
  * @param integer $block_size
  * @param integer $max_distance
  * @return integer : computed edit distance between two strings
  */
  static function distance($ustr1, $ustr2, $block_size=2, $max_distance=4) {
		if (function_exists('distance_utf')) {
			$a1 = self::utf8_to_unicode_code($ustr1);
			$a2 = self::utf8_to_unicode_code($ustr2);
			return distance_utf($a1, $a2, $block_size, $max_distance);
		} else return self::mdld_php( $ustr1, $ustr2, $block_size, $max_distance);
	}
  

  /**
  * Function: utf8_to_unicode_code
  * Purpose: Convert UTF-8 string into array of integers for furhter manipulations.
  * Inputs: string in ascii or utf-8
  * Outputs: array of integers. Each integer uniquely represents one of utf-8 character.
  * @param string $utf8_string
  * @return array
  */
	static function utf8_to_unicode_code($utf8_string){
		$expanded = iconv("UTF-8", "UTF-32", $utf8_string);
		$converted = unpack("L*", $expanded);
		return $converted;
	}
	
	/**
	 * Function: mdld
	 * Purpose: Performs Damerau-Levenshtein Distance test on two input strings, supporting block
	 *   transpositions of multiple characters
	 * Inputs: string 1 as p_str1, string 2 as p_str2, numeric limit on length of transposed block to be searched for as p_block_limit
	 * Outputs: computed edit distance between the input strings (0=identical on this measure, 1..n=increasing dissimilarity)
	 * @param string $p_str1
	 * @param string $p_str2
	 * @param integer $p_block_limit
	 * @return integer : computed edit distance between the input strings
	 */
	static function mdld_php( $p_str1, $p_str2, $p_block_limit = null, $max_distance ) {
		$p_block_limit = is_null($p_block_limit) ? 1 : $p_block_limit;
		$len1 = strlen($p_str1);$len2 = strlen($p_str2);
		$current_distance=$max_distance;

		if($p_str1 == $p_str2) {
			return 0;
		} elseif( $len1 == 0 || $len2 == 0 ) {
			return max($len1,$len2);
		} elseif( $len1 == 1 && $len2 == 1 &&  $p_str1 != $p_str2 ) {
			return 1;
		} else {
			$temp_str1 = $p_str1;
			$temp_str2 = $p_str2;			
			#first trim common leading characters
			while ( substr ($temp_str1, 0, 1) == substr ($temp_str2, 0, 1) ) {
				$temp_str1 = substr ($temp_str1, 1);
				$temp_str2 = substr ($temp_str2, 1);
			}
			#then trim common trailing characters
			while ( substr ($temp_str1, -1, 1) == substr ($temp_str2, -1, 1) ) {
				$temp_str1 = substr ($temp_str1, 0, strlen($temp_str1) - 1);
				$temp_str2 = substr ($temp_str2, 0, strlen($temp_str2) - 1);
			}
			$len1 = strlen($temp_str1);
			$len2 = strlen($temp_str2);
			#then calculate standard Levenshtein Distance
			if ( $len1 == 0 OR $len2 == 0 ) {
				return max($len1,$len2);
			} elseif ($len1 == 1 && $len2 == 1 && $p_str2 != $p_str1) {
				return 1;
			} else {

				#enter values in first (leftmost) column
				for( $t = 0; $t<= $len2; $t++ ) {
					$v_my_columns[0][$t] = $t;
				}
				
				#populate remaining columns
				for( $s = 1; $s<= $len1; $s++ ) {
					$v_my_columns[$s][0] = $s;
					$current=$max_distance;
					
					#populate each cell of one column:
					for( $t = 1; $t<= $len2; $t++ ) {
						$v_my_columns[$s][$t] = 0;
						#calculate cost
						if(substr($temp_str1, $s-1, 1) == substr($temp_str2, $t-1, 1)) {
							$v_this_cost = 0;
						} else {
							$v_this_cost = 1;
						}
						#extension to cover multiple single, double, triple, etc character transpositions
						#that includes caculation of original Levenshtein distance when no transposition found
						$v_temp_block_length = floor(min( ($len1 / 2), ($len2 / 2), $p_block_limit));			
$print = 0;						
if ($print) {							
						print "<pre>";
						print_r($v_my_columns);								
						print "</pre>";
}
						if ($v_temp_block_length < 1) {
							$v_my_columns[$s][$t] = min(
								  $v_my_columns[$s][$t - 1] + 1
								, $v_my_columns[$s - 1][$t] + 1
								, $v_my_columns[$s - 1][$t - 1] + $v_this_cost
							);
						}
						while( $v_temp_block_length >= 1) {
if ($print) {							
							print "<br>";
							print "$p_str1 $p_str2<br>";
							print "$temp_str1 $temp_str2<br>";
							print "$s >= " .  ($v_temp_block_length * 2). "<br>";
							print "$t >= " .  ($v_temp_block_length * 2). "<br>";
							print substr($temp_str1, ($s-1) - ( ($v_temp_block_length * 2) - 1), $v_temp_block_length) . "==" . substr($temp_str2, ($t-1) - ($v_temp_block_length - 1), $v_temp_block_length) . "<br>";
							print substr($temp_str1, ($s-1) - ($v_temp_block_length - 1), $v_temp_block_length) . "==" . substr($temp_str2, ($t-1) - ( ($v_temp_block_length * 2) - 1), $v_temp_block_length) . "<br>";
							print $v_temp_block_length . "<br>";
}
							if( ($s >= ($v_temp_block_length * 2))													
								&& ($t >= ($v_temp_block_length * 2))
								&& (substr($temp_str1, ($s-1) - ( ($v_temp_block_length * 2) - 1), $v_temp_block_length) == substr($temp_str2, ($t-1) - ($v_temp_block_length - 1), $v_temp_block_length))
								&& (substr($temp_str1, ($s-1) - ($v_temp_block_length - 1), $v_temp_block_length) == substr($temp_str2, ($t-1) - ( ($v_temp_block_length * 2) - 1), $v_temp_block_length))
							) {
if ($print) {															
								print "Transpostion Found<hr>";
}
								#transposition found
								$v_my_columns[$s][$t] = min(
										$v_my_columns[$s][$t - 1] + 1
									, $v_my_columns[$s - 1][$t] + 1
									, ($v_my_columns[$s - ($v_temp_block_length * 2)][$t - ($v_temp_block_length * 2)] + $v_this_cost + ($v_temp_block_length - 1))
								);
								$v_temp_block_length = 0;
							} elseif ($v_temp_block_length == 1) {
								#no transposition
if ($print) {															
								print "No Transpostion<br>";
								print $v_my_columns[$s][$t - 1] . "<br>"; 
								print $v_my_columns[$s-1][$t] . "<br>"; 
								print $v_my_columns[$s - 1][$t - 1] + $v_this_cost . "<br>";
								print "<pre>";
								print_r($v_my_columns);								
								print "</pre>";
}
								$v_my_columns[$s][$t] = min(
										$v_my_columns[$s][$t - 1] + 1
									, $v_my_columns[$s - 1][$t] + 1
									, $v_my_columns[$s - 1][$t - 1] + $v_this_cost
								);
							} else {
								$v_my_columns[$s][$t] = 0;
							}
							$v_temp_block_length -= 1;
						}
						if ($current_distance > $v_my_columns[$s][$t]) $current_distance = $v_my_columns[$s][$t];
					}
					if ($current_distance >= $max_distance) return $current_distance;
				}
			}

			if (!isset($v_my_columns[$s-1][$t-1])) {
				print "$s, $t<br><pre>";
				print_r($v_my_columns);
				print "</pre>";
			}

			if(isset($v_my_columns[$len1][$len2])) {
				return $v_my_columns[$len1][$len2];
			} else {
				return(-1);
			}
		}

	}
	
}
