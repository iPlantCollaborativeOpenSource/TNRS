<?php
class TnrsAggregator {
	private $data;
	static $field=array(
		'ScientificName_submitted',	
		'Overall_match_score',	
		'Lowest_scientificName_matched_ID',
		'Lowest_scientificName_matched',	
		'Lowest_sciName_matched_score',	
		'Author_matched',
		'Author_matched_score',
		'Canonical_author',	
		'Accepted_family',	
		'Genus_matched',	
		'Genus_matched_score',
		'SpecificEpithet_matched',	
		'SpecificEpithet_matched_score',
		'Family_submitted',
		'Family_matched', 
		'Family_matched_score', 
		'infraspecific1Rank',
		'infraspecific1Epithet',
		'infraspecific1Score',
		'infraspecific2Rank',
		'infraspecific2Epithet',
		'infraspecific2Score',
		'Status',
		'Unmatched',
		'NameSourceUrl',
		'Phonetic',
		'Acceptance',
		'Accepted_name',
		'Accepted_author',
		'Accepted_name_ID',
		'Accepted_name_SourceUrl'
	);

	public function __construct( $db = null ) {
		$this->db=$db;
		$this->xml = new SimpleXml(XML_STRING);
		$this->data=array();
	}

	public function aggregate( $taxamatch = null ) {
		$taxamatch_result=$taxamatch->getData();
		$family_parsed=$taxamatch->this_search_family;
		$genus_parsed=$taxamatch->this_search_genus;
		$species_parsed=$taxamatch->this_search_species;
		$infra1_parsed=$taxamatch->this_search_infra1;
		$rank1_parsed=$taxamatch->this_search_rank1;
		$infra2_parsed=$taxamatch->this_search_infra2;
		$rank2_parsed=$taxamatch->this_search_rank2;
		$authority_parsed=$taxamatch->this_authority;
		$start_string=$taxamatch->this_start_string;
		$family_string=$taxamatch->this_family_string;
		$family_unmatched=$taxamatch->this_family_unmatched;
		$status_string=$taxamatch->this_status_string;
		$cleaned_txt=$taxamatch->this_cleaned_txt;
		$input=$taxamatch_result["input"];
		$match_part=0;
		if ($infra2_parsed) {
			$match_part=4;
		} elseif ($infra1_parsed) {
			$match_part=3;
		} elseif ($species_parsed) {
			$match_part=2;
		} elseif ($genus_parsed) {
			$match_part=1;
		}
		if ($family_parsed) {
			$match_part++;
		}

		$gni_parser_result=null;
		$detail=null;
		$positions=null;
		$lowest_matched=null;

		if (isset($taxamatch_result["gni_parser_result"])) {
			$gni_parser_result=$taxamatch_result["gni_parser_result"];
			if ($gni_parser_result->scientificName->parsed) {
				$detail=get_object_vars($gni_parser_result->scientificName->details[0]);
				$positions=get_object_vars($gni_parser_result->scientificName->positions);
			}
		}

		$scientificname=$input;

		$matched=array();
		$canonical_author=array();
		$matched_id=array();
		$name_source_url=array();
		$accepted_name=array();
		$accepted_author=array();
		$accepted_name_id=array();
		$accepted_family=array();
		$acceptance=array();
		$phonetic_id=array();

		$family_matched=array();

		if(isset($taxamatch_result["family"]) && count($taxamatch_result["family"])) {
			if (isset($taxamatch_result["family"]["exact"])) {
				$family_matched=array_merge($family_matched, $taxamatch_result["family"]["exact"]);
				foreach ($taxamatch_result["family"]["exact"] as $ph) {
					$phonetic_id[$ph["family_id"]]=1;
				}
			}
			if (isset($taxamatch_result["family"]["phonetic"])) {
				$family_matched=array_merge($family_matched, $taxamatch_result["family"]["phonetic"]);
				foreach ($taxamatch_result["family"]["phonetic"] as $ph) {
					$phonetic_id[$ph["family_id"]]=1;
				}
			}
			if (isset($taxamatch_result["family"]["near_1"])) {
				$family_matched=array_merge($family_matched, $taxamatch_result["family"]["near_1"]);
			}
			if (isset($taxamatch_result["family"]["near_2"])) {
				$family_matched=array_merge($family_matched, $taxamatch_result["family"]["near_2"]);
			}
		}
		if (isset($taxamatch_result["infra2"]) && count($taxamatch_result["infra2"])) {
			$lowest_matched='infra2';
			if (isset($taxamatch_result["infra2"]["exact"])) {
				$matched=array_merge($matched, $taxamatch_result["infra2"]["exact"]);
				foreach ($taxamatch_result["infra2"]["exact"] as $ph) {
					$phonetic_id[$ph["infra2_id"]]=1;
				}
			}
			if (isset($taxamatch_result["infra2"]["phonetic"])) {
				$matched=array_merge($matched, $taxamatch_result["infra2"]["phonetic"]);
				foreach ($taxamatch_result["infra2"]["phonetic"] as $ph) {
					$phonetic_id[$ph["infra2_id"]]=1;
				}
			}
			if (isset($taxamatch_result["infra2"]["near_1"])) {
				$matched=array_merge($matched, $taxamatch_result["infra2"]["near_1"]);
			}
			if (isset($taxamatch_result["infra2"]["near_2"])) {
				$matched=array_merge($matched, $taxamatch_result["infra2"]["near_2"]);
			}
			foreach ($matched as &$match) {
				$match["Canonical_author"]=$match["temp_authority"];
				$temp=explode(" ", $match["genus_species_infra2"]);
				$match["Genus_matched"]=$temp[0];
				$match["SpecificEpithet_matched"]=$temp[1];
				$match["infraspecific1Rank"]=$temp[2];
				$match["infraspecific1Epithet"]=$temp[3];
				$match["infraspecific2Rank"]=$temp[4];
				$match["infraspecific2Epithet"]=$temp[5];

				$match["Genus_matched_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $genus_parsed);
				$match["SpecificEpithet_matched_score"]=self::getEDScore($match["species_ed"], $match["SpecificEpithet_matched"], $species_parsed);
				$match["infraspecific1Score"]=self::getEDScore($match["infra1_ed"], $match["infraspecific1Epithet"], $infra1_parsed);
				$match["infraspecific2Score"]=self::getEDScore($match["infra2_ed"], $match["infraspecific2Epithet"], $infra2_parsed);
				if ($match["infraspecific1Rank"] != $rank1_parsed) {
					$match["infraspecific1Score"]-=0.1;
				}
				if ($match["infraspecific2Rank"] != $rank2_parsed) {
					$match["infraspecific2Score"]-=0.1;
				}
				$match["Lowest_scientificName_matched_ID"]=$match["infra2_id"];
				$match["Lowest_scientificName_matched"]=$match["genus_species_infra1_infra2"];
				if ($authority_parsed) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_matched_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]=null;
					$match["Author_matched_score"]=null;
				}
				if ($match_part<4) {
					$match_part=4;
				}
				$match_score=$match["SpecificEpithet_matched_score"]+$match["Genus_matched_score"]+$match["infraspecific1Score"]+$match["infraspecific2Score"];
				if ($family_parsed) {
					$match['Family_submitted']=$family_parsed;
					if (count($family_matched)) {
						$family_matched_filtered=$this->filterFamily($match["Genus_matched"],$family_matched);
						if (count($family_matched_filtered)) {
							$family_matched=$family_matched_filtered;
						}
						$match["Family_matched"]=$family_matched[0]["family"];
						$match["Family_matched_score"]=self::getEDScore($family_matched[0]["family_ed"], $match["Family_matched"], $family_parsed);
						$match_score+=$match["Family_matched_score"];
					}
					if ($match_part<5) {
						$match_part=5;
					}
				}
				$match["Lowest_sciName_matched_score"]=$match_score/$match_part;
				$match["Overall_match_score"]=self::getOverallScore($match["Lowest_sciName_matched_score"], $match["Author_matched_score"]);
				$match["Phonetic"]=array_key_exists($match["infra2_id"], $phonetic_id) ? 'Y' : '';
				$matched_id[]=$match["Lowest_scientificName_matched_ID"];
			}
		} elseif (isset($taxamatch_result["infra1"]) && count($taxamatch_result["infra1"])) {
			$lowest_matched='infra1';
			if (isset($taxamatch_result["infra1"]["exact"])) {
				$matched=array_merge($matched, $taxamatch_result["infra1"]["exact"]);
				foreach ($taxamatch_result["infra1"]["exact"] as $ph) {
					$phonetic_id[$ph["infra1_id"]]=1;
				}
			}
			if (isset($taxamatch_result["infra1"]["phonetic"])) {
				$matched=array_merge($matched, $taxamatch_result["infra1"]["phonetic"]);
				foreach ($taxamatch_result["infra1"]["phonetic"] as $ph) {
					$phonetic_id[$ph["infra1_id"]]=1;
				}
			}
			if (isset($taxamatch_result["infra1"]["near_1"])) {
				$matched=array_merge($matched, $taxamatch_result["infra1"]["near_1"]);
			}
			if (isset($taxamatch_result["infra1"]["near_2"])) {
				$matched=array_merge($matched, $taxamatch_result["infra1"]["near_2"]);
			}
			foreach ($matched as &$match) {
				$match["Canonical_author"]=$match["temp_authority"];
				$temp=explode(" ", $match["genus_species_infra1"]);
				$match["Genus_matched"]=$temp[0];
				$match["SpecificEpithet_matched"]=$temp[1];
				$match["infraspecific1Rank"]=$temp[2];
				$match["infraspecific1Epithet"]=$temp[3];

				$match["Genus_matched_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $genus_parsed);
				$match["SpecificEpithet_matched_score"]=self::getEDScore($match["species_ed"], $match["SpecificEpithet_matched"], $species_parsed);
				$match["infraspecific1Score"]=self::getEDScore($match["infra1_ed"], $match["infraspecific1Epithet"], $infra1_parsed);
				if ($match["infraspecific1Rank"] != $rank1_parsed) {
					$match["infraspecific1Score"]-=0.1;
				}
				$match["Lowest_scientificName_matched_ID"]=$match["infra1_id"];
				$match["Lowest_scientificName_matched"]=$match["genus_species_infra1"];
				if ($authority_parsed) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_matched_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]=null;
					$match["Author_matched_score"]=null;
				}
				if ($match_part<3) {
					$match_part=3;
				}
				$match_score=$match["SpecificEpithet_matched_score"]+$match["Genus_matched_score"]+$match["infraspecific1Score"];
				if ($family_parsed) {
					$match['Family_submitted']=$family_parsed;
					if (count($family_matched)) {
						$family_matched_filtered=$this->filterFamily($match["Genus_matched"],$family_matched);
						if (count($family_matched_filtered)) {
							$family_matched=$family_matched_filtered;
						}
						$match["Family_matched"]=$family_matched[0]["family"];
						$match["Family_matched_score"]=self::getEDScore($family_matched[0]["family_ed"], $match["Family_matched"], $family_parsed);
						$match_score+=$match["Family_matched_score"];
					}
					if ($match_part<4) {
						$match_part=4;
					}
				}
				$match["Lowest_sciName_matched_score"]=$match_score/$match_part;
				$match["Overall_match_score"]=self::getOverallScore($match["Lowest_sciName_matched_score"], $match["Author_matched_score"]);
				$match["Phonetic"]=array_key_exists($match["infra1_id"], $phonetic_id) ? 'Y' : '';
				$matched_id[]=$match["Lowest_scientificName_matched_ID"];
			}
		} elseif (isset($taxamatch_result["species"]) && count($taxamatch_result["species"])) {
			$lowest_matched='species';
			if (isset($taxamatch_result["species"]["exact"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["exact"]);
				foreach ($taxamatch_result["species"]["exact"] as $ph) {
					$phonetic_id[$ph["species_id"]]=1;
				}
			}
			if (isset($taxamatch_result["species"]["phonetic"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["phonetic"]);
				foreach ($taxamatch_result["species"]["phonetic"] as $ph) {
					$phonetic_id[$ph["species_id"]]=1;
				}
			}
			if (isset($taxamatch_result["species"]["near_1"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["near_1"]);
			}
			if (isset($taxamatch_result["species"]["near_2"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["near_2"]);
			}
			if (isset($taxamatch_result["species"]["near_3"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["near_3"]);
			}
			if (isset($taxamatch_result["species"]["near_4"])) {
				$matched=array_merge($matched, $taxamatch_result["species"]["near_4"]);
			}
			foreach ($matched as &$match) {
				$match["Canonical_author"]=$match["temp_authority"];
				$temp=explode(" ", $match["genus_species"]);
				$match["Genus_matched"]=$temp[0];
				$match["SpecificEpithet_matched"]=$temp[1];
				$match["Genus_matched_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $genus_parsed);
				$match["SpecificEpithet_matched_score"]=self::getEDScore($match["species_ed"], $match["SpecificEpithet_matched"], $species_parsed);
				$match["Lowest_scientificName_matched_ID"]=$match["species_id"];
				$match["Lowest_scientificName_matched"]=$match["genus_species"];
				if ($authority_parsed) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_matched_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]=null;
					$match["Author_matched_score"]=null;
				}
				if ($match_part<2) {
					$match_part=2;
				}
				$match_score=$match["SpecificEpithet_matched_score"]+$match["Genus_matched_score"];
				if ($family_parsed) {
					$match['Family_submitted']=$family_parsed;
					if (count($family_matched)) {
						$family_matched_filtered=$this->filterFamily($match["Genus_matched"],$family_matched);
						if (count($family_matched_filtered)) {
							$family_matched=$family_matched_filtered;
						}
						$match["Family_matched"]=$family_matched[0]["family"];
						$match["Family_matched_score"]=self::getEDScore($family_matched[0]["family_ed"], $match["Family_matched"], $family_parsed);
						$match_score+=$match["Family_matched_score"];
					}
					if ($match_part<3) {
						$match_part=3;
					}
				}
				$match["Lowest_sciName_matched_score"]=$match_score/$match_part;
				$match["Overall_match_score"]=self::getOverallScore($match["Lowest_sciName_matched_score"], $match["Author_matched_score"]);
				$match["Phonetic"]=array_key_exists($match["species_id"], $phonetic_id) ? 'Y' : '';
				$matched_id[]=$match["Lowest_scientificName_matched_ID"];
			}
		} elseif(isset($taxamatch_result["genus"]) && count($taxamatch_result["genus"])) {
			$lowest_matched='genus';
			if (isset($taxamatch_result["genus"]["exact"])) {
				$matched=array_merge($matched, $taxamatch_result["genus"]["exact"]);
				foreach ($taxamatch_result["genus"]["exact"] as $ph) {
					$phonetic_id[$ph["genus_id"]]=1;
				}
			}
			if (isset($taxamatch_result["genus"]["phonetic"])) {
				$matched=array_merge($matched, $taxamatch_result["genus"]["phonetic"]);
				foreach ($taxamatch_result["genus"]["phonetic"] as $ph) {
					$phonetic_id[$ph["genus_id"]]=1;
				}
			}
			if (isset($taxamatch_result["genus"]["near_1"])) {
				$matched=array_merge($matched, $taxamatch_result["genus"]["near_1"]);
			}
			if (isset($taxamatch_result["genus"]["near_2"])) {
				$matched=array_merge($matched, $taxamatch_result["genus"]["near_2"]);
			}
			foreach ($matched as &$match) {
				$match["Canonical_author"]=$match["temp_authority"];
				$match["Genus_matched"]=$match["genus"];
				$match["SpecificEpithet_matched"]=null;
				$match["Genus_matched_score"]=self::getEDScore($match["genus_ed"], $match["Genus_matched"], $genus_parsed);
				$match["SpecificEpithet_matched_score"]=null;
				$match["Lowest_scientificName_matched_ID"]=$match["genus_id"];
				$match["Lowest_scientificName_matched"]=$match["Genus_matched"];
				if ($authority_parsed) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_matched_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]=null;
					$match["Author_matched_score"]=null;
				}
				if ($match_part<1) {
					$match_part=1;
				}
				$match_score=$match["Genus_matched_score"];
				if ($family_parsed) {
					$match['Family_submitted']=$family_parsed;
					if (count($family_matched)) {
						$family_matched_filtered=$this->filterFamily($match["Genus_matched"],$family_matched);
						if (count($family_matched_filtered)) {
							$family_matched=$family_matched_filtered;
						}
						$match["Family_matched"]=$family_matched[0]["family"];
						$match["Family_matched_score"]=self::getEDScore($family_matched[0]["family_ed"], $match["Family_matched"], $family_parsed);
						$match_score+=$match["Family_matched_score"];
					}
					if ($match_part<2) {
						$match_part=2;
					}
				}
				$match["Lowest_sciName_matched_score"]=$match_score/$match_part;
				$match["Overall_match_score"]=self::getOverallScore($match["Lowest_sciName_matched_score"], $match["Author_matched_score"]);
				$match["Phonetic"]=array_key_exists($match["genus_id"], $phonetic_id) ? 'Y' : '';
				$matched_id[]=$match["Lowest_scientificName_matched_ID"];
			}
		} elseif(isset($taxamatch_result["family"]) && count($taxamatch_result["family"])) {
			$lowest_matched='family';
			$matched=array_merge($matched, $family_matched);
			foreach ($matched as &$match) {
				$match["Canonical_author"]=$match["temp_authority"];
				$match["Family_matched"]=$match["family"];
				$match["SpecificEpithet_matched"]=null;
				$match["Genus_matched"]=null;
				$match["Family_matched_score"]=self::getEDScore($match["family_ed"], $match["Family_matched"], $family_parsed);
				$match["Genus_matched_score"]=null;
				$match["SpecificEpithet_matched_score"]=null;
				$match["Lowest_scientificName_matched_ID"]=$match["family_id"];
				$match["Lowest_scientificName_matched"]=$match["Family_matched"];
				if ($authority_parsed) {
					$match["Author_matched"]=$match["temp_authority"];
					$match["Author_matched_score"]=$match["auth_similarity"];
				} else {
					$match["Author_matched"]=null;
					$match["Author_matched_score"]=null;
				}
				if ($match_part<1) {
					$match_part=1;
				}
				$match_score=$match["Family_matched_score"];
				$match["Lowest_sciName_matched_score"]=$match_score/$match_part;
				$match["Overall_match_score"]=self::getOverallScore($match["Lowest_sciName_matched_score"], $match["Author_matched_score"]);
				$match["Phonetic"]=array_key_exists($match["family_id"], $phonetic_id) ? 'Y' : '';
				$matched_id[]=$match["Lowest_scientificName_matched_ID"];
			}
		} else {
			$matched[]=array("Accepted_family" => "");
		}
		if (count($matched_id)) {
			$meta_res=$this->db->getNameMetaData($matched_id);
			foreach ($meta_res as $meta) {
				$acceptance[$meta->nameID]=$meta->acceptance;
				$accepted_family[$meta->nameID]=$meta->accepted_family;
				if ($meta->accepted_name_id) {
					$accepted_name_id[$meta->nameID]=$meta->accepted_name_id;
				}
			}
			if (count(array_values($accepted_name_id))) {
				$name_res=$this->db->getScientificName(array_values($accepted_name_id));
				foreach ($name_res as $nm) {
					$accepted_name[$nm->nameID]=$nm->scientific_name;
					$accepted_author[$nm->nameID]=$nm->author;
				}
			}
			$name_source_url_id=array_merge($matched_id, array_values($accepted_name_id));
			$name_source_url_res=$this->db->getNameSourceUrl($name_source_url_id);
			foreach ($name_source_url_res as $nsu) {
				$name_source_url[$nsu->nameID]=$nsu->name_source_url;
			}
		}
		$status=$status_string;
		$unmatched='';

		if ($status) {
			if (preg_match("/vel\.? sp\.? aff\.?/", $status)) {
				$status="vel. sp. aff.";
			} elseif (preg_match("/\-?aff\.?/", $status)) {
				$status="aff.";
			} elseif (preg_match("/\-?cf\.?/", $status) || $status == '?') {
				$status="cf.";
			}
		}

		if (isset($detail)) {
			$unmatched=$cleaned_txt;
			if (isset($lowest_matched)) {
				$taxa_rank=array('uninomial', 'genus', 'species', 'infraspecies');
				$rank_level=0;
				if ($lowest_matched == 'infra1' || $lowest_matched == 'infra2'){
					$rank_level=4;
				} elseif ($lowest_matched == 'species') {
					$rank_level=3;
				} elseif ($lowest_matched == 'genus') {
					$rank_level=2;
				}
				if ($rank_level) {
					$match_rank=array_combine(array_slice($taxa_rank,0,$rank_level), array_fill(0,$rank_level,1));
					while (list($start, $pos) = each($positions)) {
						if (array_key_exists($pos[0], $match_rank)) {
							$unmatched=mb_substr($unmatched, 0, $start) . str_repeat(' ', ($pos[1] - $start)) . mb_substr($unmatched, $pos[1]);
						}
					}
				}	
			}
			if (($lowest_matched == 'infra1' || $lowest_matched == 'infra2') && $rank1_parsed) {
				$unmatched=str_ireplace($rank1_parsed, '', $unmatched);
			}
			if ($lowest_matched == 'infra2' && $rank2_parsed) {
				$unmatched=str_ireplace($rank2_parsed, '', $unmatched);
			}
			$unmatched=trim(preg_replace("/ {2,}/", ' ', $unmatched));
			if ($authority_parsed && mb_strlen($authority_parsed) == mb_strlen($unmatched)) {
				$unmatched='';
			}
		} elseif (isset($gni_parser_result) || is_null($lowest_matched)){
			$unmatched=$cleaned_txt;
		}

		foreach ($matched as &$match) {
			$match["ScientificName_submitted"]=$scientificname;
			$match["Unmatched"]=$unmatched;
			if ($family_parsed && ! count($family_matched)) {
				$match["Unmatched"]=$family_string . $match["Unmatched"];
			} elseif ($family_unmatched) {
				//$match["Unmatched"]=$family_unmatched . " " . $match["Unmatched"];
			}
			if (isset($start_string)) {
				$match["Unmatched"]= $start_string . $match["Unmatched"];
			}
			$match["Status"]=$status;
			if(isset($match["Lowest_scientificName_matched_ID"])) {
				$match["Accepted_family"]=$accepted_family[$match["Lowest_scientificName_matched_ID"]];
				$match["NameSourceUrl"]=$name_source_url[$match["Lowest_scientificName_matched_ID"]];
				$match["Acceptance"]=$acceptance[$match["Lowest_scientificName_matched_ID"]];
				if (isset($accepted_name_id[$match["Lowest_scientificName_matched_ID"]])) {
					$match["Accepted_name_ID"]=$accepted_name_id[$match["Lowest_scientificName_matched_ID"]];
					$match["Accepted_name"]=$accepted_name[$match["Accepted_name_ID"]];
					$match["Accepted_author"]=$accepted_author[$match["Accepted_name_ID"]];
					$match["Accepted_name_SourceUrl"]=$name_source_url[$match["Accepted_name_ID"]];
				}
			}
			$result=array();
			foreach (self::$field as $fd) {
				if (array_key_exists($fd, $match)) {
					$result[$fd]=$match[$fd];
				} else {
					$result[$fd]=null;
				}
			}
			$this->data[]=$result;
		}
		if (count($this->data) > 1) {
			usort($this->data, array("TnrsAggregator", "cmpMatched"));
		}
	}

	public function getData () {
		return $this->data;
	}

	public function filterFamily($genus, $family) {
		$family_res=$this->db->getFamilyByGenus($genus);
		$family_id=array();
		foreach ($family_res as $fr) {
			$family_id[$fr->family_id]=1;
		}
		$family_filtered=array();
		foreach ($family as $fam) {
			if (array_key_exists($fam["family_id"], $family_id)) {
				$family_filtered[]=$fam;
			}
		}
		return $family_filtered;
	}

	static function cmpMatched ($a, $b) {
		$as=$a['Overall_match_score'];
		$bs=$b['Overall_match_score'];
		$at=$a['Lowest_sciName_matched_score'];
		$bt=$b['Lowest_sciName_matched_score'];
		$ap=$a['Phonetic'];
		$bp=$b['Phonetic'];
		$aa=$a['Acceptance'];
		$ba=$b['Acceptance'];
		$phonetic_array=array("Y" => 1, "" => 0);
		$acceptance_array=array("A" => 2, "S" => 1, "" => 0);
		if ($at != $bt) {
			return ($at > $bt) ? -1 : +1;
		}
		if ($ap != $bp) {
			return ($phonetic_array[$ap] > $phonetic_array[$bp]) ? -1 : +1; 
		}
		if ($as != $bs) {
			return ($as > $bs) ? -1 : +1;
		}
		if ($aa != $ba) {
			return ($acceptance_array[$aa] > $acceptance_array[$ba]) ? -1 : +1;
		}
		return 0;
	}

	static function getEDScore($ed, $str1='', $str2='') {
		$score=0;
		if (isset($ed)) {
			$l1=mb_strlen($str1);
			$l2=mb_strlen($str2);
			if ($l1 > 0 || $l2 > 0) {
				$score=(1-$ed/($l1 > $l2 ? $l1 : $l2));
			}
		}
		return $score;
	}

	static function getOverallScore($edscore, $authorscore) {
		$score=0;
		if (isset($edscore)) {
			$score=isset($authorscore) ? ($edscore * 3 + $authorscore)/4 : $edscore;
		}
		return $score;
	}
}
?>
