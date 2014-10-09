package net.wtako.PlayerInventoryLogger.Schedulers;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Methods.PIL.LogReason;
import net.wtako.PlayerInventoryLogger.Utils.Config;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoLogger extends BukkitRunnable {

    private static AutoLogger instance;

    public AutoLogger() {
        AutoLogger.instance = this;
        runTaskTimerAsynchronously(Main.getInstance(), 0L, Config.AUTO_LOG_INTERVAL_SECONDS.getLong() * 20L);
    }

    @Override
    public void run() {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.AUTO_LOG.name())) {
            return;
        }
        for (final Player player: Main.getInstance().getServer().getOnlinePlayers()) {
            PIL.logPlayer(player, LogReason.AUTO_LOG, false);
        }
    }

    public static AutoLogger getInstance() {
        return AutoLogger.instance;
    }

}
