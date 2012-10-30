package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import enigma.paradoxion.ui.*;
import enigma.paradoxion.util.*;
import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;
import enigma.lol.lollib.io.*;

import enigma.lol.lolitem.data.filter.*;
import enigma.lol.lolitem.gui.dnd.*;
import enigma.lol.lolitem.gui.tooltip.*;

public class DraggableItem extends ItemPanel
{
	private int originalWidth, width, originalHeight, height;
	private Map<String, String> stats;
	private Image baseItemImage, itemImage;
	private int cornerSizeX, cornerSizeY;
	private Area clip;
	
	protected enum RenderMode {GRID, LIST};
	private RenderMode renderMode = RenderMode.GRID;
	
	private static JPopupMenu contextMenu;
	private static JMenuItem addMenuItem, filterMenuItem;
	
	//Information
	private static Font listFont, listFontSmall, listFontBold;
	
	private int glowSize = 4;
	private static Color glowColor = new Color(255, 255, 200), darkGlowColor = new Color(220, 220, 100);
	private Shape[] glowOutlines;
	private Color[] glowColors, darkGlowColors;
	private boolean showGlow = false, showDarkGlow = false;
	
	private ActionListener changeListener;
	private ItemFilterModel itemFilterModel;
	
	static
	{
		//Information
		listFont = new Font("Ariel", Font.PLAIN, 12);
		listFontSmall = listFont.deriveFont(11.0f);
		listFontBold = listFont.deriveFont(Font.BOLD);
		
		contextMenu = new JPopupMenu();
		addMenuItem = new JMenuItem("Add item to set");
		contextMenu.add(addMenuItem);
		filterMenuItem = new JMenuItem("Show related items");
		contextMenu.add(filterMenuItem);
	}
	
	public DraggableItem(Item i, ItemFilterModel model)
	{
		item = i;
		itemFilterModel = model;
		if(item != null)
		{
			itemImage = baseItemImage = GamePathUtil.getItemImage(item.getImage());
			setToolTipText(item.getToolTip());
		}
		if(item == null || itemImage == null)
			itemImage = baseItemImage = ResourceLoader.getImage("null.png").getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		
		stats = item != null ? item.getStats() : new HashMap<String, String>();
		
		setOpaque(false);
		
		originalWidth = baseItemImage.getWidth(null);
		originalHeight = baseItemImage.getHeight(null);
		setItemSize(originalWidth, originalHeight);
		
		addMouseListener(new DraggableItemMouseListener());
		
		new ItemDropTargetListener();
	}
	
