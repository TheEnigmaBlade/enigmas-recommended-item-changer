package net.enigmablade.lol.lolitem.ui.components.items;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import net.enigmablade.paradoxion.ui.components.translucent.*;

import net.enigmablade.lol.lollib.ui.pretty.*;

import net.enigmablade.lol.lolitem.ui.dnd.*;
import net.enigmablade.lol.lolitem.ui.models.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public abstract class DraggableItemContainer extends TranslucentPanel
{
	protected DraggableItemContainerModel model;
	private boolean enabled;
	
	protected int itemSize = 60;
	protected int itemGap = 10;
	
	private String emptyText = "No items found";
	private Font emptyFont;
		
	private ActionListener changeListener;
	
	private boolean showReturn = false;
	private JPanel returnPanel;
	private JButton returnButton;
	
	public DraggableItemContainer(DraggableItemContainerModel m, ActionListener changeList, ActionListener returnList)
	{
		super(0);
		
		model = m;
		model.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent unused)
			{
				updateContents(enabled);
			}
		});
		changeListener = changeList;
		
		setForeground(new Color(134, 137, 147, 100));
		
		emptyFont = getFont().deriveFont(Font.PLAIN, 38.0f);
		
		returnPanel = new JPanel();
		returnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
		returnPanel.setOpaque(false);
		
		returnButton = new PrettyButton("Return");
		returnButton.addActionListener(returnList);
		returnPanel.add(returnButton);
		
		new ItemDropTargetListener();
	}
	
	//Display updating
	
	protected void addComponent(Component c)
	{
		((DraggableItem)c).setChangeListener(changeListener);
		super.add(c);
	}
	
	protected void clearComponents()
	{
		removeAll();
	}
	
	public void updateContents(boolean enable)
	{
		enabled = enable;
		clearComponents();
		if(enabled)
		{
			for(DraggableItem item : model.getItems())
				addComponent(item);
		}
		
		if(showReturn)
		{
			returnPanel.setPreferredSize(new Dimension(getWidth(), 30));
			returnPanel.setSize(new Dimension(getWidth(), 30));
			add(returnPanel);
		}
		
		refreshPanel();
	}
	
	protected abstract void updateSize();
	
	public void refreshPanel()
	{
		updateSize();
		revalidate();
		repaint();
	}
	
	//Other
	
	public void setReturnShown(boolean show)
	{
		showReturn = show;
		updateContents(enabled);
	}
	
	public void setItemSize(int size)
	{
		itemSize = size;
		itemGap = (int)((1.0*itemSize/60)*10);
		
		model.setItemSize(size);
		
		refreshPanel();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(model.getSize() == 0)
		{
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(emptyFont);
			g.setColor(getForeground());
			g.drawString(emptyText, (getWidth()/2)-(g.getFontMetrics().stringWidth(emptyText)/2), (getHeight()/2)-g.getFontMetrics().getDescent());
		}
	}
	
	//Drag-and-drop stuff
	
	private class ItemDropTargetListener extends DropTargetAdapter
	{
		public ItemDropTargetListener()
		{
			new DropTarget(DraggableItemContainer.this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
		}
		
		public void drop(DropTargetDropEvent evt)
		{
			try
			{
				Transferable tr = evt.getTransferable();
				int action = evt.getDropAction();
				
				if(action == DnDConstants.ACTION_MOVE)
				{
					if(evt.isDataFlavorSupported(TransferableItem.removableItemFlavor))
					{
						DraggableItem item = (DraggableItem)tr.getTransferData(TransferableItem.removableItemFlavor);
						BuildPanel oldPanel = item.getBuildParent();
						writeToLog("Item removed from BuildPanel \""+oldPanel.getName()+"\"");
						
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						oldPanel.removeItem(item);
						evt.dropComplete(true);
						
						if(changeListener != null)
							changeListener.actionPerformed(new ActionEvent(DraggableItemContainer.this, ActionEvent.ACTION_PERFORMED, ""));
						
						return;
					}
				}
				else if(action == DnDConstants.ACTION_COPY)
				{
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					evt.dropComplete(true);
					return;
				}
				evt.rejectDrop();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				evt.rejectDrop();
			}
		}
	}
	
	public void setChangeListener(ActionListener list)
	{
		changeListener = list;
	}
}
