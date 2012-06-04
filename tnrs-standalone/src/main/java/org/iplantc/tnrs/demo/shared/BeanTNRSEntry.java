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
public class BeanTNRSEntry implements BeanModelTag,IsSerializable,Serializable{

	 static final long serialVersionUID = 100010011;
	
	 private long entryId;
	 private long group;
	 private String url;
	 private String submitted;
	 private String scientific;
	 private String scientificScore;
	 private String authorAttributed;
	 private String family;
	 private String genus;
	 private String genusScore;
	 private String epithet;
	 private String epithetScore;
	 private String author;
	 private String authorScore;
	 private String annotation;
	 private String unmatched;
	 private String overall;
	 private boolean selected;
	 private String matchedFamily;
	 private String matchedFamilyScore;
	 private String speciesMatched;
	 private String speciesMatchedScore;
	 private String infraspecific1Rank;
	 private String infraspecific1Epithet;
	 private String infraspecific1EpithetScore;
	 private String infraspecific2Rank;
	 private String infraspecific2Epithet;
	 private String infraspecific2EpithetScore;
	 private String acceptance;
	 private String submittedFamily;
	 private String acceptedName;
	 private String acceptedNameUrl;
	 private String acceptedAuthor;
	 private String acceptedNameFamily;
	 private String acceptedSpecies;
	 private String nameMatchedRank;
	 private int groupSize;
	 private int flag;
	 private String source;
	 private int number_sources;
	 
