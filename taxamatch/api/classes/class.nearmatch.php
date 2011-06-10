<?php
	/**
	 * Taxamatch-Webservice PHP v1.0.0
	 * @author Michael Giddens
	 * @link http://www.silverbiology.com
	 */

	require_once('class.normalize.php');

	/**
	* Class NearMatch
	*/
	class NearMatch {
			
		/**
		* Constructor 
		*/
		public function __construct() {
//			$this->xml = new SimpleXml(XML_STRING);
		}

		/**
		* Function to treat the word
		* @param string $str2
		* @param integer $strip_ending
		* @param integer $normalize : 1 -> normalize the word , 0 -> no normalization action
		* @return string
		*/
		public function treat_word( $str2, $strip_ending = 0, $normalize=1 ) {

			$temp2 = '';
			$start_letter = '';
			$next_char = '';
			$result2 = '';

			$this->input = $str2;
			$this->output = '';

      if ( ( $str2 == NULL ) || ( trim($str2) == '' ) ) {
        return('');
      } else {
			
				if ($normalize) {
					$n = new Normalize();
					$temp2 = $n->normalize( $str2 );
					$this->debug['Normalize'][] = $n->debug;
				} else {
					$temp2 = $str2;
				}
				
				$this->debug['TW'][] = "1 (temp2:$temp2)";
				
				// Do some selective replacement on the leading letter/s only: ('soundalikes')
        if(preg_match('/^AE/', $temp2))      $temp2 = preg_replace('/^AE/', 'E', $temp2);
        elseif(preg_match('/^CN/', $temp2))  $temp2 = preg_replace('/^CN/', 'N', $temp2);
        elseif(preg_match('/^CT/', $temp2))  $temp2 = preg_replace('/^CT/', 'Z', $temp2);
        elseif(preg_match('/^CZ/', $temp2))  $temp2 = preg_replace('/^CZ/', 'V', $temp2);
        elseif(preg_match('/^DJ/', $temp2))  $temp2 = preg_replace('/^DJ/', 'J', $temp2);
        elseif(preg_match('/^EA/', $temp2))  $temp2 = preg_replace('/^EA/', 'E', $temp2);
        elseif(preg_match('/^EU/', $temp2))  $temp2 = preg_replace('/^EU/', 'U', $temp2);
        elseif(preg_match('/^GN/', $temp2))  $temp2 = preg_replace('/^GN/', 'N', $temp2);
        elseif(preg_match('/^KN/', $temp2))  $temp2 = preg_replace('/^KN/', 'N', $temp2);
        elseif(preg_match('/^MC/', $temp2))  $temp2 = preg_replace('/^MC/', 'MAC', $temp2);
        elseif(preg_match('/^MN/', $temp2))  $temp2 = preg_replace('/^MN/', 'N', $temp2);
        elseif(preg_match('/^OE/', $temp2))  $temp2 = preg_replace('/^OE/', 'E', $temp2);
        elseif(preg_match('/^QU/', $temp2))  $temp2 = preg_replace('/^QU/', 'Q', $temp2);
        elseif(preg_match('/^PS/', $temp2))  $temp2 = preg_replace('/^PS/', 'S', $temp2);
        elseif(preg_match('/^PT/', $temp2))  $temp2 = preg_replace('/^PT/', 'T', $temp2);
        elseif(preg_match('/^TS/', $temp2))  $temp2 = preg_replace('/^TS/', 'S', $temp2);
        elseif(preg_match('/^WR/', $temp2))  $temp2 = preg_replace('/^WR/', 'R', $temp2);
        elseif(preg_match('/^X/', $temp2))   $temp2 = preg_replace('/^X/', 'Z', $temp2);
				// This one is not in the list but in the old taxamatch file originally because of some fish names
				// having a silent H and hard P but we are putting it back in only in the beginning
        elseif(preg_match('/^ph/', $temp2))  $temp2 = preg_replace('/^ph/', 'f', $temp2);
			
				$this->debug['TW'][] = "2 (temp2:$temp2)";
				
				// Now keep the leading character, then do selected "soundalike" replacements. The
				// following letters are equated: AE, OE, E, U, Y and I; IA and A are equated;
				// K and C;  Z and S; and H is dropped. Also, A and O are equated, MAC and MC are equated, and SC and S.
				$start_letter = substr( $temp2, 0, 1);  // quarantine the leading letter
				$temp2 = substr( $temp2, 1);  // snip off the leading letter
				$this->debug['TW'][] = "3 (start_letter:$start_letter) (temp2:$temp2)";
				
				// now do the replacements
        $temp2 = str_ireplace ('AE', 'I', $temp2);
        $temp2 = str_ireplace ('IA', 'A', $temp2);
        $temp2 = str_ireplace ('OE', 'I', $temp2);
        $temp2 = str_ireplace ('OI', 'A', $temp2);
        $temp2 = str_ireplace ('SC', 'S', $temp2);
        $temp2 = str_ireplace ('E' , 'I', $temp2);
        $temp2 = str_ireplace ('O' , 'A', $temp2);
        $temp2 = str_ireplace ('U' , 'I', $temp2);
        $temp2 = str_ireplace ('Y' , 'I', $temp2);
        $temp2 = str_ireplace ('K' , 'C', $temp2);
        $temp2 = str_ireplace ('Z' , 'S', $temp2);
        $temp2 = str_ireplace ('H' , '', $temp2);
//        $temp2 = str_ireplace ('io', 'a', $temp2);   // Not used in taxamatch?
//        $temp2 = str_ireplace ('ou', 'u', $temp2);	// Not used in taxamatch?
//        $temp2 = str_ireplace ('ph', 'f', $temp2);	// Not used in taxamatch?
				$this->debug['TW'][] = "4 (temp2:$temp2)";
								
				//add back the leading letter
				$temp2 = $start_letter . $temp2;
				$this->debug['TW'][] = "5 (temp2:$temp2)";
				
				// now drop any repeated characters (AA becomes A, BB or BBB becomes B, etc.)
				for ( $i = 0; $i <= strlen($temp2); $i++) {
					$next_char = substr( $temp2, $i, 1 );
					if ( $i == 0 ) {
						$result2 = $next_char;
					} elseif ( $next_char == substr( $result2, -1 ) ) {
					} else {
						$result2 = $result2 . $next_char;
					}
				}

				$this->debug['TW'][] = "6 (result2:$result2) (temp2:$temp2)";
				
				if ( ( strlen( $result2 ) > 4 ) && ($strip_ending) ) {
					$this->debug['TW'][] = "7  (result2:$result2)";
				
					// deal with variant endings -is (includes -us, -ys, -es), -im (was -um), -as (-os)
					// at end of string or word: translate all to -a
					if (substr($result2, -2) == 'IS') $result2 = preg_replace('/IS$/' , 'A', $result2);
					if (substr($result2, -2) == 'IM') $result2 = preg_replace('/IM$/' , 'A', $result2);
					if (substr($result2, -2) == 'AS') $result2 = preg_replace('/AS$/' , 'A', $result2);

					$this->debug['TW'][] = "7a  (result2:$result2)";
				}

				$this->debug['TW'][] = "Return: ($result2)";
				$this->output = $result2;
				return( $this->output );
				
			}	// End else
			
		}

		/**
		 * Function: near_match
		 * Purpose: Produces "Rees 2007 near match" version of an input string
		 * @Author: Tony Rees (Tony.Rees@csiro.au)
		 * Date created: June 2007
		 * Inputs: input string as str, word type as word_type (permitted values of word type are
		 *  'genus_only', 'epithet_only', or null (latter presumes binomen or trinomen)
		 * Outputs: transformed version of input string, as phonetic key for near match purposes
		 * Remarks:
		 *   (1) includes calls to external function "normalize" (performs some normalization of text strings,
		 *      includes removal of known text elements e.g.
		 *      'aff.', 'cf.', 'subsp.', subgenera if enclosed in brackets, etc. as desired)
		 *   (2) Includes additional ending normalization on epithets (but not genus name)
		 *   (3) Presumes authority information has already been stripped (i.e., not supplied)
		 * @param string $str
		 * @param string $word_type : 'genus_only' | 'epithet_only' | NULL
		 * @return mixed
		 */
		public function near_match( $str = NULL, $word_type = NULL ) {

			unset($this->debug['Near Match']);
		
			$temp = '';
			$word_no = 1;
			$result = '';
			$this->input = $str;
			$this->output = '';

			if ( ( $str == NULL ) || ( trim( $str ) == '' ) ) {
				$this->debug['Near Match'][] = "1";
				return '';
			} else {
			
				$temp = strtoupper( $str );
				
				switch ($word_type) {
					case 'genus_only':
						$this->debug['Near Match'][] = "2a (temp:$temp)";
						$result = $this->treat_word( $temp );
						break;
						
					case 'epithet_only':
						$this->debug['Near Match'][] = "2b (temp:$temp)";
						$result = $this->treat_word( $temp, 'Y' );
						break;

					default:
						$this->debug['Near Match'][] = "2c (temp:$temp)";
						// add a trailing space (otherwise will loop forever!)
						$temp = $temp . ' ';
						while ( strlen( $temp ) > 1 ) {
							// snip off words and treat consecutively
							$this_word = substr($temp, 0, strpos($temp,' ',0));
							if ( $word_no == 1 ) {
								// presume genus name, do not treat species endings, etc.
								$result = $result . ' ' . $this->treat_word( $this_word );
							} else {
								$result = $result . ' ' . $this->treat_word( $this_word, 'Y' );
							}
							$temp = substr( $temp, strpos($temp, ' ', 0) + 1 );
							$word_no = $word_no + 1;
						}
						break;
						
				} // End Switch
				
				$this->debug['Near Match'][] = "3 (result:" . trim($result) . ")";
				$this->output = trim( $result );
				return( $this->output );
			}
		}

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

		
	}
?>