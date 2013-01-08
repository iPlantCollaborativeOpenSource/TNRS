<?
	require_once('../config.mobile.php');
	require_once('../api/config.php');
	require_once('../api/classes/class.mysqli_database.php');
	require_once('../api/classes/class.taxamatch.php');
	require_once('../api/classes/class.misc.php');

	if ( isset( $_REQUEST['cmd'] ) ) {
		$cmd = $_REQUEST['cmd'];
	} else {
		$cmd = 'search';
	}
	
	switch($cmd) {
		case 'search_results':
	
			$output = '';
			$source = $_REQUEST['source'];
			$searchtxt = $_REQUEST['search_txt'];
			$search_mode = 'normal';
			$debug = 0;
	
			$db = select_source( $source );
			$tm = new Taxamatch($db);
	
			$tm->set('debug_flag',$debug);
			$tm->set('output_type',strtolower($output));
			if($tm->process($searchtxt , $search_mode, $cache )) {
				$tm->generateResponse($cache);
			}
			$data = $tm->getData();
	
			$debug = $tm->debug;
?>
<ul title="Results" class="panel" >


 <div class="panel" title="Results" id="results" style="left: 0%;" selected="true">
    <h2><?=$searchtxt ?></h2>
    <fieldset class="forceleft">
	<div class="row forceleft" style="min-height: 22px; padding-left: 10px">
		<b>Source: </b><?=$source?>
	</div>
<?php 
if($data['genus'] != '') {
?>
	<div class="row forceleft" style="min-height: 22px; padding-left: 10px; background: #efefef">
		<b>Genus Results:</b>
	</div>
	<div style="padding-left: 10px">
<?php
	// Exact Matches
	if (count($data['genus']['exact'])) {
?>
		<b>Exact Match:</b><br />
<?php
	foreach($data['genus']['exact'] as $exact) {
?>		
		<div style="padding: 0 0 5px 10px">
			Genus: <?=$exact['genus']?> <br />
<?php
			if($exact['temp_authority'] != '') {
?>
			Authority: <?=$exact['temp_authority']?> <br />
<?php
			}
?>
			Genus Id: <?=$exact['genus_id']?> <br />
		</div>
<?php
	}
	}
	// Phonetic Matches
	if (count($data['genus']['phonetic'])) {
?>
		<b>Phonetic Matches:</b><br />
<?php
	foreach($data['genus']['phonetic'] as $phonetic) {
?>		
		<div style="padding: 0 0 5px 10px">
			Genus: <?=$phonetic['genus']?> <br />
<?php
			if($phonetic['temp_authority'] != '') {
?>
			Authority: <?=$phonetic['temp_authority']?> <br />
<?php
			}
?>
			Genus Id: <?=$phonetic['genus_id']?>
		</div>
		
<?php
	}
	}
	// Near 1 Matches
	if (count($data['genus']['near_1'])) {
?>
		<b>Near Matches:</b><br />
<?php
	foreach($data['genus']['near_1'] as $near_1) {
?>				
		<div style="padding: 0 0 5px 10px">
			Genus: <?=$near_1['genus']?> <br />
			<? if ($near_1['temp_authority'] != '') { ?>
			Authority: <?=$near_1['temp_authority']?> <br />
			<? } ?>
			Genus Id: <?=$near_1['genus_id']?>
		</div>
<?
		}
	}
?>		

<?
	// Near 2 Matches
	if (count($data['genus']['near_2'])) {
?>
		<b>Kinda Near Matches:</b><br />
<?
	foreach($data['genus']['near_2'] as $near_2) {
?>		
		<div style="padding: 0 0 5px 10px">
			Genus: <?=$near_2['genus']?> <br />
			<? if ($near_2['temp_authority'] != '') { ?>
			Authority: <?=$near_2['temp_authority']?> <br />
			<? } ?>
			Genus Id: <?=$near_2['genus_id']?>
		</div>
<?
		}
	}
?>		

	</div>
<?php
}
if($data['species'] != '') {
?>
	<div class="row forceleft" style="min-height: 22px; background: #efefef">
		<b>Species Results:</b><br />
	</div>
	<div style="padding-left: 10px">
<?php
	if(count($data['species']['exact'])) {
?>				
		<b>Exact Match:</b><br />
<?php
	foreach($data['species']['exact'] as $exact) {
?>				
		<div style="padding-left: 10px">
			Species: <?=$exact['genus_species']?> <br />
			Authority: <?=$exact['temp_authority']?> <br />
			Species Id : <?=$exact['genus_id']?> <br />
		</div>
<?php
	}
	}
	if(count($data['species']['phonetic'])) {
?>		
    <b>Phonetic Matches:</b><br />
<?php
	foreach($data['species']['phonetic'] as $phonetic) {
?>				
		<div style="padding-left: 10px">
			Species: <?=$phonetic['genus_species']?> <br />
			Authority: <?=$phonetic['temp_authority']?> <br />
			Species Id: <?=$phonetic['genus_id']?>
		</div>
	
	
<?php
	}
	}
	// Near 1 Matches
	if (count($data['species']['near_1'])) {
?>
		<b>Near Matches:</b><br />
<?php
	foreach($data['species']['near_1'] as $near_1) {
?>				
		<div style="padding: 0 0 5px 10px">
			Species: <?=$near_1['genus_species']?> <br />
			<? if ($near_1['temp_authority'] != '') { ?>
			Authority: <?=$near_1['temp_authority']?> <br />
			<? } ?>
			Species Id: <?=$near_1['species_id']?>
		</div>
<?
	}
	}
	// Near 2 Matches
	if (count($data['species']['near_2'])) {
?>
		<b>Near Matches:</b><br />
<?php
	foreach($data['species']['near_2'] as $near_2) {
?>				
		<div style="padding: 0 0 5px 10px">
			Species: <?=$near_2['genus_species']?> <br />
			<? if ($near_2['temp_authority'] != '') { ?>
			Authority: <?=$near_2['temp_authority']?> <br />
			<? } ?>
			Species Id: <?=$near_2['species_id']?>
		</div>
<?
	}
	}
	// Near 3 Matches
	if (count($data['species']['near_3'])) {
?>
		<b>Near Matches:</b><br />
<?php
	foreach($data['species']['near_3'] as $near_3) {
?>				
		<div style="padding: 0 0 5px 10px">
			Species: <?=$near_3['genus_species']?> <br />
			<? if ($near_3['temp_authority'] != '') { ?>
			Authority: <?=$near_3['temp_authority']?> <br />
			<? } ?>
			Species Id: <?=$near_3['species_id']?>
		</div>
<?
	}
	}
	// Near 4 Matches
	if (count($data['species']['near_4'])) {
?>
		<b>Near Matches:</b><br />
<?php
	foreach($data['species']['near_4'] as $near_4) {
?>				
		<div style="padding: 0 0 5px 10px">
			Species: <?=$near_4['genus_species']?> <br />
			<? if ($near_4['temp_authority'] != '') { ?>
			Authority: <?=$near_4['temp_authority']?> <br />
			<? } ?>
			Species Id: <?=$near_4['species_id']?>
		</div>
<?
	}
	}
?>
</div>
<?php
}
?>
	</fieldset>
</div>
</ul>

<?php
		break;
		
	case 'search':
		$sources = getSources();
		print '<ul class="panel" title="Search">';
?>
		<form title="Search" class="panel" action="search.php?cmd=search_results" method="POST" selected="true">
		<fieldset>
		<div class="row">
			<fieldset class="forceleft" style="border:none">
			<label>Select Source:</label>
				<select name='source'>
<?php
if(count($sources)) {
	foreach($sources as $src) {
		$selected = ($src['value'] == trim($_REQUEST['source'])) ? " selected='selected' " : '';
		print "<option value='" . $src['value'] . "' $selected>" . $src['name'] . "</option>";
	}
}
?>
				</select>
			</fieldset>
			</div>
			
			<div class="row forceleft">
				<fieldset class="forceleft" style="border:none">
					<label>Name:</label>
					<input type="text" style="width:210px" name="search_txt"/>
				</fieldset>
			</div>
		</fieldset>
		<ul title="Search" id="__1__" style="left: 0%; width: 170px" selected="true" class="panel"><a class="whiteButton" type="submit" href="search.php?cmd=search_results">Search</a></ul>
		<p>Select the "Source" that you would like to use to search against. The name is either a Genus or "Genus Species" that you wish to check.</p>
		<p><i>Note: Please use "Search" button above for app to work correctly.</i></p>
		</form>

<?php
		break;
	}
?>