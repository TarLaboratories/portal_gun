package com.portalgun.portalgun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WeightedCube extends Entity {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EntityDataAccessor<Boolean> ACTIVATED = SynchedEntityData.defineId(WeightedCube.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> SPAWN_DROPPER_POS = SynchedEntityData.defineId(WeightedCube.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Optional<UUID>> PICKED_UP_BY = SynchedEntityData.defineId(WeightedCube.class, EntityDataSerializers.OPTIONAL_UUID);

    public WeightedCube(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ACTIVATED, false);
        this.entityData.define(SPAWN_DROPPER_POS, new BlockPos(0, 0, 0));
        this.entityData.define(PICKED_UP_BY, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        //activated = tag.getBoolean("activated");
        this.entityData.set(ACTIVATED, tag.getBoolean("activated"));
        this.entityData.set(SPAWN_DROPPER_POS, new BlockPos(tag.getIntArray("spawn_dropper_pos")[0], tag.getIntArray("spawn_dropper_pos")[1], tag.getIntArray("spawn_dropper_pos")[2]));
        this.entityData.set(PICKED_UP_BY, Optional.of(tag.getUUID("picked_up_by")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("activated", this.entityData.get(ACTIVATED));
        tag.putIntArray("spawn_dropper_pos", List.of(this.entityData.get(SPAWN_DROPPER_POS).getX(), this.entityData.get(SPAWN_DROPPER_POS).getY(), this.entityData.get(SPAWN_DROPPER_POS).getZ()));
        tag.putUUID("picked_up_by", this.entityData.get(PICKED_UP_BY).orElse(null));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void tick() {
        UUID picked_up_by_uuid = this.entityData.get(PICKED_UP_BY).orElse(null);
        if (picked_up_by_uuid != null) {
            LOGGER.info("UUID is not null, it is {}, following player", this.entityData.get(PICKED_UP_BY));
            Player player = this.level().getPlayerByUUID(picked_up_by_uuid);
            Vec3 following_pos = player.position();
            following_pos = following_pos.add(player.getLookAngle());
            this.moveTo(following_pos);
            return;
        }
        if (-this.getDeltaMovement().y < 1) this.addDeltaMovement(new Vec3(0, -0.1, 0));
        float flag = 1;
        boolean flag2 = false;
        if (this.mainSupportingBlockPos.orElse(null) != null) {
            if (this.level().getBlockState(this.mainSupportingBlockPos.get()) != null) {
                BlockState state = this.level().getBlockState(this.mainSupportingBlockPos.get());
                if (state.is(Blocks.SLIME_BLOCK)) flag = -0.25F;
                if (state.is(portalgun.PORTAL_BLOCK.get())) flag2 = true;
                float friction = state.getFriction(this.level(), this.mainSupportingBlockPos.get(), null)*0.95F;
                this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 1, friction));
            }
        }
        if (this.onGround() & !flag2) this.addDeltaMovement(new Vec3(0, -this.getDeltaMovement().y*flag, 0));
        this.move(MoverType.SELF, this.getDeltaMovement());
        super.tick();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void remove(RemovalReason reason) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        super.remove(reason);
        if (state != null & state.is(portalgun.PRESSURE_BUTTON.get())) state.getBlock().entityInside(state, this.level(), this.blockPosition(), this);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        LOGGER.info("Right clicked, optional uuid is: {}", entityData.get(PICKED_UP_BY));
        if (entityData.get(PICKED_UP_BY).orElse(null) == null) {
            entityData.set(PICKED_UP_BY, Optional.of(player.getUUID()));
        } else {
            entityData.set(PICKED_UP_BY, Optional.of(null));
        }
        return super.interact(player, hand);
    }
}
