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
 * 
 * TODO: Command pro registraci uživatele v databázi přímo a configurace (security)
 * TODO: Command pro registraci uživatele v databázi nepřímo za pomocí jednorázového dočasného pinu + configurace (security)
 * TODO: Command pro přímou změnu uživatelského hesla v databázi [potřebuju domluvit strukturu db se Sekim]
 * TODO: Command pro nepřímou změnu uživatelského hesla v databázi za pomocí dočasného pinu generovaného ve hře
 * TODO: Admin command pro otevírání různých druhů inventáře bez cedulky
 * TODO: Dodělat logování zakládání aukčních bodů [potřebuju se domluvit se Sekim na struktuře db]
 * TODO: Dodělat experimentální ochranu cedulek (přes bedrock), vyžaduje logování cedulek
 * TODO: Vytvořit několik různých tasků které budou oznamovat informace ve hře
 * 
 * POZDĚJI:
 * TODO: Rušení cedulek z webové části, cedulka se nesmaže z databáze, připraví se na smazání a ve hře ji smaže task.
 * TODO: Vytvořit task který na základě logů bude oznamovat hráčům itemy které nakoupili/prodali
 * TODO: Vymyslet system konfigurovani tasku (např. jak dlouhou prodlevu budou mít)
 * 
 */
public class MineAuction extends JavaPlugin {

	public static String name;
	public static String version;
	public static final String prefix = ChatColor.WHITE + "["
			+ ChatColor.DARK_GREEN + "MineAuction" + ChatColor.WHITE + "] "
			+ ChatColor.RESET;

	public static MineAuction plugin;
	public static Config config;
	public static Lang lang;
	public static Database db;

	public static Log logger;

	@Override
	public void onEnable() {

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
		getServer().getPluginManager().registerEvents(
				new MineAuctionBlockListener(this), this);
		getServer().getPluginManager().registerEvents(
				new MineAuctionPlayerListener(this), this);
		getServer().getPluginManager().registerEvents(
				new MineAuctionInventoryListener(this), this);

		// Register command listener
		this.getCommand("ma").setExecutor(new MineAuctionCommands(this));

		// Print debug information
		Log.debug("This plugin is now running in debug mode");
		Log.debug("Main class: " + this.getClass().getName());
		
		// Check database
		db.createTables();
	}

	public void onReload() {
		onDisable();
		onEnable();
	}
	
	public void reloadLang()
	{
		MineAuction.lang = new Lang(this);
	}

	public void onDisable() {
		WebInventory.forceCloseAll();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.closeInventory();
		}
	}

}
