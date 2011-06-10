package org.iplantc.tnrs.demo.client;

import org.iplantc.tnrs.demo.client.validation.EmailValidator;
import org.iplantc.tnrs.demo.client.validation.FIleInputValidator;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

public class UploadFilePanel extends LayoutContainer {

	public UploadFilePanel() {

		init();
		compose();

	}


	public void init() {
		setLayout(new FitLayout());
		setHeight(215);

	}


	private Button buildSearchButton()
	{
		submitButton = new Button("Submit List", new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				
				if(form.isValid()) {
					form.submit();
					mask("Submitting Request...");
				}else {
					MessageBox.alert("Error", "There are errors in your submission", null);
				}
				
			}
				});

		
		return submitButton;
	}

	private ToolBar buildButtonBar()
	{
		ToolBar ret = new ToolBar();
		ret.add(new FillToolItem());
		ret.add(buildSearchButton());

		return ret;
	}

	public void compose() {
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setFrame(true);
		form = new FormPanel();
		form.setLabelWidth(100);
		form.setBorders(true);
		form.setFrame(false);
		form.setHeaderVisible(false);
		TextField<String> nameField = new TextField<String>();
		nameField.setFieldLabel("Name");
		
		nameField.setName("name");
		
		form.addListener(Events.Submit, new Listener<FormEvent>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(FormEvent be) {
				//form.clear();
				unmask();
				
				MessageBox.info("","Your request has been successful.<br/> You will soon be receiving a confirmation email.", null);
			}
		});

		TextField<String> institutionField = new TextField<String>();
		institutionField.setFieldLabel("Institution");
		institutionField.setName("institution");
		

		TextField<String> emailField = new TextField<String>();
		emailField.setFieldLabel("Email");
		emailField.setAllowBlank(false);
		emailField.setName("email");
		emailField.setValidator(new EmailValidator());
		
		

		FileUploadField uploadCsv = new FileUploadField();
		uploadCsv.setFieldLabel("CSV name list");
		uploadCsv.setAllowBlank(false);
		uploadCsv.setName("upload");
		uploadCsv.setValidator(new FIleInputValidator());
		uploadCsv.setValidateOnBlur(true);
		

		
		form.setAction("upload");
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);
		
		
		
		form.add(uploadCsv);
		form.add(emailField);
		form.add(nameField);
		form.add(institutionField);
		
		
		
		panel.setBottomComponent(buildButtonBar());
		panel.add(form);
		add(panel);
	}


	private FormPanel form;
	private Button submitButton;

}
