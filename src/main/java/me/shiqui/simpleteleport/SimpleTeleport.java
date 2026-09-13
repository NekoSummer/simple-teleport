package me.shiqui.simpleteleport;

import me.shiqui.simpleteleport.commands.*;
import me.shiqui.simpleteleport.listeners.LogOutListener;
import me.shiqui.simpleteleport.tasks.ClearExpiredRequestTask;
import me.shiqui.simpleteleport.utils.DatabaseHelper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public final class SimpleTeleport extends JavaPlugin {
    private BukkitAudiences audiences;

    public @NotNull BukkitAudiences audiences() {
        if (audiences == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return audiences;
    }

    public static SimpleTeleport plugin;

    public BackCommand backCommand = new BackCommand(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        audiences = BukkitAudiences.create(this);
        plugin = this;
        saveDefaultConfig();
        DatabaseHelper.initialize("jdbc:sqlite:" + this.getDataFolder() + "/SimpleTP.db");

        // Register commands
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());

        getCommand("tpr").setExecutor(new TeleportRequestCommand());
        getCommand("tpa").setExecutor(new TeleportAcceptCommand());
        getCommand("tpd").setExecutor(new TeleportDenyCommand());

        getCommand("setwarp").setExecutor(new SetWarpCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("dewarp").setExecutor(new RemoveWarpCommand());

        getCommand("wild").setExecutor(new WildCommand());
        getCommand("killme").setExecutor(new KillMeCommand());
        getCommand("back").setExecutor(backCommand);

        // Register events
        getServer().getPluginManager().registerEvents(new LogOutListener(), this);
        getServer().getPluginManager().registerEvents(backCommand, this);

        // Run tasks
        new ClearExpiredRequestTask().runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        getLogger().info("Unloading SimpleTeleport");
        DatabaseHelper.disconnect();
    }


}