	private class DraggableItemMouseListener extends MouseAdapter
	{
		private boolean doubleClicked = false;
		
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			int button = evt.getButton();
			int clicks = evt.getClickCount();
			if(evt.isPopupTrigger() || button == MouseEvent.BUTTON3)
			{
				for(ActionListener l : addMenuItem.getActionListeners())
					addMenuItem.removeActionListener(l);
				for(ActionListener l : filterMenuItem.getActionListeners())
					filterMenuItem.removeActionListener(l);
				addMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						addItemAction();
					}
				});
				filterMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						itemFilterAction();
					}
				});
				contextMenu.show(DraggableItem.this, evt.getX(), evt.getY());
			}
			else if(button == MouseEvent.BUTTON1)
			{
				//contextMenu.setVisible(false);
				if(clicks == 2)
				{
					doubleClicked = true;
					addItemAction();
				}
				else
				{
					int interval = 300;//(Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval")/2;
					Timer timer = new Timer(interval, new ActionListener(){
						public void actionPerformed(ActionEvent evt)
						{
							if(doubleClicked)
								doubleClicked = false;
							else
							{
								itemFilterAction();
								mouseAction(false);
							}
						}    
					});
					timer.setRepeats(false);
					timer.start();
				}
			}
		}
		
		@Override
		public void mousePressed(MouseEvent evt)
		{
			mouseAction(true);
			showDarkGlow = true;
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
		
		@Override
		public void mouseReleased(MouseEvent evt)
		{
			mouseAction(true);
			setCursor(null);
		}
		
		@Override
		public void mouseEntered(MouseEvent evt)
		{
			mouseAction(true);
			setCursor(null);
		}
		
		@Override
		public void mouseExited(MouseEvent evt)
		{
			mouseAction(false);
			setCursor(null);
		}
		
		private void mouseAction(boolean sG)
		{
			showGlow = sG;
			showDarkGlow = false;
			repaint();
		}
	}
	
	private void addItemAction()
	{
		Item item = getItem();
		writeToLog("Item double clicked: "+item);
		if(changeListener != null)
			changeListener.actionPerformed(new ActionEvent(DraggableItem.this, ActionEvent.ACTION_PERFORMED, "item:"+item.getID()));
	}
	
	private void itemFilterAction()
	{
		Item item = getItem();
		writeToLog("Item clicked: "+item+", from "+item.getComponentItems()+" into "+item.getParentItems());
		itemFilterModel.setRelatedItemFilter(getItem());
	}
	
	@Override
	public void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D)g1;
		
		if(renderMode == RenderMode.LIST)
		{
			//Background
			g.setColor(GUIUtil.addAlpha(getBackground(), showGlow ? 150 : 75));
			g.fillRect(width-cornerSizeX, 0, getWidth(), height);
			
			g.setColor(Color.black);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			int xStart = width+cornerSizeX+2;
			//Name
			g.setFont(listFontBold);
			g.drawString(item.getName(), xStart, 14);
			//Cost
			g.setFont(listFontSmall);
			g.drawString(": "+item.getCost()+" gold", xStart+1+g.getFontMetrics(listFontBold).stringWidth(item.getName()), 14);
			
			//Stats
			g.setFont(listFontSmall);
			FontMetrics metrics = g.getFontMetrics();
			int xOff = xStart+5;
			int yOff = (int)(height*0.5)+11;
			for(String key : stats.keySet())
			{
				String typeString = "+"+stats.get(key)+TooltipUtil.getTypeSeparator(key);
				g.setColor(getForeground());
				g.drawString(typeString, xOff, yOff);
				String valueString = TooltipUtil.getShortType(key);
				g.setPaint(TooltipUtil.getTypePaint(key));
				g.drawString(valueString, xOff+=SwingUtilities.computeStringWidth(metrics, typeString), yOff);
				xOff+=SwingUtilities.computeStringWidth(metrics, valueString)+4;
			}
		}
		
		//Draw item image
		Area oldClip = new Area(g.getClip());
		Area newClip = (Area)oldClip.clone();
		newClip.subtract(clip);
		newClip.exclusiveOr(oldClip);
		g.setClip(newClip);
		g.drawImage(itemImage, 0, 0, this);
		
		if(showGlow)
		{
			g.setStroke(new BasicStroke(2));
			for(int n = 0; n < glowSize; n++)
			{
				Color c = showDarkGlow ? darkGlowColors[n] : glowColors[n];
				g.setColor(c);
				g.draw(glowOutlines[n]);
			}
		}
		
		g.setClip(oldClip);
	}
	
	private void initCorners()
	{
		cornerSizeX = (int)Math.ceil(width/12.0);
		cornerSizeY = (int)Math.ceil(height/12.0);
		int[] x = {0, cornerSizeX, width-cornerSizeX, width, width, width-cornerSizeX, cornerSizeX, 0};
		int[] y = {cornerSizeY, 0, 0, cornerSizeY, height-cornerSizeY, height, height, height-cornerSizeY};
		clip = new Area(new Polygon(x, y, 8));
		
		glowOutlines = new Shape[glowSize];
		glowColors = new Color[glowSize];
		darkGlowColors = new Color[glowSize];
		int alphaSub = 255/glowSize;
		for(int n = 0; n < glowSize; n++)
		{
			int xAdj = n%2 == 0 ? 1 : 0;
			int yAdj = n%2 == 0 ? 0 : 1;
			int[] gx = {n, cornerSizeX+xAdj, width-cornerSizeX-xAdj, width-n, width-n, width-cornerSizeX-xAdj, cornerSizeX+xAdj, n};
			int[] gy = {cornerSizeY+yAdj, n, n, cornerSizeY+yAdj, height-cornerSizeY-yAdj, height-n, height-n, height-cornerSizeY-yAdj};
			glowOutlines[n] = new Polygon(gx, gy, 8);
			glowColors[n] = new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 255-(n*alphaSub));
			darkGlowColors[n] = new Color(darkGlowColor.getRed(), darkGlowColor.getGreen(), darkGlowColor.getBlue(), 255-(n*alphaSub));
		}
	}
	
	//Drag-and-drop stuff
	
	private class ItemDropTargetListener extends DropTargetAdapter
	{
		public ItemDropTargetListener()
		{
			new DropTarget(DraggableItem.this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
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
							changeListener.actionPerformed(new ActionEvent(DraggableItem.this, ActionEvent.ACTION_PERFORMED, ""));
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
	
	public void setItemSize(int w, int h)
	{
		width = w;
		height = h;
		
		if(width == originalWidth && height == originalHeight)
			itemImage = baseItemImage;
		else
			itemImage = baseItemImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		initCorners();
		
		glowSize = (width/15)+2;
	}
	
	public void setRenderMode(RenderMode mode)
	{
		renderMode = mode;
	}
}
