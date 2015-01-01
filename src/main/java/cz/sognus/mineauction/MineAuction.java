package cz.sognus.mineauction;

import cz.sognus.mineauction.database.Database;
import cz.sognus.mineauction.listeners.MineAuctionBlockListener;
import cz.sognus.mineauction.listeners.MineAuctionPlayerListener;
import cz.sognus.mineauction.utils.Config;
import cz.sognus.mineauction.utils.Lang;
import cz.sognus.mineauction.utils.Log;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.plugin.java.JavaPlugin;

/**
* 
* @author Sognus
* 
*/
public class MineAuction extends JavaPlugin {
	
	public static String version;
	public static String name;
	public static final String prefix = ChatColor.WHITE+"["+ChatColor.DARK_GREEN+"MineAuction"+ChatColor.WHITE+"] "+ChatColor.RESET;
	
	public static MineAuction plugin;
	public static Config config;
	public static Lang lang;
	public static Database db;
	
	public static Log logger;
	
	@Override
	public void onEnable()
	{
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		
		plugin = this;
		config = new Config(this);
		lang = new Lang(this);
		db = new Database(this);
		
		getServer().getPluginManager().registerEvents(new MineAuctionBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new MineAuctionPlayerListener(this), this);
		
		Log.debug("This plugin is now running in debug mode");
		Log.debug("Main class: "+this.getClass().getName());	
	}
	
	public void onReload()
	{
		onDisable();
		onEnable();
	}
	
	public void onDiable()
	{
	}
	
	

}
