package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import net.enigmablade.paradoxion.util.*;


public class NinePatchRenderer
{
	private Image image;
	private int imageW, imageH;
	private int subW, subH;
	
	private boolean drawTop, drawLeft, drawBot, drawRight, drawCenter;
	
	public NinePatchRenderer(String imageName)
	{
		this(imageName, true, true, true, true, true);
	}
	
	public NinePatchRenderer(String imageName, boolean top, boolean left, boolean bot, boolean right, boolean center)
	{
		image = ResourceLoader.getImage("ui/"+imageName+".png");
		imageW = image.getWidth(null);
		imageH = image.getHeight(null);
		subW = imageW/3;
		subH = imageH/3;
		
		drawTop = top;
		drawLeft = left;
		drawBot= bot;
		drawRight = right;
		drawCenter = center;
	}
	
	public void draw(Graphics g, int w, int h)
	{
		//Top-left
		if(drawTop && drawLeft)
			g.drawImage(image, 0, 0, subW, subH, -1, 0, subW, subH, null);
		//Top
		if(drawTop)
			g.drawImage(image, drawLeft ? subW : 0, 0, drawRight ? w-subW : w, subH, subW, 0, subW*2, subH, null);
		//Top-right
		if(drawTop && drawRight)
			g.drawImage(image, w-subW, 0, w, subH, subW*2, 0, imageW, subH, null);
		//Left
		if(drawLeft)
			g.drawImage(image, 0, drawTop ? subH : 0, subW, drawBot ? h-subH : h, -1, subH, subW, subH*2, null);
		//Center
		if(drawCenter)
			g.drawImage(image, drawLeft ? subW : 0, drawTop ? subH : 0, drawRight ? w-subW : w, drawBot ? h-subH : h, subW, subH, subW*2, subH*2, null);
		//Right
		if(drawRight)
			g.drawImage(image, w-subW, drawTop ? subH : 0, w, drawBot ? h-subH : h, subW*2, subH, imageW, subH*2, null);
		//Bot-left
		if(drawBot && drawLeft)
			g.drawImage(image, 0, h-subH, subW, h, -1, subH*2, subW, imageH, null);
		//Bot
		if(drawBot)
			g.drawImage(image, drawLeft ? subW : 0, h-subH, drawRight ? w-subW : w, h, subW, subH*2, subW*2, imageH, null);
		//Bot-right
		if(drawBot && drawRight)
			g.drawImage(image, w-subW, h-subH, w, h, subW*2, subH*2, imageW, imageH, null);
	}
}
