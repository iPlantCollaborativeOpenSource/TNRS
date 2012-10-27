<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

	
/**
 * Class Splitter
 */

Class Splitter 
{
	public $parsed_response, $genus, $species, $author, $authors_years;

	public function __construct( $norm, $search_text) {
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		socket_connect($socket, 'localhost', 4334);
		$search_text=$search_text . "\n";
		socket_write($socket, $search_text);
		socket_write($socket, "exit\n");
		$json=socket_read($socket, 2048, PHP_NORMAL_READ);
		socket_close($socket);
		$this->parsed_response = json_decode( $json );
		//$this->parsed_response = json_decode( file_get_contents('http://globalnames.org/parsers.json?names=' . urlencode($search_text)));
		//$this->parsed_response = $this->parsed_response[0];
		$cnts = $this->parsed_response;
		if (isset($status)) {
			$cnts->scientificName->details[0]->status=$status;
		}
		$this->species = '';
		$this->genus = '';
		$this->infraspecies=array();
		$this->author = '';
		$this->authors=array();
			
		if (isset($cnts->scientificName->details[0]->species->string)) {
			$this->species = $cnts->scientificName->details[0]->species->string;
			$this->genus = $cnts->scientificName->details[0]->genus->string;
			if(isset($cnts->scientificName->details[0]->infraspecies)) {
				foreach($cnts->scientificName->details[0]->infraspecies as $infra) {
					$this->infraspecies[]=array($infra->rank, $infra->string);
				}
			}

			if (isset($cnts->scientificName->details[0]->species->authorship)) {
				$this->authors[]=trim($cnts->scientificName->details[0]->species->authorship);
			} else {
				$this->authors[]='';
			}

			if (isset($cnts->scientificName->details[0]->infraspecies)) {
				foreach ($cnts->scientificName->details[0]->infraspecies as $infra) {
					if (isset($infra->authorship)) {
						$this->authors[] = trim($infra->authorship);
					} else {
						$this->authors[]='';
					}
				}
			}
		} elseif (isset($cnts->scientificName->details[0]->uninomial->string)) {
			$this->genus = $cnts->scientificName->details[0]->uninomial->string;
			if (isset($cnts->scientificName->details[0]->uninomial->authorship)) {
				$this->authors[] = trim($cnts->scientificName->details[0]->uninomial->authorship);
			} else {
				$this->authors[]='';
			}
		}
		$this->author=end($this->authors);
		$this->authors_years = $this->get_authorship();
  }
	
	public function get_authorship()
	{
		if(!isset($this->parsed_response)) return false;
		$response = $this->parsed_response->scientificName;
	    if(@!$response->parsed || @!$response->parsed == 'false') return false; 
		$d = $response->details[0];
		$return = array('authors' => array(), 'years' => array());
    if(@$d->uninomial) self::merge_author_year_result($return, $this->get_authors_and_years($d->uninomial));
		if(@$d->genus) self::merge_author_year_result($return, $this->get_authors_and_years($d->genus));
		if(@$d->species) self::merge_author_year_result($return, $this->get_authors_and_years($d->species));
		if(@$d->infraspecies) self::merge_author_year_result($return, $this->get_infraspecies_authors_and_years($d->infraspecies));

		return $return;
	}
	
	private function get_authors_and_years($parsed_name_part)
	{
	    $return = array('authors' => array(), 'years' => array());
      self::merge_author_year_result($return, $this->get_authors_years('basionymAuthorTeam', $parsed_name_part));
      self::merge_author_year_result($return, $this->get_authors_years('combinationAuthorTeam', $parsed_name_part));
      return $return;
	}
	
	private function get_infraspecies_authors_and_years($infraspecies)
	{
	    $return = array('authors' => array(), 'years' => array());
	    foreach($infraspecies as $parsed_name_part)
	    {
	        self::merge_author_year_result($return, $this->get_authors_years('basionymAuthorTeam', $parsed_name_part));
            self::merge_author_year_result($return, $this->get_authors_years('combinationAuthorTeam', $parsed_name_part));
	    }
        return $return;
	}
	
	private function get_authors_years($author_type, $parsed_name_part)
	{
	    $return = array('authors' => array(), 'years' => array());
	    
	    if(@$parsed_name_part->$author_type)
	    {
	        if(@$parsed_name_part->$author_type->author)
	        {
	            foreach($parsed_name_part->$author_type->author as $author)
	            {
	                $return['authors'][] = $author;
	            }
	        }
	        if(@$parsed_name_part->$author_type->year) $return['years'][] = $parsed_name_part->$author_type->year;
	        if(@$parsed_name_part->$author_type->exAuthorTeam)
	        {
	            if(@$parsed_name_part->$author_type->exAuthorTeam->author)
	            {
	                foreach($parsed_name_part->$author_type->exAuthorTeam->author as $author)
    	            {
    	                $return['authors'][] = $author;
    	            }
	            }
    	        if(@$parsed_name_part->$author_type->exAuthorTeam->year) $return['years'][] = $parsed_name_part->$author_type->exAuthorTeam->year;
	        }
	    }
	    
	    return $return;
	}
	
	private static function merge_author_year_result(&$result1, $result2)
	{
    foreach($result2['authors'] as $auth) $result1['authors'][] = $auth;
    foreach($result2['years'] as $yr) $result1['years'][] = $yr;
    return $result1;
	}
	
  public function get($field) {
		return $this->$field;
	}
}

?>
