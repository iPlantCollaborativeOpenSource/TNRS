package org.iplantc.tnrs.demo.client;

import org.iplantc.tnrs.demo.client.panels.TNRSPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TNRSDemo implements EntryPoint{
	private ApplicationLayout layoutApplication;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		setEntryPointTitle();

		layoutApplication = new ApplicationLayout();
				
		
		//History.addValueChangeHandler(this);
		//History.fireCurrentHistoryState();
		
		RootPanel.get().add(layoutApplication);
		
		layoutApplication.replaceCenterPanel(new TNRSPanel());
	}

	private void setEntryPointTitle()
	{
		Window.setTitle("Taxonomic Name Resolution Service ");
	}
	
	
	
	
	
}
