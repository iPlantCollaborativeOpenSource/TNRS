package org.iplantc.tnrs.server.processing;

import java.util.Comparator;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.iplantc.tnrs.server.TnrsJob;

public class NameSourceComparator implements Comparator<JSONObject>{

	private TnrsJob job;
	private String order_column;

	public NameSourceComparator(TnrsJob tnrs_job,boolean taxonomic) {
		job= tnrs_job;

		if(taxonomic) {
			order_column="Highertaxa_score_order";
		}else {
			order_column="Overall_score_order";
		}
		
	}


	@Override
	public int compare(JSONObject o1, JSONObject o2) {

		
		
		Vector<String> sources = job.getSources();
		JSONArray name1_sources = o1.getJSONArray("Source");
		JSONArray name2_sources = o2.getJSONArray("Source");
		int order1 = o1.getInt(order_column); 
		int order2 = o2.getInt(order_column);
		
		for(int i=0; i < sources.size();i++) {

			if(name1_sources.contains(sources.elementAt(i)) && !name2_sources.contains(sources.elementAt(i)) ) {
				return -1;
			}else if (!name1_sources.contains(sources.elementAt(i)) && name2_sources.contains(sources.elementAt(i))   ) {
				return 1;
			}else if(name1_sources.contains(sources.elementAt(i)) && name2_sources.contains(sources.elementAt(i))) {
				if(order1 < order2) {
					return -1;
				}else if(order1>order2){
					return 1;
				}else if(order1==order2) {
					return 0;
				}
			}




		}


		return 0;
	}


}
