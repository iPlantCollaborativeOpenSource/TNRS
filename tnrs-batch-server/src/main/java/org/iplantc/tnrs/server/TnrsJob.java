package org.iplantc.tnrs.server;

/**
 * TNRSJob
 * 
 * This class holds all the information regarding a job execution and the names associated
 * with this run, a job can be either matching or parsing.
 * 
 * @author Juan Antonio Raygoza Garay
 * 
 */



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import net.sf.json.JSONObject;


/**
 * @author raygoza
 *
 */
public class TnrsJob implements Serializable{

	public static int NAME_MATCH_JOB=0;
	public static int PARSING_JOB=1;
	
	private TnrsJobRequest request;
    private boolean enabled;
	private double progress;
	private int num_records;
	private int nextBatch=0;
	private String submittedAt;
	private String status;
	private int processed=0;
	private int steps;
	private boolean email=true;
	private double sensitivity;
	private boolean containsId=false;
	private int type;
	private boolean taxonomic;
	private String userFolder;
	private String inputFilePath;
	private String finishedAt;
	private String tnrs_version;
	private Vector<String> sources;
	private String classification;
	private boolean sortBySource;
	private boolean allowPartial;
	
	
	/**
	 * Constructor
	 * 
	 * Takes a request, submitted date and job type to initialize this job object.
	 * 
	 * @param request Contains most information provided by the user
	 * @param submitted_date the date of successfull submission
	 * @param job_type The job type is either parsing or matching
	 */
	
	
	public TnrsJob(TnrsJobRequest request,String submitted_date,String job_type) {
		this.request = request;
		if(job_type.toLowerCase().equals("matching")){
			type=NAME_MATCH_JOB;
		}else{
			type=PARSING_JOB;
		}
		sources = new Vector<String>();
		userFolder = request.getEmail().replace("@","-").replace(".", "-");
		inputFilePath = "/tnrs-jobs/"+userFolder+"/"+request.getId()+new Date().toString().replaceAll("[ |:]", "")+".csv";
		submittedAt = submitted_date;
		enabled=true;
		sortBySource =true;
		try {
			BufferedReader rd = new BufferedReader(new FileReader(request.getFilename()));
			String line="";
			while(true) {
			    line=rd.readLine();
				if(line==null) break;
				if(line.trim().equals("")) continue;
				num_records++;
			}


			steps =(int)Math.ceil(num_records/100.0);
			rd.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	/**
	 * Specifies whether the job input names contain a column with user specified ids.
	 * 
	 * @param containsId true if it contains an id or false otherwise
	 */
	
	
	public void setContainsId(boolean containsId){
		this.containsId = containsId;
	}
	
	
	/**
	 * specifies whether the submitted job has an id or not.
	 * 
	 * @return true if the job was submitted with an id or false otherwise.
	 */
	
	public boolean containsId(){
		return containsId;
	}
	
	
	/**
	 * Returns the chunk number that is being currently processed
	 * 
	 * 
	 * @return the batch number
	 */
	
	public int getCurrentBatch() {
		return nextBatch;
	}
	
	/**
	 * Specifies whether the job is valid for execution.
	 * 
	 * 
	 * 
	 * @return true if valid, false otherwise
	 */

    public boolean isEnabled(){
    	return enabled;
    }

    
    /**
     * Returns the type of job that was intended for execution
     * 
     * 
     * @return A integer representing NAME_MATCH_JOB or PARSING_JOB
     */
    
    public int getType() {
    	return type;
    }
    
    
    /**
     *  If an error occurs this method is used to invalidate the job.
     * 
     * 
     */

    public void disable(){
    	enabled=false;
    	status="Stopped";
    }
    
    
    /**
     * This method merely pauses the job, this can be used when shutting the server down.
     * 
     * 
     */
    
    public void pause() {
    	status="paused";
    }
    
    /**
     * Resumes the job for further execution
     * 
     * 
     */
    
    
    public void resume() {
    	if(!status.equalsIgnoreCase("running")) {
    		status="idle";
    	}
    }
    
    
    /**
     * This methods reads 100 names from the file starting from the next name available in the file. If 
     * no 100 names are available it only returns the available number of names. It increments the progress
     * and the current batch number.
     * 
     * 
     * @return A new line separated string of names for processing.
     * @throws Exception
     */
    
    
    
    
	public synchronized String getNextDataBatch() throws Exception{
		if(nextBatch < steps) {

			int start = 100*nextBatch;
			int end = start+100 -1;

			
			
			if(end > num_records) {
				end = num_records;
			}
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(request.getFilename()), "UTF-8");
			BufferedReader rd = new BufferedReader(isr,500000);

			int c=0;
			int k=0;
			String line ="";

			while(true) {

				line = rd.readLine();
				if(line==null) break;
				if(line.trim().equals("")) continue;

				if(c>=start && c<=end) {
					buffer.append(line+"\n");

					k++;
				}
				c++;
				if(c>end) break;

			}
			rd.close();
			processed+=k;
			nextBatch++;
			progress = ((double)(processed)/(double)num_records)*100.0;

			return buffer.toString();
		}else {
			return "";
		}
	}

	
	/**
	 * Returns  whether the job is to be constrained taxonomically.
	 * 
	 * 
	 * 
	 * @return true if constrained taxonomically, false otherwise
	 */
	
	public boolean isTaxonomic() {
		return taxonomic;
	}

	
	/**
	 * Sets whether the job is to be constrained taxonomically
	 * 
	 * 
	 * 
	 * @param taxonomic true if the job is to be constrained, false otherwise
	 */
	
	public void setTaxonomic(boolean taxonomic) {
		this.taxonomic = taxonomic;
	}
	
	
	/**
	 * Returns the remaining names to be processed.
	 * 
	 * 
	 * @return an integer representing the number of names left for processing.
	 */
	
	
	public int outstandingNames(){
		return num_records-processed;
	}

	
	/**
	 * Returns a string representing the type of job to be performed
	 * 
	 * 
	 * @return A string with either "parsing" or "matching"
	 */
	
	
	public String getTypeString(){
		if(type==NAME_MATCH_JOB){
			return "matching";
		}else{
			return"parsing";
		}
	
	}
	
	
	/**
	 * Returns a string representation of the status of the job: running, idle, stopped,etc
	 * The possible values can be extended for further functionality.
	 * 
	 * @return A string with the status value
	 */
	
	public String status() {
		return status;
	}
	
	
	/**
	 * Sets the status of the job according to various events.
	 * 
	 * 
	 * @param status
	 */

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	/**
	 * Returns the date/time at which this job was submitted
	 * 
	 * 
	 * @return String representation of date/time.
	 */

	public String getSubmissionDate() {
		return submittedAt;
	}
	
	/**
	 * Returns the request object the created this job.
	 * 
	 * @return An object of type TNRSJobRequest
	 */

	public TnrsJobRequest getRequest() {
		return request;
	}
	
	
	/**
	 * Returns the fraction of names processed so far.
	 * 
	 * 
	 * @return a number between 0 and 1.0
	 */

	public double progress() {
		return progress;
	}
	
	/**
	 * Returns whether an email should be sent when various events happen.
	 * This is useful for jobs submitted from the system itself or api jobs.
	 * 
	 * 
	 * @return true if email should be sent, false otherwise
	 */
	
	
	public boolean email() {
		return email;
	}
	
	
	/**
	 * Sets whether the system should send an email on certain events.
	 * 
	 * 
	 * @param send_email a boolean 
	 */
	
	public void setEmail(boolean send_email) {
		email = send_email;
	}
	
	
	/**
	 * Returns the filtering threshold for names returned set by the user.
	 * 
	 * 
	 * @return a double between zero and one.
	 */
	
	public double sensitivity() {
		return sensitivity;
	}
	
	
	/**
	 * Sets the sensitivity to filter names returned.
	 * 
	 * 
	 * @param sensitivity_value a value between 0.0 and 1.0
	 */
	
	public void setSensitivity(double sensitivity_value) {
		if(sensitivity_value>1.0) {
			sensitivity_value=1.0;
		}
		if(sensitivity_value<0.0) {
			sensitivity_value=0.0;
		}
		sensitivity = sensitivity_value;
	}
	
	
	/**
	 * Returns a json formatted string containing all the job information.
	 * 
	 * 
	 * @return a json representation of the job.
	 */
	
	
	public String toJsonString(){
		JSONObject json = new JSONObject();
		
		json.put("email", email);
		json.put("sensitivity", sensitivity);
		json.put("submitted", submittedAt);
		json.put("filename", request.getOriginalFilename());
		json.put("type", type);
		json.put("number_of_records", num_records);
		json.put("taxonomic",taxonomic);
		return json.toString();
	}
	

	/**
	 * Returns a string representing the path where all input and output files for
	 * the user who started the job will be located.
	 * 
	 * @return A string with the user folder path.
	 */
	
	public String userFolder(){
		return userFolder;
	}
	
	
	/**
	 * Returns the name of the files where the input names were saved.
	 * 
	 * 
	 * @return A string with the input filename.
	 */
	
	
	public String inputFilePath(){
		return inputFilePath;
	}
	
	/**
	 * Returns the name of the file where all the processing results will be stored and retrieved.
	 * 
	 * 
	 * @return a string with the rersults filename.
	 */
	
	
	public String resultFilePath(){
		return "result"+request.getId();
	}
	
	/**
	 * Returns the total number of names submitted, that is not counting blank lines.
	 * 
	 * 
	 * @return the total name count submitted
	 */
	
	public int recordCount(){
		return num_records;
	}
	
	/**
	 * Returns a string representation of the date/time at which the job finished.
	 * 
	 * 
	 * @return a string with finished date/time.
	 */

	public String getFinishedAt() {
		return finishedAt;
	}

	/**
	 * Sets the string containing the completion date/time for the job
	 * 
	 * 
	 * @param finishedAt A string containing the date/time
	 */
	
	
	public void setFinishedAt(String finishedAt) {
		this.finishedAt = finishedAt;
	}
	
	
	/**
	 * Returns the tnrs version at which this job was created and executed.
	 * 
	 * 
	 * @return A string with the version.
	 */
	

	public String getTnrs_version() {
		return tnrs_version;
	}

	
	/***
	 * 
	 * Sets the version of the TNRS at which this job was created.
	 *  
	 * @param tnrs_version the string containing the current version
	 */
	
	
	public void setTnrs_version(String tnrs_version) {
		this.tnrs_version = tnrs_version;
	}

	public String getSourcesAsString() {
		
		String srcs= sources.elementAt(0);
		
		for(int i=1; i < sources.size();i++){
			srcs+=","+sources.elementAt(i);
		}
		
		return srcs;
	}
	
	public Vector<String> getSources(){
		return sources;
	}

	public void setSources(String new_sources) {
		
		String[] the_sources = new_sources.split(",");
		
		for(int i=0; i < the_sources.length;i++){
			sources.add(the_sources[i]);
		}	
	}


	public String getClassification() {
		return classification;
	}


	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	
	public void setBySource(boolean sort) {
		sortBySource= sort;
	}
	
	public boolean sortBySource() {
		return this.sortBySource;
	}


	public boolean isAllowPartial() {
		return allowPartial;
	}


	public void setAllowPartial(boolean allowPartial) {
		this.allowPartial = allowPartial;
	}
	
	
	
	
	
}

