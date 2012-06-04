/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;

import org.iplantc.tnrs.demo.client.EditSettingsDialog;
import org.iplantc.tnrs.demo.client.gxt.HelpIcon;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Anchor;

/**
 * @author raygoza
 *
 */
public class SettingsSummaryPanel extends ContentPanel {

	private EditSettingsDialog dialog;
	private Label modeLabel;
	private Label accuracyLabel;
	private Label taxonomicLabel;
	private Label accuracy;
	private Label constrain;
	private Label sourceName;
	private Label classificiationLabel;
	private LayoutContainer accuracyDisplay;
	private LayoutContainer sourcesDisplay;
	private LayoutContainer classificationDisplay;
	
	
	
	public SettingsSummaryPanel(){
		
		init();
		compose();
		
	}
	
	
	public void init(){
		setHeading("<img src=\"images/settings.jpg\"/>&nbsp;&nbsp;Name processing settings");
		setFrame(true);
		setWidth(580);
		setHeight(240);
		setLayout(new FitLayout());
	}
	
	
	public void setSettingsDialog(EditSettingsDialog dlg){
		dialog= dlg;
		dialog.setModal(true);
		dialog.addListener(Events.Hide, new Listener<ComponentEvent>() {
			
			public void handleEvent(ComponentEvent be) {
				update();
			};
			
		});
	}
	
	public void compose(){
		VerticalPanel pnlInner= new VerticalPanel();
		
		
		pnlInner.add(buildProcessingModeDisplay());
		pnlInner.add(new Html("<div>&nbsp;</div>"));
		accuracyDisplay = buildAccuracyDisplay();
		pnlInner.add(accuracyDisplay);
		pnlInner.add(new Html("<div>&nbsp;</div>"));
		sourcesDisplay = buildSourceRankingDisplay();
		pnlInner.add(sourcesDisplay);
		pnlInner.add(new Html("<div>&nbsp;</div>"));
		classificationDisplay = buildClassificationDisplay(); 
		pnlInner.add(classificationDisplay);
		pnlInner.add(new Html("<div>&nbsp;</div>"));
		
		add(pnlInner);
		
		
	}
	
	
	
	
	private LayoutContainer buildAccuracyDisplay(){
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		LayoutContainer header = new LayoutContainer();
		header.setLayout(new ColumnLayout());
		accuracy = new Label("Match Accuracy:&nbsp;&nbsp;");
		header.add(accuracy);
		
		Anchor change = new Anchor("Edit");
		change.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
				dialog.showTab("accuracy");
				update();
			}
		});
		
		header.add(change);
		header.add(new HelpIcon("Match accuracy","instructions.html#match"));
		accuracyLabel= new Label("");
		container.add(header);
		container.add(accuracyLabel);
		return container;
	}
	
	
	private LayoutContainer buildClassificationDisplay(){
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		LayoutContainer header = new LayoutContainer();
		header.setLayout(new ColumnLayout());
		header.add(new Label("Family Classification:&nbsp;&nbsp;"));
		
		Anchor change = new Anchor("Edit");
		change.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
				dialog.showTab("classification");
				update();
			}
		});
		
		header.add(change);
		header.add(new HelpIcon("Classification","instructions.html#classification"));
		classificiationLabel = new Label("");
		container.add(header);
		container.add(classificiationLabel);
		
		return container;
	}
	
	private LayoutContainer buildSourceRankingDisplay(){
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		LayoutContainer header = new LayoutContainer();
		header.setLayout(new ColumnLayout());
		header.add(new Label("Sources:&nbsp;&nbsp;"));
		
		Anchor change = new Anchor("Edit");
		change.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
				dialog.showTab("sources");
				update();
			}
		});
		
		header.add(change);
		header.add(new HelpIcon("Sources","instructions.html#source_selection"));
		sourceName = new Label("");
		container.add(header);
		container.add(sourceName);
		
		return container;
	}
	
	private LayoutContainer buildProcessingModeDisplay(){
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		LayoutContainer header = new LayoutContainer();
		header.setLayout(new ColumnLayout());
		header.add(new Label("Processing Mode:&nbsp;&nbsp;"));
		
		Anchor change = new Anchor("Edit");
		change.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
				dialog.showTab("processing");
				update();
			}
		});
		
		header.add(change);
		header.add(new HelpIcon("Processing Mode","instructions.html#processing"));
		modeLabel = new Label("");
		container.add(header);
		container.add(modeLabel);
		
		return container;
	}
	
	private Button buildSettingsButton() {
		
		Button classify = new Button("Change settings");
		
		classify.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				dialog.setModal(true);
				dialog.showTab("accuracy");
				update();
				layout();
			}
		});
		return classify;
	}
	
	public void update(){
		
	
		
		JSONObject json = (JSONObject)JSONParser.parseStrict(dialog.getSettings());
		sourceName.setText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+json.get("sources").toString().replace("\"", ""));
		String msg = "Allow partial matches, ";
		
		if(json.get("match_to_rank").toString().contains("false")){
			
			msg= "Don't " + msg.toLowerCase();
		}
		
		
		if(json.get("mode").toString().replace("\"", "").equals("matching")){
			modeLabel.setText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Selected mode:&nbsp;&nbsp; Perform Name Resolution");
			classificiationLabel.setText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Selected classification source: "+json.get("classification").toString().replace("\"", "").toString().toUpperCase());
			accuracyLabel.setText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+msg+" Selected minimum threshold: "+json.get("sensitivity").toString().replace("\"", ""));
			accuracyDisplay.setVisible(true);
			classificationDisplay.setVisible(true);
			sourcesDisplay.setVisible(true);
		
		}else{
			modeLabel.setText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Perform Name Parsing");
			accuracyDisplay.setVisible(false);
			classificationDisplay.setVisible(false);
			sourcesDisplay.setVisible(false);
		}
		
		boolean taxonomic = Boolean.parseBoolean(json.get("taxonomic").toString().replace("\"",	 ""));
		
		
		
	}
	
}
