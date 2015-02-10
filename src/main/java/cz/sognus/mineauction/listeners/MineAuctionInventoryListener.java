package cz.sognus.mineauction.listeners;

import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WebInventory;
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
		
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
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
		
		event.setCancelled(true);
		
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		
		if(clickedInventory.getTitle().startsWith("[MineAuction]"))
		{
			onWithdraw(event);
		}
		else
		{
			onDeposit(event);
		}
		
		
		
		
	}
	
	// placeholder method -> it is planed to move it into WebInventory class
	public static void onDeposit(InventoryClickEvent event)
	{
		Log.info("Attempt to deposit items to database");
		
		Map<String, Object> map = event.getCurrentItem().getItemMeta().serialize();
		String vystup = "";
		
		for(Entry<String, Object> entry : map.entrySet())
		{
			String key = entry.getKey();
			Object val = entry.getValue();
			
			vystup += String.format("<%s:%s>", key, val);
		}
		
	}
	
	// 	// placeholder method -> it is planed to move it into WebInventory class - Tottaly useless method
	public static void onWithdraw(InventoryClickEvent event)
	{
		Bukkit.broadcastMessage("Attempt to withdraw from database");
	}
}
