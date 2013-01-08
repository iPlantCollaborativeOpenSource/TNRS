<php
///////////////////////////////////////////////////////////
// Purpose:
// Converts file from utf16 to utf8
// Saves copy with utf8 suffix
///////////////////////////////////////////////////////////

$cmd="iconv --from-code UTF-16 --to-code UTF-8 20120211GlobalCompositaeChecklistTNRSOutput.csv >20120211GlobalCompositaeChecklistTNRSOutput_utf8.csv ";
system($cmd);


?>
