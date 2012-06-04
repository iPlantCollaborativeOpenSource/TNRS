<?php

////////////////////////////////////////////////////////////////////
// Purpose:
// Downloads dbf files from GRIN ftp site to data directory, unzips 
// them, and converts each file to plain text. Each file ends up
// with same base name plus extension .txt
// You must have utility dbf_dump installed on your machine; see:
// http://search.cpan.org/~janpaz/DBD-XBase-1.03/bin/dbfdump.PL
////////////////////////////////////////////////////////////////////

// make download directory if it doesn't already exist
if (!is_dir($download_dir)) {
   mkdir($download_dir);
} 

echo "Downloading files:\r\n";
foreach($tbls as $basename) {
	$file=$basename.".zip";
	$url=$source_url.$file;
	echo " $file...";
	$cmd = "wget -q -P $download_dir '$url'";
	exec($cmd);
	echo $msg_success;
}

echo "Unzipping dbf files:\r\n";
foreach($tbls as $basename) {
	$file = $basename.".zip";
	$file_and_path = $download_dir.$file;
	echo " $basename...";
	$cmd = "gunzip -f -S .zip $file_and_path";
	exec($cmd);
	echo $msg_success;
}

echo "Converting dbf files to plain text:\r\n";
foreach($tbls as $basename) {
	$dbffile = $basename;
	$dbffile_and_path = $download_dir.$dbffile;
	$textfile = $basename.$plaintextext;
	$textfile_and_path = $download_dir.$textfile;
	echo " $dbffile-->$textfile...";
	$cmd = "dbf_dump --fs='@' $dbffile_and_path > $textfile_and_path";
	exec($cmd);
	echo $msg_success;
}

?>
