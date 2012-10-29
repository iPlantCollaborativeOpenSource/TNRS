package org.iplantc.tnrs.demo.client;



import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

public class RemoteTNRSDetailsDialog extends TNRSDetailsDialog {

	private SearchServiceAsync service = GWT.create(SearchService.class);
	private ListStore<BeanModel> page_store;
	private int rowIndex;
	private String email;
	private String key;
	
	
	public RemoteTNRSDetailsDialog(final ListStore<TNRSEntry> store, boolean displayFamilyNames, final ClientCommand cmdOk, boolean showAll,final ListStore<BeanModel> page_store,final int rowIndex,String email, String key) {
		super(store,displayFamilyNames,cmdOk,showAll);
		this.page_store = page_store;
		this.rowIndex = rowIndex;
		this.email = email;
		this.key = key;
		
	}
	
	
	@Override
	protected ToolBar buildToolbar() {
		ToolBar ret = new ToolBar();

		ret.add(new FillToolItem());

		ret.add(new Button("Apply selected", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				ListStore<TNRSEntry> details_store = grid.getStore();
				
				int selectedIdx = getSelectedIdx(details_store);
				
				TNRSEntry selected = details_store.getAt(selectedIdx);
				
				BeanModel model = page_store.getAt(rowIndex);
				
			//	mask("Applying changes..");
				updateEntry(model,selected);
				page_store.update(model);
				//unmask();
				
				ListStore<TNRSEntry> store = grid.getStore();
				
				TNRSEntry entry = store.getAt(getSelectedIdx(grid.getStore()));
				cmdOk.execute(entry.getNameMatchedId());
				hide();
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
	
	
	private void updateEntry(BeanModel model, TNRSEntry entry) {
		
		Integer groupSize = (Integer)model.get("groupSize");
		
		model.set("group", Long.parseLong(entry.get("group").toString()));
		model.set("url", entry.get("url").toString());			
		model.set("submitted", entry.get("submitted").toString());
		model.set("scientific", entry.get("scientific").toString());
		model.set("scientificScore", entry.get("scientificScore").toString());
		model.set("authorAttributed", entry.get("authorAttributed").toString());
		model.set("family", entry.get("family").toString());
		System.out.println("["+entry.get("family").toString()+"]");
		model.set("genus", entry.get("genus").toString());
		model.set("genusScore", entry.get("genusScore").toString());
		model.set("epithet", entry.get("epithet").toString());
		model.set("epithetScore", entry.get("epithetScore").toString());
		model.set("author", entry.get("author").toString());
		model.set("authorScore", entry.get("authorScore").toString());
		model.set("annotation", entry.get("annotation").toString());
		model.set("unmatched", entry.get("unmatched").toString());
		model.set("overall", entry.get("overall").toString());
		model.set("selected", entry.get("selected"));
		model.set("matchedFamily",entry.get("matchedFamily").toString());
		model.set("matchedFamilyScore",entry.get("matchedFamilyScore").toString());
		model.set("speciesMatched",entry.get("speciesMatched").toString());
		model.set("speciesMatchedScore",entry.get("speciesMatchedScore").toString());
		model.set("infraspecific1Rank",entry.get("infraspecific1Rank").toString());
		model.set("infraspecific1Epithet",entry.get("infraspecific1Epithet").toString());
		model.set("infraspecific1EpithetScore",entry.get("infraspecific1EpithetScore").toString());
		model.set("infraspecific2Rank",entry.get("infraspecific2Rank").toString());
		model.set("infraspecific2Epithet",entry.get("infraspecific2Epithet").toString());
		model.set("infraspecific2EpithetScore",entry.get("infraspecific2EpithetScore").toString());
		model.set("acceptance",entry.get("acceptance").toString());
		model.set("submittedFamily", entry.get("submittedFamily").toString());
		model.set("acceptedName",entry.get("acceptedName").toString());
		model.set("acceptedNameUrl",entry.get("acceptedNameUrl").toString());
		model.set("acceptedAuthor", entry.get("acceptedAuthor").toString());
		model.set("groupSize", groupSize);
		model.set("source",entry.get("Source").toString());
		model.set("flag", Integer.parseInt(entry.get("flag").toString()));
	}
	
}
