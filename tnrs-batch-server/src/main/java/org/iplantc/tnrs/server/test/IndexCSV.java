package org.iplantc.tnrs.server.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

public class IndexCSV {

	public static void main(String[] args)throws Exception {
		
		FileInputStream is = new FileInputStream(args[0]);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		
		CSVReader csvrd = new CSVReader(rd,'\t','\0');

		BufferedWriter wr = new BufferedWriter(new FileWriter(new File(args[0].replace("csv", "")+".idx")));
		
		int k=0;
		int group=-1;
		int m=0;
		
		csvrd.readNext();
		while(true) {
			String[] values = csvrd.readNext();
			if(values==null) break;
			//System.out.println(Integer.parseInt(values[0].trim()));
			if(Integer.parseInt(values[0].trim())!=group) {
				k++;
				group = Integer.parseInt(values[0].trim());
			}
			
			if(k%100==0) {
				
				System.out.println(is.getChannel().position());
				wr.write(is.getChannel().position()+"\n");
				m++;
				k=1;
			}
		}
		
		wr.close();
		csvrd.close();
		
	}
	
	
}
