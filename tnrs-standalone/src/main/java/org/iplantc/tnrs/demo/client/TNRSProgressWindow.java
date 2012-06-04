/**
 * 
 */
package org.iplantc.tnrs.demo.client;


import org.iplantc.tnrs.demo.client.panels.TNRSProgressPanel;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author raygoza
 *
 */
public class TNRSProgressWindow extends Window{

	private TNRSProgressPanel pane;
	
	/**
	 * 
	 */
	public TNRSProgressWindow() {
		init();
		compose();
	}
	
	private void compose() {
		setSize(400,100);
		setClosable(false);
		setLayout(new FitLayout());
	}
	
	private void init() {
		
		pane = new TNRSProgressPanel(0.00001,false);
		pane.setHeaderVisible(false);
		pane.enableSpinnerImage();
		add(pane);
		layout();
	}
	
	public void updateProgress(double pct) {
		pane.updateProgress(pct);
		
	}
	
}
