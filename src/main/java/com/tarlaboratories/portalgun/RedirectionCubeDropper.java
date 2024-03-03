package com.tarlaboratories.portalgun;

import net.minecraft.world.entity.EntityType;

public class RedirectionCubeDropper extends WeightedCubeDropper {

    public RedirectionCubeDropper(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getDroppedEntityType() {
        return portalgun.REDIRECTION_CUBE_ENTITYTYPE.get();
    }
}
