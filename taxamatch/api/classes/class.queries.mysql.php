<?php

/**
 * Taxamatch-Webservice PHP v1.0.0
 * @author Michael Giddens
 * @link http://www.silverbiology.com
 */

	/**
	 * Class Queries
	 * Contains functions to interact with the database
	 */
	class Queries {
			
		/**
		 * Constructor
		 * @param mixed $db : database connection object
		 * @param string $source : to select the project and database
		 */
		public function __construct( $db=null, $source ) {
			$this->db = $db;
			$this->source = $source;
			$this->post_fix = '_' . $source;
		}

		/**
		 * User in Normalize Auth to find full name from abrrivated author
		 * @param string $this_word : Abbrevated Author
		 */
		public function get_auth_full( $this_word ) {
		
			$value = null;			
			$query = sprintf("SELECT auth_full FROM auth_abbrev%s WHERE auth_abbr = '%s' and auth_full != '-';", mysql_escape_string($this->post_fix), $this_word);
			$this->debug['get_auth_full'][] = "1 $query";
			$value = $this->db->query_one( $query );
			$this->debug['get_auth_full'][] = "2 (auht_full:$value->auth_full)";
			
			return( $value->auth_full );
			
		}
		
		/**
		 * Select details from the genus and species list
		 * @param string $search_mode : normal / rapid / no_shaping
		 * @param mixed $this_near_match_genus
		 * @param mixed $this_near_match_species
		 * @param integer $this_genus_length
		 * @param integer $this_genus_start
		 * @param integer $this_genus_end
		 * @return mixed
		 */
		public function genus_cur($search_mode, $this_near_match_genus, $this_near_match_species, $this_genus_length,$this_genus_start,$this_genus_end) {


/*			$query = sprintf("SELECT distinct A.genus_id, A.genus, A.near_match_genus, A.search_genus_name FROM genlist%s A ",mysql_escape_string($this->post_fix));

			if(!is_null($this_near_match_species) && $this_near_match_species!='') {
				$query .= sprintf(", splist%s B ",mysql_escape_string($this->post_fix));
			}

			$query .= sprintf(" WHERE A.near_match_genus = '%s'",mysql_escape_string($this_near_match_genus));

			$this->debug['genus_cur'][] = "1 (search_mode:$search_mode)";
			if ( is_null($search_mode) || ( !is_null($search_mode) && $search_mode != 'rapid')) {
				$this->debug['genus_cur'][] = "1a";
			
				$query .= sprintf(" OR ( 
					A.gen_length between %s AND %s AND 
			
					(least(%s, A.gen_length) <4 and (A.search_genus_name like '%s' 
					OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) = 4 and (A.search_genus_name like '%s' 
						OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) = 5 and (A.search_genus_name like '%s' 
						OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) >5 and (A.search_genus_name like '%s%%' 
							OR A.search_genus_name like '%%%s')) ) "
					, mysql_escape_string($this_genus_length - 2)
					, mysql_escape_string($this_genus_length + 2)
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,1).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-1))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,2).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-2))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,2).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-3))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string($this_genus_start)
					, mysql_escape_string($this_genus_end)
				);
			}

			$this->debug['genus_cur'][] = "2 (this_near_match_species:$this_near_match_species)";
			if(!is_null($this_near_match_species) && $this_near_match_species!='') {
				$this->debug['genus_cur'][] = "2a";
				$query .= sprintf(" OR ( A.gen_length between %s AND %s AND B.near_match_species = '%s' AND A.genus_id = B.genus_id) "
					, mysql_escape_string($this_genus_length - 3)
					, mysql_escape_string($this_genus_length + 3)
					, mysql_escape_string($this_near_match_species)
				);
			}
			$query .= " GROUP BY A.genus_id, A.genus, A.near_match_genus, A.search_genus_name ORDER BY A.genus";*/
// print $query;
// *************

			$base_query = sprintf("SELECT distinct A.genus_id, A.genus, A.near_match_genus, A.search_genus_name FROM genlist%s A ",mysql_escape_string($this->post_fix));

			$tail_query = " GROUP BY A.genus_id, A.genus, A.near_match_genus, A.search_genus_name ";
			# This ORDER may not be needed and would reduce the use of using filesort in mysql.
			$tail_query .= " ORDER BY A.genus";

			$query = $base_query . sprintf(" WHERE A.near_match_genus = '%s'",mysql_escape_string($this_near_match_genus)) . $tail_query;

			$value = $this->db->query_all( $query );

			$value = (!is_null($value)) ? $value : array();

			if ( is_null($search_mode) || ( !is_null($search_mode) && $search_mode != 'rapid')) {
				$query = $base_query . 
					sprintf(" WHERE 
					(A.gen_length between %s AND %s) AND (
			
					(least(%s, A.gen_length) <4 and (A.search_genus_name like '%s' 
					OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) = 4 and (A.search_genus_name like '%s' 
						OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) = 5 and (A.search_genus_name like '%s' 
						OR A.search_genus_name like '%s'))
			
					OR (least(%s, A.gen_length) >5 and (A.search_genus_name like '%s%%' 
							OR A.search_genus_name like '%%%s'))) "
					, mysql_escape_string($this_genus_length - 2)
					, mysql_escape_string($this_genus_length + 2)
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,1).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-1))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,2).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-2))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string(substr($this_genus_start,0,2).'%')
					, mysql_escape_string('%'.substr($this_genus_end,-3))
					, mysql_escape_string($this_genus_length)
					, mysql_escape_string($this_genus_start)
					, mysql_escape_string($this_genus_end)
				) . $tail_query;

			$value1 = $this->db->query_all( $query );
			$value = ( !is_null($value1) ) ? array_merge($value,$value1) : $value;
			}

			// This is used to get genera that have a species phonetic match and wider length buffer on the genus
			// Might consider putting a flag to run this or not
			if(!is_null($this_near_match_species) && $this_near_match_species!='') {
				$query = $base_query . 
					sprintf(", splist%s B ",mysql_escape_string($this->post_fix)) . 
 					sprintf(" WHERE ( A.gen_length between %s AND %s AND B.near_match_species = '%s' AND A.genus_id = B.genus_id) "
					, mysql_escape_string($this_genus_length - 3)
					, mysql_escape_string($this_genus_length + 3)
					, mysql_escape_string($this_near_match_species)
				) . $tail_query;

			$value1 = $this->db->query_all( $query );
			
			$value = ( !is_null($value1) ) ? array_merge($value,$value1) : $value;
			}

