<?php

/////////////////////////////////////////////////////////////////////
// Purpose:
// Create new temporary database
/////////////////////////////////////////////////////////////////////

// Create database
echo "Creating database...";
$sql = "CREATE DATABASE `$DB_NEW`";
$retval = mysql_query( $sql, $dbh );
if($retval) {
	echo $msg_success;
}else{
	die('Could not create database: ' . mysql_error());
}

?>
