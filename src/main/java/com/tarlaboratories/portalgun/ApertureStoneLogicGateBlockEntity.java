package com.tarlaboratories.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ApertureStoneLogicGateBlockEntity extends BlockEntity {
    public int NORTH_POWER = 0;
    public int SOUTH_POWER = 0;
    public int EAST_POWER = 0;
    public int WEST_POWER = 0;
    public int DOWN_POWER = 0;
    public int UP_POWER = 0;

    public ApertureStoneLogicGateBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.APERTURESTONE_LOGIC_GATE_BLOCKENTITY.get(), pos, state);
    }
}
