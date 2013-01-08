/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.client.images.Resources;
import org.iplantc.tnrs.demo.client.views.Hyperlink;
import org.iplantc.tnrs.demo.shared.BeanTnrsParsingEntry;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author raygoza
 *
 */
public class NameParsingResultsGrid extends LayoutContainer{

	private PagingToolBar pagingToolbar;

	private Grid<BeanModel> grid;
	private PagingLoader<PagingLoadResult<ModelData>> loader ;
	private List<TNRSParsingEntry> entries;
	private PagingToolBar toolbar;
	private final SearchServiceAsync searchService;
	private RpcProxy<BasePagingLoadResult<BeanTnrsParsingEntry>> proxy;
    private String email;
    private String key;
    private ContentPanel download_panel;
    private JSONObject json;
    
	public NameParsingResultsGrid(SearchServiceAsync service,String json_options) {
		searchService = service;
		JSONObject json = (JSONObject)JSONParser.parseStrict(json_options);
		email = json.get("email").toString();
		key = json.get("key").toString();
		init();
		compose(json);
	}

	private void init() {
		setSize(1600,330); 
		setBorders(false);
		setStyleAttribute("margin","5px");
		Element el = XDOM.getElementById("toggle");
		el.setAttribute("flag", "true");
	}


	private ToolBar buildDownloadToolbar() {
		ToolBar ret = new ToolBar();

		Button but = new Button("Download results", new SelectionListener<ButtonEvent>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.SelectionListener#componentSelected(com.extjs.gxt.ui.client.event.ComponentEvent)
			 */
			@Override
			public void componentSelected(ButtonEvent ce) {
				TNRSDownloadDialog select_download = new TNRSDownloadDialog(new SelectionDownloadEvent(email,key,searchService),"parsing",false,false,false);

				select_download.show();

			}

		});

		but.setIcon( AbstractImagePrototype.create(Resources.INSTANCE.download()));

		but.setStyleAttribute("color", "blue");
		but.setBorders(true);
		ret.add(but);

		ret.add(new FillToolItem());
		ret.setHeight(30);
		ret.setBorders(true);


		return ret;

	}


	private void compose(JSONObject json) {


		ListStore<BeanModel> store = buildStore(json);

		final ColumnModel cm = buildColumnModel();

		buildGrid(store,cm);

		VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSize(1600, 330);
		vpanel.setLayout(new FitLayout());
		vpanel.add(buildDownloadToolbar());
		vpanel.add(grid);
		toolbar = new PagingToolBar(100);
		toolbar.bind(loader);  
		vpanel.add(toolbar);
		download_panel = new ContentPanel();
		download_panel.setFrame(false);
		download_panel.setSize(1, 1);
		download_panel.setVisible(false);
		loader.load();
		
		
		vpanel.add(download_panel);
		add(vpanel);
		layout();
		json= new JSONObject();
		json.put("key", new JSONString(key));
		json.put("email", new JSONString(email));
		json.put("first", new JSONString("false"));
	}


	private ListStore<BeanModel> buildStore(final JSONObject json) {

		ListStore<BeanModel> entries = new ListStore<BeanModel>();
		
		json.put("first", new JSONString("true"));
		
		proxy =new RpcProxy<BasePagingLoadResult<BeanTnrsParsingEntry>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<BasePagingLoadResult<BeanTnrsParsingEntry>> callback) {
				searchService.getRemoteParsingData((PagingLoadConfig)loadConfig, json.toString(), callback);

			}
		};

		BeanModelReader reader = new BeanModelReader();
		
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy, reader);

		entries = new ListStore<BeanModel>(loader);

		return entries;
	}



	private void buildGrid(final ListStore<BeanModel> store,final ColumnModel cm) {

		grid = new Grid<BeanModel>(store, cm);

		store.setSortField("submitted");
		store.setSortDir(SortDir.ASC);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.getView().setAutoFill(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getView().refresh(true);
		grid.setSize(1150, 330);
		grid.disableTextSelection(false);
	}


	protected ColumnConfig buildConfig(final String id, final String caption, int width, final HorizontalAlignment alignment) {
		return buildColumnConfig(id, caption, width, alignment, null);
	}

	protected ColumnConfig buildColumnConfig(String id, String caption, int width, HorizontalAlignment alignment, final GridCellRenderer<BeanModel> renderer) {

		ColumnConfig ret = new ColumnConfig(id, caption, width);

		ret.setMenuDisabled(true);
		ret.setSortable(true);
		ret.setAlignment(alignment);

		if(renderer !=null) {
			ret.setRenderer(renderer);
		}

		return ret;
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
	
	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();

		config.add(buildColumnConfig("submittedName","Name Submitted", 200, HorizontalAlignment.LEFT, null));
		config.add(buildColumnConfig("taxonName", "Taxon Name", 200, HorizontalAlignment.LEFT, null));
		config.add(buildColumnConfig("author", "Author", 200, HorizontalAlignment.LEFT, null));
		config.add(buildColumnConfig("unmatched", "Unmatched terms", 200, HorizontalAlignment.LEFT, null));
		config.add(buildColumnConfig("details","Details", 70, HorizontalAlignment.LEFT, new DetailsCellRenderer()));

		return new ColumnModel(config);
	}


	class DetailsCellRenderer implements GridCellRenderer<BeanModel> {
		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(BeanModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BeanModel> store, Grid<BeanModel> grid) {

			final Hyperlink ret = new Hyperlink("Details", "de_tnrs_hyperlink");
			ret.setStyleAttribute("color", "blue");
			ret.setStyleAttribute("text-decoration", "underline");
			// by default, the hyperlinks un-highlight after mouse over. This is
			// undesirable, to the following line has
			// been added as a short term workaround.
			ret.setOnMouseOutStyle("text-decoration", "underline");
			ret.setOnMouseOverStyle("cursor", "pointer");
			final BeanModel entry = store.getAt(rowIndex);
			ret.addListener(Events.OnClick, new Listener<ComponentEvent>() {
				public void handleEvent(ComponentEvent be) {
					TNRSParsingDetailsDialog dlg = new TNRSParsingDetailsDialog(entry);
					dlg.show();
				};
			});

			return ret;
		}

	}


}
