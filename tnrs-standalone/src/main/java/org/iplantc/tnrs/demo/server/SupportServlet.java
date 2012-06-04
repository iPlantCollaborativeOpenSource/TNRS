/**
 * 
 */
package org.iplantc.tnrs.demo.server;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author raygoza
 *
 */
public class SupportServlet extends HttpServlet {


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map params = req.getParameterMap();


		String[] email = (String[]) params.get("email");
		String[] name = (String[]) params.get("name");
		String[] contents = (String[]) params.get("contents");

		if(params.containsKey("valid")){
			try {
				Email response = new SimpleEmail();
				response.setHostName("localhost");
				response.setSmtpPort(25);
				response.setFrom("support@iplantcollaborative.org");
				response.setSubject("TNRS support Ticket");
				response.setMsg("TNRS support ticket from: "+name[0]+" ("+email[0]+"). " +
						"\n\n\n" + contents[0]); 
				response.addTo("support@iplantcollaborative.org");
				response.send();


			}catch(Exception ex) {
				throw new IOException(ex);
			}
		}


	}

}
