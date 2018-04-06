package org.iplantc.tnrs.demo.client;



import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class TNRSSearchPanel extends VerticalPanel
{
	private SearchTextArea areaData;
	private ClientCommand cmdSearch;
	private ClientCommand cmdClear;

	public TNRSSearchPanel(final ClientCommand cmdSearch, final ClientCommand cmdClear)
	{
		this.cmdSearch = cmdSearch;
		this.cmdClear = cmdClear;
		setHorizontalAlign(HorizontalAlignment.CENTER);
		initDataTextArea();
	}

	private void initDataTextArea()
	{
		areaData = new SearchTextArea();
		areaData.setSize(450, 150);
		areaData.setSelectOnFocus(true);
		areaData.setEmptyText("Enter up to 5000 names.");
		areaData.setValue("");
	}

	private void validateLineCount(final String text, int maxLines)
	{
		int count = 0;

		String test = text.trim();
		for(int i = 0,len = test.length();i < len;i++)
		{
			if(test.charAt(i) == '\n')
			{
				count++;
			}
		}

		if(count >= maxLines)
		{
			String msg = "More than " + maxLines + " lines were entered. Only the first " + maxLines
			+ " will be processesed. (beta version only)";

			MessageBox.alert("Warning", msg, null);
		}


	}

	
	
	private Button buildSearchButton()
	{
		return new Button("Submit List", new SelectionListener<ButtonEvent>()
				{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				String text = areaData.getValue();
			
				if( text==null || text.trim().equals("") ) {
					MessageBox.alert("Error", "You must enter at least one name",null);
					return;
				}
				
				cmdSearch.execute(text);
				Element el = RootPanel.getBodyElement();
				el.setAttribute("alert", "true");
			}
				});
	}
	
	private Button buildClearButton() {
		return new Button("Clear", new SelectionListener<ButtonEvent>() {
			
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.event.SelectionListener#componentSelected(com.extjs.gxt.ui.client.event.ComponentEvent)
			 */
			@Override
			public void componentSelected(ButtonEvent ce) {
				areaData.setValue("");
				cmdClear.execute("");
			}
			
		});
	}

	private ToolBar buildButtonBar()
	{
		ToolBar ret = new ToolBar();

		ret.add(new FillToolItem());
		ret.add(buildClearButton());
		ret.add(buildSearchButton());
		
		return ret;
	}

	private ContentPanel buildInnerPanel()
	{
		ContentPanel ret = new ContentPanel();

		ret.setHeadingText("Enter scientific names to check");

		ret.setBottomComponent(buildButtonBar());

		ret.setFrame(true);

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);

		ContentPanel panelInner = buildInnerPanel();
		panelInner.add(areaData);

		add(panelInner);
	}
}
