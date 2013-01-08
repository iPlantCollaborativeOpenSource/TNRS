package org.iplantc.tnrs.demo.client;



import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.client.util.NumberUtil;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;


/**
 * Dialog for displaying details about a TNRS entry.
 * 
 * @author amuir
 * 
 */
public class TNRSDetailsDialog extends Window
{
	protected Grid<TNRSEntry> grid;
	protected int idxOrigSelected;
	protected final ClientCommand cmdOk;
	private boolean showAllfields;
	private ContentPanel download_panel;



	/**
	 * Instantiate from a store of entries.
	 * 
	 * @param store entries to view.
	 * @param displayFamilyNames true to display family names.
	 */
	public TNRSDetailsDialog(final ListStore<TNRSEntry> store, boolean displayFamilyNames, final ClientCommand cmdOk, boolean showAll)
	{		
		this.cmdOk = cmdOk;
		idxOrigSelected = getSelectedIdx(store);    
		showAllfields =  showAll;
		init(store, displayFamilyNames);

		initComponents(store);

		compose();
	}

	private void initHeader(final ListStore<TNRSEntry> store)
	{
		String name = "";

		if(store != null)
		{
			TNRSEntry entry = store.getAt(0);

			if(entry != null)
			{
				name = entry.getSubmittedName();
			}
		}

		setHeading("Submitted Name: " +name);
	}



