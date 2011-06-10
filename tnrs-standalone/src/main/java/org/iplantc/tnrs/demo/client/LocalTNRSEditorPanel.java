package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.iplantc.tnrs.demo.client.images.Resources;
import org.iplantc.tnrs.demo.client.util.NumberUtil;
import org.iplantc.tnrs.demo.client.views.Hyperlink;


import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
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
import com.extjs.gxt.ui.client.widget.Layout;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;


public class LocalTNRSEditorPanel extends TNRSEditorPanel
{

	private Grid<TNRSEntry> grid;
	private HashMap<Long, ListStore<TNRSEntry>> groups;
	private boolean hasFamilies;
	private PagingLoader<PagingLoadResult<TNRSEntry>> loader;
	private final SearchServiceAsync searchService;
	private List<TNRSEntry> all_data;
	/**
	 * Default constructor
	 */
	public LocalTNRSEditorPanel(final SearchServiceAsync searchService, final String json)
	{
		super();
		groups = new HashMap<Long,ListStore<TNRSEntry>>();
		this.searchService = searchService;
		all_data = new ArrayList<TNRSEntry>();
		compose(json);
	}

	

	private ToolBar buildDownloadToolbar()
	{
		ToolBar ret = new ToolBar();



		Button but = new Button("Download results", new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				TNRSDownloadDialog select_download = new TNRSDownloadDialog(new SelectionDownloadEvent());

				select_download.show();


			}
				});
		but.setIcon( AbstractImagePrototype.create(Resources.INSTANCE.download()));

		but.setStyleAttribute("color", "blue");
		but.setBorders(true);
		ret.add(but);


		ret.add(new Html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:blue;'>Entered names results</span>"));
		ret.add(new FillToolItem());
		ret.setHeight(30);
		ret.setBorders(true);
		return ret;
	}




	private void compose(final String json)
	{

		ListStore<TNRSEntry> store = buildStore(json);

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
		loader.load(0, 100);
		vpanel.add(toolBar);
		vpanel.add(download_panel);
		add(vpanel);
		layout();
	}




	// this function will add an entry to our hash map of stores.
	// if the entry that's added has been selected, the entry is returned for later
	// addition
	// to our main grid, otherwise the function returns null. JUST BECAUSE THIS METHOD
	// RETURNED NULL DOES NOT MEAN THAT AN ENTRY WAS NOT ADDED.
	private TNRSEntry addEntry(JsTNRSEntry jsEntry)
	{
		TNRSEntry entry = new TNRSEntry(jsEntry);

		Long idGroup = entry.getGroup();

		if(groups.containsKey(idGroup))
		{
			groups.get(idGroup).add(entry);
		}
		else
		{
			ListStore<TNRSEntry> group = new ListStore<TNRSEntry>();

			group.sort("overall", SortDir.DESC);
			group.add(entry);
			groups.put(idGroup, group);
		}

		return (entry.isSelected()) ? entry : null;
	}





	private ListStore<TNRSEntry> buildStore(final String json)
	{
		ListStore<TNRSEntry> entries = new ListStore<TNRSEntry>();

		if(json != null)
		{


			JSONObject objJson = (JSONObject)JSONParser.parse(json);

			JSONValue val = objJson.get("items");
			JSONArray items = val.isArray();

			JsArray<JsTNRSEntry> jsEntries = JsonUtil.asArrayOf(items.toString());

			List<TNRSEntry> data = new ArrayList<TNRSEntry>(); 

			if(jsEntries != null)
			{
				TNRSEntry entry;

				for(int i = 0,len = jsEntries.length();i < len;i++)
				{
					all_data.add(new TNRSEntry(jsEntries.get(i)));
					entry = addEntry(jsEntries.get(i));

					if(entry!=null){
						data.add(entry);
					}

				}
			}


			PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(data);

			loader = new BasePagingLoader<PagingLoadResult<TNRSEntry>>(proxy);

			loader.setRemoteSort(true);

			entries = new ListStore<TNRSEntry>(loader);




		}


		System.out.println(all_data.size());
		return entries;
	}





	
	 

	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();


		config
		.add(buildConfig("submitted", "Name<br/> Submitted", 200,
				HorizontalAlignment.LEFT, new SubmittedNameRenderer()));

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

	

	
	private void buildGrid(final ListStore<TNRSEntry> store, final ColumnModel cm)
	{



		grid = new Grid<TNRSEntry>(store, cm);

		store.setSortField("submitted");
		store.setSortDir(SortDir.ASC);
		grid.setLoadMask(true);
		grid.getStore().sort("submitted", SortDir.ASC);
		grid.setBorders(true);		
		grid.setAutoExpandColumn("scientific");
		grid.setStripeRows(true);
		grid.getView().setAutoFill(true);
		grid.getView().setShowDirtyCells(true);
		// disallow multi-select
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getView().refresh(true);
		grid.setSize(1150, 330);

	}




	private class ScientificNameCellRenderer implements GridCellRenderer<TNRSEntry>
	{
		private String buildCountOutput(TNRSEntry entry)
		{
			String ret = "";

			ListStore<TNRSEntry> store = groups.get(entry.getGroup());

			if(store != null)
			{
				int count = store.getCount();

				if(count > 1)
				{
					// we don't count ourself
					count--;

					ret = " (+" + count + " " + "more" + ")";
				}
			}

			return ret;
		}

		@Override
		public Object render(TNRSEntry model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<TNRSEntry> store, Grid<TNRSEntry> grid)
		{

			String ret="";

			if(NumberUtil.isDouble(model.getScientificScore())) {
				String url = model.getURL();

				return new NameMatchedRenderer(url,model.getScientificName() +" "+model.getAttributedAuthor(),buildCountOutput(model),model);
			}else {
				ret ="No matches found.";

				return ret;
			}
		}
	}



	

	

	


	private class SubmittedNameRenderer implements GridCellRenderer<TNRSEntry>{


		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(TNRSEntry model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<TNRSEntry> store, Grid<TNRSEntry> grid) {

			LayoutContainer container = new LayoutContainer();
			container.add(new Html("<span>"+model.getSubmittedName()+"</span>"));

			return container;
		}

	}

	private class DetailsCellRenderer implements GridCellRenderer<TNRSEntry>
	{
		private void launchDetailsWindow(final TNRSEntry entry)
		{
			Long group = entry.getGroup();

			ListStore<TNRSEntry> store = groups.get(group);

			if(store != null)
			{
				DetailDialog dlg = new DetailDialog(entry);

				dlg.show();

			}
		}



		@Override
		public Object render(final TNRSEntry model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<TNRSEntry> store, Grid<TNRSEntry> grid)
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

	private class AcceptedNameRenderer implements GridCellRenderer<TNRSEntry>{

		@Override
		public Object render(TNRSEntry model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<TNRSEntry> store, Grid<TNRSEntry> grid) {
			if(model.getAcceptedName()!=null && !model.getAcceptedName().equals("")) {
				setLayout(new ColumnLayout());
				String prefix =  "<a href='"+model.getAcceptedNameUrl()+ "' target='_blank'>";
				String suffix =  "</a>&nbsp;&nbsp; ";

				// setup our base link
				String ahref = prefix + model.getAcceptedName() + " "+model.getAcceptedAuthor() + suffix;


				Html link = new Html(ahref);
				return link;

			}else {
				return "";
			}

		}

	}

	private class OverallCellRenderer implements GridCellRenderer<TNRSEntry>
	{
		@Override
		public Object render(TNRSEntry model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<TNRSEntry> store, Grid<TNRSEntry> grid)
		{
			return NumberUtil.formatPercentage(model.getOverall());
		}
	}


	private String buildSelectedJson()
	{
		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();


		// add items
		List<TNRSEntry> store = all_data;

		int k=0;
		for(int i = 0;i < store.size();i++)
		{
			TNRSEntry entry = store.get(i);
			if(entry.isSelected()) {
				array.set(k, buildEntryJson(entry));
				k++;
			}
		}

		json.put("items", array);


		return json.toString();
	}

	private String buildSimpleSelectedJson()
	{
		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();


		// add items
		List<TNRSEntry> store = all_data; 
		int k=0;
		for(int i = 0;i < store.size();i++)
		{
			TNRSEntry entry = store.get(i);
			if(entry.isSelected()) {
				array.set(k, buildSimpleJson(entry));
				k++;
			}
		}

		json.put("items", array);


		return json.toString();
	}


	private String buildAllJson()
	{
		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();

		// add items
		List<TNRSEntry> store = all_data;

		for(int i = 0;i < store.size();i++)
		{
			TNRSEntry entry = store.get(i);
			array.set(i, buildEntryJson(entry));
		}

		json.put("items", array);


		return json.toString();
	}

	private String buildAllSimpleJson()
	{
		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();
		
		List<TNRSEntry> store = all_data;

		for(int i = 0;i < store.size();i++)
		{
			TNRSEntry entry = store.get(i);
			array.set(i, buildSimpleJson(entry));
		}

		json.put("items", array);
		return json.toString();
	}



	private JSONObject buildSimpleJson(TNRSEntry entry) {

		JSONObject json = new JSONObject();	

		json.put("nameSubmitted", new JSONString( entry.getSubmittedName()));
		json.put("nameMatched", new JSONString(entry.getScientificName()));
		json.put("nameMatchedAuthor", new JSONString(entry.getAttributedAuthor()));
		json.put("overallScore", new JSONString(NumberUtil.formatPercentage(entry.getOverall())));
		json.put("acceptance",new JSONString(entry.getAcceptance()));
		json.put("acceptedName",new JSONString(entry.getAcceptedName()));
		json.put("acceptedNameAuthor", new JSONString(entry.getAcceptedAuthor()));

		return json;
	}


	private JSONObject buildEntryJson(TNRSEntry entry)
	{

		JSONObject json = new JSONObject();
		//wr.write("Name submitted,Matched Name,Matched Name Score, Attributed Author, Family matched,Family Matched Score, Genus Matched,Genus Score, Species Matched, Species Score,Infraspecific Rank 1,Infraspecific Epithet 1 Matched, Infraspecific Epithet 1 Score,Infraspecific Rank2, Infraspecific Epithet 2 Matched, Infraspecific Epithet 2 Score,  Annotation,Acceptance,Accepted Name,Unmatched Terms,overall\n");

		json.put("nameSubmitted", new JSONString( entry.getSubmittedName()));
		json.put("overallScore", new JSONString(entry.getOverall()));
		json.put("nameMatched", new JSONString(entry.getScientificName()));
		json.put("nameMatchedScore", new JSONString(NumberUtil.formatPercentage(entry.getScientificScore())));
		json.put("nameMatchedAuthor", new JSONString(entry.getAttributedAuthor()));
		json.put("nameMatcheUrl", new JSONString(entry.getURL()));
		json.put("authorMatched", new JSONString(entry.getAuthor()));
		json.put("authorMatchedScore", new JSONString(NumberUtil.formatPercentage(entry.getAuthorScore())));
		json.put("familyMatched ", new JSONString(entry.getFamilyMatched()));
		json.put("familyMatchedScore",new JSONString(NumberUtil.formatPercentage(entry.getFamilyMatchedScore())));
		json.put("genusMatched", new JSONString(entry.getGenus()));
		json.put("genusScore", new JSONString(NumberUtil.formatPercentage(entry.getGenusScore())));
		json.put("speciesMatched", new JSONString(entry.getSpeciesMatched()));
		json.put("speciesMatchedScore", new JSONString(NumberUtil.formatPercentage(entry.getSpeciesMatchedScore())));
		json.put("infraspecific1Rank", new JSONString(entry.getInfraSpecificRank1()));
		json.put("infraspecific1Matched", new JSONString(entry.getInfraSpecificEpithet1()));
		json.put("infraspecific1MatchedScore", new JSONString(NumberUtil.formatPercentage(entry.getInfraSpecificEpithet1Score())));
		json.put("infraspecificRank 2", new JSONString(entry.getInfraSpecificRank2()));
		json.put("infraspecific2Matched", new JSONString(entry.getInfraSpecificEpithet2()));
		json.put("infraspecific2MatchedScore", new JSONString(NumberUtil.formatPercentage(entry.getInfraSpecificEpithet2Score())));
		json.put("annotations", new JSONString(entry.getAnnotation()));
		json.put("unmatchedTerms", new JSONString(entry.getUnmatched()));
		json.put("selected", new JSONString(Boolean.toString(entry.isSelected())));
		if(entry.getAcceptance()==null) {
			json.put("acceptance",new JSONString("Accepted"));
		}else {
			json.put("acceptance",new JSONString(entry.getAcceptance()));
		}
		json.put("family", new JSONString(entry.getFamily()));
		if(entry.getAcceptedName()==null) {
			json.put("acceptedName", new JSONString(""));
		}else {
			json.put("acceptedName",new JSONString(entry.getAcceptedName()));
		}
		json.put("acceptedAuthor", new JSONString(entry.getAcceptedAuthor()));
		json.put("acceptedNameUrl", new JSONString(entry.getAcceptedNameUrl()));


		return json;		
	}



	private void doDownload(final String json)
	{
		if(json != null)
		{
			searchService.downloadResults(json, new AsyncCallback<String>()
					{
				@Override
				public void onFailure(Throwable arg0)
				{
					MessageBox.alert("Error", "File download failed.", null);
				}

				@Override
				public void onSuccess(String result)
				{		
					   
					download_panel.setUrl(result); 


				}
					});
		}
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Container#onBeforeLayoutExcecuted(com.extjs.gxt.ui.client.widget.Layout)
	 */
	@Override
	protected void onBeforeLayoutExcecuted(Layout layout) {
		download_panel.setUrl("");

		super.onBeforeLayoutExcecuted(layout);
	}

	private TNRSEntry getSelected(final ListStore<TNRSEntry> store)
	{
		TNRSEntry ret = null;

		if(store != null)
		{
			TNRSEntry entry;

			// loop through our store and find the selected item
			for(int i = 0;i < store.getCount();i++)
			{
				entry = store.getAt(i);

				if(entry.isSelected())
				{
					ret = entry;
					break;
				}
			}
		}

		return ret;
	}



	private void updateSelection(final Long group)
	{
		TNRSEntry selected = getSelected(groups.get(group));

		if(selected != null)
		{
			TNRSEntry entry;

			ListStore<TNRSEntry> store = grid.getStore();

			for(int i = 0;i < store.getCount();i++)
			{
				entry = store.getAt(i);

				if(entry.getGroup() == group)
				{
					// remove our original
					store.remove(i);

					// insert our selected item
					store.insert(selected, i);

					// refresh our grid so that the correct entry is displayed
					grid.getView().refresh(false);

					break;
				}
			}
		}
	}


	@Override
	protected void afterRender() {
		grid.getStore().sort("submitted", SortDir.ASC);
		super.afterRender();
	}

	class SelectionUpdatedEvent implements ClientCommand
	{
		private Long group;

		public SelectionUpdatedEvent(Long group)
		{
			this.group = group;
		}

		@Override
		public void execute(String h)
		{
			updateSelection(group);
		}
	}

	class SelectionDownloadEvent implements ClientCommand
	{

		@Override
		public void execute(String h)
		{
			String[] options= h.split("#");
			if(options[0].equals("Best matches only") && options[1].equals("Simple")) {

				doDownload(buildSimpleSelectedJson());

			}else if(options[0].equals("Best matches only") && options[1].equals("Detailed")) {
				doDownload(buildSelectedJson());
			}else if(options[0].equals("All matches") && options[1].equals("Simple")) {
				doDownload(buildAllSimpleJson());
			}else if(options[0].equals("All matches") && options[1].equals("Detailed")) {
				doDownload(buildAllJson());
			}

		}
	}

	class NameMatchedRenderer extends LayoutContainer{

		TNRSEntry entry;

		public NameMatchedRenderer(String url,String name,String countString,TNRSEntry model) {
			setLayout(new ColumnLayout());
			String prefix = (url == null) ? "" : "<a href='" + url + "' target='_blank'>";
			String suffix = (url == null) ? " " : "</a>&nbsp;&nbsp; ";
			entry =model;
			// setup our base link
			String ahref = prefix + name  + suffix;


			Html link = new Html(ahref);
			add(link);

			if(!countString.equals("")) {


				Anchor countLink = new Anchor();
				countLink.setText("     "+countString);
				countLink.setStyleName("linkCursor");
				countLink.addStyleName("details-link-color");
				countLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {


						Long group = entry.getGroup();

						ListStore<TNRSEntry> store = groups.get(group);

						if(store != null)
						{
							TNRSDetailsDialog dlg = new TNRSDetailsDialog(store, hasFamilies,new SelectionUpdatedEvent(group),false);

							dlg.show();						
						}
					}
				});

				add(countLink);

			}

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
