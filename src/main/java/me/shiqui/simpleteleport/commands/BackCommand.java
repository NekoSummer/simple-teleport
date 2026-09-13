package me.shiqui.simpleteleport.commands;

import me.shiqui.simpleteleport.SimpleTeleport;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommand implements CommandExecutor, Listener {
    private final SimpleTeleport plugin;
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    public BackCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        deathLocations.put(p.getUniqueId(), p.getLocation().clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        deathLocations.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Audience audSender = plugin.audiences().sender(sender);
        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Audience audPlayer = plugin.audiences().player(player);
        Location deathPoint = deathLocations.get(player.getUniqueId());

        if (deathPoint == null) {
            audPlayer.sendMessage(Component.text("You have no recorded death location.", NamedTextColor.RED));
            return true;
        }

        deathPoint.getWorld().getChunkAt(deathPoint.getBlockX() >> 4, deathPoint.getBlockZ() >> 4).load();

        /** 死了都要try */
        try {
            if (player.isOnline()) {
                player.teleport(deathPoint);
                audPlayer.sendMessage(Component.text("You have returned to your death location.", NamedTextColor.GREEN));
                // 防止玩家重复返回死亡点
                deathLocations.remove(player.getUniqueId());
            }
        } catch (Exception e) {
            audPlayer.sendMessage(Component.text("An error occurred. Please try again.", NamedTextColor.RED));
            audSender.sendMessage(Component.text("Error returning player \"" + player.getName() + "\" to death point: " + e.getMessage()));
            return true;
        }

        return true;
    }
}
