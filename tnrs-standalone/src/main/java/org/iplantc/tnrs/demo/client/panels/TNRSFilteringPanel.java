/**
 * 
 */
package org.iplantc.tnrs.demo.client.panels;

//import org.iplantc.tnrs.demo.client.panels.TNRSClassificationDialog;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;


/**
 * @author raygoza
 *
 */
public class TNRSFilteringPanel extends VerticalPanel {


	private Slider sensitivity;
	private RadioGroup group;
	private CheckBox HTSort;
	private Label msg;
	private Label msg2;
	private Label threshold;
	private CheckBox match_to_rank;

	public TNRSFilteringPanel() {
		init();
		compose();
	}

	public void init() {
		setAutoHeight(true);
		setAutoWidth(true);
	}

	public void compose() {
		setLayout(new FitLayout());
		add(buildOptionsPanel());
		setStyleAttribute("backgroundColor", "#F1F1F1");
	}


	public VerticalPanel buildOptionsPanel(){

		VerticalPanel  panel = new VerticalPanel();
		panel.setLayout(new CenterLayout());
		panel.setStyleAttribute("padding", "5px");
		panel.setStyleAttribute("backgroundColor", "#F1F1F1");
	
		match_to_rank = new CheckBox();
		match_to_rank.setHideLabel(true);
		match_to_rank.setBoxLabel("Allow partial matching");
		panel.add(match_to_rank);
		Label exp = new Label();
		exp.setText("When selected, the TNRS will match to a higher taxon if a match to the full name submitted cannot be found." +
				" If not selected and not match is found to the full taxon name, you will be returned a status of \"no suitable matches\" for the name submitted.");
		exp.setStyleAttribute("fontSize", "11px");
		match_to_rank.setValue(true);
		panel.add(exp);
		msg = new Label("Set Match Accuracy");
		msg2 = new Label("Adjust the match sensitivity you would like to use for your list resolution.");
		threshold = new Label();

		sensitivity = new Slider();
		sensitivity.setMinValue(5);
		sensitivity.setMaxValue(100);
		sensitivity.setIncrement(1);
		sensitivity.setWidth(250);
		sensitivity.addListener(Events.Change, new Listener<SliderEvent>() {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
			 */
			@Override
			public void handleEvent(SliderEvent be) {
				Slider sl = (Slider)be.getSource();
				sl.setMessage(((double)sl.getValue()/100.0)+"");
			}
		});
		sensitivity.setValue(5);

		LayoutContainer lc = new LayoutContainer(new ColumnLayout());

		Label allow_fuzzy = new Label("Allow full fuzzy<br/>matching");
		allow_fuzzy.setStyleAttribute("fontSize", "10px");
		allow_fuzzy.setStyleAttribute("paddingRight", "4px");
		Label exact_match = new Label("Exact match<br/> only");
		exact_match.setStyleAttribute("paddingLeft", "4px");
		exact_match.setStyleAttribute("fontSize", "10px");
		lc.add(allow_fuzzy);
		lc.add(sensitivity);
		lc.add(exact_match);
		panel.add(new Html("<div>&nbsp; </div>"));
		panel.add(msg);
		panel.add(new Html("<div>&nbsp; </div>"));
		panel.add(msg2);
		panel.add(new Html("<div>&nbsp; </div>"));
		panel.add(lc);
		
		panel.add(new Html("<div>&nbsp; </div>"));
		panel.add(new Html("<div>&nbsp; </div>"));
		
		panel.setSize(420, 300);

		
		return panel;

	}

	public JSONObject selectedValues(){
		JSONObject json = new JSONObject();
		json.put("sensitivity", new JSONString(((double)sensitivity.getValue()/100.0)+""));
		json.put("match_rank", new JSONString(Boolean.toString(match_to_rank.getValue())));
		return json;	
	}


	

	

}
