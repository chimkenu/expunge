package me.chimkenu.expunge.commands;

import me.chimkenu.expunge.Expunge;
import me.chimkenu.expunge.game.Director;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Values implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Game Stats:");
        sender.sendMessage(ChatColor.GRAY + "  isGameRunning: " + Expunge.isGameRunning);
        sender.sendMessage(ChatColor.GRAY + "  isSpawningEnabled: " + Expunge.isSpawningEnabled);
        sender.sendMessage(ChatColor.GRAY + "  players: " + Expunge.playing.getKeys().size());
        sender.sendMessage(ChatColor.GRAY + "  spectators: " + Expunge.spectators.size());
        if (Expunge.isGameRunning) {
            Director director = Expunge.runningDirector;
            sender.sendMessage(ChatColor.GRAY + "  currentMap: " + Expunge.currentMap.getName());
            sender.sendMessage(ChatColor.GRAY + "  currentSceneIndex: " + Expunge.currentSceneIndex);
            sender.sendMessage(ChatColor.GRAY + "  difficulty: " + Expunge.difficulty);
            sender.sendMessage(ChatColor.GRAY + "  gameTime: " + director.getGameTime());
            sender.sendMessage(ChatColor.GRAY + "  sceneTime: " + director.getSceneTime());
            sender.sendMessage(ChatColor.GRAY + "  sceneAttempts: " + director.getSceneAttempts());
            sender.sendMessage(ChatColor.GRAY + "  activeMobs: " + director.getActiveMobs().size());
            sender.sendMessage(ChatColor.GRAY + "  totalKills: " + director.getTotalKills());
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            decimalFormat.setRoundingMode(RoundingMode.CEILING);
            sender.sendMessage(ChatColor.GRAY + "  directorRating: " + decimalFormat.format((director.calculateRating() * 100)) + "%");
        } else {
            sender.sendMessage(ChatColor.RED + "There is no active game at the moment.");
        }
        return true;
    }
}
