package org.iplantc.tnrs.demo.client;




import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.json.client.JSONObject;

/**
 * Model for a TNRS entry.
 * 
 * @author amuir
 * 
 */
public class TNRSEntry extends BaseModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7401738381508092645L;

	
	public TNRSEntry(JSONObject json) {
		
		
		set("selected",json.get("selected").toString());
		
		
	}
	
	public TNRSEntry(BeanModel entry) {

		if(entry != null)
		{
			//set("entryId", entry.getEntryId());
			set("group", Long.parseLong(entry.get("group").toString()));
			set("url", entry.get("url").toString());			
			set("submitted", entry.get("submitted").toString());
			set("scientific", entry.get("scientific").toString());
			set("scientificScore", entry.get("scientificScore").toString());
			set("authorAttributed", entry.get("authorAttributed").toString());
			set("family", entry.get("family").toString());
			set("genus", entry.get("genus").toString());
			set("genusScore", entry.get("genusScore").toString());
			set("epithet", entry.get("epithet").toString());
			set("epithetScore", entry.get("epithetScore").toString());
			set("author", entry.get("author").toString());
			set("authorScore", entry.get("authorScore").toString());
			set("annotation", entry.get("annotation").toString());
			set("unmatched", entry.get("unmatched").toString());
			set("overall", entry.get("overall").toString());
			set("selected", new Boolean(entry.get("selected").toString().trim()));
			System.out.println(entry.get("selected").toString().trim());
			set("matchedFamily",entry.get("matchedFamily").toString());
			set("matchedFamilyScore",entry.get("matchedFamilyScore").toString());
			set("speciesMatched",entry.get("speciesMatched").toString());
			set("speciesMatchedScore",entry.get("speciesMatchedScore").toString());
			set("infraspecific1Rank",entry.get("infraspecific1Rank").toString());
			set("infraspecific1Epithet",entry.get("infraspecific1Epithet").toString());
			set("infraspecific1EpithetScore",entry.get("infraspecific1EpithetScore").toString());
			set("infraspecific2Rank",entry.get("infraspecific2Rank").toString());
			set("infraspecific2Epithet",entry.get("infraspecific2Epithet").toString());
			set("infraspecific2EpithetScore",entry.get("infraspecific2EpithetScore").toString());
			set("acceptance",entry.get("acceptance").toString());
			set("submittedFamily", entry.get("submittedFamily").toString());
			set("acceptedName",entry.get("acceptedName").toString());
			set("acceptedNameUrl",entry.get("acceptedNameUrl").toString());
			set("acceptedAuthor", entry.get("acceptedAuthor").toString());
			set("groupSize",Integer.parseInt(entry.get("groupSize").toString()));
			set("flag",Integer.parseInt(entry.get("flag").toString()));
			set("acceptedSpecies",entry.get("acceptedSpecies").toString());
			set("nameMatchedRank",entry.get("nameMatchedRank").toString());
			set("Accepted_name_family",entry.get("acceptedNameFamily").toString());
			set("Source",entry.get("source").toString()); 
			set("source","");
		}
		
		
	}
	
	/**
	 * Default constructor.
	 * 
	 */
	public TNRSEntry(JsTNRSEntry entry)
	{
		if(entry != null)
		{
			set("entryId", entry.getEntryId());
			set("group", Long.parseLong(entry.getGroup()));
			set("url", entry.getURL());// entry.getURL());			
			set("submitted", entry.getSubmittedName().replace("\\\\\"", "\""));
			set("scientific", entry.getScientificName());
			set("scientificScore", entry.getScientificScore());
			set("authorAttributed", entry.getAttributedAuthor());
			set("family", entry.getFamily());
			set("genus", entry.getGenus());
			set("genusScore", entry.getGenusScore());
			set("epithet", entry.getEpithet());
			set("epithetScore", entry.getEpithetScore());
			set("author", entry.getAuthorMatched());
			set("authorScore", entry.getAuthorScore());
			set("annotation", entry.getAnnotation());
			set("unmatched", entry.getUnmatched().replace("\\\\\"", "\""));
			set("overall", entry.getOverall());
			set("selected", entry.isSelected());
			System.out.println(get("selected"));
			set("matchedFamily",entry.getFamilyMatched());
			set("matchedFamilyScore",entry.getFamilyMatchedScore());
			set("speciesMatched",entry.getSpeciesMatched());
			set("speciesMatchedScore",entry.getSpeciesMatchedScore());
			set("infraspecific1Rank",entry.getInfraSpecificRank1());
			set("infraspecific1Epithet",entry.getInfraSpecific1Epithet());
			set("infraspecific1EpithetScore",entry.getInfraSpecific1EpithetScore());
			set("infraspecific2Rank",entry.getInfraSpecificRank2());
			set("infraspecific2Epithet",entry.getInfraSpecific2Epithet());
			set("infraspecific2EpithetScore",entry.getInfraSpecific2EpithetScore());
			set("acceptance",entry.getAcceptance());
			set("submittedFamily", entry.getSubmittedFamily());
			set("acceptedName",entry.getAcceptedName());
			set("acceptedNameUrl",entry.getAcceptedNameUrls());
			set("acceptedAuthor", entry.getAcceptedAuthor());
			set("flag",entry.getFlag());
			set("acceptedSpecies",entry.getAcceptedSpecies());
			set("nameMatchedRank",entry.getNameMatchedRank());
			set("Accepted_name_family",entry.getAcceptedFamily());
			set("Source",entry.getSource());
			set("name_matched_id",entry.getNameMatchedId());
		}
	}

	/**
	 * Retrieve unique item id.
	 * 
	 * @return item.
	 */
	public String getEntryId()
	{
		return get("entryId");
	}
	
	/**
	 * Retrieve group.
	 * 
	 * @return group.
	 */
	public Long getGroup()
	{
		return get("group");
	}

	/**
	 * Retrieve URL for name lookup.
	 * 
	 * @return URL.
	 */
	public String getURL()
	{
		return get("url");
	}
	
	/**
	 * Retrieve submitted name.
	 * 
	 * @return submitted name.
	 */
	public String getSubmittedName()
	{
		return get("submitted");
	}

	/**
	 * Retrieve scientific name.
	 * 
	 * @return scientific name.
	 */
	public String getScientificName()
	{
		return get("scientific");
	}

	/**
	 * Retrieve scientific score.
	 * 
	 * @return scientific score.
	 */
	public String getScientificScore()
	{
		return get("scientificScore");
	}

	/**
	 * Retrieve attributed author.
	 * 
	 * @return attributed author.
	 */
	public String getAttributedAuthor()
	{
		return get("authorAttributed");
	}

	/**
	 * Retrieve family.
	 * 
	 * @return family.
	 */
	public String getFamily()
	{
		return get("family");
	}

	/**
	 * Retrieve genus.
	 * 
	 * @return genus.
	 */
	public String getGenus()
	{
		return get("genus");
	}

	/**
	 * Retrieve genus score.
	 * 
	 * @return genus score.
	 */
	public String getGenusScore()
	{
		return get("genusScore");
	}

	/**
	 * Retrieve epithet.
	 * 
	 * @return epithet.
	 */
	public String getEpithet()
	{
		return get("epithet");
	}

	/**
	 * Retrieve epithet score.
	 * 
	 * @return epithet score.
	 */
	public String getEpithetScore()
	{
		return get("epithetScore");
	}

	/**
	 * Retrieve author.
	 * 
	 * @return author.
	 */
	public String getAuthor()
	{
		return get("author");
	}

	/**
	 * Retrieve author score.
	 * 
	 * @return author score.
	 */
	public String getAuthorScore()
	{
		return get("authorScore");
	}

	/**
	 * Retrieve annotation.
	 * 
	 * @return annotation.
	 */
	public String getAnnotation()
	{
		return get("annotation");
	}

	/**
	 * Retrieve unmatched field.
	 * 
	 * @return unmatched field.
	 */
	public String getUnmatched()
	{
		return get("unmatched");
	}

	/**
	 * Retrieve overall score.
	 * 
	 * @return overall score.
	 */
	public String getOverall()
	{
		return get("overall");
	}

	/**
	 * Determines selected state.
	 * 
	 * @return true if selected.
	 */
	public boolean isSelected()
	{
		Object retrieved = get("selected");

		return (retrieved == null) ? false : ((Boolean)get("selected")).booleanValue();
	}

	/**
	 * Set entry as selected.
	 */
	public void setSelected()
	{
		set("selected", true);
	}

	/**
	 * Clear selected state.
	 */
	public void clearSelection()
	{
		set("selected", false);
	}
	
	/**
	 * Retrieve Family matched score
	 * 
	 * 
	 * @return Retrieve Family matched score
	 */
	
	public String getFamilyMatchedScore() {
		return get("matchedFamilyScore");
	}
	
	/**
	 * Retrieve Family matched 
	 * 
	 * 
	 * @return Retrieve Family matched
	 */
	
	public String getFamilyMatched() {
		return get("matchedFamily");
	}
	
	
	/**
	 * Retrieve Species Matched
	 * 
	 * @return Retrieve Species Matched
	 */

	
	public String getSpeciesMatched() {
		return get("speciesMatched");
	}
	
	/**
	 * Retrieve Species Matched Score
	 * 
	 * @return Retrieve Species Matched Score
	 */

	
	public String getSpeciesMatchedScore() {
		return get("speciesMatchedScore");
	}
	
	/**
	 * Retrieve Infraspecific Rank 1
	 * 
	 * @return Infraspecific Rank 1
	 * 
	 */
	
	public String getInfraSpecificRank1() {
		return get("infraspecific1Rank");
	}
	
	
	/**
	 * Retrieve Infra-Specific Epithet 1
	 * 
	 * 
	 * @return Infra-Specific Epithet 1
	 * 
	 */
	
	public String getInfraSpecificEpithet1() {
		return get("infraspecific1Epithet");
	}
	
	
	/**
	 * Retrieve Infra-Specific Epithet 1 Score
	 * 
	 * 
	 * @return Infra-Specific Epithet 1 Score
	 * 
	 */
	
	public String getInfraSpecificEpithet1Score() {
		return get("infraspecific1EpithetScore");
	}
	
	/**
	 * Retrieve Infraspecific Rank 2
	 * 
	 * @return Infraspecific Rank 2
	 * 
	 */
	
	public String getInfraSpecificRank2() {
		return get("infraspecific2Rank");
	}
	
	
	/**
	 * Retrieve Infra-Specific Epithet 2
	 * 
	 * 
	 * @return Infra-Specific Epithet 2
	 * 
	 */
	
	public String getInfraSpecificEpithet2() {
		return get("infraspecific2Epithet");
	}
	
	
	/**
	 * Retrieve Infra-Specific Epithet 2 Score
	 * 
	 * 
	 * @return Infra-Specific Epithet 2 Score
	 * 
	 */
	
	public String getInfraSpecificEpithet2Score() {
		return get("infraspecific2EpithetScore");
	}
	
	/**
	 * Retrieve the acceptance status of this match
	 * 
	 * @return acceptance
	 * 
	 * 
	 */
	 
	public String getAcceptance() {
		return get("acceptance");
	}
	
	
	/**
	 * Retrieve the accepted name of this match
	 * 
	 * @return accepted name
	 * 
	 * 
	 */
	 
	public String getAcceptedName() {
		return get("acceptedName");
	}
	 
	
	/**
	 * Retrieve the accepted name of this match
	 * 
	 * @return accepted name
	 * 
	 * 
	 */
	 
	public String getAcceptedNameUrl() {
		return get("acceptedNameUrl");
	}
	
	/**
	 * Retrieve the accepted author
	 * 
	 * 
	 * 
	 */
	
	public String getAcceptedAuthor() {
		return get("acceptedAuthor");
	}
	
	
	public void setSubmitted(String submitted)
	{
		set("submitted",submitted);
	}
	
	
	public String getNameMatchedRank() {
		return get("nameMatchedRank");
	}
	
	public String getAcceptedSpecies() {
		return get("acceptedSpecies");
	}
	
	public String getAcceptedFamily() {
		return get("Accepted_name_family");
	}
	
	public String getSources(){
		return get("Source");
	}
	
	public String getNameMatchedId(){
		return get("name_matched_id");
	}
	
}
