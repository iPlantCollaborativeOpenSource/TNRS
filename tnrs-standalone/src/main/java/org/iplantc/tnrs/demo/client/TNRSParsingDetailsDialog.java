/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * @author raygoza
 *
 */
public class TNRSParsingDetailsDialog extends Dialog {

	BeanModel entry;
	
	
	
	public TNRSParsingDetailsDialog(BeanModel entry) {
		super();
		
		this.entry =entry;
		
		init();
		compose();
		
	}
	
	public void init() {
		setSize(480,380);
		setHeading("Details");
		setResizable(true);
		setLayout(new FitLayout());
		setStyleAttribute("fontSize","12px");
		setHideOnButtonClick(true);
	}
	
	public void compose() {
		
		ContentPanel panel = new ContentPanel();
		panel.setHeading("Name Submitted: "+entry.get("submittedName"));
		panel.setLayout(new FitLayout());
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("flexfontsize");
		panel.setStyleAttribute("fontSize", "12px");
		
		
		Label nMatched = new Label("Name submitted:");
		nMatched.setStyleName("flexfontsize");
		Label scientific = new Label(entry.get("submittedName").toString());
		scientific.setStyleName("flexfontsize");
		
		flexTable.setWidget(0, 0, nMatched);
		flexTable.setWidget(0, 1, scientific);
		
		
		Label canonicalT = new Label("Taxon Name:");
		canonicalT.setStyleName("flexfontsize");
		Label canonicalt = new Label(entry.get("taxonName").toString());
		
		flexTable.setWidget(1,0, canonicalT);
		flexTable.setWidget(1, 1, canonicalt);
		
		Label canonicalL = new Label("Canonical Name:");
		canonicalL.setStyleName("flexfontsize");
		Label canonical = new Label(entry.get("cannonicalName").toString());
		
		flexTable.setWidget(2,0, canonicalL);
		flexTable.setWidget(2, 1, canonical);
		
		Label authorL = new Label("Author:");
		authorL.setStyleName("flexfontsize");
		Label author = new Label(entry.get("author").toString());
		author.setStyleName("flexfontsize");
		
		flexTable.setWidget(3,0, authorL);
		flexTable.setWidget(3, 1, author);
		
		
		Label familyL = new Label("Family:");
		familyL.setStyleName("flexfontsize");
		Label family = new Label(entry.get("family").toString());
		family.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(4,0, familyL);
		flexTable.setWidget(4, 1, family);
		
		Label genusL = new Label("Genus:");
		genusL.setStyleName("flexfontsize");
		Label genus = new Label(entry.get("genus").toString());
		genus.setStyleName("flexfontsize");
		   
		
		flexTable.setWidget(5,0, genusL);
		flexTable.setWidget(5, 1, genus);
		
		Label speciesL = new Label("Specific epithet:");
		speciesL.setStyleName("flexfontsize");
		Label species = new Label(entry.get("species").toString());
		species.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(6,0, speciesL);
		flexTable.setWidget(6, 1, species);
		
		Label infra1L = new Label("InfraSpecific Epithet 1:");
		infra1L.setStyleName("flexfontsize");
		Label infra1 = new Label(entry.get("infraSpecificEpithet1").toString());
		infra1.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(7,0, infra1L);
		flexTable.setWidget(7, 1, infra1);
		
		Label infra1rankL = new Label("InfraSpecific Epithet Rank:");
		infra1L.setStyleName("flexfontsize");
		Label infrarank1 = new Label(entry.get("infraSpecificEpithet1_rank").toString());
		infra1.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(8,0, infra1rankL);
		flexTable.setWidget(8, 1, infrarank1);
		
		
		Label infra2L = new Label("InfraSpecific Epithet 2:");
		infra2L.setStyleName("flexfontsize");
		Label infra2 = new Label(entry.get("infraSpecificEpithet2").toString());
		infra2.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(9,0, infra2L);
		flexTable.setWidget(9, 1, infra2);
		
		Label infra2rankL = new Label("InfraSpecific Epithet 2 Rank:");
		infra1L.setStyleName("flexfontsize");
		Label infrarank2 = new Label(entry.get("infraSpecificEpithet2_rank").toString());
		infra1.setStyleName("flexfontsize");
		
		
		flexTable.setWidget(10,0, infra2rankL);
		flexTable.setWidget(10, 1, infrarank2);
		
		
		
		Label annoL = new Label("Anotations:");
		annoL.setStyleName("flexfontsize");
		Label anno = new Label(entry.get("annotations").toString());
		anno.setStyleName("flexfontsize");
		
		flexTable.setWidget(11,0, annoL);
		flexTable.setWidget(11, 1, anno);
		
		Label unmatchedL = new Label("Unmatched terms:");
		unmatchedL.setStyleName("flexfontsize");
		Label unmatched = new Label(entry.get("unmatched").toString());
		unmatched.setStyleName("flexfontsize");
		
		flexTable.setWidget(12,0, unmatchedL);
		flexTable.setWidget(12, 1, unmatched);
		
		panel.add(flexTable);
		add(panel);
		layout();
		
	}
	
}
