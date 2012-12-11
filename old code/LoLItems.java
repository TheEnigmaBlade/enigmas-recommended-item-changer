package enigma.lol.lolitem;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;

import net.enigmablade.paradoxion.localization.*;
import net.enigmablade.paradoxion.io.*;
import net.enigmablade.paradoxion.io.cache.*;
import net.enigmablade.paradoxion.ui.*;
import net.enigmablade.paradoxion.ui.components.*;
import net.enigmablade.paradoxion.ui.components.translucent.*;
import net.enigmablade.paradoxion.util.*;
import net.enigmablade.paradoxion.util.Logger.*;
import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.gui.dialogs.*;
import net.enigmablade.lol.lollib.io.*;
import net.enigmablade.lol.lollib.io.pathhelpers.regions.*;
import net.enigmablade.lol.lollib.io.pathhelpers.platforms.*;

import enigma.lol.lolitem.ui.SplashScreen;
import enigma.lol.lolitem.ui.*;
import enigma.lol.lolitem.ui.components.*;
import enigma.lol.lolitem.ui.dialogs.*;
import enigma.lol.lolitem.ui.dnd.*;
import enigma.lol.lolitem.ui.models.*;
import enigma.lol.lolitem.ui.renderers.*;
import enigma.lol.lolitem.data.*;
import enigma.lol.lolitem.io.*;
import enigma.lol.lolitem.util.*;
import enigma.lol.lolitem.data.filter.*;

public class LoLItems extends JFrame implements DragGestureListener
{
	public static final String appName = "Enigma's Recommended Item Changer";
	public static final String appKey = "EnigmaItem";
	public static final String version = "2.4.0", buildVersion = "0", versionAdd = "dev";
	
	//GUI
	private BackgroundPanel backgroundPanel;
	private ChampionImagePanel championImagePanel;
	private JLabel championNameLabel, championTitleLabel;
	private ChampionComboBox championComboBox;
	private GameModeComboBox gameModeComboBox;
	private ItemSetPanel[] itemPanels;
	private EditableLabel itemCategoryLabel1, itemCategoryLabel2, itemCategoryLabel3, itemCategoryLabel4;
	private BuildPanel itemCategoryPanel1, itemCategoryPanel2, itemCategoryPanel3, itemCategoryPanel4;
	private JScrollPane draggableItemsScrollPane;
	private TranslucentPanel draggableItemsViewportView;
	private JButton draggableItemsModeButton;
	private JLabel draggableItemsHeaderLabel;
	private DraggableItemContainer draggableItemGridPanel, draggableItemListPanel;
	private DraggableItemContainerModel draggableItemModel;
	private JLabel itemSetsLabel;
	private JComboBox<String> itemSetComboBox;
	private SelectionListCellRenderer<String> itemSetRenderer;
	private JButton itemSetAddButton, itemSetRemoveButton;
	private SaveButton saveButton;
	private ResetButton resetButton;
	
	private JPanel itemSelectionPanel;
	
	private TranslucentPanel filtersPanel;
	private FocusTextField itemFilterTextField;
	private JLabel categoriesLabel, statsLabel;
	private TranslucentCheckBox attackCheckBox, defenseCheckBox, movementCheckBox, magicCheckBox, consumableCheckBox,
								attackDamageCheckBox, abilityPowerCheckBox,
								attackSpeedCheckBox, cooldownCheckBox,
								armorCheckBox, magicResCheckBox,
								healthCheckBox, healthRegenCheckBox,
								manaCheckBox, manaRegenCheckBox,
								armorPenCheckBox, magicPenCheckBox,
								lifestealCheckBox, spellVampCheckBox,
								criticalStrikeCheckBox, tenacityCheckBox;
	
	private TranslucentPanel shopPanel;
	private HistoryPanel shopHistoryPanel;
	private ShopPanel shopCardsPanel;
	private JButton defenseShopButton, attackShopButton, magicShopButton, movementShopButton, consumableShopButton,
					attackDamageShopButton, attackSpeedShopButton, criticalStrikeShopButton, lifestealShopButton,
					abilityPowerShopButton, cooldownShopButton, manaShopButton, manaRegenShopButton,
					healthShopButton, magicResShopButton, armorShopButton, healthRegenShopButton;
	private JButton backAttackShopButton, backMagicShopButton, backDefenseShopButton, backMovementButton, backConsumablesButton,
					backAttackDamageShopButton, backAttackSpeedShopButton, backCriticalStrikeShopButton, backLifestealShopButton,
					backAbilityPowerShopButton, backCooldownShopButton, backManaShopButton, backManaRegenButton,
					backHealthShopButton, backMagicResistShopButton, backArmorShopButton, backHealthRegenShopButton;
	
	private List<TranslucentPanel> translucentPanels = new LinkedList<TranslucentPanel>();
	
	//Menu bar
	private JMenu fileMenu, buildMenu, optionsMenu, helpMenu;
	
	private JMenuItem importMenuItem, exportMenuItem, copyCodeMenuItem, getCodeMenuItem, openFolderMenuItem, launchMenuItem, exitMenuItem;
	private JMenuItem newBuildMenuItem, duplicateBuildMenuItem, saveBuildMenuItem, copyBuildMenuItem, clearBuildMenuItem, resetBuildMenuItem, resetDefaultsBuildMenuItem;
	private JCheckBoxMenuItem updateStartupMenuItem, enableTooltipsMenuItem, useFixedWidthMenuItem, advancedFiltersMenuItem, useDefaultsMenuItem, backupEnableMenuItem, minimizeTrayMenuItem;
	private JMenuItem updateProgramMenuItem, updateCacheMenuItem, manualPathMenuItem, editFavoritesMenuItem, backupRestoreMenuItem;
	private JMenu updateMenu, backupMenu, itemDisplayModeMenu, languageMenu, backgroundImageMenu, backgroundSolidsMenu, backgroundGradientsMenu, tooltipsMenu, imageSizeMenu;
	private JRadioButtonMenuItem backgroundNoImage, imageSizeMenuItems[];
	private JMenuItem helpMenuItem, aboutMenuItem, changelogMenuItem, donateMenuItem;
	private JMenu toolsMenu;
	private JMenuItem debugRestartMenuItem;
	
	private JLabel versionLabel;
	
	private Map<ItemDisplayMode, JRadioButtonMenuItem> itemModeMenuItems;
	private Map<String, JRadioButtonMenuItem> colorMenuItems;
	
	//System tray
	private SystemTray systemTray;
	private TrayIcon trayIcon;
	private JPopupMenu popup;
	private JMenuItem sysTrayVersionMenuItem, sysTrayTitleMenuItem;
	private JMenu sysTrayOptionsMenu;
	private JMenuItem sysTrayManualPathMenuItem, sysTrayEditFavoritesMenuItem;
	private JMenuItem sysTrayDisplayMenuItem, sysTrayExitMenuItem;
	
	//GUI Data
	private boolean displayed = true;
	private boolean systemTrayPressed = false;
	
	private String titleBase = appName;
	
	private String noChampionText = "No Champion selected";
	private String defaultItemsText = "Default Items";
	private String newItemSetText = "New Set";
	
	private String sysTrayHideWindowText = "Hide window";
	private String sysTrayShowWindowText = "Show window";
	
	private int checkBoxAlpha1 = 125, checkBoxAlpha2 = 25;
	
	private int shopButtonWidth = 150, shopButtonHeight = 30;
	
	private Map<String, String> defaultBackgrounds;
	private ArrayList<String> customBackgrounds;
	private Map<String, String> backgroundColors, backgroundGradients;
	
	private HashMap<String, String[]> titles;
	
	//Data
	private List<String> allChampions, favoriteChampions;
	private Champion currentChampion;
	private List<ItemSet> championItems;
	
	private GameMode currentGameType = GameMode.CLASSIC;
	private int currentItemSet = 0;
	private ItemFilterModel itemFilterModel;
	
	private int listNum = 1;
	private AtomicBoolean disableComboBoxUpdate = new AtomicBoolean(false);
	private boolean disableItemModeUpdate = false;
	
	//Options
	private String lolDirPath = null;
	private String lolDirRegion = null;
	private Deque<GamePath> lolDirHistory;
	
	private boolean checkVersion;
	private static final String[] languages = {"en-US", "en-UK", "en-CA", "en-AU"};
	private String currentLanguage = languages[0];
	private int[] imageSizes = {60, 45, 30};
	private int imageSize = 60;
	private String currentBackground;
	private boolean tooltipsEnabled = true, fixedTooltipWidth = false;
	private boolean useAdvancedFilters = false;
	private enum ItemDisplayMode { GRID("LIST"), LIST("GRID");
		private String next;
		
		private ItemDisplayMode(String n)
		{
			next = n;
		}
		
		public ItemDisplayMode getNextMode()
		{
			return valueOf(next);
		}
		
		public static ItemDisplayMode getType(String s)
		{
			ItemDisplayMode type = ItemDisplayMode.valueOf(s.toUpperCase());
			if(type != null)
				return type;
			writeToLog("ItemDisplayType - Invalid conversion: "+s, LoggingType.WARNING);
			return GRID;
		}
	};
	private ItemDisplayMode itemDisplayMode = ItemDisplayMode.GRID;
	private boolean useDefaultItems = true;
	private boolean backupEnabled = false;
	private String backupLocation = "backup";
	private boolean minimizeToTray = false;
	private boolean systemTrayEnabled = false;
	
	private boolean changeMade;
	private boolean showSaveWarning = true;
	private int saveDefault = JOptionPane.YES_OPTION;
	
	
	public LoLItems()
	{
		writeToLog("App info: "+appName+" ("+appKey+"), v"+version+"b"+buildVersion+" "+versionAdd);
		
		UpdateUtil.finishUpdate();
		
		writeToLog("Begin startup initialization");
		
		loadOptions();
		
		if(checkVersion)
		{
			SplashScreen.drawString("Checking version...");
			UpdateUtil.startUpdater(appKey, version, buildVersion, false);
			UpdateUtil.startCacheUpdater(false);
		}
		
		SplashScreen.drawString("Loading resources...");
		writeToLog("Extracting resource cache");
		boolean extracted = CacheResourceLoader.initialize(new File("cache.dat"), appKey);
		if(!extracted)
		{
			writeToLog("Failed to extract resource cache", LoggingType.ERROR);
			JOptionPane.showMessageDialog(null, "Failed to extract data files", "Load error", JOptionPane.ERROR_MESSAGE);
			SystemUtil.exit(1);
		}
		
		LocaleDatabase.loadLocales("resources.locales", languages);
		LocaleDatabase.setLocale(currentLanguage);
		
		SplashScreen.drawString("Finding paths...");
		writeToLog("Initializing LoL file IO");
		GamePathUtil.initialize(lolDirPath, Region.stringToRegion(lolDirRegion));
		lolDirHistory = new ArrayDeque<GamePath>();
		
		//if(!createLock() && JOptionPane.showConfirmDialog(this, "There is another instance of the item changer currently running.\nMultiple instances could cause a loss of saved data.\n\nDo you want to launch the additional instance?", "Instance locked", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
		//	SystemUtil.exit(0);
		
		SplashScreen.drawString("Loading UI...");
		writeToLog("Initializing UI");
		initData();
		initSystemTray();
		initMenuBar();
		initComponents();
		showAdvancedFilters(useAdvancedFilters);
		setBackground(currentBackground);
		reloadText();
		
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setReshowDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		
		SplashScreen.drawString("Loading data...");
		writeToLog("Initializing item data");
		boolean success = loadItems();
		writeToLog("Initializing champion data");
		success &= loadChampions();
		
		if(success)
		{
			setTooltipsFixed(fixedTooltipWidth);
			setItemImageSize(imageSize);
			
			writeToLog("Startup initialization complete");
			checkGameVersion();
			writeToLog("------------------------------------");
		}
		else
		{
			writeToLog("Failed to initialize", LoggingType.ERROR);
			JOptionPane.showMessageDialog(null, "Failed to initialize the program", "Load error", JOptionPane.ERROR_MESSAGE);
			SystemUtil.exit(1);
		}
	}
	
	//region: Initialization
	
	public void initData()
	{
		writeToLog("Loading UI data", 1);
		
		//Backgrounds
		backgroundColors = new LinkedHashMap<String, String>();
		backgroundColors.put("Red", "16711680");
		backgroundColors.put("Orange", "16756480");
		backgroundColors.put("Yellow", "16776960");
		backgroundColors.put("Green", "65280");
		backgroundColors.put("Cornflower blue", "6591981");
		backgroundColors.put("Blue", "255");
		backgroundColors.put("Purple", "11468975");
		backgroundColors.put("Gray", "6579300");
		backgroundColors.put("Black", "0");
		
		backgroundGradients = new LinkedHashMap<String, String>();
		backgroundGradients.put("Green-Blue", "9880943-13163448-43488");
		backgroundGradients.put("Purple-Blue", "13395711-12641520-43487");
		
		if(currentBackground == null)
			currentBackground = "gradient-"+backgroundGradients.get("Green-Blue");
		
		//Titles
		titles = new HashMap<String, String[]>();
		addTitle("Alistar", "YOU CAN'T MILK THOSE");
		addTitle("Annie", "MAH BAH TABBAH");
		addTitle("Ashe", "dat ashe");
		addTitle("Blitzcrank", "oh blitzcrank you silly goose", "BEEP BOOP FUCK THE TURRET");
		addTitle("Chogath", "om nom nom");
		addTitle("DrMundo", "MUNDO GOES WHERE HE PLEASES");
		addTitle("Ezreal", "You belong in a museum");
		addTitle("Fiddlesticks", "CAW CAW CAW CAW CAW CAW");
		addTitle("Gangplank", "Ate some oranges and it was k");
		addTitle("Garen", "DEMACIAAAAAAAAAA!!!");
		addTitle("Karthus", "Go get 'em, champ!", "#1 Pentakiller");
		addTitle("Mordekaiser", "HUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUEHUE", "es the best, #1, shield es always win, never loose");
		addTitle("Nasus", "big dog");
		addTitle("Rammus", "ok.");
		addTitle("Rumble", "WARNING WARNING WARNING WARNING WARNING");
		addTitle("Singed", "Shift-4");
		addTitle("Sivir", "I played Sivir before she was cool");
		addTitle("Taric", "Truly, truly outrageous");
		addTitle("Vladimir", "Problem?");
	}
	
	private void addTitle(String champ, String... title)
	{
		titles.put(champ, title);
	}
	
	//SAVE: initSystemTray
	private void initSystemTray()
	{
		writeToLog("Loading system tray", 1);
		if(SystemTray.isSupported())
		{
			try
			{
				systemTray = SystemTray.getSystemTray();
			}
			catch(Exception e)
			{
				writeToLog("Failed to get the system tray", 2);
				writeStackTrace(e);
			}
			
			if(systemTrayEnabled = systemTray != null)
			{
				//Create pop-up menu
				popup = new JPopupMenu();
				popup.addFocusListener(new FocusAdapter(){
					@Override
					public void focusLost(FocusEvent e)
					{
						popup.setVisible(false);
					}
				});
				
				sysTrayTitleMenuItem = new JMenuItem(appName);
				sysTrayTitleMenuItem.setEnabled(false);
				popup.add(sysTrayTitleMenuItem);
				
				sysTrayVersionMenuItem = new JMenuItem("    Version "+version+" "+versionAdd);
				sysTrayVersionMenuItem.setEnabled(false);
				popup.add(sysTrayVersionMenuItem);
				
				popup.addSeparator();
				
				//Options menu
				
				sysTrayOptionsMenu = new JMenu("Options");
				popup.add(sysTrayOptionsMenu);
				
				sysTrayManualPathMenuItem = new JMenuItem("Manually set LoL path");
				sysTrayManualPathMenuItem.setMnemonic('m');
				sysTrayManualPathMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e)
					{
						openPathDialog();
					}
				});
				sysTrayOptionsMenu.add(sysTrayManualPathMenuItem);
				
				sysTrayOptionsMenu.addSeparator();
				
