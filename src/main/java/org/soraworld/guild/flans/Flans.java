package org.soraworld.guild.flans;

import com.flansmod.common.guns.EntityBullet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.soraworld.guild.manager.TeamManager;

public class Flans {

    private static boolean support = false;

    public static void checkFlans(final TeamManager config) {
        try {
            CraftEntity.class.getName();
            net.minecraft.server.v1_7_R4.Entity.class.getName();
            EntityBullet.class.getName();
            support = true;
            config.consoleKey("flanSupport");
        } catch (Throwable ignored) {
            config.consoleKey("flanNotSupport");
        }
    }

    public static Player getShooter(Entity craftBullet) {
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
