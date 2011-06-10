package org.iplantc.tnrs.server;

/**
 * This class holds information regarding a user request
 * for name matching 
 * 
 * @author Juan Antonio Raygoza Garay -- The iPlant Collaborative
 */

import org.apache.commons.codec.digest.DigestUtils;

public class NameMatchingRequest {

	private String email;
	private String filename;
	private String id;
	private String original;
	
	
	public NameMatchingRequest(String email, String inputFile,String original_filename) throws Exception{
		this.email = email;
		this.filename = inputFile;
		id = DigestUtils.md5Hex(email+inputFile);
		original = original_filename;
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
	
	
	
	
	
}
