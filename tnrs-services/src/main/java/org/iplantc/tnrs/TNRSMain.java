package org.iplantc.tnrs;

import org.mule.MuleServer;


public class TNRSMain {

	public static void main(String[] args) throws Exception{


		try {
			System.out.println("Starting tnrs standalone services...");
			MuleServer server = new MuleServer("mule-config.xml");
			System.out.println("Initializing Server...");
			server.initialize();
			System.out.println("Server initialization complete...");
			System.out.println("Running...");
			server.start(false, false);

		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

}

