package net.myarry.slimeharvest.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.myarry.slimeharvest.SlimeHarvest;
import net.myarry.slimeharvest.entity.slime.natural.NaturalSlime;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SlimeHarvest.MOD_ID);

    public static final Supplier<EntityType<NaturalSlime>> NATURAL_SLIME =
            ENTITY_TYPES.register("na_slime", () -> EntityType.Builder.of(NaturalSlime::new, MobCategory.CREATURE)
                    .sized(0.7f, 0.7f).build("na_slime"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
