package cz.sognus.mineauction;

import cz.sognus.mineauction.utils.Config;
import cz.sognus.mineauction.utils.Chat;
import cz.sognus.mineauction.utils.Lang;
import cz.sognus.mineauction.utils.Log;

import org.bukkit.plugin.java.JavaPlugin;

/**
* 
* @author Sognus
* 
*/
public class MineAuction extends JavaPlugin {
	
	public static String version;
	public static String name;
	
	public static MineAuction plugin;
	public static Config config;
	public static Lang lang;
	
	public static Log logger;
	public static Chat chat;
	
	@Override
	public void onEnable()
	{
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		
		plugin = this;
		config = new Config(this);
		lang = new Lang(this);
		
		Log.debug("This plugin is now running in debug mode");
		Log.debug("Main class: "+this.getClass().getName());	
	}
	
	public void onReload()
	{
		// Stop all task, clear all variables then load them again
		onDisable();
		onEnable();
	}
	
	public void onDiable()
	{
		// Stop all task, clear all variables
	}
	
	

}
