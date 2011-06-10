package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;

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
		setWidth(680);
		setHeight(240);
	}

	private void displayText()
	{
		VerticalPanel pnlInner = new VerticalPanel();
		
		pnlInner.setSpacing(5);
		Label welcome = new Label(
				"TNRS allows you to validate and correct a list of plant names against an authoritative database of published scientific names and authorities.  \n" + 
				"\n<br/><br/>" + 
				"You may enter names on the fly by typing or pasting up to 5000 names in the Enter List tab to the left. \n" + 
				"\n<br/><br/>" + 
				"Alternatively, you can upload a text file with an unlimited number of names in the Upload and Submit List tab. Your file MUST be plain text, with one name per line, and must have a .csv or .txt filename extension; word processor or spreadsheet files are NOT supported. You will receive an email notification when the list is done being processed. You can retrieve your results at that time in the Retrieve Results tab. Click the links above for more detailed instructions and other information.\n" + 
				"\n" + 
				"");
		
		welcome.setStyleAttribute("fontSize", "12px");
		
		pnlInner.add(welcome);
		
		add(pnlInner);
	}
}
