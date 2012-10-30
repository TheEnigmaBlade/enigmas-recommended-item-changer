package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.border.*;

import enigma.paradoxion.ui.*;
import enigma.paradoxion.util.*;
import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;
import enigma.lol.lollib.io.*;

import enigma.lol.lolitem.data.filter.*;
import enigma.lol.lolitem.gui.dnd.*;

public class ItemSetPanel extends ItemPanel
{
	private int id;
	
	private Image itemImage;
	private int size;
	
	private ActionListener changeListener;
	private ItemFilterModel itemFilterModel;
	
	public ItemSetPanel(int id, Item i, ItemFilterModel model)
	{
		this(id, i, model, 60);
	}
	
	public ItemSetPanel(int id, Item i, ItemFilterModel model, int s)
	{
		this.id = id;
		itemFilterModel = model;
		size = s;
		
		setPreferredSize(new Dimension(size, size));
		setSize(new Dimension(size, size));
		setBorder(new LineBorder(Color.GRAY, 2));
		setBackground(GUIUtil.addAlpha(Color.GRAY, 125));
		setOpaque(false);
		
		setItem(i);
		
		new ItemDropTargetListener(this);
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				if(item != null && itemFilterModel != null)
				{
					writeToLog("Item clicked: "+item+" from "+item.getComponentItems()+" into "+item.getParentItems());
					itemFilterModel.setRelatedItemFilter(getItem());
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(itemImage, 0, 0, this);
	}
	
	//Drag and drop stuff
	
	private class ItemDropTargetListener extends DropTargetAdapter
	{
		private ItemSetPanel panel;
		
		public ItemDropTargetListener(ItemSetPanel panel)
		{
			this.panel = panel;
			
			new DropTarget(panel, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
		}
		
		
		public void drop(DropTargetDropEvent evt)
		{
			try
			{
				Transferable tr = evt.getTransferable();
				int action = evt.getDropAction();
				
				if(action == DnDConstants.ACTION_COPY)
				{
					Item item = (Item)tr.getTransferData(TransferableItem.itemFlavor);
					writeToLog("Item dropped to slot "+id+": "+item);
					
					if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_COPY);
						panel.setItem(item);
						evt.dropComplete(true);
						if(changeListener != null)
							changeListener.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, null));
						return;
					}
				}
				else if(action == DnDConstants.ACTION_MOVE)
				{
					ItemSetPanel oldPanel = (ItemSetPanel)tr.getTransferData(TransferableItem.panelFlavor);
					writeToLog("ItemPanel dropped to slot "+id+": "+oldPanel.getItem());
					
					if(evt.isDataFlavorSupported(TransferableItem.panelFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						Item oldItem = panel.getItem();
						panel.setItem(oldPanel.getItem());
						oldPanel.setItem(oldItem);
						evt.dropComplete(true);
						if(changeListener != null)
							changeListener.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, null));
						return;
					}
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
	
	//Accessor methods
	
	public void setItem(Item i)
	{
		item = i;
		if(item != null)
		{
			itemImage = GamePathUtil.getItemImage(item.getImage());
			if(itemImage == null)
				itemImage = ResourceLoader.getImage("null.png");
			
			itemImage = itemImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
			
			setToolTipText();
		}
		else
		{
			itemImage = null;
			setToolTipText();
		}
		
		repaint();
	}
	
	public int getID()
	{
		return id;
	}
}
