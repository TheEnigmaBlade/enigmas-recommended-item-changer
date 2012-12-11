package enigma.installer.util;

import java.io.*;

public class Logger
{
	private static PrintStream errorOut;
	
	public static void createErrorLog(String folder)
	{
		if(errorOut == null)
		{
			File outFile = new File(new File(folder).getAbsoluteFile()+"/install_error.log");
			try
			{
				errorOut = new PrintStream(outFile);
			}
			catch(FileNotFoundException e){}
		}
	}
	
	public static void closeLogs()
	{
		if(errorOut != null)
			errorOut.close();
		errorOut = null;
	}
	
	//IO
	
	public static void errorOut(String s)
	{
		if(errorOut != null)
			errorOut.println(s);
	}
	
	public static void errorTrace(Throwable t)
	{
		if(errorOut != null)
			t.printStackTrace(errorOut);
	}
}
