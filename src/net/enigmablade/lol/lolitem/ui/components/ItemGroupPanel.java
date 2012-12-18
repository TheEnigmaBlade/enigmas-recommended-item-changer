package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import org.jdesktop.swingx.*;

import net.enigmablade.paradoxion.ui.components.*;

import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.pretty.*;
import net.enigmablade.lol.lollib.data.*;

import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.components.items.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class ItemGroupPanel 
{
	private JPanel topPanel;
	private JXCollapsiblePane collapsiblePane;
	private JPanel spacer;
	
	private EditableLabel nameLabel;
	private JButton collapseButton;
	private JButton removeButton;
	
	private JScrollPane contentScrollPane;
	private BuildPanel content;
	
	public ItemGroupPanel(String name, ItemFilterModel itemFilterModel)
	{
		initComponents(itemFilterModel);
		initActions();
		
		nameLabel.setText(name);
	}
	
	private void initComponents(ItemFilterModel itemFilterModel)
	{
		//Top panel
		topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(0, 22));
		topPanel.setOpaque(false);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		topPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel tabPanel = new JPanel(){
			@Override
			public void paintComponent(Graphics g)
			{
				int adj = collapsiblePane.isCollapsed() ? 0 : 1;
				g.setColor(UIUtil.COMPONENT_BASE);
				g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1+adj, 4, 4);
				g.setColor(UIUtil.COMPONENT_BORDER);
				g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1+adj, 4, 4);
			}
		};
		tabPanel.setBorder(new EmptyBorder(0, 0, 0, 4));
		topPanel.add(tabPanel, BorderLayout.WEST);
		tabPanel.setLayout(new BorderLayout(4, 0));
		
		nameLabel = new EditableLabel("");
		nameLabel.setCaretColor(UIUtil.FOREGROUND);
		tabPanel.add(nameLabel, BorderLayout.CENTER);
		
		collapseButton = new PrettyButton();
		collapseButton.setText("\u2212");
		collapseButton.setOpaque(false);
		collapseButton.setFocusPainted(false);
		collapseButton.setBorder(null);
		collapseButton.setPreferredSize(new Dimension(20, 20));
		tabPanel.add(collapseButton, BorderLayout.WEST);
		
		JPanel shrinkingPanel = new JPanel();
		shrinkingPanel.setLayout(new BorderLayout());
		shrinkingPanel.setBorder(new EmptyBorder(2, 4, 4, 2));
		shrinkingPanel.setPreferredSize(new Dimension(22, 20));
		shrinkingPanel.setOpaque(false);
		topPanel.add(shrinkingPanel, BorderLayout.EAST);
		
		removeButton = new PrettyButton("x");
		removeButton.setOpaque(false);
		removeButton.setFocusPainted(false);
		removeButton.setBorder(null);
		shrinkingPanel.add(removeButton, BorderLayout.CENTER);
		
		//Content
		collapsiblePane = new JXCollapsiblePane();
		collapsiblePane.setPreferredSize(new Dimension(0, 100));
		collapsiblePane.setOpaque(false);
		
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBar(new PrettyScrollBar(PrettyScrollBar.VERTICAL));
		contentScrollPane.setPreferredSize(new Dimension(0, 100));
		contentScrollPane.setBorder(new LineBorder(UIUtil.COMPONENT_BORDER, 1, false));
		collapsiblePane.setContentPane(contentScrollPane);
		
		content = new BuildPanel(itemFilterModel);
		contentScrollPane.setViewportView(content);
		
		//Spacer
		spacer = new JPanel();
		spacer.setOpaque(false);
		spacer.setPreferredSize(new Dimension(0, 6));
	}
	
	private void initActions()
	{
		collapseButton.addActionListener(collapsiblePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
		collapseButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				collapseButton.setText(collapsiblePane.isCollapsed() ? "\u2212" : "+");
			}
		});
	}
	
	public Component[] getComponents()
	{
		return new Component[]{topPanel, collapsiblePane, spacer};
	}
	
	public void addComponentListener(ComponentListener list)
	{
		collapsiblePane.addComponentListener(list);
	}
	
	public void removeComponentListener(ComponentListener list)
	{
		collapsiblePane.removeComponentListener(list);
	}
	
	public void addRemoveListener(ActionListener list)
	{
		removeButton.addActionListener(list);
	}
	
	public void removeRemoveListener(ActionListener list)
	{
		removeButton.removeActionListener(list);
	}
	
	public void setIndex(int index)
	{
		removeButton.setActionCommand(index+"");
	}
	
	//Accessor methods
	
	public void setItems(ItemGroup set)
	{
		List<Item> items = set.getItems();
		List<Integer> itemCounts = set.getItemCounts();
		for(int n = 0; n < items.size(); n++)
		{
			Item i = items.get(n);
			int count = itemCounts.get(n);
			writeToLog(i+" ("+count+")", 3);
			content.addItem(i, count);
		}
	}
	
	public ItemGroup getItems()
	{
		return new ItemGroup(nameLabel.getText(), content.getItems(), content.getItemCounts());
	}
	
	public void refreshPanel()
	{
		content.refreshPanel();
	}
}
