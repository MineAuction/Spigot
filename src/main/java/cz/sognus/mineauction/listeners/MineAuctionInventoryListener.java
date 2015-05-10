package cz.sognus.mineauction.listeners;

import java.sql.ResultSet;
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
	@SuppressWarnings("deprecation")
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
			
			WebInventory wip = WebInventory.getInstance(event.getWhoClicked()
					.getName());
			Player pl = Bukkit.getPlayer(event.getWhoClicked().getName());
			int qty = event.getClick().isShiftClick() ? 1 : is.getAmount();

			// Get item stack
			try {
				ResultSet rs = DatabaseUtils.getItemFromDatabase(is);

				while (rs.next()) {
					// Get item Data
					int itemID = rs.getInt("itemID");
					short itemDamage = rs.getShort("itemDamage");
					int qnty = rs.getInt("qty");
					String itemData = rs.getString("itemMeta");
					Map<Enchantment, Integer> itemEnch = WebInventoryMeta
							.getItemEnchantmentMap(rs.getString("enchantments"));

					ItemStack stack = null;

					if (itemData != null && itemData != "") {
						// Yaml metadata
						is = WebInventoryMeta.getItemStack(itemData);
					} else {
						// No yaml metadata
						is = new ItemStack(Material.getMaterial(itemID), qnty,
								itemDamage);
					}

					// Overwrite values
					is.setDurability(itemDamage);
					is.addEnchantments(itemEnch);

					// Update database
					boolean success = DatabaseUtils.updateItemInDatabase(pl,
							is, qty);

					// Update player inventory
					if (!success) {
						pl.sendMessage(MineAuction.prefix + ChatColor.RED + MineAuction.lang.getString("no_item_update"));
						return;
					}
					WCInventory wci = new WCInventory(pl);
					wci.addItems(is, qty);
					WebInventory.getInstance(pl.getName()).refreshInventory();
				}

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
				wi.itemDeposit(new WebInventoryMeta(istmp));

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
}
