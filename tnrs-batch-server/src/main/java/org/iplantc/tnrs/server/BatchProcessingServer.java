package org.iplantc.tnrs.server;

/**
 * BatchProcessing
 * 
 * this class works as a middle layer between the user and other services such as the taxamatch application and GNI parser. Also it provides 
 * preprocessing and postprocessing steps.
 * 
 * @author Juan Antonio Raygoza Garay
 */


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.iplantc.tnrs.server.handler.GroupRetrieverHandler;
import org.iplantc.tnrs.server.handler.GroupUpdater;
import org.iplantc.tnrs.server.handler.HandlerHelper;
import org.iplantc.tnrs.server.handler.JobInfoHandler;
import org.iplantc.tnrs.server.handler.NameParsingHandler;
import org.iplantc.tnrs.server.handler.ResultsChunkRetrieverHandler;
import org.iplantc.tnrs.server.handler.SourcesHandler;
import org.iplantc.tnrs.server.processing.TNRSResultsTransformer;
import org.mozilla.universalchardet.UniversalDetector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;





public class BatchProcessingServer extends Thread {

	private String tnrsBaseUrl;
	private HttpServer server;

	private HashMap<String, TnrsJob> job_ids = new HashMap<String, TnrsJob>();
	private List<TnrsJob> jobs;
	private SimpleDateFormat dateFormat;

	private Vector<ExecutionThread> threads = new Vector<BatchProcessingServer.ExecutionThread>();
	private String servicesUrl;
	private String baseFolder;
	private GNIParserInterface gni_interface;
	private Properties properties;


	static Logger log = Logger.getLogger(BatchProcessingServer.class);

	public BatchProcessingServer(String tnrsBaseUrl, int port) throws Exception{

		InetSocketAddress address = new InetSocketAddress(port);
		server = HttpServer.create(address, 10);
		jobs = Collections.synchronizedList( new LinkedList<TnrsJob>());

		dateFormat = new SimpleDateFormat("E MMM d yyyy hh:mm:ss z");
		properties = new Properties();

		properties.load(new FileInputStream(System.getProperty("user.home")+"/.tnrs/tnrs.properties"));
        
		this.tnrsBaseUrl= properties.getProperty("tnrsUrl");


		gni_interface = new GNIParserInterface(properties.getProperty("gni_parser_url"));
		servicesUrl = properties.getProperty("servicesUrl");
		baseFolder = "/tnrs-jobs/";
		FileAppender appender = new DailyRollingFileAppender(new HTMLLayout(), "tnrs.html", "'.'yyyy-MM-dd");
		log.addAppender(appender);

		log.setLevel(Level.ALL);

		server.createContext("/submit", new FileUploadHandler());
		server.createContext("/status", new JobStatusRetriever()); 
		server.createContext("/upload", new FileUploadHandler());  
		server.createContext("/retrievedata", new ResultsChunkRetrieverHandler());
		server.createContext("/group",new GroupRetrieverHandler());
		server.createContext("/download", new RemoteDownloadHandler());
		server.createContext("/updategroup", new GroupUpdater());
		server.createContext("/support", new SupportHandler());
		server.createContext("/downloaddirect", new DirectDownloader());
		server.createContext("/serverStatus", new GetStatusHttphandler());
		server.createContext("/parseNames",new NameParsingHandler());
		server.createContext("/modifyJob", new JobManager());
		server.createContext("/sources",new SourcesHandler());
		server.createContext("/jobinfo", new JobInfoHandler(properties));
		server.start();
	}




	/**
	 * This main loop iterates over the submitted jobs and creates the corresponding threads 
	 * that will carry the execution of each job segment.
	 * 
	 * 
	 * 
	 */


