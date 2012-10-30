package enigma.lol.lolitem.gui.tooltip;

import java.awt.*;
import java.util.*;

import static enigma.paradoxion.util.Logger.*;

public class TooltipUtil
{
	private static Map<String, String> types, shortTypes, reverseTypes, replacements;
	private static Map<String, Boolean> hasPercent;
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
		types = new HashMap<String, String>();
		shortTypes = new HashMap<String, String>();
		reverseTypes = new HashMap<String, String>();
		hasPercent = new HashMap<String, Boolean>();
		typeColors = new HashMap<String, Paint>();
		
		types.put("ad", "Attack Damage");
		shortTypes.put("ad", "AD");
		typeColors.put("ad", physicalColor);
		
		types.put("as", "Attack Speed");
		shortTypes.put("as", "AS");
		hasPercent.put("as", true);
		typeColors.put("as", speedColor);
		
		types.put("ap", "Ability Power");
		shortTypes.put("ap", "AP");
		typeColors.put("ap", magicColor);
		
		types.put("appl", "Ability Power per level");
		shortTypes.put("appl", "AP per level");
		typeColors.put("appl", magicColor);
		
		types.put("h", "Health");
		shortTypes.put("h", "Health");
		typeColors.put("h", healthColor);
		
		types.put("hregen", "Health Regen per 5 seconds");
		shortTypes.put("hregen", "Health Regen");
		typeColors.put("hregen", healthColor);
		
		types.put("m", "Mana");
		shortTypes.put("m", "Mana");
		typeColors.put("m", manaColor);
		
		types.put("mregen", "Mana Regen per 5 seconds");
		shortTypes.put("mregen", "Mana Regen");
		typeColors.put("mregen", manaColor);
		
		types.put("ar", "Armor");
		shortTypes.put("ar", "Armor");
		typeColors.put("ar", defenseColor);
		
		types.put("apen", "Armor Penetration");
		shortTypes.put("apen", "Armor Pen");
		typeColors.put("apen", physicalColor);
		
		types.put("mr", "Magic Resistance");
		shortTypes.put("mr", "Magic Res");
		typeColors.put("mr", defenseColor);
		
		types.put("mpen", "Magic Penetration");
		shortTypes.put("mpen", "Magic Pen");
		typeColors.put("mpen", magicColor);
		
		types.put("cd", "Cooldown Reduction");
		shortTypes.put("cd", "CDR");
		typeColors.put("cd", magicColor);
		
		types.put("ten", "Tenacity");
		shortTypes.put("ten", "Tenacity");
		typeColors.put("ten", tenacityColor);
		
		types.put("crit", "Critical Strike");
		shortTypes.put("crit", "Crit");
		hasPercent.put("crit", true);
		typeColors.put("crit", physicalColor);
		
		types.put("ls", "Life Steal");
		shortTypes.put("ls", "LS");
		hasPercent.put("ls", true);
		typeColors.put("ls", physicalColor);
		
		types.put("sv", "Spell Vamp");
		shortTypes.put("sv", "SV");
		hasPercent.put("sv", true);
		typeColors.put("sv", magicColor);
		
		types.put("mv", "Movement Speed");
		shortTypes.put("mv", "MS");
		hasPercent.put("mv", true);
		typeColors.put("mv", movementColor);
		
		for(String key : types.keySet())
			reverseTypes.put(types.get(key), key);
				
		//Replacements
		replacements = new HashMap<String, String>();
		replacements.put("UP", "UNIQUE Passive: ");
		replacements.put("UA", "UNIQUE Active: ");
		replacements.put("UR", "UNIQUE Aura: ");
		replacements.put("P", "Passive: ");
		replacements.put("A", "Active: ");
		replacements.put("R", "Aura: ");
		replacements.put("AA", "Ability Augment: ");
		replacements.put("C2C", "Click to Consume: ");
		
		replacements.put("SCD", " second cooldown.");
		replacements.put("MCD", " minute cooldown.");
		replacements.put("S", " seconds");
		replacements.put("MINS", " minutes");
		replacements.put("MIN", " minute");
		
		replacements.put("CH", "champion");
		replacements.put("BA", "basic attack");
		replacements.put("MD", "magic damage");
		replacements.put("PD", "physical damage");
		replacements.put("TD", "true damage");
		replacements.put("SD", "spell damage");
		
		replacements.put("DNS", "Does not stack with ");
		
		for(String key : types.keySet())
			replacements.put(key.toUpperCase(), types.get(key));
	}
	
	public static String getType(String key)
	{
		String type = types.get(key);
		if(type == null)
		{
			writeToLog("TooltipUtil.getType(): key has null result", LoggingType.WARNING);
			return "!"+key+"!";
		}
		return type;
	}
	
	public static String getShortType(String key)
	{
		String type = shortTypes.get(key);
		if(type == null)
		{
			writeToLog("TooltipUtil.getShortType(): key has null result", LoggingType.WARNING);
			return "!"+key+"!";
		}
		return type;
	}
	
	public static String getTypeSeparator(String key)
	{
		return hasPercent.containsKey(key) ? "% " : " ";
	}
	
	public static Paint getTypePaint(String key)
	{
		return typeColors.get(key);
	}
	
	public static String parseReplacement(String key)
	{
		return replacements.get(key);
	}
}
