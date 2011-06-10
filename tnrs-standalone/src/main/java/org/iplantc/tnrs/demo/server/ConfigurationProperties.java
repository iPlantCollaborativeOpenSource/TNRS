/**
 * 
 */
package org.iplantc.tnrs.demo.server;

import java.util.Properties;

/**
 * @author raygoza
 *
 */
public class ConfigurationProperties {

	Properties props = new Properties();
	
	public ConfigurationProperties() throws Exception{
		props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("tnrs.properties"));
		
	}
	
	public String get(String property) {
		return props.getProperty(property);
	}
	
}
