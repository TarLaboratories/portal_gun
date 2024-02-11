package com.portalgun.portalgun;

import java.util.ArrayList;
import java.util.Optional;

import javax.sound.sampled.Port;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")

public class PortalGunItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    //private static final Minecraft minecraft = Minecraft.getInstance();
    
    public PortalGunItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //player.setYRot(player.rotate(Rotation.CLOCKWISE_180));
        CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
        BlockEntity ORANGE_PORTAL_BLOCKENTITY = null;
        BlockEntity BLUE_PORTAL_BLOCKENTITY = null;
        @SuppressWarnings("rawtypes")
        BlockEntityType PORTAL_BLOCK_ENTITY_TYPE = RegistryObject.create(new ResourceLocation("portalgun:portal_block_blockentity"), ForgeRegistries.BLOCK_ENTITY_TYPES).get();
        if (tag.contains("orange_portal_pos")) ORANGE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")));
        if (tag.contains("blue_portal_pos")) BLUE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("blue_portal_pos")));
        //if (ORANGE_PORTAL_BLOCKENTITY != null) {LOGGER.info(ORANGE_PORTAL_BLOCKENTITY.toString() + "============================================="); LOGGER.info(((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).replaced_block_blockstate.toString());}
        //LOGGER.info(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")).toString());
        //if (level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos"))) != null) LOGGER.info(level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos"))).toString());
        HitResult res = player.pick(6000, 0.0F, false);
        BlockHitResult res_block = (BlockHitResult) res;
        final RegistryObject<PortalBlock> PORTAL_BLOCK = RegistryObject.create(new ResourceLocation("portalgun:portal_block"), ForgeRegistries.BLOCKS);
        BlockState PORTAL_BLOCKSTATE = PORTAL_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, hand,getDefaultInstance(), res_block)).setValue(PortalBlock.FACE, res_block.getDirection());
        if ((PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) != Direction.UP) & (PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) != Direction.DOWN)) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, Direction.DOWN);
        if (res_block != null & this.canPlacePortal(res_block.getBlockPos(), PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE), PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING), level)) {
            if (ORANGE_PORTAL_BLOCKENTITY != null & !player.isShiftKeyDown()) {((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).removePortal(); ((PortalBlockBlockEntity) level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();}
            if (BLUE_PORTAL_BLOCKENTITY != null & player.isShiftKeyDown()) {((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).removePortal(); ((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();}
            //BlockState PORTAL_BLOCKSTATE = PORTAL_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, hand,getDefaultInstance(), res_block)).setValue(PortalBlock.FACE, res_block.getDirection());
            //else if ((PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) == PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING))) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, Direction.EAST);
            if (!player.isShiftKeyDown()) tag.put("orange_portal_pos", NbtUtils.writeBlockPos(res_block.getBlockPos()));
            if (player.isShiftKeyDown()) tag.put("blue_portal_pos", NbtUtils.writeBlockPos(res_block.getBlockPos()));
            BlockState old_block_state = level.getBlockState(res_block.getBlockPos());
            BlockState old_blockstate2 = level.getBlockState(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)));
            if (player.isShiftKeyDown()) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.IS_ORANGE, false);
            BlockState second_portal_block = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING).getOpposite());
            level.setBlockAndUpdate(res_block.getBlockPos(), PORTAL_BLOCKSTATE);
            level.setBlockAndUpdate(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)), second_portal_block);
            
            PortalBlockBlockEntity res_portal_block_entity = ((PortalBlock) PORTAL_BLOCKSTATE.getBlock()).newBlockEntity(res_block.getBlockPos(), PORTAL_BLOCKSTATE);
            PortalBlockBlockEntity res_portal_blockentity2 = ((PortalBlock) second_portal_block.getBlock()).newBlockEntity(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)), second_portal_block);
            if (BLUE_PORTAL_BLOCKENTITY != null & !player.isShiftKeyDown()) {
                res_portal_block_entity.setLinkPos(BLUE_PORTAL_BLOCKENTITY.getBlockPos());
                res_portal_blockentity2.setLinkPos(BLUE_PORTAL_BLOCKENTITY.getBlockPos());
                ((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).setLinkPos(res_portal_block_entity.getBlockPos());
                ((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).setLinkPos(res_portal_block_entity.getBlockPos());
                level.setBlockAndUpdate(res_portal_block_entity.getBlockPos(), res_portal_block_entity.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(res_portal_blockentity2.getBlockPos(), res_portal_blockentity2.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(BLUE_PORTAL_BLOCKENTITY.getBlockPos(), BLUE_PORTAL_BLOCKENTITY.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)), level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING))).getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
            }
            if (ORANGE_PORTAL_BLOCKENTITY != null & player.isShiftKeyDown()) {
                res_portal_block_entity.setLinkPos(ORANGE_PORTAL_BLOCKENTITY.getBlockPos());
                res_portal_blockentity2.setLinkPos(ORANGE_PORTAL_BLOCKENTITY.getBlockPos());
                ((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).setLinkPos(res_portal_block_entity.getBlockPos());
                ((PortalBlockBlockEntity) level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).setLinkPos(res_portal_block_entity.getBlockPos());
                level.setBlockAndUpdate(res_portal_block_entity.getBlockPos(), res_portal_block_entity.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(res_portal_blockentity2.getBlockPos(), res_portal_blockentity2.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(ORANGE_PORTAL_BLOCKENTITY.getBlockPos(), ORANGE_PORTAL_BLOCKENTITY.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                level.setBlockAndUpdate(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)), level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING))).getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
            }
            res_portal_block_entity.setReplacedBlock(old_block_state);
            res_portal_blockentity2.setReplacedBlock(old_blockstate2);
            level.setBlockEntity(res_portal_block_entity);
            level.setBlockEntity(res_portal_blockentity2);
            //LOGGER.info("Set portal block entity");
            //LOGGER.info(res_portal_block_entity.toString());
            //LOGGER.info(res_portal_block_entity.replaced_block_blockstate.toString());
            //LOGGER.info(res_portal_block_entity.getBlockPos().toString());
              player.getItemInHand(hand).setTag(tag);
            player.getCooldowns().addCooldown(this, 5);
            return super.use(level, player, hand);
        } else {
            player.displayClientMessage(Component.translatable("Cannot place portal here!"), true);
        }
        return super.use(level, player, hand);
    }

    protected boolean canPlacePortal(BlockPos pos, Direction face, Direction facing, Level level) {
        /*LOGGER.info("===============================================");
        LOGGER.info("Checking if can place portal at {}", pos.toString());
        LOGGER.info("List of positions to check:");
        LOGGER.info("Check if {} is portalable", pos.toString());
        LOGGER.info("Check if {} is portalable", pos.relative(facing).toString());
        LOGGER.info("Check if {} is air", pos.relative(face).toString());
        LOGGER.info("Check if {} is air", pos.relative(facing).relative(face).toString());
        LOGGER.info("===============================================");*/
        return (isPortalable(level.getBlockState(pos).getBlock()) &
            isPortalable(level.getBlockState(pos.relative(facing)).getBlock()) &
            level.getBlockState(pos.relative(face)).isAir() &
            level.getBlockState(pos.relative(facing).relative(face)).isAir());
    }

    protected boolean isPortalable(Block block) {
        /*LOGGER.info("===============================================");
        LOGGER.info("Checking if can place portal on {}", block);
        LOGGER.info("Checking if it is a portalable block: {}", Config.portalable_blocks.contains(block));
        LOGGER.info("Checking if it is whitelist mode: {}", Config.is_whitelist_mode);
        LOGGER.info("Result: {}", Config.portalable_blocks.contains(block) ^ !Config.is_whitelist_mode);
        LOGGER.info("===============================================");*/
        return ((Config.portalable_blocks.contains(block)) ^ !Config.is_whitelist_mode) & block != Blocks.AIR & block != Blocks.VOID_AIR & block != Blocks.CAVE_AIR;
    }

    public static void clearPortals(Level level, ItemStack item) {
        CompoundTag tag = item.getOrCreateTag();
        BlockEntity ORANGE_PORTAL_BLOCKENTITY = null;
        BlockEntity BLUE_PORTAL_BLOCKENTITY = null;
        if (tag.contains("orange_portal_pos")) ORANGE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")));
        if (tag.contains("blue_portal_pos")) BLUE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("blue_portal_pos")));
        if (ORANGE_PORTAL_BLOCKENTITY != null) {((PortalBlockBlockEntity) level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal(); ((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).removePortal();}
        if (BLUE_PORTAL_BLOCKENTITY != null) {((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).removePortal();}
        tag.remove("orange_portal_pos");
        tag.remove("blue_portal_pos");
        item.setTag(tag);
    }

    @SubscribeEvent
    public static void EntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();
        if (entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;
        for (ItemStack item : player.getInventory().items) {
            if (item.is(portalgun.PORTAL_GUN_ITEM.get())) {
                clearPortals(level, item);
            }
        }
    }

    @Override
    public boolean mineBlock(ItemStack item, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity.isShiftKeyDown()) clearPortals(level, item);
        return false;
    }
}
