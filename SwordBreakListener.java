package com.example.woodensword;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

import java.time.Instant;

/**
 * Listens for PlayerItemBreakEvent, which Bukkit/Paper fires exactly when an
 * item's durability reaches its maximum and the item is actually destroyed
 * (NOT when it's merely damaged or nearly broken). If the broken item is the
 * special sword, the wielder is permanently banned using the server's normal
 * (profile-based) ban list.
 */
final class SwordBreakListener implements Listener {

    private static final String BAN_REASON = "Your special wooden sword broke. You have been permanently banned.";

    private final Plugin plugin;

    SwordBreakListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        ItemStack broken = event.getBrokenItem();
        if (!SwordManager.isSpecialSword(broken)) {
            return;
        }

        Player player = event.getPlayer();
        banPermanently(player);
    }

    private void banPermanently(Player player) {
        PlayerProfile profile = player.getPlayerProfile();

        BanList<PlayerProfile> banList = Bukkit.getBanList(BanList.Type.PROFILE);
        // expires = null (Instant) => permanent ban.
        banList.addBan(profile, BAN_REASON, (Instant) null, plugin.getName());

        player.kick(Component.text(BAN_REASON, NamedTextColor.RED));

        plugin.getLogger().info("Permanently banned " + player.getName() + " because their special sword broke.");
    }
}
