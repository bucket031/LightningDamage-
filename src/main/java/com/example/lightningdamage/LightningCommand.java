package com.example.lightningdamage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** /lightning config | set <0-100> | reload */
public final class LightningCommand implements TabExecutor {

    private final LightningDamage plugin;

    public LightningCommand(LightningDamage plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "config" -> {
                if (sender instanceof Player player) {
                    new LightningMenu(plugin).open(player);
                } else {
                    msg(sender, "The config menu can only be opened in-game.", NamedTextColor.RED);
                }
            }
            case "set" -> set(sender, args);
            case "reload" -> {
                plugin.reloadConfig();
                msg(sender, "LightningDamage config reloaded.", NamedTextColor.GREEN);
            }
            default -> {
                msg(sender, "Lightning damage is currently " + LightningDamage.format(plugin.multiplier()) + "x.",
                        NamedTextColor.GOLD);
                msg(sender, "/" + label + " config  - open the menu", NamedTextColor.WHITE);
                msg(sender, "/" + label + " set <0-100>  - set the multiplier", NamedTextColor.WHITE);
                msg(sender, "/" + label + " reload  - reload config.yml", NamedTextColor.WHITE);
            }
        }
        return true;
    }

    private void set(CommandSender sender, String[] args) {
        if (args.length < 2) {
            msg(sender, "Usage: /lightning set <0-100>", NamedTextColor.YELLOW);
            return;
        }
        double value;
        try {
            value = Double.parseDouble(args[1].toLowerCase(Locale.ROOT).replace("x", ""));
        } catch (NumberFormatException e) {
            msg(sender, "'" + args[1] + "' is not a number.", NamedTextColor.RED);
            return;
        }
        if (Double.isNaN(value) || value < LightningDamage.MIN || value > LightningDamage.MAX) {
            msg(sender, "Pick a number from 0 to 100.", NamedTextColor.RED);
            return;
        }
        plugin.setMultiplier(value);
        msg(sender, "Lightning damage is now " + LightningDamage.format(plugin.multiplier()) + "x.",
                NamedTextColor.GREEN);
    }

    private static void msg(CommandSender to, String text, NamedTextColor color) {
        to.sendMessage(Component.text(text, color));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            out.addAll(List.of("config", "set", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            out.addAll(List.of("0", "1", "2", "5", "10", "50", "100"));
        }
        String typed = args[args.length - 1].toLowerCase(Locale.ROOT);
        out.removeIf(s -> !s.startsWith(typed));
        return out;
    }
}
