/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.Element;

/**
 * @author raygoza
 *
 */
public class TNRSEditorPanel extends LayoutContainer {

	protected PagingToolBar toolBar;
	protected ContentPanel download_panel;
	
	protected TNRSEditorPanel() {
		init();
	}
	
	// build column with custom renderer
	protected ColumnConfig buildConfig(String id, String caption, int width,
			HorizontalAlignment alignment, final GridCellRenderer<?> renderer)
	{
		ColumnConfig ret = new ColumnConfig(id, caption, width);



		ret.setMenuDisabled(true);
		ret.setSortable(true);
		ret.setAlignment(alignment);

		if(renderer != null)
		{
			ret.setRenderer(renderer);
		}

		return ret;
	}
	
	private void init()
	{

		setSize(1200, 330);
		setBorders(false);
		setStyleAttribute("margin", "5px");
		setLayout(new FitLayout());
		Element el = XDOM.getElementById("toggle");
		el.setAttribute("flag", "true");
		download_panel = new ContentPanel();
		download_panel = new ContentPanel();
		download_panel.setFrame(false);
		download_panel.setSize(1, 1);
		download_panel.setVisible(false);
	}

	// build column without custom renderer
	protected ColumnConfig buildConfig(final String id, final String caption, int width,
			final HorizontalAlignment alignment)
	{
		return buildConfig(id, caption, width, alignment, null);
	}
	
	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.BoxComponent#onHide()
	 */
	@Override
	protected void onHide() {
		download_panel.setUrl("");
		super.onHide();
	}

	
}
