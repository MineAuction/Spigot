package cz.sognus.mineauction.database;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
 */
public class Database
{
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;

    private Connection connection;
    private Plugin plugin;

    public Database(Plugin plugin)
    {
        this.plugin = plugin;
        this.hostname = MineAuction.config.getString("mysql.host");
        this.port = MineAuction.config.getString("mysql.port");
        this.database = MineAuction.config.getString("mysql.database");
        this.user = MineAuction.config.getString("mysql.user");
        this.password = MineAuction.config.getString("mysql.password");
        this.connection = null;
    }

    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL server! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "JDBC Driver not found!");
        }
        return connection;
    }

    public boolean checkConnection() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error closing the MySQL Connection!");
                e.printStackTrace();
            }
        }
    }

    public ResultSet querySQL(String query) {
        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        ResultSet ret = null;

        try {
            ret = s.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();

        return ret;
    }

    public void updateSQL(String update) {

        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
            s.executeUpdate(update);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        closeConnection();

    }
    
    // Sognus code:
    public void createTables()
    {
    	Log.info("Checking database structure");
    	Connection c = null;
    	Statement s = null;
    	
    	try
    	{
    		c = MineAuction.db.openConnection();
    		s = c.createStatement();
    		
    		if(plugin.getResource("create.sql") == null)
    		{
    			Log.error("Unable to find create.sql, disabling plugin");
    			MineAuction.plugin.onDisable();
    			return;
    		}
    		
			String[] queries = CharStreams.toString(new InputStreamReader(plugin.getResource("create.sql"))).split(";");
			Log.debug("Creating DB table");
			for (String query : queries) {
				s.execute(query);
			}
		}
    	catch(Exception e)
    	{
    		if(MineAuction.config.getBool("debug")) e.printStackTrace();
    		MineAuction.plugin.onDisable();
    	}
    	finally
    	{
    		try
    		{
    			if(s != null) 
    				s.close();
    			c.close();	
    		}
    		catch(Exception e)
    		{
    			Log.error("Unable to close connection.");
    		}
    	}
    }
}