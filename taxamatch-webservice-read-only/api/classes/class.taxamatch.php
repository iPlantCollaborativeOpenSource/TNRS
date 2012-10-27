<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

/**
 * Includes the config and other classes
 */
//	require_once('../config.php');
	require_once('class.authormatch.php');
	require_once('class.normalize.php');
	require_once('class.nearmatch.php');
	require_once('class.queries.mysql.php');
	require_once('class.taxamatch-cache.php');
	require_once('class.authormatch.php');
	if(!CHOP_OVERLOAD) {
		switch(NAME_PARSER) {
			case 'gni':
				require_once('class.gni_splitter.php');
				break;
			case 'taxamatch':
				require_once('class.taxamatch_splitter.php');
				break;
		}
	}
	
/**
 * Class Taxamatch
 */
	class Taxamatch {

		/**
		* holds the output
		* @access private
		* @var string
		*/
		private $data;
		static $ext_th=0.5;
		static $nor_th=0.3334;
		static $ext_mt=3;
		static $nor_mt=2;
		
		/**
		 * Constructor
		 * @param $db database connection object
		 */
		public function __construct( $db = null ) {
			$this->db = $db;
			$this->db->debug = &$this->debug;
			$this->xml = new SimpleXml(XML_STRING);
			$this->familymatches=array();
			$this->genusmatches=array();
			$this->speciesmatches=array();
			$this->infra1matches=array();
			$this->infra2matches=array();
		}

		/**
		 * Get the output data
		 * @return string
		 */
		public function getData( $format = 'object' ) {
			switch( $format ) {
				case 'flat':					
					$tmp = array();
					foreach( $this->data as $key => $val ) {
						if ($key == 'input') continue;
						foreach( $val as $key2 => $val2 ) {
							foreach( $val2 as $key3 => $val3 ) {
								$tmp[] = array_merge( array("rank" => $key, "input" => $this->data["input"], "match_type" => $key2 ), $val3 );
							}
						}
					}
					return( $tmp );
					break;

				case 'object':
				default:
					return $this->data;
			}
		}

		/**
		 * Sets value to the method property
		 * @param mixed $name class property name
		 * @param mixed $value class property value
		 */
		public function set($name,$value) {
			$this->$name = $value;
		}

		/**
		 * Reduce Spaces
		 * This will reduce the string to only allow once space between characters
		 * @param string $str : input string
		 * @return string  : processed string
		 */
		public function reduce_spaces( $str ) {
		
			$str = preg_replace("/ {2,}/", ' ', $str );
			$str = trim( $str );
			
			return( $str );
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
		public function mdld( $p_str1, $p_str2, $p_block_limit, $max_distance=4) {
//			return( levenshtein( $p_str1, $p_str2 ) );
			require_once('class.damerau_levenshtein_mod.php');
			$value = DamerauLevenshteinMod::distance( $p_str1, $p_str2, $p_block_limit, $max_distance );
			$this->debug['mdld'][] = "1 (p_str1:$p_str1) (p_str2:$p_str2) (p_block_limit:$p_block_limit) (value:$value)";
			return( $value );
		}
		/**
		 * Function: compare_auth
		 * Purpose: Compares two authority strings
		 * Author: Tony Rees (Tony.Rees@csiro.au)
		 * Date created: March 2008
		 * Inputs: authority string 1 as auth1, authority string 1 as auth2
		 * Outputs: Numeric similarity value of the 2 strings using weighted n-gram analysis,
		 *    on 0-1 scale (1 = identical - typically after normalization; 0 = no similarity)
		 * Remarks:
		 *   (1) Invokes function "normalize_auth" on both strings, to compare only normalized
		 *         versions of the same
		 *   (2) Returns blend of 2/3 bigram, 1/3 trigram similarity (bigrams better correspond
		 *         to intuitivesimilarity, however are insensitive to word order, i.e. "Smith et
		 *         Jones" = "Jones et Smith" without some trigram contribution)
		 *   (3) Returns blend of 50% similarity with, and 50% without, stripping of diacritical
		 *         marks - so that the contribution of the latter is reduced but not eliminated
		 *   (4) Is case insensitive (i.e. "de Saedeleer" = "De Saedeleer", etc.)
		 *   (5) Threshold between low / possible / good match is in the area of
		 *         0-0.3 / 0.3-0.5 / 0.5+.
		 * @param string $auth1 : authority string 1
		 * @param string $auth2 : authority string 2
		 * @return number : between 0 - 1 : (1 = identical - typically after normalization; 0 = no similarity)
		 */
		public function compare_auth( $auth1 = NULL, $auth2 = NULL ) {

			if ( ( $auth1 == NULL ) || ( $auth2 == NULL ) ) {
				return( NULL );
			} else {

				$this->input = array($auth1,$auth2);

				$this->debug['compare_auth'][] = "Args: (auth1:$auth1) (auth2:$auth2)";

				$n = new Normalize($this->db);
				$new_auth1 = $n->normalize_auth( $auth1 );
				$new_auth2 = $n->normalize_auth( $auth2 );

				$this->debug['compare_auth'][] = "1 (new_auth1:$new_auth1) (new_auth2:$new_auth2)";
								
				if ( $new_auth1 == $new_auth2 ) {
					$this_auth_match = 1;
					$this->debug['compare_auth'][] = "2a (this_auth_match:$this_auth_match)";
				} else {
					// create second versions without diacritical marks
					#$new_auth1b = $this->translate( $new_auth1, '¡…Õ”⁄¿»Ã“Ÿ¬ Œ‘€ƒÀœ÷‹√—’≈«ÿ', 'AEIOUAEIOUAEIOUAEIOUANOACO');
					#$new_auth2b = $this->translate( $new_auth2, '¡…Õ”⁄¿»Ã“Ÿ¬ Œ‘€ƒÀœ÷‹√—’≈«ÿ', 'AEIOUAEIOUAEIOUAEIOUANOACO');
					$new_auth1b = $n->utf8_to_ascii($new_auth1);
					$new_auth2b = $n->utf8_to_ascii($new_auth2);
					
					// weighted ngram comparison, use 67% n=2, 33% n=3
					// use mean of versions with and without diacritical marks (to lessen their effect by 50%)
					$temp_auth_match1 = ( ( 2 * $this->ngram( $new_auth1, $new_auth2, 2 ) ) + $this->ngram( $new_auth1, $new_auth2, 3 ) ) / 3;
					$temp_auth_match2=0;
					if ($new_auth1 == $new_auth1b && $new_auth2 == $new_auth2b) {
						$temp_auth_match2=$temp_auth_match1;
					} else {
						$temp_auth_match2 = ( ( 2 * $this->ngram( $new_auth1b, $new_auth2b, 2 ) ) + $this->ngram( $new_auth1b, $new_auth2b, 3 ) ) / 3;
					}
					$this->debug['compare_auth'][] = '2b (temp_auth_match1:$temp_auth_match1) (temp_auth_match2:$temp_auth_match2)';
					$this_auth_match = ( $temp_auth_match1 + $temp_auth_match2 ) / 2;
					$this->debug['compare_auth'][] = '2c (this_auth_match:$this_auth_match)';
				}
			
			}

			$this_auth_match = round( $this_auth_match, 4 ); 
			$this->debug['compare_auth'][] = "Return: $this_auth_match";
/*			$this->output = $this_auth_match;
	    return( $this->output );*/
			return $this_auth_match;
		}

		/**
		 * Function: ngram
		 * Purpose: Perform n-gram comparison of two input strings
		 * Author: Tony Rees (Tony.Rees@csiro.au)
		 * Date created: March 2008
		 * Inputs: string 1 as source_string, string 2 as target_string, required value of n to be
		 *   incorporated for as n_used
		 * Outputs: computed similarity between the input strings, on 0-1 scale (1=identical on this measure, 0=no similarity)
		 * Remarks:
		 *   (1) Input parameter n_used determines whether the similarity is calculated using unigrams (n=1),
		 *   bigrams (n=2), trigrams (n=3), etc; defaults to n=1 if not supplied.
		 *   (2) Input strings are padded with (n-1) spaces, to avoid under-weighting of terminal characters.
		 *   (3) Repeat instances of any n-gram substring in the same input string are treated as new substrings,
		 *   for comparison purposes (up to 9 handled in this implementation)
		 *   (4) Is case sensitive (should translate input strings to same case externally to render case-insensitive)
		 *   (5) Similarity is calculated using Diceís coefficient.
		 * @param string $source_string
		 * @param string $target_string
		 * @param string $n_used : determines whether the similarity is calculated using unigrams (n=1),bigrams (n=2), trigrams (n=3), etc; defaults to n=1 if not supplied.
		 * @return number : between 0 - 1 : (1 = identical - typically after normalization; 0 = no similarity)
		 */
		public function ngram( $source_string = NULL, $target_string = NULL, $n_used = 1 ) {

			$match_count = 0;

			$this->input = array($source_string,$target_string);

			$this->debug['ngram'][] = "1 (n_used:$n_used) (source_string:$source_string) (target_string:$target_string)";
		
			$padding=str_repeat(" ", $n_used -1);

			$this_source_string = $padding . $source_string . $padding;
			$this_target_string = $padding . $target_string . $padding;
			// build strings of n-grams plus occurrence counts
	
			$source_ngram_number=mb_strlen($source_string)+$n_used-1;
			$target_ngram_number=mb_strlen($target_string)+$n_used-1;

			$source_ngram=array();
			$target_ngram=array();

			for ($i=0; $i < $source_ngram_number; $i++) {
				$ngram=mb_substr($this_source_string, $i, $n_used);
				if (! isset($source_ngram[$ngram])) {
					$source_ngram[$ngram]=0;
				}
				$source_ngram[$ngram]++;
			}
			for ($i=0; $i < $target_ngram_number; $i++) {
				$ngram=mb_substr($this_target_string, $i, $n_used);
				if (! isset($target_ngram[$ngram])) {
					$target_ngram[$ngram]=0;
				}
				$target_ngram[$ngram]++;
			}

			while (list($ngram, $source_ngram_count) = each($source_ngram)){
				if (array_key_exists($ngram, $target_ngram)) {
					$target_ngram_count = $target_ngram[$ngram];
					$match_count+=$target_ngram_count < $source_ngram_count ? $target_ngram_count : $source_ngram_count;
				}
			}

			$result = round(( 2 * $match_count ) / ( $source_ngram_number + $target_ngram_number ), 4);

			return $result;
		}
		
		public function name_strings_match($name_string1, $name_string2) {
			$info_1 = new Splitter(null,$name_string1);
			profile("splitting 1");
			
			$info_2 = new Splitter(null,$name_string2);
			profile("splitting 2");
			
			return $this->name_objects_match($info_1, $info_2);
		}
		
		public function name_objects_match($name_object_1, $name_object_2) {
			profile("starting name match");
			
			$genus_match = $this->match_genera($name_object_1->genus, $name_object_2->genus);
			profile("match_genera");

			$epithets_match = $this->match_species_epithets($name_object_1->species, $name_object_2->species);
			profile("match_species_epithets");

			$total_length = strlen($name_object_1->genus) + strlen($name_object_1->species) + strlen($name_object_2->genus) + strlen($name_object_2->species);
			$match = $this->match_matches($genus_match, $epithets_match);
			profile("match_matches");

			if ($match['match']) {
				$author_match_score = $this->compare_authorities($name_object_1->authors_years, $name_object_2->authors_years);
				profile("compare_authorities");
				if(!$author_match_score) $match['match'] = false;
			}
			
			return $this->match_response_to_float($match, $total_length);
		}
		
		public function compare_authorities($authority1, $authority2) {
			return AuthorMatch::compare_authorities($authority1, $authority2);
		}
		
		public function match_response_to_float($match_response, $total_length_of_strings) {
			if(!$match_response['match']) return 0.0;
			
			return (1 - ($match_response['edit_distance'] / ($total_length_of_strings/2)));
		}
		public function match_family($family1, $family2) {
			$family1=strtoupper($family1);
			$family2=strtoupper($family2);
			$match = $phonetic_match = 0;
			$nm = new NearMatch();
			$family1_phonetic = $nm->near_match($family1);
			$family2_phonetic = $nm->near_match($family2);
			$family1_length = strlen($family1);
			$family2_length = strlen($family2);

			$temp_family_ED = $this->mdld($family2, $family1, 2, 3);
			$min_family_length = $family1_length < $family2_length ? $family1_length : $family2_length;
			$nor_thrs=self::$nor_th;
			if ($this->search_mode == 'extended') {
				$nor_thrs=self::$ext_th;
			}
			$ext_thrs=self::$ext_th;
			if ($min_family_length == 0 || $min_family_length < 6 && $temp_family_ED/$min_family_length > $ext_thrs || $min_family_length >= 6 && $temp_family_ED/$min_family_length > $nor_thrs) return array('edit_distance' => $temp_family_ED, 'phonetic_match' => false, 'match' => false);
			// first char must match for ED 2+
			if( ($temp_family_ED <= 3 && ( $min_family_length >= ( $temp_family_ED * 2 ))
						&& ( $temp_family_ED < 2 || ( substr($family2,0,1) == substr($family1,0,1) ) ) )    
					|| ($family1_phonetic == $family2_phonetic) ) {
				$match = true;
				if($family1_phonetic == $family2_phonetic) $phonetic_match = true;
			}
			return array(
				'match' => $match, 
				'phonetic_match' => $phonetic_match, 
				'edit_distance' => $temp_family_ED);
		}
		
		public function match_genera($genus1, $genus2) {
			$genus1=strtoupper($genus1);
			$genus2=strtoupper($genus2);
			$match = $phonetic_match = 0;
			$nm = new NearMatch();
			$genus1_phonetic = $nm->near_match($genus1);
			$genus2_phonetic = $nm->near_match($genus2);
			$genus1_length = strlen($genus1);
			$genus2_length = strlen($genus2);

			//$temp_genus_ED = $this->mdld($genus2, $genus1, 2, 3);
			$temp_genus_ED = $this->mdld($genus2, $genus1, 2, 3); // there is a bug in modified Damerau Levenshtein, when it is fixed change 1 to 2:  $this->mdld($genus2, $genus1, 2, 3)
			$min_genus_length = $genus1_length < $genus2_length ? $genus1_length : $genus2_length;
			$nor_thrs=self::$nor_th;
			if ($this->search_mode == 'extended') {
				$nor_thrs=self::$ext_th;
			}
			$ext_thrs=self::$ext_th;
			if ($min_genus_length == 0 || $min_genus_length < 6 && $temp_genus_ED/$min_genus_length > $ext_thrs || $min_genus_length >= 6 && $temp_genus_ED/$min_genus_length > $nor_thrs) return array('edit_distance' => $temp_genus_ED, 'phonetic_match' => false, 'match' => false);
			// add the genus post-filter
			// min. 51% "good" chars
			// first char must match for ED 2+
			if( ($temp_genus_ED <= 3 && ( $min_genus_length >= ( $temp_genus_ED * 2 ))
						&& ( $temp_genus_ED < 2 || ( substr($genus2,0,1) == substr($genus1,0,1) ) ) )    
					|| ($genus1_phonetic == $genus2_phonetic) ) {
				$match = true;
				// accept as exact or near match; append to genus results table
				$this->debug['process'][] = "6a (near_match_genus:$genus2_phonetic) (this_near_match_genus:$genus1_phonetic)";

				if($genus1_phonetic == $genus2_phonetic) $phonetic_match = true;
			}
			return array(
				'match' => $match, 
				'phonetic_match' => $phonetic_match, 
				'edit_distance' => $temp_genus_ED);
		}
		
		public function match_species_epithets($species_epithet1, $species_epithet2) {
			$species_epithet1=strtoupper($species_epithet1);
			$species_epithet2=strtoupper($species_epithet2);
			$match = false;
			$phonetic_match = false;
			$epithet1_length = strlen($species_epithet1);
			$epithet2_length = strlen($species_epithet2);
			
			$nm = new NearMatch();
			$epithet1_phonetic = $nm->near_match($species_epithet1);
			$epithet2_phonetic = $nm->near_match($species_epithet2);
			//$temp_species_ED = $this->mdld($species_epithet2, $species_epithet1, 4, 4);
			$temp_species_ED = $this->mdld($species_epithet2, $species_epithet1, 4, 4);// there is a bug in modified Damerau Levenshtein, when it is fixed change 1 to 2:  $this->mdld($genus2, $genus1, 4, 4)
			//edit distance/word length threshold
			$min_sp_length = $epithet1_length < $epithet2_length ? $epithet1_length : $epithet2_length;
			$nor_thrs=self::$nor_th;
			if ($this->search_mode == 'extended') {
				$nor_thrs=self::$ext_th;
			}
			$ext_thrs=self::$ext_th;

			if ($min_sp_length == 0 || $min_sp_length < 6 && $temp_species_ED/$min_sp_length > $ext_thrs || $min_sp_length >=6 && $temp_species_ED/$min_sp_length > $nor_thrs) return array('edit_distance' => $temp_species_ED, 'phonetic_match' => false, 'match' => false);
			// add the species post-filter
			// min. 50% "good" chars
			// first char must match for ED2+
			// first 3 chars must match for ED4
			if ($epithet2_phonetic == $epithet1_phonetic) $match = true;
			elseif( $temp_species_ED <= 4 && $min_sp_length >= ($temp_species_ED*2)
				&& ($temp_species_ED < 2 || substr($species_epithet2, 0, 1) == substr($species_epithet1,0,1))
				&& ($temp_species_ED < 4 || substr($species_epithet2, 0, 3) == substr($species_epithet1,0,3))) $match = true;
			
			// if phonetic match, set relevant flag
			if ($epithet2_phonetic == $epithet1_phonetic) $phonetic_match = true;
			return array('match' => $match, 'phonetic_match' => $phonetic_match, 'edit_distance' => $temp_species_ED);
		}

		public function match_matches($matches) {
			$mtpl=self::$nor_mt;
			if ($this->search_mode == 'extended') {
				$mtpl=self::$ext_mt;
			}
			$max_ed=count($matches) * $mtpl;
			$match=array_shift($matches);
			foreach ($matches as $mt) {
				$match["edit_distance"]+=$mt["edit_distance"];
				if($match["edit_distance"] > $max_ed)  $match['match'] = false;
				if(!$mt['match']) $match['match'] = false;
				if(!$mt['phonetic_match']) $match['phonetic_match'] = false;
			}
			return $match;
		}
		
		
		//public function match_matches($genus_match, $species_epithets_match, $infra1_match=NULL, $infra2_match=NULL) {
		//	$binomial_match = $species_epithets_match;
		//	$binomial_match['edit_distance'] = $genus_match["edit_distance"] + $species_epithets_match["edit_distance"];
		//	
		//	if(!$genus_match['match']) $binomial_match['match'] = false;
		//	if($binomial_match["edit_distance"] > 4)  $binomial_match['match'] = false;
		//	if(!$genus_match['phonetic_match']) $binomial_match['phonetic_match'] = false;
		//	
		//	return $binomial_match;
		//}
		
		public function match_authors($preparsed_1, $preparsed_2, $boolean = false) {
		  $authorship1 = array('authors' => $preparsed_1['all_authors'], 'years' => $preparsed_1['all_years']);
      $authorship2 = array('authors' => $preparsed_2['all_authors'], 'years' => $preparsed_2['all_years']);
      return AuthorMatch::compare_authorities($authorship1, $authorship2, 50, $boolean);
		}
		
		// public function match_species($genus1, $species_epithet1, $genus2, $species_epithet2, $genus_edit_distance) {
		// 	$match = false;
		// 	$phonetic_match = false;
		// 	$epithet1_length = strlen($species_epithet1);
		// 	$epithet2_length = strlen($species_epithet2);
		// 	
		// 	$nm = new NearMatch();
		// 	$genus1_phonetic = $nm->near_match(genus1);
		// 	$genus2_phonetic = $nm->near_match(genus2);			
		// 	$epithet1_phonetic = $nm->near_match($species_epithet1);
		// 	$epithet2_phonetic = $nm->near_match($species_epithet2);
		// 	$temp_species_ED = $this->mdld($species2, $species1, 4);
		// 	// add the species post-filter
		// 	// min. 50% "good" chars
		// 	// first char must match for ED2+
		// 	// first 3 chars must match for ED4
		// 	if ( ($epithet2_phonetic == $epithet1_phonetic) 
		// 		|| ( ($genus_edit_distance + $temp_species_ED <= 4)
		// 		&& ($temp_species_ED <= 4 && min(strlen($epithet2_length),$epithet1_length) >= ($temp_species_ED*2)
		// 		&& ($temp_species_ED < 2 || strpos($species_epithet2 , substr($species_epithet1,1,1)) !== false)
		// 		&& ($temp_species_ED < 4 || strpos($species_epithet2 , substr($species_epithet1,1,3)) !== false) 
		// 		&& ($genus_edit_distance + $temp_species_ED <= 4) ))) {
		// 		$match = true;
		// 		// accept as exact or near match, append to species results table
		// 		// if phonetic match, set relevant flag
		// 		if ( ($genus2_phonetic == $genus1_phonetic) && ($epithet2_phonetic == $epithet1_phonetic) ) $phonetic_match = true;
		// 	}
		// 	return array('match' => $match, 'phonetic_match' => $phonetic_match, 'edit_distance' => $temp_species_ED);
		// }

		/**
		 * Function : process
		 * Purpose: Perform exact and fuzzy matching on a species name, or single genus name
		 * Input: - genus, genus+species, or genus+species+authority (in this version), as "searchtxt"
		 *        - "search_mode" to control search mode: currently normal (default) / rapid / no_shaping
		 *        - "debug" - print internal parameters used if not null
		 * Outputs: list of genera and species that match (or near match) input terms, with associated
		 *   ancillary info as desired
		 * Remarks:
		 *   (1) This demo version is configured to access base data in three tables:
		 *          - genlist_test1 (genus info); primary key (PK) is genus_id
		 *          - splist_test1 (species info); PK is species_id, has genus_id as foreign key (FK)
		 *              (= link to relevant row in genus table)
		 *          - auth_abbrev_test1 (authority abbreviations - required by subsidiary function
		 *            "normalize_auth". Refer README file for relevant minimum table definitions.
		 *       If authority comparisons are not required, calls to "normalize_auth" can be disabled and
		 *         relevant function commented out, removing need for third table.
		 *       (In a production system, table and column names can be varied as desired so long as
		 *         code is altered at relevant points, also could be re-configured to hold all genus+species info together in a single table with minor re-write).
		 *   (2) Writes to and reads back from pre-defined global temporary tables
		 *      "genus_id_matches" and "species_id_matches", new instances of these are automatically
		 *      created for each session (i.e., do not need clearing at procedure end). Refer
		 *      README file for relevant table definitions.
		 *   (3) When result shaping is on in this version, a relevant message displayed as required
		 *      for developer feedback, if more distant results are being masked (in producton version,
		 *       possibly would not do this)
		 *   (4) Requires the following subsidiary functions (supplied elsewhere in this package):
		 *         - normalize
		 *         - normalize_auth
		 *         - reduce_spaces
		 *         - ngram
		 *         - compare_auth
		 *         - near_match
		 *         - mdld
		 *   (5) Accepts "+" as input separator in place of space (e.g. "Homo+sapiens"), e.g. for calling
		 *         via a HTTP GET request as needed.
		 * @param string $searchtxt : genus, genus+species, or genus+species+authority
		 * @param string $search_mode : normal (default) / rapid / no_shaping
		 * @param boolean $cache
		 * @return boolean
		 */
		public function process($searchtxt, $search_mode='normal', $cache = false) {
			$this->input = $searchtxt;
			$this->search_mode=$search_mode;
			$this->searchtxt = $searchtxt;
			$this->debug['process'][] = "1 (searchtxt:$searchtxt) (search_mode:$search_mode)";

			$this->this_search_family='';
			$this->this_search_genus = '';
			$this->this_search_species = '';
			$this->this_authority = '';
			$this->this_authorities=array();
			$this->this_search_infra1='';
			$this->this_search_infra2='';
			$this->this_search_rank1='';
			$this->this_search_rank2='';

			$this->this_start_string='';
			$this->this_cleaned_txt='';
			$this->this_family_string='';
			$this->this_family_unmatched='';
			$this->this_status_string='';

			$text_str = $searchtxt;

			// accept "+" as separator if supplied, tranform to space
			if ( strpos($text_str,'+') !== false ) {
				$text_str = str_replace('+',' ',$text_str);
			}

			#$replace=array("%", "<", "{", "}", "&", "_", "\t");  
			$replace=array("\t");  
			$text_str = str_replace($replace,' ',$text_str);

			if ( strpos($text_str,'  ') !== false ) {
				$text_str = preg_replace("/ {2,}/", ' ',$text_str);
			}
			$text_str = trim($text_str);

			$this->debug['process'][] = "1a (text_str:$text_str)";
			
			if ( is_null($text_str) || $text_str == '' ) {
				$this->debug['process'][] = "2 Return(false)";
				return false;
			}
			if (preg_match('/^[^[:alpha:]]+/u', $text_str, $start_matches)) {
				$text_str=str_replace($start_matches[0],'',$text_str);
				$this->this_start_string=$start_matches[0];
			}
			if (preg_match("/(?:(?:\s|^)(?:\-?cf\.?|vel\.? sp\.? aff\.?|\-?aff\.?)(?:\s|$))|(?:\?+)/i", $text_str, $anno_matches)) {
				$text_str=trim(str_replace($anno_matches[0],' ',$text_str));
				$this->this_status_string=trim($anno_matches[0]);
			}
			$text_str=str_replace(' -','-',$text_str);
			$text_str=str_replace('- ','-',$text_str);
			$this->this_preprocessed_txt=$text_str;
			$text_str = preg_replace("/(?<=\s|^)(?:\S*[^[:alpha:][:space:]])?(indeterminad[ao]|undetermined|unknown|indet\.?|sp\.?\s+nov\.?|sp\.?)(?:[^[:alpha:][:space:]]\S*)?(?=\s|$)/i", ' ',$text_str);
			if ( strpos($text_str,'  ') !== false ) {
				$text_str = preg_replace("/ {2,}/", ' ',$text_str);
			}
			$text_str = trim($text_str);
			if (preg_match('/^(((?:[[:alpha:]]+aceae)|Cruciferae|Guttiferae|Umbelliferae|Compositae|Leguminosae|Palmae|Labiatae|Gramineae|Mimosoideae|Papilionoideae|Caesalpinioideae|fam(?:ily)?)((?:[^[:alpha:][:space:]]\S*)?))(?=\s+|$)/i', $text_str, $fam_matches)) {
				$text_str=trim(str_replace($fam_matches[0],'',$text_str));
				$this->this_family_string=$fam_matches[1];
				#$this->this_search_family=$fam_matches[2];
				$this->this_search_family=mb_strtoupper(mb_substr($fam_matches[2],0,1)) . mb_strtolower(mb_substr($fam_matches[2],1));
				$this->this_family_unmatched=$fam_matches[3];
				if (preg_match("/^fam(ily)?$/i", $this->this_search_family)) {
					$this->this_family_unmatched=$this->this_search_family . $this->this_family_unmatched;
					$this->this_search_family='';
				}
				if (! $this->parse_only && $this->this_search_family) {
					$searchFamilyName=$this->db->searchFamilyName($this->this_search_family);

					if (isset($searchFamilyName)) {
						foreach ($searchFamilyName as $returnedFamilyName){
							$this->saveFamilyMatches($returnedFamilyName->nameID, $this->this_search_family, 0, 'Y');
						}
					}
					$nm = new NearMatch();
					$this_near_match_family = $nm->near_match($this->this_search_family);
					$this_family_start = substr($this->this_search_family,0,3);
					$this_family_length = strlen($this->this_search_family);
					$family_res = $this->db->family_cur($this->search_mode, $this_near_match_family, $this_family_length, $this_family_start);
					if(count($family_res)) {
						foreach ($family_res as $drec) {
					        $family_match = $this->match_family($this->this_search_family, $drec->search_family_name);
					        if ($family_match['match']) {
								$phonetic_flag = $family_match['phonetic_match'] ? 'Y' : null;
								$this->saveFamilyMatches($drec->family_id, $drec->family, $family_match['edit_distance'], $phonetic_flag);
						   }
						} // end foreach
					}
				}
			}

			if ($text_str == 'exit' || $text_str =='end' || $text_str == 'q' || $text_str == '.') {
				return true;
			}

			//unhyphened trinormial
			if (preg_match('/^([[:alpha:]]+) ([[:alpha:]]+)[\.\s]([[:alpha:]]+)(.*)/', $text_str, $matches)) {
				$specific_epithet_str="$matches[2]-$matches[3]";
				$check_res=$this->db->checkSpecificEpithet(array($specific_epithet_str));
				foreach($check_res as $ck) {
				   if ($ck->count > 0 &&  mb_strtolower($ck->specificEpithet) == mb_strtolower($specific_epithet_str)) {	
						$text_str=str_ireplace_first("$matches[2] $matches[3]", "$matches[2]-$matches[3]", $text_str);
						$this->this_preprocessed_txt=str_ireplace_first("$matches[2] $matches[3]", "$matches[2]-$matches[3]", $this->this_preprocessed_txt); 
				   }
				}
			}

			$this->this_cleaned_txt=$text_str;

			$token=explode(" ", $text_str);
			for($i=0; $i < count($token); $i++) {
				if (preg_match('/^[[:alpha:]]+\.?$/u', $token[$i])) {
					if ($i == 0) {
						$token[$i]=mb_strtoupper(mb_substr($token[$i],0,1)) . mb_strtolower(mb_substr($token[$i],1));
					} elseif ( mb_strtoupper($token[$i]) == $token[$i] ) {
						$token[$i]=mb_strtolower($token[$i]);
					} else {
						break;
					}
				} 
			}
			$text_str=implode(" ",$token);


			// Clearing the temporary tables
			//$this->db->clearTempTables();

			// includes stripping of presumed non-relevant content including subgenera, comments, cf's, aff's, etc... to 

			// Normalizing the search text
			$n = new Normalize($this->db);

			$this->debug['process'][] = "3 (text_str:$text_str)";

			if(!$this->chop_overload) {
				// leave presumed genus + species + authority (in this instance), with  genus and species in uppercase
				$splitter = new Splitter($n,$text_str);
				
				$this->this_search_genus = $this_search_genus = $splitter->get('genus');
				$this->this_search_species = $this_search_species = $splitter->get('species');
				$this->this_authorities=$splitter->get('authors');
				$this->this_authority = $this_authority = end($this->this_authorities);			
				if (preg_match("/^gen(us)?$/i", $this->this_search_genus)) {
					$this->this_search_genus='';
				}
				if (preg_match("/^sp(p|ecies)?$/i", $this->this_search_species)) {
					$this->this_search_species='';
				}
				$infraspecies = $splitter->get('infraspecies');
				if (isset($infraspecies)) {
					if(isset($infraspecies[0])) {
						if ($infraspecies[0][0] != 'n/a') {
							$this->this_search_rank1=$infraspecies[0][0];
						}
						$this->this_search_infra1=$infraspecies[0][1];
					}
					if(isset($infraspecies[1])) {
						if ($infraspecies[1][0] != 'n/a') {
							$this->this_search_rank2=$infraspecies[1][0];
						}
						$this->this_search_infra2=$infraspecies[1][1];
					}
				}
				if ( NAME_PARSER == 'gni') {
					$this->gni_parser_result = $splitter->parsed_response;
				}
			}
			if ($this->parse_only) {
				return true;
			}

			// cache_flag switch detemines if caching is allowed for the source
			if($this->cache_flag == true) {

				if ( $this_search_genus != '' && $this_search_species != '' && $this_authority != '' ) {
					$cache_key = $this_search_genus . '-' . $this_search_species . '-' . $this_authority . '_' . $search_mode;
					$cache_path = $this->cache_path . $this->db->source . "/authority/";
				} else if ( $this_search_genus != '' && $this_search_species != '' ) {
					$cache_key = $this_search_genus . '-' . $this_search_species . '_' . $search_mode;
					$cache_path = $this->cache_path . $this->db->source . "/species/";
				} else if ( $this_search_genus != '' ) {
					$cache_key = $this_search_genus . '_' . $search_mode;
					$cache_path = $this->cache_path . $this->db->source . "/genus/";
				}
				
				$this->mkdir_recursive($cache_path);
				$this->_cache = new Cache( $cache_path );
				$this->_cache->setKey($cache_key);

			}

			$cache_loop_flag = false;
			if($cache == true && $this->cache_flag == true) {
				if($this->_cache->cache_exists()) $cache_loop_flag = true;
			}

			if(!$cache_loop_flag) {
				$search_str=$this->this_search_genus;
				if ($this->this_search_species) {
					$search_str .= ' ' . $this_search_species; 
				}
				if ($this->this_search_infra1) {
						if($this->this_search_rank1 != '') {
							$search_str .= ' ' . $this->this_search_rank1;
						}
					$search_str .= ' ' . $this->this_search_infra1; 
				}
				if ($this->this_search_infra2) {
						if($this->this_search_rank2 != '') {
							$search_str .= ' ' . $this->this_search_rank2;
						}
					$search_str .= ' ' . $this->this_search_infra2; 
				}

				$searchScientificName=$this->db->searchScientificName(array($text_str, $search_str));
				if (isset($searchScientificName)) {
					$has_match=0;
					foreach ($searchScientificName as $returnedScientificName){
						if ($returnedScientificName->specificEpithet != '') {
							$has_match=1;
							if ($returnedScientificName->nameRank != 'species' && $returnedScientificName->nameRank != 'nothospecies') {
								if ($returnedScientificName->infraspecificEpithet2 || $this->this_search_infra2) {
									$this->saveInfra2Matches($returnedScientificName->nameID, $returnedScientificName->scientificName, 0, 0, 0, 0, 0, 'Y');
								} elseif ($returnedScientificName->infraspecificEpithet || $this->this_search_infra1){
									$this->saveInfra1Matches($returnedScientificName->nameID, $returnedScientificName->scientificName, 0, 0, 0, 0, 'Y');
								}
							} else {
								$this->saveSpeciesMatches($returnedScientificName->nameID, $returnedScientificName->scientificName, 0, 0, 0, 'Y');
							}
						} elseif ($returnedScientificName->genus != '') {
							$has_match=1;
							$this->saveGenusMatches($returnedScientificName->nameID, $returnedScientificName->genus, 0, 'Y');
						}
					}
					if ($has_match) {
						return true;
					}
				}

				$this->debug['process'][] = "3a (this_search_genus:$this_search_genus) (this_search_species:$this_search_species) (this_authority:$this_authority)";
	
				$nm = new NearMatch();
				$this_near_match_genus = $nm->near_match($this_search_genus);
				$this_near_match_species = '';
	
				$this->debug['process'][] = "3b (this_near_match_genus:$this_near_match_genus)";
//TODO refactor inside of a method
				$this_genus_start = substr($this_search_genus,0,3);
				$this_genus_end = substr($this_search_genus,-3);
				$this_genus_length = strlen($this_search_genus);
//TODO_END
				$this->debug['process'][] = "3c (this_search_genus,$this_search_genus) (this_genus_start:$this_genus_start) (this_genus_end:$this_genus_end) (this_genus_length:$this_genus_length)";
	
				if ($this_search_species != '') {
					$this_near_match_species = $nm->near_match($this_search_species, 'epithet_only');
					$this_species_length = strlen($this_search_species);
					$this->debug['process'][] = "4 (this_search_species:$this_search_species) (this_near_match_species:$this_near_match_species) (this_species_length:$this_species_length)";
				}

				// now look for exact or near matches on genus first select candidate genera for edit distance (MDLD) test
	
				// for drec in genus_cur loop -- includes the genus pre-filter (main portion)
				$genus_res = $this->db->genus_cur3($this->search_mode, $this_near_match_genus, $this_near_match_species, $this_genus_length,$this_genus_start,$this_genus_end);
	
#				$this->debug['process'][] = array("5 (genus_res)" => $genus_res);

				$genus_matches = array();

				if(count($genus_res)) {
					// EJS -- attempt to reduce the amount of species_cur
					// this will be the naive approach
					foreach ($genus_res as $drec) {
					        $genus_match = $this->match_genera($this_search_genus, $drec->search_genus_name);
					        if ($genus_match['match']) {
					           // don't include a genus already in the array
						        if (!array_key_exists($drec->genus_id,$genus_matches)) {
									$phonetic_flag = $genus_match['phonetic_match'] ? 'Y' : null;
									$this->saveGenusMatches($drec->genus_id, $drec->genus, $genus_match['edit_distance'], $phonetic_flag);
									$this->genera_tested++;
								}
								$genus_matches[$drec->genus_id] = $genus_match;
							}
						} // end foreach
					}
					$species_matches = array();

					if ($this_search_species != '' && count ($genus_matches)) {
						$species_res = $this->db->species_cur_in2(array_keys($genus_matches), $this_species_length );
						if (isset($species_res)) {
							foreach ($species_res as $drec) {
								$species_epithets_match = $this->match_species_epithets($this_search_species, $drec->search_species_name);
								$genus_match=$genus_matches[$drec->genus_id];
								$binomials_match = $this->match_matches(array($genus_match, $species_epithets_match));
								if ($binomials_match['match']) {		
									if (!array_key_exists($drec->species_id,$species_matches)) {
										$binomial_phonetic_flag = $binomials_match['phonetic_match'] ? 'Y' : null;
										$this->saveSpeciesMatches($drec->species_id, $drec->genus_species, $genus_match['edit_distance'], $species_epithets_match['edit_distance'], $binomials_match['edit_distance'], $binomial_phonetic_flag);
										$this->species_tested++;
									}
									$species_epithets_match['genus_match']=$genus_match;
									$species_matches[$drec->species_id]=$species_epithets_match;
								}
							}
					// EJS -- end
						}
					}
					$infra1_matches = array();
					if ($this->this_search_infra1 != '' && count ($species_matches)) {
						$this_infra1=$this->this_search_infra1;
						$this_rank1=$this->this_search_rank1;
						$this_infra1_length = strlen($this_infra1);
						$infra1_res = $this->db->infra1_cur_in(array_keys($species_matches), $this_infra1_length );
						if (isset($infra1_res)) {
							foreach ($infra1_res as $drec) {
								$infra1_match = $this->match_species_epithets($this_infra1, $drec->search_infra1_name);
								$species_match=$species_matches[$drec->species_id];
								$genus_match=$species_match["genus_match"];
								$binomials_match = $this->match_matches(array($genus_match, $species_match, $infra1_match));
								if ($binomials_match['match']) {		
									if (!array_key_exists($drec->infra1_id,$infra1_matches)) {
										$binomial_phonetic_flag = $binomials_match['phonetic_match'] ? 'Y' : null;
										$this->saveInfra1Matches($drec->infra1_id, $drec->species_infra1, $genus_match['edit_distance'], $species_match['edit_distance'], $infra1_match['edit_distance'], $binomials_match['edit_distance'], $binomial_phonetic_flag);
									}
									$infra1_match["species_match"]=$species_match;
									$infra1_matches[$drec->infra1_id]=$infra1_match;
								}
							}
					// EJS -- end
						}
					}
					$infra2_matches = array();
					if ($this->this_search_infra2 != '' && count ($infra1_matches)) {
						$this_infra2=$this->this_search_infra2;
						$this_rank2=$this->this_search_rank2;
						$this_infra2_length = strlen($this_infra2);
						$infra2_res = $this->db->infra2_cur_in(array_keys($species_matches), $this_infra2_length );
						if (isset($infra2_res)) {
							foreach ($infra2_res as $drec) {
								$infra2_match = $this->match_species_epithets($this_infra2, $drec->search_infra2_name);
								$infra1_match=$infra1_matches[$drec->infra1_id];
								$species_match=$infra1_match['species_match'];
								$genus_match=$species_match["genus_match"];
								$binomials_match = $this->match_matches(array($genus_match, $species_match, $infra1_match, $infra2_match));
								if ($binomials_match['match']) {		
									if (!array_key_exists($drec->infra2_id,$infra2_matches)) {
										$binomial_phonetic_flag = $binomials_match['phonetic_match'] ? 'Y' : null;
										$this->saveInfra2Matches($drec->infra1_id, $drec->species_infra1, $genus_match['edit_distance'], $species_match['edit_distance'],$infra2_match['edit_distance'],  $infra1_match['edit_distance'], $binomials_match['edit_distance'], $binomial_phonetic_flag);
									}
									$infra2_match["infra1_match"]=$infra1_match;
									$infra2_matches[$drec->infra2_id]=$infra2_match;
								}
							}
					// EJS -- end
						}
					}
				} // End Cache Loop Flag
				return true;
			}

		/**
		 * generateResponse
		 * Result generation section (including ranking, result shaping,
		 * and authority comparison) - for demo purposes only
		 * NB, in a production system this would be replaced by something
		 * more appropriate, e.g. write to a file or database table,
		 * generate a HTML page for web display,
		 * generate XML response, etc. etc.
		 * @param boolean $cache
		 * @return boolean
		 */
		public function generateResponse($cache) {

			$cache_loop_flag = false;
			if($cache == true && $this->cache_flag == true) {
				if($this->_cache->cache_exists()) $cache_loop_flag = true;
			}
	
	// 		if($cache == true && $this->_cache->cache_exists() && $this->cache_flag == true) {
			if($cache_loop_flag) {
			
				$this->data = $this->_cache->fetch();
				$data_array = json_decode($this->data,true);
				$data_array['cache'] = $cache;
				$this->data = json_encode($data_array);
				
			} else {
			
				// genus exact, phonetic, and other near matches
				$this->output['input'] = $this->searchtxt;
				if ( NAME_PARSER == 'gni' && isset($this->gni_parser_result) ) {
					$this->output['gni_parser_result'] = $this->gni_parser_result;
				}
				if (isset($this->this_search_family)) {
					$this->getFamilyAuthority(0,'exact',$this->this_authority);
					$this->getFamilyAuthority('P','phonetic',$this->this_authority);
					$this->getFamilyAuthority(1,'near_1',$this->this_authority);
					$this->getFamilyAuthority(2,'near_2',$this->this_authority);

					if ($this->search_mode == 'extended') {
						$this->getFamilyAuthority(3,'near_4',$this->this_authority);
						$this->getFamilyAuthority(4,'near_4',$this->this_authority);
					}
				}
				$this->debug['generateResponse'][] = "1 (input:" . $this->searchtxt . ")";
		
				// Genus Exact
				$this->debug['generateResponse'][] = "1a (getGenusAuthority:exact)";
				$this->getGenusAuthority(0,'exact', $this->this_authority);
				// Genus Phonetic
				$this->debug['generateResponse'][] = "1b (getGenusAuthority:phonetic)";
				$this->getGenusAuthority('P','phonetic', $this->this_authority);
				// Genus near matches
				$this->debug['generateResponse'][] = "1c (getGenusAuthority:near_1)";
				$this->getGenusAuthority(1,'near_1', $this->this_authority);
				$this->debug['generateResponse'][] = "1d (getGenusAuthority:near_2)";
				$this->getGenusAuthority(2,'near_2', $this->this_authority);
				if ($this->search_mode == 'extended') {
					$this->getGenusAuthority(3,'near_3', $this->this_authority);
					$this->getGenusAuthority(4,'near_4', $this->this_authority);
				}
	
				if ($this->this_search_species) {
					// species exact, phonetic, and other near matches
		
					$this->debug['generateResponse'][] = "2a (getSpeciesAuthority:exact) ($this->this_authority)";
					$this->getSpeciesAuthority( 0, 'exact', $this->this_authority );
					$this->debug['generateResponse'][] = "2b (getSpeciesAuthority:phonetic) ($this->this_authority)";
					$this->getSpeciesAuthority( 'P', 'phonetic', $this->this_authority );
					$this->debug['generateResponse'][] = "2c (getSpeciesAuthority:near_1) ($this->this_authority)";
					$this->getSpeciesAuthority( 1, 'near_1', $this->this_authority );
					$this->debug['generateResponse'][] = "2d (getSpeciesAuthority:near_2) ($this->this_authority)";
					$this->getSpeciesAuthority( 2, 'near_2', $this->this_authority );
					if ($this->search_mode == 'extended') {
						$this->getSpeciesAuthority( 3, 'near_3', $this->this_authority );
						$this->getSpeciesAuthority( 4, 'near_4', $this->this_authority );
					}

					// -- Here is the result shaping section (only show ED 3 if no ED 1,2 or phonetic matches, only
					// --   show ED 4 if no ED 1,2,3 or phonetic matches). By default shaping is on, unless disabled
					// --   via the input parameter "search_mode" set to 'no_shaping'.
					// --   In this demo we supplement any actual shaping with a message to show that it has been invoked,
					// --   to show the system operates correctly.
					#if ($this->search_mode != 'no_shaping') {
					#	if(!isset($this->species_found) || $this->species_found != 'Y') {
					#		$this->getSpeciesAuthority( 3, 'near_3', $this->this_authority );
					#	}
					#	if(!isset($this->species_found) || $this->species_found != 'Y') {
					#		$this->getSpeciesAuthority( 4, 'near_4', $this->this_authority );
					#	}
					#} // END temp_species_count > 0 and "no_shaping"
				} // END If this_search_species
				if ($this->this_search_infra1) {
					$this->getInfra1Authority(0,'exact',$this->this_authority);
					$this->getInfra1Authority('P','phonetic',$this->this_authority);
					$this->getInfra1Authority(1,'near_1',$this->this_authority);
					$this->getInfra1Authority(2,'near_2',$this->this_authority);
					if ($this->search_mode == 'extended') {
						$this->getInfra1Authority(3,'near_3',$this->this_authority);
						$this->getInfra1Authority(4,'near_4',$this->this_authority);
					}
				} elseif($this->this_search_infra2) {
					$this->getInfra2Authority(0,'exact',$this->this_authority);
					$this->getInfra2Authority('P','phonetic',$this->this_authority);
					$this->getInfra2Authority(1,'near_1',$this->this_authority);
					$this->getInfra2Authority(2,'near_2',$this->this_authority);
					if ($this->search_mode == 'extended') {
						$this->getInfra2Authority(3,'near_3',$this->this_authority);
						$this->getInfra2Authority(4,'near_4',$this->this_authority);
					}
				}
				
				if($this->output_type == 'rest') {
					if($this->debug_flag) {
						$this->data = json_encode( array( 'success' => true, 'cache' => $cache, 'data' => $this->output, 'debug' => $this->debug ) );
					} else {
						$this->data = json_encode( array( 'success' => true, 'cache' => $cache, 'data' => $this->output));
					}
				} else {
					$this->data = $this->output;
				}
	
				if($this->cache_flag == true) {
					if( ! $this->_cache->cache_exists()) {
						if($this->debug_flag) {
							$op_array = array (
								'success' => true
								, 'cache_date' => date('Y-m-d')
								, 'data' => $this->output
								, 'debug' => $this->debug
							);
						} else {
							$op_array = array (
								'success' => true
								, 'cache_date' => date('Y-m-d')
								, 'data' => $this->output
							);
			
						}
						$op = json_encode($op_array);
						$this->_cache->update($op);
						$tmp_cache_key = $this->_cache->getKey();
						$this->_cache->setKey($tmp_cache_key . '_debug');
						$dbg = @json_encode($this->debug);
						$this->_cache->update($dbg);
						$this->_cache->setKey($tmp_cache_key);
					}
				}
			}
	
			return true;
	
		}

	/**
	 * Generate the genus part of the output from the search results.
	 * @param integer|string $genus_result_cur : 0 = exact, 'P' = phonetic, 1 = near_1, 2 = near_2 ...
	 * @param string $type : 'exact', 'phonetic', 'near_1', 'near_2' ...
	 * @return boolean
	 */
	private function getGenusAuthority( $genus_result_cur = 0, $type = 'exact', $this_authority) {
		if(!isset($this->output['genus'])) {
			$this->output['genus']=array();
		}

		$this->debug['getGenusAuthority'][] = "1 (genus_result_cur:$genus_result_cur)";
		$gen_res = $this->genus_result_cur($genus_result_cur);
		$this->debug['getGenusAuthority'][] = $gen_res;

		if(count($gen_res)) {
			foreach($gen_res as $drec) {
				//select ancillary info here as desired
				//(authority only is shown in this example,
				//but would most likely be more as available in a production system)
				$res = $this->db->getAuthority2($drec['genus_id']);

				$temp_authority = isset($res) ? $res->authority : '';
				$result = array(
					  'genus' => $drec['genus']
					, 'temp_authority' => $temp_authority
					, 'genus_id' => $drec['genus_id']
					, 'genus_ed' => $drec['genus_ed']
				);
				if ($this_authority) {
					$auth_similarity = $this->compare_auth($this_authority, $temp_authority);
					$result['auth_similarity'] = $auth_similarity;
				}
				$this->output['genus'][$type][] = $result;
			}
		}
		return true;
	}
	private function getFamilyAuthority($family_result_cur = 0, $type = 'exact', $this_authority) {
		if(!isset($this->output['family'])) {
			$this->output['family']=array();
		}
		$fam_res = $this->family_result_cur($family_result_cur);

		if(count($fam_res)) {
			foreach($fam_res as $drec) {
				$res = $this->db->getAuthority2($drec['family_id']);

				$temp_authority = isset($res) ? $res->authority : '';
				$result = array(
					  'family' => $drec['family']
					, 'temp_authority' => $temp_authority
					, 'family_id' => $drec['family_id']
					, 'family_ed' => $drec['family_ed']
				);
				if ($this_authority) {
					$auth_similarity = $this->compare_auth($this_authority, $temp_authority);
					$result['auth_similarity'] = $auth_similarity;
				}
				$this->output['family'][$type][] = $result;
			}
		}
		return true;
	}
	/**
	 * Generate the species part of the output from the search results.
	 * @param integer|string $species_result_cur : 0 = exact, 'P' = phonetic, 1 = near_1, 2 = near_2 ...
	 * @param string $type : 'exact', 'phonetic', 'near_1', 'near_2' ...
	 * @param string $this_authority : the authority part of thr search string input
	 * @return boolean
	 */
	private function getSpeciesAuthority( $species_result_cur = 0, $type = 'exact', $this_authority ) {
		if(!isset($this->output['species'])) {
			$this->output['species']=array();
		}
		$this->debug['getSpeciesAuthority'][] = $this;
		$this->debug['getSpeciesAuthority'][] = "1 (species_result_cur:$species_result_cur)";
		$species_res = $this->species_result_cur($species_result_cur);
		$this->debug['getSpeciesAuthority'][] = $species_res;

		if(count($species_res)) {
			$this->species_found = 'Y';
			foreach($species_res as $drec) {
				$res = $this->db->getAuthority2($drec['species_id']);
				$temp_authority='';
				if (isset($res)) {
					$temp_authority = $res->authority;
				}

				$this->debug['getSpeciesAuthority'][] = "2 (temp_authority:$temp_authority)";
				$result=array(
					'genus_species' => $drec['genus_species']
					, 'temp_authority' => $temp_authority
					, 'species_id' => $drec['species_id']
					, 'genus_ed' => $drec['genus_ed']
					, 'species_ed' => $drec['species_ed']
				);
				if ($this_authority) {
					$auth_similarity = $this->compare_auth($this_authority, $temp_authority);
					$result['auth_similarity'] = $auth_similarity;
				}
				$this->output['species'][$type][] = $result;
			}
		}
		return( true );
	}
	private function getInfra1Authority( $infra1_result_cur = 0, $type = 'exact', $this_authority ) {
		if(!isset($this->output['infra1'])) {
			$this->output['infra1']=array();
		}
		$infra1_res = $this->infra1_result_cur($infra1_result_cur);

		if(count($infra1_res)) {
			foreach($infra1_res as $drec) {
				$res = $this->db->getAuthority2($drec['infra1_id']);
				$temp_authority='';
				if (isset($res)) {
					$temp_authority = $res->authority;
				}
				$result=array(
					'genus_species_infra1' => $drec['genus_species_infra1']
					, 'temp_authority' => $temp_authority
					, 'infra1_id' => $drec['infra1_id']
					, 'genus_ed' => $drec['genus_ed']
					, 'species_ed' => $drec['species_ed']
					, 'infra1_ed' => $drec['infra1_ed']
				);

				if ($this_authority) {
					$auth_similarity = $this->compare_auth($this_authority, $temp_authority);
					$result['auth_similarity'] = $auth_similarity;
				}
				$this->output['infra1'][$type][] = $result;
			}
		}
		return( true );
	}
	private function getInfra2Authority( $infra2_result_cur = 0, $type = 'exact', $this_authority ) {
		if(!isset($this->output['infra2'])) {
			$this->output['infra2']=array();
		}
		$infra2_res = $this->infra2_result_cur($infra2_result_cur);

		if(count($infra2_res)) {
			foreach($infra2_res as $drec) {
				$res = $this->db->getAuthority2($drec['infra2_id']);
				$temp_authority='';
				if (isset($res)) {
					$temp_authority = $res->authority;
				}

				$result=array(
					'genus_species_infra1_infra2' => $drec['genus_species_infra1_infra2']
					, 'temp_authority' => $temp_authority
					, 'infra2_id' => $drec['infra2_id']
					, 'genus_ed' => $drec['genus_ed']
					, 'species_ed' => $drec['species_ed']
					, 'infra1_ed' => $drec['infra1_ed']
					, 'infra2_ed' => $drec['infra2_ed']
				);

				if ($this_authority) {
					$auth_similarity = $this->compare_auth($this_authority, $temp_authority);
					$result['auth_similarity'] = $auth_similarity;
				}
				$this->output['infra1'][$type][] = $result;
			}
		}
		return( true );
	}

	private function saveFamilyMatches($family_id,$family,$family_ed,$phonetic_flag) {
		$this->familymatches[$family_id]=array($family_id,$family,$family_ed,$phonetic_flag);
	}

	private function saveGenusMatches($genus_id,$genus,$genus_ed,$phonetic_flag) {
		$this->genusmatches[$genus_id]=array($genus_id,$genus,$genus_ed,$phonetic_flag);
	}
	private function saveSpeciesMatches($species_id,$genus_species,$genus_ed,$species_ed,$gen_sp_ed,$phonetic_flag) {
		$this->speciesmatches[$species_id]=array($species_id,$genus_species,$genus_ed,$species_ed,$gen_sp_ed,$phonetic_flag);
	}
	private function saveInfra1Matches($infra1_id,$genus_species_infra1,$genus_ed,$species_ed,$infra1_ed,$gen_sp_infra1_ed,$phonetic_flag) {
		$this->infra1matches[$infra1_id]=array($infra1_id,$genus_species_infra1,$genus_ed,$species_ed,$infra1_ed,$gen_sp_infra1_ed,$phonetic_flag);
	}
	private function saveInfra2Matches($infra2_id,$genus_species_infra1_infra2,$genus_ed,$species_ed,$infra2_ed,$infra1_id,$gen_sp_infra1_infra2_ed,$phonetic_flag) {
		$this->infra2matches[$infra2_id]=array($infra2_id,$genus_species_infra1_infra2,$genus_ed,$species_ed,$infra2_ed,$infra1_id,$gen_sp_infra1_infra2_ed,$phonetic_flag);
	}
	private function family_result_cur($this_ed = null) {
		$family_cur_keys=array('family_id', 'family', 'family_ed', 'phonetic_flag');
		$value=array();
		foreach (array_values($this->familymatches) as $family_cur) {
			if ($this_ed === 0 && $family_cur[2] == $this_ed) {
				$value[]=array_combine($family_cur_keys, $family_cur);
			} elseif ($this_ed === 'P' && $family_cur[2] > 0 && $family_cur[3] == 'Y') {
				$value[]=array_combine($family_cur_keys, $family_cur);
			} elseif ($this_ed != 'P' && $this_ed > 0 && is_null($family_cur[3]) && $family_cur[2] == $this_ed) {
				$value[]=array_combine($family_cur_keys, $family_cur);
			}
		}
		return ($value);
	}
	private function genus_result_cur($this_ed = null) {
		$genus_cur_keys=array('genus_id', 'genus', 'genus_ed', 'phonetic_flag');
		$value=array();
		foreach (array_values($this->genusmatches) as $genus_cur) {
			if ($this_ed === 0 && $genus_cur[2] == $this_ed) {
				$value[]=array_combine($genus_cur_keys, $genus_cur);
			} elseif ($this_ed === 'P' && $genus_cur[2] > 0 && $genus_cur[3] == 'Y') {
				$value[]=array_combine($genus_cur_keys, $genus_cur);
			} elseif ($this_ed != 'P' && $this_ed > 0 && is_null($genus_cur[3]) && $genus_cur[2] == $this_ed) {
				$value[]=array_combine($genus_cur_keys, $genus_cur);
			}
		}
		return ($value);
	}
	private function species_result_cur($this_ed = null) {
		$species_cur_keys=array('species_id', 'genus_species', 'genus_ed', 'species_ed', 'gen_sp_ed', 'phonetic_flag');
		$value=array();
		foreach (array_values($this->speciesmatches) as $species_cur) {
			if ($this_ed === 0 && $species_cur[3] == $this_ed) {
				$value[]=array_combine($species_cur_keys, $species_cur);
			} elseif ($this_ed === 'P' && $species_cur[3] > 0 && $species_cur[5] == 'Y') {
				$value[]=array_combine($species_cur_keys, $species_cur);
			} elseif ($this_ed != 'P' && $this_ed > 0 && is_null($species_cur[5]) && $species_cur[3] == $this_ed) {
				$value[]=array_combine($species_cur_keys, $species_cur);
			}
		}
		return ($value);
	}
	private function infra1_result_cur($this_ed = null) {
		$infra1_cur_keys=array('infra1_id', 'genus_species_infra1', 'genus_ed', 'species_ed', 'infra1_ed', 'gen_sp_infra1_ed', 'phonetic_flag');
		$value=array();
		foreach (array_values($this->infra1matches) as $infra1_cur) {
			if ($this_ed === 0 && $infra1_cur[4] == $this_ed) {
				$value[]=array_combine($infra1_cur_keys, $infra1_cur);
			} elseif ($this_ed === 'P' && $infra1_cur[4] > 0 && $infra1_cur[6] == 'Y') {
				$value[]=array_combine($infra1_cur_keys, $infra1_cur);
			} elseif ($this_ed != 'P' && $this_ed > 0 && is_null($infra1_cur[6]) && $infra1_cur[4] == $this_ed) {
				$value[]=array_combine($infra1_cur_keys, $infra1_cur);
			}
		}
		return ($value);
	}
	private function infra2_result_cur($this_ed = null) {
		$infra2_cur_keys=array('infra2_id', 'genus_species_infra1_infra2', 'genus_ed', 'species_ed', 'infra2_ed', 'infra1_ed', 'gen_sp_infra1_infra2_ed', 'phonetic_flag');
		$value=array();
		foreach (array_values($this->infra2matches) as $infra2_cur) {
			if ($this_ed === 0 && $infra2_cur[4] == $this_ed) {
				$value[]=array_combine($infra2_cur_keys, $infra2_cur);
			} elseif ($this_ed === 'P' && $infra2_cur[4] > 0 && $infra2_cur[7] == 'Y') {
				$value[]=array_combine($infra2_cur_keys, $infra2_cur);
			} elseif ($this_ed != 'P' && $this_ed > 0 && is_null($infra2_cur[7]) && $infra2_cur[4] == $this_ed) {
				$value[]=array_combine($infra2_cur_keys, $infra2_cur);
			}
		}
		return ($value);
	}



	/**
	 * Translates the given string
	 * @param string $string : string to translate
	 * @param string $auth_chars
	 * @param string $new_chars
	 * @return string
	 */
	private function translate( $string, $auth_chars, $new_chars ) {
		$this->debug['translate'][] = "1 (string:$string)";
		$auth_chars = str_split( utf8_decode($auth_chars) );
		$new_chars = str_split($new_chars);
		$newStr = '';
		$string = utf8_decode($string);

		for ($i=0; $i< strlen($string); $i++) {
			$pos = array_search($string[$i], $auth_chars);
			if ($pos !== false) {
				$newStr .= $new_chars[$pos];
			} else {
				$newStr .= $string[$i];
			}
		}
		$this->debug['translate'][] = "Return: ($newStr)";
		return( $newStr );
	}

	/**
	 * Creates folders according to the given path
	 * @param string $pathname
	 * @return boolean
	 */

	public function mkdir_recursive( $pathname )	{
		is_dir(dirname($pathname)) || $this->mkdir_recursive(dirname($pathname));
		return is_dir($pathname) || @mkdir($pathname, 0775);
	}

		/**
		 * getXML
		 * Get the Output in xml format
		 * @return string|bool : the xml string or false in case output is absent
		 */
		public function getXML() {

			if(is_array($this->output) && count($this->output)){
				$this->xml->addHeader('true');
				$this->xml->addInput($this->input);
				$this->xml->addChild('result');

				foreach($this->output as $key => $value) {
					$this->createChild($key, $value, $this->xml->result);
				}

				return $this->xml->getXML();
				
			} else if($this->output != '') {
				$this->xml->addHeader('true');
				$this->xml->addInput();
				if(is_array($this->input) && count($this->input)) {
					foreach($this->input as $input) {
						$this->xml->input->addChild('input1',$input);
					}
				}
				$this->xml->addChild('result',$this->output);
				return $this->xml->getXML();
			} else {
				return false;
			}
		}

		/**
		 * Recursive function to build child nodes
		 */

		public function createChild ($key,$ar,$node) {
			if(is_array($ar) && count($ar)) {
				$node->addChild($key);
				foreach($ar as $k => $v) {
					if(is_numeric($k)) {
						foreach($v as $k1 => $v1) {
							$this->createChild($k1,$v1,$node->$key);
						}
					} else {
						$this->createChild($k,$v,$node->$key);
					}
				}
				
			} else {
				$node->addChild($key, $ar);
				return $node->$key;
			}
		}


} // End Class Taxamatch
?>
