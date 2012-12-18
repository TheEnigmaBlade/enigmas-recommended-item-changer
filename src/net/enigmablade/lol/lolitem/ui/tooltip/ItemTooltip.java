package net.enigmablade.lol.lolitem.ui.tooltip;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

import net.enigmablade.paradoxion.ui.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.ui.*;

public class ItemTooltip extends JToolTip
{
	private static final int topHeight = 36;
	private ItemTooltipImagePanel imagePanel;
	private Map<String, String> stats;
	private JLabel nameLabel, goldLabel, modeLabel;
	private JLabel separatorLabel, goldUnitLabel;
	
	private static Color outlineColor = new Color(50, 50, 50);
	private static Color stripeColor1, stripeColor2;
	
	static
	{
		Border border = BorderFactory.createLineBorder(outlineColor);
		UIManager.put("ToolTip.border", border);
	}
	
	public ItemTooltip(Item item)
	{
		setUI(new ItemToolTipUI());
		setBackground(UIUtil.COMPONENT_BASE);
		
		initComponents();
		
		if(stripeColor1 == null)
		{
			stripeColor1 = getBackground();
			stripeColor2 = GUIUtil.adjustColor(stripeColor1, -15);
		}
		
		stats = item != null ? item.getStats() : new HashMap<String, String>();
		
		imagePanel.setImage(item != null ? item.getImage() : null);
		nameLabel.setText(item.getName());
		goldLabel.setText(item.getTotalCost()+(item.getCost() != item.getTotalCost() ? " ("+item.getCost()+")" : ""));
		
		boolean isClassic = item.hasProperty(ItemProperty.CLASSIC_MODE);
		boolean isTwisted = item.hasProperty(ItemProperty.TWISTED_MODE);
		boolean isDominion = item.hasProperty(ItemProperty.DOMINION_MODE);
		boolean isAllMid = item.hasProperty(ItemProperty.ALL_MID_MODE);
		String text = "Summoner's Rift only";
		if(isClassic && isTwisted && isDominion && isAllMid)
			text = "All game modes";
		else if(isTwisted && isDominion)
			text = "Twisted Treeline and Crystal Scar only";
		else if(isTwisted && isAllMid)
			text = "Twisted Treeline and Proving Grounds only";
		else if(isDominion && isAllMid)
			text = "Crystal Scar and Proving Grounds only";
		else if(isTwisted)
			text = "Twisted Treeline only";
		else if(isDominion)
			text = "Crystal Scar only";
		else if(isAllMid)
			text = "Proving Grounds only";
		modeLabel.setText(text);
	}
	
	private void initComponents()
	{
		setLayout(new BorderLayout());
		setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(0, topHeight));
		topPanel.setBorder(new MatteBorder(0, 0, 1, 0, outlineColor));
		topPanel.setLayout(new BorderLayout(0, 0));
		topPanel.setBackground(getBackground());
		add(topPanel, BorderLayout.NORTH);
		
		imagePanel = new ItemTooltipImagePanel(topHeight);
		imagePanel.setBorder(new MatteBorder(0, 0, 0, 1, outlineColor));
		topPanel.add(imagePanel, BorderLayout.WEST);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(getBackground());
		topPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel namePanel = new JPanel();
		FlowLayout fl_namePanel = (FlowLayout)namePanel.getLayout();
		fl_namePanel.setVgap(2);
		fl_namePanel.setHgap(2);
		infoPanel.add(namePanel);
		
		nameLabel = new JLabel("");
		nameLabel.setFont(nameLabel.getFont().deriveFont(nameLabel.getFont().getStyle() | Font.BOLD, nameLabel.getFont().getSize() + 1f));
		namePanel.add(nameLabel);
		
		separatorLabel = new JLabel(": ");
		namePanel.add(separatorLabel);
		
		goldLabel = new JLabel("");
		namePanel.add(goldLabel);
		
		goldUnitLabel = new JLabel("gold");
		namePanel.add(goldUnitLabel);
		
