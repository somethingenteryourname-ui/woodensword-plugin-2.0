package com.example.woodensword;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles everything about the special "Kill Sword":
 *  - creating a brand-new one (with an optional starting Sharpness level)
 *  - checking whether a given ItemStack is a genuine special sword
 *  - recording a player kill and recalculating Sharpness
 *  - keeping the display lore in sync with the sword's stored stats
 *
 * The sword is only ever identified via its PersistentDataContainer
 * "sword_id" tag, never by its display name or lore, so it cannot be
 * spoofed by giving a normal wooden sword a matching name/lore.
 */
final class SwordManager {

    static final int MAX_SHARPNESS = 35;
    static final int SHARPNESS_PER_KILL = 5;

    private SwordManager() {
    }

    /**
     * Creates a brand-new special Kill Sword.
     *
     * @param startingSharpness the Sharpness level the sword should start with (0 for a fresh sword).
     *                          This does NOT affect the kill counter.
     */
    static ItemStack createSword(int startingSharpness) {
        int sharpness = clamp(startingSharpness);

        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = sword.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(SwordKeys.swordId(), PersistentDataType.STRING, UUID.randomUUID().toString());
        pdc.set(SwordKeys.kills(), PersistentDataType.INTEGER, 0);
        pdc.set(SwordKeys.sharpness(), PersistentDataType.INTEGER, sharpness);

        meta.displayName(Component.text("Kill Sword", NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        applyEnchant(meta, sharpness);
        applyLore(meta, 0, sharpness);

        sword.setItemMeta(meta);
        return sword;
    }

    /**
     * True if the given item is a genuine special sword, identified solely by its
     * persistent "sword_id" tag (never by display name/lore, which can be faked).
     */
    static boolean isSpecialSword(ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_SWORD) {
            return false;
        }
        if (!item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(SwordKeys.swordId(), PersistentDataType.STRING);
    }

    /**
     * Records a player kill on the given sword: increments the kill counter,
     * recalculates Sharpness (capped at MAX_SHARPNESS), updates the enchantment
     * and lore, and writes the updated meta back onto the passed ItemStack.
     *
     * The caller is responsible for writing the (mutated) ItemStack back into
     * the relevant inventory slot, since Bukkit inventory getters can return
     * a detached copy of the item.
     */
    static void registerPlayerKill(ItemStack sword) {
        if (!isSpecialSword(sword)) {
            return;
        }

        ItemMeta meta = sword.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        int kills = pdc.getOrDefault(SwordKeys.kills(), PersistentDataType.INTEGER, 0);
        kills = kills + 1;

        int sharpness = clamp(kills * SHARPNESS_PER_KILL);

        pdc.set(SwordKeys.kills(), PersistentDataType.INTEGER, kills);
        pdc.set(SwordKeys.sharpness(), PersistentDataType.INTEGER, sharpness);

        applyEnchant(meta, sharpness);
        applyLore(meta, kills, sharpness);

        sword.setItemMeta(meta);
    }

    private static void applyEnchant(ItemMeta meta, int sharpness) {
        meta.removeEnchant(Enchantment.SHARPNESS);
        if (sharpness > 0) {
            // ignoreLevelRestriction = true, since Sharpness can exceed vanilla's normal max of 5.
            meta.addEnchant(Enchantment.SHARPNESS, sharpness, true);
        }
    }

    private static void applyLore(ItemMeta meta, int kills, int sharpness) {
        List<Component> lore = new ArrayList<>();
        lore.add(legacy("§7Kills: §f" + kills));
        lore.add(legacy("§7Sharpness: §f" + sharpness));
        meta.lore(lore);
    }

    private static Component legacy(String legacyText) {
        return LegacyComponentSerializer.legacySection().deserialize(legacyText);
    }

    private static int clamp(int sharpness) {
        if (sharpness < 0) {
            return 0;
        }
        return Math.min(sharpness, MAX_SHARPNESS);
    }
}
