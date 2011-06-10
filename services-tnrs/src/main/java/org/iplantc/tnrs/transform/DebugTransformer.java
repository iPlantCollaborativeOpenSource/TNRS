package org.iplantc.tnrs.transform;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class DebugTransformer extends AbstractMessageAwareTransformer{

	
	public Object transform(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		try {
		String ctype = message.getEncoding();
		
		
		
		
		
		return message;
		
		}catch(Exception ex) {
			throw new TransformerException(this, ex);
		}
	}
	
	
	
}
