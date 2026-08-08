package com.example.woodensword;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Central place for all the NamespacedKeys used to tag the special sword's
 * PersistentDataContainer. Using PersistentDataContainer means this data is
 * stored on the ItemStack itself (in its NBT), so it survives:
 *  - server restarts
 *  - logging out / back in
 *  - dropping / picking up
 *  - moving between inventories and containers (chests, etc.)
 */
final class SwordKeys {

    private static NamespacedKey swordIdKey;
    private static NamespacedKey killsKey;
    private static NamespacedKey sharpnessKey;

    private SwordKeys() {
    }

    static void init(Plugin plugin) {
        swordIdKey = new NamespacedKey(plugin, "sword_id");
        killsKey = new NamespacedKey(plugin, "kills");
        sharpnessKey = new NamespacedKey(plugin, "sharpness_level");
    }

    /** Unique per-sword identifier (String UUID). Marks an item as a genuine special sword. */
    static NamespacedKey swordId() {
        return swordIdKey;
    }

    /** Number of players killed with this specific sword (Integer). */
    static NamespacedKey kills() {
        return killsKey;
    }

    /** Current Sharpness enchantment level applied to this sword (Integer). */
    static NamespacedKey sharpness() {
        return sharpnessKey;
    }
}
