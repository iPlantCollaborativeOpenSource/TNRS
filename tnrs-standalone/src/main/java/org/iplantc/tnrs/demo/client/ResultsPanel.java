/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import org.iplantc.tnrs.demo.client.validation.BlankValidator;
import org.iplantc.tnrs.demo.client.validation.EmailValidator;
import org.iplantc.tnrs.demo.client.validation.KeyValidator;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author raygoza
 *
 */
public class ResultsPanel extends ContentPanel {

	private FormPanel panel;
	private ClientCommand cmdView;
	private TextField<String> code;
	private ClientCommand cmdProgress;
	TextField<String> email;
	
	private final SearchServiceAsync searchService = GWT.create(SearchService.class);


	public ResultsPanel(ClientCommand cmdView,ClientCommand cmdPercentage) {
		this.cmdView = cmdView;
		cmdProgress = cmdPercentage;
		init();
		compose();

	}

	public void init() {
		setFrame(true);
		setLayout(new FitLayout());
		setHeight(215);
		setHeading("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Enter email used for job submission and key received    ");
		
	}

	public void compose() {

		panel = new FormPanel();

		panel.setBorders(true);
		panel.setLabelWidth(100);
		email = new TextField<String>();
		email.setFieldLabel("Email");
		email.setValidator(new EmailValidator());
		email.setName("email");
		email.setValidateOnBlur(true);

		code = new TextField<String>();
		code.setFieldLabel("Submission key");
		code.setValidator(new KeyValidator());
		code.setValidateOnBlur(true);
		

		panel.setHeaderVisible(false);
		panel.add(email);
		panel.add(code);

		email.setValue("");
		code.setValue("");


		add(panel);
		setBottomComponent(buildButtonBar());
		layout();
	}


	private ToolBar buildButtonBar()
	{
		ToolBar ret = new ToolBar();
		ret.add(new FillToolItem());
		Button retrieve = new Button("Retrieve");

		retrieve.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(panel.isValid()) {
					String res_json = "{\"email\":\""+email.getValue()+"\",\"key\":\""+code.getValue()+"\"}";
					searchService.checkJobStatus(res_json, new AsyncCallback<String>() {

						@Override
						public void onSuccess(String arg0) {
							
							JSONObject json = (JSONObject) JSONParser.parse(arg0);
							
							System.out.println(json.toString());
							
							String type = json.get("type").toString().replace("\"", "");
							
							if(type.equals("complete")) {
								cmdView.execute( email.getValue().trim()+"#"+code.getValue().trim());
							} else if(type.equals("non-existent")) {
								MessageBox.alert("Error", "No job matches the entered data", null);
							}else if(type.equals("incomplete")) {
								
								cmdProgress.execute(json.get("progress").toString());
							}
								
							
						}

						@Override
						public void onFailure(Throwable arg0) {
							MessageBox.wait("", arg0.getMessage(), null);

						}
					});

				}else {
					MessageBox.alert("Error", "There are errors in your submission", null);
				}
			}
		});
		ret.add(retrieve);

		return ret;
	}

}
