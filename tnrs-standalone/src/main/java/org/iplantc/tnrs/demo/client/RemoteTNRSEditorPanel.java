package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.client.gxt.ConfirmationDialog;
import org.iplantc.tnrs.demo.client.gxt.LinkIcon;
import org.iplantc.tnrs.demo.client.images.Resources;
import org.iplantc.tnrs.demo.client.util.NumberUtil;
import org.iplantc.tnrs.demo.client.views.Hyperlink;
import org.iplantc.tnrs.demo.shared.BeanTNRSEntry;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
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
import com.google.gwt.user.client.ui.HTML;



public class RemoteTNRSEditorPanel extends TNRSEditorPanel
{

	private Grid<BeanModel> grid;
	private boolean hasFamilies;
	private PagingLoader<PagingLoadResult<ModelData>>  loader;
	private final SearchServiceAsync searchService;

	private SplitButton sort;
	private RpcProxy<BasePagingLoadResult<BeanTNRSEntry>> proxy;
	private String key;
	private String email;
	private ContentPanel download_panel;
	private boolean taxonomic;
	private ListStore<BeanModel> store;
	private JSONObject conf;
	private CheckMenuItem sources_n;
	private CheckMenuItem  tax_on;
	private RemoteTNRSEditorPanel panel;
	private boolean dirty=false;
	private Button job_info;

	/**
	 * Default constructor
	 */
	public RemoteTNRSEditorPanel(final SearchServiceAsync searchService, final String params,String message)
	{
		super();
		panel=this;
		this.searchService = searchService;
		String[] paramsA = params.split("#");
		init();
		key=paramsA[1];
		email=paramsA[0];
		this.taxonomic = false;
		compose(message);
		loader.load();
	}

	private void init(){
		sources_n = new CheckMenuItem("Constrain by Source");
		sources_n.setChecked(false);
		tax_on = new CheckMenuItem("Constrain by Higher Taxonomy");
		tax_on.setChecked(false);
	}

