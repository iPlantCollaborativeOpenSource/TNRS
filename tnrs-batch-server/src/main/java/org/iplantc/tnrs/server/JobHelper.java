package org.iplantc.tnrs.server;

/**
 * This class contains useful methods for dealing with common job operations.
 * 
 * @author Juan Antonio Raygoza Garay
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class JobHelper {

	
	
	/**
	 * Loads the serialized job information given the key and email.
	 * 
	 * 
	 * @param basefolder The folder where TNRS saves all job data.
	 * @param email  The user's email for the submitted job
	 * @param key The identifier of the specific job
	 * @return An instance of the Job de-serialized
	 * @throws Exception
	 */
	
	public static TnrsJob readJobInfo(String basefolder,String email, String key) throws Exception{

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(basefolder+email.replace("@","-").replace(".", "-")+"/"+key+".job"));
		TnrsJob job = (TnrsJob)in.readObject();
		in.close();
		return job;
	}
	
	
	/**
	 * Serializes the given job in the user's folder who is the owner of this job.
	 * 
	 * 
	 * @param basefolder The root folder where TNRS  stores all job information
	 * @param job The job to be serialized
	 * @throws IOException
	 */
	
	
	public static void persistJobInfo(String basefolder,TnrsJob job) throws IOException{
		String file_name = basefolder+job.userFolder()+"/"+job.getRequest().getId()+".job";

		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file_name));
		out.writeObject(job);
		out.close();

	}
	
	
	
	/**
	 * This method removes all input and result files from the user workspace.
	 * 
	 * 
	 * 
	 * @param basefolder The root folder where TNRS  stores all job information
	 * @param job  The job for which the data is to be cleared.
	 * @throws Exception
	 */
	
	public static void cleanJobData(String basefolder,TnrsJob job) throws Exception{
		
		File info = new File(basefolder+job.userFolder()+"/"+job.getRequest().getId()+".job");
		
		if(info.exists()){
			FileUtils.forceDelete(info);
		}
		
		File input = new File(basefolder+job.userFolder()+"/"+job.inputFilePath());
		if(input.exists()){
			FileUtils.forceDelete(input);
		}
		
		File results = new File(basefolder+job.userFolder()+"/"+job.resultFilePath());
		
		if(results.exists()){
			FileUtils.forceDelete(results);
		}
		
	}
	
	
	/**
	 * Checks for the existence of the file where a given job has been serialized.
	 * 
	 * 
	 * @param basefolder The root folder where TNRS  stores all job information
	 * @param email  The user's email
	 * @param key  The identifier for the job
	 * @return true if the file exists and can be read. false otherwhise.
	 * @throws Exception
	 */
	
	public static boolean jobFileExists(String basefolder,String email, String key) throws Exception{
		String file_name = basefolder+email.replace("@","-").replace(".", "-")+"/"+key+".job";
		File job_file = new File(file_name); 
		return job_file.exists() && job_file.canRead();
	}
	
	
	/**
	 * Creates a file containing the job information for sending it as an attachment
	 * 
	 * 
	 * @param job the job from which the information will be extracted
	 */
	
	public static String createJobInfoFile(TnrsJob job) throws Exception{
		
		File info = new File("/tmp/"+UUID.randomUUID().toString());
		
		BufferedWriter wr = new BufferedWriter(new FileWriter(info)); 
		
		
		
		wr.write("E-mail:"+job.getRequest().getEmail()+"\n");
		wr.write("Id: "+ job.getRequest().getId()+"\n");
		wr.write("Job type: "+job.getTypeString()+"\n");
		wr.write("Contains Id: "+job.containsId()+"\n");
		wr.write("Start time :"+job.getSubmissionDate()+"\n");
		wr.write("Finish time :"+job.getFinishedAt()+"\n");
		wr.write("TNRS version :"+job.getTnrs_version()+"\n");
		Vector<String> sources = job.getSources();
		
		wr.write("Sources selected : ["+sources.elementAt(0));
		
		for(int i=1; i < sources.size(); i++) {
			wr.write(" ,"+sources.elementAt(i));
		}
		
		wr.write(" ]\n");
		wr.write("Match threshold: "+job.sensitivity()+"\n");
		wr.write("Classification :" + job.getClassification()+"\n");
		wr.write("Allow partial matches? :"+job.isAllowPartial());
		wr.close();
		
		
		return info.getAbsolutePath();
	}
	
	
	/***
	 * Generate temporary file for interaction within a session
	 * 
	 * 
	 * @param the identifier for the session which corresponds to the filename
	 */
	
	
	public static void createWorkingFile(TnrsJob job,String session_id,String basefolder) throws Exception {
		File file = new File("/tmp/"+session_id+job.getRequest().getId()+".csv");
		
		
		FileUtils.copyFile(new File(basefolder+job.userFolder()+"/result"+job.getRequest().getId()), file);
		
		
	}
	
	/***
	 * Generate temporary file for interaction within a session
	 * 
	 * 
	 * @param the identifier for the session which corresponds to the filename
	 */
	
	
	public static boolean workingFileExists(TnrsJob job,String session_id,String basefolder) throws Exception {
		File file = new File("/tmp/"+session_id+job.getRequest().getId()+".csv");
		return file.exists();
		
	}
	
	
	
}
