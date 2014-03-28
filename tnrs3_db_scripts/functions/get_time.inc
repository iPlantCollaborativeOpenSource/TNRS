<?php

//////////////////////////////////////
// Gets time in seconds
// Timer must have been started previously
//////////////////////////////////////

// Stop timer and get time
$mtime = microtime(); 
$mtime = explode(" ", $mtime); 
$mtime = $mtime[1] + $mtime[0]; 
$endtime = $mtime; 
$totaltime = ($endtime - $starttime); 

// Calculate time elapsed in seconds
$tsecs = round($totaltime,2);

?>
