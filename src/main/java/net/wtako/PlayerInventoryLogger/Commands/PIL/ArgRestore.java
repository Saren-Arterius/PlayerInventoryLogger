package net.wtako.PlayerInventoryLogger.Commands.PIL;

import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Utils.CommandHelper;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgRestore {

    @SuppressWarnings("deprecation")
    public ArgRestore(final CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_RESTORE.toString(), CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        Integer rowID;
        try {
            rowID = Integer.parseInt(args[1]);
        } catch (final NumberFormatException e) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_RESTORE.toString(), CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        Player target;
        if (args.length > 2) {
            target = Main.getInstance().getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(MessageFormat.format(Lang.PLAYER_NOT_FOUND.toString(), args[2]));
                return;
            }
        } else {
            target = (Player) sender;
        }

        PIL.restoreInventory(sender, target, rowID);
    }
}
