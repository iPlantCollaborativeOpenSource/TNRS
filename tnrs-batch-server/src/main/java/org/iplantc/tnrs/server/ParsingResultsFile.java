package org.iplantc.tnrs.server;


/**
 *  This class manages the I/O operations for output files that are/will-be
 *  associated with a specific job. 
 * 
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 */



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;


import org.apache.commons.io.output.FileWriterWithEncoding;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class ParsingResultsFile {


	private Vector<String> column_definition;
	private TnrsJob job;
	private File results ;

	
	/***
	 * Instantiates an object and is linked to a specific job.
	 * 
	 * 
	 * 
	 * @param job The job to which files will be operated on
	 * @param tnrs_folder Base tnrs date folder
	 * @param existing 
	 * @throws Exception
	 */
	

	public ParsingResultsFile(TnrsJob job,String tnrs_folder) throws Exception
	{

		column_definition= new Vector<String>();

		column_definition.add("name_submitted");
		column_definition.add("canonical_name");
		column_definition.add("author");
		column_definition.add("family");
		column_definition.add("genus");
		column_definition.add("specific_epithet");
		column_definition.add("infraspecific_epithet");
		column_definition.add("infraspecific_rank");
		column_definition.add("infraspecific_epithet_2");
		column_definition.add("infraspecific_rank_2");
		column_definition.add("annotations");
		column_definition.add("unmatched_terms");
		column_definition.add("taxon_name");
		column_definition.add("user_id");
		

		results = new File(tnrs_folder+job.userFolder()+"/"+job.resultFilePath());
		this.job = job;

	}


	/**
	 * Writes a set of results in JSON format to the linked output file in a TAB delimited format.
	 * 
	 * 
	 * 
	 * @param parsing_data The result parsing data to be written 
	 * @param ids The set of user supplied ids associated with each name
	 * @throws Exception
	 */

	public void writeJsonData(JSONArray parsing_data, Vector<String> ids) throws Exception{

		boolean header = false;
		
		if(!results.exists()){
			header=true;
		}
		
		BufferedWriter wr = new BufferedWriter(new FileWriterWithEncoding(results, "UTF-8", true),3*1024*1024);
	
		if(header){
			wr.write(column_definition.elementAt(0));

			for(int i=1; i < column_definition.size(); i++){
				wr.write("\t"+column_definition.elementAt(i));
			}

			wr.write("\n");
		}

		for(int i=0; i < parsing_data.size(); i++){
			JSONObject name = parsing_data.getJSONObject(i);

			String taxon_name="";
			String canonical_name ="";
			
			if(name.getString("Genus").trim().equals("")){
				taxon_name = name.getString("Family");
				canonical_name = name.getString("Family");
			}else{
				taxon_name = name.getString("Genus")+" "+ name.getString("Specific_epithet")+" " + name.getString("Infraspecific_rank")+" "+name.getString("Infraspecific_epithet") + " "+name.getString("Infraspecific_rank_2") +" "+name.getString("Infraspecific_epithet_2") ;
				canonical_name = name.getString("Genus")+" "+ name.getString("Specific_epithet")+" " +name.getString("Infraspecific_epithet") +" "+name.getString("Infraspecific_epithet_2") ;
			}
			
			wr.write(name.getString("Name_submitted")+"\t");
			wr.write( canonical_name+"\t");
			wr.write( name.getString("Author")+"\t");
			wr.write(name.getString("Family")+"\t");
			wr.write(name.getString("Genus")+"\t");
			wr.write(name.getString("Specific_epithet")+"\t");
			wr.write(name.getString("Infraspecific_epithet")+"\t");
			wr.write(name.getString("Infraspecific_rank")+"\t");
			wr.write(name.getString("Infraspecific_epithet_2")+"\t");
			wr.write(name.getString("Infraspecific_rank_2")+"\t");
			wr.write(name.getString("Annotations")+"\t");
			wr.write(name.getString("Unmatched_terms")+"\t");
			wr.write( taxon_name);

			if(job.containsId()){
				wr.write("\t"+ids.elementAt(i));
			}

			
			wr.write("\n");
		}


		wr.close();


	}


	/**
	 * Returns the number of names submitted.
	 * 
	 * 
	 * 
	 * 
	 * @return the number of groups
	 * @throws Exception
	 */
	
	public int getResultsSize() throws Exception{
		return job.recordCount();
	}

	
	
	/**
	 * Retrieves the top names in each group that falls into the specified range.
	 * 
	 * 
	 * @param start interval start
	 * @param end interval end
	 * @return A json array with the names that are present in the given interval.
	 * @throws Exception
	 */
	
	public JSONArray getResultsInterval(int start, int end) throws Exception{

		JSONArray results_array = new JSONArray();

		BufferedReader rd = new BufferedReader(new FileReader(results));
		rd.readLine();
		String line="";
		int c=0;
		int name_count=0;
		while(true){
			line = rd.readLine();
			if(line==null) break;

			String[] values = line.split("\t",-1);

			
			if(c>=start && name_count<end){
				JSONObject name = new JSONObject();
				name.put("Name_submitted",values[column_definition.indexOf("name_submitted")]);
				name.put("Canonical_name",values[column_definition.indexOf("canonical_name")]);
				name.put("Family",values[column_definition.indexOf("family")]);
				name.put("Genus",values[column_definition.indexOf("genus")]);
				name.put("Specific_epithet",values[column_definition.indexOf("specific_epithet")]);
				name.put("Infraspecific_rank",values[column_definition.indexOf("infraspecific_rank")]);
				name.put("Infraspecific_epithet",values[column_definition.indexOf("infraspecific_epithet")]);
				name.put("Infraspecific_rank_2",values[column_definition.indexOf("infraspecific_rank_2")]);
				name.put("Infraspecific_epithet_2",values[column_definition.indexOf("infraspecific_epithet_2")]);
				name.put("Author",values[column_definition.indexOf("author")]);
				name.put("Annotations",values[column_definition.indexOf("annotations")]);
				name.put("Unmatched_terms",values[column_definition.indexOf("unmatched_terms")]);
				name.put("Taxon_name", values[column_definition.indexOf("taxon_name")]);
				results_array.add(name);
				name_count++;
			}else if(name_count>=end){
				break;
			}
			c++;
			
		}


		return results_array;
	}


	/**
	 * Creates a file for download for the specified job.
	 * 
	 * 
	 * @param output_folder The folder set as the default output for downloads
	 * @throws Exception
	 */
	
	public void createFileForDownload(String output_folder) throws Exception {



		BufferedWriter wr = new BufferedWriter(new FileWriter(output_folder+"csv"+job.getRequest().getId()+".csv"));


		wr.write("Name_submitted\tTaxon_name\tCanonical_name\tAuthor\tFamily\tGenus\tSpecific_epithet\tInfraspecific_rank\tInfraspecific_epithet\tInfraspecific_rank_2\tInfraspecific_epithet_2\tAnnotations\tUnmatched_terms");  //write header

		if(job.containsId()) {
			wr.write("\tuser_id");
		}
		wr.write("\n");

		BufferedReader rd = new BufferedReader(new FileReader(results));
		rd.readLine();
		String line="";

		while(true){
			line = rd.readLine();
			if(line==null) break;

			String[] values = line.split("\t",-1);


			wr.write(values[column_definition.indexOf("name_submitted")]+"\t"+values[column_definition.indexOf("taxon_name")]+"\t"+values[column_definition.indexOf("canonical_name")]+"\t"+values[column_definition.indexOf("author")]+"\t"+values[column_definition.indexOf("family")]+"\t"+values[column_definition.indexOf("genus")]+"\t"+values[column_definition.indexOf("specific_epithet")]+"\t"+ values[column_definition.indexOf("infraspecific_rank")] +"\t"+values[column_definition.indexOf("infraspecific_epithet")]+"\t"+values[column_definition.indexOf("infraspecific_rank_2")]+"\t"+values[column_definition.indexOf("infraspecific_epithet_2")]+"\t"+values[column_definition.indexOf("annotations")]+"\t"+values[column_definition.indexOf("unmatched_terms")]);

			if(job.containsId()) {
				wr.write("\t"+values[column_definition.indexOf("user_id")]);
			}

			wr.write("\n");

		}


		wr.close();


	}












}
