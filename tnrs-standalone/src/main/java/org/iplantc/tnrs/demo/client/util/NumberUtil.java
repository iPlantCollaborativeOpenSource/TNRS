/**
 * 
 */
package org.iplantc.tnrs.demo.client.util;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author raygoza
 *
 */
public class NumberUtil {

	private static NumberFormat fmt = NumberFormat.getFormat("#.00");
	
	public static boolean isDouble(final String test)
	{
		boolean ret = false; // assume failure

		try
		{
			if(test != null)
			{
				Double.parseDouble(test);

				// if we get here, we know parseDouble succeeded
				ret = true;
			}
		}
		catch(NumberFormatException nfe)
		{
			// we are assuming false - setting the return value here would be redundant
		}

		return ret;
	}
	
	
	public static String formatPercentage(final String score)
	{
		String ret = ""; // assume failure... if we have no percentage we just return an
		// empty string

		if(isDouble(score))
		{
			double d = Double.parseDouble(score);
		
			if(d==0.0) return "0";
			ret = fmt.format(d);
		}else{
			return "0";
		}
		
		return ret;
	}
	
}
