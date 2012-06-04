package org.iplantc.tnrs.demo.client;

/**
 * Basic interface for command pattern.
 * 
 * @author amuir
 * 
 */
public interface ClientCommand
{
	/**
	 * Execute command.
	 */
	void execute(String params);
	
}