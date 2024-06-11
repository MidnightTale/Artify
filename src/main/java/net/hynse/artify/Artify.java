package net.hynse.artify;

import org.bukkit.plugin.java.JavaPlugin;

public final class Artify extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HangingPlaceListener(this), this);
    }
}