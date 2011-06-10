<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

include_once('cache.php');

/**
 * Class Cache extending from Fast Cache Class
 * DB operations
 *
 * @copyright SilverBiology, LLC
 * @author Michael Giddens
 * @website http://www.silverbiology.com
 */

class Cache extends FastCache{


/**
 * Array for keeping the input
 * @access private
 * @var mixed
 */
private $data;

/**
 * Constructor
 * @param string $cache_path
 */
	public function __construct($cache_path) {
		$this->CACHE_SUFFIX = ".txt";
		parent::FastCache($cache_path);
	}

/**
 * setKey
 * Sets the Key
 * @param string $key
 * @return boolean
 */
	public function setKey($key) {
		$this->CACHE_KEY = $key;
		return true;
	}

/**
 * getKey
 * Gets the Key
 * @return string
 */
	public function getKey() {
		return $this->CACHE_KEY;
	}

/**
 * cache_exists
 * Checks if the cache file exists
 * @return int : 1 | 0
 */
	function cache_exists() {
		if(file_exists($this->CACHE_PATH.$this->CACHE_KEY.$this->CACHE_SUFFIX)) {
			return 1;
		} else {
			return 0;
		}
	}

}

?>