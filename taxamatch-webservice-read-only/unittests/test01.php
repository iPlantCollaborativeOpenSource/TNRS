<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Test 1</title>
</head>

<body>
	<h1>Normalize Tests</h1>
	<pre>
<?php

	$tests = array( "Carex", "Carex%0A", "Carex%20", "Barbatia (Mesocibota) bistrigata (Dunker, 1866)", "Barbatia (?) bistrigata (Dunker, 1866)", "Aphis [?] ficus Theobald, [1918]" );
	
	foreach( $tests as $str ) {
		print "String: $str<br>";
		$str = urlencode( $str );
		$req = "http://arjuna.iplantcollaborative.org/taxamatch-webservice-read-only/api/taxamatch.php?cmd=normalize&str=$str&debug=" . $_REQUEST['debug'];
		print $req . "<br>";
		$r = file_get_contents( $req );
		print_r( json_decode($r) );
		print "<hr>";
	}
	
?>
</body>
</html>
