package org.iplantc.tnrs.demo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


import org.apache.commons.io.IOUtils;
import org.iplantc.tnrs.demo.client.BeanTNRSEntry;
import org.iplantc.tnrs.demo.client.SearchService;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService
{
	
	private String servicesHost;
	
	public SearchServiceImpl() throws IllegalArgumentException {
		try {
		ConfigurationProperties props = new ConfigurationProperties();
		servicesHost = props.get("org.iplantc.tnrs.servicesHost");
		}catch(Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	private HttpURLConnection getUrlConnection(String address) throws IOException
	{
		URL url = new URL(address);

		return (HttpURLConnection)url.openConnection();
	}
	
	private URLConnection update(String address, String body) throws IOException
	{
		// make post mode connection
		HttpURLConnection urlc = getUrlConnection(address);
		urlc.setRequestMethod("POST");
		urlc.setDoOutput(true);

		// send post
		OutputStreamWriter outRemote = null;
		try
		{
			outRemote = new OutputStreamWriter(urlc.getOutputStream());
			outRemote.write(body);
			outRemote.flush();
		}
		finally
		{
			if(outRemote != null)
			{
				outRemote.close();
			}
		}

		return urlc;
	}
 
	

	@Override
	public String doSearch(String input) throws IllegalArgumentException
	{
		String ret = "";

		try
		{	
			URLConnection connection = update("http://"+servicesHost+"/tnrsm-svc/submitNames", input);
			connection.setConnectTimeout(0);
			ret = retrieveResult(connection);
			//System.out.println(ret);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
	}

	
	public PagingLoadResult<BeanTNRSEntry> getRemoteData(final PagingLoadConfig config,String jsons) throws IllegalArgumentException {
		try {
			
			JSONObject info = new JSONObject();
			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);
			
			
			info.put("key", json.getString("key"));
			info.put("start", config.getOffset());
			info.put("how_many", config.getLimit());
			info.put("email",json.getString("email"));
			
			System.out.println(info.toString());
			
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/retrievedata", info.toString());
		
			String result = retrieveResult(connection);
		
			System.out.println(result);
			
			JSONObject res = (JSONObject)JSONSerializer.toJSON(result.trim().replace("\n", ""));
			
			ArrayList<BeanTNRSEntry> entries = new ArrayList<BeanTNRSEntry>();
			
			
			JSONArray array = res.getJSONArray("items");
			
			for(int i=0; i < array.size(); i++) {
				BeanTNRSEntry entry = new BeanTNRSEntry();
				JSONObject item = array.getJSONObject(i);
				
				
				//entry.setEntryId(i);
				entry.setGroup(item.getLong("group"));
				entry.setSubmitted(item.getString("nameSubmitted"));
				entry.setUrl(item.getString("url"));
				entry.setScientific( item.getString("nameScientific"));
				entry.setScientificScore(item.getString("scientificScore"));
				entry.setAuthorAttributed(item.getString("authorAttributed"));
				entry.setFamily(item.getString("family"));
				entry.setGenus(item.getString("genus"));
				entry.setGenusScore(item.getString("genusScore"));
				entry.setEpithet(item.getString("epithet"));
				entry.setEpithetScore(item.getString("epithetScore"));
				entry.setAuthor(item.getString("author"));
				entry.setAuthorScore(item.getString("authorScore"));
				entry.setAnnotation(item.getString("annotation"));
				entry.setUnmatched(item.getString("unmatched"));
				entry.setOverall(item.getString("overall"));
				entry.setSelected( item.getBoolean("selected"));
				entry.setMatchedFamily(item.getString("matchedFamily"));
				entry.setMatchedFamilyScore(item.getString("matchedFamilyScore"));
				entry.setSpeciesMatched(item.getString("speciesMatched"));
				entry.setSpeciesMatchedScore(item.getString("speciesMatchedScore"));
				entry.setInfraspecific1Rank(item.getString("infraspecific1Rank"));
				entry.setInfraspecific1Epithet(item.getString("infraspecific1Epithet"));
				entry.setInfraspecific1EpithetScore(item.getString("infraspecific1EpithetScore"));
				entry.setInfraspecific2Rank(item.getString("infraspecific2Rank"));
				entry.setInfraspecific2Epithet(item.getString("infraspecific2Epithet"));
				entry.setInfraspecific2EpithetScore(item.getString("infraspecific2EpithetScore"));
				entry.setAcceptance(item.getString("acceptance"));
				entry.setSubmittedFamily(item.getString("familySubmitted"));
				entry.setAcceptedName(item.getString("acceptedName"));
				entry.setAcceptedNameUrl(item.getString("acceptedNameUrl"));
				entry.setAcceptedAuthor(item.getString("acceptedAuthor"));
				entry.setGroupSize(item.getInt("groupSize"));
				
				entries.add(entry);
			}
			
			
			int total = res.getInt("total");
			
			return new BasePagingLoadResult<BeanTNRSEntry>(entries, config.getOffset(), total);
		
		}catch(Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		}
	}
	
	
	public String updateGroup(String jsonParams)  {
		
		try
		{
			update("http://"+servicesHost+"/tnrs-svc/update_group", jsonParams);  //batch server
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}
		
		return "";
	}
	
	public String checkJobStatus(String jsonString) {
		
		String ret = "";

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/status", jsonString);  //batch server
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
		

		
		
	}
	
	private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException
	{
		String result = IOUtils.toString(new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8")));
		return result;
	}
	
	
	public String getResultsInfo(String email, String key) throws IllegalArgumentException {
		try {
		String json ="{\"email\":\""+email+"\",\"key\":\""+key+"\"}";
		
		
		
		URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/resultinfo",json);
		
		String result = retrieveResult(connection);
		return result;
		}catch(Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	
	public String requestGroupMembers(String input) throws IllegalArgumentException{
		
		
		String ret = "";

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/group", input);  //batch server
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
		

	}
	
	
	public String downloadRemoteResults(String options) throws IllegalArgumentException{
		String ret = "";

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/download", options); //batch server
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
		
		
	}
	
	
	@Override
	public String downloadResults(String input) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrsm-svc/download", input);
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
	}
}
