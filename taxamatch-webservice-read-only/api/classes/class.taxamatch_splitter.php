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
	public $genus, $species, $author;

	public function __construct( $norm, $search_text ) {
		$search_text = $norm->normalize($search_text);
		$txt_arr = explode(' ',trim($search_text));
		$this->genus = array_shift($txt_arr);
		$this->species = array_shift($txt_arr);
//Keep this here for now			$this_authority = implode(" ", $txt_arr);
		$this->author = array_shift($txt_arr);
	}

	public function get($field) {
		return $this->$field;
	}
}

?>