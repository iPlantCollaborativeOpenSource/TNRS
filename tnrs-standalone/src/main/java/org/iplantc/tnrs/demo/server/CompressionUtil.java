package org.iplantc.tnrs.demo.server;

import org.apache.commons.codec.binary.Base64;

public class CompressionUtil {

	public static String compressString(String value) {
		
		String base64String;
		
		byte[] bytes = value.getBytes();
		
		byte[] base64bytes = Base64.encodeBase64(bytes);
		
		base64String = new String(base64bytes);
		
		return base64String;
	}
	
	
	public static String decompressString(String value) {
		
		String plainString;
		
		byte[] base64bytes = Base64.decodeBase64(value.getBytes());
		
		plainString = new String(Base64.decodeBase64(base64bytes));
		
		
		
		return plainString;
	}
	
	
}
