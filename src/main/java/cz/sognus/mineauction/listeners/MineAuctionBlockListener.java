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

/**
 * 
 * @author Sognus
 * 
 */
public class MineAuctionBlockListener implements Listener {

	@SuppressWarnings("unused")
	private final MineAuction plugin;

	public MineAuctionBlockListener(MineAuction plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player p = e.getPlayer();

		if (block.getType() == Material.WALL_SIGN
				|| block.getType() == Material.SIGN_POST) {

			Sign s = (Sign) block.getState();
			if (s.getLine(0).equals("[MineAuction]")) {
				if (!p.hasPermission("ma.admin.remove")) {
					e.setCancelled(true);
					p.sendMessage(MineAuction.prefix + ChatColor.RED
							+ MineAuction.lang.getString("no_permission"));
				} else {
					p.sendMessage(MineAuction.prefix + ChatColor.GREEN
							+ MineAuction.lang.getString("sign_removed"));
				}

			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		String[] lines = e.getLines();

		Player p = e.getPlayer();

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
			return;
		}

		if (p.hasPermission("ma.admin.create." + type.toLowerCase())) {
			e.setLine(0, "[MineAuction]");
			e.setLine(1, type);
			p.sendMessage(MineAuction.prefix + ChatColor.GREEN
					+ MineAuction.lang.getString(langString));
		} else {
			e.setCancelled(true);
			p.sendMessage(MineAuction.prefix + ChatColor.RED
					+ MineAuction.lang.getString("no_permission"));
		}

	}

}
