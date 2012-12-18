package net.enigmablade.lol.lolitem;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import net.enigmablade.paradoxion.io.cache.*;
import net.enigmablade.paradoxion.localization.*;
import net.enigmablade.paradoxion.util.*;
import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;
import net.enigmablade.lol.lollib.io.pathhelpers.platforms.*;
import net.enigmablade.lol.lollib.io.pathhelpers.regions.*;
import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.dialogs.*;
import net.enigmablade.lol.lollib.ui.renderers.*;
import net.enigmablade.lol.lollib.util.*;

import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.io.*;
import net.enigmablade.lol.lolitem.io.BuildFileIO.*;
import net.enigmablade.lol.lolitem.ui.*;
import net.enigmablade.lol.lolitem.ui.components.*;
import net.enigmablade.lol.lolitem.ui.components.ItemBuildComboBox.*;

public class EnigmaItems
{
	public static final String appName = "Enigma's Recommended Item Changer";
	public static final String appKey = "EnigmaItem";
	public static final String version = "3.1.0", buildVersion = "2", versionAdd = "";
	
	//UI
	private MainUI ui;
	
	//UI models
	private ItemFilterModel itemFilterModel;
	
	//UI listeners
	private ItemBuildComboBox.ItemBuildTextListener itemBuildTextListener;
	
	//Data
	private List<String> allChampions;
	private Champion currentChampion;
	private List<ItemBuild> championBuilds;
	
	private GameMode currentGameMode = GameMode.CLASSIC;
	private int currentBuildIndex = -1, oldBuildIndex = -1;
	
	private boolean changeMade = false;
	
	//Options
	private Options options;
	
	//Start-up
	
	public EnigmaItems()
	{
		writeToLog("App info: "+appName+" ("+appKey+"), v"+version+"b"+buildVersion+" "+versionAdd);
		
		UpdateUtil.finishUpdate(appKey);
		
		writeToLog("Begin startup initialization");
		long loadStartTime = System.nanoTime();
		
		//Options
		options = Options.loadOptions();
		
		//Version check
		if(options.checkVersion)
		{
			SplashScreen.drawString("Checking version...");
			UpdateUtil.startUpdater(appKey, version, buildVersion, false);
			UpdateUtil.startCacheUpdater(false, "items.xml");
		}
		
		//Cache
		SplashScreen.drawString("Loading resources...");
		writeToLog("Extracting resource cache");
		boolean extracted = CacheResourceLoader.initialize(new File("cache.dat"), appKey);
		if(!extracted)
		{
			writeToLog("Failed to extract resource cache", LoggingType.ERROR);
			JOptionPane.showMessageDialog(null, "Failed to extract data files", "Load error", JOptionPane.ERROR_MESSAGE);
			//SystemUtil.exit(1);
		}
		
		//Locales
		LocaleDatabase.loadLocales("resources.locales", Options.languages);
		LocaleDatabase.setLocale(options.currentLanguage);
		
		//Game path
		SplashScreen.drawString("Finding paths...");
		writeToLog("Initializing LoL file IO");
		GamePathUtil.initialize(options.lolDirPath, Region.stringToRegion(options.lolDirRegion));
		
		//UI
		SplashScreen.drawString("Loading UI...");
		writeToLog("Initializing UI");
		initUI();
		
		//Data
		SplashScreen.drawString("Loading data...");
		
		SplashScreen.drawSubString("Default items");
		BuildFileIO.initDefaultItems(appKey);
		
		writeToLog("Initializing item data");
		SplashScreen.drawSubString("Items");
		boolean success = loadItems();
		writeToLog("Initializing champion data");
		SplashScreen.drawSubString("Champions");
		success &= loadChampions();
		
		if(success)
		{
			setChampion(null);
			
			long loadEndTime = System.nanoTime();
			long diff = (loadEndTime-loadStartTime)/1000;
			long diffMS = diff/1000;
			writeToLog("Startup initialization complete: "+diffMS+" ms ("+(diffMS/1000.0)+" s)");
			//checkGameVersion();
			writeToLog("------------------------------------");
		}
		else
		{
			writeToLog("Failed to initialize", LoggingType.ERROR);
			JOptionPane.showMessageDialog(null, "Failed to initialize the program.\nSee the log for more information.", "Load error", JOptionPane.ERROR_MESSAGE);
			SystemUtil.exit(1);
		}
	}
	
