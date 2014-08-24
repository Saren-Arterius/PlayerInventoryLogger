package net.wtako.PlayerInventoryLogger.Utils;

import java.util.List;

import net.wtako.PlayerInventoryLogger.Main;

import org.bukkit.configuration.file.FileConfiguration;

public enum Config {

    LOG_ECON("logger.loc-econ", true),
    AUTO_PURGE_BEFORE_DAYS("auto-purge.before-days", 7),
    AUTO_PURGE_INTERVAL_MINUTES("auto-purge.interval-minutes", 30),
    AUTO_LOG_INTERVAL_SECONDS("logger.auto-log.interval-seconds", 60);

    private String path;
    private Object value;

    Config(String path, Object var) {
        this.path = path;
        final FileConfiguration config = Main.getInstance().getConfig();
        if (config.contains(path)) {
            value = config.get(path);
        } else {
            value = var;
        }
    }

    public Object getValue() {
        return value;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public String getString() {
        return (String) value;
    }

    public int getInt() {
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        return (int) value;
    }

    public long getLong() {
        return Integer.valueOf(getInt()).longValue();
    }

    public double getDouble() {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return (double) value;
    }

    public String getPath() {
        return path;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getValues() {
        return (List<Object>) value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStrings() {
        return (List<String>) value;
    }

    public static void saveAll() {
        final FileConfiguration config = Main.getInstance().getConfig();
        for (final Config setting: Config.values()) {
            if (!config.contains(setting.getPath())) {
                config.set(setting.getPath(), setting.getValue());
            }
        }
        Main.getInstance().saveConfig();
    }

}