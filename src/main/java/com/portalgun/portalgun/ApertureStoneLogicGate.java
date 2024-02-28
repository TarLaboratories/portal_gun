package com.portalgun.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ApertureStoneLogicGate extends ApertureStoneCable implements EntityBlock {
    public static final EnumProperty<LogicGateType> LOGIC_GATE_TYPE = EnumProperty.create("logic_gate_type", LogicGateType.class);
    public static final DirectionProperty OUTPUT_FACE = DirectionProperty.create("output_face");

    public ApertureStoneLogicGate(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(LOGIC_GATE_TYPE, LogicGateType.OR)
                .setValue(OUTPUT_FACE, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LOGIC_GATE_TYPE, OUTPUT_FACE));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureStoneLogicGateBlockEntity(pos, state);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ApertureStoneCable.setSignalStrength(level, pos, 0, null, new HashSet<BlockPos>(), null);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(OUTPUT_FACE, context.getClickedFace());
    }
}
