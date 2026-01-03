package net.myarry.slimeharvest.entity.slime.coal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.entity.slime.BaseSlimeEntity;
import net.myarry.slimeharvest.item.ModItems;
import org.jetbrains.annotations.Nullable;




public class CoalSlime extends BaseSlimeEntity {


    public CoalSlime(EntityType<? extends BaseSlimeEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new HybridSlimeControl(this);
    }

    @Override
    public String getSlimeType() {

         return "coal";
    }

    @Override
    protected void registerGoals() {
        super.registerCommonGoals();

        this.goalSelector.addGoal(2, new TemptGoal(this, 3,
                stack -> stack.is(ModItems.COAL_SLIMORITE), false));
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
