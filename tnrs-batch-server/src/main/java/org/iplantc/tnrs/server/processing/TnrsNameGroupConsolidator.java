package org.iplantc.tnrs.server.processing;

import java.util.HashMap;
import java.util.Vector;

import org.iplantc.tnrs.server.TnrsJob;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * This class is responsible of processing a group of names and
 * consolidates names that are considered to be the same name,
 * but come  from a different source.
 * 
 * @author Juan Antonio Raygoza Garay
 *
 */


public class TnrsNameGroupConsolidator {

	
	
	public JSONArray consolidate(JSONArray array,TnrsJob job){
		
		JSONArray new_array= new JSONArray();
		
		HashMap<String, JSONObject> names = new HashMap<String, JSONObject>();
		
		
		for(int i=0; i < array.size();i++){
			JSONObject name = array.getJSONObject(i);
			
			String name_id = name.getString("Name_matched_id");
			String acceptance = name.getString("Taxonomic_status");
			String accepted_name_id= name.getString("Accepted_name_id");
			
			if(!names.containsKey(name_id)){
				prepareName(name);
				names.put(name_id, name);
				continue;
			}else{
				JSONObject main_name_object = names.get(name_id);
				if(main_name_object.getString("Taxonomic_status").equals(acceptance) && main_name_object.getString("Accepted_name_id").equals(accepted_name_id)){
					main_name_object.accumulate("Source", name.get("Source"));
					main_name_object.accumulate("Name_matched_url", name.get("Name_matched_url"));
					main_name_object.accumulate("Accepted_name_url",name.getString("Accepted_name_url"));
					
				}else{
					prepareName(name);
					new_array.add(name);
				}
				
			}
			
		}
		
		
		new_array.addAll(names.values());
		
		
		return new_array;
	}
	
	
	public static void prepareName(JSONObject name) {
		
		JSONArray sources = new JSONArray();
		sources.add(name.getString("Source"));
		name.remove("Source");
		name.put("Source", sources);
		
		JSONArray urls = new JSONArray();
		urls.add(name.getString("Name_matched_url"));
		name.remove("Name_matched_url");
		name.put("Name_matched_url",urls);
		
		
		JSONArray accepted_urls = new JSONArray();
		accepted_urls.add(name.getString("Accepted_name_url"));
		name.remove("Accepted_name_url");
		name.put("Accepted_name_url",accepted_urls);
	}
	
	
	
	
	
}
