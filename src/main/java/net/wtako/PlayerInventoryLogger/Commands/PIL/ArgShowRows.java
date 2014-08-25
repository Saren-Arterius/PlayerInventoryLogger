package net.wtako.PlayerInventoryLogger.Commands.PIL;

import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Methods.PIL.LogReason;
import net.wtako.PlayerInventoryLogger.Utils.CommandHelper;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ArgShowRows {

    @SuppressWarnings("deprecation")
    public ArgShowRows(final CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_SHOW_ROWS.toString(),
                    CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        final OfflinePlayer target = Main.getInstance().getServer().getOfflinePlayer(args[1]);
        if (target == null || target.getUniqueId() == null) {
            sender.sendMessage(MessageFormat.format(Lang.PLAYER_NOT_FOUND.toString(), args[1]));
            return;
        }

        int inclusiveFrom = 0;
        int inclusiveTo = 0;
        LogReason reason = null;

        try {
            inclusiveFrom = Integer.parseInt(args[2]);
        } catch (final NumberFormatException e) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_SHOW_ROWS.toString(),
                    CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        if (args.length > 3) {
            try {
                inclusiveTo = Integer.parseInt(args[3]);
            } catch (final NumberFormatException e) {
                sender.sendMessage(MessageFormat.format(Lang.HELP_SHOW_ROWS.toString(),
                        CommandHelper.joinArgsInUse(args, 1)));
                return;
            }
        } else {
            inclusiveTo = inclusiveFrom;
        }

        if (args.length > 4) {
            try {
                reason = LogReason.valueOf(args[4].toUpperCase().replace("-", "_"));
            } catch (final IllegalArgumentException e) {
                sender.sendMessage(MessageFormat.format(Lang.HELP_SHOW_ROWS.toString(),
                        CommandHelper.joinArgsInUse(args, 1)));
                String msg = "";
                for (int i = 0; i < LogReason.values().length; i++) {
                    msg += LogReason.values()[i].name().toLowerCase().replace("_", "-");
                    if (i < LogReason.values().length - 1) {
                        msg += ", ";
                    }
                }
                sender.sendMessage(MessageFormat.format(Lang.AVAILABLE_LOG_REASONS.toString(), msg));
                return;
            }
        }

        PIL.showRows(sender, target, reason, inclusiveFrom, inclusiveTo);

    }
}
