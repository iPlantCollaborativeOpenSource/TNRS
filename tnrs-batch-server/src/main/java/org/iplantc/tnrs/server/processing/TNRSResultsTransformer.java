package org.iplantc.tnrs.server.processing;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.iplantc.tnrs.server.BatchProcessingServer;
import org.iplantc.tnrs.server.TnrsJob;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TNRSResultsTransformer {

	static Logger log = Logger.getLogger(TNRSResultsTransformer.class);

	public JSONArray transform(JSONObject taxamtach_results,TnrsJob job, Vector<String> ids,Vector<String> original_names) {



		try {
			TnrsNameGroupConsolidator consolidator = new TnrsNameGroupConsolidator();
			int batch = 0;
			synchronized (job) {
				batch = job.getCurrentBatch()-1;
			}

			double sensitivity =job.sensitivity();
			JSONArray data = taxamtach_results.getJSONArray("data");
			JSONArray outputArray = new JSONArray();
			String cur_id="";
			String cur_name="";
			long group = (batch)*100;
			for(int i=0; i < data.size(); i++) {

				JSONArray cur_array = data.getJSONArray(i);
				int groupSize=0;

				if(job.containsId()){
					cur_id = ids.elementAt(i);

				}
				cur_name = original_names.elementAt(i);
				TreeSet<String> sources = new TreeSet<String>();


				
				
				cur_array = consolidator.consolidate(cur_array, job);

				JSONArray grouping_array= new JSONArray();

				for(int k=0; k < cur_array.size();k++) {
					JSONObject cur_result = cur_array.getJSONObject(k);
					String score = cur_result.getString("Overall_score");
					if(score.trim().equals("")) {
						score="0.0";
					}

					double cur_overall = roundTwoDecimals(Double.parseDouble(score.trim()));
					int warnings = cur_result.getInt("Warnings");


					if(cur_overall >= sensitivity) {
						groupSize++;
					}

					if(!job.isAllowPartial() && ( cur_result.getInt("Warnings")&1)==1) {

						if(groupSize>0) {
							groupSize--;
						}
					}

					sources.add(cur_result.getString("Source").toLowerCase());
				}




				for(int k=0; k < cur_array.size();k++) {
					JSONObject cur_result = cur_array.getJSONObject(k);
					JSONObject item = new JSONObject();

					String score = cur_result.getString("Overall_score");
					if(score.trim().equals("")) {
						score="0.0";
					}

					if(groupSize==0) {
						item = emptyItem(cur_result,(int) group, cur_name);
						TnrsNameGroupConsolidator.prepareName(item);
						if(job.containsId()){
							item.put("user_id", cur_id);
						}

						grouping_array.add(item);

						break;

					}


					if(!job.isAllowPartial() && ( cur_result.getInt("Warnings")&1)==1) {
						continue;
					}

					double cur_overall = roundTwoDecimals(Double.parseDouble(score));
					if(cur_overall >= sensitivity ) {


						cur_result.put("Nsources", sources.size());
						cur_result.put("group", group);
						cur_result.remove("Name_submitted");
						cur_result.put("Name_submitted", cur_name);

						if(k==0) {
							cur_result.put("selected", new Boolean(true));
						}else {
							cur_result.put("selected", new Boolean(false));
						}

						if(job.isTaxonomic() && cur_result.getInt("Highertaxa_score_order")==1 && cur_result.getInt("Highertaxa_score_order")<cur_result.getInt("Overall_score_order") )
						{
							int warning = cur_result.getInt("Warnings");
							cur_result.remove("Warnings");
							cur_result.put("Warnings", warning+4);
						}

						cur_result.put("groupSize",groupSize);


						if(job.containsId()){
							cur_result.put("user_id", cur_id);
						}
						grouping_array.add(cur_result);
					}
				}


				append_order_fields(grouping_array,job);
				fix_order_fields(grouping_array,job);
				fix_selected(grouping_array);
				outputArray.addAll(grouping_array);

				group++;

			}

			return outputArray;

		}catch(Exception ex) {
			log.error(ExceptionUtils.getFullStackTrace(ex));
			ex.printStackTrace();
			return null;
		}




	}

	@SuppressWarnings("unchecked")
	private void fix_selected(JSONArray array) {
		
		JSONArray samples = new JSONArray();

		samples.addAll(array);

		NameOrderComparator comparator = new NameOrderComparator("Source_constrain_off_order");

		Collections.sort(samples,comparator);


		for(int i=0; i < samples.size();i++) {
			JSONObject obj= samples.getJSONObject(i);
			obj.remove("selected");
			if(i==0) {
				obj.put("selected", true);
				
			}else {
				obj.put("selected", false);
			}
			
		}

		array.clear();
		array.addAll(samples);
		
	}
	
	private void setSelectedOnScore(JSONArray array){

		Vector<JSONObject> data = new Vector<JSONObject>();

		for(int i=0; i < array.size();i++){


			JSONObject json = array.getJSONObject(i);

			if(json.getInt("Overall_score_order")==1){
				data.insertElementAt(json, 0);
			}else{
				data.add(json);
			}
		}


		array.clear();

		array.addAll(data);


	}
	
	
	private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d));
	}

	@SuppressWarnings("unchecked")
	public void fix_order_fields(JSONArray array, TnrsJob job) {

		JSONArray samples = new JSONArray();

		samples.addAll(array);

		NameOrderComparator comparator = new NameOrderComparator("Overall_score_order");

		Collections.sort(samples,comparator);


		for(int i=0; i < samples.size();i++) {
			JSONObject object = samples.getJSONObject(i);
			object.remove("Overall_score_order");
			object.put("Overall_score_order", i+1);
		}


		comparator = new NameOrderComparator("Highertaxa_score_order");

		Collections.sort(samples,comparator);

		for(int i=0; i < samples.size();i++) {
			JSONObject object = samples.getJSONObject(i);
			object.remove("Highertaxa_score_order");
			object.put("Highertaxa_score_order", i+1);
		}


		array.clear();
		array.addAll(samples);


	}

	@SuppressWarnings("unchecked")
	private void append_order_fields(JSONArray array,TnrsJob job) {


		JSONArray samples = new JSONArray();

		samples.addAll(array);

		Collections.sort(samples, new NameSourceComparator(job,false));

		for(int i=0; i < samples.size();i++) {
			samples.getJSONObject(i).put("Source_constrain_off_order", i+1);
		}

		Collections.sort(samples, new NameSourceComparator(job,true));

		for(int i=0; i < samples.size();i++) {
			samples.getJSONObject(i).put("Source_constrain_on_order", i+1);
		}


		array.clear();
		array.addAll(samples);
	}


	private JSONObject emptyItem(JSONObject cur_result,int group, String cur_name) {
		JSONObject item = new JSONObject();
		item.put("group", group);
		item.put("Accepted_name_author","");
		item.put("Name_submitted", cur_name);
		item.put("Name_matched_id","");
		item.put("Name_matched_url", "");
		item.put("Name_matched", "No suitable matches found.");
		item.put("Name_score","0.0");
		item.put("Family_matched", "");
		item.put("Family_score","0.0");
		item.put("Canonical_author", "");
		item.put("Family_submitted", "");
		item.put("Genus_matched","");
		item.put("Genus_score","0.0");
		item.put("Specific_epithet_submitted", "");
		item.put("Specific_epithet_matched", "");
		item.put("Specific_epithet_score","0.0");
		item.put("Infraspecific_rank", "");
		item.put("Infraspecific_epithet_matched",  "");
		item.put("Infraspecific_epithet_score","0.0");
		item.put("Infraspecific_rank_2", "");
		item.put("Infraspecific_epithet_2_matched",  "");
		item.put("Infraspecific_epithet_2_score","0.0");
		item.put("Author_matched","");
		item.put("Author_score","0.0");
		item.put("Annotations", "");
		item.put("Unmatched_terms", "");
		item.put("Overall_score","0.0");
		item.put("Accepted_name","");
		item.put("Warnings","0");
		item.put("Taxonomic_status","" );
		item.put("Family_submitted", cur_result.optString("family", "") );
		item.put("selected", true);
		item.put("groupSize",1);
		item.put("Accepted_name_url","");
		item.put("Accepted_species", "");
		item.put("Name_matched_rank", "");
		item.put("Name_matched_accepted_family","");
		item.put("Genus_submitted", "");
		item.put("Phonetic", "");
		item.put("Accepted_name_id", "");
		item.put("Accepted_name_rank", "");
		item.put("Accepted_family", "");
		item.put("Overall_score_order", 1);
		item.put("Highertaxa_score_order", 1);
		item.put("Source_constrain_off_order", 1);
		item.put("Source_constrain_on_order",1);
		item.accumulate("Source", "");
		item.put("Nsources", "0");
		return item;
	}

}
