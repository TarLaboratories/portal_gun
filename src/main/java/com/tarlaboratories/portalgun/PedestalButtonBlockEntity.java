package com.tarlaboratories.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalButtonBlockEntity extends BlockEntity {
    public long ticks_for_tick = 2;
    public long ticks_since_last_decrease = 0;

    public PedestalButtonBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.PEDESTAL_BUTTON_BLOCKENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity_) {
        PedestalButtonBlockEntity blockEntity = (PedestalButtonBlockEntity) blockEntity_;
        if (blockEntity.ticks_since_last_decrease >= blockEntity.ticks_for_tick) {
            int ticks_since_activation = state.getValue(PedestalButton.TICKS_SINCE_ACTIVATION);
            if (ticks_since_activation < 8) {
                level.setBlockAndUpdate(pos, state.setValue(PedestalButton.TICKS_SINCE_ACTIVATION, ticks_since_activation + 1));
                if (ticks_since_activation >= 7) level.setBlockAndUpdate(pos, state.setValue(PedestalButton.TICKS_SINCE_ACTIVATION, 8).setValue(PedestalButton.ACTIVATED, false));
            }
            blockEntity.ticks_since_last_decrease = 0;
        } else blockEntity.ticks_since_last_decrease += 1;
    }
}
