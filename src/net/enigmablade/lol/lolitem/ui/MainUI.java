package net.enigmablade.lol.lolitem.ui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.JXCollapsiblePane.*;

import net.enigmablade.paradoxion.localization.*;
import net.enigmablade.paradoxion.ui.*;
import net.enigmablade.paradoxion.ui.components.*;
import net.enigmablade.paradoxion.ui.components.translucent.*;
import net.enigmablade.paradoxion.util.*;
import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lolitem.*;
import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.components.*;
import net.enigmablade.lol.lolitem.ui.components.items.*;
import net.enigmablade.lol.lolitem.ui.components.pretty.*;
import net.enigmablade.lol.lolitem.ui.dialogs.*;
import net.enigmablade.lol.lolitem.ui.dnd.*;
import net.enigmablade.lol.lolitem.ui.models.*;
import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;

public class MainUI extends JFrame implements DragGestureListener
{
	private EnigmaItems main;
	
	//Data
	private Options options;
	private Map<ItemDisplayMode, JRadioButtonMenuItem> itemModeMenuItems;
	private Map<String, JRadioButtonMenuItem> languageMenuItems;
	
	private Map<String, String[]> titles;
	
	//Locks
	private boolean disableItemModeUpdate = false;
	private boolean disableGameModeUpdate = false;
	
	//UI
	private ChampionImagePanel championImagePanel;
	private JLabel championNameLabel, championTitleLabel;
	private ChampionComboBox championComboBox;
	private GameModeComboBox gameModeComboBox;
	
	private ItemBuildComboBox buildComboBox;
	private JButton buildAddButton, buildRemoveButton;
	
	private JButton buildInfoButton;
	private JXCollapsiblePane extraInfoCollapsiblePane;
	private JTextField authorField, typeField;
	private JTextArea descriptionField;
	private ItemGroupListPanel buildGroupList;
	
	private SaveButton saveButton;
	private ResetButton resetButton;
	private JXCollapsiblePane saveResponseCollapsiblePane;
	private JLabel saveResponseLabel;
	
	private FocusTextField filterItemsField;
	private JScrollPane draggableItemsScrollPane;
	private JButton draggableItemsModeButton;
	private DraggableItemContainer draggableItemGridPanel, draggableItemListPanel;
	
	private JCheckBox sortTypeCheckBox;
	private JRadioButton sortDescendingRadioButton, sortAscendingRadioButton;
	private JCheckBox adCheckBox, apCheckBox, hCheckBox, arCheckBox;
	private JCheckBox asCheckBox, cdCheckBox, healthRegenCheckBox, mrCheckBox;
	private JCheckBox lsCheckBox, svCheckBox, mCheckBox, tenCheckBox;
	private JCheckBox arPenCheckBox, mrPenCheckBox, mRegenCheckBox, mvCheckBox;
	private JCheckBox critCheckBox, consumeCheckBox;
	private ArrayList<JCheckBox> filterCheckBoxes;
	
	//Menu bar
	private JMenu fileMenu, buildMenu, optionsMenu, helpMenu;
	
	private JMenuItem importMenuItem, exportMenuItem, copyCodeMenuItem, getCodeMenuItem, openFolderMenuItem, exitMenuItem;
	private JMenuItem newBuildMenuItem, duplicateBuildMenuItem, saveBuildMenuItem, copyBuildMenuItem, clearBuildMenuItem, resetBuildMenuItem, resetDefaultsBuildMenuItem;
	private JCheckBoxMenuItem updateStartupMenuItem, enableTooltipsMenuItem, useDefaultsMenuItem, backupEnableMenuItem, minimizeTrayMenuItem;
	private JMenuItem updateProgramMenuItem, updateCacheMenuItem, manualPathMenuItem, editFavoritesMenuItem, backupRestoreMenuItem;
	private JMenu updateMenu, backupMenu, itemDisplayModeMenu, languageMenu, tooltipsMenu, imageSizeMenu;
	private JRadioButtonMenuItem imageSizeSmallMenuItem, imageSizeNormalMenuItem, imageSizeLargeMenuItem;
	private JMenuItem helpMenuItem, aboutMenuItem, changelogMenuItem, donateMenuItem;
	private JMenu toolsMenu;
	private JMenuItem debugRestartMenuItem;
	
	private JLabel versionLabel;
	
	//Models
	private DraggableItemContainerModel draggableItemModel;
	private ItemFilterModel itemFilterModel;
	
	//Initialization
	
	public MainUI(EnigmaItems m, Options o)
	{
		main = m;
		
		initGlobal();
		
		initMenuBar();
		initOptions(o);
		initMenuActions();
		
		initComponents();
		
		initExtraData();
		
		//Set default states
		setChampion(null);
		updateTitle(null);
		
		enableChampionEditing(false);
		enableBuildEditing(false);
	}
	
	private void initGlobal()
	{
		/*Font mainFont;
		try
		{
			//mainFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceStream("fritz.ttf"));
			//mainFont = mainFont.deriveFont(12.0f);
			mainFont = new Font("Georgia", Font.PLAIN, 10);
		}
		catch(Exception e)
		{
			writeToLog("Failed to load font \"fritz\"", LoggingType.WARNING);
			writeStackTrace(e);
			mainFont = new Font("Times New Roman", Font.PLAIN, 12);
		}
		UIUtil.setUIFont(new FontUIResource(mainFont));*/
		
		UIUtil.setUIBackground(UIUtil.BACKGROUND);
		UIUtil.setUISelectionBackground(UIUtil.FOREGROUND.darker());
		
		UIUtil.setUIForeground(UIUtil.FOREGROUND);
		UIUtil.setUIDisabledForeground(UIUtil.DISABLED_FOREGROUND);
		
		//UIManager.put("ButtonUI", PrettyButton.PrettyButtonUI.class.getName());
	}
	
