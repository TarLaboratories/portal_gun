package com.portalgun.portalgun;

import com.ibm.icu.impl.number.Properties;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod(portalgun.MODID)
public class portalgun {
    public static final String MODID = "portalgun";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);


    public static final RegistryObject<EntityType<?>> WEIGHTED_CUBE_ENTITYTYPE = ENTITIES.register("weighted_storage_cube", () -> EntityType.Builder.of(WeightedCube::new, MobCategory.MISC).sized(0.75F, 0.75F).fireImmune().build("portalgun:weighted_storage_cube"));
    public static final RegistryObject<EntityType<?>> COMPANION_CUBE_ENTITYTYPE = ENTITIES.register("companion_cube", () -> EntityType.Builder.of(CompanionCube::new, MobCategory.MISC).sized(0.75F, 0.75F).fireImmune().build("portalgun:companion_cube"));

    public static final RegistryObject<Block> PORTAL_BLOCK = BLOCKS.register("portal_block", () -> new PortalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).destroyTime(-1)));
    public static final RegistryObject<Block> EMANCIPATION_GRID_EMITTER = BLOCKS.register("emancipation_grid_emitter", () -> new EmancipationGridEmitter(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission()));
    public static final RegistryObject<Block> EMANCIPATION_GRID_BLOCK = BLOCKS.register("emancipation_grid", () -> new EmancipationGridBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission().destroyTime(-1).noOcclusion().isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)));
    public static final RegistryObject<Block> APERTURESTONE_CABLE = BLOCKS.register("aperturestone_cable", () -> new ApertureStoneCable(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> APERTURESTONE_CABLE_ENCASED = BLOCKS.register("aperturestone_cable_encased", () -> new ApertureStoneFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> CREATIVE_APERTURESTONE_SOURCE = BLOCKS.register("creative_aperturestone_source", () -> new CreativeApertureStoneSource(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> APERTURESTONE_REDSTONE_SWITCH = BLOCKS.register("aperturestone_redstone_switch", () -> new ApertureStoneRedstoneSwitch(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> APERTURESTONE_LOGIC_GATE = BLOCKS.register("aperturestone_logic_gate", () -> new ApertureStoneLogicGate(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> APERTURESTONE_INDICATOR = BLOCKS.register("aperturestone_indicator", () -> new ApertureStoneIndicator(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> HARD_LIGHT_BRIDGE_EMITTER = BLOCKS.register("hard_light_bridge_emitter", () -> new HardLightBridgeEmitter(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE)));
    public static final RegistryObject<Block> HARD_LIGHT_BRIDGE = BLOCKS.register("hard_light_bridge", () -> new HardLightBridge(BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE)));
    public static final RegistryObject<Block> PRESSURE_BUTTON = BLOCKS.register("pressure_button", () -> new PressureButton(BlockBehaviour.Properties.of().mapColor(DyeColor.RED)));
    public static final RegistryObject<Block> WEIGHTED_STORAGE_CUBE_BLOCK = BLOCKS.register("weighted_storage_cube_block", () -> new WeightedStorageCubeBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> WEIGHTED_CUBE_DROPPER = BLOCKS.register("weighted_cube_dropper", () -> new WeightedCubeDropper(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE)));

    public static final RegistryObject<BlockEntityType<?>> PORTAL_BLOCK_BLOCKSTATE = BLOCK_ENTITIES.register("portal_block_blockentity", () -> BlockEntityType.Builder.of(PortalBlockBlockEntity::new, PORTAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<?>> CREATIVE_APERTURESTONE_SOURCE_BLOCKENTITY = BLOCK_ENTITIES.register("creative_aperturestone_source_blockentity", () -> BlockEntityType.Builder.of(CreativeApertureStoneSourceBlockEntity::new, CREATIVE_APERTURESTONE_SOURCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<?>> APERTURESTONE_LOGIC_GATE_BLOCKENTITY = BLOCK_ENTITIES.register("aperturestone_logic_gate", () -> BlockEntityType.Builder.of(ApertureStoneLogicGateBlockEntity::new, APERTURESTONE_LOGIC_GATE.get()).build(null));

    public static final RegistryObject<Item> PORTAL_GUN_ITEM = ITEMS.register("portal_gun", () -> new PortalGunItem(new Item.Properties()));
    public static final RegistryObject<Item> EMANCIPATION_GRID_EMITTER_ITEM = ITEMS.register("emancipation_grid_emitter", () -> new BlockItem(EMANCIPATION_GRID_EMITTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> APERTURESTONE_CABLE_ITEM = ITEMS.register("aperturestone_cable", () -> new BlockItem(APERTURESTONE_CABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> APERTURESTONE_CABLE_ENCASED_ITEM = ITEMS.register("aperturestone_cable_encased", () -> new BlockItem(APERTURESTONE_CABLE_ENCASED.get(), new Item.Properties()));
    public static final RegistryObject<Item> CREATIVE_APERTURESTONE_SOURCE_ITEM = ITEMS.register("creative_aperturestone_source", () -> new BlockItem(CREATIVE_APERTURESTONE_SOURCE.get(), new Item.Properties()));
    public static final RegistryObject<Item> APERTURESTONE_REDSTONE_SWITCH_ITEM = ITEMS.register("aperturestone_redstone_switch", () -> new BlockItem(APERTURESTONE_REDSTONE_SWITCH.get(), new Item.Properties()));
    public static final RegistryObject<Item> APERTURESTONE_LOGIC_GATE_ITEM = ITEMS.register("aperturestone_logic_gate", () -> new BlockItem(APERTURESTONE_LOGIC_GATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> APERTURESTONE_INDICATOR_ITEM = ITEMS.register("aperturestone_indicator", () -> new BlockItem(APERTURESTONE_INDICATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> HARD_LIGHT_BRIDGE_EMITTER_ITEM = ITEMS.register("hard_light_bridge_emitter", () -> new BlockItem(HARD_LIGHT_BRIDGE_EMITTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> PRESSURE_BUTTON_ITEM = ITEMS.register("pressure_button", () -> new BlockItem(PRESSURE_BUTTON.get(), new Item.Properties()));
    public static final RegistryObject<Item> WEIGHTED_CUBE_DROPPER_ITEM = ITEMS.register("weighted_cube_dropper", () -> new BlockItem(WEIGHTED_CUBE_DROPPER.get(), new Item.Properties()));
    
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("portalgun_mod_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> PORTAL_GUN_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(PORTAL_GUN_ITEM.get());
                output.accept(EMANCIPATION_GRID_EMITTER_ITEM.get());
                output.accept(APERTURESTONE_CABLE_ITEM.get());
                output.accept(CREATIVE_APERTURESTONE_SOURCE_ITEM.get());
                output.accept(APERTURESTONE_REDSTONE_SWITCH_ITEM.get());
                output.accept(APERTURESTONE_LOGIC_GATE_ITEM.get());
                output.accept(APERTURESTONE_CABLE_ENCASED_ITEM.get());
                output.accept(APERTURESTONE_INDICATOR_ITEM.get());
                output.accept(HARD_LIGHT_BRIDGE_EMITTER_ITEM.get());
                output.accept(PRESSURE_BUTTON_ITEM.get());
                output.accept(WEIGHTED_CUBE_DROPPER_ITEM.get());
                //output.accept(PORTAL_BLOCK_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).title(Component.translatable("Portal Gun Mod"))
            .build());
    
    public portalgun() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PortalGunItem.class);

        //modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    //private void addCreative(BuildCreativeModeTabContentsEvent event) {
    //    if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(PORTAL_BLOCK_ITEM);
    //}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SuppressWarnings("deprecation")
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(portalgun.EMANCIPATION_GRID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(portalgun.EMANCIPATION_GRID_EMITTER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(portalgun.APERTURESTONE_LOGIC_GATE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(portalgun.HARD_LIGHT_BRIDGE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(portalgun.HARD_LIGHT_BRIDGE_EMITTER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(portalgun.WEIGHTED_CUBE_DROPPER.get(), RenderType.translucent());
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            BlockEntityType<PortalBlockBlockEntity> tmp = (BlockEntityType<PortalBlockBlockEntity>) PORTAL_BLOCK_BLOCKSTATE.get();
            event.registerBlockEntityRenderer(tmp, PortalBlockBlockEntityRenderer::new);
            event.registerEntityRenderer(((EntityType<WeightedCube>) WEIGHTED_CUBE_ENTITYTYPE.get()), WeightedCubeRenderer::new);
            event.registerEntityRenderer(((EntityType<CompanionCube>) COMPANION_CUBE_ENTITYTYPE.get()), CompanionCubeRender::new);
        }
    }
}
