package org.iplantc.tnrs.demo.client;



import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;

public class TNRSProgressPanel extends ContentPanel {

	private double percentage;


	public TNRSProgressPanel(double percentage) {
		this.percentage = percentage;
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
		NumberFormat format = NumberFormat.getFormat("#0.00 %");
		add(new Label("   "));
		add(new Label("Your job is "+format.format(percentage/100)+" complete"));
		ProgressBar progressBar = new ProgressBar();
		progressBar.updateProgress(percentage/100, format.format(percentage/100));
		add(new Label("    "));
		add(progressBar);
	}


}
