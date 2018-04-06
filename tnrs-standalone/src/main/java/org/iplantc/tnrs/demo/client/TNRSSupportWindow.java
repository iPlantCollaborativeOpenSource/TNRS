/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import org.iplantc.tnrs.demo.client.validation.BlankValidator;
import org.iplantc.tnrs.demo.client.validation.EmailValidator;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * @author raygoza
 *
 */
public class TNRSSupportWindow extends Window {

	
	private FormPanel support_panel;
	
	public TNRSSupportWindow() {
		init();
		compose();
	}
	
	private void init() {
		setSize(350,450);
		setHeadingText("Contact TNRS Support");
	}
	
	private void compose() {
		
		setLayout(new FitLayout());
		ContentPanel panel = new ContentPanel();
		
		support_panel = new FormPanel();
		support_panel.setAction("support");
		support_panel.setMethod(Method.POST);
		
		
		
		TextField<String> email = new TextField<String>();
		email.setValidator(new EmailValidator());
		email.setValidateOnBlur(true);
		email.setFieldLabel("Email");
		email.setName("email");
		
		email.setAllowBlank(false);
		
		
		
		TextField<String> name = new TextField<String>();
		name.setValidateOnBlur(true);
		name.setValidator(new BlankValidator());
		name.setName("name");
		name.setAllowBlank(false);
		name.setFieldLabel("Name");
		
		TextField<String> valid = new TextField<String>();
		valid.setValidateOnBlur(true);
		valid.setValidator(new BlankValidator());
		valid.setName("valid");
		valid.setVisible(false);
		
		TextArea area = new TextArea();
		area.setName("contents");
		area.setValidator(new BlankValidator());
		area.setFieldLabel("Description");
		area.setAllowBlank(false);
		area.setSize(250, 250);
		
		support_panel.setHeight(300);
		support_panel.add(email);
		support_panel.add(name);
		support_panel.add(area);
		support_panel.add(valid);
		support_panel.addListener(Events.Submit, new Listener<ComponentEvent>() {
			
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				unmask();
				MessageBox.info("TNRS support", "Thanks for your feedback.<br/> We will contact you soon if needed.", null);
				hide();
			}
		});
		
		
		support_panel.setBottomComponent(buildButtonBar());
		add(support_panel);
		layout();
		
	}
	
	private ToolBar buildButtonBar()
	{
		ToolBar ret = new ToolBar();
		ret.add(new FillToolItem());
		Button submit = new Button("Send");
		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(support_panel.isValid()) {
					mask("Submitting..");
					support_panel.submit();
				}
				
			}
		});
		
		ret.add(submit);

		return ret;
	}
	
}
