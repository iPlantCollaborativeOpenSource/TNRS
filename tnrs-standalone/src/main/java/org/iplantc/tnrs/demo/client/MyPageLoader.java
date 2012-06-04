/**
 * 
 */
package org.iplantc.tnrs.demo.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.tnrs.demo.shared.BeanTNRSEntry;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author raygoza
 *
 */
public class MyPageLoader extends BasePagingLoadResult<BeanTNRSEntry> implements IsSerializable{
	
	
     static final long serialVersionUID =0;
     
     /**
	 * 
	 */
	public MyPageLoader() {
		super(new ArrayList<BeanTNRSEntry>(),0,0);
		
		
		
	}
     
     
	/**
	 * 
	 */
	public MyPageLoader(List<BeanTNRSEntry> entries,int offset,int length) {
		super(entries,length,offset);
	}
	
}
