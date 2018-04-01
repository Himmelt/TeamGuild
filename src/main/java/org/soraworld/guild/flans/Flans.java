package org.soraworld.guild.flans;

import com.flansmod.common.guns.EntityBullet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Flans {

    public static Player getShooter(Entity craftBullet) {
        try {
            System.out.println("CraftEntity:" + craftBullet + craftBullet.getClass());
            if (craftBullet instanceof CraftEntity) {
                net.minecraft.server.v1_7_R4.Entity entity = ((CraftEntity) craftBullet).getHandle();
                if (entity instanceof EntityBullet) {
                    EntityBullet bullet = (EntityBullet) entity;
                    System.out.println("EntityBullet:" + bullet + bullet.getClass());
                    System.out.println("owner:" + bullet.owner);
                    if (bullet.owner != null) {
                        CraftEntity player = bullet.owner.getBukkitEntity();
                        System.out.println("CraftEntity:" + player + player.getClass());
                        if (player instanceof Player) {
                            return (Player) player;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