	@Override
	public void run() {
		try {
			int jobno=0;

			while(true) { 

				sleep(20);
				synchronized (jobs) {
					if(jobs.size()==0) {

						continue;
					}
				}


				TnrsJob job = jobs.get(jobno % jobs.size());
				if(job.status().equalsIgnoreCase("idle")) {
					ExecutionThread thread = new ExecutionThread(job);
					threads.add(thread);
					thread.start();
				}else if(job.status().equalsIgnoreCase("stopped")) {
					jobs.remove(job);
					JobHelper.cleanJobData(baseFolder,job);
					jobno=0;
					continue;
				}else if(job.status().equals("failed")){
					sendFailedJobEmail(job);
					job.setStatus("error");
				}
				jobno++;



				if(jobs.size()==200) {

					for(int i=0; i < threads.size();i++) {
						threads.elementAt(i).join();
					}
					jobno=0;
					threads.clear();
				}

				sleep(10);
			}

		} catch (Exception e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
	}



	class JobManager implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			try {
				JSONObject command = (JSONObject) JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));

				String job_id = command.getString("job_id");

				TnrsJob job = null;

				for(TnrsJob jobc: jobs) {
					if(jobc.getRequest().getId().equals(job_id)) {
						job = jobc;
						break;
					}
				}

				if(job==null) {
					HandlerHelper.writeResponseRequest(arg0, 500, "Invalid job identifier", "text/plain");
				}

				if(command.getString("command").equals("stop")) {

					synchronized (job) {
						job.disable();
					}


				}else if (command.getString("command").equals("pause")){

					synchronized (job) {
						job.pause();
					}


				}else if (command.getString("command").equals("resume")){

					synchronized (job) {
						job.resume();
					}


				}else {
					HandlerHelper.writeResponseRequest(arg0, 500, "Wrong command", "text/plain");
					return;
				}

				HandlerHelper.writeResponseRequest(arg0, 200, "", "text/plain");
			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));

			}
		}

	}

	class ExecutionThread extends Thread{

		TnrsJob job;

		public ExecutionThread(TnrsJob job) {
			this.job = job;
		}


		@Override
		public void run() {
			try {
				StopWatch stp2 = new StopWatch();
				stp2.start();
				JSONObject json=new JSONObject();
				job.setStatus("running");
				if(job.progress()==100.0) {

					finalizeJob(job);
					return;
				}
				Vector<String> ids = new Vector<String>();
				Vector<String> original_names = new Vector<String>(); 

				String data = job.getNextDataBatch();

				if(data==null || data.equals("")) return;

				String[] lines= data.split("\n");

				if(job.containsId()){
					for(int i=0; i < lines.length;i++) {
						if(lines[i].trim().equals("")) continue;
						ids.add(NameUtil.getNameId(lines[i]));
					}
				}

				for(int i=0; i < lines.length;i++) {
					original_names.add(NameUtil.processName(lines[i], job.containsId()));
				}


				String names = NameUtil.CleanNames(lines, job);

				if(names.equals("")) return;
								
				if(job.getType()==TnrsJob.NAME_MATCH_JOB) {

					TaxamatchInterface taxa_match = new TaxamatchInterface(tnrsBaseUrl);
					String result =taxa_match.queryTaxamatch(names,job);
					json = (JSONObject) JSONSerializer.toJSON(result);

				}else if(job.getType()==TnrsJob.PARSING_JOB) {

					json = gni_interface.parseNames(names);
				}
				if(job.outstandingNames()==0){
					JobHelper.persistJobInfo(baseFolder,job);
					
				}
				saveResults(job, json,ids,original_names,"");

				
				job.setStatus("idle");
				stp2.stop();
				log.info("overall :"+stp2.toString());
			}
			catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				job.setStatus("failed");
				ex.printStackTrace();
			}

		}


	}//thread


	public void finalizeJob(TnrsJob job) throws Exception{
		synchronized (jobs) {
			jobs.remove(job);
		}
		
		job.setStatus("complete");

		if(job.email() && !job.getRequest().getEmail().trim().equals("tnrs@lka5jjs.orv")) {
			job.setFinishedAt(new Date().toString());
			job_ids.remove(job.getRequest().getId());
			sendNormalCompletionEmail(job);

		}

	}




	private void sendNormalCompletionEmail(TnrsJob job) throws Exception{

		
		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(JobHelper.createJobInfoFile(job));
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription("Job information");
		attachment.setName(job.getRequest().getFilename()+" "+job.getRequest().getId()+".txt");
		MultiPartEmail response = new MultiPartEmail();
		response.setHostName(properties.getProperty("org.iplantc.tnrs.mail.host"));
		response.setSmtpPort(Integer.parseInt(properties.getProperty("org.iplantc.tnrs.mail.port")));
		response.setFrom("iplant-tnrs@iplantcollaborative.org");
		response.setSubject("TNRS Job completion");
		response.setMsg("Your TNRS "+job.getTypeString()+" job for the file "+job.getRequest().getOriginalFilename()+" completed on "+dateFormat.format(new Date())+".Details describing the settings applied for this job are attached to this email. You may wish to retain this for your records. \n\n Your results will be available for 7 days, after which they will be deleted from our system. 	\n\n" + 
				"To view your results, go to the TNRS website at http://tnrs.iplantcollaborative.org, select the \"Retrieve results\" tab, and enter your email address and the following submission key:\n\n" + 

				job.getRequest().getId()+"\n\n" + 
				"Please contact us at support@iplantcollaborative.org if you have any difficulty retrieving your results.\n" + 
				"\n" + 
				"Thank you,\n" + 
				"iPlant Collaborative");
		response.addTo(job.getRequest().getEmail());
		response.attach(attachment);
		response.send();

	}

	
	private void sendFailedJobEmail(TnrsJob job) throws Exception{
		if(!job.email()) return;
		
		HtmlEmail response = new HtmlEmail();
		response.setHostName("localhost");
		response.setSmtpPort(25);
		response.setFrom("support@iplantcollaborative.org");
		response.setSubject("TNRS Job failure");
		response.setMsg("Your TNRS "+job.getTypeString()+" job for the file "+job.getRequest().getOriginalFilename()+" failed on "+dateFormat.format(new Date())+". Please provide the job key and if possible the name list you were runing so we can better help you diagnose the issue.	\n\n" + 
			"The job key is:"+ 

				job.getRequest().getId()+"\n\n" + 
				"Thank you,\n" + 
				"iPlant Collaborative");
		response.addTo(job.getRequest().getEmail());
		response.send();

	}

