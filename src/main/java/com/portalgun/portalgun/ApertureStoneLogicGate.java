package com.portalgun.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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
    /*public static final EnumProperty<RedstoneSide> NORTH_POWER = EnumProperty.create("north_power", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> SOUTH_POWER = EnumProperty.create("south_power", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> EAST_POWER = EnumProperty.create("east_power", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> WEST_POWER = EnumProperty.create("west_power", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> DOWN_POWER = EnumProperty.create("down_power", RedstoneSide.class);
    public static final EnumProperty<RedstoneSide> UP_POWER = EnumProperty.create("up_power", RedstoneSide.class);*/

    public ApertureStoneLogicGate(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(LOGIC_GATE_TYPE, LogicGateType.OR)
                .setValue(OUTPUT_FACE, Direction.NORTH)
                /*.setValue(NORTH_POWER, RedstoneSide.NONE)
                .setValue(SOUTH_POWER, RedstoneSide.NONE)
                .setValue(EAST_POWER, RedstoneSide.NONE)
                .setValue(WEST_POWER, RedstoneSide.NONE)
                .setValue(DOWN_POWER, RedstoneSide.NONE)
                .setValue(UP_POWER, RedstoneSide.NONE)*/
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LOGIC_GATE_TYPE, OUTPUT_FACE));//, NORTH_POWER, SOUTH_POWER, EAST_POWER, WEST_POWER, DOWN_POWER, UP_POWER));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureStoneLogicGateBlockEntity(pos, state);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ApertureStoneCable.setSignalStrength(level, pos, 0, null, new HashSet<BlockPos>());
    }
}
