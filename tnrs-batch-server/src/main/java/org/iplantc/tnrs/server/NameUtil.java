package org.iplantc.tnrs.server;

/**
 * This class contains common utility methods to operate on names 
 * prior to submission to TAXAMATCH. 
 * 
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 *
 */




public class NameUtil {

	
	/**
	 * Searches for either TAB or COMMA to extract the user id supplied portion of the
	 * parameter string. An exception will be thrown if the method cannot find such a
	 * separator 
	 * 
	 * @param line Input string with id and name submitted.
	 * @return A string containing the user supplied id
	 */
	
	
	public static String getNameId(String line){
		int idx=-1;
		idx = line.indexOf("\t");
		if(idx==-1) idx = line.indexOf(",");
		//log.info(idx +"  "+ line.substring(0,idx));
		return line.substring(0,idx);
	}
	
	/**
	 * Extracts the name portion of the supplied string and removes spaces, trims
	 * the string and removes tabs that might appear in the string
	 * 
	 * 
	 * 
	 * @param name The raw submitted string by the user
	 * @param containsId A boolean to distinguish whether the string is expected to have an id.
	 * @return
	 */
	
	
	public static String processName(String name,boolean containsId){
		String new_name="";
		int idx=-1;
		idx = name.indexOf("\t");
		if(idx==-1) idx = name.indexOf(",");

		if(containsId){
			new_name = name.substring(idx+1, name.length());
		}else{
			new_name =name;
		}
		return new_name.replace("\t"," ").replace("_", " ").replace("\u00D7", "x");
	}
	
	
	/**
	 * Takes a set of lines from the input file and returns a valid string to be processed by taxamatch.
	 * That is it extracts the name portion for each line and concatenates them, as well as remove ;' within
	 * the name
	 * 
	 * 
	 * @param namelist The raw 
	 * @param job
	 * @return
	 */
	
	public static String CleanNames(String[] namelist,TnrsJob job){

		String names=NameUtil.processName(namelist[0], job.containsId());

		for(int i=1; i < namelist.length;i++) {
			if(namelist[i].trim().equals("")) continue;

			names+= ";" + NameUtil.processName(namelist[i].replace(";", ""),job.containsId());
		}

		return names;

	}
	
}
