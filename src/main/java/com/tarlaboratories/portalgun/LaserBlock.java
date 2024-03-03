package com.tarlaboratories.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaserBlock extends Block {
    public static final EnumProperty<Axis> AXIS = EnumProperty.create("axis", Axis.class);
    public static final BooleanProperty BLOCKED = BooleanProperty.create("blocked");
    public static final VoxelShape SHAPE_X = box(0.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D);
    public static final VoxelShape SHAPE_Y = box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    public static final VoxelShape SHAPE_Z = box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 16.0D);

    public LaserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(AXIS, Axis.X)
                .setValue(BLOCKED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS, BLOCKED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        /*switch (state.getValue(AXIS)) {
            case X: return SHAPE_X;
            case Y: return SHAPE_Y;
            case Z: return SHAPE_Z;
        }*/
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        VoxelShape shape = null;
        switch (state.getValue(AXIS)) {
            case X: shape = SHAPE_X;
            case Y: shape = SHAPE_Y;
            case Z: shape = SHAPE_Z;
        }
        if (Shapes.joinIsNotEmpty(shape.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(entity.getBoundingBox()), BooleanOp.AND)) {
            if (!entity.fireImmune()) entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getType() == portalgun.WEIGHTED_CUBE_ENTITYTYPE.get()) level.setBlock(pos, state.setValue(BLOCKED, true), 0);
            if (entity.getType() == portalgun.COMPANION_CUBE_ENTITYTYPE.get()) level.setBlock(pos, state.setValue(BLOCKED, true), 0);
        }
    }
}
