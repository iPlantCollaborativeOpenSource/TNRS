<?php
// Check basic dependencies before proceeding

///////////////////////////////////////////////////////
// Required directories for each source
//
// For each sourceName, a subdirectory named:
// 'import_sourceName/'
// must be present in this directory 
///////////////////////////////////////////////////////

// get list of subdirectories of current directory
$subdirs = array_filter(glob('*'), 'is_dir');

foreach ($src_array as $src) {
	$subdir="import_".$src;
	if (!in_array($subdir, $subdirs)) {
		// Report error and abort
		$msg_error="\r\nError: required subdirectory '$subdir' not found!\r\n\r\n";
		die($msg_error);		
	} 
}

?>