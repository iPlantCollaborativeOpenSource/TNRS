package org.iplantc.tnrs.demo.client.panels;



import org.iplantc.tnrs.demo.client.ClientCommand;
import org.iplantc.tnrs.demo.client.ClientCommandWithOptions;
import org.iplantc.tnrs.demo.client.EditSettingsDialog;
import org.iplantc.tnrs.demo.client.SearchTextArea;
import org.iplantc.tnrs.demo.client.TNRSSupportWindow;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
 
public class TNRSSearchPanel extends VerticalPanel
{
	private SearchTextArea areaData;
	private ClientCommandWithOptions cmdSearch;
	private ClientCommand cmdClear;

	private ClientCommand cmdParser;
	private EditSettingsDialog dlg;
	
	public TNRSSearchPanel(final ClientCommandWithOptions cmdSearch, final ClientCommand cmdClear,final ClientCommand cmdParse, EditSettingsDialog dialog)
	{
		this.cmdSearch = cmdSearch;
		this.cmdClear = cmdClear;
		cmdParser = cmdParse;
		setHorizontalAlign(HorizontalAlignment.CENTER);
		initDataTextArea();
		dlg = dialog;
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
				mask("  ");
				String text = areaData.getValue();
			
				if( text==null || text.trim().equals("") ) {
					MessageBox.alert("Error", "You must enter at least one name",null);
					unmask();
					return;
				}
				//cmdSearch.setOptions(dlg.getSelectedSensitivity());
				cmdSearch.setOptions("0.75");
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

		Anchor support = new Anchor("Click here for support");
		support.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				TNRSSupportWindow support = new TNRSSupportWindow();
				
				support.setModal(true);
				support.show();
			}
		});
		LayoutContainer container = new LayoutContainer();
		container.add(support);
		ret.add(container);
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
