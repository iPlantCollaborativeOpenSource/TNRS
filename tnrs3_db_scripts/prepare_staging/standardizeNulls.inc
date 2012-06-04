<?php

echo "  Converting empty strings to NULL...";
if (!(empty_string_to_null($tbl_staging))) die("Error\r\n.");
echo "  done\r\n";

?>
