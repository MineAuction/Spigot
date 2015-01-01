package cz.sognus.mineauction.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import cz.sognus.mineauction.MineAuction;

public class DatabaseUtils {

	public static boolean playerRegistered(UUID playerUUID)
	{
		Connection con = MineAuction.db.getConnection();
		PreparedStatement ps = null;
		boolean registered = false;
		
		try
		{
			ps = con.prepareStatement("SELECT COUNT(*) FROM ma_players WHERE uuid = ?");
			ps.setString(1, playerUUID.toString());
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				registered = rs.getInt(1) == 1 ? true : false;
				return registered;
			}
			
			
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
			
			conn.close();
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

}


