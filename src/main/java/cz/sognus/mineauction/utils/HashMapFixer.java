package cz.sognus.mineauction.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;

import cz.sognus.mineauction.MineAuction;

public class HashMapFixer {
	
	private Map<String, Object> map;
	private Map<String, Object> outMap = new HashMap<String, Object>();
	
	public HashMapFixer(Map<String, Object> map)
	{
		this.map = map;
	}
	
	public Map<String, Object> fix()
	{
		for(Entry<String, Object> entry : this.map.entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			
			// Fix repair-cost
			if(key == "repair-cost")
			{	
				Object o = value;
				String s = String.valueOf(o);
				s = s.substring(0, s.indexOf("."));
				Integer i = Integer.valueOf(s);
				
				outMap.put(key, i);
				
				Log.debug("Fixed key: "+key+" with value "+s);
				
			}
			else
			{
				outMap.put(key, value);
			}
		}
		
		return this.outMap;
	}
	
	public void printInputMap()
	{
		if(!MineAuction.config.getBool("debug")) return;
		
		Bukkit.broadcastMessage(ChatColor.BLUE+"<InputHashMap>");
		String s = "";
		for(Entry<String, Object> entry : map.entrySet())
		{
			s = entry.getKey() + "=>"+entry.getValue().toString();
			Bukkit.broadcastMessage(s);
		}
		Bukkit.broadcastMessage(ChatColor.BLUE+"</InputHashMap>");
	}
	
	public void printOutputMap()
	{
		if(!MineAuction.config.getBool("debug")) return;
		
		Bukkit.broadcastMessage(ChatColor.BLUE+"<OutputHashMap>");
		String s = "";
		for(Entry<String, Object> entry : outMap.entrySet())
		{
			s = entry.getKey() + "=>"+entry.getValue().toString();
			Bukkit.broadcastMessage(s);
		}
		Bukkit.broadcastMessage(ChatColor.BLUE+"</OutputHashMap>");
	}

}
