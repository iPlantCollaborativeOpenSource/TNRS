package org.iplantc.tnrs.demo.client;


import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class DetailDialog extends Dialog {

	TNRSEntry entry;
	
	public DetailDialog(BeanModel entry) {
		this(new TNRSEntry(entry));
	}
	
	public DetailDialog(TNRSEntry entry) {
		super();
		
		this.entry =entry;
		
		init();
		compose();
		
	}
	
	public void init() {
		setSize(550,650);
		setHeading("Details");
		setResizable(true);
		setLayout(new FitLayout());
		setStyleAttribute("fontSize","12px");
		setHideOnButtonClick(true);
	}
	
	public void compose() {
		
		ContentPanel panel = new ContentPanel();
		panel.setHeading("Name Submitted: "+entry.getSubmittedName());
		panel.setLayout(new FitLayout());
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("flexfontsize");
		panel.setStyleAttribute("fontSize", "12px");
		
		Label nMatched = new Label("Name matched:");
		nMatched.setStyleName("flexfontsize");
		Label scientific = new Label(entry.getScientificName() + " "+entry.getAttributedAuthor());
		scientific.setStyleName("flexfontsize");
		
		flexTable.setWidget(0, 0, nMatched);
		flexTable.setWidget(0, 1, scientific);
		
		
		Label nMatchedScore = new Label("Name matched score:");
		nMatchedScore.setStyleName("flexfontsize");
		Label scientificScore = new Label(formatPercentage(entry.getScientificScore()));
		scientificScore.setStyleName("flexfontsize");
		
		
		
		flexTable.setWidget(1, 0, nMatchedScore);
		flexTable.setWidget(1, 1, scientificScore);
		
		flexTable.setWidget(2, 0, new Label("Author matched:"));
		flexTable.setWidget(2, 1, new Label(entry.getAuthor()));
		
		flexTable.setWidget(3, 0, new Label("Author matched score:"));
		flexTable.setWidget(3, 1, new Label(formatPercentage(entry.getAuthorScore())));
		
		flexTable.setWidget(4, 0, new Label("Overall score:"));
		flexTable.setWidget(4, 1, new Label(formatPercentage(entry.getOverall())));
		
		flexTable.setWidget(5, 0, new Label("Accepted family:"));
		flexTable.setWidget(5, 1, new Label(entry.getFamily()));
		
		flexTable.setWidget(6, 0, new Label("Accepted name:"));
		flexTable.setWidget(6, 1, new Label(entry.getAcceptedName()+" "+entry.getAcceptedAuthor()));
		
		flexTable.setWidget(7, 0, new Label("Family matched:"));
		flexTable.setWidget(7, 1, new Label(entry.getFamilyMatched()));
		
		flexTable.setWidget(8, 0, new Label("Family matched score:"));
		flexTable.setWidget(8, 1, new Label(formatPercentage(entry.getFamilyMatchedScore())));
		
		flexTable.setWidget(9, 0, new Label("Genus matched:"));
		flexTable.setWidget(9, 1, new Label(entry.getGenus()));
		
		flexTable.setWidget(10, 0, new Label("Genus matched score:"));
		flexTable.setWidget(10, 1, new Label(formatPercentage(entry.getGenusScore())));
		
		flexTable.setWidget(11, 0, new Label("Species matched:"));
		flexTable.setWidget(11, 1, new Label(entry.getEpithet()));
		
		flexTable.setWidget(12, 0, new Label("Species matched score:"));
		flexTable.setWidget(12, 1, new Label(formatPercentage(entry.getEpithetScore())));
		
		flexTable.setWidget(13, 0, new Label("Infraspecific rank 1:"));
		flexTable.setWidget(13, 1, new Label(entry.getInfraSpecificRank1()));
		
		flexTable.setWidget(14, 0, new Label("Infraspecific epithet 1:"));
		flexTable.setWidget(14, 1, new Label(entry.getInfraSpecificEpithet1()));
		
		flexTable.setWidget(15, 0, new Label("Infraspecific epither 1 score:"));
		flexTable.setWidget(15, 1, new Label(formatPercentage(entry.getInfraSpecificEpithet1Score())));
		
		flexTable.setWidget(16, 0, new Label("Infraspecific rank 2:"));
		flexTable.setWidget(16, 1, new Label(entry.getInfraSpecificRank2()));
		
		flexTable.setWidget(17, 0, new Label("Infraspecific epithet 2:"));
		flexTable.setWidget(17, 1, new Label(entry.getInfraSpecificEpithet2()));
		
		flexTable.setWidget(18, 0, new Label("Infraspecific epither 2 score:"));
		flexTable.setWidget(18, 1, new Label(formatPercentage(entry.getInfraSpecificEpithet2Score())));
		
		flexTable.setWidget(19, 0, new Label("Annotations:"));
		flexTable.setWidget(19, 1, new Label(entry.getAnnotation()));
		
		flexTable.setWidget(20, 0, new Label("Unmatched terms:"));
		flexTable.setWidget(20, 1, new Label(entry.getUnmatched()));
		
		flexTable.setWidget(21, 0, new Label("Status:"));
		flexTable.setWidget(21, 1, new Label(entry.getAcceptance()));
		
		
		for(int i=0; i < 22; i++) {
			flexTable.getWidget(i, 0).setStyleName("flexfontsize");
			flexTable.getWidget(i,1).setStyleName("flexfontsize");
		}
		
		panel.add(flexTable);
		add(panel);
		layout();
	}
	
	
	private String formatPercentage(String score)
	{
		String ret = ""; // assume failure... if we have no percentage we just return an
		// empty string

		if(isDouble(score))
		{
			double d = Double.parseDouble(score);

			int percentage = (int)(d * 100.0);
			ret = percentage + "%";
		}

		return ret;
	}

	
	public static boolean isDouble(String test)
	{
		boolean ret = false; // assume failure

		try
		{
			if(test != null)
			{
				Double.parseDouble(test);

				// if we get here, we know parseDouble succeeded
				ret = true;
			}
		}
		catch(NumberFormatException nfe)
		{
			// we are assuming false - setting the return value here would be redundant
		}

		return ret;
	}
	
	
}
