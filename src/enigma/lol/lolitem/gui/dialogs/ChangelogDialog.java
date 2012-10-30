package enigma.lol.lolitem.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.localization.*;

public class ChangelogDialog extends JDialog
{
	private static ChangelogDialog instance;
	
	//GUI
	private JButton closeButton;
	
	//Data
	private static final String changelogURL = "http://dl.dropbox.com/u/1240253/Software/EnigmaItem/release/changelog.txt";
	
	public ChangelogDialog()
	{
		initComponents();
	}
	
	private void initComponents()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("Changelog");
		setBounds(0, 0, 625, 475);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		closeButton = new JButton("Close");
		closeButton.setPreferredSize(new Dimension(80, 23));
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				dispose();
			}
		});
		buttonPanel.add(closeButton, BorderLayout.EAST);
		getRootPane().setDefaultButton(closeButton);
		
		JScrollPane textScrollPane = new JScrollPane();
		contentPane.add(textScrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		String text = "";
		try
		{
			Scanner scanner = new Scanner(new File("changelog.txt"));
			while(scanner.hasNext())
				text += scanner.nextLine()+"\n";
			textArea.setText(text);
			scanner.close();
		}
		catch(Exception e){}
		if(text.length() == 0)
		{
			String line1 = LocaleDatabase.getString("dialog.changelog.errorLine1");
			String line2 = LocaleDatabase.getString("dialog.changelog.errorLine2");
			textArea.setText(line1+"\n\n"+line2+"\n\n"+changelogURL);
		}
		textScrollPane.setViewportView(textArea);
	}
	
	public static void reloadText()
	{
		if(instance != null)
		{
			instance.setTitle(LocaleDatabase.getString("dialog.changelog.title"));
			instance.closeButton.setText(LocaleDatabase.getString("dialog.changelog.close"));
		}
	}
	
	public static void openDialog(Component parent)
	{
		if(instance == null)
		{
			instance = new ChangelogDialog();
			reloadText();
		}
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
	}
}
