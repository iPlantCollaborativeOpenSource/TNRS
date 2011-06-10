<?php
// Creates TNRS core db 

include "params.inc";	// everything you need to set is here and dbconfig.inc

// Confirm with user before starting
if (confirm($confmsg)) {
	echo "\r\nBegin operation\r\n\r\n";
}

include_once "create_tables.inc";

echo "\r\nOperation completed\r\n";

?>
