package com.tarlaboratories.portalgun;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class PortalBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty IS_ORANGE = BooleanProperty.create("is_orange");
    //public static final IntegerProperty LINK_X = IntegerProperty.create("link_x", 0, 6000);
    //public static final IntegerProperty LINK_Y = IntegerProperty.create("link_y", 0, 400);
    //public static final IntegerProperty LINK_Z = IntegerProperty.create("link_z", 0, 6000);
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    private static final VoxelShape INSIDE = box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    private static final VoxelShape UP_PANE = box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_PANE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape EAST_PANE = box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_PANE = box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_PANE = box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_PANE = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    protected static final VoxelShape START_SHAPE = Shapes.join(Shapes.block(), INSIDE, BooleanOp.ONLY_FIRST);
    private static final Minecraft minecraft = Minecraft.getInstance();
    private BlockPos target_block_pos = null;
    private BlockState replaced_block = null;

    private static final Logger LOGGER = LogUtils.getLogger();

    public PortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(FACE, Direction.EAST)
                .setValue(FACING, Direction.UP)
                .setValue(IS_ORANGE, true)
                //.setValue(LINK_X, 0)
                //.setValue(LINK_Y, 100)
                //.setValue(LINK_Z, 0)
                .setValue(IS_ACTIVE, false)
        );
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACE, FACING, IS_ORANGE, /*LINK_X, LINK_Y, LINK_Z,*/ IS_ACTIVE));
    }

    public Class<PortalBlockBlockEntity> getBlockEntityClass() {
        return PortalBlockBlockEntity.class;
    }

    public VoxelShape getShape(BlockState p_151964_, BlockGetter p_151965_, BlockPos p_151966_, CollisionContext p_151967_) {
        VoxelShape shape = START_SHAPE;
        switch (p_151964_.getValue(PortalBlock.FACE)) {
            case DOWN: shape = Shapes.join(shape, DOWN_PANE, BooleanOp.ONLY_FIRST); break;
            case EAST: shape = Shapes.join(shape, EAST_PANE, BooleanOp.ONLY_FIRST); break;
            case NORTH: shape = Shapes.join(shape, NORTH_PANE, BooleanOp.ONLY_FIRST); break;
            case SOUTH: shape = Shapes.join(shape, SOUTH_PANE, BooleanOp.ONLY_FIRST); break;
            case UP: shape = Shapes.join(shape, UP_PANE, BooleanOp.ONLY_FIRST); break;
            case WEST: shape = Shapes.join(shape, WEST_PANE, BooleanOp.ONLY_FIRST); break;
        }
        switch (p_151964_.getValue(PortalBlock.FACING)) {
            case DOWN: shape = Shapes.join(shape, DOWN_PANE, BooleanOp.ONLY_FIRST); break;
            case EAST: shape = Shapes.join(shape, EAST_PANE, BooleanOp.ONLY_FIRST); break;
            case NORTH: shape = Shapes.join(shape, NORTH_PANE, BooleanOp.ONLY_FIRST); break;
            case SOUTH: shape = Shapes.join(shape, SOUTH_PANE, BooleanOp.ONLY_FIRST); break;
            case UP: shape = Shapes.join(shape, UP_PANE, BooleanOp.ONLY_FIRST); break;
            case WEST: shape = Shapes.join(shape, WEST_PANE, BooleanOp.ONLY_FIRST); break;
        }
        return shape;
    }

    public VoxelShape getInteractionShape(BlockState p_151955_, BlockGetter p_151956_, BlockPos p_151957_) {
        return INSIDE;
    }

    @Override
    public PortalBlockBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalBlockBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        long distance_x, distance_y, distance_z;
        distance_x = context.getClickedPos().getX() - context.getPlayer().getBlockX();
        distance_y = context.getClickedPos().getY() - context.getPlayer().getBlockY();
        distance_z = context.getClickedPos().getZ() - context.getPlayer().getBlockZ();
        if (Math.abs(distance_x) >= Math.abs(distance_z)) {
            if (distance_x > 0) state = state.setValue(PortalBlock.FACING, Direction.WEST);
            else state = state.setValue(PortalBlock.FACING, Direction.EAST);
        } /*else if (Math.abs(distance_y) >= Math.abs(distance_x) & Math.abs(distance_y) >= Math.abs(distance_z)) {
            if (distance_y > 0) state = state.setValue(PortalBlock.FACING, Direction.DOWN);
            else state = state.setValue(PortalBlock.FACING, Direction.UP);
        }*/ else {
            if (distance_z > 0) state = state.setValue(PortalBlock.FACING, Direction.NORTH);
            else state = state.setValue(PortalBlock.FACING, Direction.SOUTH);
        }
        state.setValue(PortalBlock.FACE, context.getClickedFace());
        return state;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        try {
            if (entity.isOnPortalCooldown()) return;
            if (!state.getValue(PortalBlock.IS_ACTIVE)) return;
            ServerLevel serverlevel = null;
            long chunk = 0;
            if (!level.isLoaded(pos) & !level.isClientSide) {
                serverlevel = (ServerLevel) level;
                chunk = new ChunkPos(pos).toLong();
                ForgeChunkManager.forceChunk(serverlevel, "portalgun", pos, (int) chunk, (int) (chunk >> 32), true, false);
            }
            BlockPos link_pos = ((PortalBlockBlockEntity) level.getBlockEntity(pos)).link_pos;
            if (link_pos == null) return;
            if (entity.getType() == EntityType.PLAYER) entity = (Player) entity;
            entity.setPos(link_pos.relative(level.getBlockState(link_pos).getValue(PortalBlock.FACE)).relative(level.getBlockState(link_pos).getValue(PortalBlock.FACING)).getCenter());
            float old_rotation = entity.getYRot();
            //float headRot = entity.getYHeadRot();
            //LOGGER.info("Old Y rotation: {}", rot);
            //LOGGER.info("Going from portal with face {}", state.getValue(PortalBlock.FACE).toString());
            Vec3 emv = entity.getDeltaMovement();
            //LOGGER.info("Old movement vector: {}", emv);
            switch (state.getValue(PortalBlock.FACE)) {
                case DOWN: emv = new Vec3(emv.x, emv.z, -emv.y); break;
                case EAST: entity.setYRot(entity.rotate(Rotation.CLOCKWISE_90)); emv = new Vec3(-emv.z, emv.y, emv.x); break;
                case NORTH: entity.setYRot(entity.rotate(Rotation.CLOCKWISE_180)); emv = new Vec3(-emv.x, emv.y, -emv.z); break;
                case SOUTH: break;
                case UP: emv = new Vec3(emv.x, -emv.z, emv.y); break;
                case WEST: entity.setYRot(entity.rotate(Rotation.COUNTERCLOCKWISE_90)); emv = new Vec3(emv.z, emv.y, -emv.x); break;
            }
            //LOGGER.info("This should be around 0 or smth: {}", entity.getYRot());
            //LOGGER.info("To portal with face {}", level.getBlockState(link_pos).getValue(PortalBlock.FACE));
            //LOGGER.info("Middle movement vector: {}", emv);
            switch (level.getBlockState(link_pos).getValue(PortalBlock.FACE)) {
                case DOWN: entity.teleportRelative(0, -1, 0); emv = new Vec3(emv.x, emv.z, -emv.y); break;
                case EAST: entity.setYRot(entity.rotate(Rotation.CLOCKWISE_90)); emv = new Vec3(-emv.z, emv.y, emv.x); break;
                case NORTH: break;
                case SOUTH: entity.setYRot(entity.rotate(Rotation.CLOCKWISE_180)); emv = new Vec3(-emv.x, emv.y, -emv.z); break;
                case UP: emv = new Vec3(emv.x, -emv.z, emv.y); break;
                case WEST: entity.setYRot(entity.rotate(Rotation.COUNTERCLOCKWISE_90)); emv = new Vec3(emv.z, emv.y, -emv.x); break;
            }
            //LOGGER.info("New movement vector: {}", emv);
            entity.setDeltaMovement(emv);
            //entity.setOldPosAndRot();
            //LOGGER.info("Teleported successfully!");
            //LOGGER.info("New Y rotation: {}", entity.getYRot());
            entity.setPortalCooldown(1);
            if (serverlevel != null) {
                ForgeChunkManager.forceChunk(serverlevel, "portalgun", pos, (int) chunk, (int) (chunk >> 32), false, false);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
        }
    }
}
