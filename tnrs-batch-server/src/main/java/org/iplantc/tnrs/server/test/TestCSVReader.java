package org.iplantc.tnrs.server.test;

import java.io.File;
import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;

public class TestCSVReader {

	
	public static void main(String[] args) throws Exception{
		
		CSVReader reader = new CSVReader(new FileReader(new File(args[0])), '\t','\0');
	
		
		String[] row =null;
		int k=0;
		while((row=reader.readNext())!=null) {
			System.out.println(k+"  "+row.length +" "+ row[0]);
			k++;
		}
		
		
		reader.close();
		
	}
	
}
