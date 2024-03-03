package com.tarlaboratories.portalgun;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RedirectionCube extends WeightedCube {
    public static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(WeightedCube.class, EntityDataSerializers.BOOLEAN);
    public RedirectionCube(EntityType<?> type, Level level) {
        super(type, level);
    }
    
    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
    }
}
