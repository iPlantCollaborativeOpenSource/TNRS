<?php
// Everything you need to set is here and in global_params.inc

include "../global_params.inc";		// most parameters here

// confirmation message
$confmsg="\r\nRebuild taxamatch tables in database `$DB`? (Y/N):";

if ($is_default=true) {
	$sourceName_primary=$sourceName;
}else{
	// Enter primary source here if not same 
	// as current source being loaded
	// This source MUST already be in database
	// This is source used to build preferred 
	// classification of genera in families
	$sourceName_primary="Tropicos";	
}

$fld_default_family="defaultFamily"; // name of default family field in core db. Just in case this changes.

// Table names
$tbl_fam="famlist";
$tbl_gen="genlist";
$tbl_sp="splist";
$tbl_infra1="infra1list";
$tbl_infra2="infra2list";
$tbl_comb_fg="genlist_famlist_combined";
$tbl_comb_gs="splist_genlist_combined";
$tbl_comb_st="infra1list_splist_combined";
$tbl_comb_tq="infra2list_infra1list_combined";


?>
