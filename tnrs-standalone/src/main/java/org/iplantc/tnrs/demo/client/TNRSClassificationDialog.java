/**
 * 
 */
package org.iplantc.tnrs.demo.client;


import org.iplantc.tnrs.demo.client.model.Classification;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author raygoza
 *
 */
public class TNRSClassificationDialog extends Dialog {

	private TreePanel<Classification> treePanel;
	
	public TNRSClassificationDialog() {
		init();
		compose();
		
	}
	
	
	public void init() {
		setSize(300, 500);
	}
	
	public void compose() {
		setLayout(new FitLayout());
		treePanel = new TreePanel<Classification>(buildStore());
		treePanel.setDisplayProperty("name");
		treePanel.setWidth(390);
		treePanel.setCheckable(true);
		treePanel.setAutoLoad(true);
		treePanel.setStyleAttribute("backgroundColor", "white");
		treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treePanel.addCheckListener(new CheckChangedListener<Classification>(){
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.CheckChangedListener#handleEvent(com.extjs.gxt.ui.client.event.CheckChangedEvent)
			 */
			@Override
			public void handleEvent(CheckChangedEvent<Classification> ce) {
				TreePanel<Classification> tree = (TreePanel<Classification>)ce.getSource();
				
				Classification cls = tree.getSelectionModel().getSelectedItem();
				
				
			}
		});
		
		
		add(treePanel);
	}
	
	
	private TreeStore<Classification> buildStore(){
		
		TreeStore<Classification> ts = new TreeStore<Classification>();
		Classification cls = getClassificationModel();
		
		ts.add(cls, true);
		
		return ts;
	}
	
	
	private Classification getClassificationModel() {
		
		String fakeJSon ="{\"classification\" : [{\"name\" : \"Non-green Plants\"},{\"name\" : \"Green Plants (Viridiplantae)\",\"children\" : [{\"name\" : \"Green plants exlcuding land plants (non-embryophyte Viridiplantae)\"},{\"name\" : \"Land Plants (Enbryophyta)\",\"children\" : [{\"name\" : \"Hornworts (Anthocerotophyta)\"},{\"name\" : \"Liverworts (Marchatiophyta)\"},{\"name\" : \"Mosses (Bryophyta)\"},{\"name\" : \"Vascular plants (Tracheophyta)\",\"children\" : [{\"name\" : \"Lycophytes (Lycopodiophyta)\"},{\"name\" : \"Ferns, horsetails and allies (Monlliformopses)\"},{\"name\" : \"Seed Plants\",\"children\" : [{\"name\" : \"Conifers (Coniferophyta)\"},{\"name\" : \"Cycads (Cycadophyta)\"},{\"name\" : \"Ginkgos (Ginkigophyta)\"},{\"name\" : \"Gnetum and Ephedra (Gnetophyta)\"},{\"name\" : \"Flowering Plants (Magnoliophyta)\",\"children\" : [{\"name\" : \"Basil magnoliophytes\"},{\"name\" : \"Eudicots (Including Ceratophyllaies)\"},{\"name\" : \"Magnollids\"},{\"name\" : \"Monocots\"}]}]}]}]}]}]}";
		
		
		Classification cls = new Classification("classification");
		
		JSONObject classif = (JSONObject) JSONParser.parseStrict(fakeJSon);
		
		JSONArray jsa = classif.get("classification").isArray();
		
		for(int i=0; i < jsa.size(); i++) {
			parseJson(cls, (JSONObject)jsa.get(i));
		}
		
		return cls;
		
	}
	
	
	public void parseJson(Classification parent, JSONObject child) {
		
		Classification cls = new Classification(child.get("name").toString().replace("\"", ""));
		
		if(child.containsKey("children")) {
			
			JSONArray children = child.get("children").isArray();
			
			for(int i=0; i < children.size(); i++) {
				parseJson(cls, (JSONObject)children.get(i));
			}
			
		}
		
		parent.add(cls);
	}
	
}
