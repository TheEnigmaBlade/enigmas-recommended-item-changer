package enigma.lol.lolitem.gui.dialogs;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

import enigma.paradoxion.localization.*;
import enigma.paradoxion.ui.models.*;

import enigma.lol.lolitem.gui.renderers.*;

public class FavoritesDialog extends JDialog
{
	private static FavoritesDialog instance;
	
	private JLabel championsLabel, favoriteChampionsLabel;
	private JList<String> championsList, favoritesList;
	private JLabel addLabel, removeLabel;
	private JButton addButton, removeButton;
	private JButton saveButton, cancelButton;
	
	private ArrayList<String> allBackup, favBackup;
	
	private FavoritesDialog()
	{
		initComponents();
	}
	
	private void initComponents()
	{
		setMinimumSize(new Dimension(350, 180));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("Set Favorite Champions");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				cancel();
			}
		});
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		setContentPane(contentPane);
		
		contentPane.setPreferredSize(new Dimension(350, 300));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel);
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_mainPanel.rowHeights = new int[]{0, 0, 0};
		gbl_mainPanel.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_mainPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		mainPanel.setLayout(gbl_mainPanel);
		
		championsLabel = new JLabel("Champions");
		championsLabel.setPreferredSize(new Dimension(0, 14));
		GridBagConstraints gbc_championsLabel = new GridBagConstraints();
		gbc_championsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_championsLabel.insets = new Insets(0, 0, 2, 6);
		gbc_championsLabel.gridx = 0;
		gbc_championsLabel.gridy = 0;
		mainPanel.add(championsLabel, gbc_championsLabel);
		championsLabel.setFont(championsLabel.getFont().deriveFont(championsLabel.getFont().getStyle() | Font.BOLD, championsLabel.getFont().getSize() + 2f));
		championsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		favoriteChampionsLabel = new JLabel("Favorites");
		favoriteChampionsLabel.setPreferredSize(new Dimension(0, 14));
		GridBagConstraints gbc_favoriteChampionsLabel = new GridBagConstraints();
		gbc_favoriteChampionsLabel.insets = new Insets(0, 0, 2, 0);
		gbc_favoriteChampionsLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_favoriteChampionsLabel.gridx = 2;
		gbc_favoriteChampionsLabel.gridy = 0;
		mainPanel.add(favoriteChampionsLabel, gbc_favoriteChampionsLabel);
		favoriteChampionsLabel.setFont(favoriteChampionsLabel.getFont().deriveFont(favoriteChampionsLabel.getFont().getStyle() | Font.BOLD, favoriteChampionsLabel.getFont().getSize() + 2f));
		favoriteChampionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane championsScrollPane = new JScrollPane();
		championsScrollPane.setPreferredSize(new Dimension(0, 0));
		championsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_championsScrollPane = new GridBagConstraints();
		gbc_championsScrollPane.fill = GridBagConstraints.BOTH;
		gbc_championsScrollPane.insets = new Insets(0, 0, 0, 6);
		gbc_championsScrollPane.gridx = 0;
		gbc_championsScrollPane.gridy = 1;
		mainPanel.add(championsScrollPane, gbc_championsScrollPane);
		
		championsList = new JList<String>();
		championsList.setModel(new SortedListModel<String>());
		championsList.setCellRenderer(new ChampionListCellRenderer(false));
		championsScrollPane.setViewportView(championsList);
		
		JScrollPane favoritesScrollPane = new JScrollPane();
		favoritesScrollPane.setPreferredSize(new Dimension(0, 0));
		favoritesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_favoritesScrollPane = new GridBagConstraints();
		gbc_favoritesScrollPane.fill = GridBagConstraints.BOTH;
		gbc_favoritesScrollPane.gridx = 2;
		gbc_favoritesScrollPane.gridy = 1;
		mainPanel.add(favoritesScrollPane, gbc_favoritesScrollPane);
		
		favoritesList = new JList<String>();
		favoritesList.setModel(new SortedListModel<String>());
		favoritesList.setCellRenderer(new ChampionListCellRenderer(false));
		favoritesScrollPane.setViewportView(favoritesList);
		
		JPanel centerPanel = new JPanel();
		GridBagConstraints gbc_centerPanel = new GridBagConstraints();
		gbc_centerPanel.gridheight = 2;
		gbc_centerPanel.insets = new Insets(0, 0, 0, 6);
		gbc_centerPanel.gridx = 1;
		gbc_centerPanel.gridy = 0;
		mainPanel.add(centerPanel, gbc_centerPanel);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		centerPanel.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{19, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		addLabel = new JLabel("Add");
		GridBagConstraints gbc_addLabel = new GridBagConstraints();
		gbc_addLabel.gridx = 0;
		gbc_addLabel.gridy = 0;
		panel.add(addLabel, gbc_addLabel);
		addLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		addButton = new JButton("\u2212\u2212>");
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(0, 0, 8, 0);
		gbc_addButton.gridx = 0;
		gbc_addButton.gridy = 1;
		panel.add(addButton, gbc_addButton);
		addButton.setFont(addButton.getFont().deriveFont(addButton.getFont().getSize() + 1f));
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				transferSelected(championsList, favoritesList);
			}
		});
		addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		removeLabel = new JLabel("Remove");
		GridBagConstraints gbc_removeLabel = new GridBagConstraints();
		gbc_removeLabel.gridx = 0;
		gbc_removeLabel.gridy = 2;
		panel.add(removeLabel, gbc_removeLabel);
		removeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		removeButton = new JButton("<\u2212\u2212");
		GridBagConstraints gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.gridx = 0;
		gbc_removeButton.gridy = 3;
		panel.add(removeButton, gbc_removeButton);
		removeButton.setFont(removeButton.getFont().deriveFont(removeButton.getFont().getSize() + 1f));
		removeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				transferSelected(favoritesList, championsList);
			}
		});
		removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonsPanel = new JPanel();
		bottomPanel.add(buttonsPanel, BorderLayout.EAST);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		saveButton = new JButton("Save");
		buttonsPanel.add(saveButton);
		saveButton.setPreferredSize(new Dimension(86, 23));
		getRootPane().setDefaultButton(saveButton);
		
		cancelButton = new JButton("Cancel");
		buttonsPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				cancel();
			}
		});
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				save();
			}
		});
		
		pack();
	}
	
	public static void reloadText()
	{
		if(instance != null)
		{
			instance.setTitle(LocaleDatabase.getString("dialog.favorites.title"));
			instance.championsLabel.setText(LocaleDatabase.getString("dialog.favorites.champions"));
			instance.favoriteChampionsLabel.setText(LocaleDatabase.getString("dialog.favorites.favorites"));
			instance.addLabel.setText(LocaleDatabase.getString("dialog.favorites.add"));
			instance.removeLabel.setText(LocaleDatabase.getString("dialog.favorites.remove"));
			instance.saveButton.setText(LocaleDatabase.getString("dialog.favorites.save"));
			instance.cancelButton.setText(LocaleDatabase.getString("dialog.favorites.cancel"));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void transferSelected(JList<String> from, JList<String> to)
	{
		Object[] selected;
		if((selected = (Object[])from.getSelectedValues()).length > 0)
		{
			SortedListModel<String> removeModel = (SortedListModel<String>)from.getModel();
			SortedListModel<String> addModel = (SortedListModel<String>)to.getModel();
			for(int n = selected.length-1; n >= 0; n--)
			{
				selected = (Object[])from.getSelectedValues();
				String value = (String)selected[n];
				removeModel.removeElement(value);
				addModel.addElement(value);
			}
		}
	}
	
	private void setLists(List<String> all, List<String> fav)
	{
		SortedListModel<String> allModel = (SortedListModel<String>)(championsList.getModel());
		allModel.clear();
		for(String s : all)
			if(!fav.contains(s))
				allModel.addElement(s);
		
		SortedListModel<String> favModel = (SortedListModel<String>)(favoritesList.getModel());
		favModel.clear();
		for(String s : fav)
			favModel.addElement(s);
	}
	
	private List<String> getFavorites()
	{
		return new ArrayList<String>(favBackup);
	}
	
	private void save()
	{
		allBackup.clear();
		SortedListModel<String> allModel = (SortedListModel<String>)championsList.getModel();
		for(int n = 0; n < allModel.getSize(); n++)
			allBackup.add((String)allModel.getElementAt(n));
		
		favBackup.clear();
		SortedListModel<String> favModel = (SortedListModel<String>)favoritesList.getModel();
		for(int n = 0; n < favModel.getSize(); n++)
			favBackup.add((String)favModel.getElementAt(n));
		
		dispose();
	}
	
	private void cancel()
	{
		setLists(allBackup, favBackup);
		dispose();
	}
	
	public static List<String> editFavorites(Component parent, List<String> all, List<String> fav)
	{
		if(instance == null)
		{
			instance = new FavoritesDialog();
			reloadText();
			instance.setLists(all, fav);
			instance.allBackup = new ArrayList<String>(all);
			instance.favBackup = new ArrayList<String>(fav);
		}
		
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
		
		return instance.getFavorites();
	}
}
