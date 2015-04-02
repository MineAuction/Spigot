package cz.sognus.mineauction;

import cz.sognus.mineauction.database.Database;
import cz.sognus.mineauction.listeners.MineAuctionBlockListener;
import cz.sognus.mineauction.listeners.MineAuctionCommands;
import cz.sognus.mineauction.listeners.MineAuctionInventoryListener;
import cz.sognus.mineauction.listeners.MineAuctionPlayerListener;
import cz.sognus.mineauction.utils.Config;
import cz.sognus.mineauction.utils.Lang;
import cz.sognus.mineauction.utils.Log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
* 
* @author Sognus
* 
*/
public class MineAuction extends JavaPlugin {
	
	public static String name;
	public static String version; 
	public static final String prefix = ChatColor.WHITE+"["+ChatColor.DARK_GREEN+"MineAuction"+ChatColor.WHITE+"] "+ChatColor.RESET;
	
	public static MineAuction plugin;
	public static Config config;
	public static Lang lang;
	public static Database db;
	
	public static Log logger;
	
	@Override
	public void onEnable()
	{
		
		// Setup plugin information
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		
		plugin = this;
		config = new Config(this);
		lang = new Lang(this);
		
		// Database
		db = new Database(this);
		db.openConnection();
		
		// Register listeners
		getServer().getPluginManager().registerEvents(new MineAuctionBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new MineAuctionPlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new MineAuctionInventoryListener(this), this);
		
		// Register command listener
		this.getCommand("ma").setExecutor(new MineAuctionCommands(this));
		
		// Print debug information
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
		WebInventory.forceCloseAll();
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.closeInventory();
		}
	}
	
	

}