	private ToolBar buildDownloadToolbar(String message)
	{
		ToolBar ret = new ToolBar();



		Button but = new Button("Download results", new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				TNRSDownloadDialog select_download = new TNRSDownloadDialog(new SelectionDownloadEvent(email,key,searchService),"matching",dirty,sources_n.isChecked(),tax_on.isChecked());

				select_download.show();


			}
				});

		but.setIcon( AbstractImagePrototype.create(Resources.INSTANCE.download()));
		but.setBorders(true);

		ret.add(buildTaxonomicSortingButtonbutton());
		ret.add(downloadJobInfoButton());
		ret.add(new SeparatorToolItem());
		ret.add(but);

		ret.add(new FillToolItem());

		ret.setHeight(30);
		ret.setBorders(true);
		return ret;
	}

	@Override
	protected void onRender(Element parent, int index) {

		super.onRender(parent, index);

	}

	private SplitButton buildTaxonomicSortingButtonbutton(){

		sort = new SplitButton("Best match settings");
		sort.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.settings()));
		sort.setBorders(true);
		Menu menu = new Menu();

		

		tax_on.addListener(Events.OnClick, new Listener<ComponentEvent>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
			
				ConfirmationDialog dlg= new ConfirmationDialog("All your previous changes will be overridden, are you sure you want to continue?",panel,tax_on);
				dlg.setModal(true);
				dlg.show();
				
				
				
				
			}
		});

		sources_n.addListener(Events.OnClick, new Listener<ComponentEvent>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
				ConfirmationDialog dlg= new ConfirmationDialog("All your previous changes will be overridden, are you sure you want to continue?",panel,sources_n);
				dlg.setModal(true);
				dlg.show();

			}
		});

		

		tax_on.setChecked(false);
		sources_n.setChecked(false);
		menu.add(tax_on);
		menu.add(new SeparatorMenuItem());
		menu.add(sources_n);


		sort.setMenu(menu);

		return sort;
	}
	
	private Button downloadJobInfoButton(){
		
		job_info = new Button("Download settings");
		job_info.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.info()));
		
		
		job_info.addListener(Events.OnClick, new Listener<ComponentEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(ComponentEvent be) {
			
				JSONObject json = new JSONObject();
				
				json.put("email", new JSONString(email));
				json.put("key", new JSONString(key));
				json.put("taxonomic", new JSONString(Boolean.toString(tax_on.isChecked())));
				json.put("sortbysource", new JSONString(Boolean.toString(sources_n.isChecked())));
				
				searchService.getJobInfoUrl(json.toString(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String arg0) {
						download_panel.setUrl(arg0+"&name=settings.txt&encoding=utf-8");
						
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						MessageBox.alert("", arg0.getMessage(), null);
						
					}
				});
				
			}
		});
		return job_info;
	}
	
	
	public void update(){
		conf = new JSONObject();
		conf.put("email",new JSONString(email));
		conf.put("key", new JSONString(key));
		conf.put("taxonomic_constraint", new JSONString(Boolean.toString(tax_on.isChecked())));
		conf.put("source_sorting", new JSONString(Boolean.toString(sources_n.isChecked()))); 
		conf.put("first", new JSONString("false"));
		loader.load();
	}

	private void compose(String message)
	{

		setHeight(500);
		store = buildStore();
		System.out.println(store.getCount());
		final ColumnModel cm = buildColumnModel();

		buildGrid(store, cm);
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSize(1600, 930);
		vpanel.setLayout(new FitLayout());
		vpanel.add(buildDownloadToolbar(message));
		vpanel.add(grid);
		toolBar = new PagingToolBar(100);
		download_panel = new ContentPanel();
		download_panel.setFrame(false);
		download_panel.setSize(1, 1);
		download_panel.setVisible(false);
		toolBar.bind(loader);
		vpanel.add(toolBar);
		vpanel.add(download_panel);
		//grid.getStore().getLoader().load();
		grid.getView().refresh(false);


		toolBar.unmask();
		vpanel.unmask();
		add(vpanel);
		unmask();
		conf = new JSONObject();
		conf.put("email",new JSONString(email));
		conf.put("key", new JSONString(key));
		conf.put("taxonomic_constraint", new JSONString(Boolean.toString(tax_on.isChecked())));
		conf.put("source_sorting", new JSONString(Boolean.toString(sources_n.isChecked()))); 
		conf.put("first", new JSONString("false"));
	}


	public void setDirty(boolean isdirty){
		dirty = isdirty;
	}


	private ListStore<BeanModel> buildStore() 
	{
		mask("wait");

		conf = new JSONObject();
		conf.put("email",new JSONString(email));
		conf.put("key", new JSONString(key));
		conf.put("taxonomic_constraint", new JSONString(Boolean.toString(tax_on.isChecked())));
		conf.put("source_sorting", new JSONString(Boolean.toString(sources_n.isChecked())));
		conf.put("first", new JSONString("true"));
		proxy = new RpcProxy<BasePagingLoadResult<BeanTNRSEntry>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<BasePagingLoadResult<BeanTNRSEntry>> callback) {

				searchService.getRemoteData((PagingLoadConfig)loadConfig, conf.toString(), callback);



			}
		};

		BeanModelReader reader = new BeanModelReader();


		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy, reader){

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.data.BasePagingLoader#onLoadSuccess(java.lang.Object, com.extjs.gxt.ui.client.data.PagingLoadResult)
			 */
			@Override
			protected void onLoadSuccess(Object loadConfig,
					PagingLoadResult<ModelData> result) {
				// TODO Auto-generated method stub
				super.onLoadSuccess(loadConfig, result);
			}

		};  
		ListStore<BeanModel> store = new ListStore<BeanModel>(loader);  

		return store;
	}


	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();

		config.add(buildConfig("flag", "<img src=\"images/flag.png\" />", 30, HorizontalAlignment.CENTER,new FlagRenderer()));
		config
		.add(buildConfig("submitted", "Name<br/> Submitted", 200,
				HorizontalAlignment.LEFT));

		config.add(buildConfig("scientific", "Name Matched", 380,
				HorizontalAlignment.LEFT, new ScientificNameCellRenderer()));
		config.add(buildConfig("source", "Name Source", 150, HorizontalAlignment.LEFT, new SourceRenderer()));
		config.add(buildConfig("overall", "Overall <br/>Score", 60, HorizontalAlignment.CENTER,
				new OverallCellRenderer()));

		config.add(buildConfig("acceptance","Taxonomic Status", 150, HorizontalAlignment.LEFT));

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
		grid.disableTextSelection(false);
		grid.getView().refresh(true);
		grid.setSize(1157,300);
		grid.mask("Loading...");

		//loader.load();
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











	private class DetailsCellRenderer implements GridCellRenderer<BeanModel>
	{
		private void launchDetailsWindow(final BeanModel entry)
		{
			//


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

					DetailDialog dlg = new DetailDialog(model,taxonomic);

					dlg.show();

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
				LayoutContainer container = new LayoutContainer();
				container.setLayout(new ColumnLayout());

				container.add(new Label(acceptedName+" "+acceptedAuthor+"&nbsp&nbsp"));

				String[] urls = acceptedNameUrl.split(";",-1);
				String[] sources = model.get("source").toString().toUpperCase().split(";",-1);
				for(int i=0; i < urls.length;i++){
					LinkIcon icon = new LinkIcon(urls[i],true,sources[i]);
					container.add(icon);
				}

				return container;

			}else {
				return "";
			}

		}

	}


	private class SourceRenderer implements GridCellRenderer<BeanModel>
	{

		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {

			String url = model.get("url").toString();
			String source = model.get("source").toString().toUpperCase();
			LayoutContainer container= new LayoutContainer();
			container.setLayout(new ColumnLayout());

			String[] urls = url.split(";",-1);
			String[] sources = source.split(";",-1);


			for(int i=0; i < sources.length; i++){
				
				if(!urls[i].trim().equals("") || sources[i].equals("")){

					Html link = new Html("<a href=\""+urls[i]+"\" target='_blank'>"+sources[i]+"</a>&nbsp;&nbsp;&nbsp;");

					container.add(link);
				}else{

					container.add(new Label(sources[i]+"&nbsp;&nbsp;&nbsp;"));
				}
			}

			return container;
		}

	}

	private class OverallCellRenderer implements GridCellRenderer<BeanModel>
	{
		@Override
		public Object render(BeanModel model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid)
		{
			return NumberUtil.formatPercentage(model.get("overall").toString());
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

	@Override
	protected void afterRender() {
		grid.getStore().sort("submitted", SortDir.ASC);
		super.afterRender();
	}


	class SelectionUpdatedEvent implements ClientCommand
	{

		long group;
		public SelectionUpdatedEvent(long group)
		{

		}

		@Override
		public void execute(String selected)
		{

			JSONObject json = new JSONObject();
			json.put("group", new JSONString(Long.toString(group)));
			json.put("email",new JSONString(email));
			json.put("key", new JSONString(key));
			json.put("name_id", new JSONString(selected));
			
			
			searchService.updateGroup(json.toString(), new AsyncCallback<String>() {

				@Override
				public void onSuccess(String arg0) {
					
					
				}

				@Override
				public void onFailure(Throwable arg0) {
					MessageBox.alert("Error", "There was an error updating your selection. <br>"+arg0.getMessage(), null);

				}
			});
			
			grid.getView().refresh(false);
		}
	}

	class SelectionDownloadEvent implements ClientCommand
	{

		String key;
		String email;
		String name;
		SearchServiceAsync searchService;

		public SelectionDownloadEvent( String email,String key,SearchServiceAsync service) {
			this.key= key;
			this.email =email;
			searchService = service;
		}

		public void setName(String name) {
			this.name =name;

		}

		@Override
		public void execute(String options)
		{

			if(options != null)
			{

				JSONObject json = (JSONObject) JSONParser.parseStrict(options);


				final String name = json.get("name").toString().replace("\"", "");
				final String encoding = json.get("encoding").toString().replace("\"", "");

				json.put("email", new JSONString(email));
				json.put("key", new JSONString(key));


				searchService.downloadRemoteResults(json.toString(), new AsyncCallback<String>()
						{
					@Override
					public void onFailure(Throwable arg0)
					{
						MessageBox.alert("Error", "File download failed.<br/> "+arg0.getMessage(), null);
					}

					@Override
					public void onSuccess(String result)
					{		
						download_panel.setUrl(result+"&name="+name+"&encoding="+encoding);						
					}
						});
			}


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
			if(name.trim().equals("No suitable matches found.")){
				add(new Label(name));
				return;
			}
			String ahref = prefix + name  + suffix;



			add(new Label(name+"&nbsp;&nbsp;"));

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
						json.put("source_sorting", new JSONString(Boolean.toString(sources_n.isChecked())) );
						json.put("taxonomic_constraint", new JSONString(Boolean.toString(tax_on.isChecked())));
						final long group = Long.parseLong(entry.get("group").toString());
						searchService.requestGroupMembers(json.toString(), new AsyncCallback<String>() {

							@Override
							public void onSuccess(String json) {
								System.out.println(json);
								ListStore<TNRSEntry> store = new ListStore<TNRSEntry>();


								JSONObject objJson = (JSONObject)JSONParser.parseStrict(json);

								JSONValue val = objJson.get("items");
								JSONArray items = val.isArray();

								JsArray<JsTNRSEntry> jsEntries = JsonUtil.asArrayOf(items.toString());


								for(int i=0; i < items.size(); i++) {
									TNRSEntry entry = new TNRSEntry(jsEntries.get(i));
									store.add(entry);
								}



								if(store != null)
								{

									RemoteTNRSDetailsDialog dlg = new RemoteTNRSDetailsDialog(store, hasFamilies,new SelectionUpdatedEvent(group),false,page_store,rowIndex,email,key);

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


	class FlagRenderer implements GridCellRenderer<BeanModel>{

		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {

			final int flag = Integer.parseInt(model.get("flag").toString());
			final boolean taxonomic = tax_on.isChecked();
			
			
			
			if(flag==0) {
				return " ";
			}

			String flagText = "";

			if((flag&1)==1 ) {
				flagText += "- Partial match <br/>";
			}
			
			if((flag&2)==2) {
				flagText += "- Ambiguous match <br/> ";
			}
			
			if((flag&4)==4 ){
				flagText += " -Better higher taxonomic match available ";
			}
			if((flag&8)==8 ) {
				
				flagText += "- Better spelling match in different higher taxon <br/>";
			}
			
			if(flagText.trim().equals("")){
				return "";
			}

			flagText+="<br/> Click on the flag for more information";

			LayoutContainer container = new LayoutContainer();

			container.setToolTip(new ToolTipConfig("Information",flagText));

			container.add( new HTML("<span ><img src=\"images/flag.png\" /></span>"));
			container.addListener(Events.OnClick, new Listener<ComponentEvent>() {

				/* (non-Javadoc)
				 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
				 */
				@Override
				public void handleEvent(ComponentEvent be) {
					Dialog dialog = new Dialog();
					dialog.setSize(200, 200);
					Html text = new Html();
					text.setHtml("");
					
					if((flag&1)==1 ) {
						text.setHtml("<div style=\"font-weight: bold;\">Partial match</div><br/> Name matched is a higher taxon than the name submitted.<br/>");
					}
					if((flag&2)==2 ) {
						text.setHtml(text.getHtml()+"<div style=\"font-weight: bold;\">Ambiguous match</div><br/> More than one name with the same score and acceptance.<br/>");
					}
					if((flag&4)==4) {
						text.setHtml(text.getHtml()+"<div style=\"font-weight: bold;\">Better higher taxonomic match available</div><br/> Another name with lower overall score has a better matching higher taxon .<br/>");
					}

					if((flag&8)==8 ){
						text.setHtml(text.getHtml()+"<div style=\"font-weight: bold;\"> Better spelling match in different higher taxon</div><br/> Another name in different higher taxon has a better overall score <br/>");
						
					}

					dialog.setLayout(new FitLayout());
					LayoutContainer container = new LayoutContainer();
					container.add(text);
					container.setScrollMode(Scroll.AUTO);
					dialog.add(container);
					dialog.layout();
					dialog.setHideOnButtonClick(true);
					dialog.setModal(true);
					dialog.show();

				}
			});
			return container;

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
