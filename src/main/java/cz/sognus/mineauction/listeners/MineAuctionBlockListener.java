package cz.sognus.mineauction.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;
import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.database.DatabaseLogger;
import cz.sognus.mineauction.database.DatabaseUtils;

/**
 * 
 * @author Sognus
 *
 * TODO: Save metadata with bedrock security
 */
public class MineAuctionBlockListener implements Listener {

	@SuppressWarnings("unused")
	private final MineAuction plugin;

	public MineAuctionBlockListener(MineAuction plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		// vars
		String oldBlock;

		Block block = e.getBlock();
		Player p = e.getPlayer();

		if (block.getType() == Material.WALL_SIGN
				|| block.getType() == Material.SIGN_POST) {

			Sign s = (Sign) block.getState();
			if (s.getLine(0).equals("[MineAuction]")
					&& (s.getLine(1).equalsIgnoreCase("mailbox")
							|| s.getLine(1).equalsIgnoreCase("withdraw") || s
							.getLine(1).equalsIgnoreCase("deposit"))) {
				if (!p.hasPermission("ma.admin.remove")) {
					e.setCancelled(true);
					p.sendMessage(MineAuction.prefix + ChatColor.RED
							+ MineAuction.lang.getString("no_permission"));
				} else {
					p.sendMessage(MineAuction.prefix + ChatColor.GREEN
							+ MineAuction.lang.getString("sign_removed"));

					// Configurable option:
					if (MineAuction.config
							.getBool("plugin.security.logging.signs")) {
						oldBlock = DatabaseLogger.deleteRecordAuction(block
								.getLocation());

						org.bukkit.material.Sign sign = (org.bukkit.material.Sign) e
								.getBlock().getState().getData();
						Block attached = e.getBlock().getRelative(
								sign.getAttachedFace());

						// Change bedrock to previous block
						if (attached.getType() == Material.BEDROCK) {
							Material m = Material.matchMaterial(oldBlock);
							attached.setType(m);

							// Delete bedrock if it stuck in database
							if (attached.getType() == Material.BEDROCK) {
								attached.setType(Material.AIR);
							}
						}
					}

				}

			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		String[] lines = e.getLines();
		Player p = e.getPlayer();

		org.bukkit.material.Sign s = (org.bukkit.material.Sign) e.getBlock()
				.getState().getData();
		Block attached = e.getBlock().getRelative(s.getAttachedFace());

		if (p == null)
			return;

		if (!lines[0].equalsIgnoreCase("[MineAuction]")
				&& !lines[0].equalsIgnoreCase("[ma]"))
			return;

		String type;
		String langString;

		switch (lines[1].toUpperCase()) {
		case "MAILBOX":
			type = "MailBox";
			langString = "sign_create";
			break;
		case "DEPOSIT":
			type = "Deposit";
			langString = "sign_create_deposit";
			break;
		case "WITHDRAW":
			type = "Withdraw";
			langString = "sign_create_withdraw";
			break;
		default:
			p.sendMessage(MineAuction.prefix + ChatColor.RED
					+ MineAuction.lang.getString("sign_invalid"));
			e.setLine(0, "");
			e.setLine(1, "");
			e.setLine(2, "");
			e.setLine(3, "");
			return;
		}

		if (p.hasPermission("ma.admin.create." + type.toLowerCase())) {
			e.setLine(0, "[MineAuction]");
			e.setLine(1, type);
			e.setLine(2, "");
			e.setLine(3, "");
			p.sendMessage(MineAuction.prefix + ChatColor.GREEN
					+ MineAuction.lang.getString(langString));

			// Configurable option:
			if (MineAuction.config.getBool("plugin.security.logging.signs"))
				DatabaseLogger.logRecordAuctionCreate(
						DatabaseUtils.getPlayerId(e.getPlayer().getUniqueId()),
						attached, e.getBlock(), type.toLowerCase());

			if (MineAuction.config.getBool("plugin.security.logging.signs")
					&& MineAuction.config.getBool("plugin.security.bedrock")){
				attached.setType(Material.BEDROCK);
			}
		} else {
			e.setCancelled(true);
			p.sendMessage(MineAuction.prefix + ChatColor.RED
					+ MineAuction.lang.getString("no_permission"));
			e.setLine(0, "");
			e.setLine(1, "");
			e.setLine(2, "");
			e.setLine(3, "");
		}

	}

}
