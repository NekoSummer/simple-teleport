package me.shiqui.simpleteleport.utils;

import me.shiqui.simpleteleport.SimpleTeleport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class MessageHelper {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static String stringFromConfig(String path){
        FileConfiguration config = SimpleTeleport.plugin.getConfig();
        String msg = config.getString("prefix") + " " + config.getString(path);
        //return Objects.requireNonNull(ChatColor.translateAlternateColorCodes('&', msg));
        Component component = LEGACY.deserialize(Objects.requireNonNull(msg));
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
