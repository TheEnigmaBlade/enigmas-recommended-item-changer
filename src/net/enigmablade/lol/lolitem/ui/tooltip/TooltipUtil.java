package net.enigmablade.lol.lolitem.ui.tooltip;

import java.awt.*;
import java.util.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class TooltipUtil
{
	private static Map<String, String> shortTypes;
	private static Map<String, Paint> typeColors;
	
	static
	{
		Color physicalColor = new Color(210, 160, 0);
		Color speedColor = Color.green.darker();
		Color magicColor = new Color(150, 0, 200);
		Color defenseColor = new Color(75, 75, 75);
		Color healthColor = Color.red;
		Color manaColor = Color.blue;
		Color movementColor = Color.black;
		Color tenacityColor = Color.blue.brighter();
		
		//Types
		shortTypes = new HashMap<String, String>();
		typeColors = new HashMap<String, Paint>();
		
		shortTypes.put("attack damage", "AD");
		typeColors.put("attack damage", physicalColor);
		
		shortTypes.put("attack damage per level", "AD per level");
		typeColors.put("attack damage per level", physicalColor);
		
		shortTypes.put("attack speed", "AS");
		typeColors.put("attack speed", speedColor);
		
		shortTypes.put("ability power", "AP");
		typeColors.put("ability power", magicColor);
		
		shortTypes.put("ability power per level", "AP per level");
		typeColors.put("ability power per level", magicColor);
		
		shortTypes.put("health", "Health");
		typeColors.put("health", healthColor);
		
		shortTypes.put("health regen per 5 seconds", "Health Regen");
		typeColors.put("health regen per 5 seconds", healthColor);
		
		shortTypes.put("mana", "Mana");
		typeColors.put("mana", manaColor);
		
		shortTypes.put("mana regen per 5 seconds", "Mana Regen");
		typeColors.put("mana regen per 5 seconds", manaColor);
		
		shortTypes.put("armor", "Armor");
		typeColors.put("armor", defenseColor);
		
		shortTypes.put("armor penetration", "Armor Pen");
		typeColors.put("armor penetration", physicalColor);
		
		shortTypes.put("magic resist", "Magic Res");
		typeColors.put("magic resist", defenseColor);
		
		shortTypes.put("magic penetration", "Magic Pen");
		typeColors.put("magic penetration", magicColor);
		
		shortTypes.put("cooldown reduction", "CDR");
		typeColors.put("cooldown reduction", magicColor);
		
		shortTypes.put("tenacity", "Tenacity");
		typeColors.put("tenacity", tenacityColor);
		
		shortTypes.put("critical strike chance", "Crit");
		typeColors.put("critical strike chance", physicalColor);
		
		shortTypes.put("life steal", "Lifesteal");
		typeColors.put("life steal", physicalColor);
		
		shortTypes.put("spell vamp", "SV");
		typeColors.put("spell vamp", magicColor);
		
		shortTypes.put("movement speed", "MS");
		typeColors.put("movement speed", movementColor);
	}
	
	public static String getShortType(String key)
	{
		if(key != null)
			key = key.toLowerCase();
		String type = shortTypes.get(key);
		if(type == null)
		{
			writeToLog("TooltipUtil.getShortType(): key \""+key+"\" has null result", LoggingType.WARNING);
			return "!"+key+"!";
		}
		return type;
	}
	
	public static Paint getTypePaint(String key)
	{
		if(key != null)
			key = key.toLowerCase();
		return typeColors.get(key);
	}
}
