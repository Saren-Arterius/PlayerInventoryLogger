package net.wtako.PlayerInventoryLogger.Commands.PIL;

import java.text.MessageFormat;

import net.wtako.PlayerInventoryLogger.Main;
import net.wtako.PlayerInventoryLogger.Methods.PIL;
import net.wtako.PlayerInventoryLogger.Utils.CommandHelper;
import net.wtako.PlayerInventoryLogger.Utils.Lang;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgHasItem {

    @SuppressWarnings("deprecation")
    public ArgHasItem(final CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageFormat.format(Lang.HELP_HAS_ITEM.toString(), CommandHelper.joinArgsInUse(args, 1)));
            return;
        }

        final Player player = (Player) sender;

        if (player.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(Lang.CHECK_OWNS_AIR.toString());
            return;
        }

        final OfflinePlayer target = Main.getInstance().getServer().getOfflinePlayer(args[1]);
        if (target == null || target.getUniqueId() == null) {
            sender.sendMessage(MessageFormat.format(Lang.PLAYER_NOT_FOUND.toString(), args[1]));
            return;
        }

        Integer limitCycles = 1;

        if (args.length > 2) {
            try {
                limitCycles = Integer.parseInt(args[2]);
            } catch (final NumberFormatException e) {
                sender.sendMessage(MessageFormat.format(Lang.HELP_HAS_ITEM.toString(),
                        CommandHelper.joinArgsInUse(args, 1)));
                return;
            }
        }

        sender.sendMessage(MessageFormat.format(Lang.CHECK_OWNS_TITLE.toString(), target.getName(),
                target.getUniqueId()));
        PIL.showPlayerEverHasSimilarItem(player, target, player.getItemInHand(), limitCycles);

    }
}
