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
public class LinkIcon extends LayoutContainer{

	String Url ;
	
	public LinkIcon(String url,boolean newpage,String tooltip){
		super();
		Url =url;
		String new_page="";
		String icon = "images/link.png";
		if(newpage){
			new_page =" target='_blank' "; 
		}
		Html innerHtml=new Html();;
		if(Url.equals("")){		
			icon="images/linkred.png";
			innerHtml = new Html("<img src=\""+icon+"\" ></a>");
			setToolTip(tooltip +" url not available");
		}else{
			innerHtml = new Html("<a href=\""+Url+"\" +"+new_page+"><img src=\""+icon+"\" ></a>");
			setToolTip(tooltip);
		}
		
		 
		add(innerHtml);
		
	}
	
	
}
