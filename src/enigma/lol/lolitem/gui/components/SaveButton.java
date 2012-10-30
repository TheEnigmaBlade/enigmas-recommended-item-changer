package enigma.lol.lolitem.gui.components;

import java.awt.*;
import javax.swing.*;
import enigma.paradoxion.localization.*;

public class SaveButton extends JButton
{
	private static String saveButtonTextBase = "Save Champion",
						  saveButtonTextSuccess = "Champion Saved",
						  saveButtonTextFailure = "Save Failed";
	
	private static Color baseColor = Color.black,
						 successColor = new Color(0, 175, 0),
						 failureColor = new Color(175, 0, 0);
	
	public SaveButton()
	{
		super(saveButtonTextBase);
		
		//setForeground(baseColor);
	}
	
	public void reloadText()
	{
		saveButtonTextBase = LocaleDatabase.getString("main.buttons.save");
		saveButtonTextSuccess = LocaleDatabase.getString("main.buttons.save.success");
		saveButtonTextFailure = LocaleDatabase.getString("main.buttons.save.failure");
		setText(saveButtonTextBase);
	}
	
	public void setSuccess(boolean success)
	{
		if(success)
		{
			setText(saveButtonTextSuccess);
			setForeground(successColor);
		}
		else
		{
			setText(saveButtonTextFailure);
			setForeground(failureColor);
		}
		
		SwingUtilities.invokeLater(new Thread(){
			public void run()
			{
				try
				{
					sleep(1500);
				}
				catch(InterruptedException e){}
				
				setText(saveButtonTextBase);
				setForeground(baseColor);
			}
		});
	}
}
