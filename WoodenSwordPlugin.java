package com.example.woodensword;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for the WoodenSwordPlugin.
 *
 * Registers the /woodensword command and the listeners that:
 *  - increase Sharpness when the special sword is used to kill another player
 *  - permanently ban a player whose special sword breaks
 */
public final class WoodenSwordPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SwordKeys.init(this);

        WoodenSwordCommand command = new WoodenSwordCommand();
        getCommand("woodensword").setExecutor(command);
        getCommand("woodensword").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new SwordKillListener(), this);
        getServer().getPluginManager().registerEvents(new SwordBreakListener(this), this);

        getLogger().info("WoodenSwordPlugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WoodenSwordPlugin disabled.");
    }
}
