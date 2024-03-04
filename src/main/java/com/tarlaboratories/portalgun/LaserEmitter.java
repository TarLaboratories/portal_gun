package com.tarlaboratories.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LaserEmitter extends AnyBlockEmitter {
    public LaserEmitter(Properties properties) {
        super(properties, (BlockState state) -> portalgun.LASER_BLOCK.get().defaultBlockState().setValue(LaserBlock.AXIS, state.getValue(AnyBlockEmitter.FACING).getAxis()).setValue(LaserBlock.BLOCKED, false));
        this.blockedByCubes = true;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        deactivate(level, pos);
        for (Direction direction : Direction.values()) {
            ApertureStoneCable.setSignalStrength(level, pos.relative(direction), 0, null, new HashSet<BlockPos>(), null);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(IS_ACTIVE, false).setValue(FACING, context.getClickedFace());
    }
}
