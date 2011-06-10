package org.iplantc.tnrs.demo.client.views;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;

/**
 * 
 * A Hyper link widget for Discovery Environment
 * 
 * @author sriram
 * 
 */
public class Hyperlink extends Html
{

	/**
	 * Create a new instance of Hyperlink with given text and default style
	 * 
	 * @param text a display string
	 */
	public Hyperlink(String text)
	{
		super(text);
		setStyleName("de_hyperlink");
		init();
	}

	/**
	 * Create a new instance of Hyperlink with given text and style
	 * 
	 * @param text a display string
	 * @param styleName a css style name
	 */
	public Hyperlink(String text, String styleName)
	{
		super(text);
		setStyleName(styleName);
		init();
	}

	/**
	 * set style attribute for Mouse Over event
	 * 
	 * @param attribute a style attribute like text-decoration
	 * @param style a style value like underline
	 */
	public void setOnMouseOverStyle(final String attribute, final String style)
	{
		addListener(Events.OnMouseOver, new Listener<ComponentEvent>()
		{
			@Override
			public void handleEvent(ComponentEvent be)
			{
				setStyleAttribute(attribute, style);

			}
		});
	}

	/**
	 * set style attribute for Mouse out event
	 * 
	 * @param attribute a style attribute like text-decoration
	 * @param style a style value like underline
	 */
	
	public void setOnMouseOutStyle(final String attribute, final String style)
	{
		addListener(Events.OnMouseOut, new Listener<ComponentEvent>()
		{
			@Override
			public void handleEvent(ComponentEvent be)
			{
				setStyleAttribute(attribute, style);

			}
		});
	}

	private void init()
	{
		sinkEvents(Events.OnClick.getEventCode());
		sinkEvents(Events.OnMouseOver.getEventCode());
		sinkEvents(Events.OnMouseOut.getEventCode());
		setOnMouseOverStyle("text-decoration", "underline");
		setOnMouseOutStyle("text-decoration", "none");
		
	}

}
