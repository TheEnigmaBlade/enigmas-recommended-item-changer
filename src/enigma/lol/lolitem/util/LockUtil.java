package enigma.lol.lolitem.util;

import java.io.*;

import enigma.lol.lollib.io.*;

public class LockUtil
{
	public static boolean createLock()
	{
		File itemFile = new File(GamePathUtil.getItemDir()+".lock");
		if(itemFile.exists())
			return false;
		try
		{
			itemFile.createNewFile();
			itemFile.deleteOnExit();
		}
		catch(IOException e){}
		
		return true;
	}
	
	public static void deleteLock()
	{
		File itemFile = new File(GamePathUtil.getItemDir()+".lock");
		if(itemFile.exists())
			itemFile.delete();
	}
}
