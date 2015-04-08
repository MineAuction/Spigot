package cz.sognus.mineauction.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class HashMapFixer {
	
	private Map<String, Object> map;
	
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
				
				map.remove(key, value);
				map.put(key, i);
				
				Log.debug("Fixed key: "+key+" with value "+s);
				
			}
		}
		
		return this.map;
	}

}
