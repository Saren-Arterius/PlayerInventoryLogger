package net.wtako.PlayerInventoryLogger.Commands.PIL;

import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Methods.PIL.LogReason;
import net.wtako.PlayerInventoryLogger.Utils.Config;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArgLogAllNow {

    private static boolean lock = false;

    public ArgLogAllNow(final CommandSender sender, String[] args) {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.MANUAL.name())) {
            sender.sendMessage(MessageFormat.format(Lang.LOG_TYPE_DISABLED.toString(), LogReason.MANUAL.name()));
            return;
        }
        if (ArgLogAllNow.lock) {
            sender.sendMessage(Lang.LOG_ALL_NOW_RUNNING.toString());
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ArgLogAllNow.lock = true;
                sender.sendMessage(Lang.LOG_ALL_NOW_START.toString());
                for (final Player player: Main.getInstance().getServer().getOnlinePlayers()) {
                    sender.sendMessage(MessageFormat.format(Lang.LOG_ALL_NOW_LOGGING_WHO.toString(), player.getName()));
                    PIL.logPlayer(player, LogReason.MANUAL, false);
                }
                sender.sendMessage(Lang.LOG_ALL_NOW_FINISH.toString());
                ArgLogAllNow.lock = false;
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
