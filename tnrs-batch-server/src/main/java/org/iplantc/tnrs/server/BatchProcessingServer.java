package org.iplantc.tnrs.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.iplantc.tnrs.server.request.MultipartRequestParser;
import org.mozilla.universalchardet.UniversalDetector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;




public class BatchProcessingServer extends Thread {

	private String tnrsBaseUrl;
	private HttpServer server;
	private String hostUrl;
	private static int staticCounter=0;
	private final int nBits=4;

	private HashMap<String, NameMatchingJob> job_ids = new HashMap<String, NameMatchingJob>();
	private List<NameMatchingJob> jobs;
	private int executing=0;
	private SimpleDateFormat dateFormat;
	private Vector<ExecutionThread> threads = new Vector<BatchProcessingServer.ExecutionThread>();
	private String servicesUrl;

	public BatchProcessingServer(String tnrsBaseUrl, int port) throws Exception{

		InetSocketAddress address = new InetSocketAddress(port);
		server = HttpServer.create(address, 10);
		jobs = Collections.synchronizedList( new LinkedList<NameMatchingJob>());

		dateFormat = new SimpleDateFormat("E MMM d yyyy h:m:s z");
		Properties props = new Properties();

		props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("tnrs.conf"));

		this.tnrsBaseUrl= props.getProperty("tnrsUrl");
		hostUrl= props.getProperty("hostUrl");
		servicesUrl = props.getProperty("servicesUrl");


