package org.iplantc.tnrs.server.request;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class MultipartRequestParser {

	private String separator;
	private HashMap<String, String>  values; 
	
	public MultipartRequestParser(String chunkSeparator) {
		separator = chunkSeparator;
	}
	
	public MultipartRequestParser(HttpExchange exchange) throws Exception {
		
		byte[] data = IOUtils.toByteArray(new BufferedInputStream(exchange.getRequestBody(),2*1048576));

		String content ="";
		UniversalDetector detector = new UniversalDetector(null);

		detector.handleData(data, 0, data.length);

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		System.out.println(encoding);
		if (encoding != null) {
			if(encoding.equals("WINDOWS-1252")){
				encoding="ISO-8859-1";

				ByteArrayOutputStream t = new ByteArrayOutputStream();

				OutputStreamWriter wr = new OutputStreamWriter(t,"UTF-8");

				wr.write(new String(data,encoding));
				wr.close();

				content = t.toString("UTF-8");

			}else {
				content =new String(data,"UTF-8");
			}
		} else {
			content =new String(data,"UTF-8");
		}




		Headers headers = exchange.getRequestHeaders();

		String content_type = headers.get("Content-type").get(0);

		String[] content_keys = content_type.split(";");
		String boundary = content_keys[1].replace("boundary=", "").trim();

		MultipartRequestParser parser = new MultipartRequestParser(boundary);

		values = parser.parseRequest(content); 

		
		
		
	}
	
	public HashMap<String, String> parseRequest(String request) throws Exception{
		
		HashMap<String, String> values = new HashMap<String, String>();
		
		String tag ="none#";
		String value="";
		StringReader body = new StringReader(request);
		
		BufferedReader rd = new BufferedReader(body);
		
		String line ="";
		
		String content_type="";
		
		
		while(true) {
			line = rd.readLine();
			if(line==null) break;
		
			if(line.trim().equals("")) continue;
			if(line.contains(separator)) {
				if(!tag.equals("none#")) {
					values.put(tag, value);
				}
				continue;
			}
			
			if(line.contains("Content-Disposition")) {
				
				
				String[] elements = line.split(";"); 
				
				tag = elements[1].replace("name=", "").replace("\"", "").trim();
				System.out.println(tag);
				if(!line.contains("filename")) {
					rd.readLine();
					value = rd.readLine().trim();
					continue;
				}else {
					line="";
					content_type = rd.readLine();
					continue;
					
				}
			}
			
			value+= line.trim()+"\n";
			
		}

		

		
		
		return values;
	}
	
	
	public HashMap<String, String> getParameters(){
		return values;
	}
	
	
	
	
}
