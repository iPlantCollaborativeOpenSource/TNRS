package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.client.gxt.HelpIcon;
import org.iplantc.tnrs.demo.client.validation.FileNameInputValidator;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Label;


public class TNRSDownloadDialog extends Dialog{


	private RadioGroup ret1;
	private RadioGroup ret2;
	private final ClientCommand cmdOk;
	private TextField<String> file_name;
	private ComboBox<Encoding> encoding;
	private ListStore<Encoding> options;
	private String mode;
	private boolean sort_by_source;
	private boolean taxonomic;
	private boolean dirty;
	
	public TNRSDownloadDialog(ClientCommand cmdOk,String mode,boolean isdirty,boolean sources_n,boolean taxonomic_constraint) {
		this.mode = mode;
		init();
		compose();
		sort_by_source = sources_n;
		dirty = isdirty;
		taxonomic = taxonomic_constraint;
		this.cmdOk =cmdOk;
	}

	public void init() {
		if(mode.equals("matching")){
			setSize(200,380);
		}else{
			setSize(200,180);
		}
		setLayout(new FitLayout());
		setHeading("Download Options");
		setStyleAttribute("background-color", "#EDEDED");
		setModal(true);
		setButtons(Dialog.OKCANCEL);
		setHideOnButtonClick(false);

	}


	public RadioGroup buildOptions1() {

		ret1 = new RadioGroup();

		Radio down_sel = new Radio();
		down_sel.setBoxLabel("Best matches only&nbsp;&nbsp;<img src=\"images/q.png\" />");
		down_sel.setStyleAttribute("background-color", "#EDEDED");
		down_sel.setValue(true);
		down_sel.setToolTip("Include only the single best match for each name");
		down_sel.setId("Best");
		
		Radio down_best = new Radio();
		down_best.setBoxLabel("All matches &nbsp;&nbsp;<img src=\"images/q.png\" />");
		down_best.setStyleAttribute("background-color", "#EDEDED");
		down_best.setValue(true);
		down_best.setToolTip("Include all possible matches to each name");
		down_best.setId("All");


		ret1.add(down_sel);
		ret1.add(down_best);

		ret1.setOrientation(Orientation.VERTICAL);


		return ret1;
	}

	public RadioGroup buildOptions2() {

		ret2 = new RadioGroup();

		Radio down_simple = new Radio();
		down_simple.setBoxLabel("Simple &nbsp;&nbsp;<img src=\"images/q.png\" />");
		down_simple.setStyleAttribute("background-color", "#EDEDED");
		down_simple.setValue(true);
		down_simple.setToolTip("Include only basic information on names matched");
		down_simple.setId("Simple");
		

		Radio down_detailed = new Radio();
		down_detailed.setBoxLabel("Detailed &nbsp;&nbsp;<img src=\"images/q.png\" />");
		down_detailed.setStyleAttribute("background-color", "#EDEDED");
		down_detailed.setToolTip("Full information on each name matched, including parsed name components, links to sources and warnings");
		down_detailed.setId("Detailed");
		
		ret2.add(down_simple);
		ret2.add(down_detailed);
		ret2.setOrientation(Orientation.VERTICAL);
		return ret2;
	}


	public void compose() {

		VerticalPanel pnlInner = new VerticalPanel();

		pnlInner.setSpacing(5);
		pnlInner.setStyleAttribute("background-color", "#EDEDED");
		if(mode.equals("matching")){
			FieldSet set1 = new FieldSet();
			set1.setLayout(new FitLayout());
			set1.setHeading("Results to download");
			set1.add(buildOptions1());

			FieldSet set2 = new FieldSet();
			set2.setLayout(new FitLayout());
			set2.setHeading("Download format");
			set2.add(buildOptions2());
			
			pnlInner.add(set1);
			pnlInner.add(set2);
		}
		file_name = new TextField<String>();
		file_name.setEmptyText("tnrs_results");
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new ColumnLayout());

		container.add(file_name);
		
		container.add(new HelpIcon("File will be downloaded in .csv format. Provide the name of the file without the extension"));

		
		pnlInner.add(new Label("Download as:"));
		pnlInner.add(container);

		LayoutContainer container2 = new LayoutContainer(new ColumnLayout());
		container.add(new Label("Encoding:"));
		encoding = new ComboBox<Encoding>();
		options = new ListStore<Encoding>();

		options.add(new Encoding("UTF-16 (Ms Excel)"));
		options.add(new Encoding("UTF-8"));

		List<Encoding> list = new ArrayList<Encoding>();
		list.add(new Encoding("UTF-16 (Ms Excel)"));
		encoding.setEmptyText("UTF-16 (Ms Excel) ");
		encoding.setDisplayField("name");
		encoding.setEditable(false);
		encoding.setStore(options);
		encoding.setTriggerAction(TriggerAction.ALL);
		encoding.setSelection(list);

		
		pnlInner.add(encoding);
		pnlInner.add(container2);
		add(pnlInner);
		setupEventHandlers();
		layout();
	}


	private void setupEventHandlers()
	{

		Button btn = getButtonById(Dialog.CANCEL);

		btn.addSelectionListener(new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				hide();
			}
				});

		btn = getButtonById(Dialog.OK);

		// handle ok button
		btn.addSelectionListener(new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				if(!file_name.isValid()) {
					file_name.focus();
					return;
				}

				if(cmdOk != null)
				{

					hide();
					JSONObject json = new JSONObject();
					String filename ="tnrs_results.txt";
					if(file_name.getValue()!=null) {
						filename = file_name.getValue() +".txt";
					}
					json.put("name", new JSONString(filename));
					if(mode.equals("matching")){
						
						json.put("mode", new JSONString(ret1.getValue().getId()));
						json.put("type", new JSONString(ret2.getValue().getId()));
					}
					String enc ="utf16";

					if(!encoding.getRawValue().toLowerCase().contains("utf-16")){
						enc="utf8";
					}
					json.put("encoding", new JSONString(enc));
					json.put("dirty", new JSONString(Boolean.toString(dirty)));
					json.put("sources", new JSONString(Boolean.toString(sort_by_source)));
					json.put("taxonomic", new JSONString(Boolean.toString(taxonomic)));
					cmdOk.execute(json.toString());
				}
			}
				});






	}


}

class Encoding extends BaseModelData {

	public Encoding(String name){
		set("name",name);
	}

	public String getName(){
		return get("name");
	}

	public void setName(String name){
		set("name",name);
	}

}
