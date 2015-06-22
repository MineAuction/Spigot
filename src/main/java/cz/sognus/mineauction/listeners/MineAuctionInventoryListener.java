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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
		// Target inventory not belongs to MineAuctions
		if (!event.getView().getTopInventory().getTitle()
				.startsWith("[MineAuction]"))
			return;
		Bukkit.broadcastMessage("Click event fired!");

		// Player is trying to drop item to ground
		String[] blackListActions = new String[] { "DROP_ALL_SLOT",
				"DROP_ONE_SLOT", "DROP_ALL_CURSOR", "DROP_ONE_CURSOR" };
		String inventoryAction = event.getAction().toString();

		if (Arrays.asList(blackListActions).contains(inventoryAction)) {
			event.setCancelled(true);
			return;

		}

		// Determine if action was issued

		Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				WebInventory wi = WebInventory.getInstance(event
						.getWhoClicked().getName());
				wi.manageItemChanges(event.getInventory().getContents());
			}
		});

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemPickup(PlayerPickupItemEvent event) {
		String playerName = event.getPlayer().getName();
		if (WebInventory.getInstance(playerName) != null) {
			event.setCancelled(true);
		}
	}
}
