package com.portalgun.portalgun;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.Port;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Camera;
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
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@SuppressWarnings("unused")
//@Mod.EventBusSubscriber(modid = portalgun.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PortalGunItem extends Item implements ClickHandlingItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    //private static final Minecraft minecraft = Minecraft.getInstance();
    
    public PortalGunItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> shootPortal(Level level, Player player, InteractionHand hand, boolean is_leftclick) {
        //player.setYRot(player.rotate(Rotation.CLOCKWISE_180));
        try {
            CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
            BlockEntity ORANGE_PORTAL_BLOCKENTITY = null;
            BlockEntity BLUE_PORTAL_BLOCKENTITY = null;
            @SuppressWarnings("rawtypes")
            BlockEntityType PORTAL_BLOCK_ENTITY_TYPE = RegistryObject.create(new ResourceLocation("portalgun:portal_block_blockentity"), ForgeRegistries.BLOCK_ENTITY_TYPES).get();
            if (tag.contains("orange_portal_pos")) ORANGE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")));
            if (tag.contains("blue_portal_pos")) BLUE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("blue_portal_pos")));
            if (ORANGE_PORTAL_BLOCKENTITY != null) ((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).destroyHardLightBridge();
            if (BLUE_PORTAL_BLOCKENTITY != null) ((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).destroyHardLightBridge();
            //if (ORANGE_PORTAL_BLOCKENTITY != null) {LOGGER.info(ORANGE_PORTAL_BLOCKENTITY.toString() + "============================================="); LOGGER.info(((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).replaced_block_blockstate.toString());}
            //LOGGER.info(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")).toString());
            //if (level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos"))) != null) LOGGER.info(level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos"))).toString());
            HitResult res = player.pick(6000, 0.0F, false);
            BlockHitResult res_block = (BlockHitResult) res;
            final RegistryObject<PortalBlock> PORTAL_BLOCK = RegistryObject.create(new ResourceLocation("portalgun:portal_block"), ForgeRegistries.BLOCKS);
            BlockState PORTAL_BLOCKSTATE = PORTAL_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, hand,getDefaultInstance(), res_block)).setValue(PortalBlock.FACE, res_block.getDirection());
            if ((PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) != Direction.UP) & (PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) != Direction.DOWN)) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, Direction.DOWN);
            if (res_block != null & this.canPlacePortal(res_block.getBlockPos(), PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE), PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING), level)) {
                if (ORANGE_PORTAL_BLOCKENTITY != null & !is_leftclick & !tag.contains("only_blue")) {((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).removePortal(); ((PortalBlockBlockEntity) level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();}
                if (BLUE_PORTAL_BLOCKENTITY != null & (is_leftclick | tag.contains("only_blue"))) {((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).removePortal(); ((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();}
                //BlockState PORTAL_BLOCKSTATE = PORTAL_BLOCK.get().getStateForPlacement(new BlockPlaceContext(player, hand,getDefaultInstance(), res_block)).setValue(PortalBlock.FACE, res_block.getDirection());
                //else if ((PORTAL_BLOCKSTATE.getValue(PortalBlock.FACE) == PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING))) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, Direction.EAST);
                if (!is_leftclick & !tag.contains("only_blue")) tag.put("orange_portal_pos", NbtUtils.writeBlockPos(res_block.getBlockPos()));
                if (is_leftclick | tag.contains("only_blue")) tag.put("blue_portal_pos", NbtUtils.writeBlockPos(res_block.getBlockPos()));
                BlockState old_block_state = level.getBlockState(res_block.getBlockPos());
                BlockState old_blockstate2 = level.getBlockState(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)));
                if (is_leftclick | tag.contains("only_blue")) PORTAL_BLOCKSTATE = PORTAL_BLOCKSTATE.setValue(PortalBlock.IS_ORANGE, false);
                BlockState second_portal_block = PORTAL_BLOCKSTATE.setValue(PortalBlock.FACING, PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING).getOpposite());
                level.setBlockAndUpdate(res_block.getBlockPos(), PORTAL_BLOCKSTATE);
                level.setBlockAndUpdate(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)), second_portal_block);
                
                PortalBlockBlockEntity res_portal_block_entity = ((PortalBlock) PORTAL_BLOCKSTATE.getBlock()).newBlockEntity(res_block.getBlockPos(), PORTAL_BLOCKSTATE);
                PortalBlockBlockEntity res_portal_blockentity2 = ((PortalBlock) second_portal_block.getBlock()).newBlockEntity(res_block.getBlockPos().relative(PORTAL_BLOCKSTATE.getValue(PortalBlock.FACING)), second_portal_block);
                if (BLUE_PORTAL_BLOCKENTITY != null & !is_leftclick & !tag.contains("only_blue")) {
                    res_portal_block_entity.setLinkPos(BLUE_PORTAL_BLOCKENTITY.getBlockPos());
                    res_portal_blockentity2.setLinkPos(BLUE_PORTAL_BLOCKENTITY.getBlockPos());
                    ((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).setLinkPos(res_portal_block_entity.getBlockPos());
                    ((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).setLinkPos(res_portal_block_entity.getBlockPos());
                    level.setBlockAndUpdate(res_portal_block_entity.getBlockPos(), res_portal_block_entity.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                    level.setBlockAndUpdate(res_portal_blockentity2.getBlockPos(), res_portal_blockentity2.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                    level.setBlockAndUpdate(BLUE_PORTAL_BLOCKENTITY.getBlockPos(), BLUE_PORTAL_BLOCKENTITY.getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                    level.setBlockAndUpdate(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)), level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING))).getBlockState().setValue(PortalBlock.IS_ACTIVE, true));
                }
                if (ORANGE_PORTAL_BLOCKENTITY != null & (is_leftclick | tag.contains("only_blue"))) {
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
        } catch (Exception e) {
            player.displayClientMessage(Component.translatable("An error has occured! Removing all your portals..."), true);
            clearPortals(level, player.getItemInHand(hand));
            LOGGER.error("An exception has occured while trying to place portal: {}", e.toString());
            e.printStackTrace();
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
            canBeInFrontOfPortal(level.getBlockState(pos.relative(face))) &
            canBeInFrontOfPortal(level.getBlockState(pos.relative(facing).relative(face))));
    }

    protected boolean isPortalable(Block block) {
        /*LOGGER.info("===============================================");
        LOGGER.info("Checking if can place portal on {}", block);
        LOGGER.info("Checking if it is a portalable block: {}", Config.portalable_blocks.contains(block));
        LOGGER.info("Checking if it is whitelist mode: {}", Config.is_whitelist_mode);
        LOGGER.info("Result: {}", Config.portalable_blocks.contains(block) ^ !Config.is_whitelist_mode);
        LOGGER.info("===============================================");*/
        List<Block> cannot_place_portal_on = List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, portalgun.PORTAL_BLOCK.get(), portalgun.EMANCIPATION_GRID_BLOCK.get(), portalgun.EMANCIPATION_GRID_EMITTER.get());
        return ((Config.portalable_blocks.contains(block)) ^ !Config.is_whitelist_mode) & !cannot_place_portal_on.contains(block);
    }

    public static void clearPortals(Level level, ItemStack item) {
        CompoundTag tag = item.getOrCreateTag();
        BlockEntity ORANGE_PORTAL_BLOCKENTITY = null;
        BlockEntity BLUE_PORTAL_BLOCKENTITY = null;
        if (tag.contains("orange_portal_pos") & !tag.contains("only_blue")) ORANGE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("orange_portal_pos")));
        if (tag.contains("blue_portal_pos")) BLUE_PORTAL_BLOCKENTITY = level.getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("blue_portal_pos")));
        if (ORANGE_PORTAL_BLOCKENTITY != null) {((PortalBlockBlockEntity) level.getBlockEntity(ORANGE_PORTAL_BLOCKENTITY.getBlockPos().relative(ORANGE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal(); ((PortalBlockBlockEntity) ORANGE_PORTAL_BLOCKENTITY).removePortal();}
        if (BLUE_PORTAL_BLOCKENTITY != null) {((PortalBlockBlockEntity) level.getBlockEntity(BLUE_PORTAL_BLOCKENTITY.getBlockPos().relative(BLUE_PORTAL_BLOCKENTITY.getBlockState().getValue(PortalBlock.FACING)))).removePortal();((PortalBlockBlockEntity) BLUE_PORTAL_BLOCKENTITY).removePortal();}
        if (!tag.contains("only_blue")) tag.remove("orange_portal_pos");
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
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    public InteractionResult onLeftClick(Player player, InteractionHand hand) {
        Level level = player.level();
        return shootPortal(level, player, hand, true).getResult();
    }

    public InteractionResult onRightClick(Player player, InteractionHand hand) {
        Level level = player.level();
        return shootPortal(level, player, hand, false).getResult();
    }

    @SubscribeEvent
    public void LeftClickEmpty(LeftClickEmpty event) {
        if (event.getItemStack().is(portalgun.PORTAL_GUN_ITEM.get())) onLeftClick(event.getEntity(), event.getHand());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) return new InteractionResultHolder<ItemStack>(onLeftClick(player, hand), player.getItemInHand(hand));
        return new InteractionResultHolder<ItemStack>(onRightClick(player, hand), player.getItemInHand(hand));
    }

    protected boolean canBeInFrontOfPortal(BlockState state) {
        if (state.canBeReplaced()) return true;
        if (state.is(portalgun.HARD_LIGHT_BRIDGE.get())) return true;
        if (state.is(portalgun.HARD_LIGHT_BRIDGE_EMITTER.get())) return true;
        return false;
    }
}
