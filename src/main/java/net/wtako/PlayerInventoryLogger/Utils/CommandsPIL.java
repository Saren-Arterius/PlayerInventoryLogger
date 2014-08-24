package net.wtako.PlayerInventoryLogger.Utils;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgHasItem;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgHelp;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgLogAllNow;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgReload;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgRestore;
import net.wtako.PlayerInventoryLogger.Commands.PIL.ArgShowLog;

public enum CommandsPIL implements BaseCommands {

    MAIN_COMMAND(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    LAN(Lang.HELP_LOG_ALL_NOW.toString(), ArgLogAllNow.class, Main.artifactId + ".admin"),
    LOG_ALL_NOW(Lang.HELP_LOG_ALL_NOW.toString(), ArgLogAllNow.class, Main.artifactId + ".admin"),
    SL(Lang.HELP_SHOW_LOG.toString(), ArgShowLog.class, Main.artifactId + ".admin"),
    SHOW_LOG(Lang.HELP_SHOW_LOG.toString(), ArgShowLog.class, Main.artifactId + ".admin"),
    RST(Lang.HELP_RESTORE.toString(), ArgRestore.class, Main.artifactId + ".admin"),
    RESTORE(Lang.HELP_RESTORE.toString(), ArgRestore.class, Main.artifactId + ".admin"),
    HI(Lang.HELP_HAS_ITEM.toString(), ArgHasItem.class, Main.artifactId + ".admin"),
    HAS_ITEM(Lang.HELP_HAS_ITEM.toString(), ArgHasItem.class, Main.artifactId + ".admin"),
    H(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    HELP(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    RELOAD(Lang.HELP_RELOAD.toString(), ArgReload.class, Main.artifactId + ".reload");

    private String   helpMessage;
    private Class<?> targetClass;
    private String   permission;

    private CommandsPIL(String helpMessage, Class<?> targetClass, String permission) {
        this.helpMessage = helpMessage;
        this.targetClass = targetClass;
        this.permission = permission;
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public String getRequiredPermission() {
        return permission;
    }
}
