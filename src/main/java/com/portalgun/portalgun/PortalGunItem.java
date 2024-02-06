package com.portalgun.portalgun;

import java.util.ArrayList;

import org.apache.logging.log4j.core.config.builder.api.Component;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")

public class PortalGunItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    private PortalBlockBlockEntity ORANGE_PORTAL_BLOCKENTITY;
    private PortalBlockBlockEntity BLUE_PORTAL_BLOCKENTITY;
    //private static final Minecraft minecraft = Minecraft.getInstance();
    
    public PortalGunItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
        if (tag.contains("orange_portal_pos")) ORANGE_PORTAL_BLOCKENTITY = (PortalBlockBlockEntity) level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_block")));
        HitResult res = player.pick(6000, 0.0F, false);
        BlockHitResult res_block = (BlockHitResult) res;
        if (res_block != null) {
            if (ORANGE_PORTAL_BLOCKENTITY != null) ORANGE_PORTAL_BLOCKENTITY.removePortal();
            final RegistryObject<PortalBlock> PORTAL_BLOCK = RegistryObject.create(new ResourceLocation("portalgun:portal_block"), ForgeRegistries.BLOCKS);
            final BlockState PORTAL_BLOCKSTATE = PORTAL_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, hand,getDefaultInstance(), res_block)).setValue(PortalBlock.FACE, res_block.getDirection());
            tag.put("orange_portal_pos", NbtUtils.writeBlockPos(res_block.getBlockPos()));
            BlockState old_block_state = level.getBlockState(res_block.getBlockPos());
            level.setBlockAndUpdate(res_block.getBlockPos(), PORTAL_BLOCKSTATE);
            PortalBlockBlockEntity res_portal_block_entity = (PortalBlockBlockEntity) level.getBlockEntity(res_block.getBlockPos());
            if (BLUE_PORTAL_BLOCKENTITY != null) {
                res_portal_block_entity.setLinkPos(BLUE_PORTAL_BLOCKENTITY.getBlockPos());
                res_portal_block_entity.getBlockState().setValue(PortalBlock.IS_ACTIVE, true);
            }
            res_portal_block_entity.setReplacedBlock(old_block_state);
            level.setBlockEntity(res_portal_block_entity);
            return super.use(level, player, hand);
        }
        return super.use(level, player, hand);
    }
}