<?php

echo "Updating infra1...";

$src_tbl=$tbl_infra1;
$desc_tbl=$src_tbl . '_new';
$sql_select="SELECT * FROM $src_tbl";
$outfile=$src_tbl;
$infile=$desc_tbl;

sql_select_file_from_host($HOST, $USER, $PWD, $DB, $sql_select, $outfile, false);

$outh = fopen($outfile, "r");
$inh = fopen($infile, "w");
$is_header=true;
$header=array();
while (($field = fgetcsv($outh, 1000, "\t")) !== FALSE) {
	if ($is_header) {
		$header=$field;
		$is_header=false;
	} else {
		$n = new Normalize();
		$nm = new NearMatch();
		$row=array();
		$row=array_combine($header, $field);
		$row["infra1_length"]=strlen($row["infra1_norank"]);
		$row["near_match_infra1"]=$nm->near_match($row["infra1_norank"]);
		$row["search_infra1_name"]=$n->normalize($row["infra1_norank"]);
		$field=array_values($row);
		unset($row);
		unset($n);
		unset($nm);
	}
	fputcsv($inh, $field, "\t");
	unset($field);
}
fclose($outh);
fclose($inh);
unset($header);

$sql="DROP TABLE IF EXISTS $DB.$desc_tbl;CREATE TABLE $DB.$desc_tbl LIKE $DB.$src_tbl;LOAD DATA LOCAL INFILE '$infile' INTO TABLE $desc_tbl character set utf8 COLUMNS $tr_optionally_enclosed_by $tr_ignore_lines;DROP TABLE $DB.$src_tbl;RENAME TABLE $DB.$desc_tbl TO $DB.$src_tbl";
sql_execute_multiple($sql);
system("rm $outfile $infile");

echo $msg_success;

?>
