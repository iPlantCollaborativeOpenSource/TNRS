<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

	include ('class.xml.php');

	/**
	 * Class Normalize
	 * Used to convert a string to a standarized format.
	 */
	class Normalize {

		/**
		 * To hold the input
		 * @var string
		 */
		public $input;

		/**
		 * Whether to debug or nor
		 * @var bool|integer
		 */
		public $debug_flag;

		/**
		 * To hold output data
		 * @var string
		 */
		public $output;

		/**
		 * xml object
		 * @var mixed
		 */
		public $xml;

		/**
		 * Constructor 
		 */
		public function __construct( $db=null ) {
			$this->db = $db;
//			$this->xml = new SimpleXml(XML_STRING);
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
		 * Checks whether a given character is a valid one or not and returns the character if valid and
		 * NULL if not
		 * @param string $a_char
		 * @return string : character
		 */
		private function char_test( $a_char ) {

			if ( (ord($a_char) >= 65 && ord($a_char) <= 90) || (ord($a_char) == 32) || (ord($a_char) == 46) ) {			
				return( $a_char );
			}
			
			return( NULL );
			
		}

		/**
		 * Reduce Spaces
		 * This will reduce the string to only allow once space between characters
		 * @param string $str : string to reduce space
		 * @return string : string with only once space between characters
		 */
		private function reduce_spaces( $str ) {
		
			$str = preg_replace("/ {2,}/", ' ', $str );
			$str = trim( $str );
			
			return( $str );
		}

		/**
		 * Filters a string and allows only good characters in it.
		 * @param string $str
		 * @return string : Filtered string
		 */
		private	function good_chars( $str ) {
	
			$result = '';
			$a_char = NULL;

			if ( $str == NULL ) {
			 return( NULL );
			} else {
				for ($i=0; $i < strlen($str); $i++) {
					$a_char = $str[$i];
					$result .= $this->char_test( $a_char );
				}
			}
//			print $result;
			return( $result );
		
		}

		/**
		 * Function: normalize_auth
		 * Purpose: Produce a normalized version of authority of a taxon name
		 * @author Tony Rees (Tony.Rees@csiro.au)
		 * Date created: March 2008
		 * Inputs: authority string as str
		 * Outputs: normalized version of str
		 * Remarks:
		 *  (1) Performs authority expension of known abbreviated authornames using
		 *   table "auth_abbrev_test1" (must be available and populated with relevant content)
		 *  (2) Recognises "and", "et", "&" as equivalents (special case for "et al.") - all normalized to ampersand
		 *  (3) Recognises (e.g.) "Smith 1980" and "Smith, 1980" as equivalents - comma is removed in these cases
		 *  (4) Recognises (e.g.) "F. J. R. Taylor, 1980" and "F.J.R. Taylor, 1980" as equivalents -
		 *      extra space after full stops is ignored in these cases
		 *  (5) Returns uppercase string, diacritical marks intact
		 *
		 * @param string $str : authority string
		 * @param integer $upcase : convert to uppercase if $upcase = 1
		 * @return string : Normalized author string
		 */
		public function normalize_auth( $str, $upcase=1 ) {

			$this->debug['normalize_auth'][] = "1";
			$this->input = $str;
			$this->output = '';
			$temp = $str = trim($str);
			
  		if ( ($str == NULL) || ($str == '') ) {
				$this->debug['normalize_auth'][] = "1a";
		    return '';
			}

			if ( ( $temp == null ) || ( $temp == '') ) {
				$this->debug['normalize_auth'][] = "2a";
				return('');
			} else {

				$this->debug['normalize_auth'][] = "2b";
			
				// treat some special cases here (probably more to come)
				if ( $temp == 'L.' ) {
					$temp = 'Linnaeus';
					$this->debug['normalize_auth'][] = "3a (temp:$temp)";
//				} elseif ( $temp like '(L.)%' ) {
				} elseif ( ereg('^\(L\.\)', $temp) ) {
					$temp = '(Linnaeus)' . substr($temp,5);
					$this->debug['normalize_auth'][] = "3b (temp:$temp)";
//				} elseif ( ( $temp like 'L., 1%' ) || ( $temp like 'L. 1%' ) ) {
				} elseif ( ereg('^L\., 1', $temp) || ereg('^L\. 1', $temp) ) {
					$temp = 'Linnaeus ' . substr($temp,3);				
					$this->debug['normalize_auth'][] = "3c (temp:$temp)";
//				} elseif ( $temp like '(L., 1%' ) || ( $temp like '(L. 1%' ) {
				} elseif ( ereg('^\(L\., 1', $temp) || ereg('^\(L\. 1', $temp) ) {
					$temp = '(Linnaeus ' . substr($temp,4);
					$this->debug['normalize_auth'][] = "3d (temp:$temp)";
				} elseif ( ( $temp == 'DC' ) || ( $temp == '(DC)' ) ) {
					$temp = str_replace('DC', 'de Candolle', $temp);
					$this->debug['normalize_auth'][] = "3e (temp:$temp)";
				} elseif ( ( $temp == 'D.C.') || ( $temp == '(D.C.)' ) ) {
					$temp = str_replace('D.C.', 'de Candolle', $temp);
					$this->debug['normalize_auth'][] = "3f (temp:$temp)";
				}
				
				// add space after full stops, except at end (NB, will also add spece before some close brackets)
				$temp = rtrim( str_replace('.', '. ', $temp) );
				$this->debug['normalize_auth'][] = "4 (temp:$temp)";
				
				//normalise "et", "and" to ampersand (et al. is a special case)
//				if ( $temp like '% et al%' ) {
				if ( ereg(' et al', $temp) ) {
					$temp = str_replace(' et al','zzzzz', $temp);
					$this->debug['normalize_auth'][] = "4a (temp:$temp)";
				}
				
//				$temp = str_replace(temp,' et ',' '||'&'||' ');
//				$temp = replace(temp,' and ',' '||'&'||' ');
				$temp = str_replace(' et ',' & ', $temp );
				$temp = str_replace(' and ',' & ', $temp );
//				if ( $temp like '%zzzzz%' ) {
//				if ( ereg('zzzzz', $temp) ) {
				$temp = str_replace('zzzzz',' et al', $temp);
//				}

				$this->debug['normalize_auth'][] = "5 (temp:$temp)";
				
				//remove commas before dates (only)
				//	like '%, 17%'
				if ( ereg(', 17', $temp) ) {
					$temp = str_replace(', 17',' 17', $temp);
					$this->debug['normalize_auth'][] = "5a (temp:$temp)";
				}
				
				//	like '%, 18%'
				if ( ereg(', 18', $temp) ) {
					$temp = str_replace(', 18',' 18', $temp);
					$this->debug['normalize_auth'][] = "5b (temp:$temp)";
				}
				
				//	like '%, 19%'
				if ( ereg(', 19', $temp) ) {
					$temp = str_replace(', 19',' 19', $temp);
					$this->debug['normalize_auth'][] = "5c (temp:$temp)";
				}
				
				//	like '%, 20%'
				if ( ereg(', 20', $temp) ) {
					$temp = str_replace(', 20',' 20', $temp);
					$this->debug['normalize_auth'][] = "5d (temp:$temp)";
				}
				
				// reduce multiple internal spaces to single space
				$temp = $this->reduce_spaces( $temp );
				
				//	like '% -%'
				$temp = str_replace(' -', '-', $temp);

				$this->debug['normalize_auth'][] = "6 (temp:$temp)";
				$elapsed_chars = '';
				
				foreach( explode(' ', $temp) as $this_word ) {
				
					$this->debug['normalize_auth'][] = "7 (this_word:$this_word)";
					
					//	like '(%'
					if ( ereg('^\(', $this_word) ) {
						$elapsed_chars .= '(';
						$this_word = substr( $this_word, 1 );
						$this->debug['normalize_auth'][] = "7a (this_word:$this_word) (elapsed_chars:$elapsed_chars)";
					}

					// like '%.%' AND 2 or more characters					
					if ( ereg('\.', $this_word) && ( strlen($this_word) >= 2 ) ) {
					
						// See if there is a Full Author name in the authoritative db that matches the abbrivation
						//$this_auth_full = $this->db->get_auth_full( $this_word );
						$this_auth_full = null;
						$this->debug['normalize_auth'][] = "7b (this_auth_full:$this_auth_full) (this_word:$this_word) (elapsed_chars:$elapsed_chars)";
						
						// If Author found set it to $this_word and clear this_auth_full
						if ( $this_auth_full != null ) {
							$this_word = $this_auth_full;
							$this_auth_full = null;
						}
					}
					
					// Add back the word to the final translation
					$elapsed_chars .= $this_word . ' ';
					$this->debug['normalize_auth'][] = "7c (this_word:$this_word) (elapsed_chars:$elapsed_chars)";
				}
				
				$elapsed_chars = $this->reduce_spaces( str_replace(' )', ')', $elapsed_chars) );

				if ($upcase) {
					$this->output = strtoupper( trim( $elapsed_chars ) ) ;
				} else {
					$this->output = trim( $elapsed_chars ) ;
				}
				return $this->output;
			}

		}
		
		/**
		 * Function: normalize
		 * Purpose: Produces normalized version of an input string (scientific name components)
		 * @author Tony Rees (Tony.Rees@csiro.au)
		 * Date created: June 2007-November 2008
		 * Inputs: input string as str (this version presumes genus, genus+species, or
		 * genus+species+authority)
		 * Outputs: normalized version of input string, for match purposes
		 * Remarks:
		 *    (1) Removes known text elements e.g.
		 *      'aff.', 'cf.', 'subsp.', subgenera if enclosed in brackets, etc. as desired
		 *    (2) Removes accented and non A-Z characters other than full stops 
		 *       (in scientific name portions)
		 *    (3) Returns uppercase scientific name (genus + species only) 
		 *       plus unaltered (presumed) authority
		 *     examples;
		 *       Anabaena cf. flos-aquae Ralfs ex Born. et Flah. => ANABAENA FLOSAQUAE Ralfs 
		 *       ex Born. et Flah.
		 *       Abisara lemÈe-pauli => ABISARA LEMEEPAULI
		 *       Fuc/us Vesiculos2us => FUCUS VESICULOSUS
		 *       Buffo ignicolor LacÈpËde, 1788 => BUFFO IGNICOLOR LacÈpËde, 1788
		 *       Barbatia (Mesocibota) bistrigata (Dunker, 1866) => BARBATIA BISTRIGATA (Dunker, 1866)
		 *    (4) Thus version does not handle genus+author, or genus+species+infraspecies
		 *       (second" good" term is presumed to be species epithet, anything after is 
		 *       considered to be start of the authority), however could be adapted further as required
		 *    (5) There is a separate function "normalize_auth" for normalizing authorities when required
		 *      (e.g. for authority comparisons)
		 *
		 * @param string $str : input string ( genus, genus+species, or genus+species+authority )
		 * @return string : normalized string
		 */
		public function normalize( $str = NULL ) {
			
			unset($this->debug['normalize']);

			$this->set('input',$str);
			$this->output = '';

			$temp = '';
			$first_str_part = NULL;
			$second_str_part = NULL;
			$temp_genus = '';
			$temp_species = '';
			$temp_genus_species = '';
			$temp_authority = '';
			
			$this->debug['normalize'][] = "1";

			if ( ($str == NULL) || ( trim($str) == '') ) {
				$this->debug[] = "N1a<br>";
				return '';
			} else {
				//	trim any leading, trailing spaces or line feeds
				$temp = trim( $str );
				$this->debug['normalize'][] = "1b";
			}

			if ( $temp == NULL || $temp == '') {
				$this->debug['normalize'][] = "2a";
				return '';
			} else {
				$this->debug['normalize'][] = "2b";

				// replace any HTML ampersands
				$set = array('%', '&', 'amp;%', 'AMP;%');
				$temp = str_replace( $set, '&', $temp );

				$this->debug['normalize'][] = "2b1 (temp:$temp)";

				// remove any content in angle brackets (e.g. html tags - <i>, </i>, etc.)
				$html_pattern = "(\<(/?[^\>]+)\>)";
//? This should not just handle html tags but all <*>				
				$temp = preg_replace( $html_pattern, '', $temp);
				$this->debug['normalize'][] = "2b2 (temp:$temp)";

				// if second term (only) is in round brackets, presume it is a subgenus or a comment and remove it
				// examples: Barbatia (Mesocibota) bistrigata (Dunker, 1866) => Barbatia bistrigata (Dunker, 1866)
				// Barbatia (?) bistrigata (Dunker, 1866) => Barbatia bistrigata (Dunker, 1866)
				// (obviously this will not suit genus + author alone, where first part of authorname is in brackets,
				// however this is very rare?? and in any case we are not supporting genus+authority in this version)
//if ( $temp like '% (%)%'
				$temp = preg_replace( "/ \(\w*\W*\)/", '', $temp, 1 );
//? Not sure if this will catch if				
				$this->debug['normalize'][] = "2b3 (temp:$temp)";

				// if second term (only) is in square brackets, presume it is a comment and remove it
				// example: Aphis [?] ficus Theobald, [1918] => Aphis ficus Theobald, [1918]		
//if ( $temp like '% [%]%'
				$temp = preg_replace( "/ \[\w*\W*\]/", '', $temp, 1 );
//? Not sure if this will catch if				
				$this->debug['normalize'][] = "2b4 (temp:$temp)";

				// drop indicators of questionable id's - presume all are lowercase for now (could extend as needed)
				$temp = preg_replace( "/ cf /", " ", $temp );
				$temp = preg_replace( "/ cf\. /", " ", $temp );
				$temp = preg_replace( "/ near /", " ", $temp );
				$temp = preg_replace( "/ aff\. /", " ", $temp );
				$temp = preg_replace( "/ sp\. /", " ", $temp );
				$temp = preg_replace( "/ spp\. /", " ", $temp );
				$temp = preg_replace( "/ spp /", " ", $temp );

				$this->debug['normalize'][] = "2b5 (temp:$temp)";

				// eliminate or close up any stray spaces introduced by the above
				$temp = $this->reduce_spaces( $temp );

				$this->debug['normalize'][] = "2b6 (temp:$temp)";

				// now presume first element is genus, second (if present) is species, remainder
				//   (if present) is authority
				// look for genus name
				$ar = explode( " ", $temp, 2);
				if ( count( $ar ) ) {
					$temp_genus = $ar[0];
					$temp = @$ar[1];
				} else {
					$temp_genus = $temp;
					$temp = '';
				}
				
				$this->debug['normalize'][] = "2b7 (temp_genus:$temp_genus) (temp:$temp)";

				// look for species epithet and authority
				$ar = explode( " ", $temp, 2);
				if ( count( $ar ) ) {
					$temp_species = $ar[0];
					$temp_authority = @$ar[1];
				} else {
					$temp_species = $temp;
					$temp_authority = '';
				}
				$temp = '';

				$this->debug['normalize'][] = "2b8 (temp_genus:$temp_genus) (temp_species:$temp_species) (temp_authority:$temp_authority) (temp:$temp)";

				// now can treat genus and species together				
//				$temp_genus_species = upper( rtrim($temp_genus || ' ' || $temp_species ));
				$temp_genus_species = strtoupper( rtrim($temp_genus . ' ' . $temp_species ) );

				$this->debug['normalize'][] = "2b9 (temp_genus:$temp_genus) (temp_species:$temp_species) (temp_authority:$temp_authority) (temp_genus_species:$temp_genus_species) (temp:$temp)";

				// Diacritical marks are removed here, however for authorities they should be kept
				// replace any accented characters, drop any non A-Z chars other than
				//  full stops and spaces
//?				$temp_genus_species = translate( $temp_genus_species, '√Å√â√ç√ì√ö√Ä√à√å√í√ô√Ç√ä√é√î√õ√Ñ√ã√è√ñ√ú√É√ë√ï√Ö√á√ò', 'AEIOUAEIOUAEIOUAEIOUANOACO');

				// replace selected ligatures here (Genus names can contain √Ü, OE ligature)
				$temp_genus_species = str_replace( '√Ü', 'AE', $temp_genus_species );
				
				$temp_genus_species = str_replace( chr(140), 'OE', $temp_genus_species );

				$this->debug['normalize'][] = "2b10 (temp_genus:$temp_genus) (temp_species:$temp_species) (temp_authority:$temp_authority) (temp_genus_species:$temp_genus_species) (temp:$temp)";

				// now drop any chars other than A-Z, space, and full stop
				$temp_genus_species = ltrim( rtrim( $this->good_chars( $temp_genus_species ) ) );

				$this->debug['normalize'][] = "2b11 (temp_genus:$temp_genus) (temp_species:$temp_species) (temp_authority:$temp_authority) (temp_genus_species:$temp_genus_species) (temp:$temp)";

				// reduce any new multiple internal spaces to single space, if present
				$temp_genus_species = $this->reduce_spaces( $temp_genus_species );

				$this->debug['normalize'][] = "2b12 (temp_genus:$temp_genus) (temp_species:$temp_species) (temp_authority:$temp_authority) (temp_genus_species:$temp_genus_species) (temp:$temp)";

				$this->debug['normalize'][] = "Return: \"" . trim($temp_genus_species . ' ' . $temp_authority) . "\"";
				return $this->output = trim($temp_genus_species . ' ' . $temp_authority );
				
			}
			
		} // End Normalize

		/**
		 * getXML
		 * Get the Output in xml format
		 * @return string|bool : the xml string or false in case output is absent
		 */
		public function getXML() {
			if($this->output != '') {
				$this->xml->addHeader('true');
				$this->xml->addInput($this->input);
				$this->xml->addChild('result',$this->output);
				return $this->xml->getXML();
			} else {
				return false;
			}
		}
		
		public static function normalize_author_string($author_string)
		{
            $author_string = " ". $author_string ." ";
            $author_string = str_replace("[", " ", $author_string);
            $author_string = str_replace("]", " ", $author_string);
            $author_string = str_replace("(", " ", $author_string);
            $author_string = str_replace(")", " ", $author_string);
            $author_string = str_replace(".", " ", $author_string);
            $author_string = str_replace(",", " ", $author_string);
            $author_string = str_replace("'", " ", $author_string);
            $author_string = str_replace("-", " ", $author_string);
            while(preg_match("/  /", $author_string)) $author_string = str_replace("  ", " ", $author_string);
            $author_string = trim(strtolower(self::utf8_to_ascii($author_string)));

            return $author_string;
		}
		
		public function utf8_to_ascii($string)
		{
            $string = preg_replace("/[ÀÂÅÃÄÁẤẠ]/u", "A", $string);
            $string = preg_replace("/[ÉÈÊË]/u", "E", $string);
            $string = preg_replace("/[ÍÌÎÏ]/u", "I", $string);
            $string = preg_replace("/[ÓÒÔØÕÖỚỔ]/u", "O", $string);
            $string = preg_replace("/[ÚÙÛÜ]/u", "U", $string);
            $string = preg_replace("/[Ý]/u", "Y", $string);
            $string = preg_replace("/Æ/u", "AE", $string);
            $string = preg_replace("/[ČÇ]/u", "C", $string);
            $string = preg_replace("/[ŠŞ]/u", "S", $string);
            $string = preg_replace("/[Đ]/u", "D", $string);
            $string = preg_replace("/Ž/u", "Z", $string);
            $string = preg_replace("/Ñ/u", "N", $string);
            $string = preg_replace("/Œ/u", "OE", $string);
            $string = preg_replace("/ß/u", "B", $string);
            $string = preg_replace("/Ķ/u", "K", $string);
            $string = preg_replace("/[áàâåãäăãắảạậầằ]/u", "a", $string);
            $string = preg_replace("/[éèêëĕěếệểễềẻ]/u", "e", $string);
            $string = preg_replace("/[íìîïǐĭīĩỉï]/u", "i", $string);
            $string = preg_replace("/[óòôøõöŏỏỗộơọỡốơồờớổ]/u", "o", $string);
            $string = preg_replace("/[úùûüůưừựủứụ]/u", "u", $string);
            $string = preg_replace("/[žź]/u", "z", $string);
            $string = preg_replace("/[ýÿỹ]/u", "y", $string);
            $string = preg_replace("/[đ]/u", "d", $string);
            $string = preg_replace("/æ/u", "ae", $string);
            $string = preg_replace("/[čćç]/u", "c", $string);
            $string = preg_replace("/[ñńň]/u", "n", $string);
            $string = preg_replace("/œ/u", "oe", $string);
            $string = preg_replace("/[śšş]/u", "s", $string);
            $string = preg_replace("/ř/u", "r", $string);
            $string = preg_replace("/ğ/u", "g", $string);
            $string = preg_replace("/Ř/u", "R", $string);
                        
            return $string;
		}

	} // End Class

?>
