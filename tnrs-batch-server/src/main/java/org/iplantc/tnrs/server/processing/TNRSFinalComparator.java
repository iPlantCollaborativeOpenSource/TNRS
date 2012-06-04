package org.iplantc.tnrs.server.processing;

import java.util.Comparator;

import net.sf.json.JSONObject;

public class TNRSFinalComparator implements Comparator<JSONObject>{

	private boolean order_by_source;
	private boolean constrain_by_taxonomy;
	private String column;
	
	public TNRSFinalComparator(boolean source_ordering,boolean constrain) {
		order_by_source=source_ordering;
		constrain_by_taxonomy = constrain;

		
		if(order_by_source && constrain_by_taxonomy) {
			column="Source_constrain_on_order";
		}else if(order_by_source && !constrain_by_taxonomy) {
			column="Source_constrain_off_order";
		}else if(!order_by_source && constrain_by_taxonomy) {
			column="Highertaxa_score_order";
		}else if(!order_by_source && !constrain_by_taxonomy) {
			column="Overall_score_order";
		}
	}
	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		
		int val1 = o1.getInt(column);
		int val2 = o2.getInt(column);
		
		if(val1<val2) return -1;
		else if(val1>val2) return 1;
		
		return 0;
	}
	
}
