package com.example.lightningdamage;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class LightningDamage extends JavaPlugin {

    public static final double MIN = 0.0;
    public static final double MAX = 100.0;
    public static final double VANILLA_DAMAGE = 5.0;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        LightningCommand command = new LightningCommand(this);
        PluginCommand pluginCommand = Objects.requireNonNull(getCommand("lightning"));
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }

    /** Current damage multiplier, 0 to 100. */
    public double multiplier() {
        double value = getConfig().getDouble("damage_multiplier", 1.0);
        return Math.max(MIN, Math.min(MAX, value));
    }

    public void setMultiplier(double value) {
        // Keep one decimal place so the file stays tidy.
        double clean = Math.round(Math.max(MIN, Math.min(MAX, value)) * 10.0) / 10.0;
        getConfig().set("damage_multiplier", clean);
        saveConfig();
    }

    /** "1", "2.5", "100" (no trailing .0). */
    public static String format(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }
}
