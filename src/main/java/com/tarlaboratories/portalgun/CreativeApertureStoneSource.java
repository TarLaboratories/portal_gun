package com.tarlaboratories.portalgun;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeApertureStoneSource extends Block implements EntityBlock {
    public CreativeApertureStoneSource(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeApertureStoneSourceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == portalgun.CREATIVE_APERTURESTONE_SOURCE_BLOCKENTITY.get() ? CreativeApertureStoneSourceBlockEntity::tick : null;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        for (Direction direction : Direction.values()) {
            ApertureStoneCable.setSignalStrength(level, pos.relative(direction), 0, null, new HashSet<BlockPos>(), null);
        }
    }
}
