package net.wtako.PlayerInventoryLogger.EventHandlers;

import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Methods.PIL.LogReason;
import net.wtako.PlayerInventoryLogger.Utils.Config;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.LOGIN.name())) {
            return;
        }
        PIL.logPlayer(event.getPlayer(), LogReason.LOGIN, true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.LOGOUT.name())) {
            return;
        }
        PIL.logPlayer(event.getPlayer(), LogReason.LOGOUT, true);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.CHANGE_WORLD.name())) {
            return;
        }
        PIL.logPlayer(event.getPlayer(), LogReason.CHANGE_WORLD, true);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (!Config.LOG_WHEN.getStrings().contains(LogReason.DEATH.name())) {
            return;
        }
        PIL.logPlayer(event.getEntity(), LogReason.DEATH, true);
    }

}
