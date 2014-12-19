package cz.sognus.mineauction;

import cz.sognus.mineauction.utils.Config;
import cz.sognus.mineauction.utils.Chat;
import cz.sognus.mineauction.utils.Lang;
import cz.sognus.mineauction.utils.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class MineAuction extends JavaPlugin {
	
	public static String version;
	public static String name;
	
	public static MineAuction plugin;
	public static Config config;
	public static Chat chat;
	public static Log logger;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		config = new Config(this);
		
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		
		Logger.getLogger("Minecraft").log(Level.INFO, String.format("This server is running %s version %s", name, version));
		
		
	}
	
	public void onReload()
	{
		// Stop all task, clear all variables then load them again
	}
	
	public void onDiable()
	{
		// Stop all task, clear all variables
	}
	
	

}
