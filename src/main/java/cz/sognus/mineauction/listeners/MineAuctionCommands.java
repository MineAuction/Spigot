package cz.sognus.mineauction.listeners;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import cz.sognus.mineauction.MineAuction;

public class MineAuctionCommands implements CommandExecutor {

	private MineAuction plugin;
	
	public MineAuctionCommands(MineAuction plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		int params = args.length;
		
		// Command was sent by player
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			
			// Command has no argument
			if(params == 0)
			{
				return false;
			}
			
			// Command has 1 argument
			if(params == 1)
			{
				// Command: ma version
				if(args[0].equalsIgnoreCase("version"))
				{
					String version = MineAuction.version;
					player.sendMessage(MineAuction.prefix + "Current version is: " + version);
					return true;
				}
				
			}
			
			
		}
		
		
		return false;
	}

}
