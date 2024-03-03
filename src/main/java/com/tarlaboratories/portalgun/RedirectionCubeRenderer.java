package com.tarlaboratories.portalgun;

import org.slf4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class RedirectionCubeRenderer extends EntityRenderer<RedirectionCube> {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockRenderDispatcher blockRenderer;
    public static final BlockState NOT_ACTIVE_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState().setValue(WeightedStorageCubeBlock.VARIANT, 2);
    public static final BlockState ACTIVE_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState().setValue(WeightedStorageCubeBlock.ACTIVE, true).setValue(WeightedStorageCubeBlock.VARIANT, 2);

    protected RedirectionCubeRenderer(Context context) {
        super(context);
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public ResourceLocation getTextureLocation(RedirectionCube entity) {
        return new ResourceLocation("portalgun", "redirection_cube");
    }
    
    public void render(RedirectionCube cube, float a, float b, PoseStack pose, MultiBufferSource buffer_source, int c) {
        int light = cube.level().getRawBrightness(cube.blockPosition(), 0)*20;
        if (cube.getEntityData().get(RedirectionCube.ACTIVE)) blockRenderer.renderSingleBlock(ACTIVE_STATE, pose, buffer_source, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        else blockRenderer.renderSingleBlock(NOT_ACTIVE_STATE, pose, buffer_source, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        super.render(cube, a, b, pose, buffer_source, c);
    }
}
