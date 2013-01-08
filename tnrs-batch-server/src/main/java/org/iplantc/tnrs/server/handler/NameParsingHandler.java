package org.iplantc.tnrs.server.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NameParsingHandler implements HttpHandler{

public NameParsingHandler() {
		
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
	
	
	}
	
	
	/*
	
	
	
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
		String servicesHost ="http://testbed235.iplantc.org";
		
		String jsonString = IOUtils.toString(exchange.getRequestBody());
		
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonString);
		JSONObject config = json.getJSONObject("config");
		
		String nameList = config.getString("sourceContents");
		
		HttpClient client = new HttpClient();
		
		PostMethod post = new PostMethod(servicesHost+"/biodiversity/parseNames.php");
		
		
		String nameListForParsing = processNames(nameList);
		
		
		
		exchange.sendResponseHeaders(200, results.toString().getBytes().length);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody()),40000);
		wr.write(results.toString());
		wr.close();
		
		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public JSONObject convertResults(String parsingResults,String list) {
		
		JSONObject json = new JSONObject();
		JSONObject parsed = (JSONObject)JSONSerializer.toJSON(parsingResults);
		JSONArray results = new JSONArray();
		
		JSONArray parsedNames = parsed.getJSONArray("parsedNames");
		
		for(int i=0; i < parsedNames.size(); i++) {
			
			JSONObject result = new JSONObject();
			JSONObject cur_name = parsedNames.getJSONObject(i);
			
			JSONObject scientificName = cur_name.getJSONObject("scientificName");
			result.put("submittedName", scientificName.getString("verbatim"));
			if(scientificName.getBoolean("parsed")) {
				result.put("cannonicalName", scientificName.getString("canonical"));
				
				JSONArray detailsArray = scientificName.getJSONArray("details");
				
				JSONObject details = detailsArray.getJSONObject(0);
				
				if(details.has("genus")) {
					JSONObject genus = details.getJSONObject("genus");
					
					result.put("genus", genus.getString("string"));
				}else {
					result.put("genus", "");
				}
				
				if(details.has("species")) {
					JSONObject species = details.getJSONObject("species");
					result.put("species", species.getString("string"));
					if(species.has("authorship")) {
						result.put("author", species.getString("authorship"));
					}else {
						result.put("author", "");
					}
				}else {
					result.put("species", "");
				}
				
				if(details.has("status")) {
					result.put("annotations", details.getString("status"));
				}else {
					result.put("annotations", "");
				}
				//result.put("family", details.optString("family", ""));
			}else {
				result.put("cannonicalName", "");
				result.put("author", "");
				result.put("family", "");
				result.put("genus", "");
				result.put("species", "");
				result.put("infraSpecificEpithet1", "");
				result.put("infraSpecificEpithet2", "");
				result.put("annotations", "");
				result.put("unmatched", "");
			}
			
			
			results.add(result);
		}
		
		json.put("parsedNames", results);
		return json;
	}
	
	
	public String processNames(String nameList) {
		
		String newList ="";
		
		String[] lines = nameList.split("\n");
		
		for(int i=0; i < lines.length; i++) {
			String[] values = lines[i].split(" ");
			String newName="";
			
			
			
			for(int k=0; k < values.length; k++) {
				if(!values[k].toLowerCase().contains("ceae")) {
					newName+=" "+ values[k];
				}
			}
			
			newList += newName.trim() +"\n";
			
		}
		
		
		return newList.trim();
	}*/
	
}
