package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Element;

public class SubmitBatchJobWindow extends Window {

	
	public SubmitBatchJobWindow() {
		
		init();
		
		
	}
	
	
	public void init() {
		
		setSize(500,200);
		
	}
	
	
	@Override
	protected void onRender(Element parent, int pos) {
		// TODO Auto-generated method stub
		super.onRender(parent, pos);
		
		final FormPanel uploadform = new FormPanel();
		
		uploadform.setMethod(FormPanel.Method.POST);
		uploadform.setEncoding(Encoding.MULTIPART);
		uploadform.setAction("http://compson.iplantc.org:14444/uploadfile");
		
		TextField<String> email = new TextField<String>();
		email.setFieldLabel("E-mail");
		email.setName("mail");
		uploadform.add(email);
		
		FileUploadField upfield = new FileUploadField();
		upfield.setAllowBlank(false);
		upfield.setFieldLabel("Name List");
		uploadform.add(upfield);
		
		upfield.setName("file");
		Button btn = new Button("Submit");
		
		btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				uploadform.submit();
				
				mask("Uploading file...");
			}
		});
		
		uploadform.addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {
				
				System.out.println(be.getResultHtml());
				unmask();
			}
			
		});
		
		uploadform.add(btn);
		add(uploadform);
	}
	
	
	
}
