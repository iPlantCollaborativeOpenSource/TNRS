package org.iplantc.tnrs.server.handler;

import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SupportHandler implements HttpHandler {

	static Logger log = Logger.getLogger(SupportHandler.class);
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		try {
			String jsons = IOUtils.toString(arg0.getRequestBody());

			JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

			Email response = new SimpleEmail();
			response.setHostName("localhost");
			response.setSmtpPort(25);
			response.setFrom("support@iplantcollaborative.org");
			response.setSubject("TNRS support Ticket");
			response.setMsg("TNRS support ticket from: "+json.getString("name")+" ("+json.getString("email")+"). " +
					"\n\n\n" + json.getString("contents")); 
			response.addTo("support@iplantcollaborative.org");
			response.send();
		}catch(Exception ex) {
			log.error(ExceptionUtils.getFullStackTrace(ex));
			throw new IOException(ex);
		}

	}

}

