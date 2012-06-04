/**
 * 
 */
package org.iplantc.tnrs.demo.client.gxt;

import org.iplantc.tnrs.demo.client.RemoteTNRSEditorPanel;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;

/**
 * @author raygoza
 *
 */
public class ConfirmationDialog extends Dialog{

	
	private boolean selected_ok=false;
	private RemoteTNRSEditorPanel panel;
	private CheckMenuItem check_origin;
	/**
	 * 
	 */
	public ConfirmationDialog(String text,RemoteTNRSEditorPanel remote_panel,CheckMenuItem check) {;
		setButtons(Dialog.YESNO);
		
		setSize(300,100);
		this.check_origin=check;
		Label text_label = new Label(text);
		add(text_label);
		panel= remote_panel;
		Button ok = getButtonById(Dialog.YES);
		ok.addListener(Events.OnClick, new Listener<ComponentEvent>() {
			
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				selected_ok=true;
				panel.update();
				panel.setDirty(true);
				hide();
			}
			
		});
		
		Button no = getButtonById(Dialog.NO);
		
		
		no.addListener(Events.OnClick, new Listener<ComponentEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				selected_ok=false;
				panel.setDirty(false);
				hide();
				
			}
		});
		
	}
	
	
	
	public boolean selectedOk(){
		return selected_ok;
	}
	
}
