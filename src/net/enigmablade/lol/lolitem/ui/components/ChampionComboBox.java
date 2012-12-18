package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.enigmablade.paradoxion.localization.*;
import net.enigmablade.paradoxion.ui.components.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.pretty.*;
import net.enigmablade.lol.lollib.ui.renderers.*;

import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.models.*;


import static net.enigmablade.paradoxion.util.Logger.*;

public class ChampionComboBox extends PrettyComboBox<String>
{
	private FocusTextField championsFilterField;
	private JCheckBox assassinCheckBox, carryCheckBox, fighterCheckBox, mageCheckBox, tankCheckBox, supportCheckBox;
	
	private ChampionListCellRenderer renderer;
	private ChampionFilterModel filterModel;
	private ChampionComboBoxModel model;
	
	private boolean updateFilters = false;
	
	public ChampionComboBox(boolean showSelected)
	{
		super(null, NORTH);
		
		filterModel = new ChampionFilterModel();
		model = new ChampionComboBoxModel(filterModel);
		setModel(model);
		
		initComponents();
		setRenderer(renderer = new ChampionListCellRenderer(showSelected));
		addPopupMenuListener(new PopupMenuListener(){
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent evt)
			{
				updateFilters = true;
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent evt)
			{
				championsFilterField.setText("");
				filterModel.setTextFilter("");
				assassinCheckBox.setSelected(false);
				carryCheckBox.setSelected(false);
				fighterCheckBox.setSelected(false);
				mageCheckBox.setSelected(false);
				tankCheckBox.setSelected(false);
				supportCheckBox.setSelected(false);
				updateFilters = false;
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent evt)
			{
				updateFilters = false;
			}
		});
	}
	
	private void initComponents()
	{
		JPanel championsFilterPanel = new JPanel();
		championsFilterPanel.setBackground(UIUtil.COMPONENT_BASE);
		championsFilterPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.black), new EmptyBorder(2, 2, 2, 2)));
		championsFilterPanel.setLayout(new BorderLayout());
		
		championsFilterField = new FocusTextField("Filter champions by name");
		championsFilterField.getDocument().addDocumentListener(new DocumentListener(){
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
				if(updateFilters)
				{
					Document doc = evt.getDocument();
					try
					{
						String value = doc.getText(0, doc.getLength());
						filterModel.setTextFilter(value);
					}
					catch(BadLocationException e)
					{
						writeToLog("Error when updating champion text filter", LoggingType.ERROR);
						writeStackTrace(e);
					}
				}
			}
		});
		championsFilterPanel.add(championsFilterField, BorderLayout.NORTH);
		
		ItemListener championFilter = new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				String com = ((JCheckBox)e.getSource()).getActionCommand();
				ChampionProperty newFilter = ChampionProperty.commandToFilter(com);
				
				if(e.getStateChange() == ItemEvent.SELECTED)
					filterModel.addPropertyFilter(newFilter);
				else
					filterModel.removePropertyFilter(newFilter);
			}
		};
		
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridBagLayout());
		championsFilterPanel.add(checkBoxPanel, BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.ipady = -2;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridy = 0;
		assassinCheckBox = new PrettyCheckBox("Assassin");
		assassinCheckBox.addItemListener(championFilter);
		assassinCheckBox.setActionCommand("assassin");
		c.gridx = 0;
		checkBoxPanel.add(assassinCheckBox, c.clone());
		carryCheckBox = new PrettyCheckBox("Carry");
		carryCheckBox.addItemListener(championFilter);
		carryCheckBox.setActionCommand("carry");
		c.gridx++;
		checkBoxPanel.add(carryCheckBox, c.clone());
		fighterCheckBox = new PrettyCheckBox("Fighter");
		fighterCheckBox.setFocusPainted(false);
		fighterCheckBox.addItemListener(championFilter);
		fighterCheckBox.setActionCommand("fighter");
		c.gridx++;
		checkBoxPanel.add(fighterCheckBox, c.clone());
		c.ipady = -6;
		c.gridy = 1;
		mageCheckBox = new PrettyCheckBox("Mage");
		mageCheckBox.addItemListener(championFilter);
		mageCheckBox.setActionCommand("mage");
		c.gridx = 0;
		checkBoxPanel.add(mageCheckBox, c.clone());
		tankCheckBox = new PrettyCheckBox("Tank");
		tankCheckBox.addItemListener(championFilter);
		tankCheckBox.setActionCommand("tank");
		c.gridx++;
		checkBoxPanel.add(tankCheckBox, c.clone());
		supportCheckBox = new PrettyCheckBox("Support");
		supportCheckBox.addItemListener(championFilter);
		supportCheckBox.setActionCommand("support");
		c.gridx++;
		checkBoxPanel.add(supportCheckBox, c.clone());
		
		setComponent(championsFilterPanel);
	}
	
	public void updateLists(List<String> favorites, List<String> remaining)
	{
		model.updateLists(favorites, remaining);
	}
	
	public void reloadText()
	{
		championsFilterField.setAltText(LocaleDatabase.getString("main.champion.combobox.textboxalt"));
		assassinCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.assassin"));
		carryCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.carry"));
		fighterCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.fighter"));
		mageCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.mage"));
		tankCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.tank"));
		supportCheckBox.setText(LocaleDatabase.getString("main.champion.combobox.support"));
		renderer.reloadText();
	}
}
