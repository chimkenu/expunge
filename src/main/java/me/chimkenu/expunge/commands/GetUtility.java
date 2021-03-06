package me.chimkenu.expunge.commands;

import me.chimkenu.expunge.Utils;
import me.chimkenu.expunge.guns.utilities.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetUtility implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
                return true;
            }

            for (Utility utility : Utils.getHealings()) {
                player.getInventory().addItem(utility.getUtility());
            }
            for (Utility utility : Utils.getThrowables()) {
                player.getInventory().addItem(utility.getUtility());
            }
            player.sendMessage(ChatColor.GREEN + "Here you go.");

        } else {
            sender.sendMessage(ChatColor.RED + "Only players may execute this command.");
        }
        return true;
    }
}
