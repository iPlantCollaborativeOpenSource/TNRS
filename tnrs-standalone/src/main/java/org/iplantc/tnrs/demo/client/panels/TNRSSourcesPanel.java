/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.client.SearchServiceAsync;
import org.iplantc.tnrs.demo.client.SourceEntry;





import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;


/**
 * @author raygoza
 *
 */
public class TNRSSourcesPanel extends ContentPanel{

	private Grid<SourceEntry> grid;
	private ListStore<SourceEntry> entries;
	private Slider sensitivity;
	private TNRSClassificationPanel panel;

	public TNRSSourcesPanel(ListStore<SourceEntry> entries) {
		this.entries=entries;
		init();

		compose();

	}


	public void init() {

		setHeaderVisible(false);
		setHeight(450);
		setAutoWidth(true);
		setAutoHeight(true);
	}

	public void compose() {

		//	buildStore();
		ColumnModel cm = buildColumnModel();

		buildGrid(entries,cm);

		setLayout(new RowLayout());

		add(buildUpperPannel());
		add(grid);
		setStyleAttribute("backgroundColor", "#F1F1F1");
		layout();
	}

	public VerticalPanel buildUpperPannel(){
		VerticalPanel panel = new VerticalPanel();

		panel.add(new Label("<div><br/>Drag and Drop to rank sources for resolution. Source ranked #1 will be displayed" +
				" first in the results, regardless of match score. This means that if source #1 has a match score" +
				" that is lower than source #2, source #1 will be displayed as the \"best match\" as it is your first" +
				" preference in searching.<br/><br/><br/></div>"));
		panel.setStyleAttribute("backgroundColor", "#F1F1F1");
		return panel;
	}





	private ColumnModel buildColumnModel()
	{
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();

		config.add(buildConfig("rank", "Rank", 50, HorizontalAlignment.CENTER));
		config.add(buildConfig("name", "Source", 100,
				HorizontalAlignment.LEFT,new SourceNameRenderer()));
		config.add(buildConfig("accessed_date", "Date Accessed", 70, HorizontalAlignment.LEFT));
		config.add(buildConfig("include", "Include in Results", 80,
				HorizontalAlignment.LEFT, new IncludeSourceRenderer()));


		return new ColumnModel(config);

	}

	protected ColumnConfig buildConfig(final String id, final String caption, int width,
			final HorizontalAlignment alignment)
	{
		return buildConfig(id, caption, width, alignment, null);
	}


	protected ColumnConfig buildConfig(String id, String caption, int width,
			HorizontalAlignment alignment, final GridCellRenderer<?> renderer)
	{
		ColumnConfig ret = new ColumnConfig(id, caption, width);


		ret.setResizable(false);
		ret.setMenuDisabled(true);
		ret.setSortable(false);
		ret.setAlignment(alignment);

		if(renderer != null)
		{
			ret.setRenderer(renderer);
		}

		return ret;
	}

	public void setClassificationPanel(TNRSClassificationPanel panel){
		this.panel = panel;

		this.panel.update(entries.getCount());
	}

