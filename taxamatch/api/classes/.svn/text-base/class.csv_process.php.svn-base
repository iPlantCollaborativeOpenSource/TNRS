<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

/**
 * Class normalize_csv
 */

class CSVProcess {

/**
 * holds the input data
 * @access private
 * @var integer|string
 */
	private $data;

/**
 * the delimiter for the csv data
 * @access private
 * @var string
 */
	private $delimiter = ',';

/**
 * the input csv file name
 * @access private
 * @var string
 */
	private $filename_input = "input.csv";

/**
 * the output csv file name
 * @access private
 * @var string
 */
	private $filename_output = "output.csv";

/**
 * the csv file delimiter
 * @var string
 */
	public $file_delimiter = ',';

/**
 * the file handle for the input file
 * @var string
 */
	public $handle_input;

/**
 * the file handle for the output file
 * @var string
 */
	public $handle_output;

/**
 * the new columns to be added to the csv file
 * @var string
 */
	public $new_columns;

/**
 * Function to set the input to the class
 * @param mixed $variable class property name
 * @param mixed $value class property value
 */
	public function set($variable,$value = '') {
		$this->data[$variable] = $value;
	}

/**
 * This function writes the column header to the csv file.
 */
	public function write_header() {
		// Write New Header to Output File
		if (!is_array($this->new_columns)) $this->new_columns = array();
		fputcsv( $this->handle_output, $this->new_columns, $this->file_delimiter );
	}
	
/**
 * This function prints the header and forces the data to be streamed as the response.
 * @param mixed $actual_file actual file name
 * @param mixed $output_file desired output file name
 */
	public function download_file() {

		header("Content-type: application/octet-stream");
		header("Content-Disposition: attachment; filename=\"" . $this->data['output_file'] . "\"");
		$fp = fopen( $this->data['output_file'], 'r');
		fpassthru($fp);
		fclose($fp);
		@unlink($this->data['output_file']);		
	}

	/**
	 * This is the function that process the input csv file and does the work.
 	 * @param string $output_type : output type
 	 */
	public function process( $output_type = 'file' ) {
		// Assign File
		if (file_exists($this->data['input_file'])) {
			$this->handle_input = fopen( $this->data['input_file'], "r");
		} else {
			return( false );
		}
		$this->handle_output = fopen( $this->data['output_file'], "w");

		// Build Header
		$this->build_new_header();
		// Writes column header in 1st row of output.
		$this->write_header();
		
		/**
		*	This will run through the whole data and build the Higer Taxa with
		* parentID relationship to be used in the final species list.
		*/
		$db = select_source( $this->data['source'] );
		$search_mode = 'normal';

		while (($row = fgetcsv($this->handle_input, 100000, $this->file_delimiter)) !== FALSE) {
			// Call taxamatch and process row
			$tm = new Taxamatch($db);
			if( $tm->process( $row[ $this->scientificNameIndex ], $search_mode ) ) {
				$tm->generateResponse($cache);
			}
			$data = $tm->getData('flat');			

			if(is_array($data)) {
			foreach( $data as $r ) {
				fputcsv( $this->handle_output, array_merge( $row, $r ), $this->file_delimiter );
			}
			}
		}

		fclose($this->handle_output);
		fclose($this->handle_input);
	}

/**
 * Reads the first line and identify columns to be added to the csv
 */
	public function build_new_header() {
		$header_array = array('genus', 'scientificname');
		$row = fgetcsv($this->handle_input, 10000, $this->file_delimiter);
		$this->scientificNameIndex = 0;
		if (is_array($row)) {
		foreach($row as $column) {
			if(in_array(str_replace(' ', "", strtolower($column)),$header_array)) {
				$header_flag = 1;
				$this->scientificNameIndex++;
			}
		}
		}

		if($header_flag) {
			$headers = array('rank', 'input', 'match_type', 'genus_species', 'temp_authority', 'source_id', 'genus_ed', 'species_ed');
			$this->new_columns = array_merge($row, $headers);
		}
	}
	
}
?>