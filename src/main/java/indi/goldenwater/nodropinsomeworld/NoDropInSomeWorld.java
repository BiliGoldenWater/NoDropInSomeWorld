package indi.goldenwater.nodropinsomeworld;

import indi.goldenwater.nodropinsomeworld.command.MainCommand;
import indi.goldenwater.nodropinsomeworld.listener.OnPlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoDropInSomeWorld extends JavaPlugin {
    private static NoDropInSomeWorld instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new OnPlayerDropItemEvent(), this);
        MainCommand.register();

        getLogger().info("Enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
        getLogger().info("Disabled.");
    }

    public static NoDropInSomeWorld getInstance(){
        return instance;
    }
}
