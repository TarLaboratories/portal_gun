package com.portalgun.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HardLightBridge extends Block {
    public static final EnumProperty<Axis> AXIS = EnumProperty.create("axis", Axis.class);
    public static final VoxelShape HARD_LIGHT_BRIDGE_X = box(1.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    public static final VoxelShape HARD_LIGHT_BRIDGE_Y = box(0.0D, 1.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    public static final VoxelShape HARD_LIGHT_BRIDGE_Z = box(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 3.0D);

    public HardLightBridge(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

        public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.block();
        switch (state.getValue(AXIS)) {
            case X: shape = HARD_LIGHT_BRIDGE_X; break;
            case Y: shape = HARD_LIGHT_BRIDGE_Y; break;
            case Z: shape = HARD_LIGHT_BRIDGE_Z; break;
        }
        return shape;
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }
}
