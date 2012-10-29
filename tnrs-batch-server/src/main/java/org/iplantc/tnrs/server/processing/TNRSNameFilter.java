package org.iplantc.tnrs.server.processing;

import java.util.Vector;

public class TNRSNameFilter {

	Vector<String> column_definition;
	
	
	public TNRSNameFilter(Vector<String> columns) {
		column_definition = columns;
	}
	
	
	public  boolean filter(String[] values, boolean order_by_source,boolean constrain_by_taxonomy) {
		String column="";
		
		if(values[column_definition.indexOf("sort_override")].equals("1") && values[column_definition.indexOf("selected")].equals("true")) {
			return true;
		}
		
		if(order_by_source && constrain_by_taxonomy) {
			column="source_constrain_on_order";
		}else if(order_by_source && !constrain_by_taxonomy) {
			column="source_constrain_off_order";
		}else if(!order_by_source && constrain_by_taxonomy) {
			column="highertaxa_score_order";
		}else if(!order_by_source && !constrain_by_taxonomy) {
			column="overall_score_order";
		}
		
		
		
		if(!column.equals("") && values[column_definition.indexOf(column)].equals("1")) return true;
				
		return false;
		
	}
	
	
	
}
