package com.portalgun.portalgun;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortalBlockBlockEntity extends BlockEntity {

    public BlockPos link_pos;
    public BlockState replaced_block_blockstate;

    public PortalBlockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public PortalBlockBlockEntity setReplacedBlock(BlockState replaced_block) {
        replaced_block_blockstate = replaced_block;
        return this;
    }

    public PortalBlockBlockEntity setLinkPos(BlockPos pos) {
        link_pos = pos;
        return this;
    }

    public void removePortal() {
        this.level.setBlockAndUpdate(this.worldPosition, this.replaced_block_blockstate);
    }
}
