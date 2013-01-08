<?php

	/**
	*	Process CSV with Taxamatch
	*	@author Michael Giddens
	*/

	set_time_limit(300);
	ini_set("memory_limit","1000M");
	ini_set('post_max_size', '128M');
	ini_set('upload_max_filesize', '128M');

	/**
	* auto_detect_line_endings allows for the follow csv formats
	*	1. Comma delimited CSV (*.csv)
	*	2. CSV (MacIntosh) (*.csv)
	*	3. CSV (MS-DOS)
	*/
	ini_set("auto_detect_line_endings", 1);

#	define('TAXAMATCH_URL','http://taxamatch.silverbiology.com/svn/api/taxamatch.php');

	require_once('config.php');
	require_once('classes/class.mysqli_database.php');
	require_once('classes/class.taxamatch.php');
	require_once('classes/class.misc.php');
	require_once('classes/class.csv_process.php');

	$folder_path = TMPFILE_PATH;
	$inputFolder = "";
	$tmpFolder = "csv_process/";

	$time_start = microtime_float();

	if (isset($_FILES['input_file']['tmp_name'])) {
		$input_file = $_FILES['input_file']['tmp_name'];
	} else {

		if ( trim( $_REQUEST['input'] ) == '' ) {
			print json_encode( array( success=>false, error=>"Error - Input file cannot be blank. Use argument 'input'." ) );
			exit();
		}	
		// Files are only allowed to reside in input folder to keep from hacking attemps
		$input_file = $folder_path . $inputFolder . $_REQUEST['input'];
		$input_file = str_replace('../', '', $input_file); // More hack checks
	}
	
	$output_file = $_REQUEST['output'] || md5(time()).".csv";
	$source = (isset($_REQUEST['source'])) ? $_REQUEST['source'] : "default";

	$temp_folder_path = $folder_path . $tmpFolder;

	switch ($_REQUEST['cmd']) {
		case 'url':
			// Could extend the code to handle a remote file too.
			break;

		case 'download':
			
			if ( trim( $output_file ) != '' && file_exists($input_file) ) {
			
				if ( trim( $output_file ) == '' ) {
					print json_encode( array( success=>false, error=>"Error - Output file cannot be blank. Use argument 'output'." ) );
				} else {
					$csv_process = new CSVProcess();
					$csv_process->set('source', $source);
					$csv_process->set('source', $source);
					$csv_process->set('input_file', $input_file);
					$tmp_file = $temp_folder_path . md5( mktime() ) . ".csv";
					$csv_process->set('output_file', $tmp_file);
					$csv_process->process();
					$csv_process->download_file();
#					unlink($tmp_file);
					exit();
				}
				
			} else {
				print json_encode( array( success=>false, error=>"Error - Not a valid input file or file does not exist." ) );
				exit();
			}
			break;
			
		case 'file':
		
			$output_file = $_REQUEST['output'];
			
			if ( trim( $output_file ) != '' && file_exists($input_file) ) {
			
				if ( trim( $output_file ) == '' ) {
					print "Error - Output file cannot be blank. Use argument 'output'. ";
				} else {
					$csv_process = new CSVProcess();
					$csv_process->set('source', $source);
					$csv_process->set('input_file', $input_file);
					$csv_process->set('output_file', $temp_folder_path . $output_file);
					$csv_process->process();
				}
			} else {
				print "Error - Not a valid input file.";
			}
			break;

		default:
			print "Error - Not a valid command.";
			break;
			
	}
	
	function microtime_float() {
			list($usec, $sec) = explode(" ", microtime());
			return ((float)$usec + (float)$sec);
	}

	$time_end = microtime_float();
	$time = $time_end - $time_start;

	print "Exec duration: $time<br>";
	print round( memory_get_usage() * 0.0009) . "KB<br>";
	
?>