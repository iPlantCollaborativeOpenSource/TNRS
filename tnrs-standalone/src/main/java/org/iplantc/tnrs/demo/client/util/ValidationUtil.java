/**
 * 
 */
package org.iplantc.tnrs.demo.client.util;



/**
 * @author raygoza
 *
 */
public class ValidationUtil {

	public boolean isValidEmailAddress(String emailAddress){  
		String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
		return emailAddress.matches(expression);

	}


}
