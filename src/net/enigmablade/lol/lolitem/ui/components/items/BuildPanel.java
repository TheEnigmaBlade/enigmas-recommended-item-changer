package net.enigmablade.lol.lolitem.ui.components.items;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lollib.data.*;

import net.enigmablade.lol.lolitem.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.components.*;
import net.enigmablade.lol.lolitem.ui.dnd.*;

public class BuildPanel extends JPanel implements DragGestureListener
{
	private EnigmaItems main;
	private ItemGroupPanel parentItemGroup;
	
	private ArrayList<DraggableItem> items;
	private int gap = 2;
	
	private DragSource dragSource;
	
	private String emptyText = "Drag items here";
	private Font emptyFont;
	
	private int itemSize = 38;
	
	private ItemFilterModel itemFilterModel;
	
	public BuildPanel(EnigmaItems main, ItemGroupPanel parentItemGroup, ItemFilterModel itemFilterModel)
	{
		this.main = main;
		this.parentItemGroup = parentItemGroup;
		this.itemFilterModel = itemFilterModel;
		
		setLayout(new FlowLayout(FlowLayout.CENTER, gap, gap));
		//setBackground(new Color(194, 197, 203));
		//setBackground(getBackground().darker());
		setBackground(new Color(40, 40, 40));
		setForeground(new Color(70, 70, 70));
		
		setBorder(new EmptyBorder(2, 2, 0, 0));
		
		emptyFont = getFont().deriveFont(16);
		
		dragSource = new DragSource();
		new BuildPanelDropTargetListener();
		
		items = new ArrayList<DraggableItem>();
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				requestFocus();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(items.size() == 0)
		{
			Color c = getForeground();
			Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 175);
			g.setColor(c2);
			g.setFont(emptyFont);
			g.drawString(emptyText, (getWidth()/2)-(g.getFontMetrics().stringWidth(emptyText)/2), (getHeight()/2)+(emptyFont.getSize()/4));
		}
	}
	
	//Drag and drop stuff
	
	private class BuildPanelDropTargetListener extends DropTargetAdapter
	{
		public BuildPanelDropTargetListener()
		{
			new DropTarget(BuildPanel.this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
		}
		
		public void drop(DropTargetDropEvent evt)
		{
			try
			{
				Transferable tr = evt.getTransferable();
				int action = evt.getDropAction();
				
				if(action == DnDConstants.ACTION_COPY)
				{
					if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_COPY);
						
						Item item = (Item)tr.getTransferData(TransferableItem.itemFlavor);
						addItem(item);
						
						evt.dropComplete(true);
						return;
					}
				}
				else if(action == DnDConstants.ACTION_MOVE)
				{
					if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						
						DraggableItem item = (DraggableItem)tr.getTransferData(TransferableItem.itemFlavor);
						BuildPanel source = (BuildPanel)item.getBuildParent();
						addItem(item.getItem());
						source.removeItem(item);
						
						evt.dropComplete(true);
						return;
					}
					else if(evt.isDataFlavorSupported(TransferableGroup.itemGroupFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						
						ItemGroupPanel group = (ItemGroupPanel)tr.getTransferData(TransferableGroup.itemGroupFlavor);
						main.swapBuildGroups(parentItemGroup.getIndex(), group.getIndex());
						
						evt.dropComplete(true);
						return;
					}
				}
				evt.rejectDrop();
				evt.dropComplete(false);
			}
			catch(Exception e)
			{
				writeToLog("Error occured when dropping on BuildPanel", LoggingType.ERROR);
				writeStackTrace(e);
				evt.rejectDrop();
				evt.dropComplete(false);
			}
		}
	}
	
	@Override
	public void dragGestureRecognized(DragGestureEvent evt)
	{
		Cursor cursor = null;
		Component com = evt.getComponent();
		
		if(com instanceof DraggableItem)
		{
			DraggableItem item = (DraggableItem)com;
			
			if(evt.getDragAction() == DnDConstants.ACTION_MOVE)
				cursor = DragSource.DefaultMoveDrop;
			
			evt.startDrag(cursor, new TransferableItem(item));
		}
	}
	
	//GUI methods
	
	private void updateSize()
	{
		if(items.size() == 0)
			setPreferredSize(new Dimension(0, 0));
		else if(getWidth() > 0)
		{
			int itemWidth = items.get(0).getWidth();
			int itemHeight = items.get(0).getHeight();
			//System.out.println("Item size: "+itemWidth+"x"+itemHeight);
			int numCols = (getWidth()-gap)/(itemWidth+gap);
			//System.out.println("Num cols: "+numCols);
			int numRows = items.size()/numCols+(items.size() > numCols && items.size()%numCols > 0 ? 1 : 0);
			//System.out.println("Num rows: "+numRows);
			setPreferredSize(new Dimension(0, (numRows*itemHeight)+((numRows+1)*gap)));
		}
	}
	
	public void refreshPanel()
	{
		updateSize();
		revalidate();
		repaint();
	}
	
	//Accessor methods
	
	public void addItem(Item i)
	{
		addItem(i, 1);
	}
	
	public void addItem(Item i, int count)
	{
		DraggableItem item = new DraggableItem(i, itemFilterModel, true);
		item.setBuildParent(this);
		dragSource.createDefaultDragGestureRecognizer(item, DnDConstants.ACTION_MOVE, this);
		item.setItemSize(itemSize, itemSize);
		item.setItemCount(count);
		items.add(item);
		add(item);
		
		refreshPanel();
	}
	
	public void addItemAt(Item i, DraggableItem at)
	{
		DraggableItem item = new DraggableItem(i, itemFilterModel, true);
		item.setBuildParent(this);
		dragSource.createDefaultDragGestureRecognizer(item, DnDConstants.ACTION_MOVE, this);
		item.setItemSize(itemSize, itemSize);
		
		int index = items.indexOf(at);
		if(index == items.size()-1)
			items.add(item);
		else
			items.add(index, item);
		
		removeAll();
		for(DraggableItem temp : items)
			add(temp);
		
		refreshPanel();
	}
	
	public void removeItem(DraggableItem i)
	{
		items.remove(i);
		remove(i);
		
		refreshPanel();
	}
	
	public void clear()
	{
		items.clear();
		removeAll();
		
		refreshPanel();
	}
	
	public List<Item> getItems()
	{
		List<Item> list = new ArrayList<Item>(items.size());
		for(DraggableItem di : items)
			list.add(di.getItem());
		return list;
	}
	
	public List<Integer> getItemCounts()
	{
		List<Integer> list = new ArrayList<Integer>(items.size());
		for(DraggableItem di : items)
			list.add(di.getItemCount());
		return list;
	}
}
