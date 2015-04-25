package cz.sognus.mineauction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.sognus.mineauction.database.DatabaseUtils;
import cz.sognus.mineauction.utils.Log;

public class WebInventory {
	
	protected static final Map<String, WebInventory> openInvs = new HashMap<String, WebInventory>();
	
	
	protected final Player player;
	public final Inventory inventory;
	protected final String inventoryTitle;
	protected boolean canWithdraw;
	protected boolean canDeposit;
	
	
	
	// Constructor
	public WebInventory(Player p, String invType)
	{
		this.player = p;
		this.inventoryTitle = this.getInventoryType(invType);
		
		// Create inventory
		this.inventory = Bukkit.createInventory(null, 54, inventoryTitle);
		
		// Load it
		this.loadInventory();
		//this.inventory.setItem(0, new ItemStack(Material.DIAMOND, 64));
		
		// open it
		this.player.openInventory(this.inventory);
	}
	
	// Load items to inventory
	@SuppressWarnings({ "deprecation", "static-access" })
	public void loadInventory()
	{
		try
		{
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ma_items WHERE playerID=? ORDER BY qty ASC LIMIT 54");
			
			// Prepare statement
			int playerID = DatabaseUtils.getPlayerId(this.player.getUniqueId());
			ps.setInt(1, playerID);
			
			Bukkit.broadcastMessage(ps.toString());
			
			// Result 
			ResultSet rs = ps.executeQuery();
			int i = 0;
			
			// Reset inventory
			this.inventory.clear();
			
			
			// Work with result
			while(rs.next())
			{
				try
				{
				if(rs.getInt("qty") < 1) continue;
				
				// Get Item information
				int itemID = rs.getInt("itemID");
				short itemDamage = rs.getShort("itemDamage");
				int qty = rs.getInt("qty");
				
				Map<String, Object> itemMeta = WebInventoryMeta.getItemMetaMap(rs.getString("itemMeta"));
				Map<Enchantment, Integer> itemEnch = WebInventoryMeta.getItemEnchantmentMap(rs.getString("enchantments"));
				
				// Create ItemStack and properties		
				ItemStack dbItem = new ItemStack(Material.getMaterial(itemID));
				int visualQty = qty > dbItem.getMaxStackSize() ? dbItem.getMaxStackSize() : qty;
				String visualLore = String.format("%s: %d", MineAuction.lang.getString("auction_item_quantity"), qty);
				
				// Item Meta
				ItemMeta im = (ItemMeta) ConfigurationSerialization.deserializeObject(itemMeta, ConfigurationSerialization.getClassByAlias("ItemMeta"));
				
				// qty lore
				List<String> tempList = new ArrayList<String>();
				
				if(dbItem.getItemMeta().hasLore())
				{
					tempList = dbItem.getItemMeta().getLore();
					tempList.add(visualLore);	
				}
				else
				{	
					tempList.add(visualLore);	
				}
				im.setLore(tempList);
				
				// Update itemstack
				dbItem.setItemMeta(im);
				dbItem.setAmount(visualQty);
				dbItem.setDurability(itemDamage);
				dbItem.addEnchantments(itemEnch);
				
				this.inventory.setItem(i, dbItem);
				i++;
				}
				catch(Exception e)
				{
					int itemID = rs.getInt("itemID");
					ItemStack is = new ItemStack(Material.getMaterial(itemID));
						
					this.player.sendMessage(MineAuction.plugin.prefix + ChatColor.RED + String.format("Skipping item %s, because it has some errors.", is.getType().name()));
					
					e.printStackTrace();
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	
	}
	
	public void itemDeposit(WebInventoryMeta wim) throws Exception
	{
		// Unsupported action
		if(!this.canDeposit)
		{
			this.player.sendMessage(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("action_invalid"));
			return;
		}
		
		// Save item to database
		// exist in database (a - update, b - insert)
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = null;
			
			// Fix duplicite items records, restack them at database
			// DatabaseUtils.fixItemStacking()
			
			// Same item is in database, update him
			if(DatabaseUtils.isItemInDatabase(wim, DatabaseUtils.getPlayerId(this.player.getUniqueId())) && wim.getItemStack().getMaxStackSize() > 1)
			{
				Bukkit.broadcastMessage("Item->UPDATE->Database");
				
				ps = conn.prepareStatement("UPDATE ma_items SET qty = qty + ? WHERE playerID = ? AND itemID= ? AND itemDamage = ? AND itemMeta = ? AND enchantments = ? AND lore = ?");
				ps.setInt(1, wim.getItemQty());
				ps.setInt(2, DatabaseUtils.getPlayerId(this.player.getUniqueId()));
				ps.setInt(3, wim.getId());
				ps.setShort(4, wim.getDurability());
				ps.setString(5, wim.getItemMeta());
				ps.setString(6, wim.getItemEnchantments());
				ps.setString(7, wim.getLore());
				
				Bukkit.broadcastMessage("SQL: "+ps.toString());
				
				ps.executeUpdate();
			}
			else
			{
				Bukkit.broadcastMessage("Item->INSERT->Database");
				
				ps = conn.prepareStatement("INSERT INTO `ma_items` (`playerID`, `itemID`, `itemDamage`, `qty`, `itemMeta`, `enchantments`, `lore`) VALUES (?, ?, ?,?, ?, ?, ?)");
				ps.setInt(1, DatabaseUtils.getPlayerId(this.player.getUniqueId()));
				ps.setInt(2, wim.getId());
				ps.setShort(3, wim.getDurability());
				ps.setInt(4, wim.getItemQty());
				ps.setString(5, wim.getItemMeta());
				ps.setString(6, wim.getItemEnchantments());
				ps.setString(7, wim.getLore());
				
				Bukkit.broadcastMessage("SQL: "+ps.toString());
				
				ps.execute();
			}
			
			// Update WebInventory
			this.loadInventory();
			this.player.updateInventory();

	}
	
	public void itemWithdraw(WebInventoryMeta wim) throws Exception
	{
		// Unsupported action
		if(!this.canWithdraw)
		{
			this.player.sendMessage(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("action_invalid"));
			return;
		}
		
		// Here will be withdraw from database
		
		
	}

	public Inventory getInventory()
	{
		return this.inventory;
	}
	
	// Get inventory type
	public String getInventoryType(String invType)
	{
		String inventoryTitle;
		
		switch(invType.toUpperCase())
		{
			case "MAILBOX":
				inventoryTitle = "inventory_title_mailbox";
				this.canDeposit = true;
				this.canWithdraw = true;
				break;
			case "DEPOSIT":
				inventoryTitle = "inventory_title_deposit";
				this.canDeposit = true;
				this.canWithdraw = false;
				break;
			case "WITHDRAW":
				this.canDeposit = false;
				this.canWithdraw = true;
				inventoryTitle = "inventory_title_withdraw";
				break;
			default:
				Log.warning(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("inventory_title_invalid"));
				this.canDeposit = false;
				this.canWithdraw = false;
				return "<<null>>";
		}
		
		return MineAuction.lang.getString(inventoryTitle);
	}

	// Runs when inventory opens
	public static void onInventoryOpen(Player player, String invType)
	{
		if(player == null) throw new NullPointerException();
		String playerName = player.getName();
		UUID playerUUID = player.getUniqueId();
		boolean playerRegistered = DatabaseUtils.playerRegistered(playerUUID);
		
		synchronized(openInvs)
		{
			// Player in database
			if(playerRegistered)
			{
				
				setLocked(playerUUID, true);
				if(openInvs.containsKey(playerUUID))
				{
					// Already using
					player.sendMessage(MineAuction.prefix + ChatColor.YELLOW + MineAuction.lang.getString("auction_using"));
					return;
				}
				else
				{
					// Not yet using
					player.sendMessage(MineAuction.prefix + ChatColor.GREEN + MineAuction.lang.getString("auction_loading"));
					WebInventory wi = new WebInventory(player, invType);
					openInvs.put(playerName, wi);
				}
				
			}
			else
			{
				// Player not found in database
				player.sendMessage(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("account_register"));
				
			}
			
		}
		
	}
	
	//Runs when inventory close
	public static void onInventoryClose(Player player)
	{
		if(player == null) throw new NullPointerException();
		String playerName = player.getName();
		UUID playerUUID = player.getUniqueId();
		
		synchronized(openInvs)
		{
			if(!openInvs.containsKey(playerName)) return;
			openInvs.remove(playerName);
			setLocked(playerUUID, false);
		}
		
		player.sendMessage(MineAuction.prefix + ChatColor.GREEN + MineAuction.lang.getString("auction_saving"));
	}
	
	// Close all inventories
	public static void forceCloseAll()
	{
		if(openInvs==null || openInvs.size()==0) return;
		for(final String playerName : openInvs.keySet())
		{
			final Player player = Bukkit.getPlayerExact(playerName);
			player.closeInventory();
			WebInventory.onInventoryClose(player);
		}		
	}
	
	// Set locked in database
	public static void setLocked(UUID playerUUID, boolean locked)
	{
		if(playerUUID == null) throw new NullPointerException();
		
		int lock = locked ? 1 : 0;
		
		try
		{
			Connection connection = MineAuction.db.getConnection();
			PreparedStatement st = null;
			
			st = connection.prepareStatement("UPDATE ma_players set locked= ? WHERE uuid= ? ");
			st.setInt(1, lock);
			st.setString(2 ,playerUUID.toString());
			st.executeUpdate();
			
			st.close();
			connection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static WebInventory getInstance(String playerName)
	{
		if(playerName == "" || playerName == null) return null;
		
		synchronized(openInvs)
		{
			if(openInvs.containsKey(playerName))
			{
				return openInvs.get(playerName);
			}
		}
		
		// Null if it fail
		return null;
	}
}
