package com.example.woodensword;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for player deaths. If the victim was killed by another player who
 * was holding the special sword in their main hand, registers a kill on that
 * sword (increasing its Sharpness).
 *
 * Only PLAYER kills count - mob kills never trigger this listener at all,
 * since PlayerDeathEvent only fires for player deaths, and we additionally
 * require the killer to be a Player (not e.g. null for environmental deaths).
 *
 * PlayerDeathEvent fires exactly once per death, so a single kill can never
 * grant more than one Sharpness increase.
 */
final class SwordKillListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return; // Not killed by a player (mob, environment, etc.) - no Sharpness gain.
        }

        EntityEquipment equipment = killer.getEquipment();
        if (equipment == null) {
            return;
        }

        ItemStack weapon = equipment.getItemInMainHand();
        if (!SwordManager.isSpecialSword(weapon)) {
            return;
        }

        SwordManager.registerPlayerKill(weapon);
        // getItemInMainHand() returns the live item reference for online players' inventories,
        // but we write it back explicitly to guarantee the change persists in all cases.
        equipment.setItemInMainHand(weapon);
    }
}
