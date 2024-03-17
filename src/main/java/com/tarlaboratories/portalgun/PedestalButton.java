package com.tarlaboratories.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PedestalButton extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final IntegerProperty TICKS_SINCE_ACTIVATION = IntegerProperty.create("ticks_since_activation", 0, 8);
    public static final VoxelShape SHAPE_EAST_DOWN = Shapes.join(box(11.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D), Shapes.join(box(12.0D, 1.0D, 7.0D, 14.0D, 12.0D, 9.0D), Shapes.join(box(13.0D, 13.0D, 7.0D, 14.0D, 14.0D, 9.0D), box(14.0D, 1.0D, 7.0D, 15.0D, 15.0D, 9.0D), BooleanOp.OR), BooleanOp.OR), BooleanOp.OR);

    public PedestalButton(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(ACTIVATED, false)
            .setValue(TICKS_SINCE_ACTIVATION, 8)
            .setValue(FACE, context.getClickedFace().getOpposite())
            .setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(ACTIVATED, TICKS_SINCE_ACTIVATION, FACE, FACING));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit_result) {
        level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true).setValue(TICKS_SINCE_ACTIVATION, 0));
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalButtonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == portalgun.PEDESTAL_BUTTON_BLOCKENTITY.get() ? PedestalButtonBlockEntity::tick : null;
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE_EAST_DOWN;
        Direction facing = state.getValue(FACING);
        Direction face = state.getValue(FACE);
        switch (face) {
            case DOWN: 
                switch (facing) {
                    case DOWN: break;
                    case UP: break;
                    case EAST: shape = rotate_y_shape(shape, Rotation.NONE); break;
                    case WEST: shape = rotate_y_shape(shape, Rotation.CLOCKWISE_180); break;
                    case SOUTH: shape = rotate_y_shape(shape, Rotation.CLOCKWISE_90); break;
                    case NORTH: shape = rotate_y_shape(shape, Rotation.COUNTERCLOCKWISE_90); break;
                } break;
            case UP:
                switch (facing) {
                    case DOWN: break;
                    case UP: break;
                    case EAST: shape = rotate_y_shape(shape, Rotation.NONE); break;
                    case WEST: shape = rotate_y_shape(shape, Rotation.CLOCKWISE_180); break;
                    case SOUTH: shape = rotate_y_shape(shape, Rotation.CLOCKWISE_90); break;
                    case NORTH: shape = rotate_y_shape(shape, Rotation.COUNTERCLOCKWISE_90); break;
                } shape = rotate_x_shape(shape, Rotation.CLOCKWISE_180); break;
            case EAST:
            case WEST:
            case SOUTH:
            case NORTH:
        }
        return shape;
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    public VoxelShape rotate_y_shape(VoxelShape shape, Rotation rotation) {
        VoxelShape out = Shapes.empty();
        double x1, y1, z1, x2, y2, z2, tmp;
        AABB tmp_aabb;
        for (AABB aabb : shape.toAabbs()) {
            x1 = aabb.minX; y1 = aabb.minY; z1 = aabb.minZ; x2 = aabb.maxX; y2 = aabb.maxY; z2 = aabb.maxZ;
            switch (rotation) {
                case CLOCKWISE_90: tmp = x1; x1 = z1; z1 = x2; x2 = z2; z2 = tmp; break;
                case CLOCKWISE_180: tmp = x1; x1 = x2; x2 = tmp; tmp = z1; z1 = z2; z2 = tmp; break;
                case COUNTERCLOCKWISE_90: tmp = x1; x1 = z2; z2 = x2; x2 = z1; z1 = tmp; break;
                case NONE: break;
            }
            tmp_aabb = new AABB(x1, y1, z1, x2, y2, z2);
            out = Shapes.join(out, Shapes.create(tmp_aabb), BooleanOp.OR);
        }
        return out;
    }

    public VoxelShape rotate_x_shape(VoxelShape shape, Rotation rotation) {
        VoxelShape out = Shapes.empty();
        double x1, y1, z1, x2, y2, z2, tmp;
        AABB tmp_aabb;
        for (AABB aabb : shape.toAabbs()) {
            x1 = aabb.minX; y1 = aabb.minY; z1 = aabb.minZ; x2 = aabb.maxX; y2 = aabb.maxY; z2 = aabb.maxZ;
            switch (rotation) {
                case CLOCKWISE_90: tmp = y1; y1 = z1; z1 = y2; x2 = z2; z2 = tmp; break;
                case CLOCKWISE_180: tmp = y1; y1 = y2; y2 = tmp; tmp = z1; z1 = z2; z2 = tmp; break;
                case COUNTERCLOCKWISE_90: tmp = y1; y1 = z2; z2 = y2; y2 = z1; z1 = tmp; break;
                case NONE: break;
            }
            tmp_aabb = new AABB(x1, y1, z1, x2, y2, z2);
            out = Shapes.join(out, Shapes.create(tmp_aabb), BooleanOp.OR);
        }
        return out;
    }
}
