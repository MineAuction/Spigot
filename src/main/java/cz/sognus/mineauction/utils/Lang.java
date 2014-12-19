package cz.sognus.mineauction.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.sognus.mineauction.MineAuction;

public class Lang {
	
	private HashMap<String, String> messages = new HashMap<String, String>();
	
	private MineAuction plugin;
	private String language;

	public Lang(MineAuction plugin)
	{
		this.plugin = plugin;
		this.copyLanguageFiles();
		this.loadLang(plugin.config.getString("lang"));
			
	}
	
	// Should load all language rows
	public void loadLang(String lang)
	{
		try
		{
			File file = new File(plugin.getDataFolder().toString()+File.separator+"lang"+File.separator+lang+".yml");
			if(file.exists())
			{
				FileConfiguration langConfig = YamlConfiguration.loadConfiguration(file);
				for(String key : langConfig.getStringList("Main"))
				{
					messages.put(key, langConfig.getString("Main."+key));
					
					// Debug
					Log.debug("Loaded lang key :" +key);
				}
			}
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	public void copyLanguageFiles()
	{
		try
		{
			File extFolder = new File(plugin.getDataFolder()+"/lang/");
			if(!extFolder.exists()) extFolder.mkdirs();
			
			
			File folder = new File(plugin.getClass().getResource("/lang/").getPath());
			File[] arrayFile = folder.listFiles();
			
			for(File f : arrayFile)
			{
				if(f.isFile())
				{
					File extFile = new File(plugin.getDataFolder()+"/lang/"+f.getName());
					if(extFile.exists()) continue; 
						
					extFile.createNewFile(); 
					
					InputStream is = plugin.getClass().getResourceAsStream("/lang/"+f.getName());
					FileOutputStream os = new FileOutputStream(new File(plugin.getDataFolder()+"/lang/"+f.getName()));
					
					while(is.available()>0)
					{
						os.write(is.read());
					}
					
					is.close();
					os.close();
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	public String getString(String key)
	{
		if(!messages.containsKey(key)) return "<<Message not found>>";
		String output = messages.get(key);
		if(output != null || !output.isEmpty()) return output;
		return "<<Message not found>>";
	}

}
