package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.tnrs.demo.client.images.Resources;
import org.iplantc.tnrs.demo.client.util.NumberUtil;
import org.iplantc.tnrs.demo.client.views.Hyperlink;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;



public class RemoteTNRSEditorPanel extends TNRSEditorPanel
{

	private Grid<BeanModel> grid;
	private boolean hasFamilies;
	private PagingLoader<PagingLoadResult<ModelData>>  loader;
	private final SearchServiceAsync searchService;
	
	private RpcProxy<PagingLoadResult<BeanTNRSEntry>> proxy;
	private String key;
	private String email;

	private ContentPanel download_panel;


	/**
	 * Default constructor
	 */
	public RemoteTNRSEditorPanel(final SearchServiceAsync searchService, final String params)
	{
		super();
		this.searchService = searchService;
		
		String[] paramsA = params.split("#");
		key=paramsA[1];
		email=paramsA[0];
		compose();
	}

	

	private ToolBar buildDownloadToolbar()
	{
		ToolBar ret = new ToolBar();



		Button but = new Button("Download results", new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				TNRSDownloadDialog select_download = new TNRSDownloadDialog(new SelectionDownloadEvent(email,key));

				select_download.show();


			}
				});

		but.setIcon( AbstractImagePrototype.create(Resources.INSTANCE.download()));
		but.setBorders(true);
		ret.add(but);
		ret.add(new Html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:blue;'>Submitted file results</span>"));
		ret.add(new FillToolItem());
		ret.setHeight(30);
		ret.setBorders(true);
		return ret;
	}

	@Override
	protected void onRender(Element parent, int index) {

		super.onRender(parent, index);

	}



	private void compose()
	{


		ListStore<BeanModel> store = buildStore();

		final ColumnModel cm = buildColumnModel();

		buildGrid(store, cm);
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSize(1600, 330);
		vpanel.setLayout(new FitLayout());
		vpanel.add(buildDownloadToolbar());
		vpanel.add(grid);
		toolBar = new PagingToolBar(100);
		download_panel = new ContentPanel();
		download_panel.setFrame(false);
		download_panel.setSize(1, 1);
		download_panel.setVisible(false);
		toolBar.bind(loader);

		vpanel.add(toolBar);
		vpanel.add(download_panel);

		add(vpanel);
		unmask();

	}





	private ListStore<BeanModel> buildStore() 
	{
		mask("wait");

		final JSONObject json = new JSONObject();
		json.put("email",new JSONString(email));
		json.put("key", new JSONString(key));

		proxy = new RpcProxy<PagingLoadResult<BeanTNRSEntry>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<BeanTNRSEntry>> callback) {
			
					searchService.getRemoteData((PagingLoadConfig)loadConfig, json.toString(), callback);
				

			}
		};

		BeanModelReader reader = new BeanModelReader();


		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy, reader);  
		ListStore<BeanModel> store = new ListStore<BeanModel>(loader);  

		return store;
	}





	

	

	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();


		config
		.add(buildConfig("submitted", "Name<br/> Submitted", 200,
				HorizontalAlignment.LEFT));

		config.add(buildConfig("scientific", "Name Matched", 380,
				HorizontalAlignment.LEFT, new ScientificNameCellRenderer()));

		config.add(buildConfig("overall", "Overall <br/>Score", 60, HorizontalAlignment.CENTER,
				new OverallCellRenderer()));

		config.add(buildConfig("acceptance","Status", 100, HorizontalAlignment.LEFT));

		config.add(buildConfig("acceptedName", "Accepted Name", 200, HorizontalAlignment.LEFT,new AcceptedNameRenderer()));

		config.add(buildConfig("group", "Details", 60, HorizontalAlignment.CENTER,
				new DetailsCellRenderer()));

		return new ColumnModel(config);

	}



	private void buildGrid(final ListStore<BeanModel> store, final ColumnModel cm)
	{



		grid = new Grid<BeanModel>(store, cm) ;

		store.setSortField("submitted");
		store.setSortDir(SortDir.ASC);
		grid.setLoadMask(true);

		grid.getStore().sort("submitted", SortDir.ASC);
		grid.setBorders(true);		
		grid.getView().setAutoFill(true);
		grid.getView().setShowDirtyCells(true);
		// disallow multi-select
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getView().refresh(true);
		grid.setSize(1157,300);
		grid.mask("Loading...");
		loader.load();
	}





	private class ScientificNameCellRenderer implements GridCellRenderer<BeanModel>
	{
		private String buildCountOutput(BeanModel entry)
		{
			String ret = "";

			int count = Integer.parseInt(entry.get("groupSize").toString());

			count--;
			if(count > 0) {
				ret = " (+" + count + " " + "more" + ")";
			}


			return ret;
		}

		@Override
		public Object render(BeanModel model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid)
		{

			String ret="";

			if(NumberUtil.isDouble(model.get("scientificScore").toString())) {
				String url = model.get("url").toString();

				return new NameMatchedRenderer(url,model.get("scientific").toString() +" "+model.get("authorAttributed").toString(),buildCountOutput(model),grid.getStore(),rowIndex);
			}else {
				ret ="No matches found.";

				return ret;
			}
		}
	}





	

	private String formatPercentage(final String score)
	{
		String ret = ""; // assume failure... if we have no percentage we just return an
		// empty string

		if(NumberUtil.isDouble(score))
		{
			double d = Double.parseDouble(score);

			int percentage = (int)(d * 100.0);
			ret = percentage + "%";
		}else {
			ret="";
		}

		return ret;
	}



	private class DetailsCellRenderer implements GridCellRenderer<BeanModel>
	{
		private void launchDetailsWindow(final BeanModel entry)
		{
			DetailDialog dlg = new DetailDialog(entry);

			dlg.show();


		}



		@Override
		public Object render(final BeanModel model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid)
		{
			final Hyperlink ret = new Hyperlink("Details", "de_tnrs_hyperlink");
			ret.setStyleAttribute("color", "blue");
			ret.setStyleAttribute("text-decoration", "underline");
			// by default, the hyperlinks un-highlight after mouse over. This is
			// undesirable, to the following line has
			// been added as a short term workaround.
			ret.setOnMouseOutStyle("text-decoration", "underline");
			ret.setOnMouseOverStyle("cursor", "pointer");
			ret.addListener(Events.OnClick, new Listener<ComponentEvent>() {



				@Override
				public void handleEvent(ComponentEvent be) {
					launchDetailsWindow(model);

				}
			});



			return ret;
		}
	}

	private class AcceptedNameRenderer implements GridCellRenderer<BeanModel>{

		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {

			String acceptedName = model.get("acceptedName");
			String acceptedAuthor = model.get("acceptedAuthor");
			String acceptedNameUrl = model.get("acceptedNameUrl");
			if(acceptedName!=null && !acceptedName.equals("")) {
				setLayout(new ColumnLayout());
				String prefix =  "<a href='"+acceptedNameUrl+ "' target='_blank'>";
				String suffix =  "</a>&nbsp;&nbsp; ";

				// setup our base link
				String ahref = prefix + acceptedName + " "+acceptedAuthor + suffix;


				Html link = new Html(ahref);
				return link;

			}else {
				return "";
			}

		}

	}

	private class OverallCellRenderer implements GridCellRenderer<BeanModel>
	{
		@Override
		public Object render(BeanModel model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid)
		{
			return formatPercentage(model.get("overall").toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.BoxComponent#onHide()
	 */
	@Override
	protected void onHide() {
		download_panel.setUrl("");
		super.onHide();
	}




	private void doDownload(final String options)
	{
		if(options != null)
		{
			searchService.downloadRemoteResults(options, new AsyncCallback<String>()
					{
				@Override
				public void onFailure(Throwable arg0)
				{
					MessageBox.alert("Error", "File download failed.<br/> "+arg0.getMessage(), null);
				}

				@Override
				public void onSuccess(String result)
				{		
					download_panel.setUrl(result);						
				}
					});
		}
	}






	@Override
	protected void afterRender() {
		grid.getStore().sort("submitted", SortDir.ASC);
		super.afterRender();
	}


	class SelectionUpdatedEvent implements ClientCommand
	{


		public SelectionUpdatedEvent(Integer group)
		{

		}

		@Override
		public void execute(String h)
		{

		}
	}

	class SelectionDownloadEvent implements ClientCommand
	{

		String key;
		String email;

		public SelectionDownloadEvent( String email,String key) {
			this.key= key;
			this.email =email;
		}

		@Override
		public void execute(String options)
		{

			options+="#"+email+"#"+key;
			doDownload(options);

		}
	}

	class NameMatchedRenderer extends LayoutContainer{

		BeanModel entry;

		public NameMatchedRenderer(String url,String name,String countString,final ListStore<BeanModel> page_store,final int rowIndex) {
			setLayout(new ColumnLayout());
			String prefix = (url == null) ? "" : "<a href='" + url + "' target='_blank'>";
			String suffix = (url == null) ? " " : "</a>&nbsp;&nbsp; ";
			entry =page_store.getAt(rowIndex);
			// setup our base link
			String ahref = prefix + name  + suffix;


			Html link = new Html(ahref);
			add(link);

			if(!countString.equals("")) {


				Anchor countLink = new Anchor();
				countLink.setText("     "+countString);
				countLink.setStyleName("linkCursor");

				countLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {

						JSONObject json = new JSONObject();
						json.put("group", new JSONString(entry.get("group").toString()));
						json.put("email",new JSONString(email));
						json.put("key", new JSONString(key));


						searchService.requestGroupMembers(json.toString(), new AsyncCallback<String>() {

							@Override
							public void onSuccess(String json) {
								System.out.println(json);
								ListStore<TNRSEntry> store = new ListStore<TNRSEntry>();

								JSONObject objJson = (JSONObject)JSONParser.parse(json);

								JSONValue val = objJson.get("items");
								JSONArray items = val.isArray();

								JsArray<JsTNRSEntry> jsEntries = JsonUtil.asArrayOf(items.toString());


								for(int i=0; i < jsEntries.length(); i++) {
									TNRSEntry entry = new TNRSEntry(jsEntries.get(i));
									store.add(entry);
								}

								System.out.println(store.getCount());

								if(store != null)
								{
									RemoteTNRSDetailsDialog dlg = new RemoteTNRSDetailsDialog(store, hasFamilies,new SelectionUpdatedEvent(200),false,page_store,rowIndex,email,key);

									dlg.show();						
								}

							}

							@Override
							public void onFailure(Throwable arg0) {

								MessageBox.info("", arg0.getMessage(), null);
							}
						});


					}
				});

				add(countLink);

			}

		}


	}


	class SubmittedRenderer implements GridCellRenderer<BeanModel>{

		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {
			// TODO Auto-generated method stub

			return new Html("<span>"+model.get("submitted").toString()+"</span>");
		}

	}

	class AcceptanceRenderer implements GridCellRenderer<BeanModel>{

		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {
			// TODO Auto-generated method stub
			return model.get("acceptance");
		}

	}

	class DetailsSorter extends StoreSorter<TNRSEntry>{

		@Override
		public int compare(Store<TNRSEntry> store, TNRSEntry m1, TNRSEntry m2,
				String property) {

			if(property.equals("score")) {
				Double s1= Double.parseDouble(m1.getOverall());
				Double s2= Double.parseDouble(m2.getOverall());
				return s1.compareTo(s2);
			}else if(property.equals("submitted")) {
				return m1.getSubmittedName().compareToIgnoreCase(m2.getSubmittedName());
			}

			return super.compare(store, m1, m2, property);
		}
	}
}
