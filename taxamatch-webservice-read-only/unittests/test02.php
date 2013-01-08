<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 2</title>
</head>

<body>
	<h1>Normalize Author Tests</h1>
	<pre>
<?php

	$tests = array( "L.", "(L.)", "L., 1750", "L. 1750", "(L., 1751)", "(L. 1751)", "(L.)ABC", "DC", "(DC)", "D.C.", "(D.C.)", "prefix et al sufix", "Aarons.", "F. J. R. Taylor, 1980", "F.J.R. Taylor, 1980", "Smith 1980", "Smith, 1980" );
	
	foreach( $tests as $str ) {
		print "String: $str<br>";
		$str = urlencode( $str );
		$req = "http://arjuna.iplantcollaborative.org/taxamatch-webservice-read-only/api/taxamatch.php?cmd=normalize_auth&str=$str&source=test&debug=" . $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
