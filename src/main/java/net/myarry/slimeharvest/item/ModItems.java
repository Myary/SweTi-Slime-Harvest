package net.myarry.slimeharvest.item;

import net.minecraft.world.item.Item;
import net.myarry.slimeharvest.SlimeHarvest;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlimeHarvest.MOD_ID);


    public static final DeferredItem<Item>  NATURAL_SLIMORITE = ITEMS.register("natural_slimorite",
            () -> new Item(new Item.Properties()));




    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
