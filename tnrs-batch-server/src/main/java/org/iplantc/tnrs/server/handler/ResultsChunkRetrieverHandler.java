package org.iplantc.tnrs.server.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.iplantc.tnrs.server.JobHelper;
import org.iplantc.tnrs.server.MatchingResultsFile;
import org.iplantc.tnrs.server.ParsingResultsFile;
import org.iplantc.tnrs.server.TnrsJob;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResultsChunkRetrieverHandler implements HttpHandler {

	static Logger log = Logger.getLogger(ResultsChunkRetrieverHandler.class);
	
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		try {
			String jsons = IOUtils.toString(new InputStreamReader(arg0.getRequestBody(),"UTF-8"));

			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);
			String email = json.getString("email");
			String key = json.getString("key");
			int start = json.getInt("start");
			int how_many = json.getInt("how_many");
			String session_id = json.getString("session_id");
			boolean order_by_source=true;
			boolean first_request = json.getBoolean("first");
			boolean taxonomic_constraint=true;
			
			if(json.containsKey("source_sorting")) {
				order_by_source = json.getBoolean("source_sorting");
			}
			
			if(json.containsKey("taxonomic_constraint")) {
				taxonomic_constraint = json.getBoolean("taxonomic_constraint");
			}
			
			TnrsJob job = JobHelper.readJobInfo("/tnrs-jobs/",email, key);
			

			JSONObject json_res = new JSONObject();

			if(job.getType()==TnrsJob.PARSING_JOB){

				ParsingResultsFile data = new ParsingResultsFile(job, "/tnrs-jobs/");

				JSONArray results = data.getResultsInterval(start, how_many);

				json_res.put("items", results);
				json_res.put("total",data.getResultsSize());
				
			}else{
				MatchingResultsFile data = new MatchingResultsFile(job, "/tnrs-jobs/",session_id,first_request);

				JSONArray results = data.getResultsInterval(start, how_many, job,taxonomic_constraint,order_by_source);
				
				json_res.put("items", results);
				json_res.put("total",data.getResultsSize());
				data.close();

			}

			String result = json_res.toString();

			arg0.sendResponseHeaders(200, result.getBytes().length);
			arg0.setAttribute("Content-type", "application/json");

			OutputStreamWriter owr = new OutputStreamWriter(arg0.getResponseBody(),"UTF-8");
			BufferedWriter wr = new BufferedWriter(owr);
			wr.write(result);
			wr.flush();
			wr.close();
			


		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ExceptionUtils.getFullStackTrace(ex));
			throw new IOException(ex);

		}

	}


}