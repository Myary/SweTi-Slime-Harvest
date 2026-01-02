package net.myarry.slimeharvest.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.myarry.slimeharvest.SlimeHarvest;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlimeHarvest.MOD_ID);

    public static final Supplier<CreativeModeTab> SLIME_HARVEST = CREATIVE_MODE_TAB.register("slime_harvest",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.NATURAL_SLIMORITE.get()))
                    .title(Component.translatable("creativetab.slimeharvest.slimeharvest"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.NATURAL_SLIMORITE.get());


                    }).build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}