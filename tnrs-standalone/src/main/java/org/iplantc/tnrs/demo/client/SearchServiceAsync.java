package org.iplantc.tnrs.demo.client;




import java.io.IOException;

import org.iplantc.tnrs.demo.shared.BeanTNRSEntry;
import org.iplantc.tnrs.demo.shared.BeanTnrsParsingEntry;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * The async counterpart of <code>SearchService</code>.
 */
public interface SearchServiceAsync
{
	void doSearch(String input, String sensitivity,AsyncCallback<String> callback) throws IllegalArgumentException;
	void downloadResults(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getRemoteData(final PagingLoadConfig config,String jsons,AsyncCallback<BasePagingLoadResult<BeanTNRSEntry>> callback);
	void requestGroupMembers(String group, AsyncCallback<String> callback) throws IllegalArgumentException;
	void downloadRemoteResults(String options,AsyncCallback<String> callback) throws IllegalArgumentException;
	void checkJobStatus(String options, AsyncCallback<String> callback) throws IllegalArgumentException;
	void updateGroup(String params, AsyncCallback<String> callback) throws IllegalArgumentException;
	void parseNamesOnly(String nameList,AsyncCallback<String> callback) throws IllegalArgumentException;
	void getRemoteParsingData(final PagingLoadConfig config,String jsons,AsyncCallback<BasePagingLoadResult<BeanTnrsParsingEntry>> callback); 
	void getSources(AsyncCallback<String> callback) throws IllegalArgumentException;
	void getJobInfoUrl(String info,AsyncCallback<String> callback) throws IllegalArgumentException;
}
