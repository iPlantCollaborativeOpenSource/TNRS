package org.iplantc.tnrs.server;

/**
 * This class manages the communication with the taxamatch service.
 * It abstracts the mode of execution, whether the names are sent in
 * parallel or in series.
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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class TaxamatchInterface {

	private String taxamatch_url;
	private String cmd="cmd=tnrs_taxamatch&str=";
	private URLCodec codec;
	
	
	Vector<TaxamatchThread> threads = new Vector<TaxamatchThread>();

	
	/**
	 * Instantiates an object pointing to the specified 
	 * TAXAMATCH url.
	 * 
	 * 
	 * 
	 * @param taxamatchUrl The base url of the php script used by taxamatch.
	 */
	
	public TaxamatchInterface(String taxamatchUrl){
		taxamatch_url = taxamatchUrl;
		codec = new URLCodec();
	}

	
	
	/**
	 * This method calls the taxamatch service with the set of input
	 * names supplied, this method uses the http POST interface to
	 * the TAXAMATCH service.
	 * 
	 * This implementation orchestrates the name resolution in a parallel format.
	 * Alternative ways of doing this is possible. 
	 * 
	 * @param query The list of names to resolve
	 * @return The results given by TAXAMATCH in JSON format.
	 * @throws Exception
	 */

	public String queryTaxamatch(String query,TnrsJob job) throws Exception{

		String url = taxamatch_url ;

		String[] values = query.split(";");
		System.out.println("Starting taxamatchInterface on: "+url+" with query: "+query);

		TaxamatchThread thread;
		
		Vector<String> names_v = new Vector<String>();

		for(int i=0; i < values.length;i++){
			names_v.add(codec.encode(values[i]));
			if(names_v.size()==10){
				Vector<String> x = new Vector<String>(names_v);
				thread = new TaxamatchThread(x, url,job.getSourcesAsString(),job.getClassification());
				thread.start();
				threads.add(thread);
				names_v.clear();
				names_v = new Vector<String>();;

			}

		}

		if(names_v.size()>0){

			thread = new TaxamatchThread(names_v, url,job.getSourcesAsString(),job.getClassification());
			thread.start();
			threads.add(thread);
		}



		JSONArray data = new JSONArray();

		for(int i=0; i < threads.size();i++){
			threads.elementAt(i).join();

			JSONObject json = threads.elementAt(i).getResults();

			JSONArray results_array = json.getJSONArray("data");

			for(int j=0; j < results_array.size();j++){
				data.add(results_array.get(j));
			}


		}
		threads.clear();
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("data", data);

		
		return result.toString();

	}

	
	public JSONObject getSources() throws Exception{
		HttpClient client = new HttpClient();
		
		PostMethod post = new PostMethod(taxamatch_url);
		
		String cmd ="cmd=sources";
		
		post.setRequestEntity(new StringRequestEntity(cmd, "application/x-www-form-urlencoded", "UTF-8"));
		
		client.executeMethod(post);
		
		JSONObject json = (JSONObject)JSONSerializer.toJSON(post.getResponseBodyAsString());
		
		return json;
	}


}


/**
 * This class executes a chunk of names inside its own thread.
 * Currently the actual POST call to the taxamatch service is in here.
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 *
 */


class TaxamatchThread extends Thread {

	static Logger log = Logger.getLogger(TaxamatchThread.class);
	
	Vector<String> names;
	String url;
	JSONObject results;
	private String cmd="cmd=tnrs_taxamatch&str=";
	private String sources;
	
	public TaxamatchThread(Vector<String> names, String url,String sources,String classification){
		this.names = names;
		this.url = url;
		
		if(!sources.trim().equals("")){
			cmd ="source="+sources+"&"+ cmd;
		}
		
		if(!classification.equals("")) {
		 cmd= "classification="+classification+"&"+cmd;
		}
		
		
		
	}


	public void run(){
		try{
			HttpClient client = new HttpClient();

			PostMethod post = new PostMethod(url);

			String query = names.elementAt(0);

			for(int i=1; i < names.size(); i++){
				query+= ";" + names.elementAt(i);
			}

			System.out.println(cmd+query);
			
			post.setRequestEntity(new StringRequestEntity(cmd+query, "application/x-www-form-urlencoded", "UTF-8"));

			client.executeMethod(post);
			String res = post.getResponseBodyAsString();
			log.info(res);
			results = (JSONObject) JSONSerializer.toJSON(res);

		}catch(Exception ex){
			log.error(ExceptionUtils.getStackTrace(ex));
			ex.printStackTrace();
		}
	}

	public JSONObject getResults(){
		return results;
	}

}















