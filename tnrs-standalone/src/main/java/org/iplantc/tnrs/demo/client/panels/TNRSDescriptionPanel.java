package org.iplantc.tnrs.demo.client.panels;

import org.iplantc.tnrs.demo.client.TNRSSupportWindow;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class TNRSDescriptionPanel extends ContentPanel
{
	public TNRSDescriptionPanel()
	{
		init();
		displayText();
	}

	private void init()
	{
		setHeading("Welcome");
		
		setFrame(true);
		setWidth(580);
		setHeight(240); 
	}

	private void displayText()
	{
		VerticalPanel pnlInner = new VerticalPanel();
		
		pnlInner.setSpacing(5);
		Label welcome = new Label(
				"TNRS allows you to validate and correct a list of plant names against an authoritative database of published scientific names and authorities.  \n" + 
				"\n<br/>" + 
				"You may enter names on the fly by typing or pasting up to 5000 names in the Enter List tab to the left. \n" + 
				"\n<br/><br/>" + 
				"Alternatively, you can upload a text file with an unlimited number of names in the Upload and Submit List tab. Your file MUST be plain text, with one name per line, and must have a .csv or .txt filename extension; word processor or spreadsheet files are NOT supported. You will receive an email notification when the list is done being processed. You can retrieve your results at that time in the Retrieve Results tab. Click the links above for more detailed <a href=\"instructions.html\">instructions</a> and other information.\n" + 
				"\n" 
				);
		
		welcome.setStyleAttribute("fontSize", "12px");
		
		pnlInner.add(welcome);
		Anchor support = new Anchor("Click here for support");
		support.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				TNRSSupportWindow support = new TNRSSupportWindow();
				
				support.setModal(true);
				support.show();
			}
		});
		pnlInner.add(support);
		add(pnlInner);
	}
}