		server.createContext("/submit_job", new SubmitRequestHttpHandler());
		server.createContext("/status", new JobStatusRetriever()); 
		server.createContext("/upload", new FileUploadHandler());  
		server.createContext("/retrievedata", new ResultsChunkRetrieverHandler());
		server.createContext("/group",new GroupRetriever());
		server.createContext("/download", new RemoteDownloadHandler());
		server.createContext("/update_group", new GroupUpdater());
		server.start();
	}


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


				NameMatchingJob job = jobs.get(jobno % jobs.size());
				if(job.status().equalsIgnoreCase("idle")) {
					ExecutionThread thread = new ExecutionThread(job);
					threads.add(thread);
					thread.start();
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

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	class ExecutionThread extends Thread{

		NameMatchingJob job;

		public ExecutionThread(NameMatchingJob job) {
			this.job = job;
		}


		@Override
		public void run() {
			try {
				job.setStatus("running");
				if(job.progress()==100.0) {

					synchronized (jobs) {
						jobs.remove(job);
					}

					Email response = new HtmlEmail();
					response.setHostName("localhost");
					response.setSmtpPort(25);
					response.setFrom("iplant-tnrs@iplantcollaborative.org");
					response.setSubject("TNRS Job execution");


					response.setMsg("Your TNRS job for the file "+job.getRequest().getOriginalFilename()+" completed on "+dateFormat.format(new Date())+". Your results will be available for 7 days, after which they will be deleted from our system. 	\n\n" + 
							"To view your results, go to the TNRS website at http://ohmsford.iplantc.org/tnrs-standalone/, select the \"Retrieve results\" tab, and enter your email address and the following submission key:\n\n" + 

							job.getRequest().getId()+"\n\n" + 
							"Please contact us at support@iplantcollaborative.org if you have any difficulty retrieving your results.\n" + 
							"\n" + 
							"Thank you,\n" + 
					"iPlant Collaborative");
					response.addTo(job.getRequest().getEmail());
					response.send();
				}

				URLCodec encoder = new URLCodec();
				String cmd="cmd=tnrs_taxamatch&source=tropicos&str=";
				String data = job.getNextDataBatch();

				if(data==null || data.equals("")) return;

				String[] lines= data.split("\n");
				if(lines[0].equals("null")) {
					return;
				}

				String names="";
				names+= encoder.encode(lines[0].replace(",", "").replace("null", ""));

				for(int i=1; i < lines.length;i++) {
					if(lines[i].trim().equals("")) continue;
					names+= "," + encoder.encode(lines[i].replace(",", ""));
				}


				if(names.equals("")) return;



				HttpClient client = new HttpClient();

				PostMethod post = new PostMethod(tnrsBaseUrl);

				post.setRequestEntity(new StringRequestEntity(cmd+names, "application/x-www-form-urlencoded", "UTF-8"));

				client.executeMethod(post);


				JSONObject json = (JSONObject) JSONSerializer.toJSON(post.getResponseBodyAsString());

				saveResults(job, json);
				job.setStatus("idle");

			}
			catch(Exception ex) {
				ex.printStackTrace();
			}

		}


	}//thread


	class FileUploadHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {


				JSONObject datas = (JSONObject) JSONSerializer.toJSON(IOUtils.toString(arg0.getRequestBody()));

				HashMap<String, String> info = new HashMap<String, String>();
				info.put("email", datas.getString("email"));
				info.put("institution", datas.getString("institution"));
				info.put("name", datas.getString("name"));


				/***
				 * 
				 * To be replaced by better code
				 */

				byte[] data= IOUtils.toByteArray(new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(datas.getString("upload").getBytes()))));

				UniversalDetector detector = new UniversalDetector(null);

				detector.handleData(data, 0, data.length);

				detector.dataEnd();
				String content="";
				String encoding = detector.getDetectedCharset();
				System.out.println(encoding);
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

				System.out.println(content);
				info.put("upload", content);

				/**
				 * 
				 * 
				 */

				JSONObject json = createJSONJobRequest(info);

				json.put("submitted_date",new Date().toString().replace(":", ""));
				json.put("original", datas.getString("file_name"));
				NameMatchingJob job = submitJob(json.toString());


				writeResponseRequest(arg0, 200, null);


				Email response = new HtmlEmail();
				response.setHostName("localhost");
				response.setSmtpPort(25);
				response.setFrom("iplant-tnrs@iplantcollaborative.org");
				response.setSubject("TNRS Job submission");
				response.setMsg("Your TNRS job ("+job.getRequest().getOriginalFilename()+") was successfully submitted on "+dateFormat.format(new Date())+". \n\n" + 
						"When your list is done processing, you will receive an email notification that contains instructions regarding retrieval of your results from the TNRS website at http://ohmsford.iplantc.org/tnrs-standalone/.\n\n"+
						"Please contact us at support@iplantcollaborative.org if you have any difficulty retrieving your results. <br/>\n" + 
						"\n<br/>" +
						job.getRequest().getId()+"<br/><br/>"+
						"Thank you, \n" + 
				"iPlant Collaborative"); 
				response.addTo(json.getString("email"));
				response.send();

			}catch(Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex);
			}

		}


		public JSONObject createJSONJobRequest(HashMap<String, String> data) throws Exception{

			JSONObject json = new JSONObject();

			json.put("institution", data.get("institution").trim());
			json.put("name", data.get("name").trim());
			json.put("email", data.get("email").trim());
			json.put("file_name", persistFileData(data.get("upload"),data.get("email").trim()));

			return json;
		}




		public String persistFileData(String contents,String email) throws Exception {

			File user_dir = new File("/tnrs-jobs/"+email.replace("@","-").replace(".", "-"));

			if(!user_dir.exists()) {
				user_dir.mkdir();
			}

			UUID uid = UUID.randomUUID();

			String filename = user_dir.getAbsolutePath()+"/"+uid.toString().replace("-", "")+new Date().toString().replaceAll("[ |:]", "")+".csv";

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename, true),"UTF-8");
			BufferedWriter bwr = new BufferedWriter(osw,600000);
			bwr.write(contents.trim());
			bwr.close();
			return filename;

		}
	}





	private void saveResults(NameMatchingJob job, JSONObject results) throws Exception{

		String folder_name = "/tnrs-jobs/"+job.getRequest().getEmail().replace("@","-").replace(".", "-");

		String filename="result"+job.getRequest().getId()+"";
		File rsFile = new File(folder_name+"/"+filename);
		boolean header=false;


		if(!rsFile.exists()) {
			header=true;
		}

		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(rsFile, true),"UTF-8");
		BufferedWriter result_file = new BufferedWriter(osw,600000);

		JSONArray array = taxamatchToIplantFormat(results,job.getCurrentBatch()-1);

		result_file.write(jsonToCsv(array.getJSONObject(0), header)+"\n");

		for(int i=1; i < array.size(); i++) {
			result_file.write(jsonToCsv(array.getJSONObject(i),false)+" \n");
		}

		result_file.close();
	}


	private JSONArray taxamatchToIplantFormat(JSONObject json,int batch) {


		JSONArray data = json.getJSONArray("data");

		JSONArray outputArray = new JSONArray();

		long group = (batch)*100;
		for(int i=0; i < data.size(); i++) {

			JSONArray cur_array = data.getJSONArray(i);


			for(int k=0; k < cur_array.size();k++) {
				JSONObject cur_result = cur_array.getJSONObject(k);
				JSONObject item = new JSONObject();

				item.put("group", group);
				item.put("acceptedAuthor",cur_result.optString("Accepted_author","").toString().replace("null", ""));
				item.put("nameSubmitted", cur_result.optString("ScientificName_submitted","").toString().replace("null", ""));
				item.put("url", cur_result.optString("NameSourceUrl","").replace("\\", "").toString().replace("null", ""));
				item.put("nameScientific", cur_result.optString("Lowest_scientificName_matched","").toString().replace("null", ""));
				item.put("scientificScore",cur_result.optString("Lowest_sciName_matched_score", "").toString().replace("null", ""));
				item.put("matchedFamily", cur_result.optString("Family_matched", "").toString().replace("null", ""));
				item.put("matchedFamilyScore", cur_result.optString("Family_matched_score", "").toString().replace("null", ""));
				item.put("authorAttributed", cur_result.optString("Canonical_author","").toString().replace("null", ""));
				item.put("family", cur_result.optString("Accepted_family","").toString().replace("null", ""));
				item.put("genus",cur_result.optString("Genus_matched","").toString().replace("null", ""));
				item.put("genusScore",cur_result.optString("Genus_matched_score", "").toString().replace("null", ""));
				item.put("speciesMatched", cur_result.optString("SpecificEpithet_matched", "").toString().replace("null", ""));
				item.put("speciesMatchedScore", cur_result.optString("SpecificEpithet_matched_score", "").toString().replace("null", ""));
				item.put("infraspecific1Rank", cur_result.optString("infraspecific1Rank", "").toString().replace("null", ""));
				item.put("infraspecific1Epithet",  cur_result.optString("infraspecific1Epithet", "").toString().replace("null", ""));
				item.put("infraspecific1EpithetScore",  cur_result.optString("infraspecific1Score", "").toString().replace("null", ""));
				item.put("infraspecific2Rank", cur_result.optString("infraspecific2Rank", "").toString().replace("null", ""));
				item.put("infraspecific2Epithet",  cur_result.optString("infraspecific2Epithet", "").toString().replace("null", ""));
				item.put("infraspecific2EpithetScore",  cur_result.optString("infraspecific2EpithetScore", "").toString().replace("null", ""));
				item.put("epithet", cur_result.optString("SpecificEpithet_matched","").toString().replace("null", ""));
				item.put("epithetScore", cur_result.optString("SpecificEpithet_matched_score","").toString().replace("null", ""));
				item.put("author",cur_result.optString("Author_matched","").toString().replace("null", "") );
				item.put("authorScore",cur_result.optString("Author_matched_score", "").toString().replace("null", ""));
				item.put("annotation", cur_result.optString("Status", "").toString().replace("null", ""));
				item.put("unmatched", cur_result.optString("Unmatched", "").toString().replace("null", ""));
				item.put("overall", cur_result.optString("Overall_match_score", "").toString().replace("null", ""));
				item.put("acceptedName",cur_result.optString("Accepted_name", "").replace("null", ""));



				if(cur_result.optString("Acceptance", "").replace("null", "").trim().equalsIgnoreCase("A")) {
					item.put("acceptance","Accepted" );
				}else if(cur_result.optString("Acceptance", "").replace("null", "").trim().equalsIgnoreCase("S")) {
					item.put("acceptance","Synonym" );
				}else {
					item.put("acceptance","No opinion" );
				}
				item.put("familySubmitted", cur_result.optString("family", "").replace("null", "") );

				if(k==0) {
					item.put("selected", new Boolean(true));
				}else {
					item.put("selected", new Boolean(false));
				}


				item.put("groupSize",cur_array.size());

				item.put("acceptedNameUrl",  cur_result.optString("Accepted_name_SourceUrl", "").toString().replace("null", ""));

				outputArray.add(item);
			}

			group++;

		}



		return outputArray;
	}

	private String jsonToCsv(JSONObject item, boolean header) {

		StringBuffer buffer = new StringBuffer();
		Vector<String> header_names = new Vector<String>(item.keySet());

		if(header) {

			buffer.append(header_names.elementAt(0));

			for(int i=1; i < header_names.size(); i++) {
				buffer.append("\t ");
				buffer.append(header_names.elementAt(i)+" ");
			}

			buffer.append("\n");
		}


		buffer.append(item.getString(header_names.elementAt(0)));

		for(int i=1; i < header_names.size(); i++) {
			buffer.append("\t ");
			buffer.append(item.getString(header_names.elementAt(i)));

		}

		return buffer.toString();
	}



	class GetStatusHttphandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String address = arg0.getRequestURI().toString();
				String[] components = address.split("/");

				NameMatchingJob job = job_ids.get(components[2]);

				String message = "<html><body> "+job.getRequest().getEmail() + "<br/> Submitted at: " + job.getSubmissionDate()+" <br/> Progress:" + job.progress() +"% <br/> Status: "+ job.status();

				writeResponseRequest(arg0, 200, message);

			}catch(Exception ex) {
				throw new IOException(ex);
			}

		}

	}




	class RemoteDownloadHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			int k=0;
			try {
				String value = IOUtils.toString(arg0.getRequestBody());

				String[] options = value.split("#");

				String email = options[2];
				String key= options[3];

				String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;


				BufferedReader rd = new BufferedReader(new FileReader(filename),204800);
				BufferedWriter wr = new BufferedWriter(new FileWriter("/tmp/csv"+key+".csv"));


				String line="";

				String separator="\t";
				if(options[1].equals("Detailed")) {
					wr.write("group"+separator+"nameSubmitted"+separator+"overallScore"+separator+"nameMatched"+separator+"nameMatchedScore"+separator+"nameMatchedAuthor"+separator+"nameMatchedUrl"+separator+"authorMatched"+separator+"" +
							"authorMatchedScore"+separator+"familyMatched"+separator+"familyMatchedScore"+separator+"genusMatched"+separator+"genusMatchedScore"+separator+"speciesMatched"+separator+"speciesMatchedScore"+separator+"" +
							"infraspecific1Rank"+separator+"infraspecific1Matched"+separator+"infraspecific1MatchedScore"+separator+"infraspecific2Rank"+separator+"infraspecific2RankMatched"+separator+"" +
							"infraspecific2RankMatchedScore"+separator+"annotation"+separator+"unmatchedTerms"+separator+"selected"+separator+"acceptance"+separator+"acceptedFamily"+separator+"acceptedName"+separator+"acceptedNameAuthor"+separator+"" +
					"acceptedNameUrl\n");
					rd.readLine();
				}else {
					String[] head=rd.readLine().split("\t");
					wr.write("nameSubmitted"+separator+"nameMatched"+separator+"nameMatchedAuthor"+separator+"overallScore"+separator+"acceptance"+separator+"acceptedName"+separator+"acceptedNameAuthor"+"\n");
				}

				while(true) {
					line = rd.readLine();
					if(line==null) break;
					line =line.replace("\"", "");
					String[] data = line.split("\t");
					if(options[0].equals("All matches") && options[1].equals("Detailed")) {

						wr.write(data[0]+separator+data[2]+separator+data[26]+separator+data[4]+separator+data[5]+separator+data[8]+separator+data[3]+separator+data[22]+separator+data[23]+separator+data[6]+separator+data[7]+separator+data[10]+separator+data[11]+separator+data[12]+separator+data[13]+separator+data[14]+separator+data[15]+separator+data[16]+separator+data[17]+separator+data[18]+separator+data[19]+separator+data[24]+separator+data[25]+separator+data[30]+separator+data[28]+separator+data[9]+separator+data[27]+separator+data[1]+separator+data[32]+"\n");
						continue;

					}

					k++;
					if(options[0].equals("Best matches only") && options[1].equals("Detailed")) {

						if(data[30].toLowerCase().trim().equals("true")) {
							wr.write(data[0]+separator+data[2]+separator+data[26]+separator+data[4]+separator+data[5]+separator+data[8]+separator+data[3]+separator+data[22]+separator+data[23]+separator+data[6]+separator+data[7]+separator+data[10]+separator+data[11]+separator+data[12]+separator+data[13]+separator+data[14]+separator+data[15]+separator+data[16]+separator+data[17]+separator+data[18]+separator+data[19]+separator+data[24]+separator+data[25]+separator+data[30]+separator+data[28]+separator+data[9]+separator+data[27]+separator+data[1]+separator+data[32]+"\n");
							continue;
						}
					}

					if(options[0].equals("Best matches only") && options[1].equals("Simple")) {

						if(data[30].toLowerCase().trim().equals("true")) {
							wr.write(data[2]+separator+data[4]+separator+data[8]+separator+data[26]+separator+data[28]+separator+data[27]+separator+data[1]+"\n");
							continue;
						}


					}
					if(options[0].equals("All matches") && options[1].equals("Simple")) {

						wr.write(data[2]+separator+data[4]+separator+data[8]+separator+data[26]+separator+data[28]+separator+data[27]+separator+data[1]+"\n");
						continue;
					}


				}

				wr.flush();
				wr.close();

				String url=servicesUrl+"getcsv?id="+key;



				arg0.sendResponseHeaders(200, url.getBytes().length);

				BufferedWriter wr2 = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody()));
				wr2.write(url);
				wr2.close();

			}catch(Exception ex) {
				ex.printStackTrace();
				System.out.println(k);
			}

		}

	}



	class SubmitRequestHttpHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			try {

				submitJob(IOUtils.toString(arg0.getRequestBody()));

				arg0.setAttribute("Content-type", "application/json");
				arg0.sendResponseHeaders(200, 0);

				BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody()));

				bwr.close();

			}catch(Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex);
			}
		}


	}


	public NameMatchingJob submitJob(String jsons) throws Exception{

		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsons);

		NameMatchingRequest request = new NameMatchingRequest(json.getString("email"), json.getString("file_name"),json.getString("original"));
		NameMatchingJob job = new NameMatchingJob(request,json.getString("submitted_date"));
		synchronized (jobs) {
			jobs.add(job);
		}
		job_ids.put(request.getId(), job);

		job.setStatus("idle");


		return job;

	}





	public static void main(String[] args) throws Exception{

		BatchProcessingServer server = new BatchProcessingServer("", 14445);

		server.start();

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

					NameMatchingJob job = jobs.get(i);

					if(job.getRequest().getEmail().equals(email) && job.getRequest().getId().equals(key)) {
						result.put("type", "incomplete");
						result.put("progress", job.progress());
						arg0.sendResponseHeaders(200, result.toString().getBytes().length);
						BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody(),"UTF-8"));
						wr.write(result.toString());
						wr.close();
						return;

					}
				}


				String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;

				File results = new File(filename);

				if(!results.exists()) {
					result.put("type", "non-existent");
				}else {
					result.put("type", "complete");
				}



				arg0.sendResponseHeaders(200, result.toString().getBytes().length);
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody(),"UTF-8"));
				wr.write(result.toString());
				wr.close();
				return;
			}catch(Exception ex ) {
				ex.printStackTrace();
				throw new IOException(ex);
			}

		}

	}


	class ResultsChunkRetrieverHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String jsons = IOUtils.toString(new InputStreamReader(arg0.getRequestBody(),"UTF-8"));

				JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

				String email = json.getString("email");
				String key = json.getString("key");
				int start = json.getInt("start");
				int how_many = json.getInt("how_many");

				//System.out.println(jsons);

				String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;

				File results = new File(filename);

				if(!results.exists()) {
					writeResponseRequest(arg0, 200, " ");
				}

				BufferedReader rd = new BufferedReader(new FileReader(results),60000);

				int c=0;

				String[] header = rd.readLine().split("\t");

				String line ="";

				int l=0;

				JSONObject json_res = new JSONObject();

				JSONArray array = new JSONArray();
				int k=0;
				String group ="";
				while(true) {
					line=rd.readLine();
					if(line==null) break;
					if(line.endsWith("\t")) {
						line+=" ";
					}
					String[] values = line.split("\t");
					if(!values[0].equals(group)) {
						group = values[0];
						k++;
					}
					if(values[30].trim().equals("true")) {
						c++;
					}
					if(c>=start && c<(start+how_many) && values[30].trim().equals("true")) {
						//System.out.println(c);

						JSONObject res = new JSONObject();

						res.put(header[0].trim(),values[0]);

						for(int i=1; i < header.length; i++) {

							res.put(header[i].trim(), values[i].trim());
						}

						array.add(res);
					}


				}

				json_res.put("items", array);
				json_res.put("total",k);


				String result = json_res.toString();
				//System.out.println(result);
				arg0.sendResponseHeaders(200, result.getBytes().length);
				arg0.setAttribute("Content-type", "application/json");

				OutputStreamWriter owr = new OutputStreamWriter(arg0.getResponseBody(),"UTF-8");
				BufferedWriter wr = new BufferedWriter(owr);
				wr.write(result);
				wr.flush();
				wr.close();
			}catch(Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex);

			}

		}


	}


	class JobReporter implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {

			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();


			for(int i=0; i < jobs.size(); i++) {
				JSONObject job_info = new JSONObject();
				NameMatchingJob job = jobs.get(i);

				job_info.put("email", job.getRequest().getEmail());
				job_info.put("progress",job.progress());

				array.add(job_info);
			}

			json.put("jobs", array);

			arg0.sendResponseHeaders(200, json.toString().getBytes().length);

			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody()));
			wr.write(json.toString());
			wr.flush();
			wr.close();
		}

	}

	class GroupRetriever implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String jsons = IOUtils.toString(arg0.getRequestBody());

				JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

				String email = json.getString("email");
				String key = json.getString("key");
				String group = json.getString("group");


				//System.out.println(jsons);

				String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;

				File results = new File(filename);

				if(!results.exists()) {
					writeResponseRequest(arg0, 500, "The requested job doesn't exist or might have already expired");
					return;
				}

				BufferedReader rd = new BufferedReader(new FileReader(results),60000);

				int c=0;

				String[] header = rd.readLine().split("\t");

				String line ="";

				int l=0;

				JSONObject json_res = new JSONObject();

				JSONArray array = new JSONArray();
				int k=0;

				while(true) {
					line=rd.readLine();
					if(line==null) break;
					if(line.endsWith("\t")) {
						line+=" ";
					}
					String[] values = line.split("\t");

					if(values[0].equals(group)) {


						JSONObject res = new JSONObject();

						res.put(header[0].trim(),values[0]);

						for(int i=1; i < header.length; i++) {
							if(header[i].trim().equals("selected")) {
								res.put("selected", Boolean.parseBoolean(values[i]));
								continue;
							}
							res.put(header[i].trim(), values[i].trim());
						}

						array.add(res);
					}


				}

				json_res.put("items", array);
				json_res.put("total",k);


				String result = json_res.toString();

				arg0.sendResponseHeaders(200, result.getBytes().length);
				arg0.setAttribute("Content-type", "application/json");

				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(arg0.getResponseBody()));
				wr.write(result);
				wr.flush();
				wr.close();
			}catch(Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex);

			}

		}
	}



	public void writeResponseRequest(HttpExchange exchange, int status, String response_body) throws Exception{

		if(response_body==null) {
			exchange.sendResponseHeaders(status,0);
			exchange.getResponseBody().close();
			return;
		}


		exchange.sendResponseHeaders(status, response_body.getBytes().length);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody()),40000);
		wr.write(response_body);
		wr.close();


	}


	class GroupUpdater implements HttpHandler {

		@Override
		public void handle(HttpExchange arg0) throws IOException {
			try {
				String jsons = IOUtils.toString(arg0.getRequestBody());

				//System.out.println(jsons);

				JSONObject json = (JSONObject)JSONSerializer.toJSON(jsons);

				String email = json.getString("email");
				String key = json.getString("key");
				String group = json.getString("group");
				int position = json.getInt("selected");

				//System.out.println("entering");

				String filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key;
				String temp_filename ="/tnrs-jobs/"+email.replace("@","-").replace(".", "-")+"/result"+key+"temp";


				File results = new File(filename);

				if(!results.exists()) {
					writeResponseRequest(arg0, 500, "No such job exists or your results might have already expired");
					return;
				}

				File temp_file = new File(temp_filename);

				BufferedReader rd = new BufferedReader(new FileReader(results),60000);
				BufferedWriter wr = new BufferedWriter(new FileWriter(temp_file),80000);
				int c=0;

				String header_line =rd.readLine();
				wr.write(header_line+"\n");
				String[] header = header_line.split("\t");

				String line ="";

				int l=0;


				int k=0;

				while(true) {
					line=rd.readLine();
					if(line==null) break;
					if(line.endsWith("\t")) {
						line+=" ";
					}
					line = line.replace("\"", "");
					String[] values = line.split("\t");

					if(values[0].equals(group)) {
						values[30]="false";

						if(c==position) {
							values[30]="true";
						}

						c++;
						wr.write(values[0]);
						for(int i=1; i < values.length;i++) {
							wr.write("\t"+values[i]);
						}

						wr.write("\n");

					}else {
						wr.write(line+"\n");
					}

				}

				wr.close();
				rd.close();

				results.delete();
				temp_file = new File(temp_filename);
				temp_file.renameTo(new File(filename));

				//System.out.println("exiting");

				arg0.sendResponseHeaders(200, 0);
				arg0.getResponseBody().close();


			}catch(Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex);

			}

		}
	}
}
