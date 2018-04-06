package org.iplantc.tnrs.demo.client.panels;



import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class TNRSProgressPanel extends ContentPanel {

	private double percentage;
	private HTML message = new HTML();;
	private ProgressBar progressBar = new ProgressBar();
	private String spinner_text="";
	private boolean refreshText;

	public TNRSProgressPanel(double percentage,boolean refresh) {
		this.percentage = percentage;
		refreshText = refresh;
		init();
		compose();

	}

	private void init() {
		setLayout(new RowLayout(Style.Orientation.VERTICAL));

		setSize(1170,200);
		setHeadingText("Progress");
		setFrame(true);
	}


	private void compose() {
		NumberFormat format = NumberFormat.getFormat("#0%");
		add(new Label("   "));
		message = new HTML(spinner_text+"Your name list is "+format.format(percentage/100.0)+" complete");
		add(message);
		progressBar.updateProgress(percentage/100.0, format.format(percentage/100.0));
		if(refreshText) add(new Html("<br/><br/>"));
		add(progressBar);
		if(refreshText) {add(new Html("<br/><br/>"));
		add(new Label("Click on Retrieve again to refresh the progress..."));
		}
	}


	public void updateProgress(double pct) {
		percentage = pct;
		NumberFormat format = NumberFormat.getFormat("#0%");
		message.setHTML(spinner_text+"Your name list is "+format.format(percentage/100.0)+" complete") ;
		progressBar.updateProgress(percentage/100, format.format(percentage/100.0));

	}

	public void setErrorMessage(){

		progressBar.setVisible(false);

		message.setHTML("<img src=\"images/status_error.png\" /> <div>There was an error processing your list. Please contact support to solve your issue</div> ");;

	}

	public void enableSpinnerImage() {
		spinner_text ="<img src=\"images/spinner_green.gif\" />  ";
	}

}
