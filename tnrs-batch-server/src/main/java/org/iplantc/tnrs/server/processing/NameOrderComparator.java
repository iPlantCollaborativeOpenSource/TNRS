package org.iplantc.tnrs.server.processing;

import java.util.Comparator;

import net.sf.json.JSONObject;

public class NameOrderComparator implements Comparator<JSONObject>{

	String orderField;
	
	public NameOrderComparator(String order_field) {
		orderField = order_field;
	}
	
	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		
		Integer order1 = o1.getInt(orderField);
		Integer order2 = o2.getInt(orderField); 
		
		return order1.compareTo(order2);
	}
	
	
}
