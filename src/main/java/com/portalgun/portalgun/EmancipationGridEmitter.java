package com.portalgun.portalgun;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmancipationGridEmitter extends Block {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    public static final EnumProperty<Axis> AXIS = EnumProperty.create("axis", Axis.class);
    public static final VoxelShape NORTH_FACE_EMITTER_VERTICAL = box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 1.0D);
    public static final VoxelShape NORTH_FACE_EMITTER_HORIZONTAL = box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 1.0D);
    public static final VoxelShape SOUTH_FACE_EMITTER_VERTICAL = box(6.0D, 0.0D, 15.0D, 10.0D, 16.0D, 16.0D);
    public static final VoxelShape SOUTH_FACE_EMITTER_HORIZONTAL = box(0.0D, 6.0D, 15.0D, 16.0D, 10.0D, 16.0D);
    public static final VoxelShape EAST_FACE_EMITTER_VERTICAL = box(15.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    public static final VoxelShape EAST_FACE_EMITTER_HORIZONTAL = box(15.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    public static final VoxelShape WEST_FACE_EMITTER_VERTICAL = box(0.0D, 0.0D, 6.0D, 1.0D, 16.0D, 10.0D);
    public static final VoxelShape WEST_FACE_EMITTER_HORIZONTAL = box(0.0D, 6.0D, 0.0D, 1.0D, 10.0D, 16.0D);
    public static final VoxelShape DOWN_FACE_EMITTER_X = box(6.0D, 0.0D, 0.0D, 10.0D, 1.0D, 16.0D);
    public static final VoxelShape DOWN_FACE_EMITTER_Z = box(0.0D, 0.0D, 6.0D, 16.0D, 1.0D, 10.0D);
    public static final VoxelShape UP_FACE_EMITTER_X = box(6.0D, 15.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    public static final VoxelShape UP_FACE_EMITTER_Z = box(0.0D, 15.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    public static final VoxelShape EMANCIPATION_GRID_X = box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D);
    public static final VoxelShape EMANCIPATION_GRID_Y = box(0.0D, 7.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    public static final VoxelShape EMANCIPATION_GRID_Z = box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public EmancipationGridEmitter(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(FACE, Direction.NORTH)
                .setValue(AXIS, Axis.X)
                .setValue(IS_ACTIVE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACE, AXIS, IS_ACTIVE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        Axis axis = null;
        Vec3 player_pos = context.getPlayer().position();
        BlockPos block_pos = context.getClickedPos();
        Vec3 distance = new Vec3(Math.abs(block_pos.getX() - player_pos.x), Math.abs(block_pos.getY() - player_pos.y), Math.abs(block_pos.getZ() - player_pos.z));
        state = state.setValue(EmancipationGridEmitter.FACE, context.getClickedFace().getOpposite());
        switch (state.getValue(EmancipationGridEmitter.FACE)) {
            case NORTH: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.X; break;
            case SOUTH: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.X; break;
            case EAST: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.Z; break;
            case WEST: if (distance.y >= distance.x && distance.y >= distance.z) axis = Axis.Y; else axis = Axis.Z; break;
            case UP: if (distance.x >= distance.z) axis = Axis.X; else axis = Axis.Z; break;
            case DOWN: if (distance.x >= distance.z) axis = Axis.X; else axis = Axis.Z; break;
        }
        state = state.setValue(EmancipationGridEmitter.AXIS, axis);
        state = updateShape(state, null, null, context.getLevel(), block_pos, null);
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        if (!state.getValue(EmancipationGridEmitter.IS_ACTIVE)) tryActivate(state, level, pos);
        else if (!level.getBlockState(pos.relative(state.getValue(EmancipationGridEmitter.FACE).getOpposite())).is(portalgun.EMANCIPATION_GRID_BLOCK.get())) state = state.setValue(EmancipationGridEmitter.IS_ACTIVE, false);
        return state;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!state.getValue(EmancipationGridEmitter.IS_ACTIVE)) return;
        BlockPos tmp_pos = pos;
        for (int i = 0; i < Config.emancipation_grid_emitter_range; i++) {
            tmp_pos = tmp_pos.relative(state.getValue(EmancipationGridEmitter.FACE).getOpposite());
            if (level.getBlockState(tmp_pos).is(portalgun.EMANCIPATION_GRID_BLOCK.get())) level.setBlockAndUpdate(tmp_pos, Blocks.AIR.defaultBlockState());
            else break;
        }
    }

    public static VoxelShape calculateShape(Direction face, Axis axis, Boolean is_active) {
        VoxelShape shape = null;
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
                case X: shape = Shapes.join(shape, EMANCIPATION_GRID_X, BooleanOp.OR); break;
                case Y: shape = Shapes.join(shape, EMANCIPATION_GRID_Y, BooleanOp.OR); break;
                case Z: shape = Shapes.join(shape, EMANCIPATION_GRID_Z, BooleanOp.OR); break;
            }
        }
        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return calculateShape(state.getValue(EmancipationGridEmitter.FACE), state.getValue(EmancipationGridEmitter.AXIS), state.getValue(EmancipationGridEmitter.IS_ACTIVE));
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    public boolean tryActivate(BlockState state, LevelAccessor level, BlockPos pos) {
        if (level.isClientSide()) return false;
        //LOGGER.info("trying to activate {}", state);
        if (level.getBlockState(pos.relative(state.getValue(EmancipationGridEmitter.FACE).getOpposite())).is(portalgun.EMANCIPATION_GRID_BLOCK.get())) {
            level.setBlock(pos, state.setValue(EmancipationGridEmitter.IS_ACTIVE, true), 15);
            return true;
        }
        int dist = 0;
        BlockPos tmp_pos = pos;
        for (int i = 0; i < Config.emancipation_grid_emitter_range; i++) {
            tmp_pos = tmp_pos.relative(state.getValue(EmancipationGridEmitter.FACE).getOpposite());
            dist++;
            //LOGGER.info("checking {}", tmp_pos);
            //LOGGER.info("dist is {}", dist);
            if (level.getBlockState(tmp_pos).is(portalgun.EMANCIPATION_GRID_EMITTER.get())) break;
            if (!canReplaceWithGrid(level.getBlockState(tmp_pos))) return false;
            //LOGGER.info("checked {}", tmp_pos);
        }
        if (!level.getBlockState(tmp_pos).is(portalgun.EMANCIPATION_GRID_EMITTER.get())) return false;
        //LOGGER.info("checking faces");
        if (level.getBlockState(tmp_pos).getValue(EmancipationGridEmitter.FACE) != state.getValue(EmancipationGridEmitter.FACE).getOpposite()) return false;
        //LOGGER.info("checking axis");
        if (level.getBlockState(tmp_pos).getValue(EmancipationGridEmitter.AXIS) != state.getValue(EmancipationGridEmitter.AXIS)) return false;
        //LOGGER.info("starting to set blocks");
        tmp_pos = pos;
        for (int i = 0; i < dist - 1; i++) {
            tmp_pos = tmp_pos.relative(state.getValue(EmancipationGridEmitter.FACE).getOpposite());
            //LOGGER.info("setting block {}", tmp_pos);
            level.setBlock(tmp_pos, portalgun.EMANCIPATION_GRID_BLOCK.get().defaultBlockState().setValue(EmancipationGridBlock.AXIS, state.getValue(EmancipationGridEmitter.AXIS)), 15);
        }
        level.setBlock(pos, state.setValue(EmancipationGridEmitter.IS_ACTIVE, true), 15);
        return true;
    }

    public static boolean canReplaceWithGrid(BlockState state) {
        return state.canBeReplaced();
    }
}