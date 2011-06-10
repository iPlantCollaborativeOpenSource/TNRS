<?php
	/*
	* This example is to show a simple way to return the same genus if there is a exact match
	* or return the first phonetic or near_match found.
	* 
	* Purpose: With google docs there is an import data function that is very useful.
	* Live Example: http://spreadsheets.google.com/ccc?key=r_Hu7R74XCk0HTjKeklsf8g
	* This example shows how it is possible to provide a list of genera and use the web service to
	* lookup the known genus for a given row. We then do a calculation to give the percent accurate.
	*/
	
	$output = null;
	
	// Call Web Service
	$req = "http://taxamatch.silverbiology.com/svn/api/taxamatch.php?cmd=taxamatch&source=" . $_REQUEST['source'] . "&str=" . urlencode( $_REQUEST['str'] );
	$r = json_decode( file_get_contents( $req ) );
	
	// Drop down from least accurate to most accurate to get the related genus.
	if (isset($r->data->genus)) {
		if (isset($r->data->genus->near_2[0])) $output[] = $r->data->genus->near_2[0]->genus;
		if (isset($r->data->genus->near_1[0])) $output[] = $r->data->genus->near_1[0]->genus;
		if (isset($r->data->genus->phonetic[0])) $output[] = $r->data->genus->phonetic[0]->genus;
		if (isset($r->data->genus->exact[0])) $output[] = $r->data->genus->exact[0]->genus;
	}
	
	$output = implode(", ", $output);
	print $output;
	
?>