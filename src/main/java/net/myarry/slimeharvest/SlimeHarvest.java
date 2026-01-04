package net.myarry.slimeharvest;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.myarry.slimeharvest.block.ModBlocks;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.entity.slime.breeding.BreedingManager;
import net.myarry.slimeharvest.entity.slime.coal.CoalSlimeRenderer;
import net.myarry.slimeharvest.entity.slime.mine.MineSlimeRenderer;
import net.myarry.slimeharvest.entity.slime.natural.NaturalSlimeRenderer;
import net.myarry.slimeharvest.item.ModCreativeModeTabs;
import net.myarry.slimeharvest.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(SlimeHarvest.MOD_ID)
public class SlimeHarvest {
    public static final String MOD_ID = "slimeharvest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SlimeHarvest(IEventBus modEventBus, ModContainer modContainer) {



        NeoForge.EVENT_BUS.register(this);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.register(modEventBus);


        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        // Загружаем рецепты при старте сервера
        BreedingManager.init();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = SlimeHarvest.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.NATURAL_SLIME.get(), NaturalSlimeRenderer::new);
            EntityRenderers.register(ModEntities.MINE_SLIME.get(), MineSlimeRenderer::new);
            EntityRenderers.register(ModEntities.COAL_SLIME.get(), CoalSlimeRenderer::new);

        }
    }
}
