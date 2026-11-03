package com.example.lightningdamage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The /lightning config menu.
 *   [ - ] [ bar: 7 clickable positions, 0x to 100x ] [ + ]
 * Click the bar to jump, or use - / + (1x per click, 10x with shift).
 */
public final class LightningMenu implements InventoryHolder {

    private static final int ICON_SLOT = 4;
    private static final int BASE = 9;          // first slot of the slider row
    private static final int MINUS = BASE;
    private static final int PLUS = BASE + 8;
    private static final int TRACK_START = BASE + 1;
    private static final int POSITIONS = 7;
    private static final int RESET_SLOT = 22;

    private final LightningDamage plugin;
    private final Inventory inventory;

    public LightningMenu(LightningDamage plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 27, Component.text("Lightning Damage"));
        render();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    // -------------------------------------------------------------- drawing

    private static double valueAt(int position) {
        return Math.round(LightningDamage.MAX * position / (POSITIONS - 1));
    }

    private static int positionOf(double value) {
        return (int) Math.round(value / LightningDamage.MAX * (POSITIONS - 1));
    }

    private void render() {
        double value = plugin.multiplier();

        ItemStack filler = item(Material.BLACK_STAINED_GLASS_PANE, " ", NamedTextColor.GRAY, List.of());
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, filler);

        double damage = LightningDamage.VANILLA_DAMAGE * value;
        inventory.setItem(ICON_SLOT, item(Material.LIGHTNING_ROD,
                "Lightning damage: " + LightningDamage.format(value) + "x", NamedTextColor.YELLOW, List.of(
                        "Normal lightning does 5 damage (2.5 hearts).",
                        "Now: " + LightningDamage.format(damage) + " damage ("
                                + LightningDamage.format(damage / 2.0) + " hearts)",
                        "",
                        "Armor and Resistance still reduce it.")));

        inventory.setItem(MINUS, item(Material.RED_CONCRETE, "- Damage", NamedTextColor.RED, List.of(
                "Click: -1x", "Shift-click: -10x")));
        inventory.setItem(PLUS, item(Material.GREEN_CONCRETE, "+ Damage", NamedTextColor.GREEN, List.of(
                "Click: +1x", "Shift-click: +10x")));

        int thumb = positionOf(value);
        for (int i = 0; i < POSITIONS; i++) {
            int slot = TRACK_START + i;
            if (i == thumb) {
                ItemStack knob = item(Material.GOLD_BLOCK,
                        "Damage: " + LightningDamage.format(value) + "x", NamedTextColor.GOLD,
                        List.of("Current value"));
                ItemMeta meta = knob.getItemMeta();
                meta.setEnchantmentGlintOverride(true);
                knob.setItemMeta(meta);
                inventory.setItem(slot, knob);
            } else {
                Material material = i < thumb
                        ? Material.YELLOW_STAINED_GLASS_PANE
                        : Material.LIGHT_GRAY_STAINED_GLASS_PANE;
                inventory.setItem(slot, item(material,
                        "Set to " + LightningDamage.format(valueAt(i)) + "x", NamedTextColor.YELLOW,
                        List.of("Click to set this value")));
            }
        }

        inventory.setItem(RESET_SLOT, item(Material.BARRIER, "Reset to normal (1x)", NamedTextColor.RED,
                List.of("Vanilla lightning damage")));
    }

    private static ItemStack item(Material material, String name, NamedTextColor color, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name, color).decoration(TextDecoration.ITALIC, false));
        List<Component> lines = new ArrayList<>();
        for (String line : lore) {
            lines.add(Component.text(line, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lines);
        stack.setItemMeta(meta);
        return stack;
    }

    // --------------------------------------------------------------- clicks

    public void click(Player player, int slot, boolean shift) {
        if (!player.hasPermission("lightningdamage.admin")) return;

        double current = plugin.multiplier();
        double step = shift ? 10.0 : 1.0;
        Double updated = null;

        if (slot == MINUS) {
            updated = current - step;
        } else if (slot == PLUS) {
            updated = current + step;
        } else if (slot >= TRACK_START && slot < TRACK_START + POSITIONS) {
            updated = valueAt(slot - TRACK_START);
        } else if (slot == RESET_SLOT) {
            updated = 1.0;
        }
        if (updated == null) return;

        plugin.setMultiplier(updated);
        render();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.3f);
    }
}