		JPanel modePanel = new JPanel();
		FlowLayout fl_modePanel = (FlowLayout) modePanel.getLayout();
		fl_modePanel.setVgap(2);
		fl_modePanel.setHgap(2);
		infoPanel.add(modePanel);
		
		modeLabel = new JLabel("");
		modePanel.add(modeLabel);
	}
	
	private class ItemToolTipUI extends BasicToolTipUI
	{
		private List<String> lines;
		private int maxWidth = 0;
		
		public void paint(Graphics g1, JComponent c)
		{
			Graphics2D g = (Graphics2D)g1;
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			Dimension size = c.getSize();
			g.setColor(c.getBackground());
			g.fillRect(0, 0, size.width, size.height);
			
			//Stats
			g.setFont(c.getFont().deriveFont(Font.BOLD));
			FontMetrics metrics = g.getFontMetrics();
			int fontHeight = metrics.getHeight();
			int yOff = topHeight-metrics.getDescent()+2;
			int xOff = 6;
			int count = 0;
			for(String key : stats.keySet())
			{
				yOff += fontHeight;
				//Striped background
				g.setColor(count%2 == 0 ? stripeColor2 : stripeColor1);
				g.fillRect(0, yOff-metrics.getAscent(), getWidth(), fontHeight);
				count++;
				//Text
				String valueString = stats.get(key)+" ";
				g.setColor(c.getForeground());
				g.drawString(valueString, xOff, yOff);
				g.setPaint(TooltipUtil.getTypePaint(key));
				g.drawString(/*TooltipUtil.getType(key)*/key, xOff+SwingUtilities.computeStringWidth(metrics, valueString), yOff);
			}
			
			//Passives/Actives/Auras
			if(lines != null && lines.size() > 0)
			{
				//Separator line
				if(stats.size() > 0)
				{
					yOff += metrics.getDescent();
					g.setColor(c.getBackground().darker().darker());
					g.drawLine(0, yOff, size.width, yOff);
					g.setColor(c.getBackground().brighter());
					g.drawLine(0, yOff+1, size.width, yOff+1);
				}
				
				//Text
				/*g.setFont(c.getFont());
				for(String line : lines)
				{
					yOff += fontHeight;
					xOff = 0;
					//for(String part : line)
					{
						g.setPaint(c.getForeground());
						g.drawString(line, 3+xOff, yOff);
						//xOff += metrics.stringWidth(part);
					}
				}*/
			}
			
			g.translate(0, yOff+2);
			super.paint(g, c);
		}
		
		public Dimension getPreferredSize(JComponent c)
		{
			//Header
			FontMetrics headerBoldMetrics = c.getFontMetrics(nameLabel.getFont());
			maxWidth = 50+SwingUtilities.computeStringWidth(headerBoldMetrics, nameLabel.getText());
			FontMetrics headerMetrics = c.getFontMetrics(goldLabel.getFont());
			maxWidth += SwingUtilities.computeStringWidth(headerMetrics, " "+separatorLabel.getText()+" "+goldLabel.getText()+" "+goldUnitLabel.getText());
			
			FontMetrics metrics = c.getFontMetrics(getFont());
			//Stats
			int statsHeight = stats.size()*metrics.getHeight();
			if(lines != null && lines.size() > 0 && stats.size() > 0)
				statsHeight += metrics.getDescent();
			
			//Tooltip text
			String tipText = ((JToolTip)c).getTipText();
			if(tipText == null)
				tipText = "";
			
			BufferedReader br = new BufferedReader(new StringReader(tipText));
			String line;
			List<String> newLines = new ArrayList<String>();
			try
			{
				while((line = br.readLine()) != null)
				{
					int width = SwingUtilities.computeStringWidth(metrics, line);
					maxWidth = (maxWidth < width) ? width : maxWidth;
					newLines.add(line);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			lines = new ArrayList<String>(newLines);
			int height = metrics.getHeight()*lines.size();
			
			return new Dimension(maxWidth+6, height+topHeight+statsHeight+4);
		}
	}
}
