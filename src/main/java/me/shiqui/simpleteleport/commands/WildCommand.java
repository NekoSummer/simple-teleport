package me.shiqui.simpleteleport.commands;

import me.shiqui.simpleteleport.SimpleTeleport;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class WildCommand implements CommandExecutor {
    public SimpleTeleport plugin = (SimpleTeleport) Bukkit.getPluginManager().getPlugin("SimpleTeleport");
    private final Random r = new Random();

    private boolean isAir(Block block) {
        Material type = block.getType();
        return type == Material.AIR
                || type == Material.CAVE_AIR
                || type == Material.VOID_AIR;
    }

    private boolean isSafeGround(Block block) {
        Material type = block.getType();
        if (isAir(block)) return false;
        if (type == Material.WATER || type == Material.LAVA) return false;
        if (type == Material.CACTUS || type == Material.MAGMA_BLOCK) return false;
        if (type == Material.FIRE) return false;
        // 1.13 里树叶的枚举名都是 OAK_LEAVES / SPRUCE_LEAVES 这种
        if (type.name().endsWith("_LEAVES")) return false;
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Audience audSender = plugin.audiences().sender(sender);
        if (!(sender instanceof Player)) {
            audSender.sendMessage(Component.text("This command can only be executed by a player.", NamedTextColor.RED));
            return true;
        }

        Player p = (Player) sender;
        Audience audPlayer = plugin.audiences().player(p);

        World w = p.getWorld();
        Location l = p.getLocation();

        for (int i = 0; i < 30; i++) {
            int dx = this.r.nextInt(1001) - 500;
            int dz = this.r.nextInt(1001) - 500;
            int x = l.getBlockX() + dx;
            int z = l.getBlockZ() + dz;


            if (w.isChunkLoaded(x >> 4, z >> 4)) {
                w.loadChunk(x >> 4, z >> 4, true);
            }

            int y = w.getHighestBlockYAt(x, z);

            if (y <= 0) continue;

            Block g = w.getBlockAt(x, y, z);
            Block f = w.getBlockAt(x, y + 1, z);
            Block h = w.getBlockAt(x, y + 2, z);

            if (isSafeGround(g) && isAir(f) && isAir(h)) {
                Location d = new Location(w, x + 0.5, y + 1, z + 0.5, l.getYaw(), l.getPitch());

                /** 死了都要try */
                try {
                    if (p.isOnline()){
                        p.teleport(d);
                        audPlayer.sendMessage(Component.text("Teleported you to <X:" + x + " Y:" + (y + 1) + " Z:" + z + ">", NamedTextColor.GREEN));
                    }
                } catch (Exception e) {
                    audPlayer.sendMessage(Component.text("An error occurred. Please try again.", NamedTextColor.RED));
                    audSender.sendMessage(Component.text("Error while teleporting player \"" + p.getName() + "\" : " + e.getMessage(), NamedTextColor.RED));
                    return true;
                }
                return true;
            }
        }

        audPlayer.sendMessage(Component.text("Could not find a safe location. Please try again.", NamedTextColor.YELLOW));
        return true;
    }

}
