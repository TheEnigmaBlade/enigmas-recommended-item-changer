package enigma.lol.lolitem.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import enigma.lol.lolitem.data.*;

public class BuildChooserDialog extends JDialog
{
	private static BuildChooserDialog instance;
	private JTree buildTree;
	private JButton importButton;
	private JButton cancelButton;
	private JLabel selectedTitleLabel;
	private JLabel selectedLabel;
	
	public BuildChooserDialog()
	{
		initComponents();
	}
	
	public void initComponents()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setResizable(true);
		setTitle("Select builds to import");
		setBounds(0, 0, 300, 350);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		setContentPane(contentPane);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(2);
		borderLayout.setHgap(2);
		contentPane.setLayout(borderLayout);
		
		JScrollPane buildTreeScrollPane = new JScrollPane();
		getContentPane().add(buildTreeScrollPane, BorderLayout.CENTER);
		
		buildTree = new JTree();
		buildTree.setShowsRootHandles(true);
		buildTree.setRootVisible(false);
		
	    buildTree.setCellRenderer(new CheckBoxNodeRenderer());

	    buildTree.setCellEditor(new CheckBoxNodeEditor(buildTree));
	    buildTree.setEditable(true);
		
		buildTreeScrollPane.setViewportView(buildTree);
		
		JPanel bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonsPanel = new JPanel();
		bottomPanel.add(buttonsPanel, BorderLayout.EAST);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		importButton = new JButton("Import");
		importButton.setPreferredSize(new Dimension(86, 23));
		buttonsPanel.add(importButton);
		
		cancelButton = new JButton("Cancel");
		buttonsPanel.add(cancelButton);
		
		JPanel selectedPanel = new JPanel();
		bottomPanel.add(selectedPanel, BorderLayout.WEST);
		FlowLayout fl_selectedPanel = new FlowLayout(FlowLayout.LEADING, 2, 6);
		selectedPanel.setLayout(fl_selectedPanel);
		
		selectedTitleLabel = new JLabel("Selected:");
		selectedPanel.add(selectedTitleLabel);
		
		selectedLabel = new JLabel("0");
		selectedPanel.add(selectedLabel);
	}
	
	public static void reloadText()
	{
		if(instance != null)
		{
			
		}
	}
	
	private class CheckBoxNodeRenderer implements TreeCellRenderer
	{
		private JCheckBox leafRenderer = new JCheckBox();
		private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
		private Color /*selectionBorderColor, */selectionForeground, selectionBackground, textForeground, textBackground;
		
		public CheckBoxNodeRenderer()
		{
			Font fontValue;
			fontValue = UIManager.getFont("Tree.font");
			if(fontValue != null)
			{
				leafRenderer.setFont(fontValue);
			}
			Boolean booleanValue = (Boolean)UIManager.get("Tree.drawsFocusBorderAroundIcon");
			leafRenderer.setFocusPainted((booleanValue != null)	&& (booleanValue.booleanValue()));
			
			//selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
			selectionForeground = UIManager.getColor("Tree.selectionForeground");
			selectionBackground = UIManager.getColor("Tree.selectionBackground");
			textForeground = UIManager.getColor("Tree.textForeground");
			textBackground = UIManager.getColor("Tree.textBackground");
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			Component returnValue;
			if(leaf)
			{
				String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
				leafRenderer.setText(stringValue);
				leafRenderer.setSelected(false);
				
				leafRenderer.setEnabled(tree.isEnabled());
				
				if(selected)
				{
					leafRenderer.setForeground(selectionForeground);
					leafRenderer.setBackground(selectionBackground);
				}
				else
				{
					leafRenderer.setForeground(textForeground);
					leafRenderer.setBackground(textBackground);
				}
				
				if((value != null) && (value instanceof DefaultMutableTreeNode))
				{
					Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
					if(userObject instanceof CheckBoxNode)
					{
						CheckBoxNode node = (CheckBoxNode)userObject;
						leafRenderer.setText(node.getText());
						leafRenderer.setSelected(node.isSelected());
					}
				}
				returnValue = leafRenderer;
			}
			else
			{
				returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
			return returnValue;
		}
		
		protected JCheckBox getLeafRenderer()
		{
			return leafRenderer;
		}
	}
	
	private class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor
	{
		private CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		//private ChangeEvent changeEvent = null;
		private JTree buildTree;
		
		public CheckBoxNodeEditor(JTree tree)
		{
			this.buildTree = tree;
		}
		
		public Object getCellEditorValue()
		{
			JCheckBox checkbox = renderer.getLeafRenderer();
			CheckBoxNode checkBoxNode = new CheckBoxNode(checkbox.getText(), checkbox.isSelected());
			return checkBoxNode;
		}
		
		public boolean isCellEditable(EventObject event)
		{
			boolean returnValue = false;
			if(event instanceof MouseEvent)
			{
				MouseEvent mouseEvent = (MouseEvent) event;
				TreePath path = buildTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
				if(path != null)
				{
					Object node = path.getLastPathComponent();
					if((node != null) && (node instanceof DefaultMutableTreeNode))
					{
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
						Object userObject = treeNode.getUserObject();
						returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
					}
				}
			}
			return returnValue;
		}
		
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
		{
			Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
			
			// editor always selected / focused
			ItemListener itemListener = new ItemListener()
			{
				public void itemStateChanged(ItemEvent itemEvent)
				{
					if(stopCellEditing())
					{
						fireEditingStopped();
					}
				}
			};
			if(editor instanceof JCheckBox)
			{
				((JCheckBox)editor).addItemListener(itemListener);
			}
			
			return editor;
		}
	}
	
	private class CheckBoxNode
	{
		private String text;
		private boolean selected;
		
		public CheckBoxNode(String text, boolean selected) 
		{
			this.text = text;
			this.selected = selected;
		}
		
		public boolean isSelected()
		{
			return selected;
		}
		
		/*public void setSelected(boolean newValue)
		{
			selected = newValue;
		}*/
		
		public String getText()
		{
			return text;
		}
		
		/*public void setText(String newValue)
		{
			text = newValue;
		}*/
		
		public String toString()
		{
			return getClass().getName() + "[" + text + "/" + selected + "]";
		}
	}
	
	public static ItemBuilds openDialog(Component parent, ItemBuilds builds)
	{
		if(instance == null)
			instance = new BuildChooserDialog();
		
		//set items
		
		instance.setLocationRelativeTo(parent);
		instance.setVisible(true);
		
		ItemBuilds toSave = new ItemBuilds();
		//get items
		return toSave;
	}
}
