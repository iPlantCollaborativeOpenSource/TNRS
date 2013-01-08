<?php
/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */


/**
 * Class SimpleXml
 * class to generate and manipulate xml
 */

class SimpleXml extends SimpleXMLElement
{

	/**
	 * addHeader
	 */
	public function addHeader($success = 'true') {
		$this->addChild('success', $success);
	}
	/**
	 * addInput
	 */
	public function addInput($input = '') {
		if($input == '') {
			$this->addChild('input');
		} else {
			$this->addChild('input', $input);
		}
	}

	/**
	 * getXML
	 */
	public function getXML() {
		return $this->asXML();
	}

}

?>