package net.wtako.PlayerInventoryLogger.Methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Utils.CompressUtils;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

public class Database {

    private static Database instance;
    private static int      latestVersion = 2;
    private int             currentVersion;
    public Connection       conn;

    public Database() throws SQLException {
        Database.instance = this;
        final String path = MessageFormat.format("jdbc:sqlite:{0}/{1}", Main.getInstance().getDataFolder()
                .getAbsolutePath(), Main.getInstance().getName() + ".db");
        conn = DriverManager.getConnection(path);
        check();
    }

    public void check() throws SQLException {
        Main.log.info(Lang.TITLE.toString() + "Checking database...");
        if (!areTablesExist()) {
            Main.log.info(Lang.TITLE.toString() + "Creating tables...");
            createTables();
        } else if ((currentVersion = getCurrentVersion()) < Database.latestVersion) {
            Main.log.info(Lang.TITLE.toString() + "Migrating database...");
            databaseMigrateFrom(currentVersion);
        }
        Main.log.info(Lang.TITLE.toString() + "Done.");
    }

    public void createTables() throws SQLException {
        final Statement cur = conn.createStatement();
        final String stmt = "CREATE TABLE `inventory_logs` (" + "`row_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "`player_uuid` CHAR(36) NOT NULL," + "`world_uuid` CHAR(36) NOT NULL," + "`x` INT NOT NULL,"
                + "`y` INT NOT NULL," + "`z` INT NOT NULL," + "`reason` VARCHAR(16) NOT NULL,"
                + "`inventory` BLOB NOT NULL," + "`exp` DOUBLE NOT NULL," + "`balance` DOUBLE NOT NULL,"
                + "`timestamp` INT NOT NULL" + ")";
        cur.execute(stmt);
        cur.execute("CREATE TABLE `configs` (`config` TEXT PRIMARY KEY, `value` TEXT NULL)");
        cur.close();
        addConfig("database_version", String.valueOf(Database.latestVersion));
    }

    private void addConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO `configs` (`config`, `value`) VALUES (?, ?)");
        stmt.setString(1, config);
        stmt.setString(2, value);
        stmt.execute();
        stmt.close();
    }

    private void changeConfig(String config, String value) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("UPDATE `configs` SET value = ? WHERE config = ?");
        stmt.setString(1, value);
        stmt.setString(2, config);
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

    private void databaseMigrateFrom(int version) throws SQLException {
        switch (version) {
            case 1:
                final PreparedStatement altStmt = conn.prepareStatement("ALTER TABLE `inventory_logs` "
                        + "RENAME TO `inventory_logs_old`");
                altStmt.execute();
                altStmt.close();
                final Statement cur = conn.createStatement();
                final String stmt = "CREATE TABLE `inventory_logs` (" + "`row_id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "`player_uuid` CHAR(36) NOT NULL," + "`world_uuid` CHAR(36) NOT NULL," + "`x` INT NOT NULL,"
                        + "`y` INT NOT NULL," + "`z` INT NOT NULL," + "`reason` VARCHAR(16) NOT NULL,"
                        + "`inventory` BLOB NOT NULL," + "`exp` DOUBLE NOT NULL," + "`balance` DOUBLE NOT NULL,"
                        + "`timestamp` INT NOT NULL" + ")";
                cur.execute(stmt);
                final PreparedStatement selStmt = Database.getInstance().conn
                        .prepareStatement("SELECT * FROM `inventory_logs_old`");
                final ResultSet result = selStmt.executeQuery();
                Database.getConn().setAutoCommit(false);
                PreparedStatement insStmt = null;
                while (result.next()) {
                    insStmt = Database.getInstance().conn
                            .prepareStatement("INSERT INTO `inventory_logs` (`player_uuid`, `world_uuid`, "
                                    + "`x`, `y`, `z`, `reason`, `inventory`, `exp`,  `balance`, `timestamp`) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    insStmt.setString(1, result.getString("player_uuid"));
                    insStmt.setString(2, result.getString("world_uuid"));
                    insStmt.setInt(3, result.getInt("x"));
                    insStmt.setInt(4, result.getInt("y"));
                    insStmt.setInt(5, result.getInt("z"));
                    insStmt.setString(6, result.getString("reason"));
                    insStmt.setBytes(7, CompressUtils.compress(result.getString("inventory")));
                    insStmt.setDouble(8, result.getDouble("exp"));
                    insStmt.setDouble(9, result.getDouble("balance"));
                    insStmt.setLong(10, result.getLong("timestamp"));
                    insStmt.execute();
                }
                Database.getConn().commit();
                Database.getConn().setAutoCommit(true);
                if (insStmt != null) {
                    insStmt.close();
                }
                result.close();
                selStmt.close();
                final PreparedStatement dropStmt = conn.prepareStatement("DROP TABLE `inventory_logs_old`");
                dropStmt.execute();
                dropStmt.close();
                changeConfig("database_version", "2");
        }
    }

    private int getCurrentVersion() throws SQLException {
        final PreparedStatement stmt = conn
                .prepareStatement("SELECT value FROM `configs` WHERE config = 'database_version'");
        final int dbVersion = stmt.executeQuery().getInt(1);
        stmt.close();
        return dbVersion;
    }

    public static Connection getConn() {
        return Database.instance.conn;
    }

    public static Database getInstance() {
        return Database.instance;
    }

}