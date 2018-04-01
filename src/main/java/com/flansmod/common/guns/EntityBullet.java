package com.flansmod.common.guns;

import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.World;

public abstract class EntityBullet extends Entity {

    public Entity owner;

    public EntityBullet(World world) {
        super(world);
    }

}
