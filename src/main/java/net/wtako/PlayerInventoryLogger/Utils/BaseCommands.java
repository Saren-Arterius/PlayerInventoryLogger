package net.wtako.PlayerInventoryLogger.Utils;

public interface BaseCommands {

    public String getHelpMessage();

    public String name();

    public Class<?> getTargetClass();

    public String getRequiredPermission();

}
