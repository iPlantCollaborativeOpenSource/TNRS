package org.iplantc.tnrs.server;

/**
 * This class is responsibe for executing parsing names,
 * currently being handled by the TAXAMATCH service.
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 */




import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;


public class GNIParserInterface {

	String GNIParserURL ="";
	URLCodec url_codec;
	
	public GNIParserInterface(String url){
		GNIParserURL = url;
		url_codec = new URLCodec();
	}
	
	/**
	 * 
	 * This method submits a set of names for parsing and returns the results
	 * given by the TAXAMATCH service.
	 * 
	 * 
	 * @param names A list of new-line "\n" separated names.
	 * @return
	 * @throws Exception
	 */
	
	public JSONObject parseNames(String names) throws Exception{
		
		Vector<String> families = new Vector<String>();
		Vector<String> o_names = new Vector<String>();
		String nameListForParsing="";
		
		String[] list = names.split("\n");
		
		String cmd="cmd=tnrs_taxamatch&source=tropicos&parse_only=true&str=";
		
		nameListForParsing+= url_codec.encode(list[0]);
		
		for(int i=1; i < list.length;i++){
			families.add(list[i]);
			nameListForParsing+= ";" + url_codec.encode(list[i]);
			o_names.add(list[i]);
		}
		
		HttpClient client = new HttpClient();
		
		PostMethod post = new PostMethod(GNIParserURL);
	
		post.setRequestEntity(new StringRequestEntity(cmd+nameListForParsing, "application/x-www-form-urlencoded", "UTF-8"));

		client.executeMethod(post);
		
		
		
		String jsonr = post.getResponseBodyAsString();
		
		
		JSONObject results = convertResults(jsonr,nameListForParsing);
		
		return results;
	}
	
	/***
	 *  Performs a small conversion of the layout in which results are returned
	 *  and the canonical and taxon names are computed comrrectly.
	 * 
	 * @param parsingResults The results in taxamatch format
	 * @param list This is used to attach the original name supplied by the user.
	 * @param families 
	 * @return
	 */
	public JSONObject convertResults(String parsingResults,String list) {
		
		JSONObject json = new JSONObject();
		JSONObject parsed = (JSONObject)JSONSerializer.toJSON(parsingResults);
		JSONArray results = new JSONArray();
		
		
		
		
		JSONArray parsedNames = parsed.getJSONArray("data");
		
		for(int i=0; i < parsedNames.size(); i++) {
			JSONObject cur_name = parsedNames.getJSONArray(i).getJSONObject(0);
			
			String canonical_name="";
			if(cur_name.getString("Genus").trim().equals("")){
				canonical_name = cur_name.getString("Family");
			}else{
				canonical_name = cur_name.getString("Genus") +" "+cur_name.getString("Specific_epithet")+" "+cur_name.getString("Infraspecific_rank")+" "+cur_name.getString("Infraspecific_epithet_2");
			}
			if(cur_name.getString("Author").equals("false")){
				cur_name.remove("Author");
				cur_name.put("Author", "");
			}
			cur_name.put("Canonical_name", canonical_name);
			
			results.add(cur_name);
		}
		
		json.put("parsedNames", results);
		return json;
	}
}
	
	