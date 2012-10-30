package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.event.*;

import enigma.paradoxion.ui.components.translucent.*;
import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lolitem.gui.dnd.*;
import enigma.lol.lolitem.gui.models.*;

public abstract class DraggableItemContainer extends TranslucentPanel
{
	protected DraggableItemContainerModel model;
	private boolean enabled;
	
	protected int itemSize = 60;
	protected int itemGap = 10;
	
	private String emptyText = "No items found";
	private Font emptyFont;
		
	private ActionListener changeListener;
	
	public DraggableItemContainer(DraggableItemContainerModel m, ActionListener list)
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
		changeListener = list;
		
		setForeground(new Color(134, 137, 147, 100));
		
		emptyFont = getFont().deriveFont(Font.PLAIN, 38.0f);
		
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
			g.setColor(getForeground());
			g.setFont(emptyFont);
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
					ItemSetPanel oldPanel = (ItemSetPanel)tr.getTransferData(TransferableItem.panelFlavor);
					writeToLog("Item removed from ItemPanel "+oldPanel.getID());
					
					if(evt.isDataFlavorSupported(TransferableItem.panelFlavor))
					{
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						oldPanel.setItem(null);
						evt.dropComplete(true);
						if(changeListener != null)
							changeListener.actionPerformed(new ActionEvent(DraggableItemContainer.this, ActionEvent.ACTION_PERFORMED, ""));
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
}
