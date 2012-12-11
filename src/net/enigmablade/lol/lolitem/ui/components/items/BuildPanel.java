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

import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.dnd.*;

public class BuildPanel extends JPanel implements DragGestureListener
{
	private ArrayList<DraggableItem> items;
	private int gap = 4;
	
	private DragSource dragSource;
	
	private String emptyText = "Drag items here";
	private Font emptyFont;
	
	private int itemSize = 42;
	
	private ItemFilterModel itemFilterModel;
	
	public BuildPanel(ItemFilterModel itemFilterModel)
	{
		this.itemFilterModel = itemFilterModel;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, gap, gap));
		//setBackground(new Color(194, 197, 203));
		//setBackground(getBackground().darker());
		setBackground(new Color(40, 40, 40));
		setForeground(new Color(70, 70, 70));
		
		setBorder(new EmptyBorder(0, 12, 0, 12));
		
		emptyFont = getFont().deriveFont(16);
		
		dragSource = new DragSource();
		new ItemDropTargetListener();
		
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
	
	private class ItemDropTargetListener extends DropTargetAdapter
	{
		public ItemDropTargetListener()
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
						Item item = (Item)tr.getTransferData(TransferableItem.itemFlavor);
						
						evt.acceptDrop(DnDConstants.ACTION_COPY);
						addItem(item);
						evt.dropComplete(true);
						return;
					}
					evt.dropComplete(false);
				}
				else if(action == DnDConstants.ACTION_MOVE)
				{
					if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
					{
						DraggableItem item = (DraggableItem)tr.getTransferData(TransferableItem.itemFlavor);
						BuildPanel source = (BuildPanel)item.getBuildParent();
						
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						addItem(item.getItem());
						source.removeItem(item);
						evt.dropComplete(true);
						return;
					}
					evt.dropComplete(false);
				}
				evt.rejectDrop();
			}
			catch(Exception e)
			{
				writeToLog("Error occured when dragging from BuildPanel", LoggingType.ERROR);
				writeStackTrace(e);
				evt.rejectDrop();
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
			System.out.println("Item size: "+itemWidth+"x"+itemHeight);
			int numCols = (getWidth()-gap)/(itemWidth+gap);
			System.out.println("Num cols: "+numCols);
			int numRows = items.size()/numCols+(items.size() > numCols && items.size()%numCols > 0 ? 1 : 0);
			System.out.println("Num rows: "+numRows);
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
		DraggableItem item = new DraggableItem(i, itemFilterModel, true);
		item.setBuildParent(this);
		dragSource.createDefaultDragGestureRecognizer(item, DnDConstants.ACTION_MOVE, this);
		item.setItemSize(itemSize, itemSize);
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
}