	public void buildGrid(final ListStore<SourceEntry> store, final ColumnModel cm){

		grid = new Grid<SourceEntry>(store, cm);



		//grid.getStore().sort("rank", SortDir.ASC);
		grid.setBorders(false);		
		grid.setAutoWidth(true);

		grid.getView().setAutoFill(true);
		grid.getView().setShowDirtyCells(true);
		// disallow multi-select
		grid.setSelectionModel(new GridSelectionModel<SourceEntry>() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.widget.grid.GridSelectionModel#isSelectable(int, int, boolean)
			 */
			@Override
			protected boolean isSelectable(int row, int cell, boolean acceptsNav) {
				// TODO Auto-generated method stub
				//return super.isSelectable(row, cell, acceptsNav);
				return grid.getStore().getAt(row).include();
			}

		});


		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);



		grid.getView().refresh(true);
		grid.setSize(420,220);
		grid.getView().setForceFit(true);

		GridDragSource source  = new GridDragSource(grid);  

		source.addDNDListener(new DNDListener() {

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.DNDListener#dragStart(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			@Override
			public void dragStart(DNDEvent e) {
				SourceEntry selectedEntry = grid.getSelectionModel().getSelectedItem();
				if(!selectedEntry.include()) {
					e.getStatus().setStatus(false);
					e.setCancelled(true);
				}

				super.dragStart(e);
			}


			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.DNDListener#dragMove(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			@Override
			public void dragMove(DNDEvent e) {

				GridDragSource src = (GridDragSource)e.getSource();

				//MessageBox.info("", Integer.toString(source.), callback)

				super.dragMove(e);
			}

		});

		GridDropTarget target = new GridDropTarget(grid);  
		target.setAllowSelfAsSource(true);  
		target.setFeedback(Feedback.INSERT);



		target.addDNDListener(new DNDListener() {


			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.DNDListener#dragDrop(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			@Override
			public void dragDrop(DNDEvent e) {



				ListStore<SourceEntry> store = grid.getStore();

				SourceEntry previous=null;

				for(int i=0; i < store.getCount(); i++) {
					SourceEntry entry = store.getAt(i);
					//MessageBox.info("", Integer.toString(entry.rank()),null);


					entry.setRank(i+1);
					store.update(entry);

				}

				// TODO Auto-generated method stub

				super.dragDrop(e);
			}

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.DNDListener#dragLeave(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			@Override
			public void dragLeave(DNDEvent e) {
				// TODO Auto-generated method stub

				super.dragLeave(e);

			}

			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.DNDListener#dragMove(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			@Override
			public void dragMove(DNDEvent e) {


				super.dragMove(e);
			}

		});




	}




	class SourceNameRenderer implements GridCellRenderer<SourceEntry>{

		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(SourceEntry model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<SourceEntry> store, Grid<SourceEntry> grid) {

			Label name = new Label(model.name());

			if(!model.include()) {
				name.disable();
			}

			return name;
		}

	}


	class IncludeSourceRenderer implements GridCellRenderer<SourceEntry>{


		/* (non-Javadoc)
		 * @see com.extjs.gxt.ui.client.widget.grid.GridCellRenderer#render(com.extjs.gxt.ui.client.data.ModelData, java.lang.String, com.extjs.gxt.ui.client.widget.grid.ColumnData, int, int, com.extjs.gxt.ui.client.store.ListStore, com.extjs.gxt.ui.client.widget.grid.Grid)
		 */
		@Override
		public Object render(final SourceEntry model, String property,
				ColumnData config, int rowIndex, int colIndex,
				final ListStore<SourceEntry> store, final Grid<SourceEntry> grid) {

			final CheckBox include = new CheckBox();
			include.setFieldLabel("");

			include.setValue(model.include());

			include.addListener(Events.OnChange, new Listener<ComponentEvent>() {

				/* (non-Javadoc)
				 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
				 */
				@Override
				public void handleEvent(ComponentEvent be) {
				//	MessageBox.alert("", Boolean.toString(include.getValue()), null);
					if(Integer.parseInt(model.get("warning").toString())==1 && include.getValue()){
						MessageBox.alert("Warning", "A warning is associated with this source.<br/> Please consult the documentation for further information.", null);
					}
					model.setInclude(include.getValue());
					int included = includeCount(store);
					if(included==0 && !include.getValue()){
						include.setValue(true);
						model.setInclude(true);
						MessageBox.alert("Error", "At least one source must be selected ", null);
						return;
					}


					panel.update(included);
					sortBySelected(entries);
					
					updateRank(entries);
					
					

				}


				public int includeCount(ListStore<SourceEntry> store){
					int count=0;

					for(int i=0; i < store.getCount();i++){
						if(store.getAt(i).include()){
							count++;
						}
					}

					return count;
				}

			});


			return include;
		}




	}

	private void sortBySelected(ListStore<SourceEntry> entries){

		for(int i=0; i < entries.getCount();i++){
			if(!entries.getAt(i).include()){
				SourceEntry entry = entries.getAt(i);
				entries.remove(entry);
				entries.insert(entry, entries.getCount());
			}
		}


	}
	
	
	private void updateRank(ListStore<SourceEntry> entry){
		for(int i=0; i < entries.getCount(); i++){
			entries.getAt(i).setRank(i+1);
			entries.update(entries.getAt(i));
			
		}
	}

	public String selectedValues(){
		String values= "[";
		for(int i=0; i < entries.getCount(); i++){
			if(entries.getAt(i).include()){
				values+=", "+entries.getAt(i).name();
			}
		}
		values+=" ]";
		return values.replace("[,", "[");
	}


}

