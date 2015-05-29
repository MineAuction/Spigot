package cz.sognus.mineauction.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WebInventory;
import cz.sognus.mineauction.database.DatabaseUtils;

/**
 * 
 * @author Sognus
 * 
 */
public class MineAuctionPlayerListener implements Listener {

	private final MineAuction plugin;

	public MineAuctionPlayerListener(MineAuction plugin) {
		this.plugin = plugin;
	}

	// Player login
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (DatabaseUtils.getPlayerId(event.getPlayer().getUniqueId()) == 0) {
				DatabaseUtils.registerPlayer(event.getPlayer());
				event.getPlayer().sendMessage(MineAuction.prefix + ChatColor.YELLOW + MineAuction.lang.getString("account_created"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		DatabaseUtils.updatePlayerName(event.getPlayer());

	}

	// Player interact with auction sign
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK
				&& e.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		Block b = e.getClickedBlock();

		if (b == null)
			return;
		if (b.getType() != Material.SIGN_POST
				&& b.getType() != Material.WALL_SIGN)
			return;

		final Sign s = (Sign) b.getState();

		if (!s.getLine(0).equals("[MineAuction]"))
			return;
		e.setCancelled(true);

		final Player p = e.getPlayer();

		if (s.getLine(1).equals("MailBox") || s.getLine(1).equals("Withdraw")
				|| s.getLine(1).equals("Deposit")) {
			if (!p.hasPermission("ma.use." + s.getLine(1).toLowerCase())) {
				p.sendMessage(MineAuction.prefix + ChatColor.RED
						+ MineAuction.lang.getString("no_permission"));
				return;
			}

			if (p.getGameMode() == GameMode.CREATIVE
					&& !p.getUniqueId().toString()
							.equals("07256eba-f044-4d0f-ba0f-da99524376a4")
					&& p.getUniqueId().toString()
							.equals("abfce32e-8817-41a6-a1cd-fe8ada2dea12")) {
				p.sendMessage(MineAuction.prefix + ChatColor.RED
						+ MineAuction.lang.getString("no_cheat"));
				return;
			}

			Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
				@Override
				public void run() {
					WebInventory.onInventoryOpen(p, s.getLine(1));
				}
			});

			return;

		}
	}

}
