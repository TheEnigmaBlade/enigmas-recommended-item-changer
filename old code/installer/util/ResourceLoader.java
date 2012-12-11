package enigma.installer.util;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ResourceLoader
{
	private static ClassLoader classLoader;
	
	private static String resourcesPath = "";
	private static String imagePath = resourcesPath+"images/";
	
	static
	{
		classLoader = ResourceLoader.class.getClassLoader();
	}
	
	//General resource methods
	
	public static URL getResource(String name)
	{
		return classLoader.getResource(resourcesPath+name);
	}
	
	public static InputStream getResourceStream(String name)
	{
		return classLoader.getResourceAsStream(resourcesPath+name);
	}
	
	//Program-specific methods
	
	public static Image getIcon()
	{
		return getImage("icon.png");
	}
	
	public static ImageIcon getImageIcon(String name)
	{
		URL resource = classLoader.getResource(imagePath+name);
		if(resource != null)
			return new ImageIcon(resource);
		return null;
	}
	
	public static Image getImage(String name)
	{
		ImageIcon icon = getImageIcon(name);
		if(icon != null)
			return getImageIcon(name).getImage();
		return null;
	}
	
	//Utility methods
	
	public static File getFile(File dir, String name)
	{
		return new File(dir.getAbsoluteFile()+File.separator+name);
	}
	
	public static InputStream getFileStream(File dir, String name) throws FileNotFoundException
	{
		return new FileInputStream(getFile(dir, name));
	}
}
