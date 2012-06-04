/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author raygoza
 *
 */
public class JsTNRSParsingEntry extends JavaScriptObject{

	protected JsTNRSParsingEntry() {
		
	}
	
	
	public final native String submittedName()/*-{
	return this.submittedName; 
	}-*/;
	
	public final native String canonicalName()/*-{
		return this.cannonicalName; 
	}-*/;
	
	
	public final native String author()/*-{
	    return this.author;
	}-*/;
	
	public final native String family()/*-{
		return this.family;
	}-*/;
	
	public final native String genus()/*-{
	return this.genus;
	}-*/;
	
	public final native String species()/*-{
		return this.species;
	}-*/;
	
	public final native String infraspecificEpithet1()/*-{
	 return this.infrasSpecificEpithet1;
	}-*/;
	
	public final native String infraspecificEpithet2()/*-{
		return this.infrasSpecificEpithet2;
	}-*/;
	
	public final native String annotations()/*-{
	return this.annotations;
	}-*/;
	
	public final native String unmatched()/*-{
		return this.matched;
	}-*/;
	
	public final native String taxonName()/*-{
		return this.Taxon_name;
	}-*/;
	
	
}
