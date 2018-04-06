package org.iplantc.tnrs.demo.client;


import org.iplantc.tnrs.demo.client.util.NumberUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class DetailDialog extends Dialog {

	TNRSEntry entry;
	private boolean taxonomic;
	public DetailDialog(BeanModel entry,boolean taxonomic) {
		this(new TNRSEntry(entry),taxonomic);

	}

	public DetailDialog(TNRSEntry entry,boolean taxonomic) {
		super();
		this.taxonomic = taxonomic;
		this.entry =entry;

		init();
		compose();

	}

	public void init() {
		setSize(550,500);
		setHeadingText("Details");
		setResizable(true);
		setLayout(new FitLayout());
		setStyleAttribute("fontSize","12px");
		setHideOnButtonClick(true);
		setModal(true);
	}

	public void compose() {

		ContentPanel panel = new ContentPanel();

		panel.setHeadingText("Name submitted: "+entry.getSubmittedName());
		panel.setLayout(new FitLayout());


		String html="<div><table border=\"0\">";

		html+= "<tr class=\"flexFontSize\"><td>"+"Name matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getScientificName()+"</td></tr>";

		String[] tokens = entry.getSources().split(";");
		
		String sources = "";

		if(tokens.length>0){
			sources = tokens[0];
			for(int i=1; i < tokens.length;i++){
				sources+= "  "+tokens[i].toUpperCase();
			}
		}

		html+= "<tr class=\"flexFontSize\"><td>"+"Name matched source(s):"+"</td><td>&nbsp;&nbsp;&nbsp;"+sources.toUpperCase()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Name matched rank:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getNameMatchedRank()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Name score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getScientificScore())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Author matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAuthor()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Author score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getAuthorScore())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Overall score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getOverall())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Family matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getFamilyMatched()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Family score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getFamilyMatchedScore())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Name matched accepted family:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getFamily()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Genus matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getGenus()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Genus score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getGenusScore())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Specific epithet matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getEpithet()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Specific epithet score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getEpithetScore())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific rank :"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getInfraSpecificRank1()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific epithet matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getInfraSpecificEpithet1()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific epithet score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getInfraSpecificEpithet1Score())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific rank 2:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getInfraSpecificRank2()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific epithet 2 matched:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getInfraSpecificEpithet2()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Infraspecific epithet 2 score:"+"</td><td>&nbsp;&nbsp;&nbsp;"+formatPercentage(entry.getInfraSpecificEpithet2Score())+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Annotations:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAnnotation()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Unmatched terms:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getUnmatched()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Taxoxnomic status:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAcceptance()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Accepted name:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAcceptedName()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Name matched source(s):"+"</td><td>&nbsp;&nbsp;&nbsp;"+sources.toUpperCase()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Accepted Name author:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAcceptedAuthor()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Accepted Name Species:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAcceptedSpecies()+"</td></tr>";
		html+= "<tr class=\"flexFontSize\"><td>"+"Accepted Name Family:"+"</td><td>&nbsp;&nbsp;&nbsp;"+entry.getAcceptedFamily()+"</td></tr>";

		String ambiguousText = "";
		int flag = Integer.parseInt(entry.get("flag").toString());


		if((flag)==1 ) {
			ambiguousText += " Partial match ";
		}
		if((flag)==2 ) {
			ambiguousText += " Ambiguous match  ";
		}
		if((flag)==3) {
			ambiguousText += " Partial match, Ambiguous match";
		}
		if((flag)==4) {
			if(taxonomic){
				ambiguousText += " Best match is based upon taxonomic results.";
			}else{
				ambiguousText += " Better higher taxonomic match found";
			}
		}


		if(flag==5 ){
			if(taxonomic){
				ambiguousText += " Partial match, Higher scoring names were found";
			}else{
				ambiguousText += " Partial match, Better higher taxonomic match found";
			}

		}

		if(flag==6){
			if(taxonomic){
				ambiguousText += " Ambiguous match, Higher scoring names were found";
			}else{
				ambiguousText += " Ambiguous match, Better higher taxonomic match found";
			}
		}

		if(flag==7){
			if(taxonomic){
				ambiguousText += " Partial match, Ambiguous match, Higher scoring names were found <br/>";
			}else{
				ambiguousText += " Partial match, Ambiguous match, Better higher taxonomic match found<br/>";
			}
		}

		html+= "<tr><td>"+"Warnings:"+"</td><td>"+ambiguousText+"</td></tr>";

		html+="</table></div>";


		LayoutContainer container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTO);
		container.add(new HtmlContainer(html));
		panel.add(container);
		add(panel);
		layout();
	}


	private String formatPercentage(String score)
	{
		if(score.trim().equals("") || score.trim().equals("0")) return "";
		return NumberUtil.formatPercentage(score);
	}





}
