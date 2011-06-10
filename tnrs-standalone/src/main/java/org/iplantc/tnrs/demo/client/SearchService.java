package org.iplantc.tnrs.demo.client;



import java.io.IOException;

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
	String doSearch(String name) throws IllegalArgumentException;
	String downloadResults(String input)  throws IllegalArgumentException;
    PagingLoadResult<BeanTNRSEntry> getRemoteData(final PagingLoadConfig config,String jsons) throws IllegalArgumentException;
    String requestGroupMembers(String group) throws IllegalArgumentException;
    String downloadRemoteResults(String options) throws IllegalArgumentException;
    String checkJobStatus(String options) throws IllegalArgumentException;
    String updateGroup(String params) throws IllegalArgumentException;
}
