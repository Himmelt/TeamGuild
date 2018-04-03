package org.soraworld.guild.flans;

import com.flansmod.common.guns.EntityBullet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;

public class Flans {

    private final boolean support;

    public Flans(Config config) {
        boolean temp;
        try {
            config.console("flanCheck", CraftEntity.class.getName());
            config.console("flanCheck", net.minecraft.server.v1_7_R4.Entity.class.getName());
            config.console("flanCheck", EntityBullet.class.getName());
            temp = true;
            config.console("flanSupport");
        } catch (Throwable ignored) {
            temp = false;
            config.console("flanNotSupport");
        }
        this.support = temp;
    }

    public Player getShooter(Entity craftBullet) {
        if (support && craftBullet instanceof CraftEntity) {
            net.minecraft.server.v1_7_R4.Entity entity = ((CraftEntity) craftBullet).getHandle();
            if (entity instanceof EntityBullet) {
                EntityBullet bullet = (EntityBullet) entity;
                if (bullet.owner != null) {
                    CraftEntity player = bullet.owner.getBukkitEntity();
                    if (player instanceof Player) {
                        return (Player) player;
                    }
                }
            }
        }
        return null;
    }

}
