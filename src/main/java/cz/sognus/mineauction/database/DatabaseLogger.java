package cz.sognus.mineauction.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Location;
import org.bukkit.block.Block;

import cz.sognus.mineauction.MineAuction;

public class DatabaseLogger {

	// Create new record about auction point
	// args: player id, attached block, sign block, auction type
	public static void logRecordAuctionCreate(int creator, Block attached,
			Block sign, String type) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String blockID = attached.getType().name();
			
			conn = MineAuction.db.getConnection();
			ps = conn
					.prepareStatement("INSERT INTO ma_points (creator, type, block, x, y, z) VALUES (?,?, ?, ?, ?, ?)");
			ps.setInt(1, creator);
			ps.setString(2, type);
			ps.setString(3, blockID);
			ps.setInt(4, sign.getX());
			ps.setInt(5, sign.getY());
			ps.setInt(6, sign.getZ());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Log user action: create auction point
	public static void logUserAuctionCreate() {
		// TODO: Log user action
	}
	
	// Remove record from database on auction point destroy
	public static String deleteRecordAuction(Location l)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		String blockID = null;
		
		// Retrieve information about old block
		try
		{
			conn = MineAuction.db.getConnection();
			ps = conn.prepareStatement("SELECT block FROM ma_points WHERE x=? AND y=? AND z=?");
			ps.setInt(1, l.getBlockX());
			ps.setInt(2, l.getBlockY());
			ps.setInt(3, l.getBlockZ());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				blockID = rs.getString("block");
				break;
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			conn = MineAuction.db.getConnection();
			ps = conn.prepareStatement("DELETE FROM ma_points WHERE x=? AND y=? AND z=?");
			ps.setInt(1, l.getBlockX());
			ps.setInt(2, l.getBlockY());
			ps.setInt(3, l.getBlockZ());
			ps.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

		return blockID;

		
	}

}
