package org.soraworld.guild.flans;

import com.flansmod.common.guns.EntityBullet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;

public class Flans {

    private final Config config;
    private final boolean support;

    public Flans(Config config) {
        this.config = config;
        boolean temp;
        try {
            config.iiChat.console(config.iiLang.format("flanCheck", CraftEntity.class.getName()));
            config.iiChat.console(config.iiLang.format("flanCheck", net.minecraft.server.v1_7_R4.Entity.class.getName()));
            config.iiChat.console(config.iiLang.format("flanCheck", EntityBullet.class.getName()));
            temp = true;
            config.iiChat.console(config.iiLang.format("flanSupport"));
        } catch (Throwable ignored) {
            temp = false;
            config.iiChat.console(config.iiLang.format("flanNotSupport"));
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
