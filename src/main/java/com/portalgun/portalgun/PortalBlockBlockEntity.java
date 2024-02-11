package com.portalgun.portalgun;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
        this.level.setBlockAndUpdate(this.worldPosition, this.replaced_block_blockstate);
        if (serverlevel != null) ForgeChunkManager.forceChunk(serverlevel, "portalgun", this.worldPosition, (int) chunk, (int) (chunk >> 32), false, false);
    }
}
