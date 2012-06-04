<?php
class TnrsAggregator {
	private $data;
	static $ambiguous=array();
	static $rank=array(
		'family' => 0, 
		'genus' => 1,
		'species' => 2,
		'infra1' => 3,
		'infra2' => 4
	);
	static $match_type=array(
		'exact' => 0,
		'phonetic' => 0.5,
		'near_1' => 1,
		'near_2' => 2,
		'near_3' => 3,
		'near_4' => 4
	);
	static $phonetic_array=array("Y" => 1, "" => 0);
	static $acceptance_array=array("Accepted" => 2, "Synonym" => 1, "Illegitimate" => 1, "Invalid" => 1, "No opinion" => 0, "" => 0);
	static $field=array(
		'Name_submitted',	
		'Overall_score',	
		'Name_matched_id',
		'Name_matched',	
		'Name_score',	
		'Name_matched_rank',
		'Author_submitted',
		'Author_matched',
		'Author_score',
		'Canonical_author',	
		'Name_matched_accepted_family',	
		'Genus_submitted',
		'Genus_matched',	
		'Genus_score',
		'Specific_epithet_submitted',
		'Specific_epithet_matched',	
		'Specific_epithet_score',
		'Family_submitted',
		'Family_matched', 
		'Family_score', 
		'Infraspecific_rank',
		'Infraspecific_epithet_matched',
		'Infraspecific_epithet_score',
		'Infraspecific_rank_2',
		'Infraspecific_epithet_2_matched',
		'Infraspecific_epithet_2_score',
		'Annotations',
		'Unmatched_terms',
		'Name_matched_url',
		'Name_matched_lsid',
		'Phonetic',
		'Taxonomic_status',
		'Accepted_name',
		'Accepted_species',
		'Accepted_name_author',
		'Accepted_name_id',
		'Accepted_name_rank',
		'Accepted_name_url',
		'Accepted_name_lsid',
		'Accepted_family',
		'Overall_score_order',
		'Highertaxa_score_order',
		'Source',
		'Warnings'
	);
	static $flag_def=array(
		'Partial'	=> 0x1,
		'Ambiguous'	=> 0x2,
		'HigherTaxa' => 0x4,
		'Overall' => 0x8
	);
	static $forma=array(
		"forma",
		"form.",
		"form",
		"fo.",
		"fo",
		"f."
	);
	static $standard_rank=array(
		'agsp'=>'agsp.',
		'agsp.'=>'agsp.',
		'convar.'=>'convar.',
		'convar'=>'convar.',
		'cult.'=>'cv.',
		'cult'=>'cv.',
		'cultivar'=>'cv.',
		'cv'=>'cv.',
		'cv..'=>'cv.',
		'cv'=>'cv.',
		'fo.'=>'fo.',
		'fo'=>'fo.',
		'f.'=>'fo.',
		'forma'=>'fo.',
		'grex'=>'grex',
		'lusus'=>'lusus',
		'monstr'=>'monstr.',
		'nothosubsp'=>'nothosubsp.',
		'nothogen'=>'nothogen.',
		'nothomorph'=>'nothomorph',
		'nothosect.'=>'nothosect.',
		'nothosect'=>'nothosect.',
		'nothoser.'=>'nothoser.',
		'nothoser'=>'nothoser.',
		'nothosubgen.'=>'nothosubgen.',
		'nothosubgen'=>'nothosubgen.',
		'nothosbgen'=>'nothosubgen.',
		'nothosbgen'=>'nothosubgen.',
		'nothosubsp.'=>'nothosubsp.',
		'nothosubsp.'=>'nothosubsp.',
		'nothosbsp.'=>'nothosubsp.',
		'nothosbsp.'=>'nothosubsp.',
		'nothossp.'=>'nothosubsp.',
		'nothossp'=>'nothosubsp.',
		'nothovar.'=>'nothovar.',
		'nothovar'=>'nothovar.',
		'proles'=>'proles',
		'race'=>'race',
		'rasse'=>'race',
		'sect.'=>'sect.',
		'sect'=>'sect.',
		'ser'=>'ser.',
		'ser.'=>'ser.',
		'sport'=>'sport',
		'stirps'=>'stirps',
		'subfo.'=>'subfo.',
		'subfo'=>'subfo.',
		'subf.'=>'subfo.',
		'subf'=>'subfo.',
		'subforma.'=>'subfo.',
		'sbfo.'=>'subfo.',
		'sbforma'=>'subfo.',
		'subgen.'=>'subgen.',
		'subgen'=>'subgen.',
		'subsect'=>'subsect.',
		'subsect.'=>'subsect.',
		'subser.'=>'subser.',
		'subser'=>'subser.',
		'subsp'=>'subsp.',
		'subsp.'=>'subsp.',
		'sbsp.'=>'subsp.',
		'sbsp'=>'subsp.',
		'ssp.'=>'subsp.',
		'ssp'=>'subsp.',
		'subspecies'=>'subsp.',
		'substirps'=>'substirps',
		'subvar.'=>'subvar.',
		'subvar'=>'subvar.',
		'supersect.'=>'supersect.',
		'var'=>'var.',
		'var.'=>'var.',
		'variety'=>'var.'
	);
	static $sort_scheme;
	static $extra_penalty=0.1;
	static $rank_penalty=0.3;
	static $author_weight=0.2;

