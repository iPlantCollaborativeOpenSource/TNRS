/**
 * 
 */
package org.iplantc.tnrs.demo.server;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author raygoza
 *
 */
public class ConfigurationProperties {

	Properties props = new Properties();
	
	public ConfigurationProperties() throws Exception{
		props.load(new FileInputStream(System.getProperty("user.home")+"/.tnrs/tnrs.properties"));
		
	}
	
	public String get(String property) {
		return props.getProperty(property);
	}
	
	
	public String getProperty(String property) {
		return props.getProperty(property);
	}
}
