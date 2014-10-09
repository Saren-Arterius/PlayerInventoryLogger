package net.wtako.PlayerInventoryLogger.Schedulers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.Database;
import net.wtako.PlayerInventoryLogger.Utils.Config;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.scheduler.BukkitRunnable;

public class LogPurger extends BukkitRunnable {

    private static LogPurger instance;

    public LogPurger() {
        LogPurger.instance = this;
        runTaskTimerAsynchronously(Main.getInstance(), 100L, Config.AUTO_PURGE_INTERVAL_MINUTES.getLong() * 60L * 20L);
    }

    @Override
    public void run() {
        if (!Config.AUTO_PURGE_ENABLED.getBoolean()) {
            return;
        }
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        final long purgeBefore = System.currentTimeMillis()
                - (Config.AUTO_PURGE_BEFORE_DAYS.getInt() * 1000L * 60L * 60L * 24L);
        Main.log.info(Lang.TITLE.toString()
                + MessageFormat.format("Purging data before {0} days... (< {1})",
                        Config.AUTO_PURGE_BEFORE_DAYS.getInt(), formatter.format(new Date(purgeBefore))));
        try {
            final PreparedStatement delStmt = Database.getConn().prepareStatement(
                    "DELETE FROM inventory_logs WHERE timestamp < ?");
            delStmt.setLong(1, purgeBefore);
            delStmt.execute();
            delStmt.close();
            Main.log.info("Compacting database...");
            final PreparedStatement vacStmt = Database.getConn().prepareStatement("VACUUM");
            vacStmt.executeUpdate();
            vacStmt.close();
            Main.log.info("Done.");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    public static LogPurger getInstance() {
        return LogPurger.instance;
    }

}
