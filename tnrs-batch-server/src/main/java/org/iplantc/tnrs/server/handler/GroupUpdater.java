package org.iplantc.tnrs.server.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GroupUpdater implements HttpHandler {

	
	static Logger log = Logger.getLogger(GroupUpdater.class);
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		try {
			String jsons = IOUtils.toString(arg0.getRequestBody());
			


			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

			String email = json.getString("email");
			String key = json.getString("key");
			String group = json.getString("group");
			String name_id= json.getString("name_id");
			String session_id = json.getString("session_id");
			
			
			File workingCopy = new File("/tmp/"+session_id+key+".csv");

			
			
			
			String temp_filename ="/tmp/"+email.replace("@","-").replace(".", "-")+"result"+key+"temp";


			

			if(!workingCopy.exists()) {
				HandlerHelper.writeResponseRequest(arg0, 500, "No such job exists or your results might have already expired",null);
				return;
			}


			File temp_file = new File(temp_filename);

			FileUtils.copyFile(workingCopy, temp_file);

			FileUtils.forceDelete(workingCopy);

			workingCopy = new File("/tmp/"+session_id+key+".csv");
			BufferedReader rd = new BufferedReader(new FileReader(temp_file),60000);
			BufferedWriter wr = new BufferedWriter(new FileWriter(workingCopy),80000);

			String line ="";

			while(true) {
				line=rd.readLine();
				if(line==null) break;

				
				String[] values = line.split("\t",-1);

				if(values[40].equals(group)) {

					values[42]="false";
					values[47]="1";
					if(values[2].trim().equals(name_id)) {
						values[42]="true";
					}

					wr.write(values[0]);
					for(int i=1; i < values.length;i++) {
						wr.write("\t"+values[i]);
					}

					wr.write("\n");

				}else {
					wr.write(line+"\n");
				}

			}

			wr.close();
			rd.close();


			HandlerHelper.writeResponseRequest(arg0, 200, "", "text/plain");

		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ExceptionUtils.getFullStackTrace(ex));
			throw new IOException(ex);

		}

	}
}