package cz.sognus.mineauction.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.database.DatabaseUtils;
import cz.sognus.mineauction.utils.Lang;

public class MineAuctionCommands implements CommandExecutor {

	@SuppressWarnings("unused")
	private MineAuction plugin;

	public MineAuctionCommands(MineAuction plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("static-access")
	@EventHandler(priority = EventPriority.NORMAL)
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		int params = args.length;

		// Command was sent by player
		if (sender instanceof Player) {
			Player player = (Player) sender;

			// Command has no argument
			if (params == 0) {
				return false;
			}

			// Command has 1 argument
			if (params == 1) {
				// Command: ma version
				if (args[0].equalsIgnoreCase("version")) {
					String version = MineAuction.version;
					player.sendMessage(MineAuction.prefix + ChatColor.AQUA
							+ MineAuction.lang.getString("command_version")
							+ " " + version);
					return true;
				}

				// Command: ma reload
				if (args[0].equalsIgnoreCase("reload")) {
					// Permission: ma.admin.reload
					if (player.hasPermission("ma.admin.reload")) {
						MineAuction.plugin.onReload();
						player.sendMessage(MineAuction.plugin.prefix
								+ ChatColor.RED
								+ MineAuction.lang
										.getString("command_reload_success"));
						return true;
					} else {
						player.sendMessage(MineAuction.plugin.prefix
								+ ChatColor.RED
								+ MineAuction.lang
										.getString("command_reload_permission"));
						return true;
					}

				}

				return false;
			}

			// Command has 2 arguments
			if (params == 2) {
				// Command: ma lang reload
				if (args[0].equalsIgnoreCase("lang")
						&& args[1].equalsIgnoreCase("reload")) {
					if (player.hasPermission("ma.admin.lang.reload")) {
						MineAuction.plugin.reloadLang();
						player.sendMessage(MineAuction.prefix
								+ ChatColor.GREEN
								+ MineAuction.lang
										.getString("command_lang_reload_success"));
						return true;
					} else {
						player.sendMessage(MineAuction.plugin.prefix
								+ ChatColor.RED
								+ MineAuction.lang
										.getString("command_lang_reload_permission"));
						return true;

					}
				}

				// Command: ma lang reset
				if (args[0].equalsIgnoreCase("lang")
						&& args[1].equalsIgnoreCase("reset")) {
					if (player.hasPermission("ma.admin.lang.reset")) {
						// Delete files, copy them for jar and reload lang
						Lang.deleteLangFiles();
						MineAuction.plugin.reloadLang();
						player.sendMessage(MineAuction.plugin.prefix
								+ ChatColor.GREEN
								+ MineAuction.lang
										.getString("command_lang_reset_success"));

						return true;
					} else {
						player.sendMessage(MineAuction.plugin.prefix
								+ ChatColor.RED
								+ MineAuction.lang
										.getString("command_lang_reset_permission"));
						return true;
					}

				}

				// Command: ma config reload
				if (args[0].equalsIgnoreCase("config")
						&& args[1].equalsIgnoreCase("reload")) {
					if (player.hasPermission("ma.admin.config.reload")) {
						// TODO: Oznamovací zpráva
						MineAuction.plugin.reloadConfig();
						return true;
					} else {
						player.sendMessage(ChatColor.RED
								+ MineAuction.lang.getString("no_permission"));
						return true;
					}
				}
				
				// Command: ma password <password>
				if(args[0].equalsIgnoreCase("password"))
				{
					if(DatabaseUtils.getPlayerId(player.getUniqueId()) == 0)
					{
						DatabaseUtils.registerPlayer(player);
						player.sendMessage(MineAuction.prefix + ChatColor.YELLOW + MineAuction.lang.getString("account_created"));
					}
					DatabaseUtils.updatePlayerPassword(player.getUniqueId(), args[1]);
					player.sendMessage(MineAuction.prefix + ChatColor.GREEN + MineAuction.lang.getString("account_passwordchange"));
					return true;
				}

				return false;
			}

		}

		return false;
	}

}
