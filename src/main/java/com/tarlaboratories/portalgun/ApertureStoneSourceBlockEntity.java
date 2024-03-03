package com.tarlaboratories.portalgun;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

public class ApertureStoneSourceBlockEntity extends BlockEntity implements IEnergyStorage {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    public int signal_strength = 0;
    public int signal_strength_cap = 1000;
    public int capacity = 1000000;
    public int energy = 0;
    public Set<BlockPos> connected_devices = new HashSet<BlockPos>();

    public ApertureStoneSourceBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.APERTURESTONE_SOURCE_BLOCKENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity_) {
        ApertureStoneSourceBlockEntity blockEntity = (ApertureStoneSourceBlockEntity) blockEntity_;
        blockEntity.signal_strength = Math.min(blockEntity.energy, blockEntity.signal_strength_cap) - blockEntity.connected_devices.size()*100;
        blockEntity.signal_strength = Math.max(blockEntity.signal_strength, 0);
        //LOGGER.info("Stored energy: {}", blockEntity.energy);
        //LOGGER.info("Signal strength cap: {}", blockEntity.signal_strength_cap);
        //LOGGER.info("Signal strength: {}", blockEntity.signal_strength);
        blockEntity.energy -= blockEntity.signal_strength;
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.above()))) ApertureStoneCable.setSignalStrength(level, pos.above(), blockEntity.signal_strength, Direction.DOWN, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.below()))) ApertureStoneCable.setSignalStrength(level, pos.below(), blockEntity.signal_strength, Direction.UP, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.north()))) ApertureStoneCable.setSignalStrength(level, pos.north(), blockEntity.signal_strength, Direction.SOUTH, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.south()))) ApertureStoneCable.setSignalStrength(level, pos.south(), blockEntity.signal_strength, Direction.NORTH, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.east()))) ApertureStoneCable.setSignalStrength(level, pos.east(), blockEntity.signal_strength, Direction.WEST, new HashSet<BlockPos>(), pos);
        if (ApertureStoneCable.canConnectApertureStone(level.getBlockState(pos.west()))) ApertureStoneCable.setSignalStrength(level, pos.west(), blockEntity.signal_strength, Direction.EAST, new HashSet<BlockPos>(), pos);
    }

    @Override
    public void load(CompoundTag tag) {
        this.signal_strength = tag.getInt("signal_strength");
        this.signal_strength_cap = tag.getInt("signal_strength_cap");
        this.energy = tag.getInt("energy");
        this.capacity = tag.getInt("capacity");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("signal_strength", this.signal_strength);
        tag.putInt("signal_strength_cap", this.signal_strength_cap);
        tag.putInt("energy", this.energy);
        tag.putInt("capacity", this.capacity);
        super.saveAdditional(tag);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, maxReceive);
        if (!simulate) energy += energyReceived;
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
