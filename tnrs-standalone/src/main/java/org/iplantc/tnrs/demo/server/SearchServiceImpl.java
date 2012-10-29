package org.iplantc.tnrs.demo.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.iplantc.tnrs.demo.client.SearchService;
import org.iplantc.tnrs.demo.shared.BeanTNRSEntry;
import org.iplantc.tnrs.demo.shared.BeanTnrsParsingEntry;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

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
	public String doSearch(String input,String sensitivity) throws IllegalArgumentException
	{
		Logger rootLogger = Logger.getLogger("ConsoleLogHandler");
		rootLogger.log(Level.SEVERE,"To send: "+input);

		try
		{	




			JSONObject json_input =(JSONObject) JSONSerializer.toJSON(input);
			JSONObject info = new JSONObject();

			info.put("email", "tnrs@lka5jjs.orv");
			info.put("institution", "");
			info.put("name", "");
			info.put("noemail","l");
			info.put("sensitivity", sensitivity);
			info.put("has_id", "false");
			info.put("sources", json_input.getString("sources"));
			info.put("taxonomic", json_input.getBoolean("taxonomic"));
			info.put("classification", json_input.getString("classification"));
			info.put("match_to_rank", json_input.getString("match_to_rank"));
			System.out.println("--"+info.toString());
			byte[] fileContents = json_input.getString("names").getBytes("UTF-8");

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

			GZIPOutputStream gzipStr = new GZIPOutputStream(byteArray);

			gzipStr.write(fileContents);
			gzipStr.close();

			ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();

			Base64OutputStream base64 = new Base64OutputStream(byteArray2);

			base64.write(byteArray.toByteArray());
			base64.close();

			String value = new String(byteArray2.toByteArray());
			System.out.println(value);
			info.put("upload", value);
			info.put("file_name", "none");
			info.put("type", json_input.getString("type"));

			HttpClient client = new HttpClient();

			PostMethod post = new PostMethod("http://"+servicesHost+"/tnrs-svc/upload");
			rootLogger.log(Level.SEVERE,"To send: "+info.toString());
			post.setRequestEntity(new StringRequestEntity(info.toString(),"application/json","UTF-8"));
			client.executeMethod(post);


			return post.getResponseBodyAsString();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Execution failed.", e);
		}


	}

	@Override
	public String getJobInfoUrl(String json) throws IllegalArgumentException{
		try{
			HttpClient client = new HttpClient();

			PostMethod post = new PostMethod("http://"+servicesHost+"/tnrs-svc/jobinfo");

			post.setRequestEntity(new StringRequestEntity(json,"application/json","UTF-8"));
			client.executeMethod(post);


			return post.getResponseBodyAsString();

		}catch( Exception ex){
			return ex.getMessage();
		}

	}



	@SuppressWarnings("unchecked")
	@Override
	public BasePagingLoadResult<BeanTnrsParsingEntry> getRemoteParsingData(final PagingLoadConfig config,String jsons)  {
		try{
			
			JSONObject info = new JSONObject();
			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);
			


			HttpSession session = this.getThreadLocalRequest().getSession();
			System.out.println(session.getId());
			if(session.getMaxInactiveInterval()!=10800){
				session.setMaxInactiveInterval(10800);
			}

			info.put("session_id", session.getId());

			info.put("key", json.getString("key"));
			info.put("start", config.getOffset());
			info.put("how_many", 100);
			info.put("email",json.getString("email"));
			info.put("first", json.getBoolean("first"));
			String result="";
			System.out.println(json.toString());
			try {
				URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/retrievedata", info.toString());

				result = retrieveResult(connection);

			}catch(Exception ex){
				ex.printStackTrace();
			}
			System.out.println("["+result+"]");

			JSONObject res = (JSONObject)JSONSerializer.toJSON(result.trim().replace("\n", ""));

			ArrayList<BeanTnrsParsingEntry> entries = new ArrayList<BeanTnrsParsingEntry>();

			JSONArray items = res.getJSONArray("items");

			for(int i=0; i < items.size(); i++){
				BeanTnrsParsingEntry entry = new BeanTnrsParsingEntry();
				JSONObject cur_item = items.getJSONObject(i);

				entry.setSubmittedName(cur_item.getString("Name_submitted"));
				entry.setCannonicalName(cur_item.getString("Canonical_name"));
				entry.setAuthor(cur_item.getString("Author"));
				entry.setFamily(cur_item.getString("Family"));
				entry.setGenus(cur_item.getString("Genus"));
				entry.setSpecies(cur_item.getString("Specific_epithet"));
				entry.setInfraSpecificEpithet1(cur_item.getString("Infraspecific_epithet"));
				entry.setInfraSpecificEpithet1_rank(cur_item.getString("Infraspecific_rank"));
				entry.setInfraSpecificEpithet2(cur_item.getString("Infraspecific_epithet_2"));
				entry.setInfraSpecificEpithet2_rank(cur_item.getString("Infraspecific_rank_2"));
				entry.setAnnotations(cur_item.getString("Annotations"));
				entry.setUnmatched(cur_item.getString("Unmatched_terms"));
				entry.setTaxonName(cur_item.getString("Taxon_name"));

				entries.add(entry);
			}

			return new BasePagingLoadResult<BeanTnrsParsingEntry>(entries, config.getOffset(), res.getInt("total"));
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public BasePagingLoadResult<BeanTNRSEntry> getRemoteData(final PagingLoadConfig config,String jsons)  {
		Logger rootLogger = Logger.getLogger("ConsoleLogHandler");
		rootLogger.log(Level.SEVERE,"getRemoteData: "+jsons);

		JSONObject info = new JSONObject();
		JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

		HttpSession session = this.getThreadLocalRequest().getSession();
		if(session.getMaxInactiveInterval()!=10800){
			session.setMaxInactiveInterval(10800);
		}

		info.put("session_id", session.getId());
		info.put("key", json.getString("key"));
		info.put("start", config.getOffset());
		info.put("how_many", config.getLimit());
		info.put("email",json.getString("email"));
		info.put("taxonomic_constraint", json.getBoolean("taxonomic_constraint"));
		info.put("source_sorting", json.getBoolean("source_sorting"));
		info.put("first", json.getBoolean("first"));
		String result="";
		//System.out.println(info.toString());
		try {
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/retrievedata", info.toString());

			result = retrieveResult(connection);

		}catch(Exception ex){

		}
		System.out.println(result);

		JSONObject res = (JSONObject)JSONSerializer.toJSON(result.trim().replace("\n", ""));

		ArrayList<BeanTNRSEntry> entries = new ArrayList<BeanTNRSEntry>();


		JSONArray array = res.getJSONArray("items");

		for(int i=0; i < array.size(); i++) {
			BeanTNRSEntry entry = new BeanTNRSEntry();
			JSONObject item = array.getJSONObject(i);

			//entry.setEntryId(i);
			entry.setGroup(item.getLong("group"));
			entry.setSubmitted(item.getString("Name_submitted").replace("\\\"", "\""));

			JSONArray name_matched_urls = item.getJSONArray("Name_matched_url");

			String url =name_matched_urls.getString(0);

			for(int k=1; k < name_matched_urls.size(); k++){
				url+=";"+name_matched_urls.getString(k);;
			}

			entry.setUrl(url);
			entry.setScientific( item.getString("Name_matched"));
			entry.setScientificScore(item.getString("Name_score"));
			entry.setAuthorAttributed(item.getString("Canonical_author"));
			entry.setFamily(item.getString("Name_matched_accepted_family"));
//			entry.setFamily("");
			entry.setGenus(item.getString("Genus_matched"));
			entry.setGenusScore(item.getString("Genus_score"));
			entry.setEpithet(item.getString("Specific_epithet_matched"));
			entry.setEpithetScore(item.getString("Specific_epithet_score"));
			entry.setAuthor(item.getString("Author_matched"));
			entry.setAuthorScore(item.getString("Author_score"));
			entry.setAnnotation(item.getString("Annotations"));
			entry.setUnmatched(item.getString("Unmatched_terms").replace("\\\"", "\""));
			entry.setOverall(item.getString("Overall_score"));
			entry.setSelected( item.getBoolean("selected"));
			entry.setMatchedFamily(item.getString("Family_matched"));
			entry.setMatchedFamilyScore(item.getString("Family_score"));
			entry.setSpeciesMatched(item.getString("Specific_epithet_matched"));
			entry.setSpeciesMatchedScore(item.getString("Specific_epithet_score"));
			entry.setInfraspecific1Rank(item.getString("Infraspecific_rank"));
			entry.setInfraspecific1Epithet(item.getString("Infraspecific_epithet_matched"));
			entry.setInfraspecific1EpithetScore(item.getString("Infraspecific_epithet_score"));
			entry.setInfraspecific2Rank(item.getString("Infraspecific_rank_2"));
			entry.setInfraspecific2Epithet(item.getString("Infraspecific_epithet_2_matched"));
			entry.setInfraspecific2EpithetScore(item.getString("Infraspecific_epithet_2_score"));
			entry.setAcceptance(item.getString("Taxonomic_status"));
			entry.setSubmittedFamily(item.getString("Family_submitted"));
			entry.setAcceptedName(item.getString("Accepted_name"));
			JSONArray accepted_urls = item.getJSONArray("Accepted_name_url");

			String accepted_name_urls = accepted_urls.getString(0);

			for(int k=1; k< accepted_urls.size(); k++){
				accepted_name_urls+=";"+accepted_urls.getString(k);;
			}

			entry.setAcceptedNameUrl(accepted_name_urls);
			entry.setAcceptedAuthor(item.getString("Accepted_name_author"));
			entry.setGroupSize(item.getInt("groupSize"));
			entry.setFlag(item.getInt("Warnings"));
			entry.setAcceptedSpecies(item.getString("Accepted_species"));
			entry.setNameMatchedRank(item.getString("Name_matched_rank"));
			entry.setAcceptedNameFamily(item.getString("Accepted_name_family"));

			JSONArray sources = item.getJSONArray("Source");

			String source =sources.getString(0);

			for(int k=1; k< sources.size(); k++){
				source+=";"+sources.getString(k);;
			}
			entry.setSource(source);
			entry.setNumber_sources(item.getInt("nsources"));
			entries.add(entry);
		}


		int total = res.getInt("total");
		//System.out.println(entries.toString());
		return new BasePagingLoadResult<BeanTNRSEntry>(entries, config.getOffset(), total);


	}

	public String parseNamesOnly(String nameList) {
		String ret="";


		try {
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/parseNames",nameList);

			ret =retrieveResult(connection);
		}catch(Exception ex) {
			throw new IllegalArgumentException("Execution failed.", ex);
		}

		return ret;

	}


	public String updateGroup(String jsonParams)  {
		String ret="";

		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonParams);

		HttpSession session = this.getThreadLocalRequest().getSession();
		System.out.println(session.getId());
		if(session.getMaxInactiveInterval()!=10800){
			session.setMaxInactiveInterval(10800);
		}

		json.put("session_id", session.getId());

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/updategroup", json.toString());  //batch server
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
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
		HttpSession session = this.getThreadLocalRequest().getSession();
		System.out.println(session.getId());
		if(session.getMaxInactiveInterval()!=10800){
			session.setMaxInactiveInterval(10800);
		}
		JSONObject json = (JSONObject)JSONSerializer.toJSON(input);
		json.put("session_id", session.getId());

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/group", json.toString());  //batch server
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

		JSONObject json = (JSONObject) JSONSerializer.toJSON(options);
		HttpSession session = this.getThreadLocalRequest().getSession();
		System.out.println(session.getId());
		if(session.getMaxInactiveInterval()!=10800){
			session.setMaxInactiveInterval(10800);
		}
		json.put("session_id", session.getId());

		try
		{
			URLConnection connection = update("http://"+servicesHost+"/tnrs-svc/download", json.toString()); //batch server
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;


	}


	@Override
	protected SerializationPolicy doGetSerializationPolicy(
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		//get the base url from the header instead of the body this way 
		//apache reverse proxy with rewrite on the header can work
		String moduleBaseURLHdr = request.getHeader("X-GWT-Module-Base");

		if(moduleBaseURLHdr != null){
			moduleBaseURL = moduleBaseURLHdr;
		}



		return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
	}


	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		// The serialization policy path depends only by context path
		String contextPath = request.getContextPath();
		SerializationPolicy serializationPolicy = null;
		String contextRelativePath = contextPath + "/";
		String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(contextRelativePath
				+ strongName);

		servlet.log(moduleBaseURL);
		// Open the RPC resource file and read its contents.
		InputStream is = servlet.getServletContext().getResourceAsStream(serializationPolicyFilePath);
		try {
			if (is != null) {
				try {
					serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
				} catch (ParseException e) {
					servlet.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
				} catch (IOException e) {
					servlet.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
				}
			} else {
				String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath +
						"' was not found; did you forget to include it in this deployment?";
				servlet.log(message);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore this error
				}
			}
		}

		return serializationPolicy;
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


	@Override
	public String getSources() throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection = new URL("http://"+servicesHost+"/tnrs-svc/sources").openConnection();
			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Execution failed.", e);
		}

		return ret;
	}
}
