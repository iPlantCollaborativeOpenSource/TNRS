/**
 * 
 */
package org.iplantc.tnrs.demo.server;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

/**
 * @author raygoza
 *
 */
public class FileUploadServlet extends UploadAction {

	private ConfigurationProperties props;

	public FileUploadServlet() throws Exception{

		props = new ConfigurationProperties();

	}

	/* (non-Javadoc)
	 * @see gwtupload.server.UploadAction#executeAction(javax.servlet.http.HttpServletRequest, java.util.List)
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {



		try {

			JSONObject json = new JSONObject();
			for(FileItem item: sessionFiles) {

				if(item.isFormField()) {
					json.put(item.getFieldName(), item.getString());



				}else {
					System.out.println(item.getFieldName());
					byte[] fileContents = item.get();

					ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

					GZIPOutputStream gzipStr = new GZIPOutputStream(byteArray);

					gzipStr.write(fileContents);
					gzipStr.close();

					ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();

					Base64OutputStream base64 = new Base64OutputStream(byteArray2);

					base64.write(byteArray.toByteArray());
					base64.close();

					String value = new String(byteArray2.toByteArray());


					json.put(item.getFieldName(), value);
					json.put("file_name", item.getName());

				}



			}

			 
			
			
			HttpClient client = new HttpClient();

			PostMethod post = new PostMethod("http://"+props.getProperty("org.iplantc.tnrs.servicesHost")+"/tnrs-svc/upload");
			System.out.println(json.toString());
			post.setRequestEntity(new StringRequestEntity(json.toString(),"application/json","UTF-8"));
			client.executeMethod(post);

		}catch(Exception ex) {
			ex.printStackTrace();

			throw new UploadActionException(ex.getMessage());
		}

		return "";
	}


}
