package cz.sognus.mineauction.database;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import com.google.common.io.CharStreams;

import cz.sognus.mineauction.MineAuction;
import cz.sognus.mineauction.utils.Log;

/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 * @author Sognus
 */
public class Database {
	private final String user;
	public String database;
	private final String password;
	private final String port;
	private final String hostname;

	private Connection connection;
	private Plugin plugin;

	public Database(Plugin plugin) {
		this.plugin = plugin;
		this.hostname = MineAuction.config
				.getString("plugin.general.mysql.host");
		this.port = MineAuction.config.getString("plugin.general.mysql.port");
		this.database = MineAuction.config
				.getString("plugin.general.mysql.database");
		this.user = MineAuction.config.getString("plugin.general.mysql.user");
		this.password = MineAuction.config
				.getString("plugin.general.mysql.password");
		this.connection = null;
	}

	public Connection openConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://"
				+ this.hostname + ":" + this.port + "/" + this.database,
				this.user, this.password);
		return connection;
	}

	public boolean checkConnection() {
		return connection != null;
	}

	public Connection getConnection() throws Exception {
		try {
			openConnection();
			return connection;
		} catch (Exception e) {
			MineAuction.plugin.onDisable();
			throw e;
		}
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				if (MineAuction.enabled) {
					plugin.getLogger().log(Level.SEVERE,
							"Error closing the MySQL Connection!");
					e.printStackTrace();
					plugin.onDisable();
				}
			}
		}
	}

	// Sognus code:
	public void createTables() throws Exception {
		if (!MineAuction.enabled) return;
		
		Connection c = null;
		Statement s = null;

		c = MineAuction.db.openConnection();
		s = c.createStatement();

		if (plugin.getResource("create.sql") == null) {
			if (MineAuction.enabled) {
				Log.error("Unable to find create.sql, disabling plugin");
				MineAuction.plugin.onDisable();
				return;
			}
		}

	    String[] queries = CharStreams.toString(
				new InputStreamReader(plugin.getResource("create.sql"))).split(
				";");
		for (String query : queries) {
			if (query != null && query != "")
				s.execute(query);
		}

	}
}