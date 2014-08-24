package net.wtako.PlayerInventoryLogger.Methods;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Utils.ExperienceManager;
import net.wtako.PlayerInventoryLogger.Utils.ItemUtils;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PIL {

    public enum LogReason {
        AUTO_LOG,
        LOGIN,
        LOGOUT,
        DEATH,
        MANUAL,
        CHANGE_WORLD,
    }

    public static void logPlayer(final Player player, final ItemStack[] invContents, final ItemStack[] armorContents,
            final LogReason reason, boolean forceAsync) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final PreparedStatement insStmt = Database.getConn().prepareStatement(
                            "INSERT INTO inventory_logs (`player_uuid`, `world_uuid`, "
                                    + "`x`, `y`, `z`, `reason`, `inventory`, `timestamp`, "
                                    + "`balance`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    insStmt.setString(1, player.getUniqueId().toString());
                    insStmt.setString(2, player.getWorld().getUID().toString());
                    insStmt.setInt(3, player.getLocation().getBlockX());
                    insStmt.setInt(4, player.getLocation().getBlockY());
                    insStmt.setInt(5, player.getLocation().getBlockZ());
                    insStmt.setString(6, reason.name());
                    insStmt.setString(7, ItemUtils.encodeInventory(invContents, armorContents).toJSONString());
                    insStmt.setLong(8, System.currentTimeMillis());
                    double balance = 0;
                    if (Main.econ != null) {
                        balance = Main.econ.getBalance(player);
                    }
                    insStmt.setDouble(9, balance);
                    insStmt.setDouble(10, new ExperienceManager(player).getCurrentExp());
                    insStmt.execute();
                    insStmt.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        if (forceAsync) {
            runnable.runTaskAsynchronously(Main.getInstance());
        } else {
            runnable.run();
        }
    }

    public static void logPlayer(final Player player, final LogReason reason, boolean forceAsync) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final PreparedStatement insStmt = Database.getConn().prepareStatement(
                            "INSERT INTO inventory_logs (`player_uuid`, `world_uuid`, "
                                    + "`x`, `y`, `z`, `reason`, `inventory`, `timestamp`, "
                                    + "`balance`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    insStmt.setString(1, player.getUniqueId().toString());
                    insStmt.setString(2, player.getWorld().getUID().toString());
                    insStmt.setInt(3, player.getLocation().getBlockX());
                    insStmt.setInt(4, player.getLocation().getBlockY());
                    insStmt.setInt(5, player.getLocation().getBlockZ());
                    insStmt.setString(6, reason.name());
                    insStmt.setString(7, ItemUtils.encodeInventory(player.getInventory()).toJSONString());
                    insStmt.setLong(8, System.currentTimeMillis());
                    double balance = 0;
                    if (Main.econ != null) {
                        balance = Main.econ.getBalance(player);
                    }
                    insStmt.setDouble(9, balance);
                    insStmt.setDouble(10, new ExperienceManager(player).getCurrentExp());
                    insStmt.execute();
                    insStmt.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        if (forceAsync) {
            runnable.runTaskAsynchronously(Main.getInstance());
        } else {
            runnable.run();
        }
    }

    public static void showLog(final CommandSender sender, final OfflinePlayer target, final LogReason reason,
            final int minutesSpan, final int minutesBefore) {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    PreparedStatement selStmt;
                    final long currentTime = System.currentTimeMillis();
                    if (reason != null) {
                        selStmt = Database.getConn().prepareStatement(
                                "SELECT * FROM inventory_logs WHERE player_uuid = ? "
                                        + "AND (timestamp BETWEEN ? AND ?) AND reason = ?");
                    } else {
                        selStmt = Database.getConn().prepareStatement(
                                "SELECT * FROM inventory_logs WHERE player_uuid = ? "
                                        + "AND (timestamp BETWEEN ? AND ?)");
                    }
                    selStmt.setString(1, target.getUniqueId().toString());
                    selStmt.setLong(2, currentTime - (minutesBefore * 60000L) - (minutesSpan * 60000L));
                    selStmt.setLong(3, currentTime - (minutesBefore * 60000L));
                    if (reason != null) {
                        selStmt.setString(4, reason.name());
                    }
                    final ResultSet result = selStmt.executeQuery();
                    final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    sender.sendMessage(MessageFormat.format(
                            Lang.SHOW_LOG_TITLE.toString(),
                            target.getName(),
                            formatter.format(new Date(currentTime - (minutesBefore * 60000L) - (minutesSpan * 60000L))),
                            formatter.format(new Date(currentTime - (minutesBefore * 60000L)))));
                    while (result.next()) {
                        String worldName = Lang.WORLD_NOT_FOUND.toString();
                        final World world = Main.getInstance().getServer()
                                .getWorld(UUID.fromString(result.getString("world_uuid")));
                        if (world != null) {
                            worldName = world.getName();
                        }
                        sender.sendMessage(MessageFormat.format(Lang.SHOW_LOG_FORMAT.toString(),
                                result.getInt("row_id"), formatter.format(new Date(result.getLong("timestamp"))),
                                worldName, result.getInt("x"), result.getInt("y"), result.getInt("z"),
                                result.getString("reason"), result.getDouble("balance"), result.getDouble("exp")));
                    }
                    result.close();
                    selStmt.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public static void restoreInventory(final CommandSender sender, final Player target, final int rowID) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final PreparedStatement insStmt = Database.getConn().prepareStatement(
                            "SELECT * FROM inventory_logs WHERE row_id = ?");
                    insStmt.setInt(1, rowID);
                    final ResultSet result = insStmt.executeQuery();
                    if (!result.next()) {
                        sender.sendMessage(MessageFormat.format(Lang.NO_SUCH_ROW.toString(), rowID));
                        result.close();
                        insStmt.close();
                        return;
                    }
                    final String json = result.getString("inventory");
                    final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    String worldName = Lang.WORLD_NOT_FOUND.toString();
                    final World world = Main.getInstance().getServer()
                            .getWorld(UUID.fromString(result.getString("world_uuid")));
                    if (world != null) {
                        worldName = world.getName();
                    }
                    final String msg = MessageFormat.format(Lang.CHECK_OWNS_ROW_FORMAT.toString(),
                            formatter.format(new Date(result.getLong("timestamp"))), worldName, result.getInt("x"),
                            result.getInt("y"), result.getInt("z"), result.getString("reason"),
                            result.getDouble("balance"), result.getDouble("exp"));
                    result.close();
                    insStmt.close();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemUtils.restoreInventory(target.getInventory(), json);
                            sender.sendMessage(MessageFormat.format(Lang.RESTORE_SUCCESS.toString(), rowID,
                                    target.getName()));
                            sender.sendMessage(msg);
                        }
                    }.runTask(Main.getInstance());
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public static void showPlayerEverHasSimilarItem(final CommandSender sender, final OfflinePlayer target,
            final ItemStack stack, final Integer limitCycles) {
        new BukkitRunnable() {
            @SuppressWarnings({"unchecked", "deprecation"})
            @Override
            public void run() {
                try {
                    final ArrayList<Integer> untilRowIDs = new ArrayList<Integer>();
                    final ArrayList<Integer> sinceRowIDs = new ArrayList<Integer>();
                    new ArrayList<Integer>();
                    Integer untilRowID = null;
                    Integer sinceRowID = null;
                    final Integer localLimitCycles = limitCycles < 1 ? 1 : limitCycles;
                    final PreparedStatement insStmt = Database.getConn().prepareStatement(
                            "SELECT * FROM inventory_logs WHERE player_uuid = ? ORDER BY row_id DESC");
                    insStmt.setString(1, target.getUniqueId().toString());
                    ResultSet result = insStmt.executeQuery();
                    while (result.next()) {
                        boolean invHasSimilarItem = false;
                        final Map<String, Object> itemJson = (Map<String, Object>) JSONValue.parse(result
                                .getString("inventory"));
                        final ArrayList<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(ItemUtils
                                .restoreItems((ArrayList<JSONObject>) itemJson.get("content"))));
                        items.add(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("helmet")));
                        items.add(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("chestplate")));
                        items.add(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("leggings")));
                        items.add(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("boots")));
                        for (final ItemStack item: items) {
                            if (item != null && item.isSimilar(stack)) {
                                invHasSimilarItem = true;
                                break;
                            }
                        }
                        if (invHasSimilarItem && untilRowID == null) {
                            untilRowID = result.getInt("row_id");
                        } else if (!invHasSimilarItem && untilRowID != null && sinceRowID == null) {
                            sinceRowID = result.getInt("row_id");
                        }
                        if (untilRowID != null && sinceRowID != null) {
                            untilRowIDs.add(untilRowID);
                            sinceRowIDs.add(sinceRowID);
                            untilRowID = null;
                            sinceRowID = null;
                            if (localLimitCycles != null && untilRowIDs.size() == localLimitCycles) {
                                break;
                            }
                        }
                    }
                    if (untilRowID != null && sinceRowID == null) {
                        untilRowIDs.add(untilRowID);
                        sinceRowIDs.add(untilRowID);
                    }
                    result.close();
                    insStmt.close();
                    final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    boolean hasResult = false;
                    for (int i = 0; i < untilRowIDs.size(); i++) {
                        sender.sendMessage("============================");
                        hasResult = true;
                        PreparedStatement selStmt = Database.getConn().prepareStatement(
                                "SELECT * FROM inventory_logs WHERE row_id = ?");
                        selStmt.setInt(1, sinceRowIDs.get(i));
                        result = selStmt.executeQuery();
                        String worldName = Lang.WORLD_NOT_FOUND.toString();
                        World world = Main.getInstance().getServer()
                                .getWorld(UUID.fromString(result.getString("world_uuid")));
                        if (world != null) {
                            worldName = world.getName();
                        }
                        sender.sendMessage(MessageFormat.format(Lang.CHECK_OWNS_SINCE.toString(), target.getName(),
                                sinceRowIDs.get(i)));
                        sender.sendMessage(MessageFormat.format(Lang.CHECK_OWNS_ROW_FORMAT.toString(),
                                formatter.format(new Date(result.getLong("timestamp"))), worldName, result.getInt("x"),
                                result.getInt("y"), result.getInt("z"), result.getString("reason"),
                                result.getDouble("balance"), result.getDouble("exp")));
                        result.close();
                        selStmt.close();
                        selStmt = Database.getConn().prepareStatement("SELECT * FROM inventory_logs WHERE row_id = ?");
                        selStmt.setInt(1, untilRowIDs.get(i));
                        result = selStmt.executeQuery();
                        worldName = Lang.WORLD_NOT_FOUND.toString();
                        world = Main.getInstance().getServer()
                                .getWorld(UUID.fromString(result.getString("world_uuid")));
                        if (world != null) {
                            worldName = world.getName();
                        }
                        sender.sendMessage(MessageFormat.format(Lang.CHECK_OWNS_UNTIL.toString(), target.getName(),
                                untilRowIDs.get(i)));
                        sender.sendMessage(MessageFormat.format(Lang.CHECK_OWNS_ROW_FORMAT.toString(),
                                formatter.format(new Date(result.getLong("timestamp"))), worldName, result.getInt("x"),
                                result.getInt("y"), result.getInt("z"), result.getString("reason"),
                                result.getDouble("balance"), result.getDouble("exp")));
                        result.close();
                        selStmt.close();
                    }
                    if (!hasResult) {
                        sender.sendMessage(Lang.CHECK_OWNS_NO_RESULT.toString());
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
