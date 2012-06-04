package org.iplantc.tnrs.server.handler;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class HandlerHelper {

	
	public static void writeResponseRequest(HttpExchange exchange, int status, String response_body,String contentType) throws Exception{

		if(contentType!=null) {
			Headers headers = exchange.getResponseHeaders();
			headers.set("Content-Type", contentType);
		}

		if(response_body==null) {
			exchange.sendResponseHeaders(status,0);
			exchange.getResponseBody().close();
			return;
		}



		exchange.sendResponseHeaders(status, response_body.getBytes().length);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody()),40000);
		wr.write(response_body);
		wr.close();


	}
	
}
