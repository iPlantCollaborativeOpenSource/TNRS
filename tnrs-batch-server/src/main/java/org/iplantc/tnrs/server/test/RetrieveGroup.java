package org.iplantc.tnrs.server.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

public class RetrieveGroup {

	public static void main(String[] args)throws Exception {
		
		FileInputStream is = new FileInputStream(args[0]);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		
		FileInputStream is_idx = new FileInputStream(args[0].replace(".csv","")+".idx");
		
		String[] indices = IOUtils.toString(is_idx).split("\n");
		
		int group = Integer.parseInt(args[2]);
		
		int idx = (int)Math.floor((double)group/100.0);
		
		long start = Long.parseLong(indices[idx]);
		
		
		
		CSVReader csvrd = new CSVReader(rd,'\t','\0');
		is.getChannel().position(start);
		
		
		
		
		int ogroup = -1;
		
		
		while(true) {
			String[] values = csvrd.readNext();
			if(values==null) break;
			
			if(group==Integer.parseInt(values[0].trim())) {
				System.out.println(values[0]);
				
			}
		}
		
		
		csvrd.close();
		
	}
	
	
}