	protected ToolBar buildToolbar()
	{
		ToolBar ret = new ToolBar();

		ret.add(new FillToolItem());

		ret.add(new Button("Apply selected", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				
				
				
				if(cmdOk != null)
				{
					ListStore<TNRSEntry> store = grid.getStore();
					
					TNRSEntry entry = store.getAt(getSelectedIdx(grid.getStore()));
					cmdOk.execute(entry.getNameMatchedId());
				}
			//	hide();
			}
		}));


		ret.add(new Button("Cancel", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce)
			{

				hide();
			}
		}));

		ret.setBorders(false);
		ret.setHeight(30);
		ret.setWidth(890);
		return ret;
	}



	

	private void init(final ListStore<TNRSEntry> store, boolean displayFamilyNames)
	{
		initHeader(store);

		int width = (displayFamilyNames) ? 1022: 912;
		setSize(width, 318);
		setResizable(false);
		setModal(true);

		
	}

	// build column with custom renderer
	private ColumnConfig buildConfig(String id, String caption, int width,
			HorizontalAlignment alignment, final GridCellRenderer<TNRSEntry> renderer,boolean resizable)
	{
		ColumnConfig ret = new ColumnConfig(id, caption, width);

		ret.setMenuDisabled(true);
		ret.setSortable(true);
		ret.setAlignment(alignment);
		ret.setResizable(resizable);
		if(renderer != null)
		{
			ret.setRenderer(renderer);
		}

		return ret;
	}

	// build column without custom renderer
	private ColumnConfig buildConfig(String id, String caption, int width, HorizontalAlignment alignment,boolean resizable)
	{
		return buildConfig(id, caption, width, alignment, null,resizable);
	}

	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();


		config.add(buildConfig("scientific", "Name matched", 140,
				HorizontalAlignment.LEFT, new ScientificNameCellRenderer(),true));

		
		config.add(buildConfig("source", "Name Source", 100, HorizontalAlignment.LEFT,new NameMatchedUrlRenderer(),true));
		config.add(buildConfig("score", "Score", 55,
				HorizontalAlignment.LEFT, new OverallCellRenderer(),false));

		config.add(buildConfig("author", "Author Matched", 100, HorizontalAlignment.LEFT,true));
		config.add(buildConfig("authorScore", "Author Score", 100, HorizontalAlignment.LEFT,true));
		config.add(buildConfig("acceptedSpecies", "Accepted Species", 100, HorizontalAlignment.LEFT, true));
		config.add(buildConfig("unmatched", "Unmatched Terms", 100, HorizontalAlignment.LEFT,true));
		config.add(buildConfig("acceptance", "Taxonomic status", 60,  HorizontalAlignment.LEFT,false));		
		config.add(buildConfig("selected", "Select", 60, HorizontalAlignment.CENTER,new SelectCellRenderer(),false));

		return new ColumnModel(config);
	}

	protected int getSelectedIdx(final ListStore<TNRSEntry> store)
	{
		int ret = 0;

		if(store != null)
		{
			TNRSEntry entry;

			// loop through our store and find the selected item
			for(int i = 0;i < store.getCount();i++)
			{
				entry = store.getAt(i);

				if(entry.isSelected())
				{
					ret = i;
					break;
				}
			}
		}

		return ret;
	}

	private void buildGrid(final ListStore<TNRSEntry> store)
	{
		final ColumnModel cm = buildColumnModel();

		//GroupingView sourceView = new GroupingView();
		
		/*sourceView.setGroupRenderer(new GridGroupRenderer() {
			
			@Override
			public String render(GroupColumnData data) {
				
				return ""+data.group;
			}
		})*/;
		grid = new Grid<TNRSEntry>(store, cm);
		store.setStoreSorter(new DetailsSorter());
		store.sort("submitted", SortDir.ASC);
		grid.setHeight(240);
		int width = 900;
		grid.setWidth(width);
		grid.setBorders(true);
		grid.disableTextSelection(false);
		
		if(!showAllfields) {
			grid.getView().setAutoFill(true);
		}
		//grid.setView(sourceView);
		grid.getView().setSortingEnabled(true);
		
		
		
		// disallow multi-select
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getView().setForceFit(true);
		grid.getView().ensureVisible(idxOrigSelected, 0, true);

		layout();		
	}

	private void initComponents(final ListStore<TNRSEntry> entries)
	{
		buildGrid(entries);
	}

	private void compose()
	{
		VerticalPanel pnlInner = new VerticalPanel();

		pnlInner.setSpacing(5);
		pnlInner.setStyleAttribute("background-color", "#EDEDED");
		pnlInner.setLayout(new FitLayout());

		pnlInner.add(grid);
		pnlInner.add(buildToolbar());
		download_panel = new ContentPanel();
		download_panel.setFrame(false);
		download_panel.setSize(1, 1);
		download_panel.setVisible(false);
		
		pnlInner.add(download_panel);
		add(pnlInner);
		pnlInner.layout();
	}




	private void deselectCurrent(long group)
	{
		ListStore<TNRSEntry> store = (ListStore<TNRSEntry>)grid.getStore();
		TNRSEntry entry;

		for(int i = 0, len = store.getCount(); i < len; i++)		
		{
			entry = store.getAt(i);

			if(entry.isSelected())
			{
				// update our model
				entry.clearSelection();
				store.update(entry);
				break;
			}
		}
	}

	private void promote(TNRSEntry entry)
	{
		deselectCurrent(entry.getGroup());
		entry.setSelected();
		ListStore<TNRSEntry> store = grid.getStore();
		store.update(entry);
	}

	private class ScientificNameCellRenderer implements GridCellRenderer<TNRSEntry>
	{
		@Override
		public Object render(TNRSEntry model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<TNRSEntry> store, Grid<TNRSEntry> grid)
		{
			
			return new Label(model.getScientificName() +" "+ model.getAttributedAuthor() );

		}		
	}

	
	private class NameMatchedUrlRenderer implements GridCellRenderer<TNRSEntry>
	{
		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(TNRSEntry model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<TNRSEntry> store, Grid<TNRSEntry> grid) {
		
				LayoutContainer container = new LayoutContainer();
				container.setLayout(new ColumnLayout());
				
				String[] urls = model.getURL().split(";",-1);
				String[] sources = model.getSources().toUpperCase().split(";",-1);
			
				
				for(int i=0; i < sources.length; i++){
					
					if(!urls[i].trim().equals("")){
						Html link = new  Html("<a href=\""+urls[i]+"\" target='_blank'>"+sources[i]+"</a>&nbsp;&nbsp;");
						container.add(link);
					}else{
						container.add(new Label(sources[i]+"&nbsp;&nbsp;"));
					}
					
				}
			return container;
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

	private class SelectCellRenderer implements GridCellRenderer<TNRSEntry>
	{
		@Override
		public Object render(final TNRSEntry model, String property, ColumnData config, int rowIndex,
				int colIndex, ListStore<TNRSEntry> store, Grid<TNRSEntry> grid)
		{
			Radio ret = new Radio();

			ret.setValue(model.isSelected());

			// TODO: add radio button id

			ret.addListener(Events.Change, new Listener<FieldEvent>()
					{
				@Override
				public void handleEvent(FieldEvent be)
				{
					promote(model);
				}
					});

			return ret;
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
			}

			return super.compare(store, m1, m2, property);
		}

	}

}

