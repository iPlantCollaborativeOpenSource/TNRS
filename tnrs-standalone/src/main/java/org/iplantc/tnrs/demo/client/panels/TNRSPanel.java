package org.iplantc.tnrs.demo.client.panels;



import org.iplantc.tnrs.demo.client.ClientCommand;
import org.iplantc.tnrs.demo.client.ClientCommandWithOptions;
import org.iplantc.tnrs.demo.client.EditSettingsDialog;
import org.iplantc.tnrs.demo.client.JsonUtil;
import org.iplantc.tnrs.demo.client.NameParsingResultsGrid;
import org.iplantc.tnrs.demo.client.RemoteTNRSEditorPanel;
import org.iplantc.tnrs.demo.client.ResultsPanel;
import org.iplantc.tnrs.demo.client.SearchService;
import org.iplantc.tnrs.demo.client.SearchServiceAsync;
import org.iplantc.tnrs.demo.client.TNRSProgressWindow;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.StopWatch;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;



public class TNRSPanel extends ContentPanel
{
	private final SearchServiceAsync searchService = GWT.create(SearchService.class);
	
	private RemoteTNRSEditorPanel pnlEditor=null;
	private final TNRSProgressWindow progressw = new TNRSProgressWindow();
	private HorizontalPanel pnlTop;
	private VerticalPanel pnlInner;
	private TableData tableData;
	private RemoteTNRSEditorPanel remotePnlEditor=null;
	private TNRSProgressPanel progress=null;
	private TNRSOptionsPanel optionsPanel;
	private NameParsingResultsGrid parsingResults=null;
	private TNRSSearchPanel search;
	private EditSettingsDialog dialog;
	private UploadFilePanel uploadPanel;
	
	public TNRSPanel()
	{
		init();
		
	}

	private void init()
	{			
		setHeaderVisible(false);
		initInnerPanel();
		initTopPanel();
		setScrollMode(Scroll.AUTO);
		setId("tnrs");
		
		TextField<String> flag = new TextField<String>();
		RootPanel.get().add(flag);
		flag.setVisible(false);
		flag.setValue("false");
		flag.setId("toggle");
		Element elt = flag.getElement();

		elt.setAttribute("flag", "false");
	}

	private void initTableData()
	{
		tableData = new TableData();		
		tableData.setWidth(Integer.toString(getWidth()));
		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
	}

	private void initInnerPanel()
	{
		pnlInner = new VerticalPanel();
		pnlInner.setWidth("100%");
		pnlInner.setHeight("100%");
		pnlInner.setHorizontalAlign(HorizontalAlignment.LEFT);
		pnlInner.setSpacing(10);
		pnlInner.setBorders(false);
	}

	private void initTopPanel()
	{
		pnlTop = new HorizontalPanel();
		pnlTop.setSpacing(10);
	}

	private String format(String in)
	{
		final String QUOTES = "\"";

		if(in == null)
		{
			in = "";
		}

		return JsonUtil.escapeNewLine(QUOTES + JsonUtil.escapeQuotes(in) + QUOTES);

	}

	private String buildJSON(final String items,String mode)
	{
		String ret = "{\"name\":\"\",\"type\":\"TNRS\",\"description\":\"\","
				+ "\"config\":{\"tnrs\":\"1\",\"sourceFilename\":\"\",\"sourceFileId\":\"\",\"emailNotify\":false,\"createFile\":false,";
		ret+= "\"sensitivity\":\""+mode+"\",";
		ret += "\"sourceContents\": " + format(items);

		ret += "}}";

		return ret;
	}

