<?php

$required_file = $dataDir.$noOpNamesFile;
$msg_file_missing = "Required file `$noOpNamesFile` missing!";

if (!file_exists($required_file)) die($msg_file_missing);

?>