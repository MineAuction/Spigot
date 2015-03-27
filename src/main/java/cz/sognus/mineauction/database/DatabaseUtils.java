package cz.sognus.mineauction.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WebInventoryMeta;

/**
 * 
 * @author Sognus
 */
public class DatabaseUtils {

	public static void registerPlayer(Player p)
	{
		if(playerRegistered(p.getUniqueId())) return;
		
		try
		{
			Connection con = MineAuction.db.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO ma_players (playerName, uuid) VALUES (?, ?)");
			ps.setString(1, p.getName());
			ps.setString(2, p.getUniqueId().toString());
			ps.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public static boolean playerRegistered(UUID playerUUID)
	{
		boolean registered = false;
		
		try
		{
			Connection con = MineAuction.db.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ma_players WHERE uuid = ?");
			ps.setString(1, playerUUID.toString());
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				registered = rs.getInt(1) == 1 ? true : false;
				return registered;
			}
			
			ps.close();
			rs.close();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return registered;
		
	}
	
	public static int getPlayerId(UUID playerUUID)
	{
		Connection conn = MineAuction.db.getConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
		int output = 0;
		
		try
		{
			st = conn.prepareStatement("SELECT * FROM ma_players WHERE uuid = ?  LIMIT 1");
			st.setString(1, playerUUID.toString());
			
			rs = st.executeQuery();
			
			while(rs.next())
			{
				output = rs.getInt("id");
			}
			
			rs.close();
			st.close();
			
			return output;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return output;
		
	}
	
	public static String decodeMetadata(ItemStack i)
	{
		return i.serialize().toString();
	}
	
	public static boolean isItemInDatabase(WebInventoryMeta wim, int playerID)
	{	
		try
		{
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = null;
			ResultSet rs = null;
			int output = 0;
			
			// Create preparedStatement
			ps = conn.prepareStatement("SELECT COUNT(*) FROM ma_items WHERE playerID = ? AND itemID= ? AND itemDamage = ? AND itemMeta = ? AND enchantments = ? AND lore = ?");
			ps.setInt(1, playerID);
			ps.setInt(2, wim.getId());
			ps.setShort(3, wim.getDurability());
			ps.setString(4, wim.getItemMeta());
			ps.setString(5, wim.getItemEnchantments());
			ps.setString(6, wim.getLore());
			
			Bukkit.broadcastMessage("ItemInDatabaseSQL: "+ps.toString());
			
			// Get resultSet
			rs = ps.executeQuery();
			
			// Iterate over resultSet
			while(rs.next())
			{
				return rs.getInt(1) > 0 ? true : false;
			}
			
			return false;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
		
		
	}
	
	

}


