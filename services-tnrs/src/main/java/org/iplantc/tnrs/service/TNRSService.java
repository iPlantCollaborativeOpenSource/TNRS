package org.iplantc.tnrs.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import net.sf.json.JSONObject;

public class TNRSService {


	private String serviceHost;
	private String tnrsUrl;


	public String buildTnrsRequest(String sourceString) throws Exception{

		try {

			String base ="cmd=tnrs_taxamatch&source=tropicos&str=";

			JSONObject json = (JSONObject)JSONSerializer.toJSON(sourceString);

			JSONObject config = json.getJSONObject("config");
			String sourceContents = config.getString("sourceContents");

			String[] names = sourceContents.split("\\n");

			String namelist ="";
			namelist+=URLEncoder.encode(names[0], "UTF-8");

			for(int i=1; i < names.length; i++) {
				namelist+=","+URLEncoder.encode(names[i].replace("\"", ""), "UTF-8");
			}
			System.out.println(base+namelist);

			return base+namelist;

		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;

		}
	}


	public String persistToCSV(String jSONString) throws Exception  {
		try {

			JSONObject json = (JSONObject)JSONSerializer.toJSON(jSONString);


			String id=UUID.randomUUID().toString().replace("-", "");
			String csvName = "csv"+id+".csv";

			FileWriter wr = new FileWriter("/tmp/"+csvName);
			JSONArray items = json.getJSONArray("items");

			String separator="\t";
			Vector<String> header = new Vector<String>(items.getJSONObject(0).keySet());

			wr.write(header.elementAt(0));
			for(int i=1; i < header.size(); i++) {
				wr.write(separator+header.elementAt(i));
			}

			wr.write("\n" );

			for(int i=0; i < items.size(); i++) {
				JSONObject item = items.getJSONObject(i);

				wr.write(item.getString(header.elementAt(0)));

				for(int k=1; k < header.size(); k++) {
					wr.write(separator+item.getString(header.elementAt(k)).replace("\"", ""));
				}

				wr.write("\n");
			}

			wr.close();


			return serviceHost+"getcsv?id="+id;

		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}





	public String executeTNRSJob(String jsonString) throws Exception{
		JSONObject json = (JSONObject)JSONSerializer.toJSON(jsonString);
		JSONObject result = executeTNRSJob(json);
		return result.toString();
	}

	public JSONObject executeTNRSJob(JSONObject json) throws Exception{
		String base ="cmd=tnrs_taxamatch&source=tropicos&str=";

		JSONObject config = json.getJSONObject("config");
		String sourceContents = config.getString("sourceContents");

		String[] names = sourceContents.split("\\n");

		int t = (int) Math.ceil((double)names.length/500.0);
		int u=0;

		JSONObject json_response = null;

		for(int j=0; j < t ; j++) {

			StringBuffer namelist = new StringBuffer();
			namelist.append(URLEncoder.encode(names[u].replace(",", ""), "UTF-8"));
			u++;
			if(u < names.length ) { 
				for(int i=1; i < 500; i++) {

					if(u == names.length) break;
					namelist.append(",");
					namelist.append(URLEncoder.encode(names[u].replace(",", ""), "UTF-8"));
					u++;

				}
			}


			HttpClient client = new HttpClient();

			PostMethod post = new PostMethod(tnrsUrl);

			post.setRequestEntity(new StringRequestEntity(base+namelist.toString(),"application/x-www-form-urlencoded","UTF-8"));

			client.executeMethod(post);


			if(j==0) {
				json_response = (JSONObject) JSONSerializer.toJSON(post.getResponseBodyAsString());
			}else {
				JSONObject tmp = (JSONObject) JSONSerializer.toJSON(post.getResponseBodyAsString());

				JSONArray orig = json_response.getJSONArray("data");
				JSONArray newa = tmp.getJSONArray("data");

				orig.addAll(newa);

			}
			if(u==names.length) break;
		}


		return json_response;

	}


	public String getNameMatchQuery(Map<String, String> params) throws Exception{

		Iterator<String> it = params.keySet().iterator();

		while(it.hasNext()) {
			String key = it.next();

			System.out.println(key +" " + params.get(key));

		}

		String names = params.get("names");

		String values = names.replace(",", "\n");

		JSONObject request = new JSONObject();
		JSONObject config = new JSONObject();

		config.put("sourceContents", URLDecoder.decode(values, "UTF-8"));

		request.put("config", config);
		boolean onlyBest=false;
		if(params.containsKey("retrieve")) {
			String retrieve = params.get("retrieve");
			if(retrieve.equalsIgnoreCase("all")) {
				onlyBest = false; 
			}else {
				onlyBest = true;
			}

		}

		return iTransform(executeTNRSJob(request),onlyBest).toString();
	}


	public String xmlNameMatchQuery(String queryString) throws Exception{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//factory.setValidating( tr )

			Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(queryString.getBytes()));

			Element nameList = (Element)doc.getElementsByTagName("names").item(0);

			NodeList nodes = nameList.getChildNodes();

			String namelist=null;

			for(int i=0; i < nodes.getLength(); i++) {

				Node node= nodes.item(i);

				if(node.getNodeType()==Node.CDATA_SECTION_NODE) {
					namelist = node.getTextContent().trim();
				}
			}



			JSONObject request = new JSONObject();
			JSONObject config = new JSONObject();

			boolean best=false;

			NodeList options = doc.getElementsByTagName("options");
			if(options.getLength()!=0) {
				Node optionsN = options.item(0);

				NodeList optionsChildren = optionsN.getChildNodes();

				for(int i=0; i < optionsChildren.getLength();i++) {
					Node currentNode = optionsChildren.item(i);
					if(currentNode.getNodeType()==Node.ELEMENT_NODE && currentNode.getNodeName().equals("retrieve")) {
						String option = currentNode.getTextContent();
						if(option.equals("best")) best=true;
					}
				}

			}

			config.put("sourceContents", namelist.replace("\"", "\\\""));
			request.put("config", config);

			JSONObject jsonres = iTransform(executeTNRSJob(request),best);

			return jsonres.toString();
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}



