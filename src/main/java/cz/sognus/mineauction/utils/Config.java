package cz.sognus.mineauction.utils;

import cz.sognus.mineauction.MineAuction;

import org.bukkit.configuration.Configuration;

/**
 * This class provides access to plugin configuration.
 *
 * @author Sognus
 * 
 */
public class Config {

	@SuppressWarnings("unused")
	private MineAuction plugin;
	private Configuration config;

	public Config(MineAuction plugin) {
		this.plugin = plugin;

		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

	public String getString(String key) {
		return config.getString(key);
	}

	public int getInt(String key) {
		return config.getInt(key);
	}

	public boolean getBool(String key) {
		return config.getBoolean(key);
	}

}
