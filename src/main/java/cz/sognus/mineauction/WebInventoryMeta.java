package cz.sognus.mineauction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

public class WebInventoryMeta {
	private final ItemStack item;

	public WebInventoryMeta(ItemStack i) {
		this.item = i;
	}

	// get number of item in stack
	public int getItemQty() {
		if (this.item == null)
			return 0;
		return this.item.getAmount();
	}

	public String getLore() {
		if (this.item == null || !this.item.getItemMeta().hasLore())
			return "";
		List<String> loreList = this.item.getItemMeta().getLore();
		if (loreList == null)
			return "";
		Gson gson = new Gson();
		return gson.toJson(loreList);
	}

	@SuppressWarnings("unchecked")
	public List<String> getLoreList(String json) {
		if (json == "" || json == null)
			return null;
		Gson gson = new Gson();
		List<String> loreList = new ArrayList<String>();
		Type type = new TypeToken<List<String>>() {
		}.getType();
		loreList = (List<String>) gson.fromJson(json, type);
		return loreList;

	}

	// get item enchantments in json format
	public String getItemEnchantments() {
		if (this.item == null)
			return "";

		Map<Enchantment, Integer> mapEnchantment = this.item.getEnchantments();
		Map<String, Object> mapData = new HashMap<String, Object>();

		for (Map.Entry<Enchantment, Integer> entry : mapEnchantment.entrySet()) {
			String s = entry.getKey().getName();
			Object o = entry.getValue();

			mapData.put(s, o);

		}

		Gson gson = new GsonBuilder().setLongSerializationPolicy(
				LongSerializationPolicy.STRING).create();
		String json = gson.toJson(mapData);

		return json;
	}

	// get enchantments hashmap from json
	@SuppressWarnings("unchecked")
	public static Map<Enchantment, Integer> getItemEnchantmentMap(String Ijson) {
		if (Ijson == "" || Ijson == null)
			return null;

		String json = Ijson.replaceAll("\\.\\d+", "");

		Gson gson = new GsonBuilder().setLongSerializationPolicy(
				LongSerializationPolicy.STRING).create();
		Map<String, Object> mapData = new HashMap<String, Object>();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		mapData = (Map<String, Object>) gson.fromJson(json, type);

		Map<Enchantment, Integer> mapEnchant = new HashMap<Enchantment, Integer>();

		for (Map.Entry<String, Object> entry : mapData.entrySet()) {

			Enchantment e = Enchantment.getByName(entry.getKey());
			Integer i = (Integer) Integer.parseInt((String) entry.getValue());

			mapEnchant.put(e, i);
		}

		return mapEnchant;
	}

	// get item id (string)
	@SuppressWarnings("deprecation")
	public int getId() {
		if (this.item == null)
			return -1;

		int itemTypeName = item.getType().getId();

		return itemTypeName;
	}

	// get item durability
	public short getDurability() {
		if (this.item == null)
			return -1;

		short int16 = item.getDurability();
		return int16;
	}

	public ItemStack getItemStack() {
		return this.item;
	}

	// METADATA VERSION 2 HERE
	public String getItemMeta() {
		YamlConfiguration iconf = new YamlConfiguration();
		iconf.set("data", this.item);
		String items = iconf.saveToString();
		return items;
	}

	public static ItemStack getItemStack(String yaml) {
		ItemStack is = null;
		YamlConfiguration iconf = new YamlConfiguration();

		try {
			iconf.loadFromString(yaml);
			is = iconf.getItemStack("data");
		} catch (Exception e) {
		}
		return is;
	}

	// END OF METADATA V2
}