	private void initUI()
	{
		ui = new MainUI(this, options);
		
		//Models
		itemFilterModel = new ItemFilterModel();
		itemFilterModel.addRelatedItemFilterChangedListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent evt)
			{
				Object source = evt.getSource();
				System.out.println("Related item filter changed");
				boolean show = source != ItemFilterModel.ITEM_CLEARED;
				ui.setShowRelatedItemReturn(show);
			}
		});
		
		DefaultComboBoxModel<GameMode> gameModeModel = new DefaultComboBoxModel<GameMode>();
		for(GameMode mode : GameMode.values())
			gameModeModel.addElement(mode);
		
		ui.initModels(itemFilterModel, gameModeModel);
		
		//Listeners
		itemBuildTextListener = new ItemBuildTextListener(){
			@Override
			public void textChanged(ItemBuildTextEvent evt)
			{
				int index = evt.getIndex();
				String text = evt.getText();
				writeToLog("Text change recieved: index="+index);
				writeToLog("Changing from \""+championBuilds.get(index).getName()+"\" to \""+text+"\"", 1);
				championBuilds.get(index).setName(text);
			}
		};
		
		ui.initActions(itemBuildTextListener);
		
		//Set some options
		setItemDisplayMode(options.itemDisplayMode);
	}
	
	public void open()
	{
		ui.open();
		
		/*if(options.firstStartup)
		{
			JOptionPane.showMessageDialog(ui,
					"This update (3.0) is a complete rebuild of the original item changer.\n" +
					"It is lacking a few features that will be added in soon, including:\n" +
					"- Default items\n" +
					"- Game mode item filtering\n",
					"Release Info", JOptionPane.INFORMATION_MESSAGE);
		}*/
	}
	
	public void close()
	{
		//main.checkSave();
		Options.saveOptions(options);
		SystemUtil.exit(0);
	}
	
	//GUI interface methods
	
		//Main UI methods
	
	public void setChampion(Champion champion)
	{
		checkSave();
		
		Champion oldChampion = currentChampion;
		currentChampion = champion;
		
		currentBuildIndex = -1;
		oldBuildIndex = -1;
		
		writeToLog("Setting Champion to \""+champion+"\", (old is \""+oldChampion+"\")");
		
		ui.setChampion(currentChampion);
		loadChampionBuilds();
		
		setChampionItemFilters(currentChampion, oldChampion);
	}
	
	private void setChampionItemFilters(Champion currentChampion, Champion oldChampion)
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
	
	public void addItemFilter(ItemProperty filter)
	{
		itemFilterModel.addItemPropertyFilter(filter);
	}
	
	public void removeItemFilter(ItemProperty filter)
	{
		itemFilterModel.removeItemPropertyFilter(filter);
	}
	
	public void setItemTextFilter(String text)
	{
		itemFilterModel.setTextFilter(text);
	}
	
	public void setGameMode(GameMode mode)
	{
		currentGameMode = mode;
		ui.setGameMode(currentGameMode);
		
		//Update item filter model
		for(GameMode remove : GameMode.values())
			itemFilterModel.removeItemPropertyFilter(remove.getItemProperty());
		itemFilterModel.addItemPropertyFilter(mode.getItemProperty());
		
		//Load builds for mode
		loadChampionBuilds();
	}
	
	public void setBuild(int index)
	{
		writeToLog("Setting build to index: "+index);
		//System.out.println("Old before: "+oldBuildIndex);
		oldBuildIndex = currentBuildIndex;
		//System.out.println("Old after: "+oldBuildIndex);
		//System.out.println("Current before: "+currentBuildIndex);
		currentBuildIndex = index;
		//System.out.println("Current after: "+currentBuildIndex);
		
		storeBuild(oldBuildIndex);
		
		if(championBuilds != null)
		{
			while(currentBuildIndex >= championBuilds.size()) //dunno lol, duct tape. Figured out why once, but now I forget.
				currentBuildIndex--;
			
			ItemBuild build = championBuilds.get(currentBuildIndex);
			ui.setBuild(build);
			
			//if(oldBuildIndex == -1)
			//	oldBuildIndex = currentBuildIndex;
			//System.out.println("Old updated: "+oldBuildIndex);
		}
		else
		{
			currentBuildIndex = -1;
			oldBuildIndex = -1;
			ui.setBuild(null);
		}
	}
	
	public void addBuild(String name)
	{
		writeToLog("Adding new build");
		
		name = name == null ? "New build"+(championBuilds != null ? " "+(championBuilds.size()+1) : "") : name;
		
		ItemBuild build = new ItemBuild(name, currentGameMode.getMapID(), currentGameMode.getModeID());
		String[] names = {"Starting", "Essential", "Offensive", "Defensive"};
		for(int n = 0; n < 4; n++)
		{
			ItemGroup set = new ItemGroup(names[n]/*+(championBuilds != null ? " "+(championBuilds.size()+1) : "")*/);
			build.addGroup(set);
		}
		
		//If no item sets are loaded, create a new list and remove the default text from the combobox
		if(championBuilds == null)
		{
			writeToLog("List is empty, creating", 1);
			championBuilds = new ArrayList<ItemBuild>();
			ui.clearBuilds();
		}
		championBuilds.add(build);
		
		ui.addBuild(name);
	}
	
	public void removeBuild()
	{
		writeToLog("Removing build: "+currentBuildIndex);
		currentBuildIndex = ui.removeBuild(currentBuildIndex);
		championBuilds.remove(currentBuildIndex == -1 ? 0 : currentBuildIndex);
		
		if(championBuilds.size() == 0)
		{
			championBuilds = null;
			ui.setBuilds(null);
		}
	}
	
	public void duplicateBuild()
	{
		if(championBuilds != null && currentBuildIndex >= 0)
		{
			ItemBuild build = championBuilds.get(currentBuildIndex);
			addBuild(build.getName()+" (Copy)");
			ui.setBuild(build);
		}
	}
	
	public void addBuildGroup()
	{
		writeToLog("Adding new build group");
		ItemGroup group = new ItemGroup("New Group");
		championBuilds.get(currentBuildIndex).addGroup(group);
		ui.addBuildGroup(group);
	}
	
	public void removeBuildGroup(int index)
	{
		writeToLog("Removing build group at index: "+index);
		championBuilds.get(currentBuildIndex).removeGroup(index);
		ui.removeBuildGroup(index);
	}
	
	public void save()
	{
		storeBuild(currentBuildIndex);
		
		writeToLog("Saving item builds");
		SaveError error = SaveError.UNKNOWN;
		String errorText = null;
		
		//Check if any data is invalid
		if(championBuilds != null)
		{
			for(ItemBuild build : championBuilds)
			{
				if("".equalsIgnoreCase(build.getName()))
					errorText = "Build names cannot be empty.";
				for(ItemGroup group : build.getGroups())
				{
					if("".equals(group.getName()))
						errorText = "Group names cannot be empty.";
				}
				if(errorText == null && "riot".equalsIgnoreCase(build.getType()))
					errorText = "Build types cannot be \"riot\".";
			}
		}
		
		if(errorText != null)
			error = SaveError.DATA;
		
		//Write builds to file
		else
		{
			try
			{
				error = BuildFileIO.saveBuilds(currentChampion, currentGameMode, championBuilds, currentBuildIndex);
			}
			catch(Exception e)
			{
				error = SaveError.WRITE;
				writeToLog("Failed to save item builds", 1, LoggingType.ERROR);
				writeStackTrace(e);
			}
		}
		
		//Parse errors
		boolean success;
		if(!(success = error == SaveError.NONE))
		{
			switch(error)
			{
				case DATA: break;
				case WRITE: errorText = "Failed to write to file."; break;
				case UNKNOWN: errorText = "An unknown error has occured."; break;
			}
			writeToLog("Save error: error="+error+", text=\""+errorText+"\"");
			ui.setSaveError(errorText);
		}
		ui.setSaveSuccess(success);
	}
	
	public void reset()
	{
		if(championBuilds != null && currentBuildIndex >= 0)
		{
			ItemBuild build = championBuilds.get(currentBuildIndex);
			ui.setBuild(build);
		}
	}
	
	public void resetToDefaults()
	{
		if(championBuilds != null && currentBuildIndex >= 0)
		{
			ItemBuild build = championBuilds.get(currentBuildIndex);
			ItemBuild defaultBuild = BuildFileIO.loadDefaultBuild(currentChampion, currentGameMode);
			defaultBuild.setName(build.getName());
			defaultBuild.setAuthor(build.getAuthor());
			defaultBuild.setDescription(build.getDescription());
			defaultBuild.setPriority(build.isPriority());
			defaultBuild.setType(build.getType());
			ui.setBuild(defaultBuild);
		}
	}
	
	public void clearBuild()
	{
		if(championBuilds != null && currentBuildIndex >= 0)
		{
			ItemBuild build = championBuilds.get(currentBuildIndex);
			ItemBuild clearedBuild = new ItemBuild(build.getName(), build.getMap(), build.getMode());
			clearedBuild.setAuthor(build.getAuthor());
			clearedBuild.setDescription(build.getDescription());
			clearedBuild.setPriority(build.isPriority());
			clearedBuild.setType(build.getType());
			ui.setBuild(clearedBuild);
		}
	}
	
	public void clearRelatedItemFilter()
	{
		itemFilterModel.clearRelatedItemFilter();
	}
	
		//Secondary UI methods
	
	public void nextItemDisplayMode()
	{
		setItemDisplayMode(options.itemDisplayMode = options.itemDisplayMode.getNextMode());
	}
	
		//Menu methods
	
	public void importItemSets()
	{
		//TODO
	}
	
	public void exportItemSets()
	{
		//TODO
	}
	
	public void exportCode()
	{
		//TODO
	}
	
	public void importCode()
	{
		//TODO
	}
	
	public void updateCheck()
	{
		UpdateUtil.startUpdater(appKey, version, buildVersion, true);
	}
	
	public void updateCacheCheck()
	{
		if(UpdateUtil.startCacheUpdater(true))
		{
			loadChampions();
			loadItems();
		}
	}
	
	public void editPath()
	{
		GamePath gameDir = GamePathUtil.getDir();
		
		if(!options.lolDirHistory.contains(gameDir))
			options.lolDirHistory.addFirst(gameDir);
		
		GamePath path = PathDialog.open(ui, Platform.getCurrentPlatform(), gameDir, options.lolDirHistory.toArray(new GamePath[0]));
		
		if(options.lolDirHistory.contains(path))
			options.lolDirHistory.remove(path);
		options.lolDirHistory.addFirst(path);
		if(options.lolDirHistory.size() > 5)
			options.lolDirHistory.removeLast();
		GamePathUtil.setLoLDir(path, true);
	}
	
	public void restoreBackup()
	{
		//TODO
	}
	
	public void setItemDisplayMode(ItemDisplayMode mode)
	{
		writeToLog("Setting item display mode to: "+mode);
		options.itemDisplayMode = mode;
		
		ui.setItemDisplayMode(options.itemDisplayMode);
	}
	
	public void setTooltipsEnabled(boolean enabled)
	{
		//TODO
	}
	
	//Data methods
	
	private boolean loadItems()
	{
		writeToLog("Initializing database", 1);
		boolean success = ItemDatabase.initDatabase();
		//success &= DefaultItemDatabase.initDatabase();
		
		if(success)
		{
			//Sort items
			writeToLog("Sorting item list", 1);
			Set<String> items = ItemDatabase.getItems();
			ArrayList<Item> sortedItems = new ArrayList<Item>(items.size());
			for(String itemID : items)
			{
				Item item = ItemDatabase.getItem(itemID);
				sortedItems.add(item);
			}
			Collections.sort(sortedItems);
			
			//Add items to UI
			writeToLog("Initializing item panel", 1);
			itemFilterModel.addItemPropertyFilter(ItemProperty.CLASSIC_MODE);
			ui.setItems(sortedItems);
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
			//Sort champions by display name
			Set<String> champIDs = ChampionDatabase.getChampions();
			allChampions = new ArrayList<String>(champIDs.size());
			for(String id : champIDs)
				allChampions.add(ChampionDatabase.getChampion(id).getName());
			Collections.sort(allChampions);
			
			//Add champions to UI
			rebuildChampionsList();
			ChampionListCellRenderer.loadImageCache();
		}
		else
		{
			writeToLog("Failed to load champions", LoggingType.ERROR);
		}
		
		return success;
	}
	
	public void rebuildChampionsList()
	{
		ArrayList<String> remaining = new ArrayList<String>(allChampions);
		
		//Add favorites
		if(options.favoriteChampions.size() > 0)
		{
			Collections.sort(options.favoriteChampions);
			for(String name : options.favoriteChampions)
				remaining.remove(name);
		}
		
		//Add all champions
		ui.setChampions(options.favoriteChampions, remaining);
		
		//Required dialogs
		//CopyBuildDialog.initChampions(allChampions);
	}
	
	public void loadChampionBuilds()
	{
		writeToLog("Loading builds for Champion \""+currentChampion+"\", "+currentGameMode);
		championBuilds = currentChampion != null ? BuildFileIO.loadCustomBuilds(currentChampion, currentGameMode) : null;
		if(championBuilds == null)
		{
			writeToLog("No builds exist", 1);
			boolean loaded = false;
			if(options.useDefaultItems && currentChampion != null && currentGameMode != null)
			{
				ItemBuild defaultBuild = BuildFileIO.loadDefaultBuild(currentChampion, currentGameMode);
				if(loaded = defaultBuild != null)
				{
					defaultBuild.setName("Default Items");
					
					championBuilds = new ArrayList<ItemBuild>(1);
					championBuilds.add(defaultBuild);
					ArrayList<String> names = new ArrayList<String>(1);
					names.add(defaultBuild.getName());
					
					currentBuildIndex = -1;
					ui.setBuilds(names);
					ui.enableBuildEditing(true);
					
					loaded = true;
				}
			}
			
			if(!loaded)
			{
				championBuilds = null;
				currentBuildIndex = -1;
				ui.setBuilds(null);
				//ui.setBuild(null);
				ui.enableBuildEditing(false);
			}
		}
		else
		{
			writeToLog("Builds exist, size="+championBuilds.size(), 1);
			List<String> names = new ArrayList<String>(championBuilds.size());
			for(ItemBuild build : championBuilds)
			{
				writeToLog("Build: "+build.getName(), 2);
				for(ItemGroup group : build.getGroups())
					writeToLog("Group: "+group.getName(), 3);
				names.add(build.getName());
			}
			//currentBuild = 0;
			ui.setBuilds(names);
			//ui.setBuild(championBuilds.get(0));
			ui.enableBuildEditing(true);
		}
	}
	
	public void storeBuild(int toIndex)
	{
		if(toIndex != -1)
		{
			writeToLog("Storing current build to index: "+toIndex);
			ItemBuild build = ui.getBuild(toIndex, currentGameMode);
			writeToLog("Current build: "+build, 1);
			if(championBuilds != null)
			{
				if(toIndex == championBuilds.size())
					writeToLog("Seem to be trying to save the removed build", 1, LoggingType.WARNING);
				championBuilds.set(toIndex, build);
			}
		}
	}
	
	private void checkSave()
	{
		if(changeMade && currentChampion != null)
		{
			int selection = options.saveDefault;
			if(options.showSaveWarning)
			{
				//TODO: localization
				JCheckBox checkbox = new JCheckBox("Do not show");//getString("dialog.doNotShow"));
				checkbox.setFont(UIManager.getFont("OptionPane.font"));
				String message = "Do you want to save before quitting?";//getString("dialog.unsaved.line1")+"\n"+getString("dialog.unsaved.line2");
				Object[] params = {message, checkbox};
				int n = JOptionPane.showConfirmDialog(ui, params, "Save?"/*getString("dialog.unsaved.title")*/, JOptionPane.YES_NO_CANCEL_OPTION);
				if(n != JOptionPane.CANCEL_OPTION)
				{
					options.showSaveWarning = !checkbox.isSelected();
					if(!options.showSaveWarning)
						options.saveDefault = n;
					selection = n;
				}
			}
			
			if(selection == JOptionPane.YES_OPTION)
				save();				
		}
		changeMade = false;
	}
	
	public void makeChange()
	{
		changeMade = true;
	}
}