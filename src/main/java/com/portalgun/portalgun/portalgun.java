package com.portalgun.portalgun;

import com.ibm.icu.impl.number.Properties;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
public class portalgun
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "portalgun";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Create a Deferred Register to hold Blocks which will all be registered under the "portalgun" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "portalgun" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "portalgun" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    
    // Creates a new Block with the id "portalgun:example_block", combining the namespace and path
    public static final RegistryObject<Block> PORTAL_BLOCK = BLOCKS.register("portal_block", () -> new PortalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).destroyTime(-1)));
    public static final RegistryObject<Block> EMANCIPATION_GRID_EMITTER = BLOCKS.register("emancipation_grid_emitter", () -> new EmancipationGridEmitter(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission()));
    public static final RegistryObject<Block> EMANCIPATION_GRID_BLOCK = BLOCKS.register("emancipation_grid", () -> new EmancipationGridBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission().destroyTime(-1)));
    //public static final RegistryObject<Block> APERTURESTONE_WIRE = BLOCKS.register("aperturestone_wire", () -> new ApertureStoneWire(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noCollission()));
    //static Block validBlocks = null;
    public static final RegistryObject<BlockEntityType<?>> PORTAL_BLOCK_BLOCKSTATE = BLOCK_ENTITIES.register("portal_block_blockentity", () -> BlockEntityType.Builder.of(PortalBlockBlockEntity::new, PORTAL_BLOCK.get()).build(null));
    // Creates a new BlockItem with the id "portalgun:example_block", combining the namespace and path
    //public static final RegistryObject<Item> PORTAL_BLOCK_ITEM = ITEMS.register("portal_block", () -> new BlockItem(PORTAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> PORTAL_GUN_ITEM = ITEMS.register("portal_gun", () -> new PortalGunItem(new Item.Properties()));
    public static final RegistryObject<Item> EMANCIPATION_GRID_EMITTER_ITEM = ITEMS.register("emancipation_grid_emitter", () -> new BlockItem(EMANCIPATION_GRID_EMITTER.get(), new Item.Properties()));
    //public static final RegistryObject<Item> APERTURESTONE_WIRE_ITEM = ITEMS.register("aperturestone_wire", () -> new BlockItem(APERTURESTONE_WIRE.get(), new Item.Properties()));
    //public static final RegistryObject<Item> INFINITE_REACH_ITEM = ITEMS.register("infinite_reach_item", () -> new Item(new Item.Properties()));
    
    // Creates a creative tab with the id "portalgun:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> PORTAL_GUN_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(PORTAL_GUN_ITEM.get());
                output.accept(EMANCIPATION_GRID_EMITTER_ITEM.get());
                //output.accept(APERTURESTONE_WIRE_ITEM.get());
                //output.accept(PORTAL_BLOCK_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).title(Component.translatable("Portal Gun Mod"))
            .build());
    
    //EntityType<?> PORTAL_ENTITY = EntityType.Builder.of(PortalEntity::new, MobCategory.MISC).sized(1.0F, 2.0F).fireImmune().updateInterval(1).build("portalgun:portal");
    /*@SubscribeEvent
    public void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS,
            helper -> {
                helper.register(new ResourceLocation(MODID, "example_block"), new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
                //helper.register(new ResourceLocation(MODID, "example_block_2"), new Block(...));
                //helper.register(new ResourceLocation(MODID, "example_block_3"), new Block(...));
                // ...
            }
        );
        event.register(ForgeRegistries.Keys.ITEMS,
            helper -> {
                helper.register(new ResourceLocation(MODID, "example_block"), new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
                helper.register(new ResourceLocation(MODID, "example_item"), new Item(new Item.Properties().food(new FoodProperties.Builder()
                .alwaysEat().nutrition(1).saturationMod(2f).build())));
            }
        );
        /*event.register(ForgeRegistries.Keys.CREATIVE_MODE_TABS,
            helper -> {
                helper.register(new ResourceLocation(MODID, "example_tab"), CreativeModeTab.builder())
            }
        );
    }*/
    
    public portalgun()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PortalGunItem.class);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        /*LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));*/
    }

    // Add the example block item to the building blocks tab
    //private void addCreative(BuildCreativeModeTabContentsEvent event)
    //{
    //    if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
    //        event.accept(PORTAL_BLOCK_ITEM);
    //}


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            @SuppressWarnings("unchecked")
            BlockEntityType<PortalBlockBlockEntity> tmp = (BlockEntityType<PortalBlockBlockEntity>) PORTAL_BLOCK_BLOCKSTATE.get();
            event.registerBlockEntityRenderer(tmp, PortalBlockBlockEntityRenderer::new);
        }
    }
}
