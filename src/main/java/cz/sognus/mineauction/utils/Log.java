package cz.sognus.mineauction.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import cz.sognus.mineauction.MineAuction;

/**
* This class outputs messages to log.  
*
* @author Sognus
* 
*/
public class Log {
	
	private static final Logger logg = Logger.getLogger("Minecraft");
	
	public static void info(String message)
	{
		logg.log(Level.INFO, String.format("%s %s", "[MineAuction]", message));
	}
	
	public static void error(String message)
	{
		logg.log(Level.SEVERE, String.format("%s %s", "[MineAuction]", message));
		MineAuction.plugin.onDiable();
	}
	
	public static void debug(String message)
	{
		if(MineAuction.config.getBool("debug"))
			logg.log(Level.INFO, String.format("%s%s %s", "[MineAuction]","[Debug]", message));
	}
	
	public static void warning(String message)
	{
		logg.log(Level.WARNING, String.format("%s %s", "[MineAuction]", message));
	}
	
	public static void broadcast(String message)
	{
		Bukkit.broadcastMessage(message);
	}
}
