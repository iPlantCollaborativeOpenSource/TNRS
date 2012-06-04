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
public class FileNameInputValidator implements Validator{
	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.form.Validator#validate(com.extjs.gxt.ui.client.widget.form.Field, java.lang.String)
	 */
	@Override
	public String validate(Field<?> field, String value) {
		
		
		
		if(value.trim().equals("") || !value.toLowerCase().contains(".csv")) {
			return "The filename must not be empty and it should contain the .csv file extension";
		}
		
		if(value.contains("#")) {
			return "The '#' character is not supported downloaded file names currently";
		}
		
		return null;
	}

}