	private void initMenuBar()
	{
		JMenuBar bar = new PrettyMenuBar();
		setJMenuBar(bar);
		
		//File menu
		fileMenu = new JMenu(" File ");
		fileMenu.setMnemonic('f');
		fileMenu.setForeground(UIUtil.FOREGROUND.darker());
		bar.add(fileMenu);
		
		importMenuItem = new JMenuItem("Import...");
		importMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		importMenuItem.setMnemonic('i');
		importMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/open.png"));
		fileMenu.add(importMenuItem);
		importMenuItem.setEnabled(false);
		
		exportMenuItem = new JMenuItem("Export...");
		exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		exportMenuItem.setMnemonic('e');
		fileMenu.add(exportMenuItem);
		exportMenuItem.setEnabled(false);
		
		fileMenu.addSeparator();
		
		copyCodeMenuItem = new JMenuItem("Copy code to clipboard");
		copyCodeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		copyCodeMenuItem.setMnemonic('c');
		copyCodeMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/copy.png"));
		fileMenu.add(copyCodeMenuItem);
		copyCodeMenuItem.setEnabled(false);
		
		getCodeMenuItem = new JMenuItem("Paste code from clipboard");
		getCodeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		getCodeMenuItem.setMnemonic('p');
		getCodeMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/paste.png"));
		fileMenu.add(getCodeMenuItem);
		getCodeMenuItem.setEnabled(false);
		
		fileMenu.addSeparator();
		
		openFolderMenuItem = new JMenuItem("Open config folder");
		openFolderMenuItem.setMnemonic('f');
		fileMenu.add(openFolderMenuItem);
		
		fileMenu.addSeparator();
		
		exitMenuItem = new JMenuItem("Quit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		exitMenuItem.setMnemonic('q');
		exitMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/quit.png"));
		fileMenu.add(exitMenuItem);
		
		//Build menu
		buildMenu = new JMenu(" Build ");
		buildMenu.setMnemonic('b');
		buildMenu.setForeground(UIUtil.FOREGROUND.darker());
		bar.add(buildMenu);
		
		newBuildMenuItem = new JMenuItem("New");
		newBuildMenuItem.setMnemonic('n');
		newBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newBuildMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/new.png"));
		buildMenu.add(newBuildMenuItem);
		newBuildMenuItem.setEnabled(false);
		
		duplicateBuildMenuItem = new JMenuItem("Duplicate");
		duplicateBuildMenuItem.setMnemonic('d');
		//buildMenu.add(duplicateBuildMenuItem);
		duplicateBuildMenuItem.setEnabled(false);
		
		copyBuildMenuItem = new JMenuItem("Copy...");
		copyBuildMenuItem.setMnemonic('c');	
		//buildMenu.add(copyBuildMenuItem);
		copyBuildMenuItem.setEnabled(false);
		
		buildMenu.addSeparator();
		
		saveBuildMenuItem = new JMenuItem("Save");
		saveBuildMenuItem.setMnemonic('s');
		saveBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveBuildMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/save.png"));
		buildMenu.add(saveBuildMenuItem);
		saveBuildMenuItem.setEnabled(false);
		
		buildMenu.addSeparator();
		
		clearBuildMenuItem = new JMenuItem("Clear");
		clearBuildMenuItem.setMnemonic('l');
		buildMenu.add(clearBuildMenuItem);
		clearBuildMenuItem.setEnabled(false);
		
		resetBuildMenuItem = new JMenuItem("Reset");
		resetBuildMenuItem.setMnemonic('r');
		resetBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		buildMenu.add(resetBuildMenuItem);
		resetBuildMenuItem.setEnabled(false);
		
		resetDefaultsBuildMenuItem = new JMenuItem("Reset to defaults");
		resetDefaultsBuildMenuItem.setMnemonic('f');
		//TODO: buildMenu.add(resetDefaultsBuildMenuItem);
		resetDefaultsBuildMenuItem.setEnabled(false);
		
		//Options menu
		optionsMenu = new JMenu(" Options ");
		optionsMenu.setMnemonic('o');
		optionsMenu.setForeground(UIUtil.FOREGROUND.darker());
		bar.add(optionsMenu);
		
		updateMenu = new JMenu("Updating");
		optionsMenu.add(updateMenu);
		
		updateStartupMenuItem = new JCheckBoxMenuItem("Check for updates on startup");
		updateMenu.add(updateStartupMenuItem);
		
		updateMenu.addSeparator();
		
		updateProgramMenuItem = new JMenuItem("Check for program updates");
		updateProgramMenuItem.setMnemonic('p');
		updateMenu.add(updateProgramMenuItem);
		
		updateCacheMenuItem = new JMenuItem("Check for cache updates");
		updateCacheMenuItem.setMnemonic('c');
		updateMenu.add(updateCacheMenuItem);
		
		backupMenu = new JMenu("Backup");
		//TODO: optionsMenu.add(backupMenu);
		
		backupEnableMenuItem = new JCheckBoxMenuItem("Enable automatic backup");
		backupMenu.add(backupEnableMenuItem);
		
		backupMenu.addSeparator();
		
		backupRestoreMenuItem = new JMenuItem("Restore from backup");
		backupMenu.add(backupRestoreMenuItem);
		
		minimizeTrayMenuItem = new JCheckBoxMenuItem("Minimize to system tray");
		optionsMenu.add(minimizeTrayMenuItem);
		
		manualPathMenuItem = new JMenuItem("Manually set LoL path");
		manualPathMenuItem.setMnemonic('m');
		optionsMenu.add(manualPathMenuItem);
		
		optionsMenu.addSeparator();
		
		itemDisplayModeMenu = new JMenu("Item display mode");
		optionsMenu.add(itemDisplayModeMenu);
		
		ButtonGroup itemDisplayModeButtonGroup = new ButtonGroup();
		itemModeMenuItems = new HashMap<ItemDisplayMode, JRadioButtonMenuItem>();
		
		for(ItemDisplayMode mode : ItemDisplayMode.values())
		{
			String text = mode.toString();
			text = text.substring(0, 1)+text.substring(1).toLowerCase();
			
			JRadioButtonMenuItem displayModeMenuItem = new JRadioButtonMenuItem(text);
			Image img = ResourceLoader.getImage("button_"+mode.toString().toLowerCase()+".png");
			if(img != null)
			{
				Icon icon = new ImageIcon(img.getScaledInstance(16, 16, Image.SCALE_FAST));
				displayModeMenuItem.setIcon(icon);
			}
			itemDisplayModeMenu.add(displayModeMenuItem);
			itemModeMenuItems.put(mode, displayModeMenuItem);
			itemDisplayModeButtonGroup.add(displayModeMenuItem);
		}
		
		imageSizeMenu = new JMenu("Item image size");
		imageSizeMenu.setMnemonic('s');
		optionsMenu.add(imageSizeMenu);
		
		ButtonGroup imageSizeButtonGroup = new ButtonGroup();
		
		imageSizeLargeMenuItem = new JRadioButtonMenuItem("Large");
		imageSizeLargeMenuItem.setIcon(ResourceLoader.getImageIcon("size_large.png"));
		imageSizeButtonGroup.add(imageSizeLargeMenuItem);
		imageSizeMenu.add(imageSizeLargeMenuItem);
		
		imageSizeNormalMenuItem = new JRadioButtonMenuItem("Normal");
		imageSizeNormalMenuItem.setIcon(ResourceLoader.getImageIcon("size_medium.png"));
		imageSizeButtonGroup.add(imageSizeNormalMenuItem);
		imageSizeMenu.add(imageSizeNormalMenuItem);
		
		imageSizeSmallMenuItem = new JRadioButtonMenuItem("Small");
		imageSizeSmallMenuItem.setIcon(ResourceLoader.getImageIcon("size_small.png"));
		imageSizeButtonGroup.add(imageSizeSmallMenuItem);
		imageSizeMenu.add(imageSizeSmallMenuItem);
		
		optionsMenu.addSeparator();
		
		useDefaultsMenuItem = new JCheckBoxMenuItem("Use default items by default");
		useDefaultsMenuItem.setMnemonic('d');
		optionsMenu.add(useDefaultsMenuItem);
		
		editFavoritesMenuItem = new JMenuItem("Edit favorite Champions");
		editFavoritesMenuItem.setMnemonic('f');
		optionsMenu.add(editFavoritesMenuItem);
		
		optionsMenu.addSeparator();
		
		languageMenu = new JMenu("Language");
		optionsMenu.add(languageMenu);
		
		ButtonGroup languageButtonGroup = new ButtonGroup();
		languageMenuItems = new HashMap<String, JRadioButtonMenuItem>();
		
		for(String langKey : LocaleDatabase.languages)
		{
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(LocaleDatabase.getLocaleTitle(langKey)+" ("+langKey+")");
			item.setIcon(ResourceLoader.getImageIcon("menu_icons/flags/"+langKey+".png"));
			item.setPreferredSize(new Dimension(item.getPreferredSize().width, 22));
			item.setActionCommand(langKey);
			languageMenu.add(item);
			languageButtonGroup.add(item);
		}
		
		tooltipsMenu = new JMenu("Tooltips");
		tooltipsMenu.setMnemonic('t');
		optionsMenu.add(tooltipsMenu);
		
		enableTooltipsMenuItem = new JCheckBoxMenuItem("Enable tooltips");
		enableTooltipsMenuItem.setMnemonic('e');
		tooltipsMenu.add(enableTooltipsMenuItem);
		
		//Help menu
		helpMenu = new JMenu(" Help ");
		helpMenu.setMnemonic('h');
		helpMenu.setForeground(UIUtil.FOREGROUND.darker());
		bar.add(helpMenu);
		
		helpMenuItem = new JMenuItem("Help...");
		helpMenuItem.setMnemonic('h');
		helpMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/help.png"));
		helpMenu.add(helpMenuItem);
		
		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');
		helpMenu.add(toolsMenu);
		
		debugRestartMenuItem = new JMenuItem("Restart in debug mode");
		toolsMenu.add(debugRestartMenuItem);
		
		helpMenu.addSeparator();
		
		aboutMenuItem = new JMenuItem("About...");
		aboutMenuItem.setMnemonic('a');
		helpMenu.add(aboutMenuItem);
		
		changelogMenuItem = new JMenuItem("Changelog...");
		changelogMenuItem.setMnemonic('c');
		helpMenu.add(changelogMenuItem);
		
		donateMenuItem = new JMenuItem("Donate...");
		donateMenuItem.setMnemonic('d');
		donateMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/donate.png"));
		helpMenu.add(donateMenuItem);
		
		bar.add(Box.createHorizontalGlue());
		
		versionLabel = new JLabel("Version");
		versionLabel.setForeground(Color.DARK_GRAY);
		bar.add(versionLabel);
		JLabel versionLabel2 = new JLabel(" "+EnigmaItems.version+" "+EnigmaItems.versionAdd+" ");
		versionLabel2.setForeground(Color.DARK_GRAY);
		bar.add(versionLabel2);
	}
	
	private void initMenuActions()
	{
		//File menu
		importMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.importItemSets();
			}
		});
		exportMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.exportItemSets();
			}
		});
		copyCodeMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.exportCode();
			}
		});
		getCodeMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.importCode();
			}
		});
		openFolderMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					writeToLog("Opening character dir: "+GamePathUtil.getChampionDir());
					Runtime.getRuntime().exec("explorer \""+GamePathUtil.getChampionDir()+"\"");
				}
				catch(IOException e)
				{
					writeToLog("Failed to open character folder", LoggingType.ERROR);
					writeStackTrace(e);
					//TODO: localization
					//JOptionPane.showMessageDialog(MainUI.this, getString("dialog.openFolder.failure"), getString("dialog.openFolder.failureTitle"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.close();
			}
		});
		
		//Build menu
		newBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.addBuild();
			}
		});
		duplicateBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.duplicateBuild();
			}
		});
		copyBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
				/*ItemSet itemSet = CopyBuildDialog.open(MainUI.this, currentGameType);
				if(itemSet != null)
				{
					for(int n = 0; n < 6; n++)
						setItem(n, itemSet.getItem(n));
					changeMade = true;
				}*/
			}
		});
		saveBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.save();
			}
		});
		clearBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.clearBuild();
			}
		});
		resetBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.loadChampionBuilds();
			}
		});
		resetDefaultsBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.resetToDefaults();
			}
		});
		updateStartupMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				options.checkVersion = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		updateProgramMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.updateCheck();
			}
		});	
		updateCacheMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.updateCacheCheck();
			}
		});
		backupEnableMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				options.backupEnabled = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		backupRestoreMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.restoreBackup();
			}
		});
		minimizeTrayMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				options.minimizeToTray = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		manualPathMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.editPath();
			}
		});
		useDefaultsMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				//TODO
				/*useDefaultItems = evt.getStateChange() == ItemEvent.SELECTED;
				boolean canReset = true;
				for(ItemSetPanel item : itemPanels)
					if(item != null)
						canReset = false;
				if(canReset)
					setChampionDefaults();*/
			}
		});
		editFavoritesMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				options.favoriteChampions = FavoritesDialog.editFavorites(MainUI.this, new ArrayList<String>(ChampionDatabase.getChampions()), options.favoriteChampions);
				main.rebuildChampionsList();
			}
		});
		
		for(final ItemDisplayMode mode : itemModeMenuItems.keySet())
		{
			itemModeMenuItems.get(mode).addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent evt)
				{
					if(evt.getStateChange() == ItemEvent.SELECTED && !disableItemModeUpdate)
					{
						main.setItemDisplayMode(mode);
					}
				}
			});
		}
		ActionListener languageListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				String lang = evt.getActionCommand();
				if(!lang.equals(options.currentLanguage))
				{
					options.currentLanguage = lang;
					LocaleDatabase.setLocale(options.currentLanguage);
					reloadText();
				}
			}
		};
		for(String lang : languageMenuItems.keySet())
			languageMenuItems.get(lang).addActionListener(languageListener);
		
		imageSizeLargeMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
					setItemImageSize(options.imageSizes[0]);
			}
		});
		imageSizeNormalMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
					setItemImageSize(options.imageSizes[1]);
			}
		});
		imageSizeSmallMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
					setItemImageSize(options.imageSizes[2]);
			}
		});
		enableTooltipsMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				main.setTooltipsEnabled(evt.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		//Help menu
		helpMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://enigmablade.net/lol-item-changer/help.html");
			}
		});
		debugRestartMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				writeToLog("Restarting in debug mode...");
				ProcessBuilder builder = new ProcessBuilder(new String[]{"cmd.exe", "/C", "\"Enigma Item Changer.exe\" -debug"});
				try
				{
					builder.start();
					close();
				}
				catch(Exception e)
				{
					writeToLog("Error while restarting", LoggingType.ERROR);
					writeStackTrace(e);
				}
			}
		});
		aboutMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				AboutDialog.openDialog(MainUI.this, EnigmaItems.version+"."+EnigmaItems.buildVersion+" "+EnigmaItems.versionAdd);
			}
		});
		changelogMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ChangelogDialog.openDialog(MainUI.this);
			}
		});
		donateMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://enigmablade.net/lol-item-changer/download.html");
			}
		});
	}
	
	private void initOptions(Options o)
	{
		options = o;
		updateStartupMenuItem.setSelected(o.checkVersion);
		backupEnableMenuItem.setSelected(o.backupEnabled);
		minimizeTrayMenuItem.setSelected(o.minimizeToTray && o.systemTrayEnabled);
		minimizeTrayMenuItem.setEnabled(o.systemTrayEnabled);
		for(ItemDisplayMode mode : itemModeMenuItems.keySet())
			itemModeMenuItems.get(mode).setSelected(o.itemDisplayMode == mode);
		useDefaultsMenuItem.setSelected(o.useDefaultItems);
		imageSizeSmallMenuItem.setSelected(o.imageSize == o.imageSizes[0]);
		imageSizeNormalMenuItem.setSelected(o.imageSize == o.imageSizes[1]);
		imageSizeLargeMenuItem.setSelected(o.imageSize == o.imageSizes[2]);
		for(String lang : languageMenuItems.keySet())
			languageMenuItems.get(lang).setSelected(lang.equals(o.currentLanguage));
		enableTooltipsMenuItem.setSelected(o.tooltipsEnabled);
	}
	
	private void initComponents()
	{
		//UI
		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(740, 400));
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(ResourceLoader.getIcon());
		setTitle(EnigmaItems.appName);
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				close();
			}
		});
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent evt)
			{
				if(draggableItemGridPanel != null)
					draggableItemGridPanel.refreshPanel();
				if(draggableItemListPanel != null)
					draggableItemListPanel.refreshPanel();
			}
		});
		
		PrettyBackgroundPanel contentPane = new PrettyBackgroundPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		setContentPane(contentPane);
		
		contentPane.setLayout(new BorderLayout(2, 2));
		
		JPanel sidePanel = new JPanel();
		sidePanel.setOpaque(false);
		sidePanel.setPreferredSize(new Dimension(230, 0));
		contentPane.add(sidePanel, BorderLayout.WEST);
		sidePanel.setLayout(new BorderLayout(0, 3));
		
		JPanel championPanel = new PrettyPanel();
		championPanel.setBackground(UIUtil.BACKGROUND);
		championPanel.setPreferredSize(new Dimension(0, 104));
		sidePanel.add(championPanel, BorderLayout.NORTH);
		GridBagLayout gbl_championPanel = new GridBagLayout();
		gbl_championPanel.columnWidths = new int[]{90, 0, 0};
		gbl_championPanel.rowHeights = new int[]{90, 0};
		gbl_championPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_championPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		championPanel.setLayout(gbl_championPanel);
		
		championImagePanel = new ChampionImagePanel((Champion) null);
		GridBagConstraints gbc_championImagePanel = new GridBagConstraints();
		gbc_championImagePanel.insets = new Insets(0, 0, 0, 2);
		gbc_championImagePanel.gridx = 0;
		gbc_championImagePanel.gridy = 0;
		championPanel.add(championImagePanel, gbc_championImagePanel);
		
		JPanel championInfoPanel = new JPanel();
		championInfoPanel.setOpaque(false);
		GridBagConstraints gbc_championInfoPanel = new GridBagConstraints();
		gbc_championInfoPanel.fill = GridBagConstraints.BOTH;
		gbc_championInfoPanel.gridx = 1;
		gbc_championInfoPanel.gridy = 0;
		championPanel.add(championInfoPanel, gbc_championInfoPanel);
		GridBagLayout gbl_championInfoPanel = new GridBagLayout();
		gbl_championInfoPanel.columnWidths = new int[]{0, 0};
		gbl_championInfoPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_championInfoPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_championInfoPanel.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		championInfoPanel.setLayout(gbl_championInfoPanel);
		
		championNameLabel = new JLabel(){
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(getFont());
				g2.setColor(getBackground());
				g2.drawString(getText(), 1, getFont().getSize()+1);
				g2.setColor(getForeground());
				g2.drawString(getText(), 0, getFont().getSize());
			}
		};
		championNameLabel.setFont(championNameLabel.getFont().deriveFont(championNameLabel.getFont().getStyle() | Font.BOLD, championNameLabel.getFont().getSize() + 3f));
		GridBagConstraints gbc_championLabel = new GridBagConstraints();
		gbc_championLabel.anchor = GridBagConstraints.SOUTH;
		gbc_championLabel.gridx = 0;
		gbc_championLabel.gridy = 0;
		championInfoPanel.add(championNameLabel, gbc_championLabel);
		
		championTitleLabel = new JLabel();
		GridBagConstraints gbc_championSubtitleLabel = new GridBagConstraints();
		gbc_championSubtitleLabel.anchor = GridBagConstraints.NORTH;
		gbc_championSubtitleLabel.gridx = 0;
		gbc_championSubtitleLabel.gridy = 1;
		championInfoPanel.add(championTitleLabel, gbc_championSubtitleLabel);
		
		championComboBox = new ChampionComboBox(false);
		championComboBox.setBorder(null);
		championComboBox.setOpaque(false);
		championComboBox.setPreferredSize(new Dimension(0, 22));
		championComboBox.setMaximumRowCount(18);
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.insets = new Insets(0, 0, 2, 0);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 0;
		gbc_comboBox_1.gridy = 2;
		championInfoPanel.add(championComboBox, gbc_comboBox_1);
		
		gameModeComboBox = new GameModeComboBox();
		gameModeComboBox.setBorder(null);
		gameModeComboBox.setOpaque(false);
		gameModeComboBox.setPreferredSize(new Dimension(0, 22));
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 0;
		gbc_comboBox_2.gridy = 3;
		championInfoPanel.add(gameModeComboBox, gbc_comboBox_2);
		
		JPanel buildPanel = new PrettyPanel();
		sidePanel.add(buildPanel, BorderLayout.CENTER);
		GridBagLayout gbl_buildPanel = new GridBagLayout();
		gbl_buildPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_buildPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_buildPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_buildPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		buildPanel.setLayout(gbl_buildPanel);
		
		buildInfoButton = new PrettyButton("Info");
		//buildInfoButton.setToolTipText("Toggle the visibility of item build metadata");
		buildInfoButton.setMinimumSize(new Dimension(34, 22));
		buildInfoButton.setOpaque(false);
		buildInfoButton.setFocusPainted(false);
		buildInfoButton.setPreferredSize(new Dimension(34, 22));
		buildInfoButton.setBorder(null);
		GridBagConstraints gbc_buildInfoButton = new GridBagConstraints();
		gbc_buildInfoButton.insets = new Insets(0, 0, 2, 2);
		gbc_buildInfoButton.gridx = 0;
		gbc_buildInfoButton.gridy = 0;
		buildPanel.add(buildInfoButton, gbc_buildInfoButton);
		
		buildComboBox = new ItemBuildComboBox();
		buildComboBox.setBorder(null);
		GridBagConstraints gbc_buildComboBox = new GridBagConstraints();
		gbc_buildComboBox.insets = new Insets(0, 0, 2, 2);
		gbc_buildComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildComboBox.gridx = 1;
		gbc_buildComboBox.gridy = 0;
		buildPanel.add(buildComboBox, gbc_buildComboBox);
		
		buildRemoveButton = new PrettyButton("\u2212");
		//buildRemoveButton.setToolTipText("Remove the current item build");
		buildRemoveButton.setMinimumSize(new Dimension(22, 22));
		buildRemoveButton.setOpaque(false);
		buildRemoveButton.setPreferredSize(new Dimension(22, 22));
		buildRemoveButton.setBorder(null);
		GridBagConstraints gbc_buildRemoveButton = new GridBagConstraints();
		gbc_buildRemoveButton.insets = new Insets(0, 0, 2, 1);
		gbc_buildRemoveButton.gridx = 2;
		gbc_buildRemoveButton.gridy = 0;
		buildPanel.add(buildRemoveButton, gbc_buildRemoveButton);
		
		buildAddButton = new PrettyButton("+");
		//buildAddButton.setToolTipText("Add a new item build");
		buildAddButton.setMinimumSize(new Dimension(22, 22));
		buildAddButton.setOpaque(false);
		buildAddButton.setFocusPainted(false);
		buildAddButton.setBorder(null);
		buildAddButton.setPreferredSize(new Dimension(22, 22));
		GridBagConstraints gbc_buildAddButton = new GridBagConstraints();
		gbc_buildAddButton.insets = new Insets(0, 0, 2, 0);
		gbc_buildAddButton.gridx = 3;
		gbc_buildAddButton.gridy = 0;
		buildPanel.add(buildAddButton, gbc_buildAddButton);
		
		extraInfoCollapsiblePane = new JXCollapsiblePane();
		extraInfoCollapsiblePane.setCollapsed(true);
		extraInfoCollapsiblePane.setPreferredSize(new Dimension(0, 100));
		JPanel extraInfoCollapsibleContentPane = new JPanel();
		extraInfoCollapsibleContentPane.setBorder(new EmptyBorder(0, 2, 0, 0));
		extraInfoCollapsibleContentPane.setBackground(UIUtil.BACKGROUND);
		extraInfoCollapsiblePane.setContentPane(extraInfoCollapsibleContentPane);
		extraInfoCollapsiblePane.setBackground(UIUtil.BACKGROUND);
		GridBagConstraints gbc_extraInfoCollapsiblePane = new GridBagConstraints();
		gbc_extraInfoCollapsiblePane.insets = new Insets(0, 0, 2, 0);
		gbc_extraInfoCollapsiblePane.anchor = GridBagConstraints.NORTH;
		gbc_extraInfoCollapsiblePane.gridwidth = 4;
		gbc_extraInfoCollapsiblePane.fill = GridBagConstraints.HORIZONTAL;
		gbc_extraInfoCollapsiblePane.gridx = 0;
		gbc_extraInfoCollapsiblePane.gridy = 1;
		buildPanel.add(extraInfoCollapsiblePane, gbc_extraInfoCollapsiblePane);
		GridBagLayout gbl_extraInfoCollapsibleContentPane = new GridBagLayout();
		gbl_extraInfoCollapsibleContentPane.columnWidths = new int[]{0, 0, 0};
		gbl_extraInfoCollapsibleContentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_extraInfoCollapsibleContentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_extraInfoCollapsibleContentPane.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		extraInfoCollapsibleContentPane.setLayout(gbl_extraInfoCollapsibleContentPane);
		
		JLabel authorLabel = new JLabel("Author:");
		GridBagConstraints gbc_authorLabel = new GridBagConstraints();
		gbc_authorLabel.insets = new Insets(0, 0, 2, 2);
		gbc_authorLabel.anchor = GridBagConstraints.EAST;
		gbc_authorLabel.gridx = 0;
		gbc_authorLabel.gridy = 0;
		extraInfoCollapsibleContentPane.add(authorLabel, gbc_authorLabel);
		
		authorField = new JTextField();
		authorField.setCaretColor(UIUtil.FOREGROUND);
		GridBagConstraints gbc_authorField = new GridBagConstraints();
		gbc_authorField.insets = new Insets(0, 0, 2, 0);
		gbc_authorField.fill = GridBagConstraints.HORIZONTAL;
		gbc_authorField.gridx = 1;
		gbc_authorField.gridy = 0;
		extraInfoCollapsibleContentPane.add(authorField, gbc_authorField);
		authorField.setColumns(10);
		
		JLabel typeLabel = new JLabel("Type:");
		GridBagConstraints gbc_typeLabel = new GridBagConstraints();
		gbc_typeLabel.insets = new Insets(0, 0, 2, 2);
		gbc_typeLabel.anchor = GridBagConstraints.EAST;
		gbc_typeLabel.gridx = 0;
		gbc_typeLabel.gridy = 1;
		extraInfoCollapsibleContentPane.add(typeLabel, gbc_typeLabel);
		
		typeField = new JTextField();
		typeField.setCaretColor(UIUtil.FOREGROUND);
		GridBagConstraints gbc_typeField = new GridBagConstraints();
		gbc_typeField.insets = new Insets(0, 0, 2, 0);
		gbc_typeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeField.gridx = 1;
		gbc_typeField.gridy = 1;
		extraInfoCollapsibleContentPane.add(typeField, gbc_typeField);
		typeField.setColumns(10);
		
		JLabel descriptionLabel = new JLabel("Notes:");
		GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
		gbc_descriptionLabel.insets = new Insets(0, 0, 0, 2);
		gbc_descriptionLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_descriptionLabel.gridx = 0;
		gbc_descriptionLabel.gridy = 2;
		extraInfoCollapsibleContentPane.add(descriptionLabel, gbc_descriptionLabel);
		
		JScrollPane descriptionScrollPane = new JScrollPane();
		descriptionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		descriptionScrollPane.setVerticalScrollBar(new PrettyScrollBar(JScrollBar.VERTICAL));
		descriptionScrollPane.setPreferredSize(new Dimension(0, 44));
		descriptionScrollPane.getVerticalScrollBar().setUnitIncrement(4);
		GridBagConstraints gbc_descriptionScrollPane = new GridBagConstraints();
		gbc_descriptionScrollPane.fill = GridBagConstraints.BOTH;
		gbc_descriptionScrollPane.gridx = 1;
		gbc_descriptionScrollPane.gridy = 2;
		extraInfoCollapsibleContentPane.add(descriptionScrollPane, gbc_descriptionScrollPane);
		
		descriptionField = new JTextArea();
		descriptionField.setCaretColor(UIUtil.FOREGROUND);
		descriptionField.setLineWrap(true);
		descriptionField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		descriptionScrollPane.setViewportView(descriptionField);
		
		JPanel separator = new JPanel();
		FlowLayout flowLayout = (FlowLayout) separator.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(0);
		separator.setBackground(UIUtil.COMPONENT_BASE);
		separator.setPreferredSize(new Dimension(0, 4));
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.BOTH;
		gbc_separator.gridwidth = 4;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		//buildPanel.add(separator, gbc_separator);
		
		JScrollPane buildGroupScrollPane = new JScrollPane();
		buildGroupScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		buildGroupScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		buildGroupScrollPane.setVerticalScrollBar(new PrettyScrollBar(JScrollBar.VERTICAL));
		buildGroupScrollPane.setOpaque(false);
		buildGroupScrollPane.getViewport().setOpaque(false);
		buildGroupScrollPane.setBorder(null);
		buildGroupScrollPane.getVerticalScrollBar().setUnitIncrement(8);
		GridBagConstraints gbc_buildGroupScrollPane = new GridBagConstraints();
		gbc_buildGroupScrollPane.gridwidth = 4;
		gbc_buildGroupScrollPane.fill = GridBagConstraints.BOTH;
		gbc_buildGroupScrollPane.gridx = 0;
		gbc_buildGroupScrollPane.gridy = 2;
		buildPanel.add(buildGroupScrollPane, gbc_buildGroupScrollPane);
		
		buildGroupList = new ItemGroupListPanel();
		buildGroupList.setPreferredSize(new Dimension(0, buildGroupScrollPane.getHeight()));
		FlowLayout flowLayout_1 = (FlowLayout) buildGroupList.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);
		buildGroupList.setBorder(new EmptyBorder(4, 4, 4, 4));
		buildGroupList.setOpaque(false);
		buildGroupScrollPane.setViewportView(buildGroupList);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setOpaque(false);
		sidePanel.add(controlPanel, BorderLayout.SOUTH);
		controlPanel.setLayout(new BorderLayout(2, 2));
		
		saveButton = new SaveButton();
		saveButton.setPreferredSize(new Dimension(0, 28));
		controlPanel.add(saveButton, BorderLayout.CENTER);
		
		resetButton = new ResetButton();
		controlPanel.add(resetButton, BorderLayout.EAST);
		
		saveResponseCollapsiblePane = new JXCollapsiblePane();
		saveResponseCollapsiblePane.setDirection(Direction.DOWN);
		saveResponseCollapsiblePane.setCollapsed(true);
		JPanel saveResponseCollapsibleContentPane = new JPanel(){
			@Override
			public void paintComponent(Graphics g)
			{
				g.setColor(new Color(255, 255, 255, 0));
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor(UIUtil.COMPONENT_BASE.darker());
				g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
				g.setColor(UIUtil.scale(UIUtil.ERROR_FOREGROUND, 0.35f));
				g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
			}
		};
		saveResponseCollapsibleContentPane.setPreferredSize(new Dimension(0, 22));
		saveResponseCollapsibleContentPane.setLayout(new BorderLayout());
		saveResponseCollapsibleContentPane.setBorder(new EmptyBorder(0, 4, 0, 0));
		saveResponseCollapsiblePane.setContentPane(saveResponseCollapsibleContentPane);
		
		saveResponseLabel = new JLabel("No errors here! :)");
		saveResponseLabel.setForeground(UIUtil.ERROR_FOREGROUND);
		saveResponseCollapsibleContentPane.add(saveResponseLabel, BorderLayout.CENTER);
		saveResponseCollapsiblePane.setBackground(UIUtil.BACKGROUND);
		controlPanel.add(saveResponseCollapsiblePane, BorderLayout.NORTH);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 3));
		
		JPanel filtersPanel = new PrettyPanel();
		filtersPanel.setPreferredSize(new Dimension(0, 104));
		contentPanel.add(filtersPanel, BorderLayout.NORTH);
		GridBagLayout gbl_filtersPanel = new GridBagLayout();
		gbl_filtersPanel.columnWidths = new int[]{125, 0, 0, 0, 0, 0};
		gbl_filtersPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_filtersPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_filtersPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		filtersPanel.setLayout(gbl_filtersPanel);
		
		JPanel sortPanel = new JPanel();
		sortPanel.setOpaque(false);
		sortPanel.setBorder(new TitledBorder(new LineBorder(UIUtil.BORDER), "Sorting", TitledBorder.LEADING, TitledBorder.TOP, null, UIUtil.FOREGROUND.darker()));
		GridBagConstraints gbc_sortPanel = new GridBagConstraints();
		gbc_sortPanel.insets = new Insets(0, 0, 0, 4);
		gbc_sortPanel.gridheight = 5;
		gbc_sortPanel.fill = GridBagConstraints.BOTH;
		gbc_sortPanel.gridx = 0;
		gbc_sortPanel.gridy = 0;
		filtersPanel.add(sortPanel, gbc_sortPanel);
		GridBagLayout gbl_sortPanel = new GridBagLayout();
		gbl_sortPanel.columnWidths = new int[]{0, 0};
		gbl_sortPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_sortPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_sortPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		sortPanel.setLayout(gbl_sortPanel);
		
		sortAscendingRadioButton = new PrettyRadioButton("Ascending");
		GridBagConstraints gbc_sortAscendingRadioButton = new GridBagConstraints();
		gbc_sortAscendingRadioButton.anchor = GridBagConstraints.WEST;
		gbc_sortAscendingRadioButton.gridx = 0;
		gbc_sortAscendingRadioButton.gridy = 0;
		sortPanel.add(sortAscendingRadioButton, gbc_sortAscendingRadioButton);
		
		sortDescendingRadioButton = new PrettyRadioButton("Descending");
		sortDescendingRadioButton.setSelected(true);
		GridBagConstraints gbc_sortDescendingRadioButton = new GridBagConstraints();
		gbc_sortDescendingRadioButton.anchor = GridBagConstraints.WEST;
		gbc_sortDescendingRadioButton.gridx = 0;
		gbc_sortDescendingRadioButton.gridy = 1;
		sortPanel.add(sortDescendingRadioButton, gbc_sortDescendingRadioButton);
		
		ButtonGroup sortButtonGroup = new ButtonGroup();
		sortButtonGroup.add(sortAscendingRadioButton);
		sortButtonGroup.add(sortDescendingRadioButton);
		
		sortTypeCheckBox = new PrettyCheckBox("Group by type");
		sortTypeCheckBox.setVisible(false);
		GridBagConstraints gbc_sortTypeCheckBox = new GridBagConstraints();
		gbc_sortTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_sortTypeCheckBox.gridx = 0;
		gbc_sortTypeCheckBox.gridy = 3;
		sortPanel.add(sortTypeCheckBox, gbc_sortTypeCheckBox);
		
		filterCheckBoxes = new ArrayList<JCheckBox>();
		
		adCheckBox = new PrettyCheckBox("Attack Damage");
		adCheckBox.setActionCommand(ItemProperty.ATTACK_DAMAGE.name());
		filterCheckBoxes.add(adCheckBox);
		GridBagConstraints gbc_adCheckBox = new GridBagConstraints();
		gbc_adCheckBox.anchor = GridBagConstraints.WEST;
		gbc_adCheckBox.gridx = 1;
		gbc_adCheckBox.gridy = 0;
		filtersPanel.add(adCheckBox, gbc_adCheckBox);
		
		apCheckBox = new PrettyCheckBox("Ability Power");
		apCheckBox.setActionCommand(ItemProperty.ABILITY_POWER.name());
		filterCheckBoxes.add(apCheckBox);
		GridBagConstraints gbc_apCheckBox = new GridBagConstraints();
		gbc_apCheckBox.anchor = GridBagConstraints.WEST;
		gbc_apCheckBox.gridx = 2;
		gbc_apCheckBox.gridy = 0;
		filtersPanel.add(apCheckBox, gbc_apCheckBox);
		
		hCheckBox = new PrettyCheckBox("Health");
		hCheckBox.setActionCommand(ItemProperty.HEALTH.name());
		filterCheckBoxes.add(hCheckBox);
		GridBagConstraints gbc_hCheckBox = new GridBagConstraints();
		gbc_hCheckBox.anchor = GridBagConstraints.WEST;
		gbc_hCheckBox.gridx = 3;
		gbc_hCheckBox.gridy = 0;
		filtersPanel.add(hCheckBox, gbc_hCheckBox);
		
		arCheckBox = new PrettyCheckBox("Armor");
		arCheckBox.setActionCommand(ItemProperty.ARMOR.name());
		filterCheckBoxes.add(arCheckBox);
		GridBagConstraints gbc_arCheckBox = new GridBagConstraints();
		gbc_arCheckBox.anchor = GridBagConstraints.WEST;
		gbc_arCheckBox.gridx = 4;
		gbc_arCheckBox.gridy = 0;
		filtersPanel.add(arCheckBox, gbc_arCheckBox);
		
		asCheckBox = new PrettyCheckBox("Attack Speed");
		asCheckBox.setActionCommand(ItemProperty.ATTACK_SPEED.name());
		filterCheckBoxes.add(asCheckBox);
		GridBagConstraints gbc_asCheckBox = new GridBagConstraints();
		gbc_asCheckBox.anchor = GridBagConstraints.WEST;
		gbc_asCheckBox.gridx = 1;
		gbc_asCheckBox.gridy = 1;
		filtersPanel.add(asCheckBox, gbc_asCheckBox);
		
		cdCheckBox = new PrettyCheckBox("Cooldowns");
		cdCheckBox.setActionCommand(ItemProperty.COOLDOWN.name());
		filterCheckBoxes.add(cdCheckBox);
		GridBagConstraints gbc_cdCheckBox = new GridBagConstraints();
		gbc_cdCheckBox.anchor = GridBagConstraints.WEST;
		gbc_cdCheckBox.gridx = 2;
		gbc_cdCheckBox.gridy = 1;
		filtersPanel.add(cdCheckBox, gbc_cdCheckBox);
		
		healthRegenCheckBox = new PrettyCheckBox("Health Regen");
		healthRegenCheckBox.setActionCommand(ItemProperty.HEALTH_REGEN.name());
		filterCheckBoxes.add(healthRegenCheckBox);
		GridBagConstraints gbc_healthRegenCheckBox = new GridBagConstraints();
		gbc_healthRegenCheckBox.anchor = GridBagConstraints.WEST;
		gbc_healthRegenCheckBox.gridx = 3;
		gbc_healthRegenCheckBox.gridy = 1;
		filtersPanel.add(healthRegenCheckBox, gbc_healthRegenCheckBox);
		
		mrCheckBox = new PrettyCheckBox("Magic Res");
		mrCheckBox.setActionCommand(ItemProperty.MAGIC_RES.name());
		filterCheckBoxes.add(mrCheckBox);
		GridBagConstraints gbc_mrCheckBox = new GridBagConstraints();
		gbc_mrCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mrCheckBox.gridx = 4;
		gbc_mrCheckBox.gridy = 1;
		filtersPanel.add(mrCheckBox, gbc_mrCheckBox);
		
		lsCheckBox = new PrettyCheckBox("Lifesteal");
		lsCheckBox.setActionCommand(ItemProperty.LIFESTEAL.name());
		filterCheckBoxes.add(lsCheckBox);
		GridBagConstraints gbc_lsCheckBox = new GridBagConstraints();
		gbc_lsCheckBox.anchor = GridBagConstraints.WEST;
		gbc_lsCheckBox.gridx = 1;
		gbc_lsCheckBox.gridy = 2;
		filtersPanel.add(lsCheckBox, gbc_lsCheckBox);
		
		svCheckBox = new PrettyCheckBox("Spell Vamp");
		svCheckBox.setActionCommand(ItemProperty.SPELLVAMP.name());
		filterCheckBoxes.add(svCheckBox);
		GridBagConstraints gbc_svCheckBox = new GridBagConstraints();
		gbc_svCheckBox.anchor = GridBagConstraints.WEST;
		gbc_svCheckBox.gridx = 2;
		gbc_svCheckBox.gridy = 2;
		filtersPanel.add(svCheckBox, gbc_svCheckBox);
		
		mCheckBox = new PrettyCheckBox("Mana");
		mCheckBox.setActionCommand(ItemProperty.MANA.name());
		filterCheckBoxes.add(mCheckBox);
		GridBagConstraints gbc_mCheckBox = new GridBagConstraints();
		gbc_mCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mCheckBox.gridx = 3;
		gbc_mCheckBox.gridy = 2;
		filtersPanel.add(mCheckBox, gbc_mCheckBox);
		
		tenCheckBox = new PrettyCheckBox("Tenacity");
		tenCheckBox.setActionCommand(ItemProperty.TENACITY.name());
		filterCheckBoxes.add(tenCheckBox);
		GridBagConstraints gbc_tenCheckBox = new GridBagConstraints();
		gbc_tenCheckBox.anchor = GridBagConstraints.WEST;
		gbc_tenCheckBox.gridx = 4;
		gbc_tenCheckBox.gridy = 2;
		filtersPanel.add(tenCheckBox, gbc_tenCheckBox);
		
		arPenCheckBox = new PrettyCheckBox("Armor Pen");
		arPenCheckBox.setActionCommand(ItemProperty.ARMOR_PEN.name());
		filterCheckBoxes.add(arPenCheckBox);
		GridBagConstraints gbc_arPenCheckBox = new GridBagConstraints();
		gbc_arPenCheckBox.anchor = GridBagConstraints.WEST;
		gbc_arPenCheckBox.gridx = 1;
		gbc_arPenCheckBox.gridy = 3;
		filtersPanel.add(arPenCheckBox, gbc_arPenCheckBox);
		
		mrPenCheckBox = new PrettyCheckBox("Magic Pen");
		mrPenCheckBox.setActionCommand(ItemProperty.MAGIC_PEN.name());
		filterCheckBoxes.add(mrPenCheckBox);
		GridBagConstraints gbc_mrPenCheckBox = new GridBagConstraints();
		gbc_mrPenCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mrPenCheckBox.gridx = 2;
		gbc_mrPenCheckBox.gridy = 3;
		filtersPanel.add(mrPenCheckBox, gbc_mrPenCheckBox);
		
		mRegenCheckBox = new PrettyCheckBox("Mana Regen");
		mRegenCheckBox.setActionCommand(ItemProperty.MANA_REGEN.name());
		filterCheckBoxes.add(mRegenCheckBox);
		GridBagConstraints gbc_mRegenCheckBox = new GridBagConstraints();
		gbc_mRegenCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mRegenCheckBox.gridx = 3;
		gbc_mRegenCheckBox.gridy = 3;
		filtersPanel.add(mRegenCheckBox, gbc_mRegenCheckBox);
		
		mvCheckBox = new PrettyCheckBox("Movement");
		mvCheckBox.setActionCommand(ItemProperty.MOVEMENT.name());
		filterCheckBoxes.add(mvCheckBox);
		GridBagConstraints gbc_mvCheckBox = new GridBagConstraints();
		gbc_mvCheckBox.anchor = GridBagConstraints.WEST;
		gbc_mvCheckBox.gridx = 4;
		gbc_mvCheckBox.gridy = 3;
		filtersPanel.add(mvCheckBox, gbc_mvCheckBox);
		
		critCheckBox = new PrettyCheckBox("Critical Chance");
		critCheckBox.setActionCommand(ItemProperty.CRITICAL.name());
		filterCheckBoxes.add(critCheckBox);
		GridBagConstraints gbc_critCheckBox = new GridBagConstraints();
		gbc_critCheckBox.anchor = GridBagConstraints.WEST;
		gbc_critCheckBox.gridx = 1;
		gbc_critCheckBox.gridy = 4;
		filtersPanel.add(critCheckBox, gbc_critCheckBox);
		
		consumeCheckBox = new PrettyCheckBox("Consumable");
		consumeCheckBox.setActionCommand(ItemProperty.CONSUMABLE.name());
		filterCheckBoxes.add(consumeCheckBox);
		GridBagConstraints gbc_consumeCheckBox = new GridBagConstraints();
		gbc_consumeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_consumeCheckBox.gridx = 3;
		gbc_consumeCheckBox.gridy = 4;
		filtersPanel.add(consumeCheckBox, gbc_consumeCheckBox);
		
		draggableItemsScrollPane = new JScrollPane();
		draggableItemsScrollPane.setBorder(new LineBorder(UIUtil.BORDER));
		draggableItemsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		draggableItemsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		draggableItemsScrollPane.setVerticalScrollBar(new PrettyScrollBar(JScrollBar.VERTICAL));
		draggableItemsScrollPane.getVerticalScrollBar().setUnitIncrement(8);
		draggableItemsScrollPane.setOpaque(false);
		draggableItemsScrollPane.getViewport().setOpaque(false);
		contentPanel.add(draggableItemsScrollPane, BorderLayout.CENTER);
		
		TranslucentPanel header = new TranslucentPanel(75);
		header.setPreferredSize(new Dimension(0, 18));
		header.setLayout(new BorderLayout());
		draggableItemsScrollPane.setColumnHeaderView(header);
		
		filterItemsField = new FocusTextField();
		filterItemsField.setPreferredSize(new Dimension(6, 18));
		header.add(filterItemsField, BorderLayout.CENTER);
		filterItemsField.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 1, UIUtil.COMPONENT_BASE), new EmptyBorder(2, 4, 2, 2)));
		filterItemsField.setBackground(UIUtil.BACKGROUND);
		filterItemsField.setAltText("Filter items by name");
		filterItemsField.setCaretColor(filterItemsField.getForeground());
		filterItemsField.setAltForeground(Color.darkGray);
		filterItemsField.setColumns(10);
		draggableItemsScrollPane.getColumnHeader().setOpaque(false);
		
		contentPanel.add(draggableItemsScrollPane, BorderLayout.CENTER);
		
		draggableItemsModeButton = new PrettyButton();
		draggableItemsModeButton.setOpaque(false);
		draggableItemsModeButton.setIcon(ResourceLoader.getImageIcon("icon_grid.png"));
		draggableItemsModeButton.setBorder(new EmptyBorder(1, 1, 1, 1));
		int neededWidth = draggableItemsScrollPane.getVerticalScrollBar().getPreferredSize().width;
		int neededHeight = draggableItemsScrollPane.getHorizontalScrollBar().getPreferredSize().height;
		draggableItemsModeButton.setPreferredSize(new Dimension(neededWidth, neededHeight));
		draggableItemsModeButton.setFocusPainted(false);
		draggableItemsModeButton.setFocusable(false);
		draggableItemsScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, draggableItemsModeButton);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public void initActions(ItemBuildComboBox.ItemBuildTextListener buildTextListener)
	{
		//Champion box
		championComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					String champName = (String)e.getItem();
					String champKey = ChampionDatabase.getChampionKey(champName);
					main.setChampion(ChampionDatabase.getChampion(champKey));
				}
			}
		});
		gameModeComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED && !disableGameModeUpdate)
				{
					GameMode mode = (GameMode)e.getItem();
					main.setGameMode(mode);
				}
			}
		});
		
		//Item build box
		buildAddButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.addBuild();
			}
		});
		buildRemoveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.removeBuild();
			}
		});
		buildComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED && buildComboBox.getSelectedIndex() != -1)
				{
					main.setBuild(buildComboBox.getSelectedIndex());
				}
			}
		});
		buildComboBox.addBuildTextListener(buildTextListener);
		
		buildInfoButton.addActionListener(extraInfoCollapsiblePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
		
		buildGroupList.initActions(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					main.addBuildGroup();
				}
			},
			new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					main.removeBuildGroup(Integer.parseInt(evt.getActionCommand()));
				}
			}
		);
		
		//Buttons
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.save();
			}
		});
		resetButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.loadChampionBuilds();
			}
		});
		
		//Sorting
		sortAscendingRadioButton.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
					draggableItemModel.setSortMode(DraggableItemContainerModel.SortMode.ASCENDING);
			}
		});
		sortDescendingRadioButton.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
					draggableItemModel.setSortMode(DraggableItemContainerModel.SortMode.DESCENDING);
			}
		});
		
		//Filters
		ItemListener itemFilter = new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				String com = ((JCheckBox)e.getSource()).getActionCommand();
				ItemProperty newFilter = ItemProperty.valueOf(com);
				
				if(e.getStateChange() == ItemEvent.SELECTED)
					itemFilterModel.addItemPropertyFilter(newFilter);
				else
					itemFilterModel.removeItemPropertyFilter(newFilter);
			}
		};
		
		for(JCheckBox box : filterCheckBoxes)
			box.addItemListener(itemFilter);
		
		filterItemsField.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void removeUpdate(DocumentEvent evt)
			{
				updateFilterText(evt);
			}
			
			@Override
			public void insertUpdate(DocumentEvent evt)
			{
				updateFilterText(evt);
			}
			
			@Override
			public void changedUpdate(DocumentEvent evt)
			{
				updateFilterText(evt);
			}
			
			private void updateFilterText(DocumentEvent evt)
			{
				Document doc = evt.getDocument();
				try
				{
					String value = doc.getText(0, doc.getLength());
					main.setItemTextFilter(value);
				}
				catch(BadLocationException e)
				{
					writeToLog("Error when updating item text filter", LoggingType.ERROR);
					writeStackTrace(e);
				}
			}
		});
		
		//Items box
		draggableItemsModeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				main.nextItemDisplayMode();
			}
		});
	}
	
	public void initModels(ItemFilterModel itemFilterModel, DefaultComboBoxModel<GameMode> gameModeModel)
	{
		//Game modes
		gameModeComboBox.setModel(gameModeModel);
		
		//Item panels
		this.itemFilterModel = itemFilterModel;
		draggableItemModel = new DraggableItemContainerModel(itemFilterModel);
		
		ActionListener returnListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				main.clearRelatedItemFilter();
			}
		};
		
		draggableItemGridPanel = new DraggableItemGridContainer(draggableItemModel, null, returnListener);
		draggableItemListPanel = new DraggableItemListContainer(draggableItemModel, null, returnListener);
	}
	
	public void initExtraData()
	{
		//Titles
		titles = new HashMap<String, String[]>();
		titles.put("Ahri",			new String[]{"the Foxy Lady"});
		titles.put("Alistar",		new String[]{"YOU CAN'T MILK THOSE"});
		titles.put("Annie",			new String[]{"MAH BAH TABBAH"});
		titles.put("Ashe",			new String[]{"dat ashe"});
		titles.put("Blitzcrank",	new String[]{"oh blitzcrank you silly goose", "BEEP BOOP FUCK THE TURRET"});
		titles.put("Chogath",		new String[]{"om nom nom"});
		titles.put("Draven",		new String[]{"WELCOME TO THE LEAGUE OF DRAAAAVEN!"});
		titles.put("DrMundo",		new String[]{"MUNDO GOES WHERE HE PLEASES"});
		titles.put("Ezreal",		new String[]{"You belong in a museum"});
		titles.put("Fiddlesticks",	new String[]{"CAW CAW CAW CAW CAW CAW"});
		titles.put("Gangplank",		new String[]{"Ate some oranges and it was k"});
		titles.put("Garen",			new String[]{"DEMACIAAAAAAAAAA!!!"});
		titles.put("Karthus",		new String[]{"Go get 'em, champ!", "#1 Pentakiller"});
		titles.put("Mordekaiser",	new String[]{"HUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUE", "es the best, #1, shield es always win, never loose"});
		titles.put("Nasus",			new String[]{"big dog"});
		titles.put("Rammus",		new String[]{"ok."});
		titles.put("Rumble",		new String[]{"WARNING WARNING WARNING WARNING WARNING"});
		titles.put("Singed",		new String[]{"Shift-4"});
		titles.put("Sivir",			new String[]{"I played Sivir before she was cool"});
		titles.put("Taric",			new String[]{"Truly, truly outrageous"});
		titles.put("Vladimir",		new String[]{"Problem?"});
	}
	
	public void reloadText()
	{
		//TODO
	}
	
	//Data
	
	public void setChampions(List<String> favorites, List<String> remaining)
	{
		championComboBox.updateLists(favorites, remaining);
	}
	
	public void setItems(List<Item> items)
	{
		DragSource dragSource = new DragSource();
		writeToLog("Adding all items to item panel", 1);
		for(Item item : items)
		{
			DraggableItem itemPanel = new DraggableItem(item, itemFilterModel, false);
			itemPanel.setItemSize(options.imageSize, options.imageSize);
			dragSource.createDefaultDragGestureRecognizer(itemPanel, DnDConstants.ACTION_COPY, this);
			draggableItemModel.addItem(itemPanel);
		}
		
		setItemImageSize(options.imageSize);
		
		draggableItemGridPanel.refreshPanel();
		draggableItemListPanel.refreshPanel();
	}
	
	public void setBuilds(List<String> buildNames)
	{
		//buildComboBox.disableItemListeners();
		buildComboBox.setBuilds(buildNames);
		//buildComboBox.enableItemListeners();
		//if(buildNames != null && buildNames.size() > 0)
		//	itemBuildComboBox.setSelectedIndex(0);
	}
	
	public void clearBuilds()
	{
		writeToLog("UI # Clearing item builds");
		buildComboBox.removeAllItems();
	}
	
	public void addBuild(String name)
	{
		writeToLog("UI # Adding build \""+name+"\" to UI");
		
		//itemBuildComboBox.disableItemListeners();
		buildComboBox.addBuild(name);
		//itemBuildComboBox.enableItemListeners();
		
		writeToLog("UI # Setting selected index to: "+(buildComboBox.getItemCount()-1), 1);
		buildComboBox.setSelectedIndex(buildComboBox.getItemCount()-1);
		
		//Set build components enabled
		enableBuildEditing(true);
	}
	
	public int removeBuild(int index)
	{
		writeToLog("UI # Removing build");
		//itemBuildComboBox.disableItemListeners();
		buildComboBox.removeBuild(index);
		//itemBuildComboBox.enableItemListeners();
		
		if(buildComboBox.getItemCount() == 0)
		{
			enableBuildEditing(false);
		}
		else
		{
			//itemBuildComboBox.setSelectedIndex(index-1 < 0 ? 0 : index-1);
		}
		return buildComboBox.getSelectedIndex();
	}
	
	public ItemBuild getBuild(int index)
	{
		ItemBuild build = new ItemBuild((String)buildComboBox.getItemAt(index));
		build.setAuthor(authorField.getText());
		build.setType(typeField.getText());
		build.setDescription(descriptionField.getText());
		
		List<ItemGroupPanel> groupPanels = buildGroupList.getGroups();
		for(ItemGroupPanel groupPanel : groupPanels)
			build.addGroup(groupPanel.getItems());
		
		return build;
	}
	
	public void addBuildGroup(ItemGroup group)
	{
		writeToLog("UI # Adding build group: "+group.getName());
		ItemGroupPanel panel = new ItemGroupPanel(group.getName(), itemFilterModel);
		panel.setItems(group);
		buildGroupList.addGroup(panel);
	}
	
	public void removeBuildGroup(int index)
	{
		writeToLog("UI # Removing build group at index: "+index);
		buildGroupList.removeGroup(index);
	}
	
	//GUI methods
	
	public void setChampion(Champion champion)
	{
		championImagePanel.setChampion(champion);
		championNameLabel.setText(champion != null ? champion.getName() : "");
		championTitleLabel.setText(champion != null ? champion.getTitle() : "");
		
		//buildGroupList.removeAllGroups();
		
		updateTitle(champion);
		
		enableChampionEditing(champion != null);
	}
	
	public void setGameMode(GameMode mode)
	{
		disableGameModeUpdate = true;
		gameModeComboBox.setSelectedItem(mode);
		disableGameModeUpdate = false;
	}
	
	public void setBuild(ItemBuild build)
	{
		writeToLog("UI # Setting item build: "+(build != null ? build.getName() : "null"));
		enableBuildEditing(build != null);
		
		writeToLog("UI # Clearing existing build in UI", 1);
		buildGroupList.removeAllGroups();
		
		if(build != null)
		{
			writeToLog("UI # Updating metadata", 1);
			authorField.setText(build.getAuthor());
			typeField.setText(build.getType());
			descriptionField.setText(build.getDescription());
			
			writeToLog("UI # Adding build groups", 1);
			for(ItemGroup set : build.getGroups())
			{
				writeToLog(set.getName(), 2);
				ItemGroupPanel group = new ItemGroupPanel(set.getName(), itemFilterModel);
				buildGroupList.addGroup(group);
				
				group.refreshPanel();
				buildGroupList.revalidate();
				buildGroupList.repaint();
				group.refreshPanel();
				
				group.setItems(set);
				group.refreshPanel();
			}
		}
		else
		{
			writeToLog("UI # Clearing metadata", 1);
			authorField.setText("");
			typeField.setText("");
			descriptionField.setText("");
		}
		
		enableBuildEditing(build != null);
		
		buildGroupList.revalidate();
		buildGroupList.repaint();
		
		for(ItemGroupPanel panel : buildGroupList.getGroups())
			panel.refreshPanel();
	}
	
	public void setSaveSuccess(boolean success)
	{
		saveButton.setSuccess(success);
		if(success)
			saveResponseCollapsiblePane.setCollapsed(true);
	}
	
	public void setSaveError(String errorText)
	{
		saveResponseLabel.setText(errorText);
		saveResponseCollapsiblePane.setCollapsed(false);
	}
	
	public void setItemDisplayMode(ItemDisplayMode mode)
	{
		//if(!disableItemModeUpdate)
		{
			disableItemModeUpdate = true;
			itemModeMenuItems.get(options.itemDisplayMode).setSelected(true);
			disableItemModeUpdate = false;
			
			draggableItemGridPanel.updateContents(false);
			draggableItemListPanel.updateContents(false);
			
			DraggableItemContainer dragItemCont = null;
			switch(mode)
			{
				case GRID: dragItemCont = draggableItemGridPanel;
					break;
				case LIST: dragItemCont = draggableItemListPanel;
					break;
			}
			
			String nextMode = mode.getNextMode().toString().toLowerCase();
			Icon icon = ResourceLoader.getImageIcon("button_"+nextMode+".png");
			draggableItemsModeButton.setIcon(icon);
			//TODO: localization
			//draggableItemsModeButton.setToolTipText(String.format(getString("main.items.mode.tooltip"), getString("main.menu.options.itemMode."+nextMode)));
			
			draggableItemsScrollPane.getViewport().removeAll();
			draggableItemsScrollPane.setViewportView(dragItemCont);
			dragItemCont.updateContents(true);
			
			draggableItemsScrollPane.revalidate();
			draggableItemsScrollPane.repaint();
		}
	}
	
	public void setItemImageSize(int size)
	{
		options.imageSize = size;
		draggableItemGridPanel.setItemSize(options.imageSize);
		draggableItemListPanel.setItemSize(options.imageSize);
	}
	
	public void setShowRelatedItemReturn(boolean show)
	{
		draggableItemGridPanel.setReturnShown(show);
		draggableItemListPanel.setReturnShown(show);
	}
	
	public void open()
	{
		setVisible(true);
	}
	
	public void close()
	{
		dispose();
		main.close();
	}
	
	//Private GUI methods
	
	private void updateTitle(Champion champion)
	{
		String titleAdd = null;
		if(champion != null)
		{
			String champKey = champion.getKey();
			if(titles.containsKey(champKey))
			{
				String[] list = titles.get(champKey);
				titleAdd = list[(int)(Math.random()*list.length)];
			}
		}
		
		setTitle(EnigmaItems.appName+(titleAdd != null ? ": "+titleAdd : ""));
	}
	
	//GUI enabling and disabling
	
	public void enableChampionEditing(boolean enable)
	{
		if(!enable)
		{
			enableBuildEditing(false);
			
			buildComboBox.setEnabled(false);
			buildRemoveButton.setEnabled(false);
		}
		buildAddButton.setEnabled(enable);
		
		saveButton.setEnabled(enable);
		
		newBuildMenuItem.setEnabled(enable);
		saveBuildMenuItem.setEnabled(enable);
	}
	
	public void enableBuildEditing(boolean enable)
	{
		buildGroupList.setEnabled(enable);
		buildComboBox.setEnabled(enable);
		buildRemoveButton.setEnabled(enable);
		
		resetButton.setEnabled(enable);
		
		clearBuildMenuItem.setEnabled(enable);
		copyBuildMenuItem.setEnabled(enable);
		resetBuildMenuItem.setEnabled(enable);
		resetDefaultsBuildMenuItem.setEnabled(enable);
		duplicateBuildMenuItem.setEnabled(enable);
	}
	
	//Drag and drop methods
	
	public void dragGestureRecognized(DragGestureEvent event)
	{
		writeToLog("Item being dragged");
		Cursor cursor = null;
		DraggableItem panel = (DraggableItem)event.getComponent();
			
		if(event.getDragAction() == DnDConstants.ACTION_COPY)
			cursor = DragSource.DefaultCopyDrop;
		
		event.startDrag(cursor, new TransferableItem(panel.getItem()));
	}
}
