package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BorderLayoutEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Element;

/**
 * Defines the overall layout for the root panel of the web application.
 * 
 * @author sriram
 */
public class ApplicationLayout extends Viewport
{
	private ContentPanel north;
	private Component center;
		
	private final BorderLayout layout;

	private HeaderPanel headerPanel;

	/**
	 * Default constructor.
	 */
	public ApplicationLayout()
	{
		// build top level layout
		layout = new BorderLayout();

		// make sure we re-draw when a panel expands
		layout.addListener(Events.Expand, new Listener<BorderLayoutEvent>()
		{
			public void handleEvent(BorderLayoutEvent be)
			{
				layout();
			}
		});

		setLayout(layout);

		north = new ContentPanel();		
	}

	private void assembleHeader()
	{
		drawHeader();
		north.add(headerPanel);
	}

	private void drawHeader()
	{
		headerPanel = new HeaderPanel();
		headerPanel.setBorders(false);
		headerPanel.setHeight(117);
	}

	private void drawNorth()
	{
		north.setHeaderVisible(false);
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 117);
		data.setCollapsible(false);
		data.setFloatable(false);
		data.setHideCollapseTool(true);
		data.setSplit(false);
		data.setMargins(new Margins(0, 0, 0, 0));

		add(north, data);
	}

	/**
	 * Replace the contents of the center panel.
	 * 
	 * @param view a new component to set in the center of the BorderLayout.
	 */
	public void replaceCenterPanel(Component view)
	{
		if(center != null)
		{
			remove(center);
		}

		center = view;
				
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setMargins(new Margins(0));
				
		if(center != null)
		{
			add(center, data);
		}

		layout();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		
		drawNorth();

		assembleHeader();
	}
		
	class HeaderPanel extends HorizontalPanel
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void afterRender()
		{
			super.afterRender();
			el().createChild("<div class=\"header\"> \n" + 
					"        <span id=\"logo\">\n" + 
					"        <img src=\"images/iplant_logo.png\" alt= \"iPlant Collaborative\" /> \n" + 
					"      </span>\n" + 
					"    <div class=\"subheader\">\n" + 
					"<span>Taxonomic Name Resolution Service</span> <span style=\"font-size: 24px;\">v3.0</span>\n" + 
					"    </div>\n" + 
					"  </div>");		
		}	
	}
}

