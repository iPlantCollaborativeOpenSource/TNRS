<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 10</title>
</head>

<body>
	<h1>ngram</h1>
	<pre>
<?php

	$tests = array( 
			array( 'test1', 'LINNAEUS', 'LINNEAUS', '1' )
		,	array( 'test1', 'BERMUDEZ', 'LINNAEUS', '1' )
		,	array( 'test1', 'BERMUDEZ', 'LINNAEUS', '2' )
	);

	foreach( $tests as $test ) {
		$source = urlencode( $test[0] );
		$source_string = urlencode( $test[1] );
		$target_string = urlencode( $test[2] );
		$n_used = urlencode( $test[3] );

		$req = "http://taxamatch.silverbiology.com/svn/api/taxamatch.php?cmd=ngram&source=$source&str=$source_string&str2=$target_string&n_used=$n_used&debug=". $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
