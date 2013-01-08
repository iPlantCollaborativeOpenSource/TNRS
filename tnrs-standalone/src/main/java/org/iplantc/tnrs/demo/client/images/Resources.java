package org.iplantc.tnrs.demo.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("download.png")
	ImageResource download();
	
	@Source("settings.jpg")
	ImageResource settings();
	
	@Source("infoicon.gif")
	ImageResource info();
}
