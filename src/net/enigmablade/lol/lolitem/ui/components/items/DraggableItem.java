package net.enigmablade.lol.lolitem.ui.components.items;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.painter.*;
import org.jdesktop.swingx.painter.effects.*;

import net.enigmablade.paradoxion.ui.*;
import net.enigmablade.paradoxion.util.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;
import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.pretty.*;

import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.dnd.*;
import net.enigmablade.lol.lolitem.ui.tooltip.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class DraggableItem extends ItemPanel implements Comparable<DraggableItem>
{
	private int originalWidth, width, originalHeight, height;
	private Map<String, String> stats;
	private Image baseItemImage, itemImage;
	private int cornerSizeX, cornerSizeY;
	private Area clip;
	
	protected enum RenderMode {GRID, LIST};
	private RenderMode renderMode = RenderMode.GRID;
	
	private static JPopupMenu contextMenu;
	private static JMenuItem /*addMenuItem, removeMenuItem,*/ filterMenuItem;
	
	//Display information
	private static Font listFont, listFontSmall, listFontBold;
	
	private int glowSize = 4;
	private static Color glowColor = UIUtil.FOREGROUND, darkGlowColor = UIUtil.FOREGROUND.darker();
	private Shape[] glowOutlines;
	private Color[] glowColors, darkGlowColors;
	private boolean showGlow = false, showDarkGlow = false;
	
	private static GlossPainter glossPainter;
	
	private boolean showCount = false, countVisible = false;
	private JButton countDownButton, countUpButton;
	private JXLabel countLabel;
	private TextPainter countLabelPainter;
	private int count;
	
	//Other
	
	private ActionListener changeListener;
	private ItemFilterModel itemFilterModel;
	
	static
	{
		//Information
		listFont = new Font("Ariel", Font.PLAIN, 12);
		listFontSmall = listFont.deriveFont(11.0f);
		listFontBold = listFont.deriveFont(Font.BOLD);
		
		glossPainter = new GlossPainter();
		glossPainter.setPaint(new Color(255, 255, 255, 50));
		glossPainter.setAntialiasing(true);
		
		contextMenu = new JPopupMenu();
		//addMenuItem = new JMenuItem("Add item to set");
		//contextMenu.add(addMenuItem);
		//removeMenuItem = new JMenuItem("Remove item from set");
		//contextMenu.add(removeMenuItem);
		filterMenuItem = new JMenuItem("Show related items");
		contextMenu.add(filterMenuItem);
	}
	
	public DraggableItem(Item i, ItemFilterModel model, boolean sC)
	{
		item = i;
		itemFilterModel = model;
		showCount = sC;
		
		count = 1;
		
		if(item != null)
		{
			itemImage = baseItemImage = GamePathUtil.getItemImage(item.getImage());
			if(itemImage == null)
				itemImage = baseItemImage = ResourceLoader.getImage("missing_items/"+item.getImage()+".png");
			setToolTipText(item.getToolTip());
		}
		if(itemImage == null)
		{
			writeToLog("Item image for \""+item.getImage()+"\" not found", LoggingType.WARNING);
			itemImage = baseItemImage = GamePathUtil.getItemImage("EmptyIcon");
			if(itemImage == null)
				itemImage = baseItemImage = ResourceLoader.getImage("missing_items/EmptyIcon.png");
			if(itemImage == null)
				itemImage = baseItemImage = ResourceLoader.getImage("null.png");
		}
		
		stats = item != null ? item.getStats() : new HashMap<String, String>();
		
		setBackground(new Color(150, 150, 150));
		setForeground(new Color(200, 200, 200));
		setOpaque(false);
		
		originalWidth = baseItemImage.getWidth(null);
		originalHeight = baseItemImage.getHeight(null);
		setItemSize(originalWidth, originalHeight);
		
		DraggableItemMouseHoverListener dragList = new DraggableItemMouseHoverListener();
		
		if(showCount)
			initCountUI(dragList);
		
		addMouseListener(dragList);
		addMouseListener(new DraggableItemMouseClickListener());
		addMouseMotionListener(new DraggableItemMouseMotionListener());
		
		new ItemDropTargetListener();
	}
	
	private void initCountUI(DraggableItemMouseHoverListener dragList)
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{2, 12, 12, 12, 2, 0};
		gridBagLayout.rowHeights = new int[]{0, 16, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		countDownButton = new PrettyButton("\u2212");
		countDownButton.setBorder(null);
		countDownButton.setPreferredSize(new Dimension(12, 12));
		//countDownButton.setSize(new Dimension(14, 14));
		countDownButton.setVisible(false);
		countDownButton.addMouseListener(dragList);
		countDownButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				changeCount(-1);
			}
		});
		GridBagConstraints gbc_countDownButton = new GridBagConstraints();
		gbc_countDownButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_countDownButton.gridx = 1;
		gbc_countDownButton.gridy = 1;
		add(countDownButton, gbc_countDownButton);
		
		countUpButton = new PrettyButton("+");
		countUpButton.setBorder(null);
		countUpButton.setPreferredSize(new Dimension(12, 12));
		//countUpButton.setSize(new Dimension(18, 18));
		countUpButton.setVisible(false);
		countUpButton.addMouseListener(dragList);
		countUpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				changeCount(1);
			}
		});
		GridBagConstraints gbc_countUpButton = new GridBagConstraints();
		gbc_countUpButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_countUpButton.gridx = 3;
		gbc_countUpButton.gridy = 1;
		add(countUpButton, gbc_countUpButton);
		
		countLabel = new JXLabel(count+"");
		countLabel.setPreferredSize(new Dimension(12, 16));
		countLabel.setVisible(false);
		countLabel.setHorizontalAlignment(JLabel.CENTER);
		
		countLabelPainter = new TextPainter();
		countLabelPainter.setFont(getFont());
		//countLabelPainter.setFillPaint(getForeground());
		countLabelPainter.setAntialiasing(true);
		countLabelPainter.setFillHorizontal(false);
		ShadowPathEffect shadow = new ShadowPathEffect();
		shadow.setEffectWidth(4);
		shadow.setBrushColor(UIUtil.scale(UIUtil.FOREGROUND, 0.42f));
		shadow.setOffset(new Point(0, 0));
		countLabelPainter.setAreaEffects(shadow);
		countLabel.setBackgroundPainter(countLabelPainter);
		
		GridBagConstraints gbc_countLabel = new GridBagConstraints();
		gbc_countLabel.fill = GridBagConstraints.NONE;
		gbc_countLabel.gridx = 2;
		gbc_countLabel.gridy = 1;
		add(countLabel, gbc_countLabel);
	}
	
	private class DraggableItemMouseClickListener extends MouseAdapter
	{
		private boolean doubleClicked = false;
		
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			int button = evt.getButton();
			int clicks = evt.getClickCount();
			if(evt.isPopupTrigger() || button == MouseEvent.BUTTON3)
			{
				//for(ActionListener l : addMenuItem.getActionListeners())
				//	addMenuItem.removeActionListener(l);
				for(ActionListener l : filterMenuItem.getActionListeners())
					filterMenuItem.removeActionListener(l);
				/*addMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						addItemAction();
					}
				});*/
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
	}
	
	private class DraggableItemMouseHoverListener extends MouseAdapter
	{
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
	}
	
	private class DraggableItemMouseMotionListener extends MouseMotionAdapter
	{
		@Override
		public void mouseDragged(MouseEvent evt)
		{
			clearGlow();
		}
	}
	
	private void mouseAction(boolean sG)
	{
		showGlow = sG;
		showDarkGlow = false;
		
		countVisible = sG;
		if(showCount)
		{		
			countDownButton.setVisible(countVisible);
			countUpButton.setVisible(countVisible);
			if(count == 1)
				countLabel.setVisible(countVisible);
		}
		
		repaint();
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
			g.setColor(GUIUtil.addAlpha(getBackground(), showGlow ? 75 : 25));
			g.fillRect(width-cornerSizeX, 0, getWidth(), height);
			
			g.setColor(getForeground());
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
				String valueString = stats.get(key)+" ";
				g.setColor(getForeground());
				g.drawString(valueString, xOff, yOff);
				String typeString = TooltipUtil.getShortType(key);
				g.setPaint(TooltipUtil.getTypePaint(key));
				g.drawString(typeString, xOff+=SwingUtilities.computeStringWidth(metrics, valueString), yOff);
				xOff+=SwingUtilities.computeStringWidth(metrics, typeString)+4;
			}
		}
		
		//Draw item image
		Area oldClip = new Area(g.getClip());
		Area newClip = (Area)oldClip.clone();
		newClip.subtract(clip);
		newClip.exclusiveOr(oldClip);
		g.setClip(newClip);
		g.drawImage(itemImage, 0, 0, this);
		
		//Draw glow
		if(showGlow)
		{
			glossPainter.paint(g, this, width, height);
			
			g.setStroke(new BasicStroke(2));
			for(int n = 0; n < glowSize && n < darkGlowColors.length && n < glowColors.length; n++)
			{
				Color c = showDarkGlow ? darkGlowColors[n] : glowColors[n];
				g.setColor(c);
				g.draw(glowOutlines[n]);
			}
		}
		
		g.setColor(UIUtil.COMPONENT_BORDER);
		g.draw(clip);
		
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
		for(int n = 0; n < glowSize && n < glowColors.length; n++)
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
				
				//This item is in a build panel, so we can add the dragged item in as well
				if(buildParent != null)
				{
					//New item
					if(action == DnDConstants.ACTION_COPY)
					{
						if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
						{
							Item item = (Item)tr.getTransferData(TransferableItem.itemFlavor);
							
							evt.acceptDrop(DnDConstants.ACTION_COPY);
							buildParent.addItemAt(item, DraggableItem.this);
							evt.dropComplete(true);
							
							if(changeListener != null)
								changeListener.actionPerformed(new ActionEvent(DraggableItem.this, ActionEvent.ACTION_PERFORMED, ""));
							
							return;
						}
						evt.dropComplete(false);
					}
					//Item from other build panel
					else if(action == DnDConstants.ACTION_MOVE)
					{
						if(evt.isDataFlavorSupported(TransferableItem.itemFlavor))
						{
							DraggableItem item = (DraggableItem)tr.getTransferData(TransferableItem.itemFlavor);
							BuildPanel source = (BuildPanel)item.getBuildParent();
							
							evt.acceptDrop(DnDConstants.ACTION_MOVE);
							item.clearGlow();
							buildParent.addItemAt(item.getItem(), DraggableItem.this);
							source.removeItem(item);
							evt.dropComplete(true);
							
							if(changeListener != null)
								changeListener.actionPerformed(new ActionEvent(DraggableItem.this, ActionEvent.ACTION_PERFORMED, ""));
							
							return;
						}
						evt.dropComplete(false);
					}
				}
				//This item is in the item pool, so we can remove the dragged item from its build panel
				else
				{
					if(action == DnDConstants.ACTION_MOVE)
					{
						DraggableItem item = (DraggableItem)tr.getTransferData(TransferableItem.removableItemFlavor);
						BuildPanel oldPanel = item.getBuildParent();
						writeToLog("Item removed from BuildPanel \""+oldPanel.getName()+"\"");
						
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						item.clearGlow();
						oldPanel.removeItem(item);
						evt.dropComplete(true);
						
						if(changeListener != null)
							changeListener.actionPerformed(new ActionEvent(DraggableItem.this, ActionEvent.ACTION_PERFORMED, ""));
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
	
	public void clearGlow()
	{
		showGlow = showDarkGlow = false;
		countVisible = false;
		if(showCount)
		{		
			countDownButton.setVisible(countVisible);
			countUpButton.setVisible(countVisible);
			countLabel.setVisible(count != 1);
		}
		repaint();
	}
	
	public void setCountVisible(boolean show)
	{
		countVisible = show;
		revalidate();
		repaint();
	}
	
	private void changeCount(int amt)
	{
		setItemCount(count+amt);
	}
	
	public int getItemCount()
	{
		return count;
	}
	
	public void setItemCount(int c)
	{
		count = c < 1 ? 1 : c;
		
		countLabel.setText(count+"");
		countLabelPainter.setText(count+"");
		
		countLabel.setVisible(count != 1);
	}
	
	//Connections to BuildPanel
	
	private BuildPanel buildParent;
	
	public void setBuildParent(BuildPanel parent)
	{
		buildParent = parent;
	}
	
	public BuildPanel getBuildParent()
	{
		return buildParent;
	}
	
	//Overrides
	
	public int compareTo(DraggableItem ctItem)
	{
		return item.compareTo(ctItem.item);
	}
}
