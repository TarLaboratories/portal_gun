package com.portalgun.portalgun;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LaserCatcherBlockEntity extends BlockEntity {
    public Set<BlockPos> connected_devices = new HashSet<BlockPos>();

    public LaserCatcherBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.LASER_CATCHER_BLOCKENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity_) {
        LaserCatcherBlockEntity blockEntity = (LaserCatcherBlockEntity) blockEntity_;
        int signal_strength = state.getValue(LaserCatcher.ACTIVE) ? Math.max(200 - blockEntity.connected_devices.size()*100, 0) : 1;
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.above()))) ApertureStoneCable.setSignalStrength(level, pos.above(), signal_strength, Direction.DOWN, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.below()))) ApertureStoneCable.setSignalStrength(level, pos.below(), signal_strength, Direction.UP, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.north()))) ApertureStoneCable.setSignalStrength(level, pos.north(), signal_strength, Direction.SOUTH, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.south()))) ApertureStoneCable.setSignalStrength(level, pos.south(), signal_strength, Direction.NORTH, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.east()))) ApertureStoneCable.setSignalStrength(level, pos.east(), signal_strength, Direction.WEST, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.west()))) ApertureStoneCable.setSignalStrength(level, pos.west(), signal_strength, Direction.EAST, new HashSet<BlockPos>(), pos);
    }
}
