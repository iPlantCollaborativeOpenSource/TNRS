/**
 * 
 */
package org.iplantc.tnrs.demo.shared;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author raygoza
 *
 */
public class BeanTnrsParsingEntry implements BeanModelTag,IsSerializable,Serializable  {

	
	private String submittedName;
	private String cannonicalName;
	private String author;
	private String family;
	private String genus;
	private String species;
	private String infraSpecificEpithet1;
	private String infraSpecificEpithet1_rank;
	private String infraSpecificEpithet2;
	private String infraSpecificEpithet2_rank;
	private String annotations;
	private String unmatched;
	private String taxonName;
	
	
	public BeanTnrsParsingEntry(){
		
	}

	public String getSubmittedName() {
		return submittedName;
	}

	public void setSubmittedName(String submittedName) {
		this.submittedName = submittedName;
	}

	public String getCannonicalName() {
		return cannonicalName;
	}

	public void setCannonicalName(String cannonicalName) {
		this.cannonicalName = cannonicalName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getInfraSpecificEpithet1() {
		return infraSpecificEpithet1;
	}

	public void setInfraSpecificEpithet1(String infraSpecificEpithet1) {
		this.infraSpecificEpithet1 = infraSpecificEpithet1;
	}

	public String getInfraSpecificEpithet2() {
		return infraSpecificEpithet2;
	}

	public void setInfraSpecificEpithet2(String infraSpecificEpithet2) {
		this.infraSpecificEpithet2 = infraSpecificEpithet2;
	}

	public String getAnnotations() {
		return annotations;
	}

	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}

	public String getUnmatched() {
		return unmatched;
	}

	public void setUnmatched(String unmatched) {
		this.unmatched = unmatched;
	}

	public String getInfraSpecificEpithet1_rank() {
		return infraSpecificEpithet1_rank;
	}

	public void setInfraSpecificEpithet1_rank(String infraSpecificEpithet1_rank) {
		this.infraSpecificEpithet1_rank = infraSpecificEpithet1_rank;
	}

	public String getInfraSpecificEpithet2_rank() {
		return infraSpecificEpithet2_rank;
	}

	public void setInfraSpecificEpithet2_rank(String infraSpecificEpithet2_rank) {
		this.infraSpecificEpithet2_rank = infraSpecificEpithet2_rank;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}
	
	
	
	
	
}
