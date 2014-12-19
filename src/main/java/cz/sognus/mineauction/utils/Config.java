package cz.sognus.mineauction.utils;

import cz.sognus.mineauction.MineAuction;

import org.bukkit.configuration.Configuration;

public class Config {
	private MineAuction plugin;
	private Configuration config;
	
	public static boolean Debug;
	
	// Udìlat podobnou vìc jako u lang
	
	
	public Config(MineAuction plugin)
	{
		this.plugin = plugin;
		
		this.config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
	}
	
	
	public void Load()
	{
		
	}
	
	public String getString(String key)
	{
		return config.getString(key);
	}
	
	public int getInt(String key)
	{
		return config.getInt(key);
	}
	
	public boolean getBool(String key)
	{
		return config.getBoolean(key);
	}

}
