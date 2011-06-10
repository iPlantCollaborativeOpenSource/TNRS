package org.iplantc.tnrs.demo.client;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Provides JSON utility operations.
 */
public class JsonUtil
{
	/**
	 * Returns a JavaScript array representation of JSON argument data.
	 * 
	 * @param <T> type of the elements contains in the JavaScript Array.
	 * @param json a string representing data in JSON format.
	 * @return a JsArray of type T.
	 */
	public static final native <T extends JavaScriptObject> JsArray<T> asArrayOf(String json)
	/*-{
		return eval(json);
	}-*/;

	/**
	 * Remove quotes surrounding a JSON string value.
	 * 
	 * @param value string with quotes.
	 * @return a string without quotes.
	 */
	public static String trim(String value)
	{
		StringBuilder temp = null;
		if(value != null && !value.equals(""))
		{
			final String QUOTE = "\"";

			temp = new StringBuilder(value);

			if(value.startsWith(QUOTE))
			{
				temp.deleteCharAt(0);
			}

			if(value.endsWith(QUOTE))
			{
				temp.deleteCharAt(temp.length() - 1);
			}

			return temp.toString();
		}
		else
		{
			return value;
		}
	}

	
	/***
	 * Escape quotes for adding into json String
	 * 
	 * 
	 * 
	 */
	
	public static String escapeQuotes(String value) {
		
		if(value != null && !value.equals(""))
		{
			value = value.replace("\"", "\\\"");
			
		}
		
		return value;
		
	}
	
	
	
	
	/**
	 * Escape new line char in JSON string
	 * 
	 * @param value string to escape.
	 * @return escaped string.
	 */
	public static String escapeNewLine(String value)
	{
		if(value == null || value.equals(""))
		{
			return value;
		}
		else
		{
			return value.replace("\n", "\\n");
		}
	}

	/**
	 * Format strings with new line, tab spaces and carriage returns
	 * 
	 * @param value string to format.
	 * @return formatted string.
	 */
	public static String formatString(String value)
	{
		if(value == null || value.equals(""))
		{
			return value;
		}
		else
		{
			value = value.replace("\\t", "\t");
			value = value.replace("\\r\\n", "\n");
			value = value.replace("\\r", "\n");
			value = value.replace("\\n", "\n");
			return value;
		}
	}
}

