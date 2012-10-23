package org.iplantc.tnrs.transform;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import sun.misc.BASE64Encoder;

public class PlainStringToCompressedBASE64Transformer extends AbstractMessageAwareTransformer {



	public Object transform(MuleMessage message, String outputEncoding)
	throws TransformerException {
		try {
			String body = message.getPayloadAsString();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(new GZIPOutputStream(out));
			wr.write(body);
			wr.close();
			//BASE64Encoder encoder = new BASE64Encoder();
			//String compressedBody = encoder.encode(out.toByteArray());

			message.setProperty("Content-Type", "application/text");
		//	message.setPayload(compressedBody);

			return message;

		}catch(Exception ex) {
			throw new TransformerException(this,ex);
		}
	}



}
