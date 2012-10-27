<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Soap Test: Normalize</title>
<style type="text/css">
<!--
body,td,th {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
}
body {
	background-color: #efefef;
}
.code{
	padding: 5px;
	margin: 5px;
	border: thin #000 solid;
	background-color: #fff;
	width: 600px;
}
-->
</style></head>

<body>
	<h1>Soap Test: Normalize</h1>
	<p>Sample Code</p>
	<pre class="code">
	<code>
require_once('{path_to_soap_library}/nusoap.php');
$c = new soapclient('{path_to_soap_server}/taxamatchsoap.php');
$value = $c->call(
		'normalize'
	,	array('string' => $item)
);
	</code>
	</pre>
<?php
	require_once('../../api/nusoap/lib/nusoap.php');
	$p = pathinfo($_SERVER['REQUEST_URI']);
	$c = new soapclient('http://' . $_SERVER['SERVER_NAME'] . "/" . $p['dirname'] . '/../../api/taxamatchsoap.php');

	print "<p>Here are a list of normalized words.</p>";

	$list = array( "Carex", "Carex%0A", "Carex%20", "Barbatia (Mesocibota) bistrigata (Dunker, 1866)", "Barbatia (?) bistrigata (Dunker, 1866)", "Aphis [?] ficus Theobald, [1918]" );
	print "<ul>";
	foreach( $list as $item ) {
		$value = $c->call(
				'normalize'
			,	array('string' => $item)
		);
		print "<li>Input: <b>$item</b><br>Response: <b>$value</b><br><br></li>";		
	}
	print "</ul>";
?>
</body>
</html>