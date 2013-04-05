package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.enigmablade.paradoxion.localization.*;

import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.pretty.*;


public class SaveButton extends PrettyButton
{
	private String saveButtonTextBase = "Save Champion",
		saveButtonTextSuccess = "Champion Saved",
		saveButtonTextFailure = "Save Failed";
	
	private Color baseColor,
		successColor = UIUtil.adjust(new Color(0, 175, 0), 50),
		failureColor = UIUtil.adjust(new Color(175, 0, 0), 50);
	
	public SaveButton()
	{
		setText(saveButtonTextBase);
		
		baseColor = getForeground();
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
		
		Timer timer = new Timer(1500, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				SwingUtilities.invokeLater(new Thread(){
					public void run()
					{
						setText(saveButtonTextBase);
						setForeground(baseColor);
					}
				});
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}