	public JSONObject iTransform(JSONObject tnrsOutput,boolean best_only) {

		JSONObject output = new JSONObject();

		JSONArray data = tnrsOutput.getJSONArray("data");

		JSONArray outputArray = new JSONArray();

		for(int i=0; i < data.size(); i++) {

			JSONArray cur_array = data.getJSONArray(i);

			for(int k=0; k < cur_array.size();k++) {
				JSONObject cur_result = cur_array.getJSONObject(k);
				if(best_only) {
					if(k!=0) continue;
				}

				JSONObject item = new JSONObject();

				item.put("group", Integer.toString(i));
				item.put("acceptedName","");
				item.put("acceptedAuthor",cur_result.optString("Accepted_author","").toString().replace("null", ""));
				item.put("nameSubmitted", cur_result.optString("ScientificName_submitted","").toString().replace("null", ""));
				item.put("url", cur_result.optString("NameSourceUrl","").replace("\\", "").toString().replace("null", ""));
				item.put("nameScientific", cur_result.optString("Lowest_scientificName_matched","").toString().replace("null", ""));
				item.put("scientificScore",cur_result.optString("Lowest_sciName_matched_score", "").toString().replace("null", ""));
				item.put("matchedFamily", cur_result.optString("Family_matched", "").toString().replace("null", ""));
				item.put("matchedFamilyScore", cur_result.optString("Family_matched_score", "").toString().replace("null", ""));
				item.put("authorAttributed", cur_result.optString("Canonical_author","").toString().replace("null", ""));
				item.put("family", cur_result.optString("Accepted_family","").toString().replace("null", ""));
				item.put("genus",cur_result.optString("Genus_matched","").toString().replace("null", ""));
				item.put("genusScore",cur_result.optString("Genus_matched_score", "").toString().replace("null", ""));
				item.put("speciesMatched", cur_result.optString("SpecificEpithet_matched", "").toString().replace("null", ""));
				item.put("speciesMatchedScore", cur_result.optString("SpecificEpithet_matched_score", "").toString().replace("null", ""));
				item.put("infraspecific1Rank", cur_result.optString("infraspecific1Rank", "").toString().replace("null", ""));
				item.put("infraspecific1Epithet",  cur_result.optString("infraspecific1Epithet", "").toString().replace("null", ""));
				item.put("infraspecific1EpithetScore",  cur_result.optString("infraspecific1Score", "").toString().replace("null", ""));
				item.put("infraspecific2Rank", cur_result.optString("infraspecific2Rank", "").toString().replace("null", ""));
				item.put("infraspecific2Epithet",  cur_result.optString("infraspecific2Epithet", "").toString().replace("null", ""));
				item.put("infraspecific2EpithetScore",  cur_result.optString("infraspecific2EpithetScore", "").toString().replace("null", ""));
				item.put("epithet", cur_result.optString("SpecificEpithet_matched","").toString().replace("null", ""));
				item.put("epithetScore", cur_result.optString("SpecificEpithet_matched_score","").toString().replace("null", ""));
				item.put("author",cur_result.optString("Author_matched","").toString().replace("null", "") );
				item.put("authorScore",cur_result.optString("Author_matched_score", "").toString().replace("null", ""));
				item.put("annotation", cur_result.optString("Status", "").toString().replace("null", ""));
				item.put("unmatched", cur_result.optString("Unmatched", "").toString().replace("null", ""));
				item.put("overall", cur_result.optString("Overall_match_score", "").toString().replace("null", ""));
				item.put("acceptedName",cur_result.optString("Accepted_name", "").replace("null", ""));

				if(cur_result.optString("Acceptance", "").replace("null", "").trim().equalsIgnoreCase("A")) {
					item.put("acceptance","Accepted" );
				}else if(cur_result.optString("Acceptance", "").replace("null", "").trim().equalsIgnoreCase("S")) {
					item.put("acceptance","Synonym" );
				}else {
					item.put("acceptance","No opinion" );
				}
				item.put("familySubmitted", cur_result.optString("family", "").replace("null", "") );

				if(k==0) {
					item.put("selected", Boolean.TRUE);
				}else {
					item.put("selected", Boolean.FALSE);
				}
				item.put("acceptedNameUrl",  cur_result.optString("Accepted_name_SourceUrl", "").toString().replace("null", ""));

				outputArray.add(item);
			}



		}

		output.put("items", outputArray);
		return output;


	}


	public byte[] getCsvContents(Map<String,String> params) throws Exception{

		try {
			String id= params.get("id").toString();
			byte[] contents = IOUtils.toByteArray(new FileInputStream("/tmp/csv"+id+".csv"));



			ByteArrayOutputStream t = new ByteArrayOutputStream();

			OutputStreamWriter wr = new OutputStreamWriter(t,"UnicodeLittle");

			wr.write(new String(contents,"UTF-8"));
			wr.close();

			String contentsx = t.toString("UnicodeLittle");

			return t.toByteArray();
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}


	/**
	 * @return the serviceHost
	 */
	public String getServiceHost() {
		return serviceHost;
	}


	/**
	 * @param serviceHost the serviceHost to set
	 */
	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}


	/**
	 * @return the tnrsUrl
	 */
	public String getTnrsUrl() {
		return tnrsUrl;
	}


	/**
	 * @param tnrsUrl the tnrsUrl to set
	 */
	public void setTnrsUrl(String tnrsUrl) {
		this.tnrsUrl = tnrsUrl;
	}






}
