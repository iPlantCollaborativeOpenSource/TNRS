<?php

// Stop timer and get time
$mtime = microtime(); 
$mtime = explode(" ", $mtime); 
$mtime = $mtime[1] + $mtime[0]; 
$endtime = $mtime; 
$totaltime = ($endtime - $starttime); 

// Calculate time elapsed in secondsm & current time
$tsecs = round($totaltime,2);
$curr_time = date("D, d M Y H:i:s",time());

?>
