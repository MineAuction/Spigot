package cz.sognus.mineauction.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import cz.sognus.mineauction.MineAuction;

/**
* This class formats messages for output to chat.  
*
* @author Sognus
* 
*/
public class Chat {

	public static String prefix = Chat.Format("{darkgreen}"+MineAuction.name); 
	
	private static HashMap<String, ChatColor> chatColorsMap = new HashMap<String, ChatColor>() {{
		put("{black}",         ChatColor.BLACK         );
		put("{darkblue}",      ChatColor.DARK_BLUE     );
		put("{darkgreen}",     ChatColor.DARK_GREEN    );
		put("{darkaqua}",      ChatColor.DARK_AQUA     );
		put("{darkred}",       ChatColor.DARK_RED      );
		put("{darkpurple}",    ChatColor.DARK_PURPLE   );
		put("{gold}",          ChatColor.GOLD          );
		put("{gray}",          ChatColor.GRAY          );
		put("{darkgray}",      ChatColor.DARK_GRAY     );
		put("{blue}",          ChatColor.BLUE          );
		put("{green}",         ChatColor.GREEN         );
		put("{aqua}",          ChatColor.AQUA          );
		put("{red}",           ChatColor.RED           );
		put("{lightpurple}",   ChatColor.LIGHT_PURPLE  );
		put("{yellow}",        ChatColor.YELLOW        );
		put("{white}",         ChatColor.WHITE         );
		put("{magic}",         ChatColor.MAGIC         );
		put("{bold}",          ChatColor.BOLD          );
		put("{strikethrough}", ChatColor.STRIKETHROUGH );
		put("{underline}",     ChatColor.UNDERLINE     );
		put("{italic}",        ChatColor.ITALIC        );
		put("{reset}",         ChatColor.RESET         );
	}};
	
	// Unchecked - zpùsobovalo NullPoint v onEnable()
	public static String Format(String text)
	{
		if(text==null) return "<<null>>";
		if(text.contains("{") && text.contains("}")) {
			for(Entry<String, ChatColor> entry : chatColorsMap.entrySet()) {
				String replaceWhat = entry.getKey();
				ChatColor withWhat = entry.getValue();
				text = text.replace(replaceWhat, withWhat.toString());
			}
		}
		return text;	
	}
}
