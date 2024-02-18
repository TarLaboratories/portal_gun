package com.portalgun.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmancipationGridBlock extends Block {
    public static EnumProperty<Axis> AXIS = EnumProperty.create("axis", Axis.class);
    public static final VoxelShape EMANCIPATION_GRID_X = box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D);
    public static final VoxelShape EMANCIPATION_GRID_Y = box(0.0D, 7.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    public static final VoxelShape EMANCIPATION_GRID_Z = box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public EmancipationGridBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(EmancipationGridBlock.AXIS, Axis.X)
        );
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.block();
        switch (state.getValue(EmancipationGridBlock.AXIS)) {
            case X: shape = EMANCIPATION_GRID_X; break;
            case Y: shape = EMANCIPATION_GRID_Y; break;
            case Z: shape = EMANCIPATION_GRID_Z; break;
        }
        return shape;
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            for (ItemStack itemstack : player.getInventory().items) {
                if (itemstack.is(portalgun.PORTAL_GUN_ITEM.get())) {
                    PortalGunItem.clearPortals(level, itemstack);
                }
            }
        }
    }
}
