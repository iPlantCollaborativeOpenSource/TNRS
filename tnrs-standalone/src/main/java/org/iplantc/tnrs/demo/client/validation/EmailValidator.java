/**
 * 
 */
package org.iplantc.tnrs.demo.client.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * @author raygoza
 *
 */
public class EmailValidator implements Validator{

	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.form.Validator#validate(com.extjs.gxt.ui.client.widget.form.Field, java.lang.String)
	 */
	@Override
	public String validate(Field<?> field, String value) {
		
		String  expression="[^\\s]+@[^\\s]+([.]\\w{2,3})+";  
		 
		
		if (!value.matches(expression)) {
            return "Invalid e-mail address";
        }
    
		
		return null;
	}
	
}
