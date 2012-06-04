package org.iplantc.tnrs.demo.client.panels;

import org.iplantc.tnrs.demo.client.EditSettingsDialog;
import org.iplantc.tnrs.demo.client.TNRSSupportWindow;
import org.iplantc.tnrs.demo.client.validation.EmailValidator;
import org.iplantc.tnrs.demo.client.validation.FIleInputValidator;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Anchor;

public class UploadFilePanel extends LayoutContainer {

	private EditSettingsDialog dlg;
	private TextField<String> sensitivity;
	private TextField<String> type;
	private TextField<String> taxonomic;
	private TextField<String> with_id;
	private TextField<String> sources;
	private TextField<String> classification;
	private TextField<String> match_to_rank;
	
	com.google.gwt.user.client.ui.CheckBox check;
	
	public UploadFilePanel(EditSettingsDialog dialog) {
		dlg = dialog;
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
				mask("Uploading file..");
				if(form.isValid()) {
					JSONObject json = (JSONObject)JSONParser.parseStrict(dlg.getSettings());
					sensitivity.setValue(json.get("sensitivity").toString().replace("\"",""));
					taxonomic.setValue(json.get("taxonomic").toString().replace("\"", ""));
					sources.setValue(json.get("sources").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").toLowerCase());
					type.setValue(json.get("mode").toString());
					with_id.setValue(check.getValue().toString());
					classification.setValue(json.get("classification").toString().replace("\"",""));
					match_to_rank.setValue(json.get("match_to_rank").toString().replace("\"", ""));
					mask("Uploading your file...");
					
					form.submit();
					
				}else {
					MessageBox.alert("Error", "There are errors in your submission", null);
					unmask();
				}
				unmask();
			}
				});

		
		return submitButton;
	}

	private ToolBar buildButtonBar()
	{
		ToolBar ret = new ToolBar();
		
		Anchor support = new Anchor("Click here for support");
		support.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				TNRSSupportWindow support = new TNRSSupportWindow();
				
				support.setModal(true);
				support.show();
			}
		});
		LayoutContainer container = new LayoutContainer();
		container.add(support);
		ret.add(container);
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
		nameField.setVisible(false);
		form.addListener(Events.Submit, new Listener<FormEvent>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(FormEvent be) {
				//form.clear();
				unmask();
				
				MessageBox.info("","Your job now is being processed, and there is no need to keep your browser open. You will receive a confirmation email with instructions on how to track the job's progress, and another one after the job has completed with instructions on how to retrieve your results.<br/> Alternatively, you can submit a new job at any time.", null);
			}
		});

		TextField<String> institutionField = new TextField<String>();
		institutionField.setFieldLabel("Institution");
		institutionField.setName("institution");
		institutionField.setVisible(false);

		TextField<String> emailField = new TextField<String>();
		emailField.setFieldLabel("Email");
		emailField.setAllowBlank(false);
		emailField.setName("email");
		emailField.setValidator(new EmailValidator());
		
		

		FileUploadField uploadCsv = new FileUploadField();
		uploadCsv.setFieldLabel("Name list");
		uploadCsv.setAllowBlank(false);
		uploadCsv.setName("upload");
		uploadCsv.setValidator(new FIleInputValidator());
		uploadCsv.setValidateOnBlur(true);
		
		FormData formData = new FormData("100%");
		
		taxonomic = new TextField<String>();
		taxonomic.setName("taxonomic");
		taxonomic.setVisible(false);
		
		sensitivity= new TextField<String>();
		sensitivity.setName("sensitivity");
		sensitivity.setVisible(false);
		
		sources = new TextField<String>();
		sources.setVisible(false);
		sources.setName("sources");
		
		match_to_rank = new TextField<String>();
		match_to_rank.setVisible(false);
		match_to_rank.setName("match_to_rank");
		
		classification = new TextField<String>();
		classification.setVisible(false);
		classification.setName("classification");
		
		type = new TextField<String>();
		type.setName("type");
		type.setVisible(false);
		type.setValue("matching");
		
		
		with_id = new TextField<String>();
		with_id.setName("has_id");
		with_id.setVisible(false);
		
		
		check= new com.google.gwt.user.client.ui.CheckBox();
		check.setName("hasid");
		check.setText(" my file contains an identifier as first column");
		
	
		
		form.setAction("upload");
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);
		
		form.add(sources);
		form.add(taxonomic);
		form.add(classification);
		form.add(with_id);
		form.add(uploadCsv);
		form.add(emailField);
		form.add(nameField);
		form.add(institutionField);
		form.add(check);
		form.add(sensitivity);
		form.add(type);
		form.add(match_to_rank);
		emailCheck = new CheckBox();
		emailCheck.setBoxLabel("Send results by email when finished");
		emailCheck.setName("emailresults");
		emailCheck.setHideLabel(true);
		emailCheck.setVisible(false);
		
		//form.add(emailCheck,formData);
		
		//form.add(id_checkbox,formData);
		
		panel.setBottomComponent(buildButtonBar());
		panel.add(form);
		add(panel);
	}


	private FormPanel form;
	private Button submitButton;
	private CheckBox emailCheck;
}


