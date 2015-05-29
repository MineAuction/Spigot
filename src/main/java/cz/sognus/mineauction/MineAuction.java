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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Sognus
 * 
 *         Czech only todo-list:
 * 
 *         DŮLEŽITÉ: TODO: Implementovat zbytek commandů a permissí
 * 
 *         GENERAL: TODO: Admin command pro otevírání různých druhů inventáře
 *         bez cedulky TODO: Vytvořit několik různých tasků které budou
 *         oznamovat informace ve hře
 * 
 *         POZDĚJI: TODO: Rušení cedulek z webové části, cedulka se nesmaže z
 *         databáze, připraví se na smazání a ve hře ji smaže task. TODO:
 *         Vytvořit task který na základě logů bude oznamovat hráčům itemy které
 *         nakoupili/prodali TODO: Vymyslet system konfigurovani tasku (např.
 *         jak dlouhou prodlevu budou mít)
 *         TODO: Nespustit plugin, pokud je přiliš stará verze serveru
 *         
 *         ENHANCEMENT:
 *         TODO: Zvážit vytvoření nového event listeneru na vybírání a vkládání
 *         použít možnost dragování z inventáře do inventáře.
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
	public static boolean enabled = true;

	public static Log logger;

	// Listeners
	public static MineAuctionBlockListener blockListener;
	public static MineAuctionPlayerListener playerListener;
	public static MineAuctionInventoryListener inventoryListener;

	@Override
	public void onEnable() {

		// Setup plugin information
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();

		// Text
		getServer().getConsoleSender().sendMessage(
				ChatColor.RESET
						+ "*********** Enabling MineAuction ***********");

		// Setting current instance up
		plugin = this;
		getServer().getConsoleSender().sendMessage(
				MineAuction.prefix + "Setting instance [ " + ChatColor.GREEN
						+ "OK" + ChatColor.RESET + " ]");

		// Setting configuration up
		config = new Config(this);
		getServer().getConsoleSender().sendMessage(
				MineAuction.prefix + "Loading configuration [ "
						+ ChatColor.GREEN + "OK" + ChatColor.RESET + " ]");

		// Setting lang up
		try {
			lang = new Lang(this);
			getServer().getConsoleSender().sendMessage(
					MineAuction.prefix + "Loading language files [ "
							+ ChatColor.GREEN + "OK" + ChatColor.RESET + " ]");
		} catch (Exception e) {
			getServer().getConsoleSender()
					.sendMessage(
							MineAuction.prefix + "Loading configuration [ "
									+ ChatColor.RED + "FAILED"
									+ ChatColor.RESET + " ]");
			getServer().getConsoleSender().sendMessage(
					ChatColor.WHITE
							+ "********************************************");
			getServer().getConsoleSender().sendMessage("Stacktrace: ");
			e.printStackTrace();
			onDisable();
			return;
		}

		// Setting database up
		try {
			db = new Database(this);
			db.openConnection();
			getServer().getConsoleSender().sendMessage(
					MineAuction.prefix + "Connecting to database [ "
							+ ChatColor.GREEN + "OK" + ChatColor.RESET + " ]");
		} catch (Exception e) {
			getServer().getConsoleSender()
					.sendMessage(
							MineAuction.prefix + "Connecting to database [ "
									+ ChatColor.RED + "FAILED"
									+ ChatColor.RESET + " ]");
			getServer().getConsoleSender().sendMessage(
					ChatColor.RESET
							+ "********************************************");
			getServer().getConsoleSender().sendMessage("Info: ");
			getServer().getConsoleSender().sendMessage(
					"Database exception occured: " + e.getMessage());
			onDisable();
			return;

		}

		// Register listeners
		blockListener = new MineAuctionBlockListener(this);
		playerListener = new MineAuctionPlayerListener(this);
		inventoryListener = new MineAuctionInventoryListener(this);

		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(inventoryListener, this);

		// Register command listener
		this.getCommand("ma").setExecutor(new MineAuctionCommands(this));

		// Print debug information

		// Check database
		try {
			db.createTables();
			getServer().getConsoleSender().sendMessage(
					MineAuction.prefix + "Checking database structure [ "
							+ ChatColor.GREEN + "OK" + ChatColor.RESET + " ]");
		} catch (Exception e) {
			getServer().getConsoleSender()
					.sendMessage(
							MineAuction.prefix
									+ "Checking database structure [ "
									+ ChatColor.RED + "FAILED"
									+ ChatColor.RESET + " ]");
			getServer().getConsoleSender().sendMessage(
					ChatColor.WHITE
							+ "********************************************");
			getServer().getConsoleSender().sendMessage("Info: ");
			Log.info("Database exception occured: " + e.getMessage());
			e.printStackTrace();
			onDisable();
			return;
		}

		// End Text
		getServer().getConsoleSender().sendMessage(
				ChatColor.RESET
						+ "********************************************");

		// Plugin is OK
		if (enabled) {
			Log.debug("This plugin is now running in debug mode");
			Log.debug("Main class: " + this.getClass().getName());
		}

		// Enable features
		enabled = true;
	}

	public void onReload() {
		onDisable();
		onEnable();
	}

	public void reloadLang() {
		try {
			MineAuction.lang = new Lang(this);
			getServer().getConsoleSender().sendMessage(
					MineAuction.prefix + "Reloading language files [ "
							+ ChatColor.GREEN + "OK" + ChatColor.RESET + " ]");
		} catch (Exception e) {
			getServer().getConsoleSender()
					.sendMessage(
							MineAuction.prefix + "Reloading language files [ "
									+ ChatColor.RED + "FAILED"
									+ ChatColor.RESET + " ]");
			getServer().getConsoleSender().sendMessage("Info: ");
			e.printStackTrace();
			onDisable();
			return;

		}
	}

	public void onDisable() {
		if (!enabled)
			return;

		getServer().getConsoleSender().sendMessage(
				ChatColor.RESET
						+ "****** MineAuction is being disabled *******");

		// Close all virtual inventories
		getServer().getConsoleSender().sendMessage(
				prefix + ChatColor.RESET + "Closing virtual inventories");
		WebInventory.forceCloseAll();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.closeInventory();
		}

		// Unregister event
		getServer().getConsoleSender().sendMessage(
				prefix + ChatColor.RESET + "Unregistering listeners");
		PlayerInteractEvent.getHandlerList().unregister(playerListener);
		PlayerJoinEvent.getHandlerList().unregister(playerListener);
		InventoryClickEvent.getHandlerList().unregister(inventoryListener);
		InventoryCloseEvent.getHandlerList().unregister(inventoryListener);

		getServer().getConsoleSender().sendMessage(
				ChatColor.RESET
						+ "********************************************");

		enabled = false;
	}

	public void onDisableNoConsole() {
		WebInventory.forceCloseAll();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.closeInventory();
		}

		// Unregister events
		PlayerInteractEvent.getHandlerList().unregister(playerListener);
		PlayerJoinEvent.getHandlerList().unregister(playerListener);
		InventoryClickEvent.getHandlerList().unregister(inventoryListener);
		InventoryCloseEvent.getHandlerList().unregister(inventoryListener);
		BlockBreakEvent.getHandlerList().unregister(blockListener);
		SignChangeEvent.getHandlerList().unregister(blockListener);

		enabled = false;

	}

}
