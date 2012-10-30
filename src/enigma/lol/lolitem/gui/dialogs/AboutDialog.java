package enigma.lol.lolitem.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.localization.*;
import enigma.paradoxion.ui.*;
import enigma.paradoxion.util.*;
import enigma.lol.lolitem.*;

public class AboutDialog extends JDialog
{
	private static AboutDialog instance;
	
	private JLabel titleLabel, versionTitleLabel;
	private JLabel devByTitleLabel, devAidTitleLabel, specialTitleLabel;
	private JButton websiteButton, forumsButton, redditButton;
	private JLabel copyright1Label, copyright2Label;
	private JButton closeButton;
	
	public AboutDialog(String version)
	{
		initComponents(version);
	}
	
	private void initComponents(String version)
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setResizable(false);
		setTitle("About");
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
		setContentPane(contentPane);
		
		contentPane.setPreferredSize(new Dimension(400, 250));
		contentPane.setLayout(new BorderLayout(0, 4));
		
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(207, 207, 207));
		headerPanel.setBorder(new CompoundBorder(new LineBorder(new Color(128, 128, 128)), new EmptyBorder(4, 4, 4, 4)));
		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BorderLayout(10, 0));
		
		IconPanel iconPanel = new IconPanel();
		//iconPanel.setOpaque(false);
		iconPanel.setBackground(headerPanel.getBackground());
		headerPanel.add(iconPanel, BorderLayout.WEST);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		headerPanel.add(titlePanel, BorderLayout.CENTER);
		titlePanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		titleLabel = new JLabel(LoLItems.appName);
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | Font.BOLD, titleLabel.getFont().getSize() + 4f));
		titlePanel.add(titleLabel);
		
		JPanel versionPanel = new JPanel();
		versionPanel.setOpaque(false);
		FlowLayout fl_versionPanel = (FlowLayout) versionPanel.getLayout();
		fl_versionPanel.setHgap(3);
		fl_versionPanel.setVgap(4);
		fl_versionPanel.setAlignment(FlowLayout.LEFT);
		titlePanel.add(versionPanel);
		
		versionTitleLabel = new JLabel("Version: ");
		versionTitleLabel.setFont(versionTitleLabel.getFont().deriveFont(versionTitleLabel.getFont().getSize() + 1f));
		versionPanel.add(versionTitleLabel);
		
		JLabel versionLabel = new JLabel(version);
		versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getSize() + 1f));
		versionPanel.add(versionLabel);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 4));
		
		JPanel linksPanel = new JPanel();
		linksPanel.setBorder(null);
		mainPanel.add(linksPanel, BorderLayout.SOUTH);
		linksPanel.setLayout(new GridLayout(1, 0, 2, 0));
		
		websiteButton = new JButton("Website");
		websiteButton.setFocusPainted(false);
		websiteButton.setToolTipText("http://enigmablade.net/eric/");
		websiteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://enigmablade.net/eric/");
			}
		});
		linksPanel.add(websiteButton);
		
		redditButton = new JButton("Reddit");
		redditButton.setFocusPainted(false);
		redditButton.setToolTipText("http://redd.it/os4o2");
		redditButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://redd.it/os4o2");
			}
		});
		linksPanel.add(redditButton);
		
		forumsButton = new JButton("Official Forums");
		forumsButton.setFocusPainted(false);
		forumsButton.setToolTipText("http://na.leagueoflegends.com/board/showthread.php?t=1208749");
		forumsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://na.leagueoflegends.com/board/showthread.php?t=1208749");
			}
		});
		linksPanel.add(forumsButton);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(new Color(220, 220, 220));
		infoPanel.setBorder(new CompoundBorder(new LineBorder(new Color(128, 128, 128)), new EmptyBorder(4, 4, 4, 4)));
		mainPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BorderLayout(4, 0));
		
		JPanel infoTitlesPanel = new JPanel();
		infoTitlesPanel.setOpaque(false);
		infoPanel.add(infoTitlesPanel, BorderLayout.WEST);
		GridBagLayout gbl_infoTitlesPanel = new GridBagLayout();
		gbl_infoTitlesPanel.columnWidths = new int[]{0, 0};
		gbl_infoTitlesPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_infoTitlesPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_infoTitlesPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoTitlesPanel.setLayout(gbl_infoTitlesPanel);
		
		devByTitleLabel = new JLabel("Developed by:");
		GridBagConstraints gbc_devByTitleLabel = new GridBagConstraints();
		gbc_devByTitleLabel.anchor = GridBagConstraints.EAST;
		gbc_devByTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_devByTitleLabel.gridx = 0;
		gbc_devByTitleLabel.gridy = 0;
		infoTitlesPanel.add(devByTitleLabel, gbc_devByTitleLabel);
		
		devAidTitleLabel = new JLabel("Contributers:");
		GridBagConstraints gbc_devAidTitleLabel = new GridBagConstraints();
		gbc_devAidTitleLabel.anchor = GridBagConstraints.EAST;
		gbc_devAidTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_devAidTitleLabel.gridx = 0;
		gbc_devAidTitleLabel.gridy = 1;
		infoTitlesPanel.add(devAidTitleLabel, gbc_devAidTitleLabel);
		
		specialTitleLabel = new JLabel("Special thanks:");
		GridBagConstraints gbc_specialTitleLabel = new GridBagConstraints();
		gbc_specialTitleLabel.anchor = GridBagConstraints.EAST;
		gbc_specialTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_specialTitleLabel.gridx = 0;
		gbc_specialTitleLabel.gridy = 2;
		infoTitlesPanel.add(specialTitleLabel, gbc_specialTitleLabel);
		
		JLabel filler1 = new JLabel(" ");
		GridBagConstraints gbc_filler1 = new GridBagConstraints();
		gbc_filler1.insets = new Insets(0, 0, 5, 0);
		gbc_filler1.gridx = 0;
		gbc_filler1.gridy = 3;
		infoTitlesPanel.add(filler1, gbc_filler1);
		
		JLabel filler2 = new JLabel(" ");
		GridBagConstraints gbc_filler2 = new GridBagConstraints();
		gbc_filler2.insets = new Insets(0, 0, 5, 0);
		gbc_filler2.gridx = 0;
		gbc_filler2.gridy = 4;
		infoTitlesPanel.add(filler2, gbc_filler2);
		
		JLabel filler3 = new JLabel(" ");
		GridBagConstraints gbc_filler3 = new GridBagConstraints();
		gbc_filler3.gridx = 0;
		gbc_filler3.gridy = 5;
		infoTitlesPanel.add(filler3, gbc_filler3);
		
		JPanel infoContentPanel = new JPanel();
		infoContentPanel.setOpaque(false);
		infoPanel.add(infoContentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_infoContentPanel = new GridBagLayout();
		gbl_infoContentPanel.columnWidths = new int[]{0, 0};
		gbl_infoContentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_infoContentPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_infoContentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoContentPanel.setLayout(gbl_infoContentPanel);
		
		JLabel devByLabel = new JLabel("Enigma/TheEnigmaBlade");
		GridBagConstraints gbc_devByLabel = new GridBagConstraints();
		gbc_devByLabel.anchor = GridBagConstraints.WEST;
		gbc_devByLabel.insets = new Insets(0, 0, 5, 0);
		gbc_devByLabel.gridx = 0;
		gbc_devByLabel.gridy = 0;
		infoContentPanel.add(devByLabel, gbc_devByLabel);
		
		JLabel devAidLabel = new JLabel("IceFire, Owen Durni");
		GridBagConstraints gbc_devAidLabel = new GridBagConstraints();
		gbc_devAidLabel.anchor = GridBagConstraints.WEST;
		gbc_devAidLabel.insets = new Insets(0, 0, 5, 0);
		gbc_devAidLabel.gridx = 0;
		gbc_devAidLabel.gridy = 1;
		infoContentPanel.add(devAidLabel, gbc_devAidLabel);
		
		JLabel special1Label = new JLabel("Bad News Panda, IkagiKotono, WorldwidePanther");
		GridBagConstraints gbc_special1Label = new GridBagConstraints();
		gbc_special1Label.insets = new Insets(0, 0, 5, 0);
		gbc_special1Label.anchor = GridBagConstraints.WEST;
		gbc_special1Label.gridx = 0;
		gbc_special1Label.gridy = 2;
		infoContentPanel.add(special1Label, gbc_special1Label);
		
		JLabel special2Label = new JLabel("FearMyWrench, PrivateNickel, xMoko, doubledog");
		GridBagConstraints gbc_special2Label = new GridBagConstraints();
		gbc_special2Label.insets = new Insets(0, 0, 5, 0);
		gbc_special2Label.anchor = GridBagConstraints.WEST;
		gbc_special2Label.gridx = 0;
		gbc_special2Label.gridy = 3;
		infoContentPanel.add(special2Label, gbc_special2Label);
		
		JLabel special3Label = new JLabel("/r/LeagueOfLegends");
		GridBagConstraints gbc_special3Label = new GridBagConstraints();
		gbc_special3Label.insets = new Insets(0, 0, 5, 0);
		gbc_special3Label.anchor = GridBagConstraints.WEST;
		gbc_special3Label.gridx = 0;
		gbc_special3Label.gridy = 4;
		infoContentPanel.add(special3Label, gbc_special3Label);
		
		JLabel special4Label = new JLabel("Riot Games");
		GridBagConstraints gbc_special4Label = new GridBagConstraints();
		gbc_special4Label.anchor = GridBagConstraints.WEST;
		gbc_special4Label.gridx = 0;
		gbc_special4Label.gridy = 5;
		infoContentPanel.add(special4Label, gbc_special4Label);
		
		JPanel footerPanel = new JPanel();
		footerPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
		contentPane.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel copyrightPanel = new JPanel();
		footerPanel.add(copyrightPanel, BorderLayout.CENTER);
		copyrightPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		copyright1Label = new JLabel("Champion and item images, Copyright (c) Riot Games");
		copyrightPanel.add(copyright1Label);
		
		copyright2Label = new JLabel("Copyright (c) 2012, Tyler Haines (Enigma/TheEnigmaBlade)");
		copyrightPanel.add(copyright2Label);
		
		closeButton = new JButton("Close");
		closeButton.setFocusPainted(false);
		closeButton.setPreferredSize(new Dimension(70, 23));
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				dispose();
			}
		});
		footerPanel.add(closeButton, BorderLayout.EAST);
		getRootPane().setDefaultButton(closeButton);
		
		pack();
	}
	
	public static void reloadText()
	{
		if(instance != null)
		{
			instance.setTitle(LocaleDatabase.getString("dialog.about.title"));
			instance.titleLabel.setText(LocaleDatabase.getString("main.title"));
			instance.versionTitleLabel.setText(LocaleDatabase.getString("dialog.about.version")+": ");
			instance.devByTitleLabel.setText(LocaleDatabase.getString("dialog.about.devby")+":");
			instance.devAidTitleLabel.setText(LocaleDatabase.getString("dialog.about.devaid")+":");
			instance.specialTitleLabel.setText(LocaleDatabase.getString("dialog.about.thanks")+":");
			instance.websiteButton.setText(LocaleDatabase.getString("dialog.about.website"));
			instance.forumsButton.setText(LocaleDatabase.getString("dialog.about.forums"));
			instance.copyright1Label.setText("Champion and item images, "+LocaleDatabase.getString("dialog.about.copyright")+" (c) Riot Games");
			instance.copyright2Label.setText(LocaleDatabase.getString("dialog.about.copyright")+" (c) 2012, Tyler Haines (Enigma/TheEnigmaBlade)");
			instance.closeButton.setText(LocaleDatabase.getString("dialog.about.close"));
		}
	}
	
	public static void openDialog(Component parent, String version)
	{
		if(instance == null)
		{
			instance = new AboutDialog(version);
			reloadText();
		}
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
	}
	
	//Secret components
	
	private class IconPanel extends JPanel
	{
		private Image icon;
		private double rotation = 0;
		private double rotationV = 0, rotationVMax = Math.toRadians(20), rotationA = Math.toRadians(1);
		private Timer animator;
		private boolean stopping = false;
		
		public IconPanel()
		{
			setOpaque(false);
			setPreferredSize(new Dimension(48, 48));
			setFocusable(true);
			
			icon = ResourceLoader.getIcon();
			if(icon != null)
				icon = icon.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
			
			addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent evt)
				{
					stopping = false;
					if(animator == null)
						animator = new Timer(30, new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent evt)
							{
								rotationV += (stopping ? -1 : 1)*rotationA;
								if(rotationV > rotationVMax)
									rotationV = rotationVMax;
								if(rotationV < 0)
									rotationV = 0;
								rotation += rotationV;
								repaint();
								if(stopping && rotationV == 0)
									animator.stop();
							}
						});
					animator.start();
				}
				
				@Override
				public void mouseReleased(MouseEvent evt)
				{
					//if(animator != null)
						//	animator.stop();
					stopping = true;
				}
			});
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			AffineTransform save = g2.getTransform();
			AffineTransform rotate = new AffineTransform(save);
			rotate.rotate(rotation, 24, 24);
			g2.setTransform(rotate);
			g2.drawImage(icon, 0, 0, null);
			g2.setTransform(save);
		}
	}
}
