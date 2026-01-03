package net.myarry.slimeharvest.event;


import net.myarry.slimeharvest.SlimeHarvest;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.entity.slime.coal.CoalSlime;
import net.myarry.slimeharvest.entity.slime.coal.CoalSlimeModel;
import net.myarry.slimeharvest.entity.slime.mine.MineSlime;
import net.myarry.slimeharvest.entity.slime.mine.MineSlimeModel;
import net.myarry.slimeharvest.entity.slime.natural.NaturalSlime;
import net.myarry.slimeharvest.entity.slime.natural.NaturalSlimeModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = SlimeHarvest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {


        event.registerLayerDefinition(NaturalSlimeModel.LAYER_LOCATION, NaturalSlimeModel::createBodyLayer);
        event.registerLayerDefinition(MineSlimeModel.LAYER_LOCATION, MineSlimeModel::createBodyLayer);
        event.registerLayerDefinition(CoalSlimeModel.LAYER_LOCATION, CoalSlimeModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(ModEntities.NATURAL_SLIME.get(), NaturalSlime.createAttributes().build());
        event.put(ModEntities.MINE_SLIME.get(), MineSlime.createAttributes().build());
        event.put(ModEntities.COAL_SLIME.get(), CoalSlime.createAttributes().build());

    }
}