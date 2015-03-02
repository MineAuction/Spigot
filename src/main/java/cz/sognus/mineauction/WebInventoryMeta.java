package cz.sognus.mineauction;

import java.util.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WebInventoryMeta
{
	private final ItemStack item;
	
	public WebInventoryMeta(ItemStack i)
	{
		this.item = i;
	}
	
	
	public int getItemQty()
	{
		if(this.item == null) return 0;
		return this.item.getAmount();		
	}
	
	public String getItemMeta()
	{
		if(this.item == null) return "";
		Map<String, Object> mapMeta = this.item.getItemMeta().serialize();
		
		Gson gson = new Gson();
		String json = gson.toJson(mapMeta);
		
		return json;
			
	}
	
	public String getItemEnchantments()
	{
		if(this.item == null) return "";
		
		Map<Enchantment, Integer> mapEnchantment = this.item.getEnchantments();
		Map<String, Object> mapData = new HashMap<String, Object>();
		
		for(Map.Entry<Enchantment, Integer> entry : mapEnchantment.entrySet())
		{
			String s = entry.getKey().getName();
			Object o = entry.getValue();
			
			mapData.put(s, o);
			
		}
		
		Gson gson = new Gson();
		String json = gson.toJson(mapData);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getItemMetaMap(String json)
	{
		if(json == "" || json == null) return null;
		
		Gson gson = new Gson();
		Map<String, Object> mapMeta = new HashMap<String, Object>();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		mapMeta = (Map<String, Object>)gson.fromJson(json,type);
		
		return mapMeta;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Enchantment, Integer> getItemEnchantmentMap(String json)
	{
		if(json == "" || json == null) return null;
		
		Gson gson = new Gson();
		Map<String, Object> mapData = new HashMap<String, Object>();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		mapData = (Map<String, Object>)gson.fromJson(json,type);
		
		Map<Enchantment, Integer> mapEnchant = new HashMap<Enchantment, Integer>();
		
		for(Map.Entry<String, Object> entry : mapData.entrySet())
		{
			Enchantment e = Enchantment.getByName(entry.getKey());
			Integer i = (Integer) entry.getValue();
			
			mapEnchant.put(e, i);
		}
		
		return mapEnchant;
	}
	
	public String getType(ItemStack i)
	{
		if(i == null) return "";
		
		String itemTypeName = i.getType().name();
		
		return itemTypeName;
	}
	
	public short getDurability(ItemStack i)
	{
		if(i == null) return -32000;
		
		short int16 = i.getDurability();
		return int16;
	}
}
