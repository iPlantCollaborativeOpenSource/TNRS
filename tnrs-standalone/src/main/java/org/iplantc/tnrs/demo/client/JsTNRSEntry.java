package org.iplantc.tnrs.demo.client;


import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript representation of a TNRS entry.
 * 
 * @author amuir
 * 
 */
public class JsTNRSEntry extends JavaScriptObject
{
	/**
	 * Default constructor.
	 */
	protected JsTNRSEntry()
	{
		
	}
	
	/**
	 * Retrieve unique item id.
	 * 
	 * @return id.
	 */
	public final native String getEntryId() /*-{
		return this.entryId;
	}-*/;
	
	/**
	 * Retrieve group.
	 * 
	 * @return group.
	 */
	public final native String getGroup() /*-{
		return this.group;
	}-*/;
	
	/**
	 * Retrieve URL for name lookup.
	 * 
	 * @return url.
	 */
	public final native String getURL() /*-{
		return this.url;
	}-*/;

	/**
	 * Retrieve submitted name.
	 * 
	 * @return submitted name.
	 */
	public final native String getSubmittedName() /*-{
		return this.nameSubmitted;
	}-*/;

	/**
	 * Retrieve scientific name.
	 * 
	 * @return scientific name.
	 */
	public final native String getScientificName() /*-{
		return this.nameScientific;
	}-*/;

	/**
	 * Retrieve scientific score.
	 * 
	 * @return scientific score.
	 */
	public final native String getScientificScore() /*-{
		return this.scientificScore;
	}-*/;

	/**
	 * Retrieve attributed author.
	 * 
	 * @return attributed author.
	 */
	public final native String getAttributedAuthor() /*-{
		return this.authorAttributed;
	}-*/;
	
	/**
	 * Retrieve family.
	 * 
	 * @return family.
	 */
	public final native String getFamily() /*-{
		return this.family;
	}-*/;
	
	/**
	 * Retrieve genus.
	 * 
	 * @return genus.
	 */
	public final native String getGenus() /*-{
		return this.genus;
	}-*/;

	/**
	 * Retrieve genus score.
	 * 
	 * @return genus score.
	 */
	public final native String getGenusScore() /*-{
		return this.genusScore;
	}-*/;
	
	/**
	 * Retrieve epithet.
	 * 
	 * @return epithet.
	 */
	public final native String getEpithet() /*-{
		return this.epithet;
	}-*/;
	
	/**
	 * Retrieve epithet score.
	 * 
	 * @return epithet score.
	 */
	public final native String getEpithetScore() /*-{
		return this.epithetScore;
	}-*/;
	
	/**
	 * Retrieve author.
	 * 
	 * @return author.
	 */
	public final native String getAuthor() /*-{
		return this.author;
	}-*/;
	
	/**
	 * Retrieve author score.
	 * 
	 * @return author score.
	 */
	public final native String getAuthorScore() /*-{
		return this.authorScore;
	}-*/;
	
	/**
	 * Retrieve annotation.
	 * 
	 * @return annotation.
	 */
	public final native String getAnnotation() /*-{
		return this.annotation;
	}-*/;

	/**
	 * Retrieve unmatched field.
	 * 
	 * @return unmatched field.
	 */
	public final native String getUnmatched() /*-{
		return this.unmatched;
	}-*/;

	/**
	 * Retrieve overall score.
	 * 
	 * @return overall score.
	 */
	public final native String getOverall() /*-{
		return this.overall;
	}-*/;

	/**
	 * Retrieve Family matched
	 * 
	 * @return family matched
	 */
	
	public final native String getFamilyMatched() /*-{
		return this.matchedFamily;
	}-*/;
	
	/**
	 * Retrieve Family matched score
	 * 
	 * @return Family matched score
	 */
	
	public final native String getFamilyMatchedScore() /*-{
		return this.matchedFamilyScore;
	}-*/;
	
	
	/**
	 * Return Species matched
	 * 
	 * @return Species matched
	 * 
	 */
	
	public final native String getSpeciesMatched() /*-{
		return this.speciesMatched;
	}-*/;
	
	
	/**
	 * Return Species matched score
	 * 
	 * @return Species matched score
	 * 
	 */
	
	public final native String getSpeciesMatchedScore() /*-{
		return this.speciesMatchedScore;
	}-*/;
	
	/**
	 * Return Infra-specific Rank 1
	 * 
	 * 
	 * @return Infra-specific Rank 1
	 */
	
	public final native String getInfraSpecificRank1() /*-{
		return this.infraspecific1Rank;
	}-*/;
	
	/**
	 * Return Infra-Specific Epithet 1 
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1
	 */
	
	
	public final native String getInfraSpecific1Epithet() /*-{
		return this.infraspecific1Epithet;
	}-*/;
	
	
	/**
	 * Return Infra-Specific Epithet 1 Score
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1 Score
	 */
	
	public final native String  getInfraSpecific1EpithetScore() /*-{
		return this.infraspecific1EpithetScore;
	}-*/;
	
	
	/**
	 * Return Infra-specific Rank 2
	 * 
	 * 
	 * @return Infra-specific Rank 2
	 */
	
	public final native String getInfraSpecificRank2() /*-{
		return this.infraspecific2Rank;
	}-*/;
	
	/**
	 * Return Infra-Specific Epithet 2 
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 2
	 */
	
	
	public final native String getInfraSpecific2Epithet() /*-{
		return this.infraspecific2Epithet;
	}-*/;
	
	
	/**
	 * Return Infra-Specific Epithet 1 Score
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1 Score
	 */
	
	public final native String  getInfraSpecific2EpithetScore() /*-{
		return this.infraspecific2EpithetScore;
	}-*/;
	
	
	/**
	 * Determines selected state.
	 * 
	 * @return true if selected.
	 */
	public final native boolean isSelected() /*-{
		return this.selected;
	}-*/;
	
	/**
	 *  Returns the status (Accepted Vs Synonym)
	 * 
	 * @return acceptance
	 * 
	 */
	 
	public final native String getAcceptance() /*-{
		return this.acceptance;
	}-*/;
	
	/**
	 * Return the submitted family
	 * 
	 * @return accepted family
	 * 
	 * 
	 */
	
	
	public final native String getSubmittedFamily() /*-{
		return this.familySubmitted;
	}-*/;
	
	/**
	 * Return the accepted name for this match
	 * 
	 * @return the accepted name
	 */
	 
	
	public final native String getAcceptedName() /*-{
		return this.acceptedName;
	}-*/;
	
	/**
	 * Return the accepted name for this match
	 * 
	 * @return the accepted name
	 */
	 
	
	public final native String getAcceptedNameUrl() /*-{
		return this.acceptedNameUrl;
	}-*/;
	
	/** return the accepted author for this match
	 * 
	 */
	
	public final native String getAcceptedAuthor() /*-{
		return this.acceptedAuthor;
	}-*/;
	
	
}