				sysTrayEditFavoritesMenuItem = new JMenuItem("Edit favorite Champions");
				sysTrayEditFavoritesMenuItem.setMnemonic('f');
				sysTrayEditFavoritesMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						List<String> newFavs = FavoritesDialog.editFavorites(LoLItems.this, allChampions, favoriteChampions);
						favoriteChampions = newFavs;
						buildChampionsList();
					}
				});
				sysTrayOptionsMenu.add(sysTrayEditFavoritesMenuItem);
				
				//Other buttons
				
				sysTrayDisplayMenuItem = new JMenuItem("Hide window");
				sysTrayDisplayMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e)
					{
						toggleDisplayed();
					}
				});
				popup.add(sysTrayDisplayMenuItem);
				
				popup.addSeparator();
				
				sysTrayExitMenuItem = new JMenuItem("Exit");
				sysTrayExitMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e)
					{
						close();
					}
				});
				popup.add(sysTrayExitMenuItem);
				
				//Create tray icon
				Image trayImage = ResourceLoader.getIcon();
				trayIcon = new TrayIcon(trayImage, titleBase, null);
				trayIcon.setImageAutoSize(true);
				trayIcon.addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if(e.getButton() == MouseEvent.BUTTON1)
						{
							systemTrayPressed = true;
							toggleDisplayed();
						}
					}
					
					@Override
					public void mouseReleased(MouseEvent e)
					{
						if(e.isPopupTrigger())
						{
							int height = (6*25)+(3*5);
							int yLoc = e.getY()-height;
							int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
							if(yLoc > scrHeight)
								yLoc = scrHeight-yLoc;
							popup.setLocation(e.getX(), e.getY()-height);
							popup.setInvoker(popup);
							popup.setVisible(true);
						}
					}
				});
			}
		}
		else
		{
			writeToLog("OS/Java installation does not support the system tray", 2);
		}
	}
	
	//SAVE: initMenuBar
	public void initMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		//File menu
		fileMenu = new JMenu(" File ");
		fileMenu.setMnemonic('f');
		bar.add(fileMenu);
		
		importMenuItem = new JMenuItem("Import...");
		importMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		importMenuItem.setMnemonic('i');
		importMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/open.png"));
		importMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				importItemSets();
			}
		});
		fileMenu.add(importMenuItem);
		
		exportMenuItem = new JMenuItem("Export...");
		exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		exportMenuItem.setMnemonic('e');
		exportMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				exportItemSets();
			}
		});
		fileMenu.add(exportMenuItem);
		
		fileMenu.addSeparator();
		
		copyCodeMenuItem = new JMenuItem("Copy code to clipboard");
		copyCodeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		copyCodeMenuItem.setMnemonic('c');
		copyCodeMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/copy.png"));
		copyCodeMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				exportCode();
			}
		});
		fileMenu.add(copyCodeMenuItem);
		copyCodeMenuItem.setEnabled(false);
		
		getCodeMenuItem = new JMenuItem("Paste code from clipboard");
		getCodeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		getCodeMenuItem.setMnemonic('p');
		getCodeMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/paste.png"));
		getCodeMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				importCode();
			}
		});
		fileMenu.add(getCodeMenuItem);
		getCodeMenuItem.setEnabled(false);
		
		fileMenu.addSeparator();
		
		openFolderMenuItem = new JMenuItem("Open character folder");
		openFolderMenuItem.setMnemonic('f');
		openFolderMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					writeToLog("Opening character dir: "+GamePathUtil.getItemDir());
					Runtime.getRuntime().exec("explorer \""+GamePathUtil.getItemDir()+"\"");
				}
				catch(IOException e)
				{
					writeToLog("Failed to open character folder", LoggingType.ERROR);
					writeStackTrace(e);
					JOptionPane.showMessageDialog(LoLItems.this, getString("dialog.openFolder.failure"), getString("dialog.openFolder.failureTitle"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		fileMenu.add(openFolderMenuItem);
		
		launchMenuItem = new JMenuItem("Launch game");
		launchMenuItem.setMnemonic('l');
		launchMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/game.png"));
		launchMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GamePath lolDir = GamePathUtil.getDir();
				try
				{
					Runtime.getRuntime().exec(lolDir.getPath()+File.separator+"lol.launcher.exe");
				}
				catch(IOException e)
				{
					writeToLog("Failed to start game", LoggingType.ERROR);
					writeStackTrace(e);
					JOptionPane.showMessageDialog(LoLItems.this, getString("dialog.launchGame.failure"), getString("dialog.launchGame.failureTitle"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		fileMenu.add(launchMenuItem);
		
		fileMenu.addSeparator();
		
		exitMenuItem = new JMenuItem("Quit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		exitMenuItem.setMnemonic('q');
		exitMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/quit.png"));
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				close();
			}
		});
		fileMenu.add(exitMenuItem);
		
		//Build menu
		buildMenu = new JMenu(" Build ");
		buildMenu.setMnemonic('b');
		bar.add(buildMenu);
		
		newBuildMenuItem = new JMenuItem("New");
		newBuildMenuItem.setMnemonic('n');
		newBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newBuildMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/new.png"));
		newBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				addItemSet();
			}
		});
		buildMenu.add(newBuildMenuItem);
		newBuildMenuItem.setEnabled(false);
		
		duplicateBuildMenuItem = new JMenuItem("Duplicate");
		duplicateBuildMenuItem.setMnemonic('d');
		duplicateBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				duplicateItemSet();
			}
		});
		buildMenu.add(duplicateBuildMenuItem);
		duplicateBuildMenuItem.setEnabled(false);
		
		copyBuildMenuItem = new JMenuItem("Copy...");
		copyBuildMenuItem.setMnemonic('c');
		copyBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ItemSet itemSet = CopyBuildDialog.open(LoLItems.this, currentGameType);
				if(itemSet != null)
				{
					for(int n = 0; n < 6; n++)
						setItem(n, itemSet.getItem(n));
					changeMade = true;
				}
			}
		});
		buildMenu.add(copyBuildMenuItem);
		copyBuildMenuItem.setEnabled(false);
		
		buildMenu.addSeparator();
		
		saveBuildMenuItem = new JMenuItem("Save");
		saveBuildMenuItem.setMnemonic('s');
		saveBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveBuildMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/save.png"));
		saveBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				save();
			}
		});
		buildMenu.add(saveBuildMenuItem);
		saveBuildMenuItem.setEnabled(false);
				
		buildMenu.addSeparator();
		
		clearBuildMenuItem = new JMenuItem("Clear");
		clearBuildMenuItem.setMnemonic('l');
		clearBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				reset();
			}
		});
		buildMenu.add(clearBuildMenuItem);
		clearBuildMenuItem.setEnabled(false);
		
		resetBuildMenuItem = new JMenuItem("Reset");
		resetBuildMenuItem.setMnemonic('r');
		resetBuildMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		resetBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				refreshChampionItems();
			}
		});
		buildMenu.add(resetBuildMenuItem);
		resetBuildMenuItem.setEnabled(false);
		
		resetDefaultsBuildMenuItem = new JMenuItem("Reset to defaults");
		resetDefaultsBuildMenuItem.setMnemonic('f');
		resetDefaultsBuildMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				setChampionDefaults();
			}
		});
		buildMenu.add(resetDefaultsBuildMenuItem);
		resetDefaultsBuildMenuItem.setEnabled(false);
		
		//Options menu
		optionsMenu = new JMenu(" Options ");
		optionsMenu.setMnemonic('o');
		bar.add(optionsMenu);
		
		updateMenu = new JMenu("Updating");
		optionsMenu.add(updateMenu);
		
		updateStartupMenuItem = new JCheckBoxMenuItem("Check for updates on startup");
		updateStartupMenuItem.setSelected(checkVersion);
		updateStartupMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				checkVersion = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		updateMenu.add(updateStartupMenuItem);
		
		updateMenu.addSeparator();
		
		updateProgramMenuItem = new JMenuItem("Check for program updates");
		updateProgramMenuItem.setMnemonic('p');
		updateProgramMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				UpdateUtil.startUpdater(appKey, version, buildVersion, true);
			}
		});
		updateMenu.add(updateProgramMenuItem);
		
		updateCacheMenuItem = new JMenuItem("Check for cache updates");
		updateCacheMenuItem.setMnemonic('c');
		updateCacheMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				if(UpdateUtil.startCacheUpdater(true))
				{
					loadChampions();
					loadItems();
				}
			}
		});
		updateMenu.add(updateCacheMenuItem);
		
		backupMenu = new JMenu("Backup");
		optionsMenu.add(backupMenu);
		
		backupEnableMenuItem = new JCheckBoxMenuItem("Enable automatic backup");
		backupEnableMenuItem.setSelected(backupEnabled);
		backupEnableMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				backupEnabled = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		backupMenu.add(backupEnableMenuItem);
		
		backupMenu.addSeparator();
		
		backupRestoreMenuItem = new JMenuItem("Restore from backup");
		backupRestoreMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				restoreBackup();
			}
		});
		backupMenu.add(backupRestoreMenuItem);
		
		minimizeTrayMenuItem = new JCheckBoxMenuItem("Minimize to system tray");
		minimizeTrayMenuItem.setSelected(minimizeToTray && systemTrayEnabled);
		minimizeTrayMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				minimizeToTray = evt.getStateChange() == ItemEvent.SELECTED;
			}
		});
		minimizeTrayMenuItem.setEnabled(systemTrayEnabled);
		optionsMenu.add(minimizeTrayMenuItem);
		
		manualPathMenuItem = new JMenuItem("Manually set LoL path");
		manualPathMenuItem.setMnemonic('m');
		manualPathMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				openPathDialog();
			}
		});
		optionsMenu.add(manualPathMenuItem);
		
		optionsMenu.addSeparator();
		
		advancedFiltersMenuItem = new JCheckBoxMenuItem("Use advanced filtering");
		advancedFiltersMenuItem.setMnemonic('a');
		advancedFiltersMenuItem.setSelected(useAdvancedFilters);
		advancedFiltersMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				useAdvancedFilters = evt.getStateChange() == ItemEvent.SELECTED;
				
				if(useAdvancedFilters)
					((CardLayout)itemSelectionPanel.getLayout()).show(itemSelectionPanel, "filters");
				else
					((CardLayout)itemSelectionPanel.getLayout()).show(itemSelectionPanel, "shop");
				
				resetFilters();
				resetShop();
			}
		});
		optionsMenu.add(advancedFiltersMenuItem);
		
		itemDisplayModeMenu = new JMenu("Item display mode");
		optionsMenu.add(itemDisplayModeMenu);
		
		itemModeMenuItems = new HashMap<ItemDisplayMode, JRadioButtonMenuItem>();
		ButtonGroup itemDisplayModeButtonGroup = new ButtonGroup();
		
		for(final ItemDisplayMode mode : ItemDisplayMode.values())
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
			displayModeMenuItem.setSelected(itemDisplayMode == mode);
			displayModeMenuItem.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent evt)
				{
					if(evt.getStateChange() == ItemEvent.SELECTED)
					{
						setItemDisplayMode(mode);
					}
				}
			});
			itemDisplayModeMenu.add(displayModeMenuItem);
			itemModeMenuItems.put(mode, displayModeMenuItem);
			itemDisplayModeButtonGroup.add(displayModeMenuItem);
		}
		
		imageSizeMenu = new JMenu("Item image size");
		imageSizeMenu.setMnemonic('s');
		optionsMenu.add(imageSizeMenu);
		
		ButtonGroup imageSizeButtonGroup = new ButtonGroup();
		imageSizeMenuItems = new JRadioButtonMenuItem[3];
		String[] texts = {"Large", "Medium", "Small"};
		
		for(int n = 0; n < imageSizes.length; n++)
		{
			final int size = imageSizes[n];
			JRadioButtonMenuItem imageSizeMenuItem = new JRadioButtonMenuItem(texts[n]);
			imageSizeMenuItem.setName(texts[n].toLowerCase());
			imageSizeMenuItem.setSelected(imageSize == size);
			imageSizeMenuItem.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent evt)
				{
					if(evt.getStateChange() == ItemEvent.SELECTED)
						setItemImageSize(size);
				}
			});
			imageSizeButtonGroup.add(imageSizeMenuItem);
			imageSizeMenu.add(imageSizeMenuItem);
			imageSizeMenuItems[n] = imageSizeMenuItem;
		}
		
		optionsMenu.addSeparator();
		
		useDefaultsMenuItem = new JCheckBoxMenuItem("Use default items by default");
		useDefaultsMenuItem.setMnemonic('d');
		useDefaultsMenuItem.setSelected(useDefaultItems);
		useDefaultsMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				useDefaultItems = evt.getStateChange() == ItemEvent.SELECTED;
				boolean canReset = true;
				for(ItemSetPanel item : itemPanels)
					if(item != null)
						canReset = false;
				if(canReset)
					setChampionDefaults();
			}
		});
		optionsMenu.add(useDefaultsMenuItem);
		
		editFavoritesMenuItem = new JMenuItem("Edit favorite Champions");
		editFavoritesMenuItem.setMnemonic('f');
		editFavoritesMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				List<String> newFavs = FavoritesDialog.editFavorites(LoLItems.this, allChampions, favoriteChampions);
				favoriteChampions = newFavs;
				buildChampionsList();
			}
		});
		optionsMenu.add(editFavoritesMenuItem);
		
		optionsMenu.addSeparator();
		
		languageMenu = new JMenu("Language");
		optionsMenu.add(languageMenu);
		
		ActionListener languageListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				String lang = evt.getActionCommand();
				if(!lang.equals(currentLanguage))
				{
					currentLanguage = lang;
					LocaleDatabase.setLocale(currentLanguage);
					reloadText();
				}
			}
		};
		
		ButtonGroup languageButtonGroup = new ButtonGroup();
		
		for(String langKey : languages)
		{
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(LocaleDatabase.getLocaleTitle(langKey)+" ("+langKey+")");
			if(langKey.equals(currentLanguage))
				item.setSelected(true);
			item.setIcon(ResourceLoader.getImageIcon("menu_icons/flags/"+langKey+".png"));
			item.setPreferredSize(new Dimension(item.getPreferredSize().width, 22));
			item.setActionCommand(langKey);
			item.addActionListener(languageListener);
			languageMenu.add(item);
			languageButtonGroup.add(item);
		}
		
		backgroundImageMenu = new JMenu("Background image");
		optionsMenu.add(backgroundImageMenu);
		
		ActionListener backgroundListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				String image = evt.getActionCommand();
				setBackground(image);
			}
		};
		
		ButtonGroup backgroundButtonGroup = new ButtonGroup();
		
		backgroundNoImage = new JRadioButtonMenuItem("No image");
		backgroundNoImage.addActionListener(backgroundListener);
		backgroundNoImage.setActionCommand("null");
		backgroundNoImage.setSelected(currentBackground.equals("null"));
		backgroundImageMenu.add(backgroundNoImage);
		backgroundButtonGroup.add(backgroundNoImage);
		
		backgroundSolidsMenu = new JMenu("Solid colors");
		backgroundImageMenu.add(backgroundSolidsMenu);
		
		colorMenuItems = new HashMap<String, JRadioButtonMenuItem>();
		for(String name : backgroundColors.keySet())
		{
			int color = Integer.parseInt(backgroundColors.get(name));
			JRadioButtonMenuItem bg = new JRadioButtonMenuItem(name);
			bg.addActionListener(backgroundListener);
			bg.setActionCommand("color-"+color);
			
			BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.setColor(new Color(color));
			g.fillRect(0, 0, 20, 20);
			g.dispose();
			bg.setIcon(new ImageIcon(image));
			
			if(("color-"+color).equals(currentBackground))
				bg.setSelected(true);
			backgroundSolidsMenu.add(bg);
			colorMenuItems.put(name, bg);
			backgroundButtonGroup.add(bg);
		}
		
		backgroundGradientsMenu = new JMenu("Gradients");
		backgroundImageMenu.add(backgroundGradientsMenu);
		
		for(String name : backgroundGradients.keySet())
		{
			String gradientColors = backgroundGradients.get(name);
			int index;
			int value1 = Integer.parseInt(gradientColors.substring(0, index = gradientColors.indexOf('-')));
			int value2 = Integer.parseInt(gradientColors.substring(index+1, index = gradientColors.indexOf('-', index+1)));
			int value3 = Integer.parseInt(gradientColors.substring(index+1));
			JRadioButtonMenuItem bg = new JRadioButtonMenuItem(name);
			bg.addActionListener(backgroundListener);
			bg.setActionCommand("gradient-"+gradientColors);
			
			BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setPaint(new LinearGradientPaint(0, 0, 20, 20, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{new Color(value1), new Color(value2), new Color(value3)}));
			g.fillRect(0, 0, 20, 20);
			g.dispose();
			bg.setIcon(new ImageIcon(image));
			
			if(("gradient-"+gradientColors).equals(currentBackground))
				bg.setSelected(true);
			backgroundGradientsMenu.add(bg);
			backgroundButtonGroup.add(bg);
		}
		
		backgroundImageMenu.addSeparator();
		
		loadBackgrounds();
		
		for(String key : defaultBackgrounds.keySet())
		{
			String imageFile = defaultBackgrounds.get(key);
			JRadioButtonMenuItem bg = new JRadioButtonMenuItem(key);
			bg.addActionListener(backgroundListener);
			bg.setActionCommand(imageFile);
			
			Image image = ResourceLoader.getImage("backgrounds/"+imageFile);
			if(image != null)
				bg.setIcon(new ImageIcon(image.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
			
			if(imageFile.equals(currentBackground))
				bg.setSelected(true);
			backgroundImageMenu.add(bg);
			backgroundButtonGroup.add(bg);
		}
		
		if(customBackgrounds.size() > 0)
		{
			backgroundImageMenu.addSeparator();
			for(String key : customBackgrounds)
			{
				JRadioButtonMenuItem bg = new JRadioButtonMenuItem(key);
				bg.addActionListener(backgroundListener);
				bg.setActionCommand(key);
				
				Image image = new ImageIcon(key).getImage();
				if(image != null)
					bg.setIcon(new ImageIcon(image.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
				
				if(key.equals(currentBackground))
					bg.setSelected(true);
				backgroundImageMenu.add(bg);
				backgroundButtonGroup.add(bg);
			}
		}
		
		tooltipsMenu = new JMenu("Tooltips");
		tooltipsMenu.setMnemonic('t');
		optionsMenu.add(tooltipsMenu);
		
		enableTooltipsMenuItem = new JCheckBoxMenuItem("Enable tooltips");
		enableTooltipsMenuItem.setMnemonic('e');
		enableTooltipsMenuItem.setSelected(fixedTooltipWidth);
		enableTooltipsMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				setTooltipsEnabled(evt.getStateChange() == ItemEvent.SELECTED);
			}
		});
		tooltipsMenu.add(enableTooltipsMenuItem);
		
		tooltipsMenu.addSeparator();
		
		useFixedWidthMenuItem = new JCheckBoxMenuItem("Use fixed tooltip width");
		useFixedWidthMenuItem.setMnemonic('f');
		useFixedWidthMenuItem.setSelected(fixedTooltipWidth);
		useFixedWidthMenuItem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				setTooltipsFixed(evt.getStateChange() == ItemEvent.SELECTED);
			}
		});
		tooltipsMenu.add(useFixedWidthMenuItem);
		
		//Help menu
		helpMenu = new JMenu(" Help ");
		helpMenu.setMnemonic('h');
		bar.add(helpMenu);
		
		helpMenuItem = new JMenuItem("Help...");
		helpMenuItem.setMnemonic('h');
		helpMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/help.png"));
		helpMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://enigmablade.net/eric/help.html");
			}
		});
		helpMenu.add(helpMenuItem);
		
		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');
		helpMenu.add(toolsMenu);
		
		debugRestartMenuItem = new JMenuItem("Restart in debug mode");
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
		toolsMenu.add(debugRestartMenuItem);
		
		/*JMenuItem deleteLockMenuItem = new JMenuItem("Delete IO lock");
		deleteLockMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				writeToLog("Deleting IO lock...");
				deleteLock();
			}
		});
		toolsMenu.add(deleteLockMenuItem);*/
		
		helpMenu.addSeparator();
		
		aboutMenuItem = new JMenuItem("About...");
		aboutMenuItem.setMnemonic('a');
		aboutMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				AboutDialog.openDialog(LoLItems.this, version);
			}
		});
		helpMenu.add(aboutMenuItem);
		
		changelogMenuItem = new JMenuItem("Changelog...");
		changelogMenuItem.setMnemonic('c');
		changelogMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ChangelogDialog.openDialog(LoLItems.this);
			}
		});
		helpMenu.add(changelogMenuItem);
		
		donateMenuItem = new JMenuItem("Donate...");
		donateMenuItem.setMnemonic('d');
		donateMenuItem.setIcon(ResourceLoader.getImageIcon("menu_icons/donate.png"));
		donateMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				GUIUtil.openURL("http://enigmablade.net/eric/donate.html");
			}
		});
		helpMenu.add(donateMenuItem);
		
		bar.add(Box.createHorizontalGlue());
		
		versionLabel = new JLabel("Version");
		versionLabel.setForeground(Color.GRAY);
		bar.add(versionLabel);
		JLabel versionLabel2 = new JLabel(" "+version+" "+versionAdd+" ");
		versionLabel2.setForeground(Color.GRAY);
		bar.add(versionLabel2);
	}
	
	//SAVE: initComponents
	public void initComponents()
	{
		setTitle(titleBase);
		setIconImage(ResourceLoader.getIcon());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				close();
			}
			
			@Override
			public void windowIconified(WindowEvent evt)
			{
				if(systemTrayEnabled && minimizeToTray)
				{
					try
					{
						systemTray.add(trayIcon);
						setDisplayed(false);
					}
					catch(AWTException e)
					{
						writeToLog("Failed to display system tray");
						writeStackTrace(e);
					}
				}
			}
			
			@Override
			public void windowDeiconified(WindowEvent evt)
			{
				if(systemTrayEnabled && minimizeToTray)
				{
					systemTray.remove(trayIcon);
				}
				if(systemTrayPressed)
				{
					setDisplayed(true);
					systemTrayPressed = false;
				}
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
		
		setPreferredSize(new Dimension(700, 570));
		setMinimumSize(new Dimension(660, 520));
		
		itemFilterModel = new ItemFilterModel();
		itemFilterModel.addRelatedItemFilterChangedListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent evt)
			{
				Object source = evt.getSource();
				if(source == ItemFilterModel.ITEM_CLEARED)
				{
					if(useAdvancedFilters)
					{
						resetFilters();
						((CardLayout)itemSelectionPanel.getLayout()).show(itemSelectionPanel, "filters");
					}
					return;
				}
				((CardLayout)itemSelectionPanel.getLayout()).show(itemSelectionPanel, "shop");
				itemFilterModel.clearItemPropertyFilters();
				itemFilterModel.addItemPropertyFilter(currentGameType.getItemProperty());
				updateChampionItemFilters(null);
				Item item = (Item)evt.getSource();
				String text = item.getName();
				
				shopHistoryPanel.reset();
				shopHistoryPanel.pushCard(text, text);
				shopCardsPanel.showItemPanel(item);
			}
		});
		
		backgroundPanel = new BackgroundPanel();
		backgroundPanel.setBackground(currentBackground.equals("null") ? null : currentBackground);
		backgroundPanel.setBorder(new EmptyBorder(3, 2, 3, 3));
		backgroundPanel.setLayout(new BorderLayout(3, 0));
		setContentPane(backgroundPanel);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setOpaque(false);
		sidePanel.setBorder(null);
		backgroundPanel.add(sidePanel, BorderLayout.WEST);
		sidePanel.setLayout(new BorderLayout(0, 3));
		
		TranslucentPanel championPanel = new TranslucentPanel(175);
		translucentPanels.add(championPanel);
		championPanel.setBorder(new CompoundBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null), new EmptyBorder(2, 3, 2, 3)));
		championPanel.setPreferredSize(new Dimension(225, 98));
		sidePanel.add(championPanel, BorderLayout.NORTH);
		GridBagLayout gbl_championPanel = new GridBagLayout();
		gbl_championPanel.columnWidths = new int[]{88, 0, 0, 0};
		gbl_championPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_championPanel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_championPanel.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		championPanel.setLayout(gbl_championPanel);
		
		championImagePanel = new ChampionImagePanel(null);
		GridBagConstraints gbc_championImagePanel = new GridBagConstraints();
		gbc_championImagePanel.insets = new Insets(0, 0, 0, 4);
		gbc_championImagePanel.gridheight = 3;
		gbc_championImagePanel.gridx = 0;
		gbc_championImagePanel.gridy = 0;
		championPanel.add(championImagePanel, gbc_championImagePanel);
		championImagePanel.setBorder(new LineBorder(new Color(128, 128, 128), 2));
		
		JPanel championInfoPanel = new JPanel();
		championInfoPanel.setOpaque(false);
		GridBagConstraints gbc_championInfoPanel = new GridBagConstraints();
		gbc_championInfoPanel.insets = new Insets(0, 0, 2, 0);
		gbc_championInfoPanel.gridwidth = 2;
		gbc_championInfoPanel.fill = GridBagConstraints.BOTH;
		gbc_championInfoPanel.gridx = 1;
		gbc_championInfoPanel.gridy = 0;
		championPanel.add(championInfoPanel, gbc_championInfoPanel);
		GridBagLayout gbl_championInfoPanel = new GridBagLayout();
		gbl_championInfoPanel.columnWidths = new int[]{31, 0};
		gbl_championInfoPanel.rowHeights = new int[]{0, 19, 0, 0, 0};
		gbl_championInfoPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_championInfoPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		championInfoPanel.setLayout(gbl_championInfoPanel);
		
		championNameLabel = new JLabel();
		GridBagConstraints gbc_championNameLabel = new GridBagConstraints();
		gbc_championNameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_championNameLabel.gridx = 0;
		gbc_championNameLabel.gridy = 1;
		championInfoPanel.add(championNameLabel, gbc_championNameLabel);
		championNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		championNameLabel.setFont(championNameLabel.getFont().deriveFont(championNameLabel.getFont().getStyle() | Font.BOLD, championNameLabel.getFont().getSize() + 4f));
		
		championTitleLabel = new JLabel("");
		championTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_championTitleLabel = new GridBagConstraints();
		gbc_championTitleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_championTitleLabel.gridx = 0;
		gbc_championTitleLabel.gridy = 2;
		championInfoPanel.add(championTitleLabel, gbc_championTitleLabel);
				
		championComboBox = new ChampionComboBox(false);
		GridBagConstraints gbc_championComboBox = new GridBagConstraints();
		gbc_championComboBox.insets = new Insets(0, 0, 2, 0);
		gbc_championComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_championComboBox.gridwidth = 2;
		gbc_championComboBox.gridx = 1;
		gbc_championComboBox.gridy = 1;
		championPanel.add(championComboBox, gbc_championComboBox);
		championComboBox.setMaximumRowCount(18);
		championComboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					String champName = (String)e.getItem();
					String champKey = ChampionDatabase.getChampionKey(champName);
					setChampion(ChampionDatabase.getChampion(champKey));
				}
			}
		});
		championComboBox.setOpaque(false);
		
		gameModeComboBox = new GameModeComboBox();
		DefaultComboBoxModel<GameMode> model = new DefaultComboBoxModel<GameMode>();
		for(GameMode mode : GameMode.values())
			model.addElement(mode);
		gameModeComboBox.setModel(model);
		gameModeComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED && !disableComboBoxUpdate.get())
				{
					GameMode mode = (GameMode)e.getItem();
					setGameMode(mode);
				}
			}
		});
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		championPanel.add(gameModeComboBox, gbc_comboBox);
		
		itemSelectionPanel = new JPanel();
		itemSelectionPanel.setOpaque(false);
		sidePanel.add(itemSelectionPanel, BorderLayout.CENTER);
		itemSelectionPanel.setLayout(new CardLayout(0, 0));
		
		writeToLog("Creating stats filter panel", 1);
		
		filtersPanel = new TranslucentPanel(175);
		translucentPanels.add(filtersPanel);
		itemSelectionPanel.add(filtersPanel, "filters");
		filtersPanel.setBorder(new TitledBorder(null, "Item Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filtersPanel.setLayout(new BorderLayout(0, 4));
		
		itemFilterTextField = new FocusTextField("Filter items by name");
		itemFilterTextField.setColumns(10);
		filtersPanel.add(itemFilterTextField, BorderLayout.NORTH);
		itemFilterTextField.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void removeUpdate(DocumentEvent evt)
			{
				updateFilterText(evt, false);
			}
			
			@Override
			public void insertUpdate(DocumentEvent evt)
			{
				updateFilterText(evt, true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent evt)
			{
				updateFilterText(evt, false);
			}
			
			private void updateFilterText(DocumentEvent evt, boolean added)
			{
				Document doc = evt.getDocument();
				try
				{
					String value = doc.getText(0, doc.getLength());
					itemFilterModel.setTextFilter(value);
				}
				catch(BadLocationException e)
				{
					writeToLog("Error when updating item text filter", LoggingType.ERROR);
					writeStackTrace(e);
				}
			}
		});
		
		ItemListener filter = new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				String com = ((JCheckBox)e.getSource()).getActionCommand();
				ItemProperty newFilter = ItemProperty.commandToFilter(com);
				
				if(e.getStateChange() == ItemEvent.SELECTED)
					itemFilterModel.addItemPropertyFilter(newFilter);
				else
					itemFilterModel.removeItemPropertyFilter(newFilter);
			}
		};
		
		JPanel checkFiltersPanel = new JPanel();
		checkFiltersPanel.setOpaque(false);
		filtersPanel.add(checkFiltersPanel, BorderLayout.CENTER);
		GridBagLayout gbl_checkFiltersPanel = new GridBagLayout();
		gbl_checkFiltersPanel.columnWidths = new int[]{0, 0};
		gbl_checkFiltersPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_checkFiltersPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_checkFiltersPanel.rowWeights = new double[]{0.0, 3.0, 0.0, 8.0, Double.MIN_VALUE};
		checkFiltersPanel.setLayout(gbl_checkFiltersPanel);
		
		categoriesLabel = new JLabel("Categories");
		categoriesLabel.setFont(categoriesLabel.getFont().deriveFont(categoriesLabel.getFont().getSize() + 1f));
		GridBagConstraints gbc_categoriesLabel = new GridBagConstraints();
		gbc_categoriesLabel.insets = new Insets(0, 0, 2, 0);
		gbc_categoriesLabel.gridx = 0;
		gbc_categoriesLabel.gridy = 0;
		checkFiltersPanel.add(categoriesLabel, gbc_categoriesLabel);
		
		JPanel categoriesPanel = new JPanel();
		categoriesPanel.setBorder(new LineBorder(Color.GRAY));
		categoriesPanel.setOpaque(false);
		GridBagConstraints gbc_categoriesPanel = new GridBagConstraints();
		gbc_categoriesPanel.fill = GridBagConstraints.BOTH;
		gbc_categoriesPanel.insets = new Insets(0, 0, 6, 0);
		gbc_categoriesPanel.gridx = 0;
		gbc_categoriesPanel.gridy = 1;
		checkFiltersPanel.add(categoriesPanel, gbc_categoriesPanel);
		categoriesPanel.setLayout(new GridLayout(3, 2, 0, 0));
		
		attackCheckBox = ComponentFactory.createFilterCheckBox("Attack", "attack", checkBoxAlpha1, filter);
		categoriesPanel.add(attackCheckBox);
		magicCheckBox = ComponentFactory.createFilterCheckBox("Magic", "magic", checkBoxAlpha1, filter);
		categoriesPanel.add(magicCheckBox);
		defenseCheckBox = ComponentFactory.createFilterCheckBox("Defense", "defense", checkBoxAlpha2, filter);
		categoriesPanel.add(defenseCheckBox);
		movementCheckBox = ComponentFactory.createFilterCheckBox("Movement", "movement", checkBoxAlpha2, filter);
		categoriesPanel.add(movementCheckBox);
		consumableCheckBox = ComponentFactory.createFilterCheckBox("Consumable", "consumable", checkBoxAlpha1, filter);
		categoriesPanel.add(consumableCheckBox);
		
		TranslucentPanel emptyPanel = new TranslucentPanel(checkBoxAlpha1);
		emptyPanel.setOpaque(false);
		emptyPanel.setBackground(new Color(150, 150, 150));
		categoriesPanel.add(emptyPanel);
		
		statsLabel = new JLabel("Stats");
		statsLabel.setFont(statsLabel.getFont().deriveFont(statsLabel.getFont().getSize() + 1f));
		GridBagConstraints gbc_statsLabel = new GridBagConstraints();
		gbc_statsLabel.insets = new Insets(0, 0, 2, 0);
		gbc_statsLabel.gridx = 0;
		gbc_statsLabel.gridy = 2;
		checkFiltersPanel.add(statsLabel, gbc_statsLabel);
		
		JPanel statsPanel = new JPanel();
		statsPanel.setBorder(new LineBorder(Color.GRAY));
		statsPanel.setOpaque(false);
		GridBagConstraints gbc_statsPanel = new GridBagConstraints();
		gbc_statsPanel.fill = GridBagConstraints.BOTH;
		gbc_statsPanel.gridx = 0;
		gbc_statsPanel.gridy = 3;
		checkFiltersPanel.add(statsPanel, gbc_statsPanel);
		statsPanel.setLayout(new GridLayout(8, 2, 0, 0));
		
		attackDamageCheckBox = ComponentFactory.createFilterCheckBox("Attack Damage", "ad", checkBoxAlpha1, filter);
		statsPanel.add(attackDamageCheckBox);
		abilityPowerCheckBox = ComponentFactory.createFilterCheckBox("Ability Power", "ap", checkBoxAlpha1, filter);
		statsPanel.add(abilityPowerCheckBox);
		attackSpeedCheckBox = ComponentFactory.createFilterCheckBox("Attack Speed", "as", checkBoxAlpha2, filter);
		statsPanel.add(attackSpeedCheckBox);
		cooldownCheckBox = ComponentFactory.createFilterCheckBox("Cooldowns", "cd", checkBoxAlpha2, filter);
		statsPanel.add(cooldownCheckBox);
		armorCheckBox = ComponentFactory.createFilterCheckBox("Armor", "ar", checkBoxAlpha1, filter);
		statsPanel.add(armorCheckBox);
		magicResCheckBox = ComponentFactory.createFilterCheckBox("Magic Res", "mr", checkBoxAlpha1, filter);
		statsPanel.add(magicResCheckBox);
		healthCheckBox = ComponentFactory.createFilterCheckBox("Health", "h", checkBoxAlpha2, filter);
		statsPanel.add(healthCheckBox);
		healthRegenCheckBox = ComponentFactory.createFilterCheckBox("Health Regen", "hregen", checkBoxAlpha2, filter);
		statsPanel.add(healthRegenCheckBox);
		manaCheckBox = ComponentFactory.createFilterCheckBox("Mana", "m", checkBoxAlpha1, filter);
		statsPanel.add(manaCheckBox);
		manaRegenCheckBox = ComponentFactory.createFilterCheckBox("Mana Regen", "mregen", checkBoxAlpha1, filter);
		statsPanel.add(manaRegenCheckBox);
		armorPenCheckBox = ComponentFactory.createFilterCheckBox("Armor Pen", "arp", checkBoxAlpha2, filter);
		statsPanel.add(armorPenCheckBox);
		magicPenCheckBox = ComponentFactory.createFilterCheckBox("Magic Pen", "mrp", checkBoxAlpha2, filter);
		statsPanel.add(magicPenCheckBox);
		lifestealCheckBox = ComponentFactory.createFilterCheckBox("Lifesteal", "ls", checkBoxAlpha1, filter);
		statsPanel.add(lifestealCheckBox);
		spellVampCheckBox = ComponentFactory.createFilterCheckBox("Spell Vamp", "sv", checkBoxAlpha1, filter);
		statsPanel.add(spellVampCheckBox);
		criticalStrikeCheckBox = ComponentFactory.createFilterCheckBox("Critical Chance", "crit", checkBoxAlpha2, filter);
		statsPanel.add(criticalStrikeCheckBox);
		tenacityCheckBox = ComponentFactory.createFilterCheckBox("Tenacity", "ten", checkBoxAlpha2, filter);
		statsPanel.add(tenacityCheckBox);
		
		writeToLog("Creating shop panels", 1);
		
		shopPanel = new TranslucentPanel(175);
		translucentPanels.add(shopPanel);
		shopPanel.setBorder(new TitledBorder(null, "Shop", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		itemSelectionPanel.add(shopPanel, "shop");
		shopPanel.setLayout(new BorderLayout(0, 0));
		
		ActionListener shopButtonListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				String code = evt.getActionCommand();
				JButton source = (JButton)evt.getSource();
				updateShopPanel(code, source.getText());
			}
		};
		
		shopHistoryPanel = new HistoryPanel(shopButtonListener);
		shopHistoryPanel.setOpaque(false);
		shopPanel.add(shopHistoryPanel, BorderLayout.NORTH);
		shopHistoryPanel.pushCard("main", "Home");
		
		shopCardsPanel = new ShopPanel(125);
		shopCardsPanel.setBorder(new LineBorder(Color.GRAY));
		shopCardsPanel.setBackground(new Color(150, 150, 150));
		shopPanel.add(shopCardsPanel);
		shopCardsPanel.setLayout(new CardLayout(0, 0));
		
		JPanel shopPanelMain = new JPanel();
		shopCardsPanel.add(shopPanelMain, "main");
		shopPanelMain.setOpaque(false);
		shopPanelMain.setLayout(new BoxLayout(shopPanelMain, BoxLayout.Y_AXIS));
		
		Component glue1 = Box.createVerticalGlue();
		shopPanelMain.add(glue1);
		
		defenseShopButton = ComponentFactory.createShopButton("Defense", "defense", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMain.add(defenseShopButton);
		
		Component glue2 = Box.createVerticalGlue();
		shopPanelMain.add(glue2);
		
		attackShopButton = ComponentFactory.createShopButton("Attack", "attack", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMain.add(attackShopButton);
		
		Component glue3 = Box.createVerticalGlue();
		shopPanelMain.add(glue3);
		
		magicShopButton = ComponentFactory.createShopButton("Magic", "magic", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMain.add(magicShopButton);
		
		Component glue4 = Box.createVerticalGlue();
		shopPanelMain.add(glue4);
		
		movementShopButton = ComponentFactory.createShopButton("Movement", "movement", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMain.add(movementShopButton);
		
		Component glue5 = Box.createVerticalGlue();
		shopPanelMain.add(glue5);
		
		consumableShopButton = ComponentFactory.createShopButton("Consumable", "consumable", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMain.add(consumableShopButton);
		
		Component glue6 = Box.createVerticalGlue();
		shopPanelMain.add(glue6);
		
		JPanel shopPanelAttack = new JPanel();
		shopCardsPanel.add(shopPanelAttack, "attack");
		shopPanelAttack.setOpaque(false);
		shopPanelAttack.setLayout(new BoxLayout(shopPanelAttack, BoxLayout.Y_AXIS));
		
		Component glue7 = Box.createVerticalGlue();
		shopPanelAttack.add(glue7);
		
		attackDamageShopButton = ComponentFactory.createShopButton("Attack Damage", "ad", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelAttack.add(attackDamageShopButton);
		
		Component glue8 = Box.createVerticalGlue();
		shopPanelAttack.add(glue8);
		
		attackSpeedShopButton = ComponentFactory.createShopButton("Attack Speed", "as", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelAttack.add(attackSpeedShopButton);
		
		Component glue9 = Box.createVerticalGlue();
		shopPanelAttack.add(glue9);
		
		criticalStrikeShopButton = ComponentFactory.createShopButton("Critical Strike", "crit", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelAttack.add(criticalStrikeShopButton);
		
		Component glue10 = Box.createVerticalGlue();
		shopPanelAttack.add(glue10);
		
		lifestealShopButton = ComponentFactory.createShopButton("Lifesteal", "ls", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelAttack.add(lifestealShopButton);
		
		Component glue11 = Box.createVerticalGlue();
		shopPanelAttack.add(glue11);
		
		backAttackShopButton = ComponentFactory.createShopBackButton("attack");
		backAttackShopButton.addActionListener(shopButtonListener);
		shopPanelAttack.add(backAttackShopButton);
		
		JPanel shopPanelMagic = new JPanel();
		shopCardsPanel.add(shopPanelMagic, "magic");
		shopPanelMagic.setOpaque(false);
		shopPanelMagic.setLayout(new BoxLayout(shopPanelMagic, BoxLayout.Y_AXIS));
		
		Component glue12 = Box.createVerticalGlue();
		shopPanelMagic.add(glue12);
		
		abilityPowerShopButton = ComponentFactory.createShopButton("Ability Power", "ap", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMagic.add(abilityPowerShopButton);
		
		Component glue13 = Box.createVerticalGlue();
		shopPanelMagic.add(glue13);
		
		cooldownShopButton = ComponentFactory.createShopButton("Cooldown Reduction", "cd", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMagic.add(cooldownShopButton);
		
		Component glue14 = Box.createVerticalGlue();
		shopPanelMagic.add(glue14);
		
		manaShopButton = ComponentFactory.createShopButton("Mana", "m", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMagic.add(manaShopButton);
		
		Component glue15 = Box.createVerticalGlue();
		shopPanelMagic.add(glue15);
		
		manaRegenShopButton = ComponentFactory.createShopButton("Mana Regeneration", "mregen", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelMagic.add(manaRegenShopButton);
		
		Component glue16 = Box.createVerticalGlue();
		shopPanelMagic.add(glue16);
		
		backMagicShopButton = ComponentFactory.createShopBackButton("magic");
		backMagicShopButton.addActionListener(shopButtonListener);
		shopPanelMagic.add(backMagicShopButton);
		
		JPanel shopPanelDefense = new JPanel();
		shopPanelDefense.setOpaque(false);
		shopCardsPanel.add(shopPanelDefense, "defense");
		shopPanelDefense.setLayout(new BoxLayout(shopPanelDefense, BoxLayout.Y_AXIS));
		
		Component glue17 = Box.createVerticalGlue();
		shopPanelDefense.add(glue17);
		
		healthShopButton = ComponentFactory.createShopButton("Health", "h", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelDefense.add(healthShopButton);
		
		Component glue18 = Box.createVerticalGlue();
		shopPanelDefense.add(glue18);
		
		magicResShopButton = ComponentFactory.createShopButton("Magic Resistance", "mr", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelDefense.add(magicResShopButton);
		
		Component glue19 = Box.createVerticalGlue();
		shopPanelDefense.add(glue19);
		
		armorShopButton = ComponentFactory.createShopButton("Armor", "ar", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelDefense.add(armorShopButton);
		
		Component glue20 = Box.createVerticalGlue();
		shopPanelDefense.add(glue20);
		
		healthRegenShopButton = ComponentFactory.createShopButton("Health Regeneration", "hregen", shopButtonWidth, shopButtonHeight, shopButtonListener);
		shopPanelDefense.add(healthRegenShopButton);
		
		Component glue21 = Box.createVerticalGlue();
		shopPanelDefense.add(glue21);
		
		backDefenseShopButton = ComponentFactory.createShopBackButton("defense");
		backDefenseShopButton.addActionListener(shopButtonListener);
		shopPanelDefense.add(backDefenseShopButton);
		
		JPanel shopPanelMovement = new JPanel(){
			private Image img;
			private int imgXOff, imgYOff;
			
			@Override
			public void paintComponent(Graphics g)
			{
				if(img == null)
				{
					img = ResourceLoader.getImage("snoo.png");
					imgXOff = img.getWidth(null)/2;
					imgYOff = img.getHeight(null)+backDefenseShopButton.getSize().height-2;
				}
				super.paintComponent(g);
				g.drawImage(img, (getWidth()/2)-imgXOff, getHeight()-imgYOff, null);
			}
		};
		shopCardsPanel.add(shopPanelMovement, "movement");
		shopPanelMovement.setOpaque(false);
		shopPanelMovement.setLayout(new BoxLayout(shopPanelMovement, BoxLayout.Y_AXIS));
		
		Component glue23 = Box.createVerticalGlue();
		shopPanelMovement.add(glue23);
		
		JLabel movementShopLabel = ComponentFactory.createShopLabel("Movement");
		shopPanelMovement.add(movementShopLabel);
		
		Component glue24 = Box.createVerticalGlue();
		shopPanelMovement.add(glue24);
		
		backMovementButton = ComponentFactory.createShopBackButton("movement");
		backMovementButton.addActionListener(shopButtonListener);
		shopPanelMovement.add(backMovementButton);
		
		JPanel shopPanelConsumable = new JPanel();
		shopCardsPanel.add(shopPanelConsumable, "consumable");
		shopPanelConsumable.setOpaque(false);
		shopPanelConsumable.setLayout(new BoxLayout(shopPanelConsumable, BoxLayout.Y_AXIS));
		
		Component glue25 = Box.createVerticalGlue();
		shopPanelConsumable.add(glue25);
		
		JLabel consumablesShopLabel = ComponentFactory.createShopLabel("Consumables");
		shopPanelConsumable.add(consumablesShopLabel);
		
		Component glue26 = Box.createVerticalGlue();
		shopPanelConsumable.add(glue26);
		
		backConsumablesButton = ComponentFactory.createShopBackButton("consumables");
		backConsumablesButton.addActionListener(shopButtonListener);
		shopPanelConsumable.add(backConsumablesButton);
		
		JPanel shopPanelAttackDamage = new JPanel();
		shopPanelAttackDamage.setOpaque(false);
		shopCardsPanel.add(shopPanelAttackDamage, "ad");
		shopPanelAttackDamage.setLayout(new BoxLayout(shopPanelAttackDamage, BoxLayout.Y_AXIS));
		
		Component glue27 = Box.createVerticalGlue();
		shopPanelAttackDamage.add(glue27);
		
		JLabel attackDamageShopLabel = ComponentFactory.createShopLabel("Attack Damage");
		shopPanelAttackDamage.add(attackDamageShopLabel);
		
		Component glue28 = Box.createVerticalGlue();
		shopPanelAttackDamage.add(glue28);
		
		backAttackDamageShopButton = ComponentFactory.createShopBackButton("ad");
		backAttackDamageShopButton.addActionListener(shopButtonListener);
		shopPanelAttackDamage.add(backAttackDamageShopButton);
		
		JPanel shopPanelAttackSpeed = new JPanel();
		shopPanelAttackSpeed.setOpaque(false);
		shopCardsPanel.add(shopPanelAttackSpeed, "as");
		shopPanelAttackSpeed.setLayout(new BoxLayout(shopPanelAttackSpeed, BoxLayout.Y_AXIS));
		
		Component glue29 = Box.createVerticalGlue();
		shopPanelAttackSpeed.add(glue29);
		
		JLabel attackSpeedShopLabel = ComponentFactory.createShopLabel("Attack Speed");
		shopPanelAttackSpeed.add(attackSpeedShopLabel);
		
		Component glue30 = Box.createVerticalGlue();
		shopPanelAttackSpeed.add(glue30);
		
		backAttackSpeedShopButton = ComponentFactory.createShopBackButton("as");
		backAttackSpeedShopButton.addActionListener(shopButtonListener);
		shopPanelAttackSpeed.add(backAttackSpeedShopButton);
		
		JPanel shopPanelCriticalStrike = new JPanel();
		shopPanelCriticalStrike.setOpaque(false);
		shopCardsPanel.add(shopPanelCriticalStrike, "crit");
		shopPanelCriticalStrike.setLayout(new BoxLayout(shopPanelCriticalStrike, BoxLayout.Y_AXIS));
		
		Component glue31 = Box.createVerticalGlue();
		shopPanelCriticalStrike.add(glue31);
		
		JLabel criticalStrikeShopLabel = ComponentFactory.createShopLabel("Critical Strike");
		shopPanelCriticalStrike.add(criticalStrikeShopLabel);
		
		Component glue32 = Box.createVerticalGlue();
		shopPanelCriticalStrike.add(glue32);
		
		backCriticalStrikeShopButton = ComponentFactory.createShopBackButton("crit");
		backCriticalStrikeShopButton.addActionListener(shopButtonListener);
		shopPanelCriticalStrike.add(backCriticalStrikeShopButton);
		
		JPanel shopPanelLifesteal = new JPanel();
		shopPanelLifesteal.setOpaque(false);
		shopCardsPanel.add(shopPanelLifesteal, "ls");
		shopPanelLifesteal.setLayout(new BoxLayout(shopPanelLifesteal, BoxLayout.Y_AXIS));
		
		Component glue33 = Box.createVerticalGlue();
		shopPanelLifesteal.add(glue33);
		
		JLabel lifestealShopLabel = ComponentFactory.createShopLabel("Lifesteal");
		shopPanelLifesteal.add(lifestealShopLabel);
		
		Component glue34 = Box.createVerticalGlue();
		shopPanelLifesteal.add(glue34);
		
		backLifestealShopButton = ComponentFactory.createShopBackButton("ls");
		backLifestealShopButton.addActionListener(shopButtonListener);
		shopPanelLifesteal.add(backLifestealShopButton);
		
		JPanel shopPanelAbilityPower = new JPanel();
		shopPanelAbilityPower.setOpaque(false);
		shopCardsPanel.add(shopPanelAbilityPower, "ap");
		shopPanelAbilityPower.setLayout(new BoxLayout(shopPanelAbilityPower, BoxLayout.Y_AXIS));
		
		Component glue35 = Box.createVerticalGlue();
		shopPanelAbilityPower.add(glue35);
		
		JLabel abilityPowerShopLabel = ComponentFactory.createShopLabel("Ability Power");
		shopPanelAbilityPower.add(abilityPowerShopLabel);
		
		Component glue36 = Box.createVerticalGlue();
		shopPanelAbilityPower.add(glue36);
		
		backAbilityPowerShopButton = ComponentFactory.createShopBackButton("ap");
		backAbilityPowerShopButton.addActionListener(shopButtonListener);
		shopPanelAbilityPower.add(backAbilityPowerShopButton);
		
		JPanel shopPanelCooldown = new JPanel();
		shopPanelCooldown.setOpaque(false);
		shopCardsPanel.add(shopPanelCooldown, "cd");
		shopPanelCooldown.setLayout(new BoxLayout(shopPanelCooldown, BoxLayout.Y_AXIS));
		
		Component glue37 = Box.createVerticalGlue();
		shopPanelCooldown.add(glue37);
		
		JLabel cooldownShopLabel = ComponentFactory.createShopLabel("Cooldown Reduction");
		shopPanelCooldown.add(cooldownShopLabel);
		
		Component glue38 = Box.createVerticalGlue();
		shopPanelCooldown.add(glue38);
		
		backCooldownShopButton = ComponentFactory.createShopBackButton("cd");
		backCooldownShopButton.addActionListener(shopButtonListener);
		shopPanelCooldown.add(backCooldownShopButton);
		
		JPanel shopPanelMana = new JPanel();
		shopPanelMana.setOpaque(false);
		shopCardsPanel.add(shopPanelMana, "m");
		shopPanelMana.setLayout(new BoxLayout(shopPanelMana, BoxLayout.Y_AXIS));
		
		Component glue39 = Box.createVerticalGlue();
		shopPanelMana.add(glue39);
		
		JLabel manaShopLabel = ComponentFactory.createShopLabel("Mana");
		shopPanelMana.add(manaShopLabel);
		
		Component glue40 = Box.createVerticalGlue();
		shopPanelMana.add(glue40);
		
		backManaShopButton = ComponentFactory.createShopBackButton("m");
		backManaShopButton.addActionListener(shopButtonListener);
		shopPanelMana.add(backManaShopButton);
		
		JPanel shopPanelManaRegen = new JPanel();
		shopPanelManaRegen.setOpaque(false);
		shopCardsPanel.add(shopPanelManaRegen, "mregen");
		shopPanelManaRegen.setLayout(new BoxLayout(shopPanelManaRegen, BoxLayout.Y_AXIS));
		
		Component glue41 = Box.createVerticalGlue();
		shopPanelManaRegen.add(glue41);
		
		JLabel manaRegenShopLabel = ComponentFactory.createShopLabel("Mana Regeneration");
		shopPanelManaRegen.add(manaRegenShopLabel);
		
		Component glue42 = Box.createVerticalGlue();
		shopPanelManaRegen.add(glue42);
		
		backManaRegenButton = ComponentFactory.createShopBackButton("mregen");
		backManaRegenButton.addActionListener(shopButtonListener);
		shopPanelManaRegen.add(backManaRegenButton);
		
		JPanel shopPanelHealth = new JPanel();
		shopPanelHealth.setOpaque(false);
		shopCardsPanel.add(shopPanelHealth, "h");
		shopPanelHealth.setLayout(new BoxLayout(shopPanelHealth, BoxLayout.Y_AXIS));
		
		Component glue43 = Box.createVerticalGlue();
		shopPanelHealth.add(glue43);
		
		JLabel healthShopLabel = ComponentFactory.createShopLabel("Health");
		shopPanelHealth.add(healthShopLabel);
		
		Component glue44 = Box.createVerticalGlue();
		shopPanelHealth.add(glue44);
		
		backHealthShopButton = ComponentFactory.createShopBackButton("h");
		backHealthShopButton.addActionListener(shopButtonListener);
		shopPanelHealth.add(backHealthShopButton);
		
		JPanel shopPanelMagicResist = new JPanel();
		shopPanelMagicResist.setOpaque(false);
		shopCardsPanel.add(shopPanelMagicResist, "mr");
		shopPanelMagicResist.setLayout(new BoxLayout(shopPanelMagicResist, BoxLayout.Y_AXIS));
		
		Component glue45 = Box.createVerticalGlue();
		shopPanelMagicResist.add(glue45);
		
		JLabel magicResistShopLabel = ComponentFactory.createShopLabel("Magic Resistance");
		shopPanelMagicResist.add(magicResistShopLabel);
		
		Component glue46 = Box.createVerticalGlue();
		shopPanelMagicResist.add(glue46);
		
		backMagicResistShopButton = ComponentFactory.createShopBackButton("mr");
		backMagicResistShopButton.addActionListener(shopButtonListener);
		shopPanelMagicResist.add(backMagicResistShopButton);
		
		JPanel shopPanelArmor = new JPanel();
		shopPanelArmor.setOpaque(false);
		shopCardsPanel.add(shopPanelArmor, "ar");
		shopPanelArmor.setLayout(new BoxLayout(shopPanelArmor, BoxLayout.Y_AXIS));
		
		Component glue47 = Box.createVerticalGlue();
		shopPanelArmor.add(glue47);
		
		JLabel armorShopLabel = ComponentFactory.createShopLabel("Armor");
		shopPanelArmor.add(armorShopLabel);
		
		Component glue48 = Box.createVerticalGlue();
		shopPanelArmor.add(glue48);
		
		backArmorShopButton = ComponentFactory.createShopBackButton("ar");
		backArmorShopButton.addActionListener(shopButtonListener);
		shopPanelArmor.add(backArmorShopButton);
		
		JPanel shopPanelHealthRegen = new JPanel();
		shopPanelHealthRegen.setOpaque(false);
		shopCardsPanel.add(shopPanelHealthRegen, "hregen");
		shopPanelHealthRegen.setLayout(new BoxLayout(shopPanelHealthRegen, BoxLayout.Y_AXIS));
		
		Component glue49 = Box.createVerticalGlue();
		shopPanelHealthRegen.add(glue49);
		
		JLabel healthRegenShopLabel = ComponentFactory.createShopLabel("Health Regeneration");
		shopPanelHealthRegen.add(healthRegenShopLabel);
		
		Component glue50 = Box.createVerticalGlue();
		shopPanelHealthRegen.add(glue50);
		
		backHealthRegenShopButton = ComponentFactory.createShopBackButton("hregen");
		backHealthRegenShopButton.addActionListener(shopButtonListener);
		shopPanelHealthRegen.add(backHealthRegenShopButton);
		
		JPanel buttonPanel = new JPanel();
		sidePanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		resetButton = new ResetButton("Reset");
		resetButton.setEnabled(false);
		resetButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		resetButton.setOpaque(false);
		resetButton.setFocusPainted(false);
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				refreshChampionItems();
			}
		});
		buttonPanel.add(resetButton, BorderLayout.EAST);
		
		saveButton = new SaveButton();
		saveButton.setEnabled(false);
		saveButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		saveButton.setOpaque(false);
		saveButton.setFocusPainted(false);
		saveButton.setPreferredSize(new Dimension(57, 40));
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		});
		buttonPanel.add(saveButton, BorderLayout.CENTER);
		
		writeToLog("Creating item panels", 1);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		backgroundPanel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 3));
		
		ActionListener changeListener = new ItemChangeActionListener();
		
		draggableItemsScrollPane = new JScrollPane();
		draggableItemsScrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
		draggableItemsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		draggableItemsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		draggableItemsScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		draggableItemsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(18, 0));
		draggableItemsScrollPane.setOpaque(false);
		draggableItemsScrollPane.getViewport().setOpaque(false);
		
		draggableItemsViewportView = new TranslucentPanel(175);
		translucentPanels.add(draggableItemsViewportView);
		draggableItemsViewportView.setLayout(new BorderLayout());
		draggableItemsScrollPane.setViewportView(draggableItemsViewportView);
		
		TranslucentPanel header = new TranslucentPanel(220);
		header.setPreferredSize(new Dimension(0, 18));
		header.setLayout(new BorderLayout());
		header.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), new EmptyBorder(0, 5, 0, 5)));
		draggableItemsHeaderLabel = new JLabel("Drag an item from below to one of the slots above");
		draggableItemsHeaderLabel.setForeground(Color.gray);
		draggableItemsHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
		header.add(draggableItemsHeaderLabel, BorderLayout.CENTER);
		draggableItemsScrollPane.setColumnHeaderView(header);
		draggableItemsScrollPane.getColumnHeader().setOpaque(false);
		
		mainPanel.add(draggableItemsScrollPane, BorderLayout.CENTER);
		
		draggableItemsModeButton = new TranslucentButton(220);
		draggableItemsModeButton.setIcon(ResourceLoader.getImageIcon("icon_grid.png"));
		draggableItemsModeButton.setBorder(new EmptyBorder(1, 1, 1, 1));
		int neededWidth = draggableItemsScrollPane.getVerticalScrollBar().getPreferredSize().width;
		int neededHeight = draggableItemsScrollPane.getHorizontalScrollBar().getPreferredSize().height;
		draggableItemsModeButton.setPreferredSize(new Dimension(neededWidth, neededHeight));
		draggableItemsModeButton.setFocusPainted(false);
		draggableItemsModeButton.setFocusable(false);
		draggableItemsModeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				setItemDisplayMode(itemDisplayMode.getNextMode());
			}
		});
		draggableItemsScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, draggableItemsModeButton);
		
		draggableItemModel = new DraggableItemContainerModel(itemFilterModel);
		
		draggableItemGridPanel = new DraggableItemGridContainer(draggableItemModel, changeListener);
		draggableItemListPanel = new DraggableItemListContainer(draggableItemModel, changeListener);
		setItemDisplayMode(itemDisplayMode);
		
		TranslucentPanel itemsPanel = new TranslucentPanel(175);
		translucentPanels.add(itemsPanel);
		itemsPanel.setOpaque(false);
		itemsPanel.setPreferredSize(new Dimension(0, 98));
		itemsPanel.setBorder(new CompoundBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null), new EmptyBorder(0, 2, 1, 2)));
		mainPanel.add(itemsPanel, BorderLayout.NORTH);
		itemsPanel.setLayout(new BorderLayout(0, 2));
		
		JPanel itemSetPanel = new JPanel();
		itemSetPanel.setOpaque(false);
		itemsPanel.add(itemSetPanel, BorderLayout.CENTER);
		itemSetPanel.setBorder(new EmptyBorder(0, 0, -1, 0));
		GridBagLayout gbl_itemSetPanel = new GridBagLayout();
		gbl_itemSetPanel.columnWidths = new int[]{64, 64, 64, 64, 0};
		gbl_itemSetPanel.rowHeights = new int[]{0, 60, 0};
		gbl_itemSetPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_itemSetPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		itemSetPanel.setLayout(gbl_itemSetPanel);
		
		itemCategoryLabel1 = new EditableLabel("Starting Items");
		itemCategoryLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_startingItemsLabel = new GridBagConstraints();
		gbc_startingItemsLabel.fill = GridBagConstraints.BOTH;
		gbc_startingItemsLabel.insets = new Insets(0, 0, 0, 2);
		gbc_startingItemsLabel.gridx = 0;
		gbc_startingItemsLabel.gridy = 0;
		itemSetPanel.add(itemCategoryLabel1, gbc_startingItemsLabel);
		
		itemCategoryLabel2 = new EditableLabel("Core Items");
		itemCategoryLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_coreItemsLabel = new GridBagConstraints();
		gbc_coreItemsLabel.fill = GridBagConstraints.BOTH;
		gbc_coreItemsLabel.insets = new Insets(0, 0, 0, 2);
		gbc_coreItemsLabel.gridx = 1;
		gbc_coreItemsLabel.gridy = 0;
		itemSetPanel.add(itemCategoryLabel2, gbc_coreItemsLabel);
		
		itemCategoryLabel3 = new EditableLabel("Offensive Items");
		itemCategoryLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_newLabelLabel = new GridBagConstraints();
		gbc_newLabelLabel.fill = GridBagConstraints.BOTH;
		gbc_newLabelLabel.insets = new Insets(0, 0, 0, 2);
		gbc_newLabelLabel.gridx = 2;
		gbc_newLabelLabel.gridy = 0;
		itemSetPanel.add(itemCategoryLabel3, gbc_newLabelLabel);
		
		itemCategoryLabel4 = new EditableLabel("Defensive Items");
		itemCategoryLabel4.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_defensiveItemsLabel = new GridBagConstraints();
		gbc_defensiveItemsLabel.fill = GridBagConstraints.BOTH;
		gbc_defensiveItemsLabel.gridx = 3;
		gbc_defensiveItemsLabel.gridy = 0;
		itemSetPanel.add(itemCategoryLabel4, gbc_defensiveItemsLabel);
		
		itemCategoryPanel1 = new BuildPanel();
		GridBagConstraints gbc_itemCategoryPanel1 = new GridBagConstraints();
		gbc_itemCategoryPanel1.insets = new Insets(0, 0, 0, 2);
		gbc_itemCategoryPanel1.fill = GridBagConstraints.BOTH;
		gbc_itemCategoryPanel1.gridx = 0;
		gbc_itemCategoryPanel1.gridy = 1;
		itemSetPanel.add(itemCategoryPanel1, gbc_itemCategoryPanel1);
		itemCategoryPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		itemCategoryPanel2 = new BuildPanel();
		GridBagConstraints gbc_itemCategoryPanel2 = new GridBagConstraints();
		gbc_itemCategoryPanel2.insets = new Insets(0, 0, 0, 2);
		gbc_itemCategoryPanel2.fill = GridBagConstraints.BOTH;
		gbc_itemCategoryPanel2.gridx = 1;
		gbc_itemCategoryPanel2.gridy = 1;
		itemSetPanel.add(itemCategoryPanel2, gbc_itemCategoryPanel2);
		itemCategoryPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		itemCategoryPanel3 = new BuildPanel();
		GridBagConstraints gbc_itemCategoryPanel3 = new GridBagConstraints();
		gbc_itemCategoryPanel3.insets = new Insets(0, 0, 0, 2);
		gbc_itemCategoryPanel3.fill = GridBagConstraints.BOTH;
		gbc_itemCategoryPanel3.gridx = 2;
		gbc_itemCategoryPanel3.gridy = 1;
		itemSetPanel.add(itemCategoryPanel3, gbc_itemCategoryPanel3);
		itemCategoryPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		itemCategoryPanel4 = new BuildPanel();
		GridBagConstraints gbc_itemCategoryPanel4 = new GridBagConstraints();
		gbc_itemCategoryPanel4.fill = GridBagConstraints.BOTH;
		gbc_itemCategoryPanel4.gridx = 3;
		gbc_itemCategoryPanel4.gridy = 1;
		itemSetPanel.add(itemCategoryPanel4, gbc_itemCategoryPanel4);
		itemCategoryPanel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		itemPanels = new ItemSetPanel[6];
		
		JPanel itemSetsPanel = new JPanel();
		itemSetsPanel.setBorder(null);
		itemSetsPanel.setOpaque(false);
		itemsPanel.add(itemSetsPanel, BorderLayout.SOUTH);
		itemSetsPanel.setLayout(new BorderLayout(2, 0));
		
		itemSetsLabel = new JLabel("Item Sets:");
		itemSetsPanel.add(itemSetsLabel, BorderLayout.WEST);
		
		itemSetComboBox = new JComboBox<String>();
		itemSetComboBox.setPreferredSize(new Dimension(0, 22));
		itemSetComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{noChampionText}));
		itemSetRenderer = new SelectionListCellRenderer<String>("Current");
		itemSetComboBox.setRenderer(itemSetRenderer);
		itemSetComboBox.setMaximumRowCount(8);
		itemSetComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if(!disableComboBoxUpdate.get() && evt.getStateChange() == ItemEvent.SELECTED)
				{
					disableComboBoxUpdate.set(true);
					changeItemSet(itemSetComboBox.getSelectedIndex(), true);
					disableComboBoxUpdate.set(false);
				}
			}
		});
		itemSetComboBox.setEditable(true);
		JTextComponent textEditor = (JTextComponent)itemSetComboBox.getEditor().getEditorComponent();
		textEditor.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void insertUpdate(DocumentEvent evt)
			{
				changeItemSetName(evt);
			}
			
			@Override
			public void removeUpdate(DocumentEvent evt)
			{
				changeItemSetName(evt);
			}
			
			@Override
			public void changedUpdate(DocumentEvent evt)
			{
				changeItemSetName(evt);
			}
		});
		itemSetComboBox.setOpaque(false);
		itemSetsPanel.add(itemSetComboBox, BorderLayout.CENTER);
		itemSetComboBox.setEnabled(false);
		
		JPanel itemSetsButtonPanel = new JPanel();
		itemSetsButtonPanel.setOpaque(false);
		itemSetsPanel.add(itemSetsButtonPanel, BorderLayout.EAST);
		itemSetsButtonPanel.setLayout(new GridLayout(0, 2, -1, 0));
		
		itemSetAddButton = new JButton("+");
		itemSetAddButton.setPreferredSize(new Dimension(28, 22));
		itemSetAddButton.setEnabled(false);
		itemSetAddButton.setBorder(null);
		itemSetAddButton.setOpaque(false);
		itemSetAddButton.setFocusPainted(false);
		itemSetAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				addItemSet();
			}
		});
		itemSetsButtonPanel.add(itemSetAddButton);
		
		itemSetRemoveButton = new JButton("\u2212");
		itemSetRemoveButton.setPreferredSize(new Dimension(28, 22));
		itemSetRemoveButton.setEnabled(false);
		itemSetRemoveButton.setBorder(null);
		itemSetRemoveButton.setOpaque(false);
		itemSetRemoveButton.setFocusPainted(false);
		itemSetRemoveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				removeItemSet();
			}
		});
		itemSetsButtonPanel.add(itemSetRemoveButton);
		
		pack();
		backgroundPanel.setSize(getWidth(), getHeight());
		setLocationRelativeTo(null);
	}
	
	//endregion
	
	//region: GUI methods
	
	private void reloadText()
	{
		titleBase = LocaleDatabase.getString("main.title");
		updateTitle();
		
		//Menu bar
		versionLabel.setText(getString("main.menu.version"));
		
		//File menu
		fileMenu.setText(" "+getString("main.menu.file")+" ");
		importMenuItem.setText(getString("main.menu.file.import")+"...");
		exportMenuItem.setText(getString("main.menu.file.export")+"...");
		copyCodeMenuItem.setText(getString("main.menu.file.copyCode"));
		getCodeMenuItem.setText(getString("main.menu.file.pasteCode"));
		openFolderMenuItem.setText(getString("main.menu.file.openCharFolder"));
		launchMenuItem.setText(getString("main.menu.file.launchGame"));
		exitMenuItem.setText(getString("main.menu.file.exit"));
		
		//Build menu
		buildMenu.setText(" "+getString("main.menu.build")+" ");
		newBuildMenuItem.setText(getString("main.menu.build.new"));
		duplicateBuildMenuItem.setText(getString("main.menu.build.duplicate"));
		copyBuildMenuItem.setText(getString("main.menu.build.copy")+"...");
		saveBuildMenuItem.setText(getString("main.menu.build.save"));
		clearBuildMenuItem.setText(getString("main.menu.build.clear"));
		resetBuildMenuItem.setText(getString("main.menu.build.reset"));
		resetDefaultsBuildMenuItem.setText(getString("main.menu.build.resetDefaults"));
		
		//Options menu
		optionsMenu.setText(" "+getString("main.menu.options")+" ");
		updateMenu.setText(getString("main.menu.options.update"));
		updateStartupMenuItem.setText(getString("main.menu.options.update.startup"));
		updateProgramMenuItem.setText(getString("main.menu.options.update.checkProgram"));
		updateCacheMenuItem.setText(getString("main.menu.options.update.checkCache"));
		backupMenu.setText(getString("main.menu.options.backup"));
		backupEnableMenuItem.setText(getString("main.menu.options.backup.enable"));
		backupRestoreMenuItem.setText(getString("main.menu.options.backup.restore"));
		manualPathMenuItem.setText(getString("main.menu.options.manualPath"));
		advancedFiltersMenuItem.setText(getString("main.menu.options.advancedFiltering"));
		itemDisplayModeMenu.setText(getString("main.menu.options.itemMode"));
		for(ItemDisplayMode mode : ItemDisplayMode.values())
			itemModeMenuItems.get(mode).setText(getString("main.menu.options.itemMode."+mode.toString().toLowerCase()));
		imageSizeMenu.setText(getString("main.menu.options.itemSize"));
		for(JRadioButtonMenuItem item : imageSizeMenuItems)
			item.setText(getString("main.menu.options.itemSize."+item.getName()));
		useDefaultsMenuItem.setText(getString("main.menu.options.useDefaults"));
		editFavoritesMenuItem.setText(getString("main.menu.options.editFavorites"));
		languageMenu.setText(getString("main.menu.options.language"));
		backgroundImageMenu.setText(getString("main.menu.options.bg"));
		backgroundNoImage.setText(getString("main.menu.options.bg.none"));
		backgroundSolidsMenu.setText(getString("main.menu.options.bg.solid"));
		for(String c : colorMenuItems.keySet())
		{
			String text = getString("main.menu.options.bg.solid."+c.toLowerCase().replaceAll(" ", ""));
			colorMenuItems.get(c).setText(!text.startsWith("!") ? text : c);
		}
		backgroundGradientsMenu.setText(getString("main.menu.options.bg.gradient"));
		tooltipsMenu.setText(getString("main.menu.options.tooltip"));
		enableTooltipsMenuItem.setText(getString("main.menu.options.tooltip.enable"));
		useFixedWidthMenuItem.setText(getString("main.menu.options.tooltip.setFixed"));
		minimizeTrayMenuItem.setText(getString("main.menu.options.minimizeTray"));
		
		//Help menu
		helpMenu.setText(" "+getString("main.menu.help")+" ");
		helpMenuItem.setText(getString("main.menu.help.help")+"...");
		toolsMenu.setText(getString("main.menu.help.tools"));
		debugRestartMenuItem.setText(getString("main.menu.help.tools.restart"));
		aboutMenuItem.setText(getString("main.menu.help.about")+"...");
		changelogMenuItem.setText(getString("main.menu.help.changelog")+"...");
		donateMenuItem.setText(getString("main.menu.help.donate")+"...");
		
		//Champion box
		championComboBox.reloadText();
		gameModeComboBox.reloadText();
		
		//Item set box
		itemSetsLabel.setText(getString("main.itemset.itemSets")+":");
		
		//Filters box
		((TitledBorder)filtersPanel.getBorder()).setTitle(getString("main.filter.itemFilters"));
		itemFilterTextField.setAltText(getString("main.filter.filterByName"));
		categoriesLabel.setText(getString("main.filter.itemFilterCategories"));
		statsLabel.setText(getString("main.filter.itemFilterStats"));
		
		((TitledBorder)shopPanel.getBorder()).setTitle(getString("main.filter.shop"));
		shopHistoryPanel.getFirstButton().setText(getString("main.filter.shopHome"));
		
		attackCheckBox.setText(getString("main.filter.attack"));
		attackShopButton.setText(getString("main.filter.attack"));
		magicCheckBox.setText(getString("main.filter.magic"));
		magicShopButton.setText(getString("main.filter.magic"));
		defenseCheckBox.setText(getString("main.filter.defense"));
		defenseShopButton.setText(getString("main.filter.defense"));
		movementCheckBox.setText(getString("main.filter.movement"));
		movementShopButton.setText(getString("main.filter.movement"));
		consumableCheckBox.setText(getString("main.filter.consumable"));
		consumableShopButton.setText(getString("main.filter.consumables"));
		
		abilityPowerCheckBox.setText(getString("main.filter.abilityPower"));
		abilityPowerShopButton.setText(getString("main.filter.abilityPower"));
		armorCheckBox.setText(getString("main.filter.armor"));
		armorShopButton.setText(getString("main.filter.armor"));
		armorPenCheckBox.setText(getString("main.filter.armorPen"));
		attackDamageCheckBox.setText(getString("main.filter.attackDamage"));
		attackDamageShopButton.setText(getString("main.filter.attackDamage"));
		attackSpeedCheckBox.setText(getString("main.filter.attackSpeed"));
		attackSpeedShopButton.setText(getString("main.filter.attackSpeed"));
		cooldownCheckBox.setText(getString("main.filter.cooldowns"));
		cooldownShopButton.setText(getString("main.filter.cooldownReduction"));
		criticalStrikeCheckBox.setText(getString("main.filter.criticalChance"));
		criticalStrikeShopButton.setText(getString("main.filter.criticalStrike"));
		healthCheckBox.setText(getString("main.filter.health"));
		healthShopButton.setText(getString("main.filter.health"));
		healthRegenCheckBox.setText(getString("main.filter.healthRegen"));
		healthRegenShopButton.setText(getString("main.filter.healthRegeneration"));
		lifestealCheckBox.setText(getString("main.filter.lifesteal"));
		lifestealShopButton.setText(getString("main.filter.lifesteal"));
		magicPenCheckBox.setText(getString("main.filter.magicPen"));
		magicResCheckBox.setText(getString("main.filter.magicRes"));
		magicResShopButton.setText(getString("main.filter.magicResistance"));
		manaCheckBox.setText(getString("main.filter.mana"));
		manaShopButton.setText(getString("main.filter.mana"));
		manaRegenCheckBox.setText(getString("main.filter.manaRegen"));
		manaRegenShopButton.setText(getString("main.filter.manaRegeneration"));
		spellVampCheckBox.setText(getString("main.filter.spellVamp"));
		tenacityCheckBox.setText(getString("main.filter.tenacity"));
		
		//Items box
		draggableItemsHeaderLabel.setText(getString("main.items.dragItem"));
		draggableItemsModeButton.setToolTipText(String.format(getString("main.items.mode.tooltip"), getString("main.menu.options.itemMode."+itemDisplayMode.getNextMode().toString().toLowerCase())));
		
		//Buttons
		saveButton.reloadText();
		resetButton.setText(getString("main.buttons.reset"));
		
		//System tray
		if(systemTrayEnabled)
		{
			sysTrayTitleMenuItem.setText(getString("main.title"));
			sysTrayVersionMenuItem.setText("    "+getString("systray.version")+version+" "+versionAdd);
			sysTrayOptionsMenu.setText(getString("systray.options"));
			sysTrayManualPathMenuItem.setText(getString("systray.options.setPath"));
			sysTrayEditFavoritesMenuItem.setText(getString("systray.options.editFavorites"));
			sysTrayShowWindowText = getString("systray.showWindow");
			sysTrayHideWindowText = getString("systray.hideWindow");
			if(displayed)
				sysTrayDisplayMenuItem.setText(sysTrayHideWindowText);
			else
				sysTrayDisplayMenuItem.setText(sysTrayShowWindowText);
			sysTrayExitMenuItem.setText(getString("systray.exit"));
		}
		
		//Dialogs
		BuildChooserDialog.reloadText();
		CopyBuildDialog.reloadText();
		FavoritesDialog.reloadText();
		AboutDialog.reloadText();
		ChangelogDialog.reloadText();
	}
	
	private String getString(String key)
	{
		return LocaleDatabase.getString(key);
	}
	
	private void loadBackgrounds()
	{
		defaultBackgrounds = new HashMap<String, String>();
		defaultBackgrounds.put("Abstract", "abstract.jpg");
		defaultBackgrounds.put("Dominion", "bg1.jpg");
		defaultBackgrounds.put("Kassadin", "bg2.jpg");
		defaultBackgrounds.put("Kayle", "bg3.jpg");
		defaultBackgrounds.put("Morgana", "bg4.jpg");
		defaultBackgrounds.put("Teemo", "bg5.jpg");
		
		customBackgrounds = new ArrayList<String>();
		String[] bgFolders = {"backgrounds", "bgs", "background", "bg", "."};
		for(String s : bgFolders)
		{
			File bgFolder = new File(s);
			if(bgFolder.exists() && bgFolder.isDirectory())
			{
				String[] files = bgFolder.list();
				for(String file : files)
					if(!file.startsWith(".") &&	(file.endsWith(".jpg") || file.endsWith(".png") || file.endsWith(".gif")))
						customBackgrounds.add(s+"/"+file);
			}
		}
	}
	
	private void setBackground(String bg)
	{
		currentBackground = bg;
		if(bg.startsWith("color-"))
		{
			int c = Integer.parseInt(bg.substring(6));
			int alpha = 175;
			if(c == 6579300)
				alpha = 165;
			if(c == 0)
				alpha = 155;
			for(TranslucentPanel p : translucentPanels)
				p.setAlpha(alpha);
		}
		else
		{
			int alpha = 175;
			for(TranslucentPanel p : translucentPanels)
				p.setAlpha(alpha);
		}
		backgroundPanel.setBackground(bg.equals("null") ? null : bg);
	}
	
	private void showAdvancedFilters(boolean show)
	{
		useAdvancedFilters = show;
		((CardLayout)itemSelectionPanel.getLayout()).show(itemSelectionPanel, show ? "filters" : "shop");
	}
	
	private void updateShopPanel(String code, String text)
	{
		if(code != null)
		{
			if(code.startsWith("back"))
			{
				if(!shopHistoryPanel.getCurrent().equals("main"))
				{
					itemFilterModel.clearRelatedItemFilter();
					String backCode = shopHistoryPanel.popCard();
					if(code.indexOf('-') > 0)
					{
						if(code.endsWith("main"))
						{
							while(!backCode.equals("main"))
							{
								ItemProperty newFilter = ItemProperty.commandToFilter(shopHistoryPanel.getLast());
								itemFilterModel.removeItemPropertyFilter(newFilter);
								backCode = shopHistoryPanel.popCard();
							}
						}
						backCode = backCode.substring(backCode.indexOf('-')+1);
					}
					code = backCode;
					
					ItemProperty newFilter = ItemProperty.commandToFilter(shopHistoryPanel.getLast());
					itemFilterModel.removeItemPropertyFilter(newFilter);
				}
			}
			else
			{
				shopHistoryPanel.pushCard(code, text);
				ItemProperty newFilter = ItemProperty.commandToFilter(code);
				itemFilterModel.addItemPropertyFilter(newFilter);
			}
			
			((CardLayout)shopCardsPanel.getLayout()).show(shopCardsPanel, code);
		}
	}
	
	private void setItemDisplayMode(ItemDisplayMode mode)
	{
		if(!disableItemModeUpdate)
		{
			writeToLog("Setting item display mode to: "+mode);
			itemDisplayMode = mode;
			
			disableItemModeUpdate = true;
			itemModeMenuItems.get(itemDisplayMode).setSelected(true);
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
			draggableItemsModeButton.setToolTipText(String.format(getString("main.items.mode.tooltip"), getString("main.menu.options.itemMode."+nextMode)));
			
			draggableItemsViewportView.removeAll();
			draggableItemsViewportView.add(dragItemCont, BorderLayout.NORTH);
			dragItemCont.updateContents(true);
			
			draggableItemsScrollPane.revalidate();
			draggableItemsScrollPane.repaint();
		}
	}
	
	private void setItem(int index, Item item)
	{
		writeToLog("Item "+(index+1)+" set to: "+item);
		//TODO
		//itemPanels[index].setItem(item);
	}
	
	public void reset()
	{
		writeToLog("Resetting items and filters");
		
		ItemSet set = championItems.get(currentItemSet);
		if(set != null)
			set.reset();
		
		clearItems();
		
		resetFilters();
		resetShop();
	}
	
	private void resetFilters()
	{
		attackCheckBox.setSelected(false);
		defenseCheckBox.setSelected(false);
		movementCheckBox.setSelected(false);
		magicCheckBox.setSelected(false);
		consumableCheckBox.setSelected(false);
		
		attackDamageCheckBox.setSelected(false);
		abilityPowerCheckBox.setSelected(false);
		attackSpeedCheckBox.setSelected(false);
		cooldownCheckBox.setSelected(false);
		armorCheckBox.setSelected(false);
		magicResCheckBox.setSelected(false);
		healthCheckBox.setSelected(false);
		healthRegenCheckBox.setSelected(false);
		manaCheckBox.setSelected(false);
		manaRegenCheckBox.setSelected(false);
		armorPenCheckBox.setSelected(false);
		magicPenCheckBox.setSelected(false);
		lifestealCheckBox.setSelected(false);
		spellVampCheckBox.setSelected(false);
		criticalStrikeCheckBox.setSelected(false);
		tenacityCheckBox.setSelected(false);
	}
	
	private void resetShop()
	{
		updateShopPanel("back-main", null);
	}
	
	private void clearItems()
	{
		for(int n = 0; n < 6; n++)
			setItem(n, null);
	}
	
	private void toggleDisplayed()
	{
		setDisplayed(!displayed);
	}
	
	private void setDisplayed(boolean disp)
	{
		displayed = disp;
		setVisible(displayed);
		
		if(displayed)
		{
			setState(JFrame.NORMAL);
			sysTrayDisplayMenuItem.setText(sysTrayHideWindowText);
		}
		else
		{
			sysTrayDisplayMenuItem.setText(sysTrayShowWindowText);
		}
		
		toFront();
		repaint();
	}
	
	private void setSaveSuccess(boolean success)
	{
		saveButton.setSuccess(success);
	}
	
	private void updateTitle()
	{
		String titleAdd = null;
		if(currentChampion != null)
		{
			String champKey = currentChampion.getKey();
			if(titles.containsKey(champKey))
			{
				String[] list = titles.get(champKey);
				titleAdd = list[(int)(Math.random()*list.length)];
			}
		}
		
		setTitle(titleBase+(titleAdd != null ? ": "+titleAdd : ""));
	}
	
	private void openPathDialog()
	{
		GamePath gameDir = GamePathUtil.getDir();
		GamePath path = PathDialog.open(LoLItems.this, Platform.getCurrentPlatform(), gameDir, lolDirHistory.toArray(new GamePath[0]));
		if(lolDirHistory.contains(path))
			lolDirHistory.remove(path);
		lolDirHistory.addFirst(path);
		if(lolDirHistory.size() > 5)
			lolDirHistory.removeLast();
		GamePathUtil.setLoLDir(path, true);
	}
	
	public void open()
	{
		setVisible(true);
	}
	
	private void close()
	{
		checkSave();
		saveOptions();
		dispose();
		SystemUtil.exit(0);
	}
	
	private class ItemChangeActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			String com = evt.getActionCommand();
			if(com != null && com.length() > 0)
			{
				if(com.startsWith("item"))
				{
					String id = com.substring(com.indexOf(':')+1);
					Item item = ItemDatabase.getItem(id);
					for(int n = 0; n < itemPanels.length; n++)
						if(itemPanels[n].getItem() == null)
						{
							setItem(n, item);
							changeMade = true;
							break;
						}
				}
			}
			else
				changeMade = true;
		}
	}
	
	//endregion
	
	//region: Information methods
	
	private boolean loadItems()
	{
		writeToLog("Initializing database", 1);
		boolean success = ItemDatabase.initDatabase();
		success &= DefaultItemDatabase.initDatabase();
		
		if(success)
		{
			writeToLog("Sorting item list", 1);
			Set<String> items = ItemDatabase.getItems();
			ArrayList<Item> sortedItems = new ArrayList<Item>(items.size());
			for(String itemID : items)
			{
				Item item = ItemDatabase.getItem(itemID);
				sortedItems.add(item);
			}
			Collections.sort(sortedItems);
			
			writeToLog("Initializing item panel", 1);
			itemFilterModel.addItemPropertyFilter(ItemProperty.CLASSIC);
			DragSource dragSource = new DragSource();
			writeToLog("Adding all items to item panel", 1);
			for(Item item : sortedItems)
			{
				DraggableItem itemPanel = new DraggableItem(item, itemFilterModel);
				itemPanel.setItemSize(imageSize, imageSize);
				dragSource.createDefaultDragGestureRecognizer(itemPanel, DnDConstants.ACTION_COPY, this);
				draggableItemModel.addItem(itemPanel);
			}
			
			draggableItemGridPanel.refreshPanel();
			draggableItemListPanel.refreshPanel();
			
			writeToLog("Adding drag-and-drop gesture to items", 1);
			ActionListener itemChangeListener = new ItemChangeActionListener();
			for(ItemSetPanel panel : itemPanels)
			{
				//panel.setChangeListener(itemChangeListener);
				dragSource.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_MOVE, this);
			}
		}
		else
		{
			writeToLog("Failed to load items", LoggingType.ERROR);
		}
		
		return success;
	}
	
	private boolean loadChampions()
	{
		writeToLog("Initializing champion database", 1);
		boolean success = ChampionDatabase.initDatabase();
		
		if(success)
		{
			Set<String> champIDs = ChampionDatabase.getChampions();
			allChampions = new ArrayList<String>(champIDs.size());
			for(String id : champIDs)
				allChampions.add(ChampionDatabase.getChampion(id).getName());
			Collections.sort(allChampions);
			
			buildChampionsList();
			
			ChampionListCellRenderer.loadImageCache();
		}
		else
		{
			writeToLog("Failed to load champions", LoggingType.ERROR);
		}
		
		return success;
	}
	
	private void buildChampionsList()
	{
		ArrayList<String> remaining = new ArrayList<String>(allChampions);
		
		//Add favorites
		if(favoriteChampions.size() > 0)
		{
			Collections.sort(favoriteChampions);
			for(String name : favoriteChampions)
				remaining.remove(name);
		}
		
		//Add all champions
		championComboBox.updateLists(favoriteChampions, remaining);
		
		//Required dialogs
		//CopyBuildDialog.initChampions(allChampions);
	}
	
	private void saveItemsToSet(int set)
	{
		writeToLog("Saving current items to set: "+set);
		if(set >= 0 && set < championItems.size())
		{
			ItemSet itemSet = championItems.get(set);
			
			for(int n = 0; n < itemPanels.length; n++)
			{
				Item i = itemPanels[n].getItem();
				writeToLog("   Item "+(n+1)+": "+(i != null ? i.getName() : "null"));
				itemSet.setItem(itemPanels[n].getItem(), n);
			}
			championItems.set(set, itemSet);
		}
		else
			writeToLog("   Invalid set index", LoggingType.WARNING);
	}
	
	public void save()
	{
		boolean success = true;
		ItemSet backup = null;
		
		//Update current item set with the currently set items
		try
		{
			writeToLog("Saving item sets");
			if(championItems.size() > 0)
			{
				writeToLog("   Creating backup");
				backup = championItems.get(currentItemSet).clone();
				
				saveItemsToSet(currentItemSet);
			}
			else
				writeToLog("   Nothing to save...", LoggingType.WARNING);
		}
		catch(Exception e)
		{
			writeToLog("Pre-write failed", LoggingType.ERROR);
			writeStackTrace(e);
			success = false;
		}
		
		//Write to file if preparations were successful
		if(success)
		{
			writeToLog("   Writing item set data to file");
			success = ItemFileIO.saveItems(currentChampion, currentGameType, championItems, currentItemSet);
			if(backupEnabled)
			{
				writeToLog("   Writing item set data to backup file");
				ItemFileIO.saveItems(backupLocation, currentChampion, currentGameType, championItems, currentItemSet);
			}
		}
		
		//Success
		if(success)
		{
			writeToLog("Save successful");
			itemSetRenderer.setSelected(currentItemSet);
			changeMade = false;
		}
		else
		{
			writeToLog("Save failed", LoggingType.WARNING);
			if(backup != null)
				championItems.set(currentItemSet, backup);
		}
		
		setSaveSuccess(success);
	}
	
	private void checkSave()
	{
		if(changeMade && currentChampion != null)
		{
			int selection = saveDefault;
			if(showSaveWarning)
			{
				JCheckBox checkbox = new JCheckBox(getString("dialog.doNotShow"));
				checkbox.setFont(UIManager.getFont("OptionPane.font"));
				String message = getString("dialog.unsaved.line1")+"\n"+getString("dialog.unsaved.line2");
				Object[] params = {message, checkbox};
				int n = JOptionPane.showConfirmDialog(this, params, getString("dialog.unsaved.title"), JOptionPane.YES_NO_CANCEL_OPTION);
				if(n != JOptionPane.CANCEL_OPTION)
				{
					showSaveWarning = !checkbox.isSelected();
					if(!showSaveWarning)
						saveDefault = n;
					selection = n;
				}
			}
			
			if(selection == JOptionPane.YES_OPTION)
				save();				
		}
		changeMade = false;
	}
	
	public void setGameMode(GameMode mode)
	{
		currentGameType = mode;
		
		disableComboBoxUpdate.set(true);
		gameModeComboBox.setSelectedItem(mode);
		disableComboBoxUpdate.set(false);
		
		for(GameMode remove : GameMode.values())
			itemFilterModel.removeItemPropertyFilter(remove.getItemProperty());
		itemFilterModel.addItemPropertyFilter(mode.getItemProperty());
		
		refreshChampionItems();
	}
	
	public void setChampion(Champion champ)
	{
		checkSave();
		
		Champion oldChampion = currentChampion;
		currentChampion = champ;
		
		boolean enableStuff = currentChampion != null;
		if(enableStuff)
		{
			championNameLabel.setText(currentChampion.getName());
			championTitleLabel.setText(currentChampion.getTitle());
			championImagePanel.setChampion(currentChampion);
		}
		else
		{
			championNameLabel.setText("");
			championTitleLabel.setText("");
			championImagePanel.setChampion(null);
		}
		itemSetComboBox.setEnabled(enableStuff);
		itemSetAddButton.setEnabled(enableStuff);
		itemSetRemoveButton.setEnabled(enableStuff);
		
		saveButton.setEnabled(enableStuff);
		resetButton.setEnabled(enableStuff);
		copyCodeMenuItem.setEnabled(enableStuff);
		getCodeMenuItem.setEnabled(enableStuff);
		newBuildMenuItem.setEnabled(enableStuff);
		duplicateBuildMenuItem.setEnabled(enableStuff);
		copyBuildMenuItem.setEnabled(enableStuff);
		saveBuildMenuItem.setEnabled(enableStuff);
		clearBuildMenuItem.setEnabled(enableStuff);
		resetBuildMenuItem.setEnabled(enableStuff);
		resetDefaultsBuildMenuItem.setEnabled(enableStuff);
		
		updateChampionItemFilters(oldChampion);
		
		updateTitle();
		
		refreshChampionItems();
	}
	
	public void refreshChampionItems()
	{
		checkSave();
		
		writeToLog("Changing champion item set");
		disableComboBoxUpdate.set(true);
		
		((DefaultComboBoxModel<String>)itemSetComboBox.getModel()).removeAllElements();
		
		if(currentChampion !=  null)
		{
			writeToLog("   Loading sets for champion \""+currentChampion+"\", game type: "+currentGameType.toString()+" (id="+currentGameType+")");
			championItems = ItemFileIO.getItems(currentChampion, currentGameType);
			if(championItems == null)
			{
				writeToLog("   Champion has no loaded item sets");
				championItems = new ArrayList<ItemSet>();
				listNum = 1;
				championItems.add(new ItemSet(useDefaultItems ? defaultItemsText : newItemSetText+" "+listNum));
				itemSetComboBox.addItem(championItems.get(0).getName());
				
				
				itemSetRenderer.setSelected(-1);
				currentItemSet = 0;
				
				if(useDefaultItems)
					setChampionDefaults();
				else
					changeItemSet(0, false);
			}
			else
			{
				writeToLog("   Item sets:");
				for(int n = 0; n < championItems.size(); n++)
				{
					ItemSet set = championItems.get(n);
					writeToLog("      Set: "+set);
					itemSetComboBox.addItem(set.getName());
				}
				listNum = itemSetComboBox.getItemCount();
				
				itemSetRenderer.setSelected(0);	
				changeItemSet(0, false);
			}
		}
		else
		{
			writeToLog("   Champion is null, clearing item data");
			
			championItems = new ArrayList<ItemSet>();
			itemSetComboBox.addItem(noChampionText);
			
			itemSetRenderer.setSelected(-1);
			changeItemSet(-1, false);
		}
		
		disableComboBoxUpdate.set(false);
		
		changeMade = false;
	}
	
	private void setChampionDefaults()
	{
		if(currentChampion != null)
		{
			writeToLog("Setting items to defaults for "+currentChampion.getName()+", game type: "+currentGameType.toString());
			
			List<String> newItems = DefaultItemDatabase.getDefaultItems(currentChampion, currentGameType);
			if(newItems != null && newItems.size() > 0)
			{
				for(int n = 0; n < newItems.size(); n++)
					setItem(n, ItemDatabase.getItem(newItems.get(n)));
				
				return;
			}
			else
				writeToLog("Could not set item defaults for champion: items could not be loaded", LoggingType.WARNING);
		}
		else
			writeToLog("Could not set item defaults for champion: champion is null", LoggingType.WARNING);
		
		for(int n = 0; n < itemPanels.length; n++)
			setItem(n, null);
	}
	
	private void updateChampionItemFilters(Champion oldChampion)
	{
		if(currentChampion != null)
		{
			String champKey = currentChampion.getKey();
			
			//Viktor
			if(champKey.equals("Viktor"))
			{
				writeToLog("Viktor in focus, enabling augments");
				itemFilterModel.addItemPropertyFilter(ItemProperty.VIKTOR);
			}
			else if(oldChampion != null && oldChampion.getKey().equals("Viktor"))
			{
				writeToLog("Viktor out of focus, disabling augments");
				itemFilterModel.removeItemPropertyFilter(ItemProperty.VIKTOR);
			}
			
			//Rengar
			if(champKey.equals("Rengar"))
			{
				writeToLog("Rengar in focus, enabling prize");
				itemFilterModel.addItemPropertyFilter(ItemProperty.RENGAR);
			}
			else if(oldChampion != null && oldChampion.getKey().equals("Rengar"))
			{
				writeToLog("Rengar out of focus, disabling prize");
				itemFilterModel.removeItemPropertyFilter(ItemProperty.RENGAR);
			}
		}
	}
	
	private void addItemSet()
	{
		listNum++;
		String name = "New Set "+listNum;
		writeToLog("Adding new item set: "+name);
		
		disableComboBoxUpdate.set(true);
		itemSetComboBox.addItem(name);
		disableComboBoxUpdate.set(false);
		
		championItems.add(new ItemSet(name, new Item[6]));
		
		writeToLog("   Required sets added, changing to new set");
		changeItemSet(itemSetComboBox.getItemCount()-1, true);
		
		changeMade = true;
	}
	
	private void removeItemSet()
	{
		writeToLog("Removing selected item set...");
		if(championItems.size() > 0)
		{
			try
			{
				int index = itemSetComboBox.getSelectedIndex();
				writeToLog("   Selected index: "+index);
				
				disableComboBoxUpdate.set(true);
				itemSetComboBox.removeItemAt(index);
				disableComboBoxUpdate.set(false);
				championItems.remove(index);
				
				if(index >= championItems.size())
					index--;
				changeItemSet(index, false);
			}
			catch(Exception e)
			{
				writeToLog("Failed to remove item set", LoggingType.ERROR);
				writeStackTrace(e);
			}
			
			//changeItemSet(index-1);
			if(championItems.size() == 0)
			{
				writeToLog("   Selected items are now empty");
				
				for(ItemSetPanel panel : itemPanels)
					panel.setItem(null);
				
				disableComboBoxUpdate.set(true);
				currentItemSet = -1;
				Document doc = ((JTextComponent)itemSetComboBox.getEditor().getEditorComponent()).getDocument();
				try
				{
					disableComboBoxUpdate.set(true);
					doc.remove(0, doc.getLength());
					disableComboBoxUpdate.set(false);
				}
				catch(Exception e)
				{
					writeToLog("Failed to clear combo box text", LoggingType.ERROR);
					writeStackTrace(e);
				}
			}
			
			changeMade = true;
		}
		else
			writeToLog("   Nothing to remove!");
	}
	
	private void duplicateItemSet()
	{
		Item[] itemSets = new Item[6];
		for(int n = 0; n < 6; n++)
			itemSets[n] = itemPanels[n].getItem();
		
		addItemSet();
		
		for(int n = 0; n < 6; n++)
			setItem(n, itemSets[n]);
	}
	
	private void changeItemSetName(DocumentEvent evt)
	{
		if(!disableComboBoxUpdate.get())
		{
			final int selectedIndex = itemSetComboBox.getSelectedIndex();
			Document doc = evt.getDocument();
			try
			{
				final String text = doc.getText(0, doc.getLength());
				final int length = text.length();
				final int pos = evt.getOffset()+1;
				final JTextComponent c = (JTextComponent)itemSetComboBox.getEditor().getEditorComponent();
				if(length > 0)
				{
					SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run()
						{
							disableComboBoxUpdate.set(true);
							
							itemSetComboBox.removeItemAt(selectedIndex);
							itemSetComboBox.insertItemAt(text, selectedIndex);
							itemSetComboBox.setSelectedIndex(selectedIndex);
							c.setCaretPosition(pos > length ? length : pos);
							
							disableComboBoxUpdate.set(false);
							
							ItemSet set = championItems.get(selectedIndex);
							set.setName(text);
							championItems.set(selectedIndex, set);
							
							currentItemSet = selectedIndex >= 0 ? selectedIndex : currentItemSet;
						}
					});
				}
				
				changeMade = true;
			}
			catch(Exception e)
			{
				writeToLog("Error when updating item set text", LoggingType.ERROR);
				writeStackTrace(e);
			}
		}
	}
	
	private void changeItemSet(int setIndex, boolean save)
	{
		writeToLog("Changing item set to index: "+setIndex);
		
		//Set stored index
		int oldItemSet = currentItemSet;
		currentItemSet = setIndex;
		
		if(!disableComboBoxUpdate.get())
		{
			disableComboBoxUpdate.set(true);
			itemSetComboBox.setSelectedIndex(currentItemSet);
			disableComboBoxUpdate.set(false);
		}
		
		//Save previous item set
		if(save)
		{
			writeToLog("   Saving old set: "+oldItemSet);
			saveItemsToSet(oldItemSet);
		}
		
		//The selected item set is in a valid range: set the item set
		if(currentItemSet >= 0 && currentItemSet < championItems.size())
		{
			boolean setEmpty = true;
			
			ItemSet itemSet = championItems.get(currentItemSet);
			if(itemSet != null)
			{
				writeToLog("   Item set: "+itemSet);
				for(int n = 0; n < 6; n++)
				{
					setEmpty &= (itemSet.getItem(n) == null);
					//TODO
					//itemPanels[n].setItem(itemSet.getItem(n));
				}
			}
			
			if(itemSet == null || setEmpty)
			{
				if(useDefaultItems)
				{
					writeToLog("Setting new set to default items");
					setChampionDefaults();
				}
				else
				{
					writeToLog("   No valid item set given, clearing items");
					clearItems();
				}
			}
		}
		//Otherwise clear the display
		else
		{
			writeToLog("   No valid item set given, clearing items");
			clearItems();
		}
	}
	
	private void exportItemSets()
	{
		writeToLog("Exporting item sets");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("XML files", "xml");
		fileChooser.setFileFilter(filter);
		fileChooser.setApproveButtonToolTipText("Export to the selected file or directory");
		
		if(fileChooser.showDialog(this, "Export") == JFileChooser.APPROVE_OPTION)
		{
			File dir = fileChooser.getSelectedFile();
			writeToLog("Chosen dir: "+dir.getAbsolutePath(), 1);
			BuildFileIO.exportBuilds(dir);
		}
	}
	
	private void importItemSets()
	{
		writeToLog("Importing item sets");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileNameExtensionFilter("XML files", "xml");
		fileChooser.setFileFilter(filter);
		fileChooser.setApproveButtonToolTipText("Import the selected file");
		
		if(fileChooser.showDialog(this, "Import") == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			writeToLog("Chosen dir: "+file.getAbsolutePath(), 1);
			BuildFileIO.importBuilds(file);
		}
	}
	
	private void exportCode()
	{
		writeToLog("Exporting item set code");
		if(championItems != null && currentItemSet >= 0)
		{
			ItemSet itemSet = championItems.get(currentItemSet);
			String code = "";
			int lastIndex = 0;
			for(int n = 0; n < 6; lastIndex = ++n)
			{
				Item i = itemSet.getItem(n);
				if(i != null)
					code += i.getID();
				else
				{
					code = null;
					break;
				}
			}
			if(code != null)
			{
				writeToLog("Copying code to clipboard: "+code, 1);
				ClipboardUtil.setClipboardContents(code);
			}
			else
			{
				writeToLog("Missing items, index="+lastIndex, 1, LoggingType.ERROR);
				JOptionPane.showMessageDialog(this, getString("dialog.exportCode.errorText")+getString("dialog.exportCode.errorEmpty"), getString("dialog.exportCode.errorTitle"), JOptionPane.ERROR_MESSAGE);
			}
		}
		else
			writeToLog("Could not export item set code", 1);
	}
	
	private void importCode()
	{
		String code = ClipboardUtil.getClipboardContents();
		if(code.length() == 24)
			importCode(code);
		else
			JOptionPane.showMessageDialog(this, String.format(getString("dialog.importCode.errorLength"), 24), getString("dialog.importCode.errorTitle"), JOptionPane.ERROR_MESSAGE);
	}
	
	public void importCode(String code)
	{
		//Add a new set if the current items are not empty
		boolean isEmpty = true;
		if(championItems.size() > 0)
		{
			ItemSet itemSet = championItems.get(currentItemSet);
			for(int n = 0; n < 6; n++)
				if(itemSet.getItem(n) != null)
					isEmpty = false;
		}
		else
			isEmpty = false;
		
		if(!isEmpty)
		{
			writeToLog("Current item set is not empty, creating new set", 1);
			addItemSet();
		}
		
		ItemSet itemSet = championItems.get(currentItemSet);
		for(int i = 0; i < 6; i++)
		{
			String id = code.substring(i*4, (i+1)*4);
			Item item = ItemDatabase.getItem(id);
			writeToLog("Adding item: "+item.toString(), 1);
			itemPanels[i].setItem(item);
			itemSet.setItem(item, i);
		}
	}
	
	private void restoreBackup()
	{
		if(JOptionPane.showConfirmDialog(this, getString("dialog.backuprestore.line1")+"\n\n"+getString("dialog.backuprestore.line2"), getString("dialog.backupRestore.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
		{
			try
			{
				File src = new File(backupLocation);
				File dest = new File(GamePathUtil.getItemDir());
				writeToLog("Restoring from backup: "+src.getAbsolutePath()+" -> "+dest.getAbsolutePath());
				IOUtil.copyDirectory(src, dest);
			}
			catch(IOException e)
			{
				writeToLog("Failed to restore from backup", LoggingType.ERROR);
				writeStackTrace(e);
			}
		}
	}
	
	//endregion
	
	//region: Drag and drop methods
	
	public void dragGestureRecognized(DragGestureEvent event)
	{
		writeToLog("Item being dragged");
		Cursor cursor = null;
		Component com = event.getComponent();
		
		if(com instanceof DraggableItem)
		{
			writeToLog("Item is a DraggableItem", 1);
			DraggableItem panel = (DraggableItem)com;
			
			if(event.getDragAction() == DnDConstants.ACTION_COPY)
				cursor = DragSource.DefaultCopyDrop;
			
			event.startDrag(cursor, new TransferableItem(panel.getItem()));
		}
		else if(com instanceof ItemSetPanel)
		{
			writeToLog("Item is an ItemPanel", 1);
			ItemSetPanel panel = (ItemSetPanel)com;
			if(panel.getItem() != null)
			{
				if(event.getDragAction() == DnDConstants.ACTION_MOVE)
					cursor = DragSource.DefaultMoveDrop;
				
				event.startDrag(cursor, new TransferableItem(panel));
			}
		}
		else
		{
			writeToLog("Item type is not recognized (problem!)", 1, LoggingType.WARNING);
		}
	}
	
	//endregion
	
	//region: Options methods
	
	private void loadOptions()
	{
		writeToLog("Loading options");
		File file = new File("options.txt");
		if(!file.exists())
		{
			PrintStream out = null;
			try
			{
				out = new PrintStream(file);
				out.println("lolpath:null");
				out.println("useadvanced:0");
				out.println("itemdisplay:grid");
				out.println("usedefaults:1");
				out.println("autobackup:1");
				out.println("imagesize:1");
				out.println("ttenabled:1");
				out.println("ttfixedwidth:0");
				out.println("minimizetotray:0");
				out.println("updatestartup:1");
			}
			catch(FileNotFoundException e)
			{
				writeToLog("Failed to create options file.", LoggingType.ERROR);
				writeStackTrace(e);
				JOptionPane.showMessageDialog(this, "Could not create options file", "Error", JOptionPane.ERROR_MESSAGE);
			}
			finally
			{
				if(out != null)
					out.close();
			}
		}
		
		favoriteChampions = new ArrayList<String>();
		
		Scanner scanner = null;
		String line = null;
		try
		{
			scanner = new Scanner(file);
			while(scanner.hasNext())
			{
				line = scanner.nextLine().trim();
				int index = line.indexOf(':');
				String key = line.substring(0, index);
				String value = line.substring(index+1);
				if(key.equals("lolpath"))
				{
					if("null".equals(value))
						lolDirPath = null;
					else
						lolDirPath = value;
				}
				else if(key.equals("issea"))
				{
					lolDirRegion = "sea";
				}
				else if(key.equals("lolregion"))
				{
					lolDirRegion = value;
				}
				else if(key.equals("language"))
				{
					for(String lang : languages)
						if(lang.equals(value))
						{
							currentLanguage = value;
							break;
						}
				}
				else if(key.equals("useadvanced"))
				{
					int bool = Integer.parseInt(value);
					useAdvancedFilters = bool == 1;
				}
				else if(key.equals("itemdisplay"))
				{
					itemDisplayMode = ItemDisplayMode.getType(value);
				}
				else if(key.equals("usedefaults"))
				{
					int bool = Integer.parseInt(value);
					useDefaultItems = bool == 1;
				}
				else if(key.equals("autobackup"))
				{
					int bool = Integer.parseInt(value);
					backupEnabled = bool == 1;
				}
				else if(key.equals("imagesize"))
				{
					int size = Integer.parseInt(value);
					switch(size)
					{
						case 0: imageSize = 60;
							break;
						case 1: imageSize = 45;
							break;
						case 2: imageSize = 30;
							break;
						default: imageSize = 45;
					}
				}
				else if(key.equals("ttenabled"))
				{
					int bool = Integer.parseInt(value);
					tooltipsEnabled = (bool == 1);
				}
				else if(key.equals("ttfixedwidth"))
				{
					int bool = Integer.parseInt(value);
					fixedTooltipWidth = (bool == 1);
				}
				else if(key.equals("minimizetotray"))
				{
					int bool = Integer.parseInt(value);
					minimizeToTray = (bool == 1);
				}
				else if(key.equals("background"))
				{
					currentBackground = value;
				}
				else if(key.equals("updatestartup"))
				{
					int bool = Integer.parseInt(value);
					checkVersion = bool == 1;
				}
				else if(key.equals("showsavewarning"))
				{
					int bool = Integer.parseInt(value);
					showSaveWarning = bool == 1;
				}
				else if(key.equals("saveaction"))
				{
					saveDefault = Integer.parseInt(value);
				}
				else if(key.equals("favorites"))
				{
					String[] favs = value.split(",");
					for(String s : favs)
						favoriteChampions.add(s);
				}
				else if(key.equals("globalitems"))
				{
					String[] setStrings = value.split(";");
					for(String setString : setStrings)
					{
						//String name = setString.substring(0, setString.indexOf(':'));
						String[] itemStrings = setString.substring(setString.indexOf(':'+1)).split(",");
						//ItemSet set = new ItemSet(name);
						for(int n = 0; n < itemStrings.length; n++)
						{
							//TODO
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			writeToLog("Failed to load options", LoggingType.ERROR);
			writeStackTrace(e);
			JOptionPane.showMessageDialog(this, "Error: Could not load options", "Error", JOptionPane.ERROR_MESSAGE);
			checkVersion = true;
		}
		finally
		{
			if(scanner != null)
				scanner.close();
		}
	}
	
	private void saveOptions()
	{
		writeToLog("Saving options");
		File file = new File("options.txt");
		PrintStream out = null;
		try
		{
			out = new PrintStream(file);
			GamePath dir = GamePathUtil.getDir();
			out.println("lolpath:"+(dir != null ? dir.getPath() : null));
			if(dir != null)
				out.println("lolregion:"+dir.getRegion().toString().toLowerCase());
			out.println("language:"+currentLanguage);
			out.println("useadvanced:"+(useAdvancedFilters ? "1" : "0"));
			out.println("itemdisplay:"+(itemDisplayMode.toString().toLowerCase()));
			out.println("usedefaults:"+(useDefaultItems ? "1" : "0"));
			out.println("autobackup:"+(backupEnabled ? "1" : "0"));
			out.println("imagesize:"+(imageSize == 60 ? 0 : imageSize == 45 ? 1 : 2));
			out.println("ttenabled:"+(tooltipsEnabled ? "1" : "0"));
			out.println("ttfixedwidth:"+(fixedTooltipWidth ? "1" : "0"));
			out.println("minimizetotray:"+(minimizeToTray ? "1" : "0"));
			out.println("background:"+currentBackground);
			out.println("updatestartup:"+(checkVersion ? "1" : "0"));
			if(!showSaveWarning)
			{
				out.println("showsavewarning:"+(showSaveWarning ? "1" : "0"));
				out.println("saveaction:"+saveDefault);
			}
			if(favoriteChampions.size() > 0)
			{
				String list = "";
				for(int n = 0; n < favoriteChampions.size(); n++)
				{
					list += favoriteChampions.get(n);
					if(n < favoriteChampions.size()-1)
						list += ",";
				}
				out.println("favorites:"+list);
			}
		}
		catch(FileNotFoundException e)
		{
			writeToLog("Failed to save options", LoggingType.ERROR);
			writeStackTrace(e);
			JOptionPane.showMessageDialog(this, getString("dialog.options.errorSave"), getString("dialog.options.errorTitle"), JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}
	
	private void setItemImageSize(int size)
	{
		imageSize = size;
		draggableItemGridPanel.setItemSize(imageSize);
		draggableItemListPanel.setItemSize(imageSize);
	}
	
	private void setTooltipsEnabled(boolean enabled)
	{
		tooltipsEnabled = enabled;
		
		ToolTipManager.sharedInstance().setEnabled(tooltipsEnabled);
	}
	
	private void setTooltipsFixed(boolean fixed)
	{
		fixedTooltipWidth = fixed;
		
		draggableItemModel.setFixedTooltip(fixed);
		//TODO
		//for(ItemSetPanel panel : itemPanels)
		//	panel.setFixedTooltip(fixedTooltipWidth);
	}
	
	//endregion
	
	//region: Other methods
	
	private void checkGameVersion()
	{
		writeToLog("Checking version compatibility"); 
		String localVersion = GamePathUtil.getGameVersion();
		if(localVersion != null)
		{
			writeToLog("Local version: "+localVersion, 1);
			String newestVersion = null;
			Scanner scanner = null;
			try
			{
				scanner = new Scanner(CacheResourceLoader.getResource("game_version.txt"));
				newestVersion = scanner.nextLine().trim();
			}
			catch(Exception e)
			{
				writeToLog("Failed to get newest version");
				writeStackTrace(e);
			}
			finally
			{
				if(scanner != null)
					scanner.close();
			}
			
			if(newestVersion != null)
			{
				writeToLog("Newest version: "+newestVersion, 1);
				int[] localVersionValues = GamePathUtil.getVersionValues(localVersion);
				int[] newestVersionValues = GamePathUtil.getVersionValues(newestVersion);
				if(localVersionValues != null && newestVersionValues != null)
				{
					for(int n = 0; n < Math.min(localVersionValues.length, newestVersionValues.length); n++)
					{
						if(localVersionValues[n] < newestVersionValues[n])
						{
							//The game version is older than the newest version, display warning
							writeToLog("Game version is older, displaying warning", 1);
							String title = getString("dialog.version.title");
							title = title.startsWith("!") ? "Old version detected" : title;
							String line1 = getString("dialog.version.line1");
							line1 = line1.startsWith("!") ? "The currently installed game version is older than the latest version." : line1;
							String line2 = getString("dialog.version.line2");
							line2 = line2.startsWith("!") ? "Some features require you update to the latest game version." : line2;
							JOptionPane.showMessageDialog(this, line1+"\n"+line2, title, JOptionPane.WARNING_MESSAGE);
							break;
						}
					}
				}
			}
			else
			{
				writeToLog("Newest version not found", 1, LoggingType.WARNING);
			}
		}
		else
		{
			writeToLog("Local version not found", 1, LoggingType.WARNING);
		}
	}
	
	//endregion
}
