package com.tarlaboratories.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class ApertureStoneIndicator extends ApertureStoneFullBlock {
    public ApertureStoneIndicator(Properties properties) {
        super(properties);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        if (state.getValue(IS_POWERED) == RedstoneSide.UP) return 15;
        else return 0;
    }
}
