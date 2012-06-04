package org.iplantc.tnrs.server.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import org.iplantc.tnrs.server.JobHelper;
import org.iplantc.tnrs.server.TnrsJob;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class JobInfoHandler implements HttpHandler {

	private Properties properties;
	
	
	public JobInfoHandler(Properties props) {
		properties = props;
		
	}
	
	
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		try {
		JSONObject json = (JSONObject)JSONSerializer.toJSON(arg0);
		
		String email = json.getString("email");
		String key = json.getString("key");
		
		TnrsJob job = JobHelper.readJobInfo(properties.getProperty("servicesUrl"), email, key);
		
		String uid =UUID.randomUUID().toString().replace("-", "");
		
		File info = new File("/tmp/csv"+uid+".csv");
		
		BufferedWriter wr = new BufferedWriter(new FileWriter(info)); 
		
		
		
		wr.write("E-mail:"+job.getRequest().getEmail()+"\n");
		wr.write("Id: "+ job.getRequest().getId()+"\n");
		wr.write("Job type: "+job.getTypeString()+"\n");
		wr.write("Contains Id: "+job.containsId()+"\n");
		wr.write("Start time :"+job.getSubmissionDate()+"\n");
		wr.write("Finish time :"+job.getFinishedAt()+"\n");
		wr.write("TNRS version :"+job.getTnrs_version()+"\n");
		Vector<String> sources = job.getSources();
		
		wr.write("Sources selected : ["+sources.elementAt(0));
		
		for(int i=1; i < sources.size(); i++) {
			wr.write(" ,"+sources.elementAt(i));
		}
		
		wr.write(" ]\n");
		wr.write("Match threshold: "+job.sensitivity()+"\n");
		wr.write("Classification :" + job.getClassification()+"\n");
		wr.write("Allow partial matches? :"+job.isAllowPartial());
		wr.write("Sort by source: "+json.getString("sortbysource")+"\n");
		wr.write("Constrain by higher taxonomy: "+ json.optString("taxonomic"));
		wr.close();
		
		
		
		
		String url=properties.getProperty("servicesUrl")+"getcsv?id="+uid;

		HandlerHelper.writeResponseRequest(arg0, 200, url,null);
		
		
		}catch(Exception ex) {
			throw new IOException(ex);
		}
	}
	
	
}
