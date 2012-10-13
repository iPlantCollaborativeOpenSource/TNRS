package org.iplantc.tnrs.demo.client;



import org.iplantc.tnrs.demo.client.ClientCommand;

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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class TNRSPanel extends ContentPanel
{
	private final SearchServiceAsync searchService = GWT.create(SearchService.class);

	private LocalTNRSEditorPanel pnlEditor;

	private HorizontalPanel pnlTop;
	private VerticalPanel pnlInner;
	private TableData tableData;
	private RemoteTNRSEditorPanel remotePnlEditor;
	private TNRSProgressPanel progress;

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
		pnlInner.setHorizontalAlign(HorizontalAlignment.LEFT);
		pnlInner.setSpacing(10);
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

	private String buildJSON(final String items)
	{
		String ret = "{\"name\":\"\",\"type\":\"TNRS\",\"description\":\"\","
			+ "\"config\":{\"tnrs\":\"1\",\"sourceFilename\":\"\",\"sourceFileId\":\"\",\"emailNotify\":false,\"createFile\":false,";

		ret += "\"sourceContents\": " + format(items);

		ret += "}}";

		return ret;
	}

	private void doSearch(final String items)
	{
		String json = buildJSON(items);

		if(pnlEditor != null)
		{
			pnlInner.remove(pnlEditor);	
		}

		if(remotePnlEditor!=null) {
			pnlInner.remove(remotePnlEditor);
			remotePnlEditor=null;
		}


		System.out.println(json);

		StopWatch.start("search");
		mask("Working...");
		System.out.println(json);
		searchService.doSearch(json, json, new AsyncCallback<String>()
				{
			@Override
			public void onFailure(Throwable caught)
			{
				unmask();
				String err = "An error occurred while "
					+ "attempting to contact the server. Please check your network "
					+ "connection and try again.";

				MessageBox.alert("Error", err, null);
			}

			@Override
			public void onSuccess(String result)
			{

				unmask();
				mask("Formatting results...");
				StopWatch.end("search");

				pnlEditor = new LocalTNRSEditorPanel(searchService, result);


				pnlInner.add(pnlEditor, tableData);
				Element el = XDOM.getElementById("toggle");
				el.setAttribute("flag", "true");
				layout();
				unmask();
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
		enterNames.add(new TNRSSearchPanel(new SearchCommand(), new ClearCommand()));
		tpanel.add(enterNames);
		
		
		TabItem uploadFile = new TabItem("Upload and Submit List");
		uploadFile.add(new UploadFilePanel());
		uploadFile.setStyleAttribute("textDecoration" ,"none");

		TabItem viewres = new TabItem("Retrieve Results");
		viewres.add(new ResultsPanel(new ShowRemoteResults(),new ProgressCommand()));
		viewres.addStyleName("pad-text"); 
		
		
		
		
		
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
				
				
			};
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
			}
		});
		
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
				
				
			}
		});

		tpanel.add(uploadFile);
		tpanel.add(viewres);
		

		pnlTop.add(tpanel);
		pnlTop.add(new TNRSDescriptionPanel());
		pnlTop.setSpacing(10);
		
		
		initTableData();
		pnlInner.add(pnlTop, tableData);

		add(pnlInner);
	}
	


	class SearchCommand implements ClientCommand
	{
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
			if(pnlEditor != null)
			{
				pnlInner.remove(pnlEditor);
				pnlEditor=null;
			}
			if(remotePnlEditor!=null) {
				pnlInner.remove(remotePnlEditor);
				remotePnlEditor=null;
			}
			Element el = XDOM.getElementById("toggle");
			el.setAttribute("flag", "false");

		}

	}

	public void showRemoteGrid(String params) {
		mask("Retrieving results, please wait...");
		if(remotePnlEditor!=null) {
			pnlInner.remove(remotePnlEditor);
		}
		if(progress!=null) {
			pnlInner.remove(progress);
		}
		remotePnlEditor = new RemoteTNRSEditorPanel(searchService, params, params);
		System.out.println(params);
		pnlInner.add(remotePnlEditor, tableData);
		unmask();
		layout();
	}

	public void unmaskFunction() {
		unmask();
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
			
			double percentage = Double.parseDouble(params.trim());
			progress = new TNRSProgressPanel(percentage);
			
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

			el().createChild("<div class=\"iplantc-logolinks\">" +
					"<img src=\"images/logos.jpg\"><br />" +
					"<a href=\"index.html\" >Home</a> &nbsp; &nbsp;" +
					"<a href=\"about.html\" >About</a> &nbsp; &nbsp;" +
					"<a href=\"instructions.html\" >Instructions</a> &nbsp; &nbsp;" + 
					"<a href=\"api.html\" >API/Source Code</a> &nbsp; &nbsp;" +
					"<a href=\"sources.html\" >Sources</a> &nbsp; &nbsp;" + 
					"<a href=\"contributors.html\" >Contributors</a>&nbsp; &nbsp;" +
					"<a href=\"future.html\">Future</a> &nbsp; &nbsp;"+
					"<a href=\"TNRSDemo.html\">TNRS Application</a>"+
			"</div>");	
		}
	}
}