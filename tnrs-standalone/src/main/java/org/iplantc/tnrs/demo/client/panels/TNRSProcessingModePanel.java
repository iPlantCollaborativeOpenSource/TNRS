/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;

import org.iplantc.tnrs.demo.client.EditSettingsDialog;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;

/**
 * @author raygoza
 *
 */
public class TNRSProcessingModePanel extends VerticalPanel{

	
	private RadioGroup options;
	private EditSettingsDialog dialog;
	
	public TNRSProcessingModePanel(EditSettingsDialog dlg){
		
		dialog=dlg;
		init();
	
		
		compose();
		
	}
	
	
	
	public void init(){
		setSize(385, 300);
		setStyleAttribute("backgroundColor", "#F1F1F1");
	}
	
	public void compose(){
		
		
		add(new Label("Select processing mode:"));
		options= new RadioGroup();
		
		Radio parsing = new Radio();
		parsing.setBoxLabel("Parse names only");
		parsing.setId("parsing");
		parsing.addListener(Events.Change, new Listener<ComponentEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				
				dialog.disableMatchinOptionsPanels();
				
			}
		});
		
		Radio matching = new Radio();
		matching.setBoxLabel("Perform name resolution");
		matching.setValue(true);
		matching.setId("matching");
		matching.addListener(Events.Change, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				dialog.enableMatchingOptions();
				
			};
		});
		
		options.add(matching);
		options.add(parsing);
		options.setOrientation(Orientation.VERTICAL);
		
		add(options);
		
		
	}
	
	public String selectedValue(){
		return options.getValue().getId();
	}
	
	
}
