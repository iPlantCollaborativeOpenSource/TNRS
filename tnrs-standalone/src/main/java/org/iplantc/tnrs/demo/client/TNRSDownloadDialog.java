package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TNRSDownloadDialog extends Dialog{

	
	private RadioGroup ret1;
	private RadioGroup ret2;
	private final ClientCommand cmdOk;
	
	public TNRSDownloadDialog(ClientCommand cmdOk) {
		init();
		compose();
		this.cmdOk =cmdOk;
	}
	
	public void init() {
		setSize(200,260);
		setLayout(new FitLayout());
		setHeading("Download Options");
		setStyleAttribute("background-color", "#EDEDED");
		
		setButtons(Dialog.OKCANCEL);
		setHideOnButtonClick(true);
		
	}
	
	
	public RadioGroup buildOptions1() {
		
		ret1 = new RadioGroup();
		
		Radio down_sel = new Radio();
		down_sel.setBoxLabel("Best matches only");
		down_sel.setStyleAttribute("background-color", "#EDEDED");
		down_sel.setValue(true);
		
		Radio down_best = new Radio();
		down_best.setBoxLabel("All matches");
		down_best.setStyleAttribute("background-color", "#EDEDED");
		down_best.setValue(true);
		
		
		
		ret1.add(down_sel);
		ret1.add(down_best);
		
		ret1.setOrientation(Orientation.VERTICAL);
		
		
		return ret1;
	}
	
	public RadioGroup buildOptions2() {
		
		ret2 = new RadioGroup();
		
		Radio down_all = new Radio();
		down_all.setBoxLabel("Simple");
		down_all.setStyleAttribute("background-color", "#EDEDED");
		down_all.setValue(true);
		
		Radio down_det = new Radio();
		down_det.setBoxLabel("Detailed");
		down_det.setStyleAttribute("background-color", "#EDEDED");
		
		ret2.add(down_all);
		ret2.add(down_det);
		ret2.setOrientation(Orientation.VERTICAL);
		return ret2;
	}
	
	
	public void compose() {
		
		VerticalPanel pnlInner = new VerticalPanel();
		
		pnlInner.setSpacing(5);
		pnlInner.setStyleAttribute("background-color", "#EDEDED");
		
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
		add(pnlInner);
		setupEventHandlers();
		layout();
	}
	
	
	private void setupEventHandlers()
	{
		Button btn = getButtonById(Dialog.OK);
		
		// handle ok button
		btn.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				
				
				if(cmdOk != null)
				{
					//MessageBox.alert("",ret.getValue().getBoxLabel() ,null);
					cmdOk.execute(ret1.getValue().getBoxLabel()+"#"+ret2.getValue().getBoxLabel());
				}
			}
		});
		
		btn = getButtonById(Dialog.CANCEL);
		
		btn.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				
			}
		});
	}
	
	
}
