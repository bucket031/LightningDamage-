package com.example.lightningdamage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/** Scales the damage of every lightning strike. */
public final class DamageListener implements Listener {

    private final LightningDamage plugin;

    public DamageListener(LightningDamage plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.LIGHTNING) return;

        double multiplier = plugin.multiplier();
        if (multiplier == 1.0) return;

        if (multiplier <= 0.0) {
            event.setCancelled(true);
            return;
        }
        event.setDamage(event.getDamage() * multiplier);
    }
}
