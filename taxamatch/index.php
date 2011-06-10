<?
	$mobile = "http://" . $_SERVER['SERVER_NAME'] . $_SERVER['REQUEST_URI'] . "mobile";
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Taxamatch Webservice</title>
</head>

<body>
<h1>Welcome to Taxamatch Webservice.</h1>
<p>To install please follow the INSTALL document found <a href="INSTALL">here</a>.</p>
<p>There are multiple ways to use this service.</p>
<p>We have included some basic solutions to get started.</p>
<p>iPhone Service<br/><a href="<?=$mobile?>"><?=$mobile?></a>&nbsp;</p>
</body>
</html>
