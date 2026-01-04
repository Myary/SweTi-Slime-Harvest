package net.myarry.slimeharvest.entity.slime.natural;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.entity.slime.BaseSlimeEntity;
import net.myarry.slimeharvest.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;


public class NaturalSlime extends BaseSlimeEntity {

    public NaturalSlime(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new HybridSlimeControl(this);
    }

    @Override
    public String getSlimeType() {
        return "natural";
    }

    @Override
    protected void registerGoals() {
        super.registerCommonGoals();




    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6D)
                .add(Attributes.FOLLOW_RANGE, 12D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SLIME_BALL);
    }

}
