package enigma.installer;

import javax.swing.*;

public class InstallerMain
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e){}
		
		InstallerSettings.loadSettings();
		Installer main = new Installer();
		main.setVisible(true);
	}
}
