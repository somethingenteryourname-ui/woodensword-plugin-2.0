package com.example.woodensword;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Handles:
 *   /woodensword          -> brand new sword, Kills 0 / Sharpness 0
 *   /woodensword <level>  -> brand new sword, Kills 0 / Sharpness <level>
 *
 * Only OPs may use this command (enforced both via plugin.yml's permission,
 * default "op", and an explicit isOp() check here as a safety net).
 */
final class WoodenSwordCommand implements CommandExecutor, TabCompleter {

    private static final Set<Integer> VALID_LEVELS = Set.of(5, 10, 15, 20, 25, 30, 35);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                              @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(Component.text("You do not have permission to use /woodensword.", NamedTextColor.RED));
            return true;
        }

        int startingSharpness = 0;

        if (args.length >= 1) {
            Integer parsed = parseLevel(args[0]);
            if (parsed == null) {
                player.sendMessage(Component.text(
                        "Invalid level. Valid Sharpness levels are: 5, 10, 15, 20, 25, 30, 35",
                        NamedTextColor.RED));
                return true;
            }
            startingSharpness = parsed;
        }

        ItemStack sword = SwordManager.createSword(startingSharpness);
        var leftover = player.getInventory().addItem(sword);
        if (!leftover.isEmpty()) {
            // Inventory was full - drop it at the player's feet instead of losing it.
            leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            player.sendMessage(Component.text(
                    "Your inventory was full, so the sword was dropped at your feet.", NamedTextColor.YELLOW));
        }

        if (startingSharpness > 0) {
            player.sendMessage(Component.text(
                    "You received a Kill Sword with starting Sharpness " + startingSharpness + ".",
                    NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("You received a brand-new Kill Sword.", NamedTextColor.GREEN));
        }

        return true;
    }

    private Integer parseLevel(String arg) {
        int value;
        try {
            value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
        return VALID_LEVELS.contains(value) ? value : null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                  @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (Integer level : VALID_LEVELS) {
                String levelStr = String.valueOf(level);
                if (levelStr.startsWith(args[0])) {
                    options.add(levelStr);
                }
            }
        }
        return options;
    }
}
