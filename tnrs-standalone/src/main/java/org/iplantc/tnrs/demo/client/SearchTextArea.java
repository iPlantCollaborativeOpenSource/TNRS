package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.widget.form.TextArea;

public class SearchTextArea extends TextArea
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterRender()
	{
		super.afterRender();
		el().setElementAttribute("spellcheck", "false");		
	}
}
