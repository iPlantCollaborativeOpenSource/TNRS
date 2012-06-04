/**
 * 
 */
package org.iplantc.tnrs.demo.client.gxt;

import com.extjs.gxt.ui.client.widget.form.Radio;

/**
 * @author raygoza
 *
 */
public class TNRSRadioButton extends Radio{

	private String tnrs_value;
	
	public TNRSRadioButton(){
		super();
	}

	public String getTnrs_value() {
		return tnrs_value;
	}

	public void setTnrs_value(String tnrs_value) {
		this.tnrs_value = tnrs_value;
	}
	
	
}