//DEPRECATED IN FAVOR OF CSV DOWNLOAD
	class JobInfohandler implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			try{
				JSONObject request = (JSONObject) JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));
				String email = request.getString("email");
				String key = request.getString("key");



				for(int i=0; i < jobs.size();i++){
					TnrsJob job = jobs.get(i);
					if(job.getRequest().getId().equals(key) && job.getRequest().getEmail().equals(email)){
						JSONObject json =(JSONObject)JSONSerializer.toJSON(job.toJsonString());
						json.put("status","incomplete");
						json.put("progress", job.progress());

						HandlerHelper.writeResponseRequest(arg0, 200, json.toString(), "application/json");
						return;
					}
				}
				if(JobHelper.jobFileExists(baseFolder, email, key)){
					TnrsJob job = JobHelper.readJobInfo(baseFolder, email, key);

					HandlerHelper.writeResponseRequest(arg0, 200, job.toJsonString(), "application/json");
				}else{
					HandlerHelper.writeResponseRequest(arg0, 500, "No such job exists o it might have expired", "text/plain");
				}

			}catch(Exception ex){
				log.error(ExceptionUtils.getFullStackTrace(ex));
				throw new IOException(ex);
			}
		}


	}

	class FileUploadHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			try {


				String request = IOUtils.toString(arg0.getRequestBody());

				JSONObject datas = (JSONObject) JSONSerializer.toJSON(request);


				UUID uid = UUID.randomUUID();

				HashMap<String, String> info = new HashMap<String, String>();
				info.put("email", datas.getString("email"));
				info.put("institution", "");
				info.put("name","");
				info.put("sensitivity", datas.getString("sensitivity"));
				info.put("has_id", datas.getString("has_id"));
				info.put("id", uid.toString().replace("-", ""));
				info.put("type", datas.getString("type"));
				info.put("sources", datas.getString("sources"));
				info.put("classification", datas.getString("classification"));
				info.put("match_to_rank", datas.getString("match_to_rank"));
				/***
				 * 
				 * To be replaced by better code
				 */


				byte[] data=null;

				if(datas.has("upload")){

					data = IOUtils.toByteArray(new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(datas.getString("upload").getBytes()))));
				}else if(datas.has("names")){
					data = datas.getString("names").getBytes();
				}else{
					HandlerHelper.writeResponseRequest(arg0, 500, "Invalid request!",null);
					return;
				}



				UniversalDetector detector = new UniversalDetector(null);

				detector.handleData(data, 0, data.length);

				detector.dataEnd();
				String content="";
				String encoding = detector.getDetectedCharset();

				if (encoding != null) {
					if(encoding.equals("WINDOWS-1252")){
						encoding="ISO-8859-1";

						ByteArrayOutputStream t = new ByteArrayOutputStream();

						OutputStreamWriter wr = new OutputStreamWriter(t,"UTF-8");

						wr.write(new String(data,encoding));
						wr.close();

						content = t.toString("UTF-8");

					}else {
						content =new String(data,"UTF-8");
					}
				} else {
					content =new String(data,"UTF-8");
				}


				info.put("upload", content);


				/**
				 * 
				 * 
				 */

				JSONObject json = createJSONJobRequest(info);

				json.put("submitted_date",new Date().toString().replace(":", ""));
				json.put("original", datas.getString("file_name"));
				json.put("sensitivity",datas.getString("sensitivity"));
				
				TnrsJob job = submitJob(json.toString());
				job.setTaxonomic(datas.getBoolean("taxonomic"));
				job.setEmail(!info.containsKey("noemail"));
				job.setSources(info.get("sources"));
				job.setClassification(info.get("classification"));
				job.setAllowPartial(Boolean.parseBoolean(info.get("match_to_rank")));
				
				log.info(job.getRequest().getId()+"  "+job.getRequest().getEmail());

				HandlerHelper.writeResponseRequest(arg0, 200, job.getRequest().getId(),null);

				if(info.get("email").trim().equals("tnrs@lka5jjs.orv") || !job.email()) return;

				try {

					
					sendSubmissionEmail(job);

				}catch(Exception ex) {
					log.error(ExceptionUtils.getFullStackTrace(ex));
					ex.printStackTrace();
				}
			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				ex.printStackTrace();
				throw new IOException(ex);
			}

		}


		public JSONObject createJSONJobRequest(HashMap<String, String> data) throws Exception{
			JSONObject json = new JSONObject();

			json.put("institution", data.get("institution").trim());
			json.put("name", data.get("name").trim());
			json.put("email", data.get("email").trim());
			json.put("file_name", persistFileData(data.get("upload"),data.get("email").trim(),data.get("id")));
			json.put("has_id", Boolean.parseBoolean(data.get("has_id")));
			json.put("id", data.get("id"));
			json.put("type", data.get("type"));
			return json;
		}




		public String persistFileData(String contents,String email,String id) throws Exception {
			File user_dir = new File(baseFolder+email.replace("@","-").replace(".", "-"));

			if(!user_dir.exists()) {
				user_dir.mkdir();
			}

			String filename = user_dir.getAbsolutePath()+"/"+id+new Date().toString().replaceAll("[ |:]", "")+".csv";

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename, true),"UTF-8");
			BufferedWriter bwr = new BufferedWriter(osw,600000);
			bwr.write(contents.replaceAll("\\p{Cf}", "").replace("\r\n","\n").replace("\r", "\n"));
			bwr.close();
			return filename;

		}
	}

	
	public void sendSubmissionEmail(TnrsJob job) throws Exception{
		

		String job_type = "";
		if(job.getType()==TnrsJob.NAME_MATCH_JOB){
			job_type=" matching ";
		}else{
			job_type=" parsing ";
		}
		
		Email response = new HtmlEmail();
		
		response.setHostName("localhost");
		response.setSmtpPort(25);
		response.setFrom("iplant-tnrs@iplantcollaborative.org");
		response.setSubject("TNRS Job submission");
		response.setMsg("Your TNRS "+job_type+" job ("+job.getRequest().getOriginalFilename()+") was successfully submitted on "+dateFormat.format(new Date())+". \n\n" + 
				"When your list is done processing, you will receive an email notification that contains instructions regarding retrieval of your results from the TNRS website at http://tnrs.iplantcollaborative.org/.\n\n"+
				"Please contact us at support@iplantcollaborative.org if you have any difficulty retrieving your results. \n" + 
				"\n\n You can check the status of your job in the 'Retrieve results' tab of the application using your email and the following key:  " +
				job.getRequest().getId()+".\n\nTo update the status for your job in progress, please select the 'Retrieve results' button again. Your progress will update at this time.\n\n"+
				"Thank you, \n" + 
				"iPlant Collaborative"); 
		response.addTo(job.getRequest().getEmail());
		response.send();
	}



	private void saveResults(TnrsJob job, JSONObject results,Vector<String> ids,Vector<String> original_names,String session_id) throws Exception{

		if(job.getType()==TnrsJob.PARSING_JOB){
			ParsingResultsFile csv = new ParsingResultsFile(job, baseFolder);
			csv.writeJsonData(results.getJSONArray("parsedNames"),ids);
			
		}else {
			MatchingResultsFile csv = new MatchingResultsFile(job, baseFolder,session_id,false);
			TNRSResultsTransformer transformer = new TNRSResultsTransformer();
			JSONArray results_array = transformer.transform(results,job,ids,original_names); 
			csv.writeJsonData(results_array, ids);
			csv.close();
		}

	}


	
	





	class GetStatusHttphandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String message="<html><body><table border=\"1\"><tr><th>Email</th><th>Submitted at:</th><th>Progress:</th><th>Status:</th><th>Job id</th></tr>";
				for (int i=0; i < jobs.size(); i++) {
					TnrsJob job = jobs.get(i);

					message += "<tr> <td> "+job.getRequest().getEmail() + "</td><td>" + job.getSubmissionDate()+" </td><td> " + job.progress() +"% </td><td>  "+ job.status()+"</td><td>"+job.getRequest().getId()+"</td></tr>\n";
				}
				message+="</table>";
				HandlerHelper.writeResponseRequest(arg0, 200, message,"text/html");

			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				throw new IOException(ex);
			}
		}

	}




	class RemoteDownloadHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {

				JSONObject json = (JSONObject)JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));


				String email = json.getString("email");
				String key= json.getString("key");
				String session_id = json.getString("session_id");

				TnrsJob job = JobHelper.readJobInfo(baseFolder,email, key);

				if(job.getType()==TnrsJob.PARSING_JOB) {

					ParsingResultsFile results = new ParsingResultsFile(job, baseFolder);
					results.createFileForDownload(properties.getProperty("org.iplantc.folder.tmp"));
					
				}else {

					MatchingResultsFile results = new MatchingResultsFile(job, baseFolder,session_id,false);

					results.createFileForDownload(properties.getProperty("org.iplantc.tnrs.folder.tmp"),json);

					results.close();
				}


				String url=servicesUrl+"getcsv?id="+key;

				HandlerHelper.writeResponseRequest(arg0, 200, url,null);
			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				ex.printStackTrace();

			}

		}

	}

	/*class ShutdownHandler extends HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {

		}

	}*/





	class SubmitRequestHttpHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			try {

				JSONObject json = (JSONObject)JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));

				String contents = json.getString("names");

				byte[] fileContents = contents.getBytes("UTF-8");

				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

				GZIPOutputStream gzipStr = new GZIPOutputStream(byteArray);

				gzipStr.write(fileContents);
				gzipStr.close();

				ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();

				Base64OutputStream base64 = new Base64OutputStream(byteArray2);

				base64.write(byteArray.toByteArray());
				base64.close();

				String value = new String(byteArray2.toByteArray());

				json.remove("names");
				json.put("upload", value);

				HttpClient client = new HttpClient();


				PostMethod post = new PostMethod("http://"+properties.getProperty("org.iplantc.tnrs.servicesHost")+"/tnrs-svc/upload");
				
				post.setRequestEntity(new StringRequestEntity(json.toString(),"text/plain","UTF-8"));

				client.executeMethod(post);
				
				HandlerHelper.writeResponseRequest(arg0, 200, "", "text/plain");

			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				ex.printStackTrace();
				throw new IOException(ex);
			}
		}


	}


	public TnrsJob submitJob(String jsons) throws Exception{
		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsons);
		boolean emailr =false;

		TnrsJobRequest request = new TnrsJobRequest(json.getString("email"), json.getString("file_name"),json.getString("original"),emailr);
		TnrsJob job = new TnrsJob(request,new Date().toString(),json.getString("type"));
		job.setSensitivity(Double.parseDouble(json.getString("sensitivity").toLowerCase().trim()));
		job.setTnrs_version(properties.getProperty("org.iplantc.tnrs.version"));
		synchronized (jobs) {
			jobs.add(job);
		}
		job_ids.put(request.getId(), job);
		job.setContainsId(json.getBoolean("has_id"));
		job.setStatus("idle");
		job.getRequest().setId(json.getString("id"));

		return job;

	}





	public static void main(String[] args) throws Exception{
		BatchProcessingServer server = new BatchProcessingServer("", 14445);
		System.out.println("Starting server....");
		server.start();
		System.out.println("Done...");
		server.join();

	}



	class JobStatusRetriever implements HttpHandler {


		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				JSONObject json = (JSONObject) JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));

				JSONObject result = new JSONObject();

				String email = json.getString("email");
				String key = json.getString("key");

				for(int i=0; i < jobs.size(); i++) {

					TnrsJob job = jobs.get(i);


					if(job.getRequest().getEmail().equals(email) && job.getRequest().getId().equals(key)) {
						if(job.status().equals("failed")|| job.status().equals("error")){
							result.put("type", "failed");
						}else{
							result.put("type", "incomplete");
							double progress=job.progress();
							if(job.progress()==100.0){
								progress=99.0;
							}
							result.put("progress", progress);
						}
						HandlerHelper.writeResponseRequest(arg0, 200, result.toString(), "application/json");

						return;

					}
				}

				String filename =baseFolder+email.replace("@","-").replace(".", "-")+"/result"+key;

				File results = new File(filename);

				if(!results.exists()) {
					result.put("type", "non-existent");
				}else {
					result.put("type", "complete");
					TnrsJob job = JobHelper.readJobInfo(baseFolder,email, key);

					result.put("job_type", job.getTypeString());
				}


				HandlerHelper.writeResponseRequest(arg0, 200, result.toString(), "application/json");

				return;
			}catch(Exception ex ) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				throw new IOException(ex);
			}

		}

	}





	class JobReporter implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();

				for(int i=0; i < jobs.size(); i++) {
					JSONObject job_info = new JSONObject();
					TnrsJob job = jobs.get(i);

					job_info.put("email", job.getRequest().getEmail());
					job_info.put("progress",job.progress());

					array.add(job_info);
				}

				json.put("jobs", array);

				HandlerHelper.writeResponseRequest(arg0, 200, json.toString(),"application/json");
			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				throw new IOException(ex);
			}
		}

	}



	class SupportHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String jsons = IOUtils.toString(arg0.getRequestBody());

				JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);
				if(!json.containsKey("valid")) return;
				Email response = new SimpleEmail();
				response.setHostName(properties.getProperty("org.iplantc.tnrs.mail.host"));
				response.setSmtpPort(Integer.parseInt(properties.getProperty("org.iplantc.tnrs.mail.port")));
				response.setFrom("support@iplantcollaborative.org");
				response.setSubject("TNRS support Ticket");
				response.setMsg("TNRS support ticket from: "+json.getString("name")+" ("+json.getString("email")+"). " +
						"\n\n\n" + json.getString("contents")); 
				response.addTo("support@iplantcollaborative.org");
				response.send();
			}catch(Exception ex) {
				log.error(ExceptionUtils.getFullStackTrace(ex));
				throw new IOException(ex);
			}

		}

	}


	class DirectDownloader implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			byte[] utf8b = org.apache.commons.io.ByteOrderMark.UTF_8.getBytes();

			String l ="a\tb\tc\t\nb\th\tj\n";

			ByteArrayOutputStream ous = new ByteArrayOutputStream();

			ous.write(utf8b);
			ous.write(l.getBytes("UTF-8"));
			ous.close();

			byte[] k = ous.toByteArray();

			Headers hdrs = arg0.getResponseHeaders();

			hdrs.set("Content-Type", "application/csv");
			hdrs.set("Content-Disposition", "attachment; filename=genome.csv");
			hdrs.set("Content-Encoding", "binary");
			hdrs.set("Charset", "utf-8");

			arg0.sendResponseHeaders(200, k.length);


			arg0.getResponseBody().write(k);
			arg0.getResponseBody().close();


		}
	}

	
}
