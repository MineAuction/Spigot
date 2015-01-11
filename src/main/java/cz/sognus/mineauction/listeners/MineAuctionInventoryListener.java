package cz.sognus.mineauction.listeners;

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
	
	
	// MineAuction inventory withdraw event
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryWithdraw(InventoryClickEvent event)
	{	
		Player player = (Player) event.getWhoClicked();
		PlayerInventory playerInventory = event.getWhoClicked().getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		
		// MineAuction inventory
		if(!clickedInventory.getTitle().startsWith("[MineAuction]")) return;
		
		// Invalid auction type
		if(clickedInventory.getTitle().endsWith("Deposit"))
		{
			event.setCancelled(true);
			player.sendMessage(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("action_invalid"));
			return;
		}
		
		event.setCancelled(true);
		
		//
		// Run async task to withdraw inventory from database
		//
		//
		
		// Temp debug
		String tempMessage = "Item withdraw action";
		player.sendMessage(MineAuction.prefix + ChatColor.RED + tempMessage);
	}
	
	
	
	// MineAuction inventory deposit event
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryDeposit(InventoryClickEvent event)
	{
		Player p = (Player) event.getWhoClicked();
	}
	

}
