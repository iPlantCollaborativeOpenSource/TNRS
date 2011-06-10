package org.iplantc.tnrs.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;


public class NameMatchingJob {

	private NameMatchingRequest request;

	private double progress;
	private int num_records;
	private int nextBatch=0;
	private String submittedAt;
	private String status;
	private int processed=0;
	private int steps;

	public NameMatchingJob(NameMatchingRequest request,String submitted_date) {
		this.request = request;
		submittedAt = submitted_date;
		try {
			BufferedReader rd = new BufferedReader(new FileReader(request.getFilename()));

			while(true) {
				if(rd.readLine()==null) break;
				num_records++;
			}


			steps =(int)Math.ceil(num_records/100.0);
			rd.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getCurrentBatch() {
		return nextBatch;
	}

	public String getNextDataBatch() throws Exception{
		if(nextBatch < steps) {

			int start = 100*nextBatch;
			int end = start+100 -1;

			if(end > num_records) {
				end = num_records;
			}
			System.out.println(nextBatch +"\t"+ start);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(request.getFilename()), "UTF-8");
			BufferedReader rd = new BufferedReader(isr,500000);

			int c=0;
			int k=0;
			String line ="";

			while(true) {

				line = rd.readLine();
				if(line==null) break;

				if(c>=start && c<=end) {
					buffer.append(line+"\n");

					k++;
				}
				c++;
				if(c>end) break;

			}
			rd.close();
			processed+=k;
			nextBatch++;
			progress = ((double)processed/(double)num_records)*100.0;

			return buffer.toString();
		}else {
			return "";
		}
	}

	public void advance() {




	}

	public String status() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmissionDate() {
		return submittedAt;
	}

	public NameMatchingRequest getRequest() {
		return request;
	}

	public double progress() {
		return progress;
	}
}

