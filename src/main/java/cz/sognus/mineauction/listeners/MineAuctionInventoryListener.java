package cz.sognus.mineauction.listeners;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WCInventory;
import cz.sognus.mineauction.WebInventory;
import cz.sognus.mineauction.WebInventoryMeta;
import cz.sognus.mineauction.database.DatabaseUtils;
import cz.sognus.mineauction.utils.Log;
import cz.sognus.mineauction.utils.PlayerUtils;

/**
 * 
 * @author Sognus
 * 
 */
@SuppressWarnings("unused")
public class MineAuctionInventoryListener implements Listener {

	private final MineAuction plugin;

	public MineAuctionInventoryListener(MineAuction plugin) {
		this.plugin = plugin;
	}

	// MineAuction inventory close event listener
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		String inventoryTitle = event.getInventory().getTitle();

		// Not MineAuction inventory
		if (inventoryTitle != MineAuction.lang
				.getString("inventory_title_deposit")
				&& inventoryTitle != MineAuction.lang
						.getString("inventory_title_withdraw")
				&& inventoryTitle != MineAuction.lang
						.getString("inventory_title_mailbox"))
			return;

		Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				WebInventory.onInventoryClose(player);
			}
		});

	}

	// MineAuction inventory click event	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent event) {
		// this event is only for MineAuction
		if (!event.getInventory().getTitle().contains("[MineAuction]")){
			return;
		}
		
		// click out of inventory
		if (event.getClickedInventory() == null){
			return;
		}
			
		
		String eventString = event.getAction().name();
		Bukkit.broadcastMessage(eventString);
		
		// test for available actions
		String[] availableActions = {
				"" + InventoryAction.PLACE_ALL, 
				"" + InventoryAction.PLACE_ONE,
				"" + InventoryAction.MOVE_TO_OTHER_INVENTORY, 
				"" + InventoryAction.PLACE_SOME, 
		};		
		if(Arrays.asList(availableActions).contains(eventString)){
			Bukkit.broadcastMessage("podporovana akce" + eventString);
		}
		
		//event.setCancelled(true);

		
		// Ignore click if inventory slot contains AIR
		ItemStack is = event.getCurrentItem().clone();
		if (is.getType() == Material.AIR)
			return;

		
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		String whoClicked = event.getWhoClicked().getName();
		
		// operations
		if (clickedInventory.getTitle().startsWith("[MineAuction]")) {
			// WEB INVENTORY ACTIONS
			Bukkit.broadcastMessage("web: " + clickedInventory.getTitle());
			
			
			// sognus
			WebInventory wi = WebInventory.getInstance(whoClicked);
			
			// Fix handler for invClick issued after reload
			if(wi == null)
			{
				event.getWhoClicked().closeInventory();
				return;
			}
			
			try {
				wi.itemWithdraw(event);
			} catch (Exception e) {

				e.printStackTrace();
			}
			
		}
		else{
			// PLAYER INVENTORY ACTIONS
			Bukkit.broadcastMessage("player: " + clickedInventory.getTitle());
			
			// sognus
			WebInventory wi = WebInventory.getInstance(whoClicked);
			Player p = Bukkit.getPlayer(whoClicked);

			try {
				ItemStack istmp = is.clone();
				if (event.getClick().isShiftClick())
					istmp.setAmount(1);
				boolean depositOK = wi.itemDeposit(new WebInventoryMeta(istmp));
				
				if(!depositOK) return;

				// Vymazani itemy z inventare
				WCInventory wci = new WCInventory(p);
				int output = event.getClick().isShiftClick() ? wci.removeItems(
						is, 1) : wci.removeItems(is, is.getAmount());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

	/*
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent event) {
		if (!event.getInventory().getTitle().contains("[MineAuction]"))
			return;
		if (event.getClickedInventory() == null)
			return;

		event.setCancelled(true);

		ItemStack is = event.getCurrentItem().clone();

		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();

		if (clickedInventory.getTitle().startsWith("[MineAuction]")) {
			// ITEM WITHDRAW
			// Ignore click if inventory slot contains AIR
			if (is.getType() == Material.AIR)
				return;

			WebInventory wi = WebInventory.getInstance(event.getWhoClicked()
					.getName());
			
			// Fix handler for invClick issued after reload
			if(wi == null)
			{
				event.getWhoClicked().closeInventory();
				return;
			}
			
			try {
				wi.itemWithdraw(event);
			} catch (Exception e) {

				e.printStackTrace();
			}
			
		} else {
			// ITEM DEPOSIT
			// Ignore click if inventory slot contains AIR
			if (is.getType() == Material.AIR)
				return;

			WebInventory wi = WebInventory.getInstance(event.getWhoClicked()
					.getName());
			Player p = Bukkit.getPlayer(event.getWhoClicked().getName());

			try {
				ItemStack istmp = is.clone();
				if (event.getClick().isShiftClick())
					istmp.setAmount(1);
				boolean depositOK = wi.itemDeposit(new WebInventoryMeta(istmp));
				
				if(!depositOK) return;

				// Vymazani itemy z inventare
				WCInventory wci = new WCInventory(p);
				int output = event.getClick().isShiftClick() ? wci.removeItems(
						is, 1) : wci.removeItems(is, is.getAmount());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// event.setCancelled(true);

	}
	*/
}
