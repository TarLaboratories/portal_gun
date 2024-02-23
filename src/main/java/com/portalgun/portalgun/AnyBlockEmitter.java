package com.portalgun.portalgun;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class AnyBlockEmitter extends Block {
    public final Function<BlockState, BlockState> getStateForEmmition;
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public AnyBlockEmitter(Properties properties, Function<BlockState, BlockState> getStateForEmmition) {
        super(properties);
        this.getStateForEmmition = getStateForEmmition;
        this.registerDefaultState(
            defaultBlockState()
                .setValue(IS_ACTIVE, false)
                .setValue(FACING, Direction.NORTH)
        );
    }

    public void emmit(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction direction = state.getValue(FACING);
        Direction old_direction = direction;
        BlockPos tmp_pos = pos.relative(direction);
        BlockState state_tmp = state;
        int i = 0;
        while (level.getBlockState(tmp_pos).canBeReplaced() || level.getBlockState(tmp_pos).is(this.getStateForEmmition.apply(state_tmp).getBlock())) {
            i++;
            if (i > 1000) break;
            level.setBlock(tmp_pos, this.getStateForEmmition.apply(state_tmp), 15);
            tmp_pos = tmp_pos.relative(direction);
            if (level.getBlockState(tmp_pos).is(portalgun.PORTAL_BLOCK.get())) {
                if (direction != level.getBlockState(tmp_pos).getValue(PortalBlock.FACE).getOpposite()) break;
                tmp_pos = ((PortalBlockBlockEntity) level.getBlockEntity(tmp_pos)).link_pos;
                if (tmp_pos == null) return;
                old_direction = direction;
                direction = level.getBlockState(tmp_pos).getValue(PortalBlock.FACE);
                tmp_pos = tmp_pos.relative(direction);
                try {
                    if (direction.getAxis() == Axis.X || direction.getAxis() == Axis.Z) state_tmp = state_tmp.setValue(HardLightBridgeEmitter.AXIS, Axis.Y);
                    else if (old_direction.getAxis() != Axis.Y) state_tmp = state_tmp.setValue(HardLightBridgeEmitter.AXIS, old_direction.getAxis());
                } catch (Exception e) {}
            }
        }
        level.setBlock(pos, state.setValue(IS_ACTIVE, true), 15);
    }

    public void deactivate(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction direction = state.getValue(FACING);
        BlockPos tmp_pos = pos.relative(direction);
        int i = 0;
        while (level.getBlockState(tmp_pos).is(this.getStateForEmmition.apply(state).getBlock()) || level.getBlockState(tmp_pos).isAir()) {
            i++;
            if (i > 10000) break;
            level.setBlock(tmp_pos, Blocks.AIR.defaultBlockState(), 15);
            tmp_pos = tmp_pos.relative(direction);
            if (level.getBlockState(tmp_pos).is(portalgun.PORTAL_BLOCK.get())) {
                if (direction != level.getBlockState(tmp_pos).getValue(PortalBlock.FACE).getOpposite()) break;
                tmp_pos = ((PortalBlockBlockEntity) level.getBlockEntity(tmp_pos)).link_pos;
                if (tmp_pos == null) return;
                direction = level.getBlockState(tmp_pos).getValue(PortalBlock.FACE);
                tmp_pos = tmp_pos.relative(direction);
            }
        }
        level.setBlock(pos, state.setValue(IS_ACTIVE, false), 15);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, IS_ACTIVE));
    }
}
