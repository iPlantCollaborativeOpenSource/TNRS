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
		var urls = this.Name_matched_url;
		var res = urls[0];
		
		for(i=1; i< urls.length; i++){
			res+=";"+urls[i];
		}
		
		return res;
	}-*/;

	/**
	 * Retrieve submitted name.
	 * 
	 * @return submitted name.
	 */
	public final native String getSubmittedName() /*-{
		return this.Name_submitted;
	}-*/;

	/**
	 * Retrieve scientific name.
	 * 
	 * @return scientific name.
	 */
	public final native String getScientificName() /*-{
		return this.Name_matched;
	}-*/;

	/**
	 * Retrieve scientific score.
	 * 
	 * @return scientific score.
	 */
	public final native String getScientificScore() /*-{
		return this.Name_score;
	}-*/;

	/**
	 * Retrieve attributed author.
	 * 
	 * @return attributed author.
	 */
	public final native String getAttributedAuthor() /*-{
		return this.Canonical_author;
	}-*/;
	
	/**
	 * Retrieve family.
	 * 
	 * @return family.
	 */
	public final native String getFamily() /*-{
		return this.Name_matched_accepted_family;
	}-*/;
	
	
	/**
	 * Retrieve family.
	 * 
	 * @return family.
	 */
	public final native String getFlag() /*-{
		return this.Warnings;
	}-*/;
	
	/**
	 * Retrieve genus.
	 * 
	 * @return genus.
	 */
	public final native String getGenus() /*-{
		return this.Genus_matched;
	}-*/;

	/**
	 * Retrieve genus score.
	 * 
	 * @return genus score.
	 */
	public final native String getGenusScore() /*-{
		return this.Genus_score;
	}-*/;
	
	/**
	 * Retrieve epithet.
	 * 
	 * @return epithet.
	 */
	public final native String getEpithet() /*-{
		return this.Specific_epithet_matched;
	}-*/;
	
	/**
	 * Retrieve epithet score.
	 * 
	 * @return epithet score.
	 */
	public final native String getEpithetScore() /*-{
		return this.Specific_epithet_score;
	}-*/;
	
	/**
	 * Retrieve author.
	 * 
	 * @return author.
	 */
	public final native String getAuthor() /*-{
		return this.Author_matched;
	}-*/;
	
	/**
	 * Retrieve author score.
	 * 
	 * @return author score.
	 */
	public final native String getAuthorScore() /*-{
		return this.Author_score;
	}-*/;
	
	/**
	 * Retrieve annotation.
	 * 
	 * @return annotation.
	 */
	public final native String getAnnotation() /*-{
		return this.Annotations;
	}-*/;

	/**
	 * Retrieve unmatched field.
	 * 
	 * @return unmatched field.
	 */
	public final native String getUnmatched() /*-{
		return this.Unmatched_terms;
	}-*/;

	/**
	 * Retrieve overall score.
	 * 
	 * @return overall score.
	 */
	public final native String getOverall() /*-{
		return this.Overall_score;
	}-*/;

	/**
	 * Retrieve Family matched
	 * 
	 * @return family matched
	 */
	
	public final native String getFamilyMatched() /*-{
		return this.Family_matched;
	}-*/;
	
	/**
	 * Retrieve Family matched score
	 * 
	 * @return Family matched score
	 */
	
	public final native String getFamilyMatchedScore() /*-{
		return this.Family_score;
	}-*/;
	
	
	/**
	 * Return Species matched
	 * 
	 * @return Species matched
	 * 
	 */
	
	public final native String getSpeciesMatched() /*-{
		return this.Specific_epithet_matched;
	}-*/;
	
	
	/**
	 * Return Species matched score
	 * 
	 * @return Species matched score
	 * 
	 */
	
	public final native String getSpeciesMatchedScore() /*-{
		return this.Specific_epithet_score;
	}-*/;
	
	/**
	 * Return Infra-specific Rank 1
	 * 
	 * 
	 * @return Infra-specific Rank 1
	 */
	
	public final native String getInfraSpecificRank1() /*-{
		return this.Infraspecific_rank;
	}-*/;
	
	/**
	 * Return Infra-Specific Epithet 1 
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1
	 */
	
	
	public final native String getInfraSpecific1Epithet() /*-{
		return this.Infraspecific_epithet_matched;
	}-*/;
	
	
	/**
	 * Return Infra-Specific Epithet 1 Score
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1 Score
	 */
	
	public final native String  getInfraSpecific1EpithetScore() /*-{
		return this.Infraspecific_epithet_score;
	}-*/;
	
	
	/**
	 * Return Infra-specific Rank 2
	 * 
	 * 
	 * @return Infra-specific Rank 2
	 */
	
	public final native String getInfraSpecificRank2() /*-{
		return this.Infraspecific_rank_2;
	}-*/;
	
	/**
	 * Return Infra-Specific Epithet 2 
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 2
	 */
	
	
	public final native String getInfraSpecific2Epithet() /*-{
		return this.Infraspecific_epithet_2_matched;
	}-*/;
	
	
	/**
	 * Return Infra-Specific Epithet 1 Score
	 * 
	 * 
	 * 
	 * @return Infra-Specific Epithet 1 Score
	 */
	
	public final native String  getInfraSpecific2EpithetScore() /*-{
		return this.Infraspecific_epithet_2_score;
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
		return this.Taxonomic_status;
	}-*/;
	
	/**
	 * Return the submitted family
	 * 
	 * @return accepted family
	 * 
	 * 
	 */
	
	
	public final native String getSubmittedFamily() /*-{
		return this.Family_submitted;
	}-*/;
	
	/**
	 * Return the accepted name for this match
	 * 
	 * @return the accepted name
	 */
	 
	
	public final native String getAcceptedName() /*-{
		return this.Accepted_name;
	}-*/;
	
	/**
	 * Return the accepted name for this match
	 * 
	 * @return the accepted name
	 */
	 
	
	public final native String getAcceptedNameUrls() /*-{
		
		var urls = this.Accepted_name_url;
		var res = urls[0];
		
		for(i=1; i< urls.length; i++){
			res+=";"+urls[i];
		}
		
		return res;
	}-*/;
	
	/** return the accepted author for this match
	 * 
	 */
	
	public final native String getAcceptedAuthor() /*-{
		return this.Accepted_name_author;
	}-*/;
	
	public final native String getAcceptedSpecies()/*-{
		return this.Accepted_species;
	}-*/;
	
	public final native String getNameMatchedRank()/*-{
		return this.Name_matched_rank;
	}-*/;
	
	public final native String getAcceptedFamily()/*-{
		return this.Accepted_name_family;
	}-*/;
	
	/**
	 * Return the source for the name
	 * 
	 */
	 
	
	public final native String getSource()/*-{
		var sources = this.Source;
		var res = sources[0];
		
		for(i=1; i< sources.length; i++){
			res+=";"+sources[i];
			
		}
		
		return res;
	}-*/;
	
	public final native String getAuthorMatched()/*-{
		return this.Author_matched;
		}-*/;
	
	public final native String getNameMatchedId()/*-{
		return this.Name_matched_id;
	}-*/;
	
}
