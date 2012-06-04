<?php

echo "Updating genera...";

$src_tbl=$tbl_gen;
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
		$row["GEN_LENGTH"]=strlen($row["GENUS"]);
		$row["NEAR_MATCH_GENUS"]=$nm->near_match($row["GENUS"]);
		$row["SEARCH_GENUS_NAME"]=$n->normalize($row["GENUS"]);
		$row["sgn_head1"]=substr($row["GENUS"], 0 ,1);
		$row["sgn_head2"]=substr($row["GENUS"], 0 ,2);
		$row["sgn_head3"]=substr($row["GENUS"], 0 ,3);
		$row["sgn_tail1"]=substr($row["GENUS"], -1 ,1);
		$row["sgn_tail2"]=substr($row["GENUS"], -2 ,2);
		$row["sgn_tail3"]=substr($row["GENUS"], -3 ,3);
		$field=array_values($row);
		unset($n);
		unset($nm);
		unset($row);
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
