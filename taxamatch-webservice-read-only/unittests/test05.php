<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 5</title>
</head>

<body>
	<h1>Compare Authors</h1>
	<pre>
<?php

	$tests = array( 
			array( 'L.', 'L' )
		,	array( 'L.', '(L)' )
		,	array( 'L.', 'L.' )
		,	array( 'Smith et Jones', 'Jones et Smith' )
		,	array( 'John Doe', 'Doe, John' )
		,	array( 'John Doe', 'Doe John' )
		,	array( 'de Saedeleer', 'De Saedeleer')
		
	);
	//'genus_only', 'epithet_only'	

	foreach( $tests as $test ) {
		sprintf("Author 1: %s, Author 2: %s<br>", $test[0], $test[1]);
		$auth1 = urlencode( $test[0] );
		$auth2 = urlencode( $test[1] );
		$req = "http://arjuna.iplantcollaborative.org/taxamatch-webservice-read-only/api/taxamatch.php?cmd=compare_auth&str=$auth1&str2=$auth2&debug=". $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );		
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
