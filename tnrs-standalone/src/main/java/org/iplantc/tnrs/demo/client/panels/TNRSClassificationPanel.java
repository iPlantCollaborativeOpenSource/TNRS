/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;


import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * @author raygoza
 *
 */
public class TNRSClassificationPanel extends VerticalPanel {

	private RadioGroup classification_group;
	private Radio source;
	private Radio apg3;
	
	public TNRSClassificationPanel() {
		init();
		initClassificationGroup();
		compose();
		
	}
	
	
	
	public void init() {
		setSize(385, 500);
		setStyleAttribute("backgroundColor", "#F1F1F1");
	}
	
	public void compose() {
		setLayout(new RowLayout());
		
		add(new Label("<div>Select the source for standardization above genus to family. <br/>Click on the link to view source hierarchy. <br/></div><br/>"));
		
		add(classification_group);
		
	}
	
	private void initClassificationGroup(){
		classification_group = new RadioGroup();
		
		source = new Radio();
		source.setId("");
		source.setBoxLabel("Source Specific");
		source.setHideLabel(true);
		
		
		
		apg3= new Radio();
		apg3.setId("tropicos");
		apg3.setBoxLabel("APGIII");
		apg3.setHideLabel(true);
		apg3.setValue(true);
		classification_group.add(apg3);
		
		Radio ncbi = new Radio();
		ncbi.setId("ncbi");
		ncbi.setBoxLabel("NCBI");
		ncbi.setHideLabel(true);
		ncbi.setValue(false);
		classification_group.setOrientation(Orientation.VERTICAL);
		classification_group.add(ncbi);
		
		classification_group.add(source);
	}
	
	
	public String selectedValue(){
		return classification_group.getValue().getId();
	}
	
	public void update(int selected_sources){
		System.out.println(selected_sources);
		if(selected_sources>1){
			if(source.getValue()){
				apg3.setValue(true);
			}
		source.setEnabled(false);
		}else{
			source.setEnabled(true);
		}
	}
	
	
}
