package com.tarlaboratories.portalgun;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedStorageCubeBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public WeightedStorageCubeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(VARIANT, 0).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED, VARIANT, ACTIVE));
    }
}
