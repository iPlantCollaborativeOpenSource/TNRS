<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 3</title>
</head>

<body>
	<h1>Treat Word</h1>
	<pre>
<?php

	$tests = array( "AE", "CN", "CT", "CZ", "DJ", "EA", "EU", "GN", "KN", "MC", "MN", "OE", "QU", "PS", "PT", "TS", "WR", "X", "AEAE", "IAIA", "OEOE", "OIOI", "SESES", "EAEE", "OEOO", "QUQU", "YY", "KK", "ZZ", "HH" );
	
	foreach( $tests as $str ) {
		print "String: $str<br>";
		$str = urlencode( $str );
		$req = "http://arjuna.iplantcollaborative.org/taxamatch-webservice-read-only/api/taxamatch.php?cmd=treat_word&str=$str&&normalize=" . $_REQUEST['normalize'] . "&strip_ending=" . $_REQUEST['strip_ending'] . "&debug=" . $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );		
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
