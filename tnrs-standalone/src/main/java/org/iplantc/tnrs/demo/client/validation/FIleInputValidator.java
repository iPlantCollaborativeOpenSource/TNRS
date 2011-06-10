package org.iplantc.tnrs.demo.client.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class FIleInputValidator implements Validator {

	
	@Override
	public String validate(Field<?> field, String value) {
		
		if(!value.toLowerCase().endsWith(".csv") && !value.toLowerCase().endsWith(".txt")) {
			return "We only support files with the .csv and .txt file extensions";
		}
		
		
		return null;
	}
}
