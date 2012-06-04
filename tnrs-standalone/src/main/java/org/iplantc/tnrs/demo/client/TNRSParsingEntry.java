/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * @author raygoza
 *
 */
public class TNRSParsingEntry extends BaseModel {

	
	private static final long serialVersionUID = -7401738381508092646L;
	
	
	public TNRSParsingEntry(JsTNRSParsingEntry entry) {
		
		set("submitted",entry.submittedName());
		set("canonical",entry.canonicalName());
		set("author",entry.author());
		set("family",entry.family());
		set("genus",entry.genus());
		set("species",entry.species());
		set("infraspecificEpithet1",entry.infraspecificEpithet1());
		set("infraspecificEpithet2",entry.infraspecificEpithet2());
		set("annotations",entry.annotations());
		set("unmatched",entry.unmatched());
		
	}
	
	
	public String getSubmittedName() {
		return get("submitted");
	}
	
	public String getCanonicalName() {
		return get("canonical");
	}

	public String getAuthor() {
		return get("author");
	}
	
	public String getFamily() {
		return get("family");
	}
	
	public String getGenus() {
		return get("genus");
	}
	
	public String getSpecies() {
		return get("species");
	}
	
	public String getInfraSpecificEpithet1() {
		return get("infraspecificEpithet1");
	}
	
	public String getInfraSpecificEpithet2() {
		return get("infraspecificEpithet2");
	}
	
	
	public String getAnnotations() {
		return get("annotations");
	}
	
	public String getUnmatched() {
		return get("unmatched");
	}
	
}
