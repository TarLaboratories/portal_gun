package com.portalgun.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeightedCubeDropper extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final EnumProperty<RedstoneSide> POWERED = EnumProperty.create("powered", RedstoneSide.class);
    public static final VoxelShape ACTIVE_SHAPE = Shapes.join(Shapes.block(), box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D), BooleanOp.NOT_SAME);
    public static final VoxelShape NOT_ACTIVE_SHAPE = Shapes.join(ACTIVE_SHAPE, box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D), BooleanOp.OR);

    public WeightedCubeDropper(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(ACTIVE, false)
                .setValue(POWERED, RedstoneSide.NONE)
        );
    }

    public EntityType<?> getDroppedEntityType() {
        return portalgun.WEIGHTED_CUBE_ENTITYTYPE.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(ACTIVE, POWERED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(ACTIVE)) return ACTIVE_SHAPE;
        else return NOT_ACTIVE_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }
}
