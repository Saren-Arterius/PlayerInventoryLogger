package net.wtako.PlayerInventoryLogger.Utils;

import net.wtako.PlayerInventoryLogger.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum for requesting strings from the language file.
 * 
 * @author gomeow
 */
public enum Lang {

    TITLE("[" + Main.getInstance().getName() + "]"),

    RESTORE_SUCCESS("Successfully restore inventory log ID {0} to <{1}>."),
    NO_SUCH_ROW("Log ID {0} not found."),

    LOG_ALL_NOW_START("&eStarted logging all online players' inventories..."),
    LOG_ALL_NOW_LOGGING_WHO("Logging {0}..."),
    LOG_ALL_NOW_FINISH("&aFinished logging all online players' inventories."),

    SHOW_LOG_TITLE("Inventory Log for {0} between {1} and {2}:"),
    SHOW_LOG_FORMAT("{0}. Time: {1}, World: {2}, X: {3}, Y: {4}, Z: {5}, Balance: ${7}, Exp: {8}, Log reason: {6}"),

    AVAILABLE_LOG_REASONS("Available log reasons: {0}"),
    PLAYER_NOT_FOUND("&ePlayer {0} is not found."),

    CHECK_OWNS_TITLE("Checking player <{0}> (UUID: {1})..."),
    CHECK_OWNS_AIR("&cYou are holding nothing, please hold an item."),
    CHECK_OWNS_SINCE("{0} owns this item &astarts from &frow ID &b{1}&f."),
    CHECK_OWNS_UNTIL("{0} owns this item &cuntil &frow ID &b{1}&f."),
    CHECK_OWNS_ROW_FORMAT(
            "(Owner: {8}, Time: {0}, World: {1}, X: {2}, Y: {3}, Z: {4}, Balance: ${6}, Exp: {7}, Log reason: {5})"),
            CHECK_OWNS_NO_RESULT("&eThere is no result."),
            WORLD_NOT_FOUND("&c(world not found)&f"),

            COMMAND_HELP_SEPERATOR("&6 | &a"),
            COMMAND_ARG_IN_USE("&e{0}&a"),
            SUB_COMMAND("Sub-command: &e{0}"),
            HELP_HAS_ITEM("Type &b/" + Main.getInstance().getProperty("mainCommand")
                    + " &a{0}&f <TargetPlayer> [LimitCycles = 1] to see "
                    + "whether the player has ever own the item similar to the one you are holding."),
                    HELP_LOG_ALL_NOW("Type &b/" + Main.getInstance().getProperty("mainCommand")
                            + " &a{0}&f to save all players' inventories now."),
                            HELP_SHOW_LOG("Type &b/" + Main.getInstance().getProperty("mainCommand")
                                    + " &a{0}&f <PlayerName> [MinutesSpan = 60] [MinutesBefore = 0] "
                                    + "[LogReason = ALL] show a player's inventory log."),
                                    HELP_RESTORE("Type &b/" + Main.getInstance().getProperty("mainCommand")
                                            + " &a{0}&f <RowID> [RestoreTarget = You] to restore a player's inventory to somebody."),
                                            HELP_HELP("Type &b/" + Main.getInstance().getProperty("mainCommand") + " &a{0}&f to show help (this message)."),
                                            HELP_RELOAD("Type &b/" + Main.getInstance().getProperty("mainCommand") + " &a{0}&f to reload the plugin."),
                                            NO_PERMISSION_HELP(" (&cno permission&f)"),
                                            PLUGIN_RELOADED("&aPlugin reloaded."),
                                            NO_PERMISSION_COMMAND("&cYou are not allowed to use this command.");

    private String                   path;
    private String                   def;
    private static YamlConfiguration LANG;

    /**
     * Lang enum constructor.
     * 
     * @param path
     *            The string path.
     * @param start
     *            The default string.
     */
    Lang(String start) {
        path = name().toLowerCase().replace("_", "-");
        def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     * 
     * @param config
     *            The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        Lang.LANG = config;
    }

    @Override
    public String toString() {
        if (this == TITLE) {
            return ChatColor.translateAlternateColorCodes('&', Lang.LANG.getString(path, def)) + " ";
        }
        return ChatColor.translateAlternateColorCodes('&', Lang.LANG.getString(path, def));
    }

    /**
     * Get the default value of the path.
     * 
     * @return The default value of the path.
     */
    public String getDefault() {
        return def;
    }

    /**
     * Get the path to the string.
     * 
     * @return The path to the string.
     */
    public String getPath() {
        return path;
    }
}