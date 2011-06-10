<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 9</title>
</head>

<body>
	<h1>Process - Genus+Species+Author</h1>
	<pre>
<?php

	$tests = array( 
			array( 'test1', 'Homo Sapiens Bermudez de Cas', 'normal' )
		,	array( 'test1', 'Aluteres+nasicornis+Temminck', 'normal' )
	);

	foreach( $tests as $test ) {
		$source = urlencode( $test[0] );
		$searchtxt = urlencode( $test[1] );
		$search_mode = urlencode( $test[2] );

		$req = "http://taxamatch.silverbiology.com/svn/api/taxamatch.php?cmd=taxamatch&source=$source&str=$searchtxt&search_mode=$search_mode&debug=". $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