	private void doSearch(final String items)
	{
		String json = buildJSON(items,"");

		if(pnlEditor != null)
		{
			pnlInner.remove(pnlEditor);	
			pnlEditor=null;
		}

		if(remotePnlEditor!=null) {
			pnlInner.remove(remotePnlEditor);
			remotePnlEditor=null;
		}
		if(parsingResults!=null){
			pnlInner.remove(parsingResults);
			parsingResults =null;
		}
		
		JSONObject options = (JSONObject)JSONParser.parseStrict(dialog.getSettings());

		

		final String modeS = options.get("mode").toString().replace("\"", "");
		String sensitivity = options.get("sensitivity").toString().replace("\"", "");
		JSONObject json_param= new JSONObject();
		json_param.put("sources", new JSONString(options.get("sources").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").toLowerCase()));
		json_param.put("names",new JSONString(items));
		json_param.put("type", new JSONString(modeS));
		json_param.put("taxonomic", new JSONString(options.get("taxonomic").toString().replace("\"","")));
		json_param.put("classification", new JSONString(options.get("classification").toString().replace("\"", "")));
		json_param.put("match_to_rank", new JSONString(options.get("match_to_rank").toString().replace("\"", "")));
		System.out.println(json);

		StopWatch.start("search");
		mask("Submitting....");
		System.out.println(json);
		progressw.updateProgress(0.01);
		searchService.doSearch(json_param.toString(), sensitivity,new AsyncCallback<String>()
				{
			@Override
			public void onFailure(Throwable caught)
			{
				search.unmask();
				String err = "An error occurred while "
						+ "attempting to contact the server. Please check your network "
						+ "connection and try again.";

				MessageBox.alert("Error", err, null);
			}

			@Override
			public void onSuccess(String result)
			{
				search.unmask();
				StatusTimer t = new StatusTimer(result,modeS);
				t.scheduleRepeating(2000);

			}
				});		

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);

		add(new LogoPanel());

		TabPanel tpanel = new TabPanel();
		tpanel.setWidth(464);
		tpanel.setPlain(true);
		tpanel.setAutoHeight(true);

		TabItem enterNames = new TabItem("Enter List");
		search = new TNRSSearchPanel(new SearchCommand(), new ClearCommand(),new ShowParsingResults(),dialog);
		enterNames.add(search);
		tpanel.add(enterNames);

		enterNames.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if(pnlEditor!=null) {
					pnlEditor.setVisible(true);

				}
				if(remotePnlEditor!=null) {
					remotePnlEditor.setVisible(false);
				}
				if(progress!=null) {
					progress.setVisible(false);
				}
				if(parsingResults!=null){
					parsingResults.setVisible(true);
				}

				pnlInner.layout();
			};
		});

		

		
		TabItem uploadFile = new TabItem("Upload and Submit List");
		
		uploadFile.setStyleAttribute("textDecoration" ,"none");

		TabItem viewres = new TabItem("Retrieve Results");
		viewres.add(new ResultsPanel(new ShowRemoteResults(),new ProgressCommand()));
		viewres.addStyleName("pad-text"); 
		
		viewres.addListener(Events.Select, new Listener<ComponentEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				if(pnlEditor!=null) {
					pnlEditor.setVisible(false);
				}
				if(remotePnlEditor!=null) {
					remotePnlEditor.setVisible(true);
				}else {
					if(progress!=null) {
						progress.setVisible(true);
					}
				}

				if(parsingResults!=null){
					parsingResults.setVisible(false);
				}

			}
		});

		uploadFile.addListener(Events.Select, new Listener<ComponentEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				if(pnlEditor!=null) {
					pnlEditor.setVisible(false);
				}
				if(remotePnlEditor!=null) {
					remotePnlEditor.setVisible(false);
				}
				if(progress!=null) {
					progress.setVisible(false);
				}
				if(parsingResults!=null){
					parsingResults.setVisible(false);
				}
			}
		});
		
		optionsPanel = new TNRSOptionsPanel();
		optionsPanel.setVisible(false);
		pnlTop.add(optionsPanel);
		pnlTop.add(tpanel);
		SettingsSummaryPanel summary_panel = new SettingsSummaryPanel();
		dialog = new EditSettingsDialog(searchService, summary_panel);
		summary_panel.setSettingsDialog(dialog);
		pnlTop.add(summary_panel);
		uploadPanel =  new UploadFilePanel(dialog);
		pnlTop.setSpacing(10);
		uploadFile.add(uploadPanel);
		tpanel.add(uploadFile);
		tpanel.add(viewres);
		initTableData();
		//pnlInner.add(,tableData);
		pnlInner.add(pnlTop, tableData);

		add(pnlInner);
	}



	class StatusTimer extends Timer{

		private String key="";
		final Timer timer = this;
		private String mode;
		/**
		 * 
		 */
		public StatusTimer(String key,String job_type) {
			super();
			mode = job_type;
			this.key= key;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.Timer#run()
		 */
		@Override
		public void run() {
			unmask();
			final JSONObject json = new JSONObject();

			json.put("email", new JSONString("tnrs@lka5jjs.orv"));
			json.put("key", new JSONString(key));


			progressw.setModal(true);
			progressw.show();
			searchService.checkJobStatus(json.toString(), new AsyncCallback<String>() {

				@Override
				public void onSuccess(String arg0) {

					JSONObject obj = (JSONObject) JSONParser.parseStrict(arg0);
					System.out.println(obj.toString());
					System.out.println(key);
					if(obj.get("type").toString().replace("\"", "").equals("incomplete")) {

						String progress = obj.get("progress").toString();
						if(progress.startsWith("100")) progress="99";
						progressw.updateProgress(Double.parseDouble(progress));

					}else if(obj.get("type").toString().replace("\"", "").equals("complete")) {
						cancel();
						progressw.hide();

						if(mode.equals("matching")){  
							
							pnlEditor = new RemoteTNRSEditorPanel(searchService, "tnrs@lka5jjs.orv#"+key,"Entered names result");
							pnlInner.add(pnlEditor,tableData);
							pnlInner.layout();
						}else{
							parsingResults = new NameParsingResultsGrid(searchService, json.toString());
							pnlInner.add(parsingResults,tableData);
							pnlInner.layout();
						}
						
					}else if(obj.get("type").toString().replace("\"", "").equals("failed")){
						progressw.hide();
						MessageBox.alert("Error", "There was an error processing your list.<br/> Please contact support for further help.", null);
						cancel();
					}


				}

				@Override
				public void onFailure(Throwable arg0) {
					MessageBox.alert("", arg0.getMessage(), null);
					stopThis();
					progressw.hide();
				}
			});


		}


		public void stopThis() {
			cancel();
		}

	}

	class SearchCommand implements ClientCommandWithOptions
	{
		String mode;

		public void setOptions(String newMode) {
			mode = newMode;
		}




		@Override
		public void execute(final String params)
		{

			doSearch(params);


		}
	}

	class ClearCommand implements ClientCommand{

		/* (non-Javadoc)
		 * @see org.iplantc.tnrs.demo.client.ClientCommand#execute(java.lang.String)
		 */
		@Override
		public void execute(String params) {

		}

	}

	public void showRemoteGrid(String params) {
		mask("Retrieving results, please wait...");
		if(remotePnlEditor!=null) {
			pnlInner.remove(remotePnlEditor);
			remotePnlEditor=null;
		}
		if(progress!=null) {
			pnlInner.remove(progress);
			progress=null;
		}
		if(parsingResults!=null){
			pnlInner.remove(parsingResults);
			parsingResults=null;
		}
		if(params.contains("matching")){
			remotePnlEditor = new RemoteTNRSEditorPanel(searchService, params,"Submitted file results");
			System.out.println(params);
			pnlInner.add(remotePnlEditor, tableData);
		}else{
			String[] values = params.split("#");
			JSONObject json = new JSONObject();
			json.put("email", new JSONString(values[0]));
			json.put("key", new JSONString(values[1]));
			parsingResults = new NameParsingResultsGrid(searchService, json.toString());
			pnlInner.add(parsingResults, tableData);
			
		}
		unmask();
		layout();
		
	}

	public void unmaskFunction() {
		unmask();
	}

	class ShowParsingResults implements ClientCommand{

		/* (non-Javadoc)
		 * @see org.iplantc.tnrs.demo.client.ClientCommand#execute(java.lang.String)
		 */
		@Override
		public void execute(final String params) {
			if(progress!=null) {
				pnlInner.remove(progress);
				progress=null;
			}

			if(remotePnlEditor!=null) {
				pnlInner.remove(remotePnlEditor);
				remotePnlEditor=null;
			}

			if(parsingResults!=null){
				pnlInner.remove(parsingResults);
				parsingResults=null;
			}

			mask("Parsing...");
			searchService.parseNamesOnly(params, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					
					parsingResults = new NameParsingResultsGrid(searchService, result);
					pnlInner.add(parsingResults, tableData);
					layout();
					unmask();
					System.out.println(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					MessageBox.alert("Error", "There was an error parsing your name list", null);
				}
			});



		}
	}

	class ShowRemoteResults implements ClientCommand{

		/* (non-Javadoc)
		 * @see org.iplantc.tnrs.demo.client.ClientCommand#execute(java.lang.String)
		 */
		@Override
		public void execute(String params) {
			showRemoteGrid(params);
		}

	}

	class UnmaskCommand implements ClientCommand {

		@Override
		public void execute(String params) {
			unmaskFunction();

		}

	}

	class ProgressCommand implements ClientCommand {

		/* (non-Javadoc)
		 * @see org.iplantc.tnrs.demo.client.ClientCommand#execute(java.lang.String)
		 */
		@Override
		public void execute(String params) {
			if(progress!=null) {
				pnlInner.remove(progress);
				progress=null;
			}

			if(remotePnlEditor!=null) {
				pnlInner.remove(remotePnlEditor);
				remotePnlEditor=null;
			}

			
			progress = new TNRSProgressPanel(0.0,true);
			if(params.equals("error")){
				progress.setErrorMessage();
			}else{
				double percentage = Double.parseDouble(params);
				progress.updateProgress(percentage);
			}
			
			
			pnlInner.add(progress, tableData);
			layout();
		}
	}

	class LogoPanel extends VerticalPanel
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void afterRender()
		{
			super.afterRender();
 
			el().createChild("<div class=\"logolinks\" style=\"font-size:17px;\">" +
					"      <a style=\"color:#007559;\"  class=\"home\" href=\"index.html\">Home</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"apps\" href=\"TNRSapp.html\">TNRS Application</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"instructions\" href=\"instructions.html\">Instructions</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"sources\" href=\"sources.html\">Sources</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"about\" href=\"about.html\">About</a> | " + 
					"      <a style=\"color:#007559;\"  class=\"collaborators\" href=\"collaborators.html\">Collaborators</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"features\" href=\"features.html\">Known Issues</a> |" + 
					"      <a style=\"color:#007559;\"  class=\"optimize\" href=\"optimize.html\">Optimize Your Search</a> |" + 
					"      <a style=\"color:#007559;\" class=\"cite\" href=\"how_cite.html\">How to Cite</a> " + 
					"  </div>\n" );	
		}
	}
}