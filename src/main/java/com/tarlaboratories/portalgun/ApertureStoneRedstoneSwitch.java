package com.tarlaboratories.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class ApertureStoneRedstoneSwitch extends Block {
    public static final EnumProperty<RedstoneSide> IS_POWERED = EnumProperty.create("is_powered", RedstoneSide.class);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty UP = BooleanProperty.create("up");

    public ApertureStoneRedstoneSwitch(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(IS_POWERED, RedstoneSide.NONE)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(DOWN, false)
                .setValue(UP, false)
        );
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        state = state.setValue(NORTH, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.north())));
        state = state.setValue(SOUTH, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.south())));
        state = state.setValue(EAST, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.east())));
        state = state.setValue(WEST, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.west())));
        state = state.setValue(DOWN, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.below())));
        state = state.setValue(UP, ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.above())));
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IS_POWERED, NORTH, SOUTH, EAST, WEST, DOWN, UP));
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ApertureStoneCable.setSignalStrength(level, pos, 0, null, new HashSet<BlockPos>(), null);
        super.playerWillDestroy(level, pos, state, player);
    }
}
