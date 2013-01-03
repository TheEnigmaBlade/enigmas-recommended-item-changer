package net.enigmablade.lol.lolitem.ui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import net.enigmablade.paradoxion.util.*;
import net.enigmablade.lol.lollib.ui.pretty.*;
import net.enigmablade.lol.lolitem.*;
import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lolitem.ui.renderers.*;

public class PresetDialog extends JDialog
{
	private JList<ItemBuild> buildList;
	private JList<ItemGroup> groupList;
	private JButton groupUpButton, groupDownButton, groupRenameButton, groupRemoveButton;
	private JButton buildUpButton, buildDownButton, buildRenameButton, buildRemoveButton;
	private JButton saveButton, cancelButton;
	
	private DefaultListModel<ItemBuild> buildListModel;
	private DefaultListModel<ItemGroup> groupListModel;
	
	private boolean canceled = false;
	
	public PresetDialog()
	{
		initComponents();
	}
	
	private void initComponents()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("Manage Presets");
		setIconImage(ResourceLoader.getIcon());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				cancel();
			}
		});
		setBounds(0, 0, 400, 400);
		
		JPanel contentPane = new PrettyBackgroundPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		BorderLayout bl_contentPane = new BorderLayout();
		bl_contentPane.setVgap(2);
		contentPane.setLayout(bl_contentPane);
		setContentPane(contentPane);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BorderLayout());
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		JPanel buttonAlignPanel = new JPanel();
		buttonAlignPanel.setOpaque(false);
		FlowLayout fl_buttonAlignPanel = (FlowLayout) buttonAlignPanel.getLayout();
		fl_buttonAlignPanel.setHgap(2);
		fl_buttonAlignPanel.setVgap(0);
		buttonPanel.add(buttonAlignPanel, BorderLayout.EAST);
		
		saveButton = new PrettyButton("Save");
		saveButton.setPreferredSize(new Dimension(106, 23));
		buttonAlignPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				save();
			}
		});
		getRootPane().setDefaultButton(saveButton);
		
		cancelButton = new PrettyButton("Cancel");
		buttonAlignPanel.add(cancelButton);
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				cancel();
			}
		});
		
		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(2, 0, 0, 2));
		
		JPanel buildPanel = new PrettyPanel();
		buildPanel.setOpaque(false);
		centerPanel.add(buildPanel);
		buildPanel.setLayout(new BorderLayout(2, 0));
		
		JLabel buildLabel = new JLabel("Build Presets");
		buildLabel.setBorder(new EmptyBorder(0, 2, 0, 0));
		buildLabel.setFont(buildLabel.getFont().deriveFont(buildLabel.getFont().getStyle() | Font.BOLD, buildLabel.getFont().getSize() + 1f));
		buildLabel.setHorizontalAlignment(SwingConstants.LEFT);
		buildPanel.add(buildLabel, BorderLayout.NORTH);
		
		JPanel buildButtonPanel = new JPanel();
		buildButtonPanel.setOpaque(false);
		buildButtonPanel.setPreferredSize(new Dimension(90, 0));
		buildPanel.add(buildButtonPanel, BorderLayout.EAST);
		GridBagLayout gbl_buildButtonPanel = new GridBagLayout();
		gbl_buildButtonPanel.columnWidths = new int[]{0, 0};
		gbl_buildButtonPanel.rowHeights = new int[]{0, 0, 16, 0, 16, 0, 0};
		gbl_buildButtonPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_buildButtonPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		buildButtonPanel.setLayout(gbl_buildButtonPanel);
		
		buildUpButton = new PrettyButton("Move Up");
		buildUpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_buildUpButton = new GridBagConstraints();
		gbc_buildUpButton.insets = new Insets(0, 0, 2, 0);
		gbc_buildUpButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildUpButton.gridx = 0;
		gbc_buildUpButton.gridy = 0;
		buildButtonPanel.add(buildUpButton, gbc_buildUpButton);
		
		buildDownButton = new PrettyButton("Move Down");
		buildDownButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_buildDownButton = new GridBagConstraints();
		gbc_buildDownButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildDownButton.gridx = 0;
		gbc_buildDownButton.gridy = 1;
		buildButtonPanel.add(buildDownButton, gbc_buildDownButton);
		
		buildRenameButton = new PrettyButton("Rename");
		buildRenameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ItemBuild build = buildList.getSelectedValue();
				if(build != null)
				{
					String newName = promptName(build.getName());
					if(newName != null && newName.length() > 0)
					{
						build.setName(newName);
						buildList.repaint();
					}
				}
			}
		});
		GridBagConstraints gbc_buildRenameButton = new GridBagConstraints();
		gbc_buildRenameButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildRenameButton.gridx = 0;
		gbc_buildRenameButton.gridy = 3;
		buildButtonPanel.add(buildRenameButton, gbc_buildRenameButton);
		
		buildRemoveButton = new PrettyButton("Remove");
		buildRemoveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_buildRemoveButton = new GridBagConstraints();
		gbc_buildRemoveButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_buildRemoveButton.gridx = 0;
		gbc_buildRemoveButton.gridy = 5;
		buildButtonPanel.add(buildRemoveButton, gbc_buildRemoveButton);
		
		JScrollPane buildScrollPane = new JScrollPane();
		buildPanel.add(buildScrollPane);
		
		buildList = new JList<ItemBuild>();
		buildList.setModel(buildListModel = new DefaultListModel<ItemBuild>());
		buildList.setCellRenderer(new PresetBuildListCellRenderer());
		buildScrollPane.setViewportView(buildList);
		
		JPanel groupPanel = new PrettyPanel();
		groupPanel.setOpaque(false);
		centerPanel.add(groupPanel);
		groupPanel.setLayout(new BorderLayout(2, 0));
		
		JLabel groupLabel = new JLabel("Group Presets");
		groupLabel.setBorder(new EmptyBorder(0, 2, 0, 0));
		groupLabel.setFont(groupLabel.getFont().deriveFont(groupLabel.getFont().getStyle() | Font.BOLD, groupLabel.getFont().getSize() + 1f));
		groupLabel.setHorizontalAlignment(SwingConstants.LEFT);
		groupPanel.add(groupLabel, BorderLayout.NORTH);
		
		JPanel groupButtonPanel = new JPanel();
		groupButtonPanel.setOpaque(false);
		groupButtonPanel.setPreferredSize(new Dimension(90, 0));
		groupPanel.add(groupButtonPanel, BorderLayout.EAST);
		GridBagLayout gbl_groupButtonPanel = new GridBagLayout();
		gbl_groupButtonPanel.columnWidths = new int[]{0, 0};
		gbl_groupButtonPanel.rowHeights = new int[]{0, 0, 16, 0, 16, 0, 0};
		gbl_groupButtonPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_groupButtonPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		groupButtonPanel.setLayout(gbl_groupButtonPanel);
		
		groupUpButton = new PrettyButton("Move Up");
		groupUpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_groupUpButton = new GridBagConstraints();
		gbc_groupUpButton.insets = new Insets(0, 0, 2, 0);
		gbc_groupUpButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_groupUpButton.gridx = 0;
		gbc_groupUpButton.gridy = 0;
		groupButtonPanel.add(groupUpButton, gbc_groupUpButton);
		
		groupDownButton = new PrettyButton("Move Down");
		groupDownButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_groupDownButton = new GridBagConstraints();
		gbc_groupDownButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_groupDownButton.gridx = 0;
		gbc_groupDownButton.gridy = 1;
		groupButtonPanel.add(groupDownButton, gbc_groupDownButton);
		
		groupRenameButton = new PrettyButton("Rename");
		groupRenameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ItemGroup group = groupList.getSelectedValue();
				if(group != null)
				{
					String newName = promptName(group.getName());
					if(newName != null && newName.length() > 0)
					{
						group.setName(newName);
						groupList.repaint();
					}
				}
			}
		});
		GridBagConstraints gbc_groupRenameButton = new GridBagConstraints();
		gbc_groupRenameButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_groupRenameButton.gridx = 0;
		gbc_groupRenameButton.gridy = 3;
		groupButtonPanel.add(groupRenameButton, gbc_groupRenameButton);
		
		groupRemoveButton = new PrettyButton("Remove");
		groupRemoveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				//TODO
			}
		});
		GridBagConstraints gbc_groupRemoveButton = new GridBagConstraints();
		gbc_groupRemoveButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_groupRemoveButton.gridx = 0;
		gbc_groupRemoveButton.gridy = 5;
		groupButtonPanel.add(groupRemoveButton, gbc_groupRemoveButton);
		
		JScrollPane groupScrollPane = new JScrollPane();
		groupPanel.add(groupScrollPane);
		
		groupList = new JList<ItemGroup>();
		groupList.setModel(groupListModel = new DefaultListModel<ItemGroup>());
		groupList.setCellRenderer(new PresetGroupListCellRenderer());
		groupScrollPane.setViewportView(groupList);
	}
	
	private void initData(PresetOptions presets)
	{
		buildListModel.removeAllElements();
		for(String s : presets.buildPresets.keySet())
			buildListModel.addElement(presets.buildPresets.get(s));
		
		groupListModel.removeAllElements();
		for(String s : presets.groupPresets.keySet())
			groupListModel.addElement(presets.groupPresets.get(s));
	}
	
	//Closing
	
	private void cancel()
	{
		canceled = true;
		dispose();
	}
	
	private void save()
	{
		canceled = false;
		dispose();
	}
	
	private PresetOptions getPresets()
	{
		PresetOptions options = new PresetOptions();
		
		ItemBuild temp;
		for(Enumeration<ItemBuild> e = buildListModel.elements(); e.hasMoreElements();)
			options.buildPresets.put((temp = e.nextElement()).getName(), temp);
		
		ItemGroup temp2;
		for(Enumeration<ItemGroup> e = groupListModel.elements(); e.hasMoreElements();)
			options.groupPresets.put((temp2 = e.nextElement()).getName(), temp2);
		
		return options;
	}
	
	//Helpers
	
	private String promptName(String existing)
	{
		return (String)JOptionPane.showInputDialog(this, "Input a new name for the preset:", "Input new name", JOptionPane.QUESTION_MESSAGE, null, null, existing);
	}
	
	//Opening
	
	private static PresetDialog instance;
	
	public static PresetOptions openDialog(Component parent, PresetOptions presets)
	{
		//if(instance == null)
		{
			instance = new PresetDialog();
		}
		
		instance.initData(presets);
		
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
		
		return instance.canceled ? null : instance.getPresets();
	}
}
