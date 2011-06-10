package org.iplantc.tnrs.demo.client.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class BlankValidator implements Validator{

	@Override
	public String validate(Field<?> field, String value) {
		if(value.length()==0 || value.trim().replace("null", "").equals("")) {
			return "A valid code is required";
		}
		return null;
	}
	
	
}
