package org.iplantc.tnrs.demo.client;



import java.io.IOException;

import org.iplantc.tnrs.demo.shared.BeanTNRSEntry;
import org.iplantc.tnrs.demo.shared.BeanTnrsParsingEntry;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService
{
	String doSearch(String name,String sensitivity) throws IllegalArgumentException;
	String downloadResults(String input)  throws IllegalArgumentException;
	BasePagingLoadResult<BeanTNRSEntry> getRemoteData(final PagingLoadConfig config,String jsons) ;
    String requestGroupMembers(String group) throws IllegalArgumentException;
    String downloadRemoteResults(String options) throws IllegalArgumentException;
    String checkJobStatus(String options) throws IllegalArgumentException;
    String updateGroup(String params) throws IllegalArgumentException;
    String parseNamesOnly(String nameList) throws IllegalArgumentException;
    BasePagingLoadResult<BeanTnrsParsingEntry> getRemoteParsingData(final PagingLoadConfig config,String jsons);
    String getSources() throws IllegalArgumentException;
    String getJobInfoUrl(String info ) throws IllegalArgumentException;
}
