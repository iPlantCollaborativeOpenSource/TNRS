package org.iplantc.tnrs.server.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.iplantc.tnrs.server.TaxamatchInterface;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SourcesHandler implements HttpHandler{


	@Override
	public void handle(HttpExchange arg0) throws IOException {
		try{
			Properties properties = new Properties();

			properties.load(new FileInputStream(System.getProperty("user.home")+"/.tnrs/tnrs.properties"));

			TaxamatchInterface t_interface = new TaxamatchInterface(properties.getProperty("tnrsUrl"));
			JSONObject json = t_interface.getSources();

			HandlerHelper.writeResponseRequest(arg0, 200, json.toString(),"application/json");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
