package com.portalgun.portalgun;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("unused")
public class PortalBlock extends Block {
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty IS_ORANGE = BooleanProperty.create("is_orange");
    //public static final IntegerProperty LINK_X = IntegerProperty.create("link_x", 0, 6000);
    //public static final IntegerProperty LINK_Y = IntegerProperty.create("link_y", 0, 400);
    //public static final IntegerProperty LINK_Z = IntegerProperty.create("link_z", 0, 6000);
    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private BlockPos target_block_pos = null;
    private BlockState replaced_block = null;

    private static final Logger LOGGER = LogUtils.getLogger();

    public PortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(FACE, Direction.NORTH)
                .setValue(FACING, Direction.UP)
                .setValue(IS_ORANGE, true)
                //.setValue(LINK_X, 0)
                //.setValue(LINK_Y, 100)
                //.setValue(LINK_Z, 0)
                .setValue(IS_ACTIVE, false)
        );
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACE, FACING, IS_ORANGE, /*LINK_X, LINK_Y, LINK_Z,*/ IS_ACTIVE));
    }

    public Class<PortalBlockBlockEntity> getBlockEntityClass() {
        return PortalBlockBlockEntity.class;
    }
}
