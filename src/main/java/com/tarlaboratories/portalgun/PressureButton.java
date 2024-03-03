package com.tarlaboratories.portalgun;

import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PressureButton extends Block {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final VoxelShape MAIN_SHAPE_DOWN = Shapes.join(box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), box(1.0D, 1.0D, 1.0D, 15.0D, 2.0D, 15.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_DOWN = box(2.0D, 2.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    public static final VoxelShape BUTTON_COLLISION_DOWN = box(6.0D, 2.0D, 6.0D, 10.0D, 4.0D, 10.0D);
    public static final VoxelShape MAIN_SHAPE_UP = Shapes.join(box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D), box(1.0D, 14.0D, 1.0D, 15.0D, 15.0D, 15.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_UP = box(2.0D, 13.0D, 2.0D, 14.0D, 14.0D, 14.0D);
    public static final VoxelShape BUTTON_COLLISION_UP = box(6.0D, 12.0D, 6.0D, 10.0D, 14.0D, 10.0D);
    public static final VoxelShape MAIN_SHAPE_NORTH = Shapes.join(box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D), box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 2.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_NORTH = box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 3.0D);
    public static final VoxelShape BUTTON_COLLISION_NORTH = box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 4.0D);
    public static final VoxelShape MAIN_SHAPE_SOUTH = Shapes.join(box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D), box(1.0D, 1.0D, 14.0D, 15.0D, 15.0D, 15.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_SOUTH = box(2.0D, 2.0D, 13.0D, 14.0D, 14.0D, 14.0D);
    public static final VoxelShape BUTTON_COLLISION_SOUTH = box(6.0D, 6.0D, 12.0D, 10.0D, 10.0D, 14.0D);
    public static final VoxelShape MAIN_SHAPE_WEST = Shapes.join(box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D), box(1.0D, 1.0D, 1.0D, 2.0D, 15.0D, 15.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_WEST = box(2.0D, 2.0D, 2.0D, 3.0D, 14.0D, 14.0D);
    public static final VoxelShape BUTTON_COLLISION_WEST = box(2.0D, 6.0D, 6.0D, 4.0D, 10.0D, 10.0D);
    public static final VoxelShape MAIN_SHAPE_EAST = Shapes.join(box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), box(14.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D), BooleanOp.OR);
    public static final VoxelShape BUTTON_SHAPE_EAST = box(13.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);
    public static final VoxelShape BUTTON_COLLISION_EAST = box(12.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D);

    public PressureButton(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED, FACE));
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        List<VoxelShape> shapes = getShapesForFace(state.getValue(FACE));
        if (state.getValue(POWERED)) return shapes.get(0);
        else return Shapes.join(shapes.get(0), shapes.get(1), BooleanOp.OR);
    }

    public static List<VoxelShape> getShapesForFace(Direction face) {
        switch (face) {
            case DOWN: return List.of(MAIN_SHAPE_DOWN, BUTTON_SHAPE_DOWN, BUTTON_COLLISION_DOWN);
            case UP: return List.of(MAIN_SHAPE_UP, BUTTON_SHAPE_UP, BUTTON_COLLISION_UP);
            case NORTH: return List.of(MAIN_SHAPE_NORTH, BUTTON_SHAPE_NORTH, BUTTON_COLLISION_NORTH);
            case SOUTH: return List.of(MAIN_SHAPE_SOUTH, BUTTON_SHAPE_SOUTH, BUTTON_COLLISION_SOUTH);
            case EAST: return List.of(MAIN_SHAPE_EAST, BUTTON_SHAPE_EAST, BUTTON_COLLISION_EAST);
            case WEST:  return List.of(MAIN_SHAPE_WEST, BUTTON_SHAPE_WEST, BUTTON_COLLISION_WEST);
            default: return List.of();
        }
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACE, context.getClickedFace().getOpposite()).setValue(POWERED, false);
    }

    private boolean isColliding(VoxelShape shape, Entity entity, BlockPos pos) {
        VoxelShape positionedShape = shape.move((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        //LOGGER.info("Positioned shape: {}", positionedShape);
        //LOGGER.info("Entity bounding box: {}", Shapes.create(entity.getBoundingBox()));
        return Shapes.joinIsNotEmpty(positionedShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
    }

    @Override
    public void entityInside(BlockState state, Level level_, BlockPos pos, Entity entity_) {
        List<VoxelShape> shapes = getShapesForFace(state.getValue(FACE));
        Boolean flag = false;
        if (level_.isClientSide) return;
        ServerLevel level = (ServerLevel) level_;
        List<EntityType<?>> allowedEntities = List.of(
            EntityType.PLAYER, portalgun.WEIGHTED_CUBE_ENTITYTYPE.get(),
            portalgun.COMPANION_CUBE_ENTITYTYPE.get()
        );
        for (Entity entity : level.getAllEntities()) {
            if (entity.isRemoved()) continue;
            if (!allowedEntities.contains(entity.getType())) continue;
            if (isColliding(shapes.get(2), entity, pos)) {
                if (entity.getType() == portalgun.WEIGHTED_CUBE_ENTITYTYPE.get()) {
                    entity.getEntityData().set(WeightedCube.ACTIVATED, true);
                }
                flag = true;
                break;
            } else if (entity.getType() == portalgun.WEIGHTED_CUBE_ENTITYTYPE.get()) {
                entity.getEntityData().set(WeightedCube.ACTIVATED, false);
            }
        }
        level.setBlockAndUpdate(pos, state.setValue(POWERED, flag));
    }

    public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        if (state.getValue(POWERED)) return 15;
        else return 0;
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    /*@Override
    public void tick(BlockState state, ServerLevel level_, BlockPos pos, RandomSource random) {
        LOGGER.info("ticked!");
        Boolean flag = false;
        if (level_.isClientSide) return;
        ServerLevel level = (ServerLevel) level_;
        List<EntityType<?>> allowedEntities = List.of(EntityType.PLAYER);
        for (Entity entity : level.getAllEntities()) {
            if (!allowedEntities.contains(entity.getType())) continue;
            if (isColliding(BUTTON_COLLISION_DOWN, entity, pos)) {
                flag = true;
                break;
            }
        }
        level.setBlockAndUpdate(pos, state.setValue(POWERED, flag));
    }*/
}
