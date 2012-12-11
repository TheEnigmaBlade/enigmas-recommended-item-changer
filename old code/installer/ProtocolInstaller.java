package enigma.installer;

import java.io.*;
import javax.swing.*;
import enigma.installer.util.*;


public class ProtocolInstaller
{
	public static void installProtocol(String path, boolean verbose)
	{
		InstallerSettings.loadSettings();
		
		//Get folder currently in
		String programFolder = path;
		if(path == null)
		{
			try
			{
				programFolder = new File(".").getCanonicalPath()+"\\";
			}
			catch(IOException e){}
		}
		
		//Install the run bat file
		String batPath = programFolder+"run_eric.bat";
		String vbsPath = programFolder+"invis.vbs";
		
		File batFile = new File(batPath);
		File vbsFile = new File(vbsPath);
		
		PrintStream batOut = null, vbsOut = null;
		try
		{
			batOut = new PrintStream(batFile);
			batOut.print("cd /d \""+programFolder+"\" && \""+InstallerSettings.getSetting("run")+"\" %*");
			
			vbsOut = new PrintStream(vbsFile);
			vbsOut.print("set args = WScript.Arguments\nnum = args.Count\nsargs = \"\"\nif num > 1 then\n    sargs = \" \"\n    for k = 1 to num - 1\n    	anArg = args.Item(k)\n    	sargs = sargs & anArg & \" \"\n    next\nend if\nSet WshShell = WScript.CreateObject(\"WScript.Shell\")\nWshShell.Run \"\"\"\" & WScript.Arguments(0) & \"\"\"\" & sargs, 0, False");
		}
		catch(FileNotFoundException e)
		{
			Logger.createErrorLog(programFolder);
			Logger.errorOut("Failed to create files");
			Logger.errorTrace(e);
			Logger.closeLogs();
			JOptionPane.showMessageDialog(null, "The ERIC association failed to install.\nError message: "+e.getMessage()+"\nFailed to create files", "Failure", JOptionPane.ERROR_MESSAGE);
			return;
		}
		finally
		{
			if(batOut != null)
				batOut.close();
			if(vbsOut != null)
				vbsOut.close();
		}
		
		try
		{
			Process p = Runtime.getRuntime().exec("attrib +h \""+batFile.getAbsolutePath()+"\"");
			p.waitFor();
			Process p2 = Runtime.getRuntime().exec("attrib +h \""+vbsFile.getAbsolutePath()+"\"");
			p2.waitFor();
		}
		catch(Exception e)
		{
			Logger.createErrorLog(programFolder);
			Logger.errorOut("Failed to set file permissions");
			Logger.errorTrace(e);
		}
		
		//Install registry keys
		
		try
		{
			WinRegistry.createKey(WinRegistry.HKEY_CLASSES_ROOT, "ERIC");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CLASSES_ROOT, "ERIC", "", "URL:ERIC Build Protocol");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CLASSES_ROOT, "ERIC", "URL Protocol", "");
			
			WinRegistry.createKey(WinRegistry.HKEY_CLASSES_ROOT, "ERIC\\DefaultIcon");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CLASSES_ROOT, "ERIC\\DefaultIcon", "", InstallerSettings.getSetting("run")+",1");
			
			WinRegistry.createKey(WinRegistry.HKEY_CLASSES_ROOT, "ERIC\\shell\\open\\command");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CLASSES_ROOT, "ERIC\\shell\\open\\command", "",
					"wscript.exe \""+programFolder+"invis.vbs\" \""+programFolder+"run_eric.bat\" \"%1\"");
		}
		catch(Exception e)
		{
			Logger.createErrorLog(programFolder);
			Logger.errorOut("Failed to install registry keys");
			Logger.errorTrace(e);
			Logger.closeLogs();
			JOptionPane.showMessageDialog(null, "The ERIC association failed to install.\nError message: "+e.getMessage()+"\nFailed to install registry keys", "Failure", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Logger.closeLogs();
		
		if(verbose)
			JOptionPane.showMessageDialog(null, "The ERIC association was successfully installed.", "Success", JOptionPane.INFORMATION_MESSAGE);
	}
}
