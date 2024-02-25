package com.portalgun.portalgun;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class CompanionCubeRender extends WeightedCubeRenderer {
    protected CompanionCubeRender(Context context) {
        super(context);
        NOT_POWERED_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState().setValue(WeightedStorageCubeBlock.VARIANT, 1);
        POWERED_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState().setValue(WeightedStorageCubeBlock.POWERED, true).setValue(WeightedStorageCubeBlock.VARIANT, 1);
    }
}
