/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * @author raygoza
 *
 */
public class SourceEntry extends BaseModel{

	
	public SourceEntry(){
		
	}
	
	public Boolean include() {
		return get("include");
	}
	
	public void setInclude(Boolean include) {
		set("include",include);
	}
	
	public String name() {
		return get("name");
	}
	
	public void setName(String name) {
		set("name",name);
	}
	
	public Integer rank() {
		return get("rank");
	}
	
	public void setRank(int rank) {
		set("rank",Integer.valueOf(rank));
	}
	
	public String getAccessedDate(){
		return get("accessed_date");
	}
	
	public void setAccessedDate(String date){
		set("accessed_date",date);
	}
	
	public void setWarning(int warning){
		set("warning",Integer.valueOf(warning));
	}
	
	public Integer warning(){
		return get("warning");
	}
	
}
