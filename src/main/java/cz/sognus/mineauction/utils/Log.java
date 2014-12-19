package cz.sognus.mineauction.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.sognus.mineauction.MineAuction;

public class Log {
	
	private static final Logger logg = Logger.getLogger("Minecraft");
	
	public static void info(String message)
	{
		logg.log(Level.INFO, String.format("%s %s", "[MineAuction]", message));
	}
	
	public static void error(String message)
	{
		logg.log(Level.SEVERE, String.format("%s %s", "[MineAuction]", message));
	}
	
	public static void debug(String message)
	{
		if(Config.Debug) 
			logg.log(Level.INFO, String.format("%s %s", "[MineAuction]", message));
	}
	
	public static void warning(String message)
	{
		logg.log(Level.WARNING, String.format("%s %s", "[MineAuction]", message));
	}
}
