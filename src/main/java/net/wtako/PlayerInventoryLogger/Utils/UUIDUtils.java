package net.wtako.PlayerInventoryLogger.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import net.wtako.PlayerInventoryLogger.Main;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class UUIDUtils {

    private static HashMap<String, String> uuidCache = new HashMap<String, String>();

    public static String getWorldName(UUID uuid) {
        final World world = Main.getInstance().getServer().getWorld(uuid);
        if (world != null) {
            return world.getName();
        }
        return Lang.WORLD_NOT_FOUND.toString();
    }

    public static String getPlayerName(UUID uuid) {
        final Player player = Main.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            UUIDUtils.uuidCache.put(player.getUniqueId().toString(), player.getName());
            return player.getName();
        }
        if (UUIDUtils.uuidCache.containsKey(uuid.toString())) {
            return UUIDUtils.uuidCache.get(uuid.toString());
        }
        String result = uuid.toString();
        final Essentials ess = JavaPlugin.getPlugin(Essentials.class);
        if (ess != null) {
            for (final File userdata: UUIDUtils.getChildFile(ess.getDataFolder(), "userdata", false).listFiles()) {
                final String[] filename = userdata.getName().split("\\.");
                if (filename.length != 2) {
                    continue;
                }
                final YamlConfiguration userData = YamlConfiguration.loadConfiguration(userdata);
                final String storedUUID = userData.getString("uuid");
                if (storedUUID == null) {
                    continue;
                }
                final String username = filename[0];
                UUIDUtils.uuidCache.put(storedUUID, username);
                if (uuid.toString().equalsIgnoreCase(storedUUID)) {
                    result = username;
                }
            }
        }
        return result;
    }

    public static File getChildFile(File parent, String child, boolean ignoreCase) {
        try {
            if (ignoreCase) {
                for (final File subFile: parent.listFiles()) {
                    if (subFile.getName().equalsIgnoreCase(child)) {
                        return subFile;
                    }
                }
            } else {
                for (final File subFile: parent.listFiles()) {
                    if (subFile.getName().equals(child)) {
                        return subFile;
                    }
                }
            }
            return null;
        } catch (final Exception e) {
            return null;
        }
    }

}
