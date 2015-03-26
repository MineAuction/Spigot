package cz.sognus.mineauction.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WebInventory;
import cz.sognus.mineauction.WebInventoryMeta;
import cz.sognus.mineauction.utils.Log;

@SuppressWarnings("unused")
public class MineAuctionInventoryListener implements Listener {
	
	private final MineAuction plugin;
	
	public MineAuctionInventoryListener(MineAuction plugin)
	{
		this.plugin = plugin;
	}
	
	// MineAuction inventory close event listener
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		final Player player = (Player) event.getPlayer();
		String inventoryTitle = event.getInventory().getTitle();
		
		// Not MineAuction inventory
		if(inventoryTitle != MineAuction.lang.getString("inventory_title_deposit") && inventoryTitle != MineAuction.lang.getString("inventory_title_withdraw") && inventoryTitle != MineAuction.lang.getString("inventory_title_mailbox")) return;
		
        Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                WebInventory.onInventoryClose(player);  
            }
        });
		
	}
	
	
	// MineAuction inventory click event
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(!event.getInventory().getTitle().contains("[MineAuction]")) return;
		if(event.getClickedInventory() == null) return;
		
		ItemStack is = event.getCurrentItem();
		
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		
		if(clickedInventory.getTitle().startsWith("[MineAuction]"))
		{
			onWithdraw(is);
		}
		else
		{
			// Ignore click if inventory slot contains AIR
			if(is.getType() == Material.AIR) return;
			
			Bukkit.broadcastMessage("Item->Database");
			WebInventory wi = WebInventory.getInstance(event.getWhoClicked().getName());
			
			try
			{
				wi.itemDeposit(new WebInventoryMeta(is));
				Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
				Bukkit.createInventory(p, InventoryType.PLAYER).setItem(event.getSlot(), new ItemStack(Material.AIR));
				HashMap<Integer, ItemStack> tmp = p.getInventory().removeItem(event.getCurrentItem());
				
				// Write an class:
				// addItemStack - pøidá itemStack (64 qty) daného typu
				// addItem - pøidá 1 itemstack (1 qty) daného typu
				// removeItemStack - vymaže itemstack (64 qty) z inventáøe
				// removeItem - vymaže itemStack (1 qty) z inventáøe
				
				
				String OK = tmp.isEmpty() ? "Y" : "N"; 
				Bukkit.broadcastMessage(String.format("Deleting item %s for player %s, OK=%s", is.getType().name(), p.getName(),OK));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		event.setCancelled(true);
		
	}
	
	// placeholder method -> it is planed to move it into WebInventory class
	public static void onDeposit(ItemStack itemStack)
	{
		Log.info("Attempt to deposit items to database");
		
		WebInventoryMeta wim = new WebInventoryMeta(itemStack);
		
		Bukkit.broadcastMessage("ID: "+wim.getId());
		Bukkit.broadcastMessage("Pocet: "+wim.getItemQty());
		Bukkit.broadcastMessage("Znicenost: "+wim.getDurability());
		Bukkit.broadcastMessage("Metadata: "+wim.getItemMeta());
		Bukkit.broadcastMessage("Enchanty: "+wim.getItemEnchantments());
		
		
	}
	
	// 	// placeholder method -> it is planed to move it into WebInventory class - Tottaly useless method
	public static void onWithdraw(ItemStack is)
	{
		Bukkit.broadcastMessage("Attempt to withdraw from database");
	}
}
