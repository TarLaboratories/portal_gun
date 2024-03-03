package com.tarlaboratories.portalgun;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HardLightBridgeEmitter extends AnyBlockEmitter {
    public static final EnumProperty<Axis> AXIS = EnumProperty.create("axis", Axis.class);
    public static final Function<BlockState, BlockState> getStateForEmmition = (BlockState state) -> {return portalgun.HARD_LIGHT_BRIDGE.get().defaultBlockState().setValue(HardLightBridge.AXIS, state.getValue(AXIS));};
    public static final VoxelShape HARD_LIGHT_BRIDGE_X = box(1.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    public static final VoxelShape HARD_LIGHT_BRIDGE_Y = box(0.0D, 1.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    public static final VoxelShape HARD_LIGHT_BRIDGE_Z = box(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 3.0D);
    public static final VoxelShape NORTH_FACE_EMITTER_VERTICAL = box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 1.0D);
    public static final VoxelShape NORTH_FACE_EMITTER_HORIZONTAL = box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 1.0D);
    public static final VoxelShape SOUTH_FACE_EMITTER_VERTICAL = box(0.0D, 0.0D, 15.0D, 4.0D, 16.0D, 16.0D);
    public static final VoxelShape SOUTH_FACE_EMITTER_HORIZONTAL = box(0.0D, 0.0D, 15.0D, 16.0D, 4.0D, 16.0D);
    public static final VoxelShape EAST_FACE_EMITTER_VERTICAL = box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    public static final VoxelShape EAST_FACE_EMITTER_HORIZONTAL = box(15.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    public static final VoxelShape WEST_FACE_EMITTER_VERTICAL = box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 4.0D);
    public static final VoxelShape WEST_FACE_EMITTER_HORIZONTAL = box(0.0D, 0.0D, 0.0D, 1.0D, 4.0D, 16.0D);
    public static final VoxelShape DOWN_FACE_EMITTER_X = box(0.0D, 0.0D, 0.0D, 4.0D, 1.0D, 16.0D);
    public static final VoxelShape DOWN_FACE_EMITTER_Z = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 4.0D);
    public static final VoxelShape UP_FACE_EMITTER_X = box(0.0D, 15.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    public static final VoxelShape UP_FACE_EMITTER_Z = box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 4.0D);

    public HardLightBridgeEmitter(Properties properties) {
        super(properties, getStateForEmmition);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        deactivate(level, pos);
    }

    public static VoxelShape calculateShape(Direction face, Axis axis, Boolean is_active) {
        VoxelShape shape = null;
        face = face.getOpposite();
        switch (face) {
            case NORTH: if (axis == Axis.Y) shape = NORTH_FACE_EMITTER_HORIZONTAL; else shape = NORTH_FACE_EMITTER_VERTICAL; break;
            case SOUTH: if (axis == Axis.Y) shape = SOUTH_FACE_EMITTER_HORIZONTAL; else shape = SOUTH_FACE_EMITTER_VERTICAL; break;
            case EAST: if (axis == Axis.Y) shape = EAST_FACE_EMITTER_HORIZONTAL; else shape = EAST_FACE_EMITTER_VERTICAL; break;
            case WEST: if (axis == Axis.Y) shape = WEST_FACE_EMITTER_HORIZONTAL; else shape = WEST_FACE_EMITTER_VERTICAL; break;
            case UP: if (axis == Axis.X) shape = UP_FACE_EMITTER_X; else shape = UP_FACE_EMITTER_Z; break;
            case DOWN: if (axis == Axis.X) shape = DOWN_FACE_EMITTER_X; else shape = DOWN_FACE_EMITTER_Z; break;
        }
        if (is_active) {
            switch (axis) {
                case X: shape = Shapes.join(shape, HARD_LIGHT_BRIDGE_X, BooleanOp.OR); break;
                case Y: shape = Shapes.join(shape, HARD_LIGHT_BRIDGE_Y, BooleanOp.OR); break;
                case Z: shape = Shapes.join(shape, HARD_LIGHT_BRIDGE_Z, BooleanOp.OR); break;
            }
        }
        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return calculateShape(state.getValue(FACING), state.getValue(AXIS), state.getValue(IS_ACTIVE));
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        Axis axis = null;
        Vec3 player_pos = context.getPlayer().position();
        BlockPos block_pos = context.getClickedPos();
        Vec3 distance = new Vec3(Math.abs(block_pos.getX() - player_pos.x), Math.abs(block_pos.getY() - player_pos.y), Math.abs(block_pos.getZ() - player_pos.z));
        state = state.setValue(FACING, context.getClickedFace());
        switch (state.getValue(FACING)) {
            case NORTH: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.X; break;
            case SOUTH: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.X; break;
            case EAST: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.Z; break;
            case WEST: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.Z; break;
            case UP: if (distance.x >= distance.z) axis = Axis.X; else axis = Axis.Z; break;
            case DOWN: if (distance.x >= distance.z) axis = Axis.X; else axis = Axis.Z; break;
        }
        state = state.setValue(EmancipationGridEmitter.AXIS, axis);
        return state;
    }
}
