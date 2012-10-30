package enigma.lol.lolitem.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.localization.*;
import enigma.paradoxion.util.*;

import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.data.*;
import enigma.lol.lolitem.gui.renderers.*;
import enigma.lol.lolitem.io.*;
import enigma.lol.lolitem.gui.components.*;

public class CopyBuildDialog extends JDialog
{
	private static CopyBuildDialog instance;
	
	private ChampionImagePanel championImagePanel;
	private JLabel championLabel, buildLabel;
	private ChampionComboBox championComboBox;
	private JComboBox<String> buildComboBox;
	private BuildListCellRenderer buildCellRenderer;
	private ItemSetPanel[] itemPanels;
	private JButton copyButton, cancelButton;
	
	private List<ItemSet> items;
	private int selectedIndex = -1;
	private GameMode gameMode;
	
	public CopyBuildDialog()
	{
		initComponents();
	}
	
	private void initComponents()
	{
		setTitle("Select Build to Copy");
		setIconImage(ResourceLoader.getIcon());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				selectedIndex = -1;
				close();
			}
		});
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(0, 0, 376, 190);
		setResizable(false);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(4, 2, 2, 2));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		copyButton = new JButton("Copy");
		copyButton.setPreferredSize(new Dimension(86, 23));
		copyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				close();
			}
		});
		buttonPanel.add(copyButton);
		copyButton.setEnabled(false);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				selectedIndex = -1;
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWidths = new int[]{0, 0};
		gbl_mainPanel.rowHeights = new int[]{60, 0, 0};
		gbl_mainPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_mainPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		mainPanel.setLayout(gbl_mainPanel);
		
		JPanel topPanel = new JPanel();
		GridBagConstraints gbc_topPanel = new GridBagConstraints();
		gbc_topPanel.fill = GridBagConstraints.VERTICAL;
		gbc_topPanel.insets = new Insets(0, 0, 5, 0);
		gbc_topPanel.gridx = 0;
		gbc_topPanel.gridy = 0;
		mainPanel.add(topPanel, gbc_topPanel);
		topPanel.setLayout(new BorderLayout(5, 0));
		
		JPanel setsPanel = new JPanel();
		topPanel.add(setsPanel);
		GridBagLayout gbl_setsPanel = new GridBagLayout();
		gbl_setsPanel.columnWidths = new int[]{51, 115, 0};
		gbl_setsPanel.rowHeights = new int[]{0, 0, 0};
		gbl_setsPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_setsPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		setsPanel.setLayout(gbl_setsPanel);
		
		championLabel = new JLabel("Champion:");
		GridBagConstraints gbc_championLabel = new GridBagConstraints();
		gbc_championLabel.anchor = GridBagConstraints.EAST;
		gbc_championLabel.insets = new Insets(0, 0, 2, 2);
		gbc_championLabel.gridx = 0;
		gbc_championLabel.gridy = 0;
		setsPanel.add(championLabel, gbc_championLabel);
		championLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		championComboBox = new ChampionComboBox(true);
		GridBagConstraints gbc_championComboBox = new GridBagConstraints();
		gbc_championComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_championComboBox.insets = new Insets(0, 0, 2, 0);
		gbc_championComboBox.gridx = 1;
		gbc_championComboBox.gridy = 0;
		setsPanel.add(championComboBox, gbc_championComboBox);
		championComboBox.setMaximumRowCount(16);
		championComboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					changeChampion();
				}
			}
		});
		
		buildLabel = new JLabel("Build:");
		GridBagConstraints gbc_newLabelLabel = new GridBagConstraints();
		gbc_newLabelLabel.anchor = GridBagConstraints.EAST;
		gbc_newLabelLabel.insets = new Insets(0, 0, 0, 2);
		gbc_newLabelLabel.gridx = 0;
		gbc_newLabelLabel.gridy = 1;
		setsPanel.add(buildLabel, gbc_newLabelLabel);
		buildLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		buildComboBox = new JComboBox<String>();
		buildComboBox.setRenderer(buildCellRenderer = new BuildListCellRenderer());
		GridBagConstraints gbc_buildComboBox = new GridBagConstraints();
		gbc_buildComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildComboBox.gridx = 1;
		gbc_buildComboBox.gridy = 1;
		setsPanel.add(buildComboBox, gbc_buildComboBox);
		buildComboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent evt)
			{
				if(evt.getStateChange() == ItemEvent.SELECTED)
				{
					changeBuild();
				}
			}
		});
		buildComboBox.setEnabled(false);
		buildComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"---empty"}));
		
		championImagePanel = new ChampionImagePanel(null, 60);
		championImagePanel.setBorder(new LineBorder(Color.GRAY, 2));
		topPanel.add(championImagePanel, BorderLayout.WEST);
		championImagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel itemsPanel = new JPanel();
		itemsPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_itemsPanel = new GridBagConstraints();
		gbc_itemsPanel.fill = GridBagConstraints.BOTH;
		gbc_itemsPanel.gridx = 0;
		gbc_itemsPanel.gridy = 1;
		mainPanel.add(itemsPanel, gbc_itemsPanel);
		GridBagLayout gbl_itemsPanel = new GridBagLayout();
		gbl_itemsPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_itemsPanel.rowHeights = new int[]{0, 0};
		gbl_itemsPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_itemsPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		itemsPanel.setLayout(gbl_itemsPanel);
		
		itemPanels = new ItemSetPanel[6];
		
		ItemSetPanel itemPanel1 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel1 = new GridBagConstraints();
		gbc_itemPanel1.insets = new Insets(0, 2, 0, 2);
		gbc_itemPanel1.gridx = 0;
		gbc_itemPanel1.gridy = 0;
		itemsPanel.add(itemPanel1, gbc_itemPanel1);
		itemPanels[0] = itemPanel1;
		
		ItemSetPanel itemPanel2 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel2 = new GridBagConstraints();
		gbc_itemPanel2.insets = new Insets(0, 0, 0, 2);
		gbc_itemPanel2.gridx = 1;
		gbc_itemPanel2.gridy = 0;
		itemsPanel.add(itemPanel2, gbc_itemPanel2);
		itemPanels[1] = itemPanel2;
		
		ItemSetPanel itemPanel3 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel3 = new GridBagConstraints();
		gbc_itemPanel3.insets = new Insets(0, 0, 0, 2);
		gbc_itemPanel3.gridx = 2;
		gbc_itemPanel3.gridy = 0;
		itemsPanel.add(itemPanel3, gbc_itemPanel3);
		itemPanels[2] = itemPanel3;
		
		ItemSetPanel itemPanel4 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel4 = new GridBagConstraints();
		gbc_itemPanel4.insets = new Insets(0, 0, 0, 2);
		gbc_itemPanel4.gridx = 3;
		gbc_itemPanel4.gridy = 0;
		itemsPanel.add(itemPanel4, gbc_itemPanel4);
		itemPanels[3] = itemPanel4;
		
		ItemSetPanel itemPanel5 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel5 = new GridBagConstraints();
		gbc_itemPanel5.insets = new Insets(0, 0, 0, 2);
		gbc_itemPanel5.gridx = 4;
		gbc_itemPanel5.gridy = 0;
		itemsPanel.add(itemPanel5, gbc_itemPanel5);
		itemPanels[4] = itemPanel5;
		
		ItemSetPanel itemPanel6 = new ItemSetPanel(0, null, null, 50);
		GridBagConstraints gbc_itemPanel6 = new GridBagConstraints();
		gbc_itemPanel6.insets = new Insets(0, 0, 0, 2);
		gbc_itemPanel6.gridx = 5;
		gbc_itemPanel6.gridy = 0;
		itemsPanel.add(itemPanel6, gbc_itemPanel6);
		itemPanels[5] = itemPanel6;
		
		pack();
	}
	
	public static void reloadText()
	{
		if(instance != null)
		{
			instance.setTitle(LocaleDatabase.getString("dialog.copybuild.title"));
			instance.championLabel.setText(LocaleDatabase.getString("dialog.copybuild.champion")+":");
			instance.championComboBox.reloadText();
			instance.buildLabel.setText(LocaleDatabase.getString("dialog.copybuild.build")+":");
			instance.buildCellRenderer.reloadText();
			instance.copyButton.setText(LocaleDatabase.getString("dialog.copybuild.copy"));
			instance.cancelButton.setText(LocaleDatabase.getString("dialog.copybuild.cancel"));
		}
	}
	
	public static void initChampions(List<String> allChampions)
	{
		if(instance == null)
			instance = new CopyBuildDialog();
		instance.championComboBox.updateLists(null, allChampions);
	}
	
	private void changeMode(GameMode mode)
	{
		gameMode = mode;
	}
	
	private void changeChampion()
	{
		int selected = championComboBox.getSelectedIndex();
		boolean enable = selected > 0;
		if(enable)
		{
			String name = (String)championComboBox.getSelectedItem();
			String key = ChampionDatabase.getChampionKey(name);
			Champion champion = ChampionDatabase.getChampion(key);
			championImagePanel.setChampion(champion);
			
			items = ItemFileIO.getItems(champion, gameMode);
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
			if(items != null)
			{
				model.addElement("---select");
				for(ItemSet set : items)
					model.addElement(set.getName());
			}
			else
			{
				model.addElement("---empty");
				enable = false;
			}
			buildComboBox.setModel(model);
		}
		else
		{
			championImagePanel.setChampion(null);
		}
		
		for(int n = 0; n < 6; n++)
			itemPanels[n].setItem(null);
		
		buildComboBox.setSelectedIndex(0);
		buildComboBox.setEnabled(enable);
	}
	
	private void changeBuild()
	{
		int selected = buildComboBox.getSelectedIndex();
		selectedIndex = selected-1;
		boolean enable = selected > 0;
		if(enable && items != null)
		{
			ItemSet set = items.get(selectedIndex);
			for(int n = 0; n < 6; n++)
				itemPanels[n].setItem(set.getItem(n));
		}
		else
		{
			for(int n = 0; n < 6; n++)
				itemPanels[n].setItem(null);
		}
		
		copyButton.setEnabled(enable);
	}
	
	private void reset()
	{
		championComboBox.setSelectedIndex(0);
	}
	
	private void close()
	{
		dispose();
	}
	
	private ItemSet getSelectedSet()
	{
		return selectedIndex >= 0 ? items.get(selectedIndex) : null;
	}
	
	public static ItemSet open(Component parent, GameMode gameMode)
	{
		if(instance == null)
		{
			instance = new CopyBuildDialog();
			reloadText();
		}
		else
			instance.reset();
		instance.changeMode(gameMode);
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
		
		return instance.getSelectedSet();
	}
}