	public function __construct( $db = null ) {
		$this->db=$db;
		$this->xml = new SimpleXml(XML_STRING);
		$this->data=array();
	}

	public function aggregate( $taxamatch = null ) {
		$taxamatch_result=$taxamatch->getData();
		$parsed=array();
		$parsed["family"]=$taxamatch->this_search_family;
		$parsed["genus"]=$taxamatch->this_search_genus;
		$parsed["species"]=$taxamatch->this_search_species;
		$parsed["infra1"]=$taxamatch->this_search_infra1;
		$parsed["infra2"]=$taxamatch->this_search_infra2;
		$parsed["authority"]=$taxamatch->this_authority;
		$parsed["authorities"]=$taxamatch->this_authorities;
		$parsed["rank1"]=$taxamatch->this_search_rank1;
		$parsed["rank2"]=$taxamatch->this_search_rank2;
		$start_string=$taxamatch->this_start_string;
		$family_string=$taxamatch->this_family_string;
		$family_unmatched=$taxamatch->this_family_unmatched;
		$status_string=$taxamatch->this_status_string;
		$cleaned_txt=$taxamatch->this_cleaned_txt;
		$preprocessed_txt=$taxamatch->this_preprocessed_txt;
		$scientificname=$taxamatch->searchtxt;

		if ($taxamatch->parse_only) {
			$unparsed=$preprocessed_txt;
			foreach (array_keys($parsed) as $pk) {
				if ($pk == 'authority') {
					continue;
				} elseif ($pk == 'authorities') {
					foreach ($parsed[$pk] as $auth) {
						if ($auth) {
							$unparsed=str_ireplace_first($auth, '', $unparsed);
						}
					}
				} elseif ($parsed[$pk]) {
					$unparsed=str_ireplace_first($parsed[$pk], '', $unparsed);
					if ($parsed[$pk] == 'f.' && ($pk == "rank1" || $pk == "rank2")) {
						$unparsed=str_ireplace_first(self::$forma, '', $unparsed);
					}
				}
			}
			if (isset($start_string)) {
				$unparsed=$start_string . $unparsed;
			}
			$unparsed=trim($unparsed);
			$parsed_rank1=array_key_exists($parsed["rank1"], self::$standard_rank) ? self::$standard_rank[$parsed["rank1"]] : $parsed["rank1"];
			$parsed_rank2=array_key_exists($parsed["rank2"], self::$standard_rank) ? self::$standard_rank[$parsed["rank2"]] : $parsed["rank2"];
			$this->data[]=array(
				'Name_submitted' => $scientificname,
				'Family' => $parsed["family"], 
				'Genus' => $parsed["genus"],
				'Specific_epithet' => $parsed["species"],
				'Infraspecific_rank' => $parsed_rank1,
				'Infraspecific_epithet' => $parsed["infra1"],
				'Infraspecific_rank_2' => $parsed_rank2,
				'Infraspecific_epithet_2' => $parsed["infra2"],
				'Author' => $parsed["authority"],
				'Annotations' => $status_string,
				'Unmatched_terms' => $unparsed
			);
			return true;
		}

		$lowest_parsed_rank='';
		$lowest_matched_rank='';
		foreach (array_keys(self::$rank) as $rk) {
			if ($parsed[$rk]) {
				$lowest_parsed_rank=$rk;
			}
		}

		$gni_parser_result=null;

		if (isset($taxamatch_result["gni_parser_result"])) {
			$gni_parser_result=$taxamatch_result["gni_parser_result"];
		}

		$all_id=array();
		$all_name=array();

		$matched=array();
		$matched_by_source=array();
		$synonym=array();
		$name_source_data=array();
		$classification_family=array();
		$phonetic_id=array();
		$alt_accepted_family=array();
		
		$family_matched=array();
		$genus_matched=array();
		$species_matched=array();
		$infra1_matched=array();
		$infra2_matched=array();

		$all_matched=array();
		$best_ed=array();
		$higher_taxa_ed=array();

		foreach (array_keys(self::$rank) as $rk) {
			$all_matched[$rk]=array();
			if(isset($taxamatch_result[$rk]) && count($taxamatch_result[$rk])) {
				foreach (array_keys(self::$match_type) as $type) {
					if (isset($taxamatch_result[$rk][$type])) {
						if(!isset($best_ed[$rk]) || $best_ed[$rk] > self::$match_type[$type]) {
							$best_ed[$rk]=self::$match_type[$type];
						}
						foreach ($taxamatch_result[$rk][$type] as &$match) {
							$match["match_type"]=$type;
							$match["match_score"]=0;
							$match["Lowest_matched_rank"]=$rk;
							$match["Name_matched_id"]=$match[$rk . "_id"];
							$all_id[]=$match["Name_matched_id"];
							if ($type == 'exact' || $type == 'phonetic') {
								$phonetic_id[$match["Name_matched_id"]]=1;
							}
						}
						$all_matched[$rk]=array_merge($all_matched[$rk], $taxamatch_result[$rk][$type]);
					}
				}
			}
		}
		
		if (count($all_id)) {
			$name_res=$this->db->getScientificName($all_id);
			$accepted_name_id=array();
			foreach ($name_res as $nm) {
				$all_name[$nm->nameID]=$nm;
				$name_source_data[$nm->nameID][$nm->sourceID]['url']=$nm->name_source_url;
				$name_source_data[$nm->nameID][$nm->sourceID]['lsid']=$nm->lsid;
			}
			$miss_accepted_name_id=array();
			$synonym_res=$this->db->getSynonym($all_id);
			foreach ($synonym_res as $sn) {
				$nid=$sn->nameID;
				$sid=$sn->sourceID;
				if ($sn->acceptance == 'Accepted') {
					$synonym[$nid][$sid]["accepted_name_id"]=$nid;
					$synonym[$nid][$sid]["acceptance"]=$sn->acceptance;
				} elseif ($sn->acceptance == 'Synonym' || $sn->acceptance == 'Invalid' || $sn->acceptance == 'Illegitimate') {
					if ($sn->accepted_name_id) {
						$accepted_name_id[]=$sn->accepted_name_id;
						$synonym[$nid][$sid]["accepted_name_id"]=$sn->accepted_name_id;
						$synonym[$nid][$sid]["acceptance"]=$sn->acceptance;
					}
				}
			}
			//print_r($synonym);
			if (count($accepted_name_id)) {
				$name_res=$this->db->getScientificName($accepted_name_id);
				foreach ($name_res as $nm) {
					$all_name[$nm->nameID]=$nm;
					$name_source_data[$nm->nameID][$nm->sourceID]['url']=$nm->name_source_url;
					$name_source_data[$nm->nameID][$nm->sourceID]['lsid']=$nm->lsid;
				}
			}
			//print_r($all_name);
			$classificationfamily_res=$this->db->getClassificationFamily(array_keys($all_name));
			$cl_fam=array();
			foreach ($classificationfamily_res as $cf) {
				$cl_fam[$cf->nameID][$cf->sourceID]=$cf->family;
			}
			
			$source_id=array();
			if (isset($this->db->classification_id)) {
				$source_id[]=$this->db->classification_id;
			} else {
				$source_id=array_keys($this->db->source_name);
			}

			foreach (array_keys($cl_fam) as $nid) {
				foreach ($source_id as $sid) {
					if (isset($cl_fam[$nid][$sid])) {
						$classification_family[$nid]=$cl_fam[$nid][$sid];
						break;
					}
				}
			}
		}
		//print_r($name_source_data);
		//print_r($classification_family);

		if (count($all_matched['family'])) {
			$fm=array();
			foreach ($all_matched['family'] as $fam) {
				$fm[]=$fam['family'];
			}
			$fam_res=$this->db->getFamilyAcceptedFamily($fm);
			foreach ($fam_res as $fam) {
				$alt_accepted_family[$fam->family]=$fam->accepted_family;
			}
		}

		foreach (array_keys(self::$rank) as $rk) {
			foreach ($all_matched[$rk] as &$match) {
				if (self::$rank[$rk] > self::$rank['family']) {
					$match["Family_matched"]="";
					$match["family_ed"]=0;
					if($parsed["family"]) {
						$match["family_ed"]=strlen($parsed["family"]);
						$accepted_family=isset($classification_family[$match["Name_matched_id"]]) ? $classification_family[$match["Name_matched_id"]] : '';
						$genus=$all_name[$match["Name_matched_id"]]->genus;
						$family_filtered=$this->filterFamily($all_matched["family"], $accepted_family, $genus, $alt_accepted_family);
						if (count($family_filtered)) {
							$match["family_ed"]=$family_filtered[0]["family_ed"];
							$match["Family_matched"]=$family_filtered[0]["family"];
							$match["Family_score"]=self::getEDScore($match["family_ed"], $match["Family_matched"], $parsed["family"]);
							$match["match_score"]+=$match["Family_score"];
						}
					}
				}
			}
		}

		if (count($all_matched["infra2"])) {
			foreach ($all_matched["infra2"] as &$match) {
				if (isset($higher_taxa_ed["infra2"]) && $higher_taxa_ed["infra2"] <= $match["infra2_ed"]) {
					continue;
				}
				$name=$all_name[$match["Name_matched_id"]];
				$match["Genus_matched"]=$name->genus;
				$match["Specific_epithet_matched"]=$name->specificEpithet;
				$match["Infraspecific_rank"]=$name->rankIndicator;
				$match["Infraspecific_epithet_matched"]=$name->infraspecificEpithet;
				$match["Infraspecific_rank_2"]=$name->infraspecificRank2;
				$match["Infraspecific_epithet_2_matched"]=$name->infraspecificEpithet2;

				$match["Genus_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $parsed["genus"]);
				$match["Specific_epithet_score"]=self::getEDScore($match["species_ed"], $match["Specific_epithet_matched"], $parsed["species"]);
				$match["Infraspecific_epithet_score"]=self::getEDScore($match["infra1_ed"], $match["Infraspecific_epithet_matched"], $parsed["infra1"]);
				$match["Infraspecific_epithet_2_score"]=self::getEDScore($match["infra2_ed"], $match["Infraspecific_epithet_2_matched"], $parsed["infra2"]);
				$match["Name_matched"]=$match["genus_species_infra1_infra2"];
				if (!isset($higher_taxa_ed["infra1"]) || $higher_taxa_ed["infra1"] > $match["infra1_ed"]) {
					$higher_taxa_ed["infra1"]=$match["infra1_ed"];
				}
				if (!isset($higher_taxa_ed["species"]) || $higher_taxa_ed["species"] > $match["species_ed"]) {
					$higher_taxa_ed["species"]=$match["species_ed"];
				}
				if (!isset($higher_taxa_ed["genus"]) || $higher_taxa_ed["genus"] > $match["genus_ed"]) {
					$higher_taxa_ed["genus"]=$match["genus_ed"];
				}
				if (isset($match["family_ed"]) && (!isset($higher_taxa_ed["family"]) || $higher_taxa_ed["family"] > $match["family_ed"])) {
					$higher_taxa_ed["family"]=$match["family_ed"];
				}
				if (self::cmpRank($match["Infraspecific_rank"], $parsed["rank1"]) !== true) {
					$match["Infraspecific_epithet_score"]-=self::$rank_penalty;
				}
				if (self::cmpRank($match["Infraspecific_rank_2"], $parsed["rank2"]) !== true) {
					$match["Infraspecific_epithet_2_score"]-=self::$rank_penalty;
				}
				$match["match_score"]+=$match["Specific_epithet_score"]+$match["Genus_score"]+$match["Infraspecific_epithet_score"]+$match["Infraspecific_epithet_2_score"];
				$matched[]=$match;
			}
		}
		if (count($all_matched["infra1"]) && (!isset($higher_taxa_ed["infra1"]) || isset($best_ed["infra1"]) && $higher_taxa_ed["infra1"] > $best_ed["infra1"])) {
			foreach ($all_matched["infra1"] as &$match) {
				if (isset($higher_taxa_ed["infra1"]) && $higher_taxa_ed["infra1"] <= $match["infra1_ed"]) {
					continue;
				}
				$name=$all_name[$match["Name_matched_id"]];
				$match["Genus_matched"]=$name->genus;
				$match["Specific_epithet_matched"]=$name->specificEpithet;
				$match["Infraspecific_rank"]=$name->rankIndicator;
				$match["Infraspecific_epithet_matched"]=$name->infraspecificEpithet;

				$match["Genus_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $parsed["genus"]);
				$match["Specific_epithet_score"]=self::getEDScore($match["species_ed"], $match["Specific_epithet_matched"], $parsed["species"]);
				$match["Infraspecific_epithet_score"]=self::getEDScore($match["infra1_ed"], $match["Infraspecific_epithet_matched"], $parsed["infra1"]);
				$match["Name_matched"]=$match["genus_species_infra1"];
				if (!isset($higher_taxa_ed["species"]) || $higher_taxa_ed["species"] > $match["species_ed"]) {
					$higher_taxa_ed["species"]=$match["species_ed"];
				}
				if (!isset($higher_taxa_ed["genus"]) || $higher_taxa_ed["genus"] > $match["genus_ed"]) {
					$higher_taxa_ed["genus"]=$match["genus_ed"];
				}
				if (isset($match["family_ed"]) && (!isset($higher_taxa_ed["family"]) || $higher_taxa_ed["family"] > $match["family_ed"])) {
					$higher_taxa_ed["family"]=$match["family_ed"];
				}
				if (self::cmpRank($match["Infraspecific_rank"], $parsed["rank1"]) !== true) {
					$match["Infraspecific_epithet_score"]-=self::$rank_penalty;
				}
				$match["match_score"]+=$match["Specific_epithet_score"]+$match["Genus_score"]+$match["Infraspecific_epithet_score"];
				$matched[]=$match;
			}
		}
		if (count($all_matched["species"]) && (!isset($higher_taxa_ed["species"]) || isset($best_ed["species"]) && $higher_taxa_ed["species"] > $best_ed["species"])) {
			foreach ($all_matched["species"] as &$match) {
				if (isset($higher_taxa_ed["species"]) && $higher_taxa_ed["species"] <= $match["species_ed"]) {
					continue;
				}
				$name=$all_name[$match["Name_matched_id"]];
				$match["Genus_matched"]=$name->genus;
				$match["Specific_epithet_matched"]=$name->specificEpithet;
				$match["Genus_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $parsed["genus"]);
				$match["Specific_epithet_score"]=self::getEDScore($match["species_ed"], $match["Specific_epithet_matched"], $parsed["species"]);
				$match["Name_matched"]=$match["genus_species"];
				if (!isset($higher_taxa_ed["genus"]) || $higher_taxa_ed["genus"] > $match["genus_ed"]) {
					$higher_taxa_ed["genus"]=$match["genus_ed"];
				}
				if (isset($match["family_ed"]) && (!isset($higher_taxa_ed["family"]) || $higher_taxa_ed["family"] > $match["family_ed"])) {
					$higher_taxa_ed["family"]=$match["family_ed"];
				}
				$match["match_score"]+=$match["Genus_score"]+$match["Specific_epithet_score"];
				$matched[]=$match;
			}
		} 
		if (count($all_matched["genus"]) && (!isset($higher_taxa_ed["genus"]) || isset($best_ed["genus"]) && $higher_taxa_ed["genus"] > $best_ed["genus"])) {
			foreach ($all_matched["genus"] as &$match) {
				if (isset($higher_taxa_ed["genus"]) && $higher_taxa_ed["genus"] <= $match["genus_ed"]) {
					continue;
				}
				$name=$all_name[$match["Name_matched_id"]];
				$match["Genus_matched"]=$name->genus;
				$match["Specific_epithet_matched"]='';
				$match["Genus_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $parsed["genus"]);
				$match["Specific_epithet_score"]='';
				$match["Name_matched"]=$match["Genus_matched"];
				if (isset($match["family_ed"]) && (!isset($higher_taxa_ed["family"]) || $higher_taxa_ed["family"] > $match["family_ed"])) {
					$higher_taxa_ed["family"]=$match["family_ed"];
				}
				$match["match_score"]+=$match["Genus_score"];
				$matched[]=$match;
			}
		}
		if (count($all_matched["family"]) && (!isset($higher_taxa_ed["family"]) || isset($best_ed["family"]) && $higher_taxa_ed["family"] > $best_ed["family"])) {
			foreach ($all_matched["family"] as &$match) {
				if (isset($higher_taxa_ed["family"]) && $higher_taxa_ed["family"] <= $match["family_ed"]) {
					continue;
				}
				$match["Family_matched"]=$match["family"];
				$match["Specific_epithet_matched"]='';
				$match["Genus_matched"]='';
				$match["Family_score"]=self::getEDScore($match["family_ed"], $match["Family_matched"], $parsed["family"]);
				$match["Genus_score"]='';
				$match["Specific_epithet_score"]='';
				$match["Name_matched"]=$match["Family_matched"];
				$match["match_score"]+=$match["Family_score"];
				$matched[]=$match;
			}
		}
		if (!count($matched)) {
			$matched[]=array("Lowest_matched_rank" => "", "Name_score" => "", "Family_matched" => "", "Author_score" => "", "Name_matched_accepted_family" => "", "Warnings" => 0);
		}

		$status=$status_string;

		if ($status) {
			if (preg_match("/vel\.? sp\.? aff\.?/i", $status)) {
				$status="vel. sp. aff.";
			} elseif (preg_match("/\-?aff\.?/i", $status)) {
				$status="aff.";
			} elseif (preg_match("/\-?cf\.?/i", $status) || $status == '?') {
				$status="cf.";
			}
		}

		$unmatched=array();

		foreach ($matched as &$match) {
			$match["Name_submitted"]=$scientificname;
			$match["Family_submitted"]=$parsed["family"];
			$match["Genus_submitted"]=$parsed["genus"];
			$match["Specific_epithet_submitted"]=$parsed["species"];
			$match["Author_submitted"]=$parsed["authority"];

			$match["Annotations"]=$status;

			$rk=$match["Lowest_matched_rank"];
			$parsed_part=0;
			$matched_part=0;
			if ($rk) {
				$matched_part=self::$rank[$rk];
				if ($match["Family_matched"]) { $matched_part++; }
			}

			if ($lowest_parsed_rank) { $parsed_part=self::$rank[$lowest_parsed_rank]; }
			if ($parsed["family"]) { $parsed_part++; }

			$unmatched='';
			$unmatched_part=0;
			$extra_part=0;

			if (! $rk || self::$rank[$rk] < self::$rank['genus'] || $gni_parser_result && $gni_parser_result->scientificName->parsed) {
				$unmatched=$preprocessed_txt;
			} elseif ($preprocessed_txt != $cleaned_txt) {
				$unmatched=str_ireplace_first($cleaned_txt, '', $preprocessed_txt);
			}
			
			if ($match["Family_matched"]) {
				$unmatched=str_ireplace_first($parsed['family'], '', $unmatched);
			}
			if ($rk && $gni_parser_result && $gni_parser_result->scientificName->parsed) {
				foreach (array_keys(self::$rank) as $rki) {
					if (self::$rank[$rki] >= self::$rank['genus'] && self::$rank[$rki] <= self::$rank[$rk]) {
						$unmatched=str_ireplace_first($parsed[$rki], '', $unmatched);
					}
				}
				foreach ($parsed["authorities"] as $auth) {
					if ($auth) {
						$unmatched=str_ireplace_first($auth, '', $unmatched);
					}
				}	
				if (($rk == 'infra1' || $rk == 'infra2') && $parsed["rank1"]) {
					$unmatched=str_ireplace_first($parsed["rank1"], '', $unmatched);
					$matched_part++;
					if ($parsed["rank1"] == 'f.') {
						$unmatched=str_ireplace_first(self::$forma, '', $unmatched);
					}
				}
				if ($rk == 'infra2' && $parsed["rank2"]) {
					$unmatched=str_ireplace_first($parsed["rank2"], '', $unmatched);
					$matched_part++;
					if ($parsed["rank2"] == 'f.') {
						$unmatched=str_ireplace_first(self::$forma, '', $unmatched);
					}
				}
				if ($all_name[$match["Name_matched_id"]]->isHybrid) {
					$unmatched=str_ireplace_first(' x', ' ', $unmatched);
					$unmatched=str_ireplace_first(' ×', ' ', $unmatched);
				}
			}
			//print_r($matched);
			if (strlen($unmatched)) {
				$unmatched=trim(preg_replace("/ {2,}/", ' ', $unmatched));
			}
			if (strlen($unmatched)) {
				$unmatched_part=count(explode(" ", $unmatched));
				$extra_part=$unmatched_part+$matched_part-$parsed_part;
				if ($parsed["rank1"]) { $extra_part--; }
				if ($parsed["rank2"]) { $extra_part--; }
				if ($extra_part > 1) {
					$unmatched_part-=$extra_part-1;
				}
			}
			if (isset($start_string)) {
				$unmatched=$start_string . $unmatched;
			}
			$match["Unmatched_terms"]=trim($unmatched);

			if($rk) {
				$matched_id[]=$match["Name_matched_id"];
			
				$match["Canonical_author"]=$match["temp_authority"];

				if (array_key_exists($match[$rk . "_id"], $phonetic_id)) {
					$match["Phonetic"]='Y';
				} else {
					$match["Phonetic"]="";
				}

				if (! isset($match["Warnings"])) { $match["Warnings"]=0; }
				if (isset($gni_parser_result) && $lowest_parsed_rank != $match["Lowest_matched_rank"]) {
					$match["Warnings"]|=self::$flag_def['Partial'];				
				}
				if (!$lowest_matched_rank || self::$rank[$match["Lowest_matched_rank"]] > self::$rank[$lowest_matched_rank]) {
					$lowest_matched_rank=$match["Lowest_matched_rank"];
				}

				if ($parsed["authority"] && ! ($match["Warnings"] & self::$flag_def['Partial'])) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]='';
					$match["Author_score"]='';
				}

				#$match["Name_score"]=$match["match_score"]/$parsed_part;
				$match["Name_score"]=self::num_to_score($match["match_score"], $parsed_part, 2, 1);
				#$match["Overall_score"]=self::getOverallScore($match["match_score"], $match["Author_score"], $matched_part+$unmatched_part);
				$match["Overall_score"]=self::getOverallScore($match["Name_score"], $match["Author_score"], $extra_part);
			}
		}
		
		$source_order=0;
		foreach ($this->db->source as $so) {
			$source_order++;
			$matched_by_source[$so]=array();
			$sid=$this->db->source_id[$so];
			foreach ($matched as &$match) {
				if(isset($match["Name_matched_id"])) {
					$nid=$match["Name_matched_id"];
					if (array_key_exists($sid, $name_source_data[$nid])) {
						$match["Source"]=$so;
						$match["Source_order"]=$source_order;
						$match["Name_matched_rank"]=$all_name[$nid]->nameRank;
						$match["Name_matched_accepted_family"]=isset($classification_family[$nid]) ? $classification_family[$nid] : '';
						$match["Name_matched_url"]=$name_source_data[$nid][$sid]['url'];
						$match["Name_matched_lsid"]=$name_source_data[$nid][$sid]['lsid'];
						$match["Taxonomic_status"]=isset($synonym[$nid][$sid]["acceptance"]) ? $synonym[$nid][$sid]["acceptance"] : 'No opinion';
						$match["Accepted_name"]='';
						if (isset($synonym[$nid][$sid]["accepted_name_id"])) {
							$match["Accepted_name_id"]=$synonym[$nid][$sid]["accepted_name_id"];
							$accepted_name=$all_name[$match["Accepted_name_id"]];
							$match["Accepted_name"]=$accepted_name->scientific_name;

							$match["Accepted_name_author"]=$accepted_name->author;
							$match["Accepted_name_url"]=$name_source_data[$match["Accepted_name_id"]][$sid]['url'];
							$match["Accepted_name_lsid"]=$name_source_data[$match["Accepted_name_id"]][$sid]['lsid'];
							$match["Accepted_name_rank"]=$accepted_name->nameRank;
							$match["Accepted_family"]=isset($classification_family[$match["Accepted_name_id"]]) ? $classification_family[$match["Accepted_name_id"]] : "";
						}
						$match["Accepted_species"]="";
						if ($match["Accepted_name"] && self::$rank[$match["Lowest_matched_rank"]] >= self::$rank["species"]) {
							if ($all_name[$match["Accepted_name_id"]]->isHybrid) {
								$match["Accepted_species"]=$all_name[$match["Accepted_name_id"]]->scientific_name;
							} else {
								$match["Accepted_species"]=$all_name[$match["Accepted_name_id"]]->genus . ' ' . $all_name[$match["Accepted_name_id"]]->specificEpithet;
							}
						}
						$matched_by_source[$so][]=$match;
					}
				} else {
					$matched_by_source[""]=$match;
				}
			}
		}
		$matched=array();
		if (isset($matched_by_source[""])) {
			$matched[]=$matched_by_source[""];
		} else {
			foreach ($this->db->source as $so) {
				foreach ($matched_by_source[$so] as &$match) {
					$matched[]=$match;
				}
			}
		}
		self::$sort_scheme='overall';
		usort($matched, array($this, "cmpMatched"));
		$order=1;
		foreach ($matched as &$match) {
			$match["Overall_score_order"]=$order++;
		}
		self::$sort_scheme='highertaxa';
		usort($matched, array($this, "cmpMatched"));
		$order=1;
		foreach ($matched as &$match) {
			$match["Highertaxa_score_order"]=$order++;
		}
		foreach ($matched as &$match) {
			if (isset($match['Name_matched_id']) && isset(self::$ambiguous[$match['Name_matched_id']])) {
				$match['Warnings']|=self::$flag_def['Ambiguous'];
			}
			if ($match["Highertaxa_score_order"] > $match["Overall_score_order"]) {
				$match['Warnings']|=self::$flag_def['HigherTaxa'];
			}  elseif ($match["Highertaxa_score_order"] < $match["Overall_score_order"]) {
				$match['Warnings']|=self::$flag_def['Overall'];
			}
		}
		foreach ($matched as &$match) {
			$result=array();
			foreach (self::$field as $fd) {
				if (array_key_exists($fd, $match) && ! is_null($match[$fd])) {
					$result[$fd]=$match[$fd];
				} else {
					$result[$fd]='';
				}
			}
			$this->data[]=$result;
		}
	}

	public function getData () {
		return $this->data;
	}

	public function filterFamily($family, $accepted_family, $genus, $alt_accepted_family) {
		$family_filtered=array();
		if ($accepted_family) {
			$alt_family=array();
			$altfam_res=$this->db->getAltFamily($accepted_family, $genus);
			foreach ($altfam_res as $afm) {
				$alt_family[$afm->altFamily]=1;
			}
			foreach ($family as $fam) {
				if ($fam["family"] == $accepted_family || array_key_exists($fam["family"], $alt_family) || isset($alt_accepted_family[$fam["family"]]) && ($alt_accepted_family[$fam["family"]] == $accepted_family || array_key_exists($alt_accepted_family[$fam["family"]], $alt_family)))	{
					$family_filtered[]=$fam;
				}
			}
		}
		return $family_filtered;
	}

	static function cmpMatched ($a, $b) {
		if (self::$sort_scheme == 'highertaxa') {
			foreach (array_keys(self::$rank) as $rk) {
				$rk_matched=ucfirst($rk) . "_matched";
				if (isset($a[$rk_matched]) && isset($b[$rk_matched])) {
					$rked=$rk . "_ed";
					$aed=isset($a[$rked]) ? $a[$rked] : 0;
					$bed=isset($b[$rked]) ? $b[$rked] : 0;
					if ($aed != $bed) {
						return ($aed < $bed) ? -1 : +1;
					}
				}
			}
		}
		$as=$a['Overall_score'];
		$bs=$b['Overall_score'];
		$at=$a['Name_score'];
		$bt=$b['Name_score'];
		//$ap=$a['Phonetic'];
		//$bp=$b['Phonetic'];
		$aa=$a['Taxonomic_status'];
		$ba=$b['Taxonomic_status'];
		$an=$a['Name_matched'];
		$bn=$b['Name_matched'];
		$aca=$a['Canonical_author'];
		$bca=$b['Canonical_author'];
		$aso=$a['Source_order'];
		$bso=$b['Source_order'];

		if ($at != $bt) {
			return ($at > $bt) ? -1 : +1;
		}
		//if ($ap != $bp) {
		//	return (self::$phonetic_array[$ap] > self::$phonetic_array[$bp]) ? -1 : +1; 
		//}
		if ($as != $bs) {
			return ($as > $bs) ? -1 : +1;
		}
		if ($aa != $ba) {
			return (self::$acceptance_array[$aa] > self::$acceptance_array[$ba]) ? -1 : +1;
		}
		if ($an != $bn) {
			return ($an < $bn) ? -1 : +1;
		}
		if ($aso != $bso) {
			return ($aso < $bso) ? -1 : +1;
		}
		self::$ambiguous[$a['Name_matched_id']]=1;
		self::$ambiguous[$b['Name_matched_id']]=1;
		if ($aca != $bca) {
			if (strlen($aca) == 0) {
				return 1;
			} elseif (strlen($bca) == 0) {
				return -1;
			}
			return ($aca < $bca) ? -1 : +1;
		}
		return 0;
	}

	static function getEDScore($ed=0, $str1='', $str2='') {
		$score=0;
		$l1=mb_strlen($str1);
		$l2=mb_strlen($str2);
		if ($l1 > 0 || $l2 > 0) {
			$score=(1-$ed/($l1 > $l2 ? $l1 : $l2));
		}
		return $score;
	}

	static function getOverallScore($namescore=0, $authorscore='', $extra_part) {
		$score=strlen($authorscore) ? ($namescore * (1-self::$author_weight) + $authorscore * self::$author_weight) : $namescore;
		if ($extra_part > 0) {
			$score-=self::$extra_penalty;
		}
		return $score;
	}
	static function cmpRank($rank1, $rank2) {
		$r1=explode(".", $rank1);
		$r2=explode(".", $rank2);
		if (stripos($r1[0], 'ssp') !== false) {
			$r1[0]='subsp';
		}
		if (stripos($r2[0], 'ssp') !== false) {
			$r2[0]='subsp';
		}
		if (stripos($r1[0],$r2[0]) === false && stripos($r2[0],$r1[0]) === false) {
			return false;
		} else {
			return true;
		}
	}
	static function num_to_score($num, $max, $s, $t) {
		#return (atan($num * pow($num, 2)) * 0.5/(pi()*0.5) + 0.5);
		$num=2*$num-$max;
		#return (atan($num * pow($num, 2))/atan($max * pow($max, 2)) * 0.5 + 0.5);
		return (atan(pow(($s * $num / $max), (2 * $t + 1)))/(2 * atan(pow($s, (2 * $t + 1)))) + 0.5);
	}
}
?>
