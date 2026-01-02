package net.myarry.slimeharvest.datagen;

import net.minecraft.data.PackOutput;
import net.myarry.slimeharvest.SlimeHarvest;
import net.myarry.slimeharvest.item.ModItems;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SlimeHarvest.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.NATURAL_SLIMORITE.get());


    }
}