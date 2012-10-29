package org.iplantc.tnrs.transform;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class DebugTransformer extends AbstractMessageAwareTransformer{

	
	@Override
	public Object transform(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		try {
		String ctype = message.getOrginalPayload().toString();
		message.setProperty("Content-type", "application/csv", PropertyScope.OUTBOUND);	
		System.out.println(message.toString());
		System.out.println(message.getProperty("http.request"));
		System.out.println(message.getProperty("Content-type"));
		
		
		return message;
		
		}catch(Exception ex) {
			throw new TransformerException(this, ex);
		}
	}
	
	
	
}
