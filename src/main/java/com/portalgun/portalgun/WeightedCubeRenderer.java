package com.portalgun.portalgun;

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

public class WeightedCubeRenderer extends EntityRenderer<WeightedCube> {
    //private final EntityModel<WeightedCube> model;
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockRenderDispatcher blockRenderer;
    public static final BlockState NOT_POWERED_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState();
    public static final BlockState POWERED_STATE = portalgun.WEIGHTED_STORAGE_CUBE_BLOCK.get().defaultBlockState().setValue(WeightedStorageCubeBlock.POWERED, true);

    protected WeightedCubeRenderer(Context context) {
        super(context);
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public ResourceLocation getTextureLocation(WeightedCube entity) {
        return new ResourceLocation("portalgun", "weighted_cube");
    }
    
    public void render(WeightedCube cube, float a, float b, PoseStack pose, MultiBufferSource buffer_source, int c) {
        //float red = 100, green = 100, blue = 100, alpha = 100;
        //model.renderToBuffer(pose, buffer_source.getBuffer(RenderType.solid()), 15, 0, red, green, blue, alpha);
        int light = cube.level().getRawBrightness(cube.blockPosition(), 0)*20;
        if (cube.getEntityData().get(WeightedCube.ACTIVATED)) blockRenderer.renderSingleBlock(POWERED_STATE, pose, buffer_source, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        else blockRenderer.renderSingleBlock(NOT_POWERED_STATE, pose, buffer_source, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        super.render(cube, a, b, pose, buffer_source, c);
    }
}