	 public BeanTNRSEntry() {
		 
	 }
	/**
	 * @return the entryId
	 */
	public long getEntryId() {
		return entryId;
	}
	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}
	/**
	 * @return the group
	 */
	public long getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(long group) {
		this.group = group;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the submitted
	 */
	public String getSubmitted() {
		return submitted;
	}
	/**
	 * @param submitted the submitted to set
	 */
	public void setSubmitted(String submitted) {
		this.submitted = submitted;
	}
	/**
	 * @return the scientific
	 */
	public String getScientific() {
		return scientific;
	}
	/**
	 * @param scientific the scientific to set
	 */
	public void setScientific(String scientific) {
		this.scientific = scientific;
	}
	/**
	 * @return the scientificScore
	 */
	public String getScientificScore() {
		return scientificScore;
	}
	/**
	 * @param scientificScore the scientificScore to set
	 */
	public void setScientificScore(String scientificScore) {
		this.scientificScore = scientificScore;
	}
	/**
	 * @return the authorAttributed
	 */
	public String getAuthorAttributed() {
		return authorAttributed;
	}
	/**
	 * @param authorAttributed the authorAttributed to set
	 */
	public void setAuthorAttributed(String authorAttributed) {
		this.authorAttributed = authorAttributed;
	}
	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @return the genus
	 */
	public String getGenus() {
		return genus;
	}
	/**
	 * @param genus the genus to set
	 */
	public void setGenus(String genus) {
		this.genus = genus;
	}
	/**
	 * @return the genusScore
	 */
	public String getGenusScore() {
		return genusScore;
	}
	/**
	 * @param genusScore the genusScore to set
	 */
	public void setGenusScore(String genusScore) {
		this.genusScore = genusScore;
	}
	/**
	 * @return the epithet
	 */
	public String getEpithet() {
		return epithet;
	}
	/**
	 * @param epithet the epithet to set
	 */
	public void setEpithet(String epithet) {
		this.epithet = epithet;
	}
	/**
	 * @return the epithetScore
	 */
	public String getEpithetScore() {
		return epithetScore;
	}
	/**
	 * @param epithetScore the epithetScore to set
	 */
	public void setEpithetScore(String epithetScore) {
		this.epithetScore = epithetScore;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @return the authorScore
	 */
	public String getAuthorScore() {
		return authorScore;
	}
	/**
	 * @param authorScore the authorScore to set
	 */
	public void setAuthorScore(String authorScore) {
		this.authorScore = authorScore;
	}
	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}
	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	/**
	 * @return the unmatched
	 */
	public String getUnmatched() {
		return unmatched;
	}
	/**
	 * @param unmatched the unmatched to set
	 */
	public void setUnmatched(String unmatched) {
		this.unmatched = unmatched;
	}
	/**
	 * @return the overall
	 */
	public String getOverall() {
		return overall;
	}
	/**
	 * @param overall the overall to set
	 */
	public void setOverall(String overall) {
		this.overall = overall;
	}
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * @return the matchedFamily
	 */
	public String getMatchedFamily() {
		return matchedFamily;
	}
	/**
	 * @param matchedFamily the matchedFamily to set
	 */
	public void setMatchedFamily(String matchedFamily) {
		this.matchedFamily = matchedFamily;
	}
	/**
	 * @return the matchedFamilyScore
	 */
	public String getMatchedFamilyScore() {
		return matchedFamilyScore;
	}
	/**
	 * @param matchedFamilyScore the matchedFamilyScore to set
	 */
	public void setMatchedFamilyScore(String matchedFamilyScore) {
		this.matchedFamilyScore = matchedFamilyScore;
	}
	/**
	 * @return the speciesMatched
	 */
	public String getSpeciesMatched() {
		return speciesMatched;
	}
	/**
	 * @param speciesMatched the speciesMatched to set
	 */
	public void setSpeciesMatched(String speciesMatched) {
		this.speciesMatched = speciesMatched;
	}
	/**
	 * @return the speciesMatchedScore
	 */
	public String getSpeciesMatchedScore() {
		return speciesMatchedScore;
	}
	/**
	 * @param speciesMatchedScore the speciesMatchedScore to set
	 */
	public void setSpeciesMatchedScore(String speciesMatchedScore) {
		this.speciesMatchedScore = speciesMatchedScore;
	}
	/**
	 * @return the infraspecific1Rank
	 */
	public String getInfraspecific1Rank() {
		return infraspecific1Rank;
	}
	/**
	 * @param infraspecific1Rank the infraspecific1Rank to set
	 */
	public void setInfraspecific1Rank(String infraspecific1Rank) {
		this.infraspecific1Rank = infraspecific1Rank;
	}
	/**
	 * @return the infraspecific1Epithet
	 */
	public String getInfraspecific1Epithet() {
		return infraspecific1Epithet;
	}
	/**
	 * @param infraspecific1Epithet the infraspecific1Epithet to set
	 */
	public void setInfraspecific1Epithet(String infraspecific1Epithet) {
		this.infraspecific1Epithet = infraspecific1Epithet;
	}
	/**
	 * @return the infraspecific1EpithetScore
	 */
	public String getInfraspecific1EpithetScore() {
		return infraspecific1EpithetScore;
	}
	/**
	 * @param infraspecific1EpithetScore the infraspecific1EpithetScore to set
	 */
	public void setInfraspecific1EpithetScore(String infraspecific1EpithetScore) {
		this.infraspecific1EpithetScore = infraspecific1EpithetScore;
	}
	/**
	 * @return the infraspecific2Rank
	 */
	public String getInfraspecific2Rank() {
		return infraspecific2Rank;
	}
	/**
	 * @param infraspecific2Rank the infraspecific2Rank to set
	 */
	public void setInfraspecific2Rank(String infraspecific2Rank) {
		this.infraspecific2Rank = infraspecific2Rank;
	}
	/**
	 * @return the infraspecific2Epithet
	 */
	public String getInfraspecific2Epithet() {
		return infraspecific2Epithet;
	}
	/**
	 * @param infraspecific2Epithet the infraspecific2Epithet to set
	 */
	public void setInfraspecific2Epithet(String infraspecific2Epithet) {
		this.infraspecific2Epithet = infraspecific2Epithet;
	}
	/**
	 * @return the infraspecific2EpithetScore
	 */
	public String getInfraspecific2EpithetScore() {
		return infraspecific2EpithetScore;
	}
	/**
	 * @param infraspecific2EpithetScore the infraspecific2EpithetScore to set
	 */
	public void setInfraspecific2EpithetScore(String infraspecific2EpithetScore) {
		this.infraspecific2EpithetScore = infraspecific2EpithetScore;
	}
	/**
	 * @return the acceptance
	 */
	public String getAcceptance() {
		return acceptance;
	}
	/**
	 * @param acceptance the acceptance to set
	 */
	public void setAcceptance(String acceptance) {
		this.acceptance = acceptance;
	}
	/**
	 * @return the submittedFamily
	 */
	public String getSubmittedFamily() {
		return submittedFamily;
	}
	/**
	 * @param submittedFamily the submittedFamily to set
	 */
	public void setSubmittedFamily(String submittedFamily) {
		this.submittedFamily = submittedFamily;
	}
	/**
	 * @return the acceptedName
	 */
	public String getAcceptedName() {
		return acceptedName;
	}
	/**
	 * @param acceptedName the acceptedName to set
	 */
	public void setAcceptedName(String acceptedName) {
		this.acceptedName = acceptedName;
	}
	/**
	 * @return the acceptedNameUrl
	 */
	public String getAcceptedNameUrl() {
		return acceptedNameUrl;
	}
	/**
	 * @param acceptedNameUrl the acceptedNameUrl to set
	 */
	public void setAcceptedNameUrl(String acceptedNameUrl) {
		this.acceptedNameUrl = acceptedNameUrl;
	}
	/**
	 * @return the acceptedAuthor
	 */
	public String getAcceptedAuthor() {
		return acceptedAuthor;
	}
	/**
	 * @param acceptedAuthor the acceptedAuthor to set
	 */
	public void setAcceptedAuthor(String acceptedAuthor) {
		this.acceptedAuthor = acceptedAuthor;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the groupSize
	 */
	public int getGroupSize() {
		return groupSize;
	}
	/**
	 * @param groupSize the groupSize to set
	 */
	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}
     
     
	public void setFlag(int flag) {
		this.flag = flag;
	}
	 
     
    public int getFlag() {
    	return flag;
    }
    
    public void setAcceptedSpecies(String accepted_species) {
    	this.acceptedSpecies = accepted_species;
    }
	
    
    public String getAcceptedSpecies() {
    	return acceptedSpecies;
    }
	
    
    public void setNameMatchedRank(String name_matched_rank) {
    	this.nameMatchedRank = name_matched_rank;
    }
    
    public String getNameMatchedRank() {
    	return nameMatchedRank;
    }
	public String getAcceptedNameFamily() {
		return acceptedNameFamily;
	}
	public void setAcceptedNameFamily(String acceptedNameFamily) {
		this.acceptedNameFamily = acceptedNameFamily;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getNumber_sources() {
		return number_sources;
	}
	public void setNumber_sources(int number_sources) {
		this.number_sources = number_sources;
	}
    
	
	
    
    
    
}
