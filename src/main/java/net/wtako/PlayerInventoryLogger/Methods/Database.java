package net.wtako.PlayerInventoryLogger.Methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

public class Database {

    private static Database instance;
    private static int      latestVersion = 1;
    public Connection       conn;

    public Database() throws SQLException {
        Database.instance = this;
        final String path = MessageFormat.format("jdbc:sqlite:{0}/{1}", Main.getInstance().getDataFolder()
                .getAbsolutePath(), Main.getInstance().getName() + ".db");
        conn = DriverManager.getConnection(path);
        check();
    }

    private void addConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO `configs` (`config`, `value`) VALUES (?, ?)");
        stmt.setString(1, config);
        stmt.setString(2, value);
        stmt.execute();
        stmt.close();
    }

    private boolean areTablesExist() {
        try {
            final Statement cur = conn.createStatement();
            cur.execute("SELECT * FROM `configs` LIMIT 0");
            cur.close();
            return true;
        } catch (final SQLException ex) {
            return false;
        }
    }

    public void check() throws SQLException {
        Main.log.info(Lang.TITLE.toString() + "Checking database...");
        if (!areTablesExist()) {
            Main.log.info(Lang.TITLE.toString() + "Creating tables...");
            createTables();
            Main.log.info(Lang.TITLE.toString() + "Done.");
        }
    }

    public void createTables() throws SQLException {
        final Statement cur = conn.createStatement();
        final String stmt = "CREATE TABLE `inventory_logs` (" + "`row_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "`player_uuid` CHAR(36) NOT NULL," + "`world_uuid` CHAR(36) NOT NULL," + "`x` INT NOT NULL,"
                + "`y` INT NOT NULL," + "`z` INT NOT NULL," + "`reason` VARCHAR(16) NOT NULL,"
                + "`inventory` TEXT NOT NULL," + "`exp` DOUBLE NOT NULL," + "`balance` DOUBLE NOT NULL,"
                + "`timestamp` INT NOT NULL" + ")";
        cur.execute(stmt);
        cur.execute("CREATE TABLE `configs` (`config` TEXT PRIMARY KEY, `value` TEXT NULL)");
        cur.close();
        addConfig("database_version", String.valueOf(Database.latestVersion));
    }

    public static Connection getConn() {
        return Database.instance.conn;
    }

    public static Database getInstance() {
        return Database.instance;
    }

}