package com.portalgun.portalgun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PortalBlockBlockEntityRenderer<T extends PortalBlockBlockEntity> implements BlockEntityRenderer<T> {
   public PortalBlockBlockEntityRenderer(BlockEntityRendererProvider.Context p_173689_) {
   }

   public void render(T p_112650_, float p_112651_, PoseStack p_112652_, MultiBufferSource p_112653_, int p_112654_, int p_112655_) {
      Matrix4f matrix4f = p_112652_.last().pose();
      BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
      BakedModel replaced_block_model = blockRenderer.getBlockModel(p_112650_.replaced_block_blockstate);
      BakedModel block_model = blockRenderer.getBlockModel(p_112650_.getBlockState());
      //blockRenderer.getModelRenderer().renderModel(p_112652_.last(), p_112653_.getBuffer(this.renderType()), p_112650_.replaced_block_blockstate, replaced_block_model, 0, 0, 0, 0, 0);
      this.renderCube(p_112650_, matrix4f, p_112653_.getBuffer(this.renderType()), block_model, replaced_block_model, p_112652_.last(), p_112653_.getBuffer(RenderType.solid()), p_112654_, p_112655_);
   }

   private void renderCube(T p_173691_, Matrix4f p_254024_, VertexConsumer p_173693_, BakedModel block_model, BakedModel replaced_block_model, PoseStack.Pose pose, VertexConsumer vc2, int light, int overlay) {
      float f = this.getOffsetDown();
      float f1 = this.getOffsetUp();
      this.renderFace(p_173691_, p_254024_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH, block_model, replaced_block_model, pose, vc2, light, overlay);
      this.renderFace(p_173691_, p_254024_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH, block_model, replaced_block_model, pose, vc2, light, overlay);
      this.renderFace(p_173691_, p_254024_, p_173693_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST, block_model, replaced_block_model, pose, vc2, light, overlay);
      this.renderFace(p_173691_, p_254024_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST, block_model, replaced_block_model, pose, vc2, light, overlay);
      this.renderFace(p_173691_, p_254024_, p_173693_, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN, block_model, replaced_block_model, pose, vc2, light, overlay);
      this.renderFace(p_173691_, p_254024_, p_173693_, 0.0F, 1.0F, f1, f1, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP, block_model, replaced_block_model, pose, vc2, light, overlay);
   }

   private void renderFace(T p_253949_, Matrix4f p_254247_, VertexConsumer p_254390_, float p_254147_, float p_253639_, float p_254107_, float p_254109_, float p_254021_, float p_254458_, float p_254086_, float p_254310_, Direction p_253619_, BakedModel block_model, BakedModel replaced_block_model, PoseStack.Pose pose, VertexConsumer vc2, int light, int overlay) {
      if (/*p_253949_.shouldRenderFace(p_253619_)*/p_253619_ == p_253949_.getBlockState().getValue(PortalBlock.FACE)) {
         /*p_254390_.vertex(p_254247_, p_254147_, p_254107_, p_254021_).endVertex();
         p_254390_.vertex(p_254247_, p_253639_, p_254107_, p_254458_).endVertex();
         p_254390_.vertex(p_254247_, p_253639_, p_254109_, p_254086_).endVertex();
         p_254390_.vertex(p_254247_, p_254147_, p_254109_, p_254310_).endVertex();*/
        //for (BakedQuad bakedquad : block_model.getQuads(p_253949_.getBlockState(), p_253619_, RandomSource.create(), ModelData.EMPTY, RenderType.solid())) {
        //    vc2.putBulkData(pose, bakedquad, 1, 1, 1, light, overlay);
        //}
      } else {
        for (BakedQuad bakedquad : replaced_block_model.getQuads(p_253949_.replaced_block_blockstate, p_253619_, RandomSource.create(), ModelData.EMPTY, RenderType.solid())) {
            vc2.putBulkData(pose, bakedquad, 1, 1, 1, light, overlay);
        }
      }
   }

   protected float getOffsetUp() {
      return 1;//.75F;
   }

   protected float getOffsetDown() {
      return 0;//.375F;
   }

   protected RenderType renderType() {
      return RenderType.solid();
   }
}