// 			var_dump($value);

// *******************

/*			$this->debug['genus_cur'][] = "3 $query";
			$value = $this->db->query_all( $query );
			$this->debug['genus_cur'][] = $value;*/
			
			return( $value );
		}

      /**
       * Select details from the genus and species list
       * @param string $search_mode : normal / rapid / no_shaping
       * @param mixed $this_near_match_genus
       * @param mixed $this_near_match_species
       * @param integer $this_genus_length
       * @param integer $this_genus_start
       * @param integer $this_genus_end
       * @return mixed
       */
      public function genus_cur2($search_mode, $this_near_match_genus, $this_near_match_species, $this_genus_length,$this_genus_start,$this_genus_end) {
         $base_query = sprintf("SELECT distinct genus_id, genus, near_match_genus, search_genus_name FROM genlist%s ",
				mysql_escape_string($this->post_fix));

         $tail_query = " GROUP BY genus_id, genus, near_match_genus, search_genus_name ";
         # This ORDER may not be needed and would reduce the use of using filesort in mysql.
         $tail_query .= " ORDER BY genus";

         $query = $base_query . sprintf(" WHERE near_match_genus = '%s'",mysql_escape_string($this_near_match_genus)) . $tail_query;

         $value = $this->db->query_all( $query );

         $value = (!is_null($value)) ? $value : array();

         if ( is_null($search_mode) || ( !is_null($search_mode) && $search_mode != 'rapid')) {
            $query = $base_query .
               sprintf(" WHERE 
               (gen_length between %s AND %s) AND (
         
               (least(%s, gen_length) <4 and (sgn_head1 = '%s' OR sgn_tail1 = '%s'))
         
               OR (least(%s, gen_length) = 4 and (sgn_head2 = '%s' OR sgn_tail2 = '%s'))
         
               OR (least(%s, gen_length) = 5 and (sgn_head2 = '%s' OR sgn_tail3 = '%s'))
         
               OR (least(%s, gen_length) >5 and (sgn_head3 = '%s' OR sgn_tail3 = '%s'))) "
               , mysql_escape_string($this_genus_length - 2)
               , mysql_escape_string($this_genus_length + 2)
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,1)) # pos 1, 1 char
               , mysql_escape_string(substr($this_genus_end,-1)) # pos last, 1 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,2)) # pos 1, 2 char
               , mysql_escape_string(substr($this_genus_end,-2)) # pos last, 2 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,2)) # pos 1, 2 char
               , mysql_escape_string(substr($this_genus_end,-3)) # pos last, 2 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string($this_genus_start)
               , mysql_escape_string($this_genus_end)
            ) . $tail_query;

            $value1 = $this->db->query_all( $query );
            $value = ( !is_null($value1) ) ? array_merge($value,$value1) : $value;
         }

         // This is used to get genera that have a species phonetic match and wider length buffer on the genus
         // Might consider putting a flag to run this or not
         if(!is_null($this_near_match_species) && $this_near_match_species!='') {
            // since we already normalized the splist_genlist_combined table, might as well use it
            $base_query = sprintf("SELECT distinct genus_id, genus, near_match_genus, search_genus_name FROM splist_genlist_combined%s ",
				   mysql_escape_string($this->post_fix));
            $query = $base_query .
               sprintf(" WHERE ( gen_length between %s AND %s AND near_match_species = '%s') "
               , mysql_escape_string($this_genus_length - 3)
               , mysql_escape_string($this_genus_length + 3)
               , mysql_escape_string($this_near_match_species)
            ) . $tail_query;

            $value1 = $this->db->query_all( $query );

            $value = ( !is_null($value1) ) ? array_merge($value,$value1) : $value;
         }

         return( $value );
      }



      /**
       * Select details from the genus and species list
       * @param string $search_mode : normal / rapid / no_shaping
       * @param mixed $this_near_match_genus
       * @param mixed $this_near_match_species
       * @param integer $this_genus_length
       * @param integer $this_genus_start
       * @param integer $this_genus_end
       * @return mixed
       */
      public function genus_cur3($search_mode, $this_near_match_genus, $this_near_match_species, $this_genus_length,$this_genus_start,$this_genus_end) {
         $base_query = sprintf("SELECT distinct genus_id, genus, near_match_genus, search_genus_name FROM genlist%s ",
				mysql_escape_string($this->post_fix));
		 $tail_query ="";

		 #$tail_query = " GROUP BY genus_id, genus, near_match_genus, search_genus_name ";
         # This ORDER may not be needed and would reduce the use of using filesort in mysql.
         #$tail_query .= " ORDER BY genus";

			$base_where = sprintf(" WHERE near_match_genus = '%s'",mysql_escape_string($this_near_match_genus));

         $other_where = "";

         if ( is_null($search_mode) || ( !is_null($search_mode) && $search_mode != 'rapid')) {
            $other_where = 
               sprintf(" OR 
               ((gen_length between %s AND %s) AND (
         
               (least(%s, gen_length) <4 and (sgn_head1 = '%s' OR sgn_tail1 = '%s'))
         
               OR (least(%s, gen_length) = 4 and (sgn_head2 = '%s' OR sgn_tail2 = '%s'))
         
               OR (least(%s, gen_length) = 5 and (sgn_head2 = '%s' OR sgn_tail3 = '%s'))
         
               OR (least(%s, gen_length) >5 and (sgn_head3 = '%s' OR sgn_tail3 = '%s')))) "
               , mysql_escape_string($this_genus_length - 2)
               , mysql_escape_string($this_genus_length + 2)
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,1)) # pos 1, 1 char
               , mysql_escape_string(substr($this_genus_end,-1)) # pos last, 1 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,2)) # pos 1, 2 char
               , mysql_escape_string(substr($this_genus_end,-2)) # pos last, 2 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string(substr($this_genus_start,0,2)) # pos 1, 2 char
               , mysql_escape_string(substr($this_genus_end,-3)) # pos last, 2 char
               , mysql_escape_string($this_genus_length)
               , mysql_escape_string($this_genus_start)
               , mysql_escape_string($this_genus_end)
            );
         }

			$query = $base_query . $base_where . $other_where . $tail_query;

         $value = $this->db->query_all( $query );

         $value = (!is_null($value)) ? $value : array();

         // This is used to get genera that have a species phonetic match and wider length buffer on the genus
         // Might consider putting a flag to run this or not
         if(!is_null($this_near_match_species) && $this_near_match_species!='') {
            // since we already normalized the splist_genlist_combined table, might as well use it
            $base_query = sprintf("SELECT distinct genus_id, genus, near_match_genus, search_genus_name FROM splist_genlist_combined%s ",
				   mysql_escape_string($this->post_fix));
            $query = $base_query .
               sprintf(" WHERE ( gen_length between %s AND %s AND near_match_species = '%s') "
               , mysql_escape_string($this_genus_length - 3)
               , mysql_escape_string($this_genus_length + 3)
               , mysql_escape_string($this_near_match_species)
            ) . $tail_query;

            $value1 = $this->db->query_all( $query );

            $value = ( !is_null($value1) ) ? array_merge($value,$value1) : $value;
         }

         return( $value );
      }


	  public function family_cur($search_mode, $this_near_match_family, $this_family_length, $this_family_start) {
         $base_query = sprintf("SELECT distinct family_id, family, near_match_family, search_family_name FROM famlist%s ",
				mysql_escape_string($this->post_fix));
		 $tail_query ="";

			$base_where = sprintf(" WHERE near_match_family = '%s'",mysql_escape_string($this_near_match_family));

         $other_where = "";

         if ( is_null($search_mode) || ( !is_null($search_mode) && $search_mode != 'rapid')) {
            $other_where = 
               sprintf(" OR 
               ((fam_length between %s AND %s) AND (
				   (sgn_head1 = '%s'))) "
               , mysql_escape_string($this_family_length - 2)
               , mysql_escape_string($this_family_length + 2)
               , mysql_escape_string(substr($this_family_start,0,1)) # pos 1, 1 char
            );
         }

			$query = $base_query . $base_where . $other_where . $tail_query;

         $value = $this->db->query_all( $query );
         $value = (!is_null($value)) ? $value : array();

         // This is used to get genera that have a species phonetic match and wider length buffer on the genus
         // Might consider putting a flag to run this or not

         return( $value );
	  }


		/**
		 * species_cur
		 * @param string $gen_id
		 * @param integer $this_species_length
		 * @return mixed
		 */
		public function species_cur( $gen_id, $this_species_length ) {
			$query = sprintf("
				SELECT DISTINCT A.species_id, A.species, A.search_species_name, A.near_match_species, concat(B.near_match_genus,' ',A.near_match_species) AS near_match_gen_sp
					, concat(B.genus,' ',A.species) AS genus_species
				FROM splist%s A
				JOIN  genlist%s B ON B.genus_id = A.genus_id
				WHERE B.genus_id = '%s' 
					AND sp_length BETWEEN %s AND %s 
				GROUP BY A.species_id
					, A.species
					, A.search_species_name
					, A.near_match_species
					, concat(B.near_match_genus,' ',A.near_match_species)
					, concat(B.genus,' ',A.species)
				ORDER BY A.species;"
				, mysql_escape_string($this->post_fix)
				, mysql_escape_string($this->post_fix)
				, mysql_escape_string($gen_id)
				, mysql_escape_string($this_species_length - 4)
				, mysql_escape_string($this_species_length + 4));

			$this->debug['species_cur'][] = "1 (query:$query)";
			$value = $this->db->query_all( $query );
			$this->debug['species_cur'][] = $value;
			
			return( $value );
		}

		//EJS - species_cur_in takes an array of gen_ids and batch queries the list
                public function species_cur_in( $gen_ids, $this_species_length ) {
                        $query = sprintf("
                                SELECT DISTINCT A.species_id, A.species, A.search_species_name, A.near_match_species, concat(B.near_match_genus,' ',A.near_match_species) AS near_match_gen_sp
                                        , concat(B.genus,' ',A.species) AS genus_species, A.genus_id
                                FROM splist%s A
                                JOIN  genlist%s B ON B.genus_id = A.genus_id
                                WHERE B.genus_id IN (%s)
                                        AND sp_length BETWEEN %s AND %s 
                                GROUP BY A.species_id
                                        , A.species
                                        , A.search_species_name
                                        , A.near_match_species
                                        , concat(B.near_match_genus,' ',A.near_match_species)
                                        , concat(B.genus,' ',A.species)
                                ORDER BY A.species;"
                                , mysql_escape_string($this->post_fix)
                                , mysql_escape_string($this->post_fix)
#                                , mysql_escape_string($gen_id)
                                , implode (",", $gen_ids)
                                , mysql_escape_string($this_species_length - 4)
                                , mysql_escape_string($this_species_length + 4));

                        $this->debug['species_cur'][] = "1 (query:$query)";
                        $value = $this->db->query_all( $query );
                        $this->debug['species_cur'][] = $value;

                        return( $value );
                }

                //EJS - species_cur_in takes an array of gen_ids and batch queries the list
                public function species_cur_in2( $gen_ids, $this_species_length ) {
                        $query = sprintf("
                                SELECT DISTINCT species_id, species, search_species_name, near_match_species, near_match_gen_sp,
					genus_species, genus_id
                                FROM splist_genlist_combined%s
                                WHERE genus_id IN (%s)
                                        AND sp_length BETWEEN %s AND %s" 
                                , mysql_escape_string($this->post_fix)
                                , implode (",", $gen_ids)
                                , mysql_escape_string($this_species_length - 4)
                                , mysql_escape_string($this_species_length + 4));

                        $this->debug['species_cur'][] = "1 (query:$query)";
                        $value = $this->db->query_all( $query );
                        $this->debug['species_cur'][] = $value;

                        return( $value );
                }
                public function infra1_cur_in( $sp_ids, $this_infra1_length ) {
                        $query = sprintf("
                                SELECT DISTINCT infra1_id, infra1, search_infra1_name, near_match_infra1, near_match_sp_infra1,
					species_infra1, species_id
                                FROM infra1list_splist_combined%s
                                WHERE species_id IN (%s)
                                        AND infra1_length BETWEEN %s AND %s" 
                                , mysql_escape_string($this->post_fix)
                                , implode (",", $sp_ids)
                                , mysql_escape_string($this_infra1_length - 4)
                                , mysql_escape_string($this_infra1_length + 4));

                        $value = $this->db->query_all( $query );
                        return( $value );
                }
                public function infra2_cur_in( $infra1_ids, $this_infra2_length ) {
                        $query = sprintf("
                                SELECT DISTINCT infra2_id, infra2, search_infra2_name, near_match_infra2, near_match_infra1_infra2,
					infra1_infra2, infra1_id
                                FROM infra2list_infra1list_combined%s
                                WHERE infra1_id IN (%s)
                                        AND infra2_length BETWEEN %s AND %s" 
                                , mysql_escape_string($this->post_fix)
                                , implode (",", $infra1_ids)
                                , mysql_escape_string($this_infra2_length - 4)
                                , mysql_escape_string($this_infra2_length + 4));
                        $value = $this->db->query_all( $query );
                        return( $value );
                }

		/**
		 * genus_result_cur
		 * Selects from the genus matches temporary table
		 * @param integer|string $this_ed :  0 = exact, 'P' = phonetic, 1 = near_1, 2 = near_2 ...
		 * @return mixed
		 */
		public function genus_result_cur($this_ed = null) {
			$query = sprintf('SELECT DISTINCT genus_id, genus, genus_ed, phonetic_flag from genus_id_matches%s WHERE 1=1 ',mysql_escape_string($this->post_fix));
			if ($this_ed === 0) {
				$query .= " AND genus_ed = 0 ";
			} elseif ($this_ed == 'P') {
				$query .= " AND genus_ed > 0 AND phonetic_flag = 'Y' ";
			} elseif ($this_ed != 'P' && $this_ed > 0) {
				$query .= sprintf(" AND ( phonetic_flag IS NULL OR phonetic_flag = '' ) AND genus_ed = %s ", mysql_escape_string($this_ed) );
			}
			$query .= " GROUP BY genus_id, genus, genus_ed, phonetic_flag ";
			$query .= " ORDER BY genus_ed, genus ";

			$this->debug['genus_result_cur'][] = "1 (query:$query)";
			$value = $this->db->query_all( $query );
			$this->debug['genus_result_cur'][] = $value;
			
			return( $value );
		}

		/**
		 * species_result_cur
		 * Selects from the species matches temporary table
		 * @param integer|string $this_ed :  0 = exact, 'P' = phonetic, 1 = near_1, 2 = near_2 ...
		 * @return mixed
		 */
		public function species_result_cur($this_ed = null) {
			
			$query = sprintf("SELECT DISTINCT species_id, genus_species, genus_ed, species_ed, gen_sp_ed, phonetic_flag FROM species_id_matches%s WHERE 1=1 ",mysql_escape_string($this->post_fix));
			if ($this_ed === 0) {
				#$query .= " AND gen_sp_ed = 0 ";
				$query .= " AND species_ed = 0 ";
			} elseif ($this_ed == 'P') {
				#$query .= " AND gen_sp_ed > 0 AND phonetic_flag = 'Y' ";
				$query .= " AND species_ed > 0 AND phonetic_flag = 'Y' ";
			} elseif ($this_ed != 'P' && $this_ed > 0) {
#TODO
# This is causing some problems when you want edit distance and flag is set.
# Not sure if this need to be in here
#				$query .= sprintf(" AND ( phonetic_flag IS NULL OR phonetic_flag = '' )
		    #$query .= sprintf(" AND gen_sp_ed = %s ",mysql_escape_string($this_ed));
		    $query .= sprintf(" AND species_ed = %s ",mysql_escape_string($this_ed));
			}
			$query .= " GROUP BY species_id, genus_species, genus_ed, species_ed, gen_sp_ed, phonetic_flag ";
			$query .= " ORDER BY species_ed, genus_ed, genus_species ";

			$this->debug['species_result_cur'][] = "1 (query:$query)";
			$value = $this->db->query_all( $query );
			$this->debug['species_result_cur'][] = $value;
			return( $value );
		}

		/**
		 * getAuthority
		 * Get Authority from the genus or species list tables
		 * @param string $type : 'genus' | 'species'
		 * @param string $id : genus or species id
		 * @return string : returns the authority
		 */
		//public function getAuthority($type = 'genus', $id) {
		//	$table=null;
		//	$type_id=null;
		//	if ($type=='family') {
		//		$table = sprintf('famlist%s',mysql_escape_string($this->post_fix));
		//		$type_id = 'family_id';
		//	} elseif ($type=='genus') {
		//		$table = sprintf('genlist%s',mysql_escape_string($this->post_fix));
		//		$type_id = 'genus_id';
		//	} else {
		//		$table = sprintf('splist%s',mysql_escape_string($this->post_fix));
		//		$type_id = 'species_id';
		//	}
		//	$query = sprintf("SELECT authority FROM $table WHERE $type_id = %s", mysql_escape_string($id));
		//	$this->debug['getAuthority'][] = "1 (query:$query)";
		//	$res = $this->db->query_one($query);
		//	$this->debug['getAuthority'][] = $res;
		//	
		//	return( $res );
		//}
		public function getAuthority2($id) {
			$query = sprintf("SELECT scientificNameAuthorship AS authority FROM name WHERE nameID = %s", mysql_escape_string($id));
			$res = $this->db->query_one($query);
			return( $res );
		}


		/**
		 * saveGenusMatches
		 * Saves the genus search matches to the genus matches temporary table
		 * @param string $genus_id
		 * @param string $genus
		 * @param integer $genus_ed
		 * @param string $phonetic_flag : 'Y' | 'N'
		 * @return boolean
		 */
		public function saveGenusMatches($genus_id,$genus,$genus_ed,$phonetic_flag) {
			$query = sprintf("INSERT INTO genus_id_matches%s (genus_id, genus, genus_ed, phonetic_flag) VALUES ('%s', '%s', %s, '%s')"
				,	mysql_escape_string($this->post_fix)
				,	mysql_escape_string($genus_id)
				,	mysql_escape_string($genus)
				,	mysql_escape_string($genus_ed)
				,	mysql_escape_string($phonetic_flag)
			);
				
			$this->debug['saveGenusMatches'][] = "1 (query:$query)";
			$ret = $this->db->query($query);
			$this->debug['saveGenusMatches'][] = $ret;

			return( $ret );
		}

		/**
		 * saveSpeciesMatches
		 * Saves the species search matches to the species matches temporary table
		 * @param string $species_id
		 * @param string $genus_species
		 * @param integer $genus_ed
		 * @param integer $species_ed
		 * @param integer $gen_sp_ed
		 * @param string $phonetic_flag : 'Y' | 'N'
		 * @return boolean
		 */
		public function saveSpeciesMatches($species_id,$genus_species,$genus_ed,$species_ed,$gen_sp_ed,$phonetic_flag) {
			$query = sprintf("INSERT INTO species_id_matches%s (species_id, genus_species, genus_ed, species_ed, gen_sp_ed, phonetic_flag) values ('%s', '%s', %s, %s, %s, '%s')"
				, mysql_escape_string($this->post_fix)
				, mysql_escape_string($species_id)
				, mysql_escape_string($genus_species)
				, mysql_escape_string($genus_ed)
				, mysql_escape_string($species_ed)
				, mysql_escape_string($gen_sp_ed)
				, mysql_escape_string($phonetic_flag)
			);
			
			$this->debug['saveSpeciesMatches'][] = "1 (query:$query)";
			$ret = $this->db->query($query);
			$this->debug['saveSpeciesMatches'][] = $ret;

			return( $ret );
		}

		/**
		 * Count Species Matches
		 * @param integer $gen_sp_ed
		 * @return integer
		 */
		public function countSpeciesMatches($gen_sp_ed) {
			$query = sprintf("SELECT count(*) ct FROM species_id_matches%s WHERE phonetic_flag = '' AND gen_sp_ed = %s", mysql_escape_string($this->post_fix), mysql_escape_string($gen_sp_ed));
			$this->debug['countSpeciesMatches'][] = "1 (query:$query)";
			$res = $this->db->query_one($query);
			$this->debug['countSpeciesMatches'][] = $res;
			return( $ret->ct );
		}

		/**
		 * Clear Temporary Tables
		 * @return bool
		 */
		public function clearTempTables() {
			$query = sprintf(" DELETE FROM `genus_id_matches%s` ",mysql_escape_string($this->post_fix));
			$this->db->query($query);
			$query = sprintf(" DELETE FROM `species_id_matches%s` ",mysql_escape_string($this->post_fix));
			$this->db->query($query);
			return true;
		}

		public function getFamilyName ($name_id) {
			foreach ($name_id as &$nid) {
				$nid=mysql_escape_string($nid);
			}
			$query=sprintf("SELECT nameID, defaultFamily AS family_name FROM name WHERE nameID IN ( %s )", implode(",", $name_id));
			$res=$this->db->query_all($query);
			return $res;
		}

		public function getNameSourceUrl ($name_id) {
			foreach ($name_id as &$nid) {
				$nid=mysql_escape_string($nid);
			}
			$query=sprintf("SELECT ns.nameID, ns.nameSourceUrl AS name_source_url 
				FROM source s INNER JOIN name_source ns
				ON s.sourceID=ns.sourceID
				WHERE s.sourceName='Tropicos' AND ns.nameID IN ( %s )", implode(",", $name_id)
			);
			$res=$this->db->query_all($query);
			return $res;
		}

		//public function getAcceptance ($name_id) {
		//	foreach ($name_id as &$nid) {
		//		$nid=mysql_escape_string($nid);
		//	}
		//	$query=sprintf("SELECT sy.nameID, sy.acceptedNameID, sy.acceptance
		//		FROM synonym sy INNER JOIN source so
		//		ON so.sourceID=sy.sourceID
		//		WHERE so.sourceName='Tropicos' AND sy.nameID IN ( %s ) ORDER BY sy.acceptance", implode(",", $name_id)
		//	);
		//	$res=$this->db->query_all($query);
		//	return $res;
		//}

		public function checkScientificName ($str) {
			$query=sprintf("SELECT scientificName FROM name WHERE scientificName='%s'",
				mysql_escape_string($str)
			);
			$res=$this->db->query_value($query, "string");
			return $res;
		}
		public function checkScientificNameWithAuthor ($str) {
			$query=sprintf("SELECT scientificNameWithAuthor FROM nameParsed WHERE scientificNameWithAuthor='%s'",
				mysql_escape_string($str)
			);
			$res=$this->db->query_value($query, "string");
			return $res;
		}

		public function searchScientificName($str) {
			$query=sprintf("SELECT nameID, scientificName, scientificNameAuthorship, scientificNameWithAuthor, genus, specificEpithet, nameRank FROM name WHERE scientificName='%s' or scientificNameWithAuthor='%s'", 
				mysql_escape_string($str), mysql_escape_string($str));
			$res=$this->db->query_all($query);
			return $res;
		}

		public function searchFamilyName($str) {
			$query=sprintf("SELECT nameID, scientificName as family from name where nameRank='family' and scientificName='%s'", 
				mysql_escape_string($str));
			$res=$this->db->query_all($query);
			return $res;
		}

		//public function getCanonicalFamilyName($name_id) {
		//	foreach ($name_id as &$nid) {
		//		$nid=mysql_escape_string($nid);
		//	}
		//	$query=sprintf("select n.nameID, n2.scientificName as family_name from name n inner join synonym s join name n2 on n.nameID=s.nameID and s.acceptedNameID=n2.nameID where n.nameRank='family' and n.nameID IN ( %s )", implode(",", $name_id));
		//	$res=$this->db->query_all($query);
		//	return $res;
		//}

		public function getFamilyByGenus($genus) {
			$query = sprintf("SELECT family_id FROM genlist_famlist_combined%s WHERE genus='%s'", mysql_escape_string($this->post_fix), mysql_escape_string($genus));
			$res=$this->db->query_all($query);
			if (is_null($res)) {
				$res=array();
			}
			return $res;
		}	
		public function getNameMetaData($name_id) {
			foreach ($name_id as &$nid) {
				$nid=mysql_escape_string($nid);
			}
			$query=sprintf("SELECT nameID, defaultFamily AS accepted_family, defaultAcceptance AS acceptance, defaultAcceptedNameID AS accepted_name_id from name WHERE nameID IN ( %s )", implode(",", $name_id)
			);
			$res=$this->db->query_all($query);
			if (is_null($res)) {
				$res=array();
			}
			return $res;
		}
		public function getScientificName($name_id) {
			foreach ($name_id as &$nid) {
				$nid=mysql_escape_string($nid);
			}
			$query=sprintf("SELECT nameID, scientificName AS scientific_name, scientificNameAuthorship AS author from name WHERE nameID IN ( %s )", implode(",", $name_id)
			);
			$res=$this->db->query_all($query);
			if (is_null($res)) {
				$res=array();
			}
			return $res;
		}
	}
?>
