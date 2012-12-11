package enigma.installer.util;

import java.io.*;

public class IOUtil
{
	public static void copyFile(InputStream in, File destFile) throws IOException
	{
		if(!destFile.exists())
			destFile.createNewFile();
		
		OutputStream out = new FileOutputStream(destFile);
		byte[] buf = new byte[1024];
		int len;
		while((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
		
		in.close();
		out.close();
	}
}
