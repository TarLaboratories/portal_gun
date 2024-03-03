package com.tarlaboratories.portalgun;

import net.minecraft.world.entity.EntityType;

public class CompanionCubeDropper extends WeightedCubeDropper {
    public CompanionCubeDropper(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getDroppedEntityType() {
        return portalgun.COMPANION_CUBE_ENTITYTYPE.get();
    }
}
