package org.soraworld.guild.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamGuild;

import javax.annotation.Nonnull;

public class EventListener implements Listener {

    private final Config config;

    public EventListener(@Nonnull Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();

        if (/*damager instanceof Player &&*/ damagee instanceof LivingEntity) {
            Player shooter = config.getFlans().getShooter(damager);
            System.out.println(shooter);
            TeamGuild guild = config.getGuild(((Player) damager).getName());
            if (guild != null && guild.hasMember(((Player) damagee).getName())) {
                event.setCancelled(true);
            }
        }
    }

}
