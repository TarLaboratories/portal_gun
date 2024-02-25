package com.portalgun.portalgun;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class WeightedStorageCubeBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public WeightedStorageCubeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED));
    }
}
