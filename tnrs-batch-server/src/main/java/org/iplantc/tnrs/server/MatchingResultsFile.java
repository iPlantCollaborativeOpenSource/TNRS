package org.iplantc.tnrs.server;

/***
 * This class implements I/O operations related to saving/retrieving job results
 * 
 * 
 * @author Juan Antonio Raygooza Garay
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Vector;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.iplantc.tnrs.server.processing.TNRSNameFilter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MatchingResultsFile {

	Connection conn;
	private Vector<String> column_definition;
	private String tablename;
	private DecimalFormat fmt;
	private TnrsJob job;
	private File results;
	private TNRSNameFilter filter;
	private File workingCopy;

	static Logger log = Logger.getLogger(MatchingResultsFile.class);


	/**
	 * Instantiates a MatchingResultsFile object poiting to the output file where the results are/will-be 
	 * stored for the job passed as a parameter
	 * 
	 * 
	 * 
	 * 
	 * @param job The job associated with the file that this class will oprate on. 
	 * @param tnrs_folder The root folder path for all saved input/output data.
	 * @throws Exception
	 */



	public MatchingResultsFile(TnrsJob job,String tnrs_folder,String session_id,boolean first_request) throws Exception
	{

		fmt = new DecimalFormat("#.##");

		column_definition= new Vector<String>();

		column_definition.add("name_submitted");
		column_definition.add("overall_score");
		column_definition.add("name_matched_id");
		column_definition.add("name_matched");
		column_definition.add("name_score");
		column_definition.add("name_matched_rank");
		column_definition.add("author_matched");
		column_definition.add("author_score");
		column_definition.add("canonical_author");
		column_definition.add("name_matched_accepted_family");
		column_definition.add("genus_submitted");
		column_definition.add("genus_matched");
		column_definition.add("genus_score");
		column_definition.add("specific_epithet_submitted");
		column_definition.add("specific_epithet_matched");
		column_definition.add("specific_epithet_score");
		column_definition.add("family_submitted");
		column_definition.add("family_matched");
		column_definition.add("family_score");
		column_definition.add("infraspecific_rank");
		column_definition.add("infraspecific_epithet_matched");
		column_definition.add("infraspecific_epithet_score");
		column_definition.add("infraspecific_rank_2");
		column_definition.add("infraspecific_epithet_2_matched");
		column_definition.add("infraspecific_epithet_2_score");
		column_definition.add("annotations");
		column_definition.add("unmatched_terms");
		column_definition.add("name_matched_url");
		column_definition.add("phonetic");
		column_definition.add("taxonomic_status");
		column_definition.add("accepted_name");
		column_definition.add("accepted_species");
		column_definition.add("accepted_name_author");
		column_definition.add("accepted_name_id");
		column_definition.add("accepted_name_rank");
		column_definition.add("accepted_name_url");
		column_definition.add("accepted_family");
		column_definition.add("overall_score_order");
		column_definition.add("highertaxa_score_order");
		column_definition.add("warnings");
		column_definition.add("ngroup");
		column_definition.add("ngroup_size");
		column_definition.add("selected");
		column_definition.add("source");
		column_definition.add("source_constrain_on_order");
		column_definition.add("source_constrain_off_order");
		column_definition.add("nsources");
		column_definition.add("sort_override");
		column_definition.add("accepted_name_lsid");
		column_definition.add("user_id");


		filter = new TNRSNameFilter(column_definition);

		results = new File(tnrs_folder+job.userFolder()+"/"+job.resultFilePath());



		workingCopy = new File("/tmp/"+session_id+job.getRequest().getId()+".csv");


		if(first_request && workingCopy.exists()) {
			FileUtils.forceDelete(workingCopy);
		}
		
		tablename = job.resultFilePath();

		this.job =job;


	}


	/**
	 * Given a string representation of the flag assigned to a name, it will return a
	 * human readable string that will be placed on the output file
	 * 
	 * 
	 * @param flagt flag value
	 * @return
	 * @throws Exception
	 */



	private String getFlagText(String flagt) throws Exception{
		String ambiguousText =" ";

		

		if(flagt.trim().equals("")) return " " ;
		int flag = Integer.parseInt(flagt);


		if(flag==0) return " ";
		if((flag&1)==1 ) {
			ambiguousText += "[Partial match] ";
		}
		if((flag&2)==2 ) {
			ambiguousText += "[Ambiguous match] ";
		}
		if((flag&4)==3) {
			ambiguousText += "[Better spelling match in different higher taxon] ";
		}
		if((flag&8)==4) {
			ambiguousText += "[Better higher taxonomic match available] ";
		}

		return ambiguousText;
	}



	public void close() throws Exception{



	}

	public void unlink() throws Exception{

	}


	/***
	 * Saves a set of results in JSON format into the output file associated with this object in TAB delimited
	 * format
	 * 
	 * 
	 * @param matching_data The array of json objects
	 * @param job 
	 * @param ids a vector with corresponding user supplied ids.(only populated if job.containsId() is true)
	 * @throws Exception
	 */

	public void writeJsonData(JSONArray matching_data, Vector<String> ids) throws Exception{
		try {

			boolean header = false;

			if(!results.exists()){
				header=true;
			}

			BufferedWriter wr = new BufferedWriter(new FileWriterWithEncoding(results, "UTF-8", true),3*1024*1024);

			if(header){
				wr.write(column_definition.elementAt(0));

				for(int i=1; i < column_definition.size();i++){
					wr.write("\t"+column_definition.elementAt(i));
				}
				wr.write("\n");
			}


			for(int i=0; i < matching_data.size(); i++) {

				JSONObject json = matching_data.getJSONObject(i);

				wr.write(json.getString("Name_submitted").replace("\\", "")+"\t");
				wr.write(fmt.format(json.optDouble("Overall_score",0.00))+"\t");
				wr.write(json.getString("Name_matched_id")+"\t");
				wr.write(json.getString("Name_matched").replace("\\", "")+"\t");
				wr.write(fmt.format(json.optDouble("Name_score",0.00))+"\t");
				wr.write(json.getString("Name_matched_rank")+"\t");
				wr.write(json.getString("Author_matched").replace("\\", "")+"\t");
				if(!json.optString("Author_submitted","").equals("") && json.getString("Author_score").equals("0")){
					wr.write("0.0\t");
				}else{
					wr.write(format(json.getString("Author_score"))+"\t");
				}
				wr.write(json.getString("Canonical_author").replace("\\", "")+"\t");
				wr.write(json.getString("Name_matched_accepted_family")+"\t");
				wr.write(json.getString("Genus_submitted")+"\t");
				wr.write(json.getString("Genus_matched")+"\t");
				wr.write(format(json.getString("Genus_score"))+"\t");
				wr.write(json.getString("Specific_epithet_submitted")+"\t");
				wr.write(json.getString("Specific_epithet_matched")+"\t");
				wr.write(format(json.getString("Specific_epithet_score"))+"\t");
				wr.write(json.getString("Family_submitted")+"\t");
				wr.write(json.getString("Family_matched")+"\t");
				wr.write(format(json.getString("Family_score"))+"\t");
				wr.write(json.getString("Infraspecific_rank")+"\t");
				wr.write(json.getString("Infraspecific_epithet_matched")+"\t");
				wr.write(format(json.getString("Infraspecific_epithet_score"))+"\t");
				wr.write(json.getString("Infraspecific_rank_2")+"\t");
				wr.write(json.getString("Infraspecific_epithet_2_matched")+"\t");
				wr.write(format(json.getString("Infraspecific_epithet_2_score"))+"\t");
				wr.write(json.getString("Annotations")+"\t");
				wr.write(json.getString("Unmatched_terms").replace("\\", "")+"\t");
				wr.write(JsonArrayToString(json.getJSONArray("Name_matched_url")).replace("\\", "")+"\t");
				wr.write(json.getString("Phonetic")+"\t");
				wr.write(json.getString("Taxonomic_status")+"\t");
				wr.write(json.getString("Accepted_name")+"\t");
				wr.write(json.getString("Accepted_species")+"\t");
				wr.write(json.getString("Accepted_name_author").replace("\\", "")+"\t");
				wr.write(json.getString("Accepted_name_id")+"\t");
				wr.write(json.getString("Accepted_name_rank")+"\t");
				wr.write(JsonArrayToString(json.getJSONArray("Accepted_name_url")).replace("\\", "")+"\t");
				wr.write(json.getString("Accepted_family")+"\t");
				wr.write(json.getInt("Overall_score_order")+"\t");
				wr.write(json.optInt("Highertaxa_score_order",0)+"\t");
				wr.write(json.getInt("Warnings")+"\t");
				wr.write(json.getInt("group")+"\t");
				wr.write(json.getInt("groupSize")+"\t");
				wr.write(json.getString("selected")+"\t");
				wr.write(JsonArrayToString(json.getJSONArray("Source"))+"\t");
				wr.write(json.getInt("Source_constrain_on_order")+"\t");
				wr.write(json.getInt("Source_constrain_off_order")+"\t");
				wr.write(json.getString("Nsources")+"\t");
				wr.write("0"+"\t");
				wr.write(json.optString("Accepted_name_lsid",""));
				if(job.containsId()){

					wr.write("\t"+ ids.elementAt(json.getInt("group")%100));

				}

				wr.write("\n");


			}


			wr.close();
		}catch(Exception ex)
		{
			log.error(ExceptionUtils.getFullStackTrace(ex));
			throw ex;
		} 
	}


	public String JsonArrayToString(JSONArray array) {
		String value = array.getString(0);

		for(int k=1; k < array.size();k++) {
			value += ";"+array.getString(k);
		}
		return value;
	}

	public JSONArray stringToJSONArray(String value) {

		JSONArray array = new JSONArray();

		String[] values = value.split(";",-1);

		for(int i=0; i < values.length;i++) {
			array.add(values[i]);
		}


		return array;
	}


	/**
	 * Applies a decimal formatting to a double in string format.
	 * 
	 * 
	 * 
	 * @param value The value to be formatted
	 * @return The formatted representation
	 */

	public String format(String value ){

		if(value.trim().equals("")) return value;

		return fmt.format(Double.parseDouble(value));

	}


	/**
	 * Returns the number of main records (selected=true) that are present in this file.
	 * 
	 * 
	 * @return The number of total main records.
	 * @throws Exception
	 */

	public int getResultsSize() throws Exception{

		return job.recordCount();
	}


	/**
	 * Reads a set of names based on the start and end delimiters. The names returned
	 * correspond to the best names according to a given criteria (taxonomic constraint or raw score).
	 * 
	 * 
	 * @param start
	 * @param end
	 * @param job
	 * @param isTaxonomic
	 * @return
	 * @throws Exception
	 */


	public JSONArray getResultsInterval(int start, int end,TnrsJob job,boolean isTaxonomic,boolean order_by_source) throws Exception{

		if(!workingCopy.exists()) {
			FileUtils.copyFile(results, workingCopy);
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(workingCopy), "UTF-8"));


		String line ="";
		rd.readLine();
		JSONArray results= new JSONArray();

		int row_count=0;

		while(true){
			line =rd.readLine();
			if(line==null) break;
			String[] values = line.split("\t",-1);
			int group = Integer.parseInt(values[column_definition.indexOf("ngroup")]);
			if( group>=start && row_count<end  ){
				JSONObject json = rowToJSONObject(line);

				if(filter.filter(values, order_by_source, isTaxonomic)){
					json.remove("selected");
					json.put("selected", "true");
					results.add(json);
					row_count++;
				}else{
					continue;
				}


			}else if(row_count>=end){
				break;
			}
		}
		rd.close();
		return results;
	}


	/**
	 * Returns the names for a specific group within the results file.
	 * 
	 * 
	 * 
	 * @param group index of the group.
	 * @return A JsonArray for the group names.
	 * @throws Exception
	 */

	public JSONArray getGroupInfo(int group) throws Exception{

		
		JSONArray results_array =new JSONArray();


		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(workingCopy), "UTF-8"));


		String line ="";
		rd.readLine();

		while(true){
			line =rd.readLine();
			if(line==null) break;
			String[] values=line.split("\t",-1);
			int cur_group= Integer.parseInt(values[40]);
			if( cur_group <=group ){
				if(cur_group==group){
					results_array.add(rowToJSONObject(line));
				}
			}else{
				break;
			}


		}

		rd.close();
		return results_array;
	}



	/**
	 * Reads a row from the results file and creates/populates a JSONObject with the
	 * corresponding key names and values. 
	 * 
	 * 
	 * 
	 * @param rowt The string representing the entire line from the file
	 * @return The populated json object.
	 * @throws Exception
	 */

	public JSONObject rowToJSONObject(String rowt) throws Exception{
		JSONObject item = new JSONObject();

		String[] values = rowt.split("\t",-1);

		item.put("group", values[40]);
		item.put("Accepted_name_author",values[32]);
		item.put("Name_submitted", values[0]);
		item.put("Name_matched_id",values[2]);
		item.put("Name_matched_url",stringToJSONArray(values[27].replace("\\", "")));
		item.put("Name_matched", values[3]);
		item.put("Name_score",values[4]);
		item.put("Family_matched", values[17]);
		item.put("Family_score",values[18]);
		item.put("Canonical_author", values[8]);
		item.put("Genus_matched",values[11]);
		item.put("Genus_score",values[12]);
		item.put("Specific_epithet_submitted", values[13]);
		item.put("Specific_epithet_matched", values[14]);
		item.put("Specific_epithet_score",values[15]);
		item.put("Infraspecific_rank", values[19]);
		item.put("Infraspecific_epithet_matched",  values[20]);
		item.put("Infraspecific_epithet_score",values[21]);
		item.put("Infraspecific_rank_2", values[22]);
		item.put("Infraspecific_epithet_2_matched",  values[23]);
		item.put("Infraspecific_epithet_2_score",values[24]);
		item.put("Author_matched",values[column_definition.indexOf("author_matched")]);
		item.put("Author_score",values[7]);
		item.put("Annotations", values[25]);
		item.put("Unmatched_terms", values[column_definition.indexOf("unmatched_terms")].replace("\"","\"\""));
		item.put("Overall_score",values[column_definition.indexOf("overall_score")]);
		item.put("Accepted_name",values[column_definition.indexOf("accepted_name")]);
		item.put("Warnings",values[column_definition.indexOf("warnings")]);
		item.put("Taxonomic_status",values[column_definition.indexOf("taxonomic_status")]);
		item.put("Family_submitted", values[column_definition.indexOf("family_submitted")] );		
		item.put("selected", Boolean.parseBoolean(values[42].trim()));
		item.put("groupSize",Integer.parseInt(values[column_definition.indexOf("ngroup_size")]));
		item.put("Accepted_name_url",stringToJSONArray(values[column_definition.indexOf("accepted_name_url")]));
		item.put("Accepted_species", values[column_definition.indexOf("accepted_species")]);
		item.put("Name_matched_rank", values[column_definition.indexOf("name_matched_rank")]);
		item.put("Accepted_family",values[column_definition.indexOf("accepted_family")]);
		item.put("Genus_submitted", values[column_definition.indexOf("genus_submitted")]);
		item.put("Phonetic", values[column_definition.indexOf("phonetic")]);
		item.put("Accepted_name_id", values[column_definition.indexOf("accepted_name_id")]);
		item.put("Accepted_name_rank", values[column_definition.indexOf("accepted_name_rank")]);
		item.put("Name_matched_accepted_family", values[column_definition.indexOf("name_matched_accepted_family")]);
		item.put("Overall_score_order", Integer.parseInt(values[column_definition.indexOf("overall_score_order")]));
		item.put("Highertaxa_score_order", Integer.parseInt(values[column_definition.indexOf("highertaxa_score_order")]));
		item.put("Source_constrain_on_order", Integer.parseInt(values[column_definition.indexOf("source_constrain_on_order")]));
		item.put("Source_constrain_off_order", Integer.parseInt(values[column_definition.indexOf("source_constrain_off_order")]));
		item.put("Source", stringToJSONArray(values[column_definition.indexOf("source")]));
		item.put("nsources", Integer.parseInt(values[column_definition.indexOf("nsources")]));
		item.put("Sort_override", values[column_definition.indexOf("sort_override")]);
		item.put("Accepted_name_lsid", values[column_definition.indexOf("accepted_name_lsid")]);
		item.put("Sort_override", values[column_definition.indexOf("sort_override")]);

		if(job.containsId()){
			item.put("user_id", values[column_definition.indexOf("user_id")]);
		}



		return item;
	}



	/**
	 * Creates the output file available for download according to the settings passed on.
	 * 
	 * 
	 * 
	 * @param output_folder Base tnrs data folder.
	 * @param settings
	 * @throws Exception
	 */

	public void createFileForDownload(String output_folder, JSONObject settings) throws Exception {


		String type = settings.getString("type");

		boolean dirty = settings.getBoolean("dirty");
		boolean sources = settings.getBoolean("sources");
		boolean taxonomic = settings.getBoolean("taxonomic");

		if(type.equals("Simple")){
			createSimpleFileForDownload(output_folder,settings.getString("mode"),dirty,sources,taxonomic);
		}else{
			createDetailedFileForDownload(output_folder,settings.getString("mode"),dirty,sources,taxonomic);
		}
	}

	/**
	 * Generates a "Simple" formatted output file for download.
	 * 
	 * 
	 * @param output_folder base tnrs folder
	 * @param type indicator for either All or Best names to be included in the output.
	 * @throws Exception
	 */


	private void createSimpleFileForDownload(String output_folder,String type,boolean isDirty, boolean sources, boolean taxonomic) throws Exception{


		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(workingCopy), "UTF-8"));
		rd.readLine();

		BufferedWriter wr = new BufferedWriter(new FileWriter(output_folder+"csv"+job.getRequest().getId()+".csv"));

		wr.write("Name_submitted"+"\t"+"Name_matched"+"\t"+"Author_matched"+"\t"+"Overall_score"+"\t"+"Taxonomic_status"+"\t"+"Accepted_name"+"\t"+"Accepted_author"+"\t"+"Accepted_family"+"\t"+"Source"+"\t"+"Warnings"+"\t"+"Accepted_name_lsid");

		if(job.containsId()){
			wr.write("\t"+"user_id");
		}

		wr.write("\n");

		String line="";


		while(true){
			line =rd.readLine();
			System.out.println("Reading input "+line);
			if(line==null) break;

			String[] values = line.split("\t",-1);

			if(isDirty) {
				if(!filter.filter(values, sources, taxonomic) && type.equalsIgnoreCase("best")){
					continue;
				}
				if(filter.filter(values, sources, taxonomic)) {
					values[column_definition.indexOf("selected")]="true";
				}
			}else {
				if(!values[column_definition.indexOf("selected")].equals("true") && type.equalsIgnoreCase("best")){
					continue;
				}

			}
			wr.write(values[column_definition.indexOf("name_submitted")]+"\t"+values[column_definition.indexOf("name_matched")]+"\t"+values[column_definition.indexOf("author_matched")]+"\t"+values[column_definition.indexOf("overall_score")]+"\t"+values[column_definition.indexOf("taxonomic_status")]+"\t"+values[column_definition.indexOf("accepted_name")]+"\t"+values[column_definition.indexOf("accepted_name_author")]+"\t"+values[column_definition.indexOf("accepted_family")]+"\t"+values[column_definition.indexOf("source")]+"\t"+getFlagText(values[column_definition.indexOf("warnings")])+"\t"+values[column_definition.indexOf("accepted_name_lsid")]);

			if(job.containsId()){
				wr.write("\t"+values[column_definition.indexOf("user_id")]);
			}
			wr.write("\n");
		}

		rd.close();
		wr.close();
	}


	/**
	 * Defines whether the value is selected depending on the values of sources and taxonomic ordering
	 * 
	 * 
	 * 
	 * 
	 */


	/***
	 * Generates a "Detailed" formatted output file for download.
	 * 
	 * 
	 * 
	 * @param output_folder Base TNRS data folder.
	 * @param type Indicator of whether ALL or BEST names will be included in the output.
	 * @throws Exception
	 */

	private void createDetailedFileForDownload(String output_folder, String type,boolean isDirty, boolean sources,boolean taxonomic) throws Exception{

		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(workingCopy), "UTF-8"));
		rd.readLine();
		BufferedWriter wr = new BufferedWriter(new FileWriter(output_folder+"csv"+job.getRequest().getId()+".csv"));

		wr.write("Name_number"+"\t"+
				"Name_submitted"+"\t"+
				"Overall_score"+"\t"+
				"Name_matched"+"\t"+
				"Name_matched_rank"+"\t"+
				"Name_score"+"\t"+
				"Name_matched_author"+"\t"+
				"Name_matched_url"+"\t"+
				"Author_matched"+"\t"+
				"Author_score"+"\t"+
				"Family_matched"+"\t"+
				"Family_score"+"\t"+
				"Genus_matched"+"\t"+
				"Genus_score"+"\t"+
				"Specific_epithet_matched"+"\t"+
				"Specific_epithet_score"+"\t"+
				"Infraspecific_rank"+"\t"+
				"Infraspecific_epithet_matched"+"\t"+
				"Infraspecific_epithet_score"+"\t"+
				"Infraspecific_rank_2"+"\t"+
				"Infraspecific_epithet_2_matched"+"\t"+
				"Infraspecific_epithet_2_score"+"\t"+
				"Annotations"+"\t"+
				"Unmatched_terms"+"\t"+
				"Taxonomic_status"+"\t"+
				"Accepted_name"+"\t"+
				"Accepted_name_author"+"\t"+
				"Accepted_name_rank"+"\t"+
				"Accepted_name_url"+"\t"+
				"Accepted_species"+"\t"+
				"Accepted_family"+"\t"+
				"Selected"+"\t"+
				"Source"+"\t"+
				"Warnings"+"\t"+
				"Accepted_name_lsid"
				);

		if(job.containsId()){
			wr.write("\t"+"user_id");
		}

		wr.write("\n"); 


		String line="";


		while(true){
			line= rd.readLine();
			if(line==null) break;

			String[] values= line.split("\t",-1);

			if(isDirty) {
				if(!filter.filter(values, sources, taxonomic) && type.equalsIgnoreCase("best")){
					continue;
				}

				if(filter.filter(values, sources, taxonomic)) {
					values[column_definition.indexOf("selected")]="true";
				}
			}else {

				if(!values[column_definition.indexOf("selected")].equals("true") && type.equalsIgnoreCase("best")){
					continue;
				}

			}

			wr.write(values[column_definition.indexOf("ngroup")]+"\t"+
					values[column_definition.indexOf("name_submitted")]+"\t"+
					values[column_definition.indexOf("overall_score")]+"\t"+
					values[column_definition.indexOf("name_matched")]+"\t"+
					values[column_definition.indexOf("name_matched_rank")]+"\t"+
					values[column_definition.indexOf("name_score")]+"\t"+
					values[column_definition.indexOf("canonical_author")]+"\t"+
					values[column_definition.indexOf("name_matched_url")]+"\t"+
					values[column_definition.indexOf("author_matched")]+"\t"+
					values[column_definition.indexOf("author_score")]+"\t"+
					values[column_definition.indexOf("family_matched")]+"\t"+
					values[column_definition.indexOf("family_score")]+"\t"+
					values[column_definition.indexOf("genus_matched")]+"\t"+
					values[column_definition.indexOf("genus_score")]+"\t"+
					values[column_definition.indexOf("specific_epithet_matched")]+"\t"+
					values[column_definition.indexOf("specific_epithet_score")]+"\t"+
					values[column_definition.indexOf("infraspecific_rank")]+"\t"+
					values[column_definition.indexOf("infraspecific_epithet_matched")]+"\t"+
					values[column_definition.indexOf("infraspecific_epithet_score")]+"\t"+
					values[column_definition.indexOf("infraspecific_rank_2")]+"\t"+
					values[column_definition.indexOf("infraspecific_epithet_2_matched")]+"\t"+
					values[column_definition.indexOf("infraspecific_epithet_2_score")]+"\t"+
					values[column_definition.indexOf("annotations")]+"\t"+
					values[column_definition.indexOf("unmatched_terms")].replace("\"","\"\"")+"\t"+
					values[column_definition.indexOf("taxonomic_status")]+"\t"+
					values[column_definition.indexOf("accepted_name")]+"\t"+
					values[column_definition.indexOf("accepted_name_author")]+"\t"+
					values[column_definition.indexOf("accepted_name_rank")]+"\t"+
					values[column_definition.indexOf("accepted_name_url")]+"\t"+
					values[column_definition.indexOf("accepted_species")]+"\t"+
					values[column_definition.indexOf("accepted_name_family")]+"\t"+
					values[column_definition.indexOf("selected")]+"\t"+
					values[column_definition.indexOf("source")]+"\t"+
					getFlagText(values[column_definition.indexOf("warnings")])+"\t"+
					values[column_definition.indexOf("accepted_name_lsid")]	
					);

			if(job.containsId()){
				wr.write("\t"+values[column_definition.indexOf("user_id")]);
			}

			wr.write("\n");

		}



		wr.close();
		rd.close();
	}


}
