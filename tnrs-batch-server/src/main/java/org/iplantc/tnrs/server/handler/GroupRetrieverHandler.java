package org.iplantc.tnrs.server.handler;

/**
 * This class is the Handler for when a user requests information
 * for a specific group to be retrieved.
 * 
 * @author Juan Antonio Raygoza Garay
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.iplantc.tnrs.server.JobHelper;
import org.iplantc.tnrs.server.MatchingResultsFile;
import org.iplantc.tnrs.server.TnrsJob;
import org.iplantc.tnrs.server.processing.NameSourceComparator;
import org.iplantc.tnrs.server.processing.TNRSFinalComparator;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GroupRetrieverHandler implements HttpHandler {

	static Logger log = Logger.getLogger(GroupRetrieverHandler.class);


	/**
	 *  The handle method reads the http request and extracts the email, job key and
	 *  group number and subsequently opens the results file (if it exists), and 
	 *  reads until it has read the specified group information. 
	 * 
	 */

	@Override
	@SuppressWarnings("unchecked")
	public void handle(HttpExchange arg0) throws IOException {
		try {
			String jsons = IOUtils.toString(arg0.getRequestBody());

			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

			String email = json.getString("email");
			String key = json.getString("key");
			String group = json.getString("group");
			String session_id = json.getString("session_id");
			boolean source_sorting=true;
			if(json.containsKey("source_sorting")) {
				source_sorting= json.getBoolean("source_sorting");
			}
			boolean taxonomic_constraint = true;
			if(json.containsKey("taxonomic_constraint")) {
				taxonomic_constraint=json.getBoolean("taxonomic_constraint");
			}
			String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;

			File results = new File(filename);

			if(!results.exists()) {
				HandlerHelper.writeResponseRequest(arg0, 500, "The requested job doesn't exist or might have already expired",null);
				return;
			}


			TnrsJob job = JobHelper.readJobInfo("/tnrs-jobs/",email, key);

			MatchingResultsFile data = new MatchingResultsFile(job, "/tnrs-jobs/",session_id,false);

			JSONObject json_res = new JSONObject();

			JSONArray array = data.getGroupInfo(Integer.parseInt(group));

			if(!array.getJSONObject(0).getString("Sort_override").equals("1")) {

				TNRSFinalComparator comparator = new TNRSFinalComparator(source_sorting,taxonomic_constraint);
				Collections.sort(array,comparator);

				correctSelected(array);
			}

			json_res.put("items", array);
			json_res.put("total",array.size());

			data.close();
			String result = json_res.toString();

			arg0.sendResponseHeaders(200, result.getBytes().length);
			arg0.setAttribute("Content-type", "application/json");

			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody()));
			wr.write(result);
			wr.flush();
			wr.close();
		}catch(Exception ex) {
			log.error(ExceptionUtils.getFullStackTrace(ex));
			throw new IOException(ex);

		}

	}

	private void correctSelected(JSONArray array) {

		for(int i=0; i < array.size();i++) {
			array.getJSONObject(i).remove("selected");
			if(i==0) {
				array.getJSONObject(i).put("selected", true);
			}else {
				array.getJSONObject(i).put("selected", false);
			}
		}


	}

}
