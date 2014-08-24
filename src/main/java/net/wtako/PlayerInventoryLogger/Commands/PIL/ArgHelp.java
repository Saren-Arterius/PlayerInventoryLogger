package net.wtako.PlayerInventoryLogger.Commands.PIL;

import net.wtako.PlayerInventoryLogger.Utils.CommandHelper;
import net.wtako.PlayerInventoryLogger.Utils.CommandsPIL;

import org.bukkit.command.CommandSender;

public class ArgHelp {

    public ArgHelp(final CommandSender sender, String[] args) {
        CommandHelper.sendHelp(sender, CommandsPIL.values(), "");
    }
}
