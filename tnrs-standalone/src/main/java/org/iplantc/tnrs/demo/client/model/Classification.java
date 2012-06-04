package org.iplantc.tnrs.demo.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;



public class Classification extends BaseTreeModel{

	public Classification(){

	}

	public Classification(String name){
		set("name",name);
	}

	public void addChild(Classification child){
		add(child);
	}

	public String getName(){
		return get("name");
	}

	public String toString(){
		return get("name");
	}
	
	public List<Classification> getCChildren(){
		List<Classification> children = new ArrayList<Classification>();
		List<ModelData> children_m = getChildren();
		
		if(children.size()>0) {
			for(ModelData k: children_m) {
				children.add((Classification)k);
			}
		}
		
		return children;
	}
}