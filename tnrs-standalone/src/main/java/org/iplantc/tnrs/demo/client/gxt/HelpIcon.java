/**
 * 
 */
package org.iplantc.tnrs.demo.client.gxt;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * @author raygoza
 *
 */
public class HelpIcon extends LayoutContainer{

	private String htmlMessage;
	private String url;

	public HelpIcon(String message){
		super();
		htmlMessage = message;
		url=null;
		compose();
	}

	public HelpIcon(String message,String Url){
		super();
		htmlMessage = message;
		url=Url;
		compose();
	}

	public void compose(){

		Html icon;
		if(url==null){
			icon = new Html("&nbsp;&nbsp;<img style=\"width: 13px; height: auto;\" src=\"images/q.png\" />");
		}else{
			icon = new Html("&nbsp;&nbsp;<a target='_blank' href=\""+url+"\"><img style=\"width: 13px; height: auto;\" src=\"images/q.png\" /></a>");
		}
		add(icon);
		setToolTip(htmlMessage);
	}



}
