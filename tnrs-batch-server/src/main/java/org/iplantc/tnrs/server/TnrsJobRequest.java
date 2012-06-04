package org.iplantc.tnrs.server;

/**
 * This class holds information regarding a user request
 * for name matching 
 * 
 * @author Juan Antonio Raygoza Garay -- The iPlant Collaborative
 */

import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;

public class TnrsJobRequest implements Serializable{

	private String email;
	private String filename;
	private String id;
	private String original;
	private boolean emailResults;
	
	public TnrsJobRequest(String email, String inputFile,String original_filename,boolean email_results) throws Exception{
		this.email = email;
		this.filename = inputFile;
		id = DigestUtils.md5Hex(email+inputFile);
		original = original_filename;
		emailResults = email_results;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	public String getOriginalFilename() {
		return original;
	}

	/**
	 * @return the emailResults
	 */
	public boolean sendEmailResults() {
		return emailResults;
	}

	/**
	 * @param emailResults the emailResults to set
	 */
	public void setEmailResults(boolean emailResults) {
		this.emailResults = emailResults;
	}
	
	
	
	
	
}
