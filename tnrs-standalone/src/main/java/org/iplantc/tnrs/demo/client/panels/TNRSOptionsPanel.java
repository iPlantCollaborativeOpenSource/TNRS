/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;


import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author raygoza
 *
 */
public class TNRSOptionsPanel extends TabPanel{

	
	private TNRSFilteringPanel filteringPanel;
	private TNRSProcessingModePanel modePanel;
//	private TNRSClassificationPanel classPanel;
	
	public TNRSOptionsPanel() {
		init();
		compose();
	}
	
	
	public void init() {
		
		setSize(450, 241);
		setPlain(true);
	}
	
	public void compose() {
		//buildProcessModeTab();
		//add(buildFilteringTab());
		//add(buildSourcesTab());
		//add(buildClassificationTab());
		//layout();
	}
	
	
	public TabItem buildProcessModeTab() {
		TabItem item = new TabItem();
		item.setText("Processing Mode");
		
	//	modePanel = new TNRSProcessingModePanel();
		
		item.add(modePanel);
		return item;
	}
	
	public TabItem buildSourcesTab() {
		TabItem item = new TabItem();
		item.setText("Sources");
		
	//	item.add(new TNRSSourcesPanel());
		
		item.setVisible(false);
		
		return item;
	}
	
	public JSONObject settings(){
		JSONObject settings = new JSONObject();
		 
		settings.put("mode", new JSONString(modePanel.selectedValue()));

		return settings;
	}
	
	
	public TabItem buildFilteringTab() {
		
		TabItem item = new TabItem();
		
		item.setText("Filtering");
		filteringPanel = new TNRSFilteringPanel();
		item.add(filteringPanel);
		
		return item;
	}
	
	
	public TabItem buildClassificationTab() {
		
		TabItem item = new TabItem();
		
		item.setText("Classification");
	//	classPanel = new TNRSClassificationPanel();
		//item.add(classPanel);
		
		return item;
	}
	
	public String getSettings(){

		JSONObject json = new JSONObject();

		String type = "";

		json.put("sensitivity", new JSONString("0.05"));
		json.put("mode", new JSONString("matching"));
		/*if(type.equals("Perform name resolution")){
			
		}else{
			json.put("mode", new JSONString("parsing"));
		}*/

		json.put("taxonomic", new JSONString("true"));
		
		return json.toString();
	}
	
}
