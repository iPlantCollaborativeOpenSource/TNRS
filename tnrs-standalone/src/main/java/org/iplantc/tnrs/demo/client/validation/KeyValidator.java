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
public class KeyValidator implements Validator {

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.form.Validator#validate(com.extjs.gxt.ui.client.widget.form.Field, java.lang.String)
	 */
	@Override
	public String validate(Field<?> field, String value) {
		if(value.length()!=32) {
			return "You have entered an invalid key.";
		}
		return null;
	}
	
}
