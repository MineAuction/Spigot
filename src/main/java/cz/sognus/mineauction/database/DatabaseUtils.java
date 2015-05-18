package cz.sognus.mineauction.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.WebInventoryMeta;

/**
 * 
 * @author Sognus
 */
public class DatabaseUtils {

	public static void registerPlayer(Player p) {
		if (playerRegistered(p.getUniqueId()))
			return;

		try {
			Connection con = MineAuction.db.getConnection();
			PreparedStatement ps = con
					.prepareStatement("INSERT INTO ma_players (playerName, uuid) VALUES (?, ?)");
			ps.setString(1, p.getName());
			ps.setString(2, p.getUniqueId().toString());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean playerRegistered(UUID playerUUID) {
		boolean registered = false;

		try {
			Connection con = MineAuction.db.getConnection();
			PreparedStatement ps = con
					.prepareStatement("SELECT COUNT(*) FROM ma_players WHERE uuid = ?");
			ps.setString(1, playerUUID.toString());
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				registered = rs.getInt(1) == 1 ? true : false;
				return registered;
			}

			ps.close();
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return registered;

	}

	public static int getPlayerId(UUID playerUUID) {
		PreparedStatement st = null;
		ResultSet rs = null;
		int output = 0;

		try {
			Connection conn = MineAuction.db.getConnection();

			st = conn
					.prepareStatement("SELECT * FROM ma_players WHERE uuid = ?  LIMIT 1");
			st.setString(1, playerUUID.toString());

			rs = st.executeQuery();

			while (rs.next()) {
				output = rs.getInt("id");
			}

			rs.close();
			st.close();

			return output;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;

	}

	public static boolean isItemInDatabase(WebInventoryMeta wim, int playerID) {
		try {
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = null;
			ResultSet rs = null;
			@SuppressWarnings("unused")
			int output = 0;

			// Create preparedStatement
			ps = conn
					.prepareStatement("SELECT COUNT(*) FROM ma_items WHERE playerID = ? AND itemID= ? AND itemDamage = ? AND enchantments = ? AND lore = ?");
			ps.setInt(1, playerID);
			ps.setInt(2, wim.getId());
			ps.setShort(3, wim.getDurability());
			// ps.setString(4, wim.getItemMeta());
			ps.setString(4, wim.getItemEnchantments());
			ps.setString(5, wim.getLore());

			// Get resultSet
			rs = ps.executeQuery();

			// Iterate over resultSet
			while (rs.next()) {
				return rs.getInt(1) > 0 ? true : false;
			}

			return false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public static void updatePlayerName(Player p) {
		int playerID = getPlayerId(p.getUniqueId());
		try {
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("UPDATE ma_players SET playerName=? WHERE id=?");
			ps.setString(1, p.getName());
			ps.setInt(2, playerID);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	public static boolean areItemsInDatabase(Player pl, ItemStack is, int qty) {
		if (is == null)
			return false;
		int playerID = getPlayerId(pl.getUniqueId());
		WebInventoryMeta wim = new WebInventoryMeta(is);
		ResultSet rs = null;
		try {
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM ma_items WHERE id=? AND itemDamage=? AND enchantments = ? AND qty >=?");
			ps.setInt(1, is.getType().getId());
			ps.setShort(2, is.getDurability());
			ps.setString(3, wim.getItemEnchantments());
			ps.setInt(4, qty);

			rs = ps.executeQuery();

			// Iterate over resultSet
			while (rs.next()) {
				return rs.getInt(1) > 0 ? true : false;
			}

			return false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	@SuppressWarnings("deprecation")
	public static ResultSet getItemFromDatabase(ItemStack is) {
		if (is == null)
			throw new NullPointerException();
		WebInventoryMeta wim = new WebInventoryMeta(is);
		ResultSet rs = null;
		try {
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM ma_items WHERE itemID=? AND itemDamage=? AND enchantments = ? ORDER BY qty DESC LIMIT 1");
			ps.setInt(1, is.getType().getId());
			ps.setShort(2, is.getDurability());
			ps.setString(3, wim.getItemEnchantments());

			rs = ps.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	@SuppressWarnings("deprecation")
	public static int getItemCountDatabase(ItemStack is) {
		if (is == null)
			throw new NullPointerException();
		WebInventoryMeta wim = new WebInventoryMeta(is);
		ResultSet rs = null;
		try {
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM ma_items WHERE itemID=? AND itemDamage=? AND enchantments = ? ORDER BY qty DESC LIMIT 1");
			ps.setInt(1, is.getType().getId());
			ps.setShort(2, is.getDurability());
			ps.setString(3, wim.getItemEnchantments());

			rs = ps.executeQuery();

			while (rs.next()) {
				return rs.getInt("qty");
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean updateItemInDatabase(Player p, ItemStack is,
			int qtyDelete) {
		if (is == null || p == null)
			throw new NullPointerException();
		WebInventoryMeta wim = new WebInventoryMeta(is);
		try {
			int dbQty = getItemCountDatabase(is);

			// Database vars
			Connection conn = MineAuction.db.getConnection();
			PreparedStatement ps = null;

			// Not enough items in database
			if (qtyDelete > dbQty) {
				return false;
			}

			// Delete row
			if (qtyDelete == dbQty) {
				ps = conn
						.prepareStatement("DELETE FROM ma_items WHERE itemID=? AND itemDamage=? AND enchantments = ?");
				ps.setInt(1, wim.getId());
				ps.setShort(2, wim.getDurability());
				ps.setString(3, wim.getItemEnchantments());

				ps.execute();

				// Return true if success
				return true;
			} else {
				// UPDATE ROW
				ps = conn
						.prepareStatement("UPDATE ma_items SET qty = qty - ? WHERE itemID=? AND itemDamage=? AND enchantments = ?");
				ps.setInt(1, qtyDelete);
				ps.setInt(2, wim.getId());
				ps.setShort(3, wim.getDurability());
				ps.setString(4, wim.getItemEnchantments());

				ps.execute();

				// Return true if success
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
