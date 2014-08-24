package net.wtako.PlayerInventoryLogger.EventHandlers;

import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Methods.PIL.LogReason;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        PIL.logPlayer(event.getPlayer(), LogReason.LOGIN, true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PIL.logPlayer(event.getPlayer(), LogReason.LOGOUT, true);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        PIL.logPlayer(event.getPlayer(), LogReason.CHANGE_WORLD, true);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final ItemStack[] invContents = event.getEntity().getInventory().getContents();
        final ItemStack[] armorContents = event.getEntity().getInventory().getArmorContents();
        PIL.logPlayer(event.getEntity(), invContents, armorContents, LogReason.DEATH, true);
    }

}