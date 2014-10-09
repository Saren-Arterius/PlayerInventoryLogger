package net.wtako.PlayerInventoryLogger.Utils;

import java.util.Arrays;
import java.util.List;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;

import org.bukkit.configuration.file.FileConfiguration;

public enum Config {

    LOG_ECON("logger.loc-econ", true),
    AUTO_PURGE_ENABLED("auto-purge.enabled", true),
    AUTO_PURGE_BEFORE_DAYS("auto-purge.before-days", 14),
    AUTO_PURGE_INTERVAL_MINUTES("auto-purge.interval-minutes", 30),
    AUTO_LOG_INTERVAL_SECONDS("logger.auto-log.interval-seconds", 60),
    LOG_WHEN("logger.log-when-players", Arrays.asList(Config.enumNames(PIL.LogReason.values())));

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

    public void setValue(Object value) {
        this.value = value;
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
            } else {
                setting.setValue(config.get(setting.getPath()));
            }
        }
        Main.getInstance().saveConfig();
    }

    @SuppressWarnings("rawtypes")
    public static String[] enumNames(Enum[] enums) {
        final String[] names = new String[enums.length];

        for (int i = 0; i < enums.length; i++) {
            names[i] = enums[i].name();
        }

        return names;
    }

}