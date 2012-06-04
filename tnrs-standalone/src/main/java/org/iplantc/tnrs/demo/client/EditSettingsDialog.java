/**
 * 
 */
package org.iplantc.tnrs.demo.client;


import org.iplantc.tnrs.demo.client.panels.SettingsSummaryPanel;
import org.iplantc.tnrs.demo.client.panels.TNRSClassificationPanel;
import org.iplantc.tnrs.demo.client.panels.TNRSFilteringPanel;
import org.iplantc.tnrs.demo.client.panels.TNRSProcessingModePanel;
import org.iplantc.tnrs.demo.client.panels.TNRSSourcesPanel;


import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author raygoza
 *
 */
public class EditSettingsDialog extends Dialog {

	
	private TabPanel tab_panel;
	private TNRSFilteringPanel filteringPanel;
	private TNRSSourcesPanel sourcesPanel;
	private TNRSProcessingModePanel processingModePanel;
	private TNRSClassificationPanel classificationPanel;
	private SearchServiceAsync service;
	private SettingsSummaryPanel panel;
	private TabItem sources;
	private TabItem classification; 
	private TabItem threshold;
	
	public EditSettingsDialog(SearchServiceAsync service,final SettingsSummaryPanel panel){
	
		this.service = service;
		
	
		
		service.getSources(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String arg0) {
				
				JSONObject json = (JSONObject)JSONParser.parseStrict(arg0);
				
				JSONArray data = (JSONArray)json.get("data");
				ListStore<SourceEntry> entries = new ListStore<SourceEntry>();
				for(int i=0;i < data.size();i++) {
					JSONObject jsono = (JSONObject)data.get(i);
					SourceEntry entry = new SourceEntry();
					entry.setName(jsono.get("sourceName").toString().replace("\"", "").toUpperCase());
					entry.setAccessedDate(jsono.get("dateAccessed").toString().replace("\"", "").toUpperCase());
					entry.setWarning(Integer.parseInt(jsono.get("warning").toString().replace("\"", "")));
					entry.setRank(i+1);
					if(Integer.parseInt(entry.get("warning").toString())==0){
						entry.setInclude(Boolean.TRUE);
					}else{
						entry.setInclude(Boolean.FALSE);
					}
					entries.add(entry);
				}
				sourcesPanel = new TNRSSourcesPanel(entries);
				init();
				compose();
				
				panel.update();
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
	
	
	public void init(){
		setSize(440,400);
		setLayout(new FitLayout());
		filteringPanel= new TNRSFilteringPanel();
		setHideOnButtonClick(true);
		
		classificationPanel = new TNRSClassificationPanel();
		sourcesPanel.setClassificationPanel(classificationPanel);
		processingModePanel = new TNRSProcessingModePanel(this);
		setHeading("Edit Settings");
	}
	
	public void compose(){
		tab_panel = new TabPanel();
		tab_panel.setPlain(true);
		threshold = new TabItem("Match Accuracy");
		threshold.add(filteringPanel);
		tab_panel.add(threshold);
		
		
		sources = new TabItem("Sources");
		sources.add(sourcesPanel);
		tab_panel.add(sources);
		
		
		
		classification = new TabItem("Classification");
		classification.add(classificationPanel);
		tab_panel.add(classification);
		
		TabItem mode = new TabItem("Processing Mode");
		mode.add(processingModePanel);
		tab_panel.add(mode);
		
		 
		setButtons(Dialog.OK);
		add(tab_panel);
		
		layout();
	}
	
	
	public void showTab(String tabname){
		if(tabname.equalsIgnoreCase("accuracy")){
			tab_panel.setSelection(tab_panel.getItem(0));
		}else if(tabname.equalsIgnoreCase("sources")){
			tab_panel.setSelection(tab_panel.getItem(1));
		}else if(tabname.equalsIgnoreCase("classification")){
			tab_panel.setSelection(tab_panel.getItem(2));
		}else if(tabname.equalsIgnoreCase("processing")){
			tab_panel.setSelection(tab_panel.getItem(3));
		}
		
		
		
		show();
	}
	
	
	public void disableMatchinOptionsPanels(){
		classification.disable();
		threshold.disable();
		sources.disable();
	}
	
	
	public void enableMatchingOptions(){
			classification.enable();
			threshold.enable();
			sources.enable();
	}
	
	public String getSettings(){
		
		

			JSONObject json = new JSONObject();

			String type = "";

			JSONObject filtering = filteringPanel.selectedValues();
			
			json.put("sensitivity",new JSONString(filtering.get("sensitivity").toString().replace("\"", "")));
			json.put("match_to_rank", new JSONString(filtering.get("match_rank").toString().replace("\"", "")));
			json.put("mode", new JSONString(processingModePanel.selectedValue()));
			json.put("classification", new JSONString(classificationPanel.selectedValue()));
			//json.put("source_ranking", jsonValue)
			
			json.put("taxonomic", new JSONString("true"));
			json.put("sources", new JSONString(sourcesPanel.selectedValues()));
			return json.toString();
		
		
		
		
	}
	
	
	
	
}
