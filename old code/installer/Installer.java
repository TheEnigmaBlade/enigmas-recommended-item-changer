package enigma.installer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import enigma.installer.gui.*;
import enigma.installer.gui.components.*;
import enigma.installer.util.*;


public class Installer extends JFrame
{
	private BackgroundPanel background;
	
	private JPanel cardPanel;
	private String[] panels;
	private int currentPanel;
	private JButton backButton, nextButton, cancelButton;
	
	private JTextField installLocationField;
	private JButton installLocationButton;
	private JLabel installSizeLabel;
	private JProgressBar progressBar;
	private JTextArea progressTextArea;
	private TranslucentCheckBox installAssociationCheckBox;
	
	private String[] files;
	private boolean installComplete = false;
	
	public Installer()
	{
		panels = new String[]{"license", "info", "progress"};
		currentPanel = 0;
		
		initComponents();
		initSettings();
	}
	
	private void initComponents()
	{
		setTitle("Enigma's Software Installer");
		setIconImage(ResourceLoader.getIcon());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				close();
			}
		});
		
		background = new BackgroundPanel();
		setContentPane(background);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new MatteBorder(1, 0, 0, 0, (Color) Color.DARK_GRAY));
		buttonPanel.setBackground(GUIUtil.addAlpha(buttonPanel.getBackground(), 175));
		FlowLayout fl_buttonPanel = (FlowLayout)buttonPanel.getLayout();
		fl_buttonPanel.setVgap(2);
		fl_buttonPanel.setHgap(2);
		fl_buttonPanel.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		backButton = new JButton("< Back");
		backButton.setBackground(GUIUtil.addAlpha(backButton.getBackground(), 175));
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				currentPanel--;
				if(currentPanel < 0)
					currentPanel = 0;
				updatePanelChange();
			}
		});
		backButton.setEnabled(false);
		buttonPanel.add(backButton);
		
		nextButton = new JButton("I agree");
		nextButton.setBackground(GUIUtil.addAlpha(backButton.getBackground(), 175));
		nextButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				currentPanel++;
				if(currentPanel > panels.length)
					System.exit(0);
				updatePanelChange();
			}
		});
		buttonPanel.add(nextButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBackground(GUIUtil.addAlpha(backButton.getBackground(), 175));
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		JPanel titlePanel = new JPanel();
		
		titlePanel.setBackground(GUIUtil.addAlpha(titlePanel.getBackground(), 175));
		titlePanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
		FlowLayout fl_titlePanel = (FlowLayout) titlePanel.getLayout();
		fl_titlePanel.setVgap(2);
		fl_titlePanel.setHgap(2);
		fl_titlePanel.setAlignment(FlowLayout.LEFT);
		getContentPane().add(titlePanel, BorderLayout.NORTH);
		
		JLabel titleLabel = new JLabel("Installing Enigma's Recommended Item Changer");
		titleLabel.setBorder(new EmptyBorder(2, 2, 0, 0));
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | Font.BOLD, titleLabel.getFont().getSize() + 1f));
		titlePanel.add(titleLabel);
		
		cardPanel = new JPanel();
		cardPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		cardPanel.setOpaque(false);
		getContentPane().add(cardPanel, BorderLayout.CENTER);
		cardPanel.setLayout(new CardLayout(0, 0));
		
		JPanel licensePanel = new JPanel();
		licensePanel.setOpaque(false);
		licensePanel.setBorder(null);
		cardPanel.add(licensePanel, panels[0]);
		licensePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane licenseScrollPane = new JScrollPane();
		licensePanel.add(licenseScrollPane, BorderLayout.CENTER);
		
		JTextPane licenseTextPane = new JTextPane();
		licenseTextPane.setEditable(false);
		licenseScrollPane.setViewportView(licenseTextPane);
		String licenseText = "";
		try
		{
			Scanner scanner = new Scanner(ResourceLoader.getResourceStream("license.txt"));
			while(scanner.hasNext())
				licenseText += scanner.nextLine()+"\n";
		}
		catch(Exception e){}
		licenseTextPane.setText(licenseText);
		
		TranslucentPanel installInfoPanel = new TranslucentPanel(150);
		installInfoPanel.setOpaque(false);
		installInfoPanel.setBorder(null);
		cardPanel.add(installInfoPanel, panels[1]);
		installInfoPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel installOptionsPanel = new JPanel();
		installOptionsPanel.setOpaque(false);
		installOptionsPanel.setBorder(new TitledBorder(null, "Installation Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		installInfoPanel.add(installOptionsPanel, BorderLayout.SOUTH);
		installOptionsPanel.setLayout(new BorderLayout(0, 0));
		
		installAssociationCheckBox = new TranslucentCheckBox(0);
		installAssociationCheckBox.setText("Install URL association");
		installAssociationCheckBox.setSelected(true);
		installAssociationCheckBox.setOpaque(false);
		installOptionsPanel.add(installAssociationCheckBox);
		
		JPanel installLocationPanel = new JPanel();
		installLocationPanel.setOpaque(false);
		installInfoPanel.add(installLocationPanel, BorderLayout.CENTER);
		GridBagLayout gbl_installLocationPanel = new GridBagLayout();
		gbl_installLocationPanel.columnWidths = new int[]{10, 0, 179, 27, 0};
		gbl_installLocationPanel.rowHeights = new int[]{14, 21, 14, 0};
		gbl_installLocationPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_installLocationPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		installLocationPanel.setLayout(gbl_installLocationPanel);
		
		JLabel installLocationLabel = new JLabel("Install Location:");
		GridBagConstraints gbc_installLocationLabel = new GridBagConstraints();
		gbc_installLocationLabel.gridwidth = 2;
		gbc_installLocationLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_installLocationLabel.insets = new Insets(0, 0, 2, 0);
		gbc_installLocationLabel.gridx = 0;
		gbc_installLocationLabel.gridy = 0;
		installLocationPanel.add(installLocationLabel, gbc_installLocationLabel);
		
		installLocationButton = new JButton("...");
		installLocationButton.setOpaque(false);
		installLocationButton.setBorder(new EmptyBorder(3, 8, 4, 6));
		installLocationButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				chooser.setDialogTitle("Select install location");
				chooser.setApproveButtonToolTipText("Select the install location");
				chooser.setMultiSelectionEnabled(false);
				chooser.setDragEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				File temp = new File(".");
				try
				{
					chooser.setCurrentDirectory(new File(temp.getCanonicalPath()));
				}
				catch(IOException e){}
				
				if(chooser.showDialog(Installer.this, "Select") == JFileChooser.APPROVE_OPTION)
				{
					File selected = chooser.getSelectedFile();
					installLocationField.setText(selected.getAbsolutePath());
				}
			}
		});
		
		installLocationField = new JTextField();
		installLocationField.setText(System.getenv("ProgramFiles")+"\\Enigma Item Changer");
		installLocationField.setColumns(10);
		GridBagConstraints gbc_installLocationField = new GridBagConstraints();
		gbc_installLocationField.fill = GridBagConstraints.HORIZONTAL;
		gbc_installLocationField.anchor = GridBagConstraints.NORTH;
		gbc_installLocationField.insets = new Insets(0, 0, 10, 2);
		gbc_installLocationField.gridwidth = 2;
		gbc_installLocationField.gridx = 1;
		gbc_installLocationField.gridy = 1;
		installLocationPanel.add(installLocationField, gbc_installLocationField);
		GridBagConstraints gbc_installLocationButton = new GridBagConstraints();
		gbc_installLocationButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_installLocationButton.insets = new Insets(0, 0, 2, 0);
		gbc_installLocationButton.gridx = 3;
		gbc_installLocationButton.gridy = 1;
		installLocationPanel.add(installLocationButton, gbc_installLocationButton);
		
		JLabel installSizeTitleLabel = new JLabel("Installation size:");
		GridBagConstraints gbc_installSizeTitleLabel = new GridBagConstraints();
		gbc_installSizeTitleLabel.insets = new Insets(0, 0, 0, 2);
		gbc_installSizeTitleLabel.gridwidth = 2;
		gbc_installSizeTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_installSizeTitleLabel.gridx = 0;
		gbc_installSizeTitleLabel.gridy = 2;
		installLocationPanel.add(installSizeTitleLabel, gbc_installSizeTitleLabel);
		
		installSizeLabel = new JLabel("0 KB");
		GridBagConstraints gbc_installSizeLabel = new GridBagConstraints();
		gbc_installSizeLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_installSizeLabel.insets = new Insets(0, 0, 0, 2);
		gbc_installSizeLabel.gridx = 2;
		gbc_installSizeLabel.gridy = 2;
		installLocationPanel.add(installSizeLabel, gbc_installSizeLabel);
		
		TranslucentPanel progressPanel = new TranslucentPanel(150);
		progressPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		cardPanel.add(progressPanel, panels[2]);
		progressPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel progressInfoPanel = new JPanel();
		progressInfoPanel.setOpaque(false);
		progressInfoPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		progressPanel.add(progressInfoPanel, BorderLayout.NORTH);
		progressInfoPanel.setLayout(new GridLayout(2, 1, 0, 2));
		
		JLabel progressLabel = new JLabel("Installation progress:");
		progressInfoPanel.add(progressLabel);
		
		progressBar = new JProgressBar();
		progressBar.setOpaque(true);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(0, 20));
		progressInfoPanel.add(progressBar);
		
		JScrollPane progressScrollPane = new JScrollPane();
		progressPanel.add(progressScrollPane, BorderLayout.CENTER);
		
		progressTextArea = new JTextArea();
		progressTextArea.setFont(new Font("Tahoma", progressTextArea.getFont().getStyle(), progressTextArea.getFont().getSize() - 1));
		DefaultCaret caret = (DefaultCaret)progressTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		caret.setVisible(false);
		progressTextArea.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				update();
			}
			
			private void update()
			{
				progressTextArea.setCaretPosition(progressTextArea.getDocument().getLength());
			}
		});
		progressScrollPane.setViewportView(progressTextArea);
		
		setPreferredSize(new Dimension(350, 275));
		setMinimumSize(new Dimension(350, 250));
		pack();
		setLocationRelativeTo(null);
	}
	
	private void initSettings()
	{
		//Title
		setTitle(InstallerSettings.getSetting("name"));
		
		//Size
		double size = Integer.parseInt(InstallerSettings.getSetting("size"));
		String ext = "KB";
		if(size >= 1024)
		{
			size /= 1024;
			ext = "MB";
		}
		NumberFormat formatter = new DecimalFormat("0.0");
		installSizeLabel.setText(formatter.format(size)+" "+ext);
		
		//Files
		String fileString = InstallerSettings.getSetting("files");
		files = fileString.split(",");
		
	}
	
	//UI methods
	
	public void updatePanelChange()
	{
		switch(currentPanel)
		{
			case 0: backButton.setEnabled(false);
				nextButton.setText("I agree");
				break;
			case 1: backButton.setEnabled(true);
				nextButton.setText("Next >");
				break;
			case 2: backButton.setEnabled(false);
				nextButton.setEnabled(false);
				nextButton.setText("Finish");
				
				new InstallThread().execute();
				break;
			case 3: System.exit(0);
		}
		
		((CardLayout)cardPanel.getLayout()).show(cardPanel, panels[currentPanel]);
		background.changeImage(currentPanel+1);
	}
	
	public void close()
	{
		if(installComplete || JOptionPane.showConfirmDialog(Installer.this, "Are you sure you want to cancel the installation?", "Cancel?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			System.exit(0);
	}
	
	//Installation threading
	
	private class InstallThread extends SwingWorker<Object, Object>
	{
		@Override
		public Object doInBackground()
		{
			progressTextArea.setText("Starting installation...");
			
			//Copy files
			File installLocation = new File(installLocationField.getText());
			if(!installLocation.exists())
				installLocation.mkdirs();
			
			int amountPerUpdate = 100/(files.length+1);
			for(int n = 0; n < files.length; n++)
			{
				progressTextArea.setText(progressTextArea.getText()+"\nCopying: "+files[n]);
				
				try
				{
					IOUtil.copyFile(ResourceLoader.getResourceStream(files[n]), ResourceLoader.getFile(installLocation, files[n]));
				}
				catch(IOException e)
				{
					cancel(true);
				}
				
				progressBar.setValue(progressBar.getValue()+amountPerUpdate);
				
				try
				{
					Thread.sleep(50);	
				}
				catch(Exception e){}
			}
			
			//Install protocol
			if(installAssociationCheckBox.isSelected())
			{
				progressTextArea.setText(progressTextArea.getText()+"\nInstalling URL association...");
				ProtocolInstaller.installProtocol(installLocation.getAbsolutePath()+"\\", false);
			}
			
			progressBar.setValue(100);			
			progressTextArea.setText(progressTextArea.getText()+"\nInstallation complete");
			
			return null;
		}
		
		@Override
		protected void done()
		{
			installComplete = true;
			
			Toolkit.getDefaultToolkit().beep();
			nextButton.setEnabled(true);
			cancelButton.setEnabled(false);
		}
	}
}
