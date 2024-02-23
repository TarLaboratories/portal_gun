package com.portalgun.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeApertureStoneSourceBlockEntity extends BlockEntity {
    public CreativeApertureStoneSourceBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.CREATIVE_APERTURESTONE_SOURCE_BLOCKENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.above()))) ApertureStoneCable.setSignalStrength(level, pos.above(), 1000, Direction.DOWN, new HashSet<BlockPos>());
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.below()))) ApertureStoneCable.setSignalStrength(level, pos.below(), 1000, Direction.UP, new HashSet<BlockPos>());
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.north()))) ApertureStoneCable.setSignalStrength(level, pos.north(), 1000, Direction.SOUTH, new HashSet<BlockPos>());
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.south()))) ApertureStoneCable.setSignalStrength(level, pos.south(), 1000, Direction.NORTH, new HashSet<BlockPos>());
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.east()))) ApertureStoneCable.setSignalStrength(level, pos.east(), 1000, Direction.WEST, new HashSet<BlockPos>());
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.west()))) ApertureStoneCable.setSignalStrength(level, pos.west(), 1000, Direction.EAST, new HashSet<BlockPos>());
    }
}
