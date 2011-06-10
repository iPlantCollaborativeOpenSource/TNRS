/**
 * 
 */
package org.iplantc.tnrs.demo.client.util;

/**
 * @author raygoza
 *
 */
public class NumberUtil {

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

			int percentage = (int)(d * 100.0);
			ret = percentage + "%";
		}else {
			ret="";
		}

		return ret;
	}
	
}
