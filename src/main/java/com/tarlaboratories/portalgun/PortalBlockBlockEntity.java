package com.tarlaboratories.portalgun;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList.WorldListEntry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class PortalBlockBlockEntity extends BlockEntity {

    public BlockPos link_pos;
    public BlockState replaced_block_blockstate;
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final Logger LOGGER = LogUtils.getLogger();

    public PortalBlockBlockEntity(BlockPos pos, BlockState state) {
        super(portalgun.PORTAL_BLOCK_BLOCKSTATE.get(), pos, state);
    }
    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_129205_, Property<T> p_129206_, String p_129207_, CompoundTag p_129208_, CompoundTag p_129209_) {
        Optional<T> optional = p_129206_.getValue(p_129208_.getString(p_129207_));
        if (optional.isPresent()) {
            return p_129205_.setValue(p_129206_, optional.get());
        } else {
            LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_129207_, p_129208_.getString(p_129207_), p_129209_);
            return p_129205_;
        }
    }

    protected BlockState readBlockState(CompoundTag tag) {
        if (!tag.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            ResourceLocation resourcelocation = new ResourceLocation(tag.getString("Name"));
            Block block = RegistryObject.create(resourcelocation, ForgeRegistries.BLOCKS).get();
            BlockState blockstate = block.defaultBlockState();
            if (tag.contains("Properties", 10)) {
               CompoundTag compoundtag = tag.getCompound("Properties");
               StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
               for(String s : compoundtag.getAllKeys()) {
                  Property<?> property = statedefinition.getProperty(s);
                  if (property != null) {
                     blockstate = setValueHelper(blockstate, property, s, compoundtag, tag);
                  }
               }
            }
            return blockstate;
        }
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("link_pos")) link_pos = NbtUtils.readBlockPos(tag.getCompound("link_pos"));
        if (tag.contains("replaced_block_blockstate")) replaced_block_blockstate = readBlockState(tag.getCompound("replaced_block_blockstate"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (link_pos != null) tag.put("link_pos", NbtUtils.writeBlockPos(link_pos));
        if (replaced_block_blockstate != null) tag.put("replaced_block_blockstate", NbtUtils.writeBlockState(replaced_block_blockstate));
    }

    public PortalBlockBlockEntity setReplacedBlock(BlockState replaced_block) {
        replaced_block_blockstate = replaced_block;
        this.setChanged();
        return this;
    }

    public PortalBlockBlockEntity setLinkPos(BlockPos pos) {
        link_pos = pos;
        this.setChanged();
        return this;
    }

    public void removePortal() {
        //LOGGER.info("===========Removing portal block==============");
        //LOGGER.info(this.toString());
        //LOGGER.info(this.replaced_block_blockstate.toString());
        //LOGGER.info("==============================================");
        if (this.replaced_block_blockstate == null) return;
        ServerLevel serverlevel = null;
        long chunk = 0;
        if (this.level != null & !this.level.isClientSide) {
            serverlevel = (ServerLevel) this.level;
            chunk = new ChunkPos(this.worldPosition).toLong();
            ForgeChunkManager.forceChunk(serverlevel, "portalgun", this.worldPosition, (int) chunk, (int) (chunk >> 32), true, false);
        }
        //((PortalBlockBlockEntity) this.level.getBlockEntity(this.worldPosition.relative(this.getBlockState().getValue(PortalBlock.FACING)))).removePortal();
        this.destroyHardLightBridge();
        this.destroyLaser();
        this.level.setBlockAndUpdate(this.worldPosition, this.replaced_block_blockstate);
        if (serverlevel != null) ForgeChunkManager.forceChunk(serverlevel, "portalgun", this.worldPosition, (int) chunk, (int) (chunk >> 32), false, false);
    }

    public void destroyHardLightBridge() {
        if (level.getBlockState(this.worldPosition.relative(this.getBlockState().getValue(PortalBlock.FACE))).is(portalgun.HARD_LIGHT_BRIDGE.get())) {
            BlockState state = level.getBlockState(this.worldPosition);
            Direction direction = state.getValue(PortalBlock.FACE);
            BlockPos tmp_pos = this.worldPosition.relative(direction);
            while (level.getBlockState(tmp_pos).is(portalgun.HARD_LIGHT_BRIDGE.get())) {
                level.setBlock(tmp_pos, Blocks.AIR.defaultBlockState(), 15);
                tmp_pos = tmp_pos.relative(direction);
                if (level.getBlockState(tmp_pos).is(portalgun.PORTAL_BLOCK.get())) {
                    if (direction != level.getBlockState(tmp_pos).getValue(PortalBlock.FACE).getOpposite()) break;
                    tmp_pos = ((PortalBlockBlockEntity) level.getBlockEntity(tmp_pos)).link_pos;
                    direction = level.getBlockState(tmp_pos).getValue(PortalBlock.FACE);
                    tmp_pos = tmp_pos.relative(direction);
                }
            }
        }
    }

    public void destroyLaser() {
        if (level.getBlockState(this.worldPosition.relative(this.getBlockState().getValue(PortalBlock.FACE))).is(portalgun.LASER_BLOCK.get())) {
            BlockState state = level.getBlockState(this.worldPosition);
            Direction direction = state.getValue(PortalBlock.FACE);
            BlockPos tmp_pos = this.worldPosition.relative(direction);
            while (level.getBlockState(tmp_pos).is(portalgun.LASER_BLOCK.get())) {
                level.setBlock(tmp_pos, Blocks.AIR.defaultBlockState(), 15);
                tmp_pos = tmp_pos.relative(direction);
                if (level.getBlockState(tmp_pos).is(portalgun.PORTAL_BLOCK.get())) {
                    if (direction != level.getBlockState(tmp_pos).getValue(PortalBlock.FACE).getOpposite()) break;
                    tmp_pos = ((PortalBlockBlockEntity) level.getBlockEntity(tmp_pos)).link_pos;
                    direction = level.getBlockState(tmp_pos).getValue(PortalBlock.FACE);
                    tmp_pos = tmp_pos.relative(direction);
                } else if (level.getBlockState(tmp_pos).is(portalgun.LASER_CATCHER.get())) {
                    level.setBlock(tmp_pos, level.getBlockState(tmp_pos).setValue(LaserCatcher.ACTIVE, false), 0);
                }
            }
        }
    }

    /*public void renderPortal() {
        Entity cameraEntity = minecraft.cameraEntity;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        ClientLevel newWorld = minecraft.level;
        int renderDistance = 2;

    }*/
}
