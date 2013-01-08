package org.iplantc.tnrs.transform;

import java.util.Hashtable;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class DownloadPropertiesTransformer extends AbstractMessageAwareTransformer{

	
	@Override
	public Object transform(MuleMessage message, String outputEncoding)
			throws TransformerException {
	
		String http_request = message.getProperty("http.request").toString();
		
		Hashtable<String, String> properties = getMapFromString(http_request);
		
		String file_name=properties.get("name");
		String encoding=properties.get("encoding");
		
		if(encoding.equals("utf8")){
			encoding = "UTF-8";
		}else{
			encoding="UTF-16LE";
		}
		
		
		message.setProperty("Content-type", "application/txt", PropertyScope.OUTBOUND);
		message.setProperty("Content-Disposition", "attachment;filename="+file_name,PropertyScope.OUTBOUND);
		message.setProperty("Charset", encoding, PropertyScope.OUTBOUND);
		message.setProperty("Content-Transfer-Encoding", "binary", PropertyScope.OUTBOUND);
		
		return message;
	}
	
	
	private Hashtable<String, String> getMapFromString(String request){
		
		Hashtable<String, String> properties = new Hashtable<String, String>();
		
		String[] values = request.split("&");
		
		for(int i=0; i < values.length; i++){
			String[] vals = values[i].split("=");
			properties.put(vals[0], vals[1]);
		}
		
		return properties;
		
	}
	
	
	
	
}
