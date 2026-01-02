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
import net.minecraft.world.level.Level;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;


public class NaturalSlime extends Animal   {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public NaturalSlime(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new HybridSlimeControl(this);
    }


    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25,
                stack -> stack.is(ModItems.NATURAL_SLIMORITE), false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));

        // ПОНИЖЕННЫЙ ПРИОРИТЕТ для случайных прыжков:
        this.goalSelector.addGoal(6, new SlimeRandomDirectionGoal(this)); // было 4
        this.goalSelector.addGoal(7, new SlimeKeepOnJumpingGoal(this));   // было 5

        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6D)
                .add(Attributes.MOVEMENT_SPEED, 1D)
                .add(Attributes.FOLLOW_RANGE, 12D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.NATURAL_SLIMORITE.get());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.NATURAL_SLIME.get().create(level);
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final NaturalSlime slime;

        public SlimeKeepOnJumpingGoal(NaturalSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof NaturalSlime.HybridSlimeControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(2F);
            }

        }
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 20;
    }

    static class HybridSlimeControl extends MoveControl {
        private final NaturalSlime slime;
        private float targetYRot;
        private int jumpDelay;

        public HybridSlimeControl(NaturalSlime slime) {
            super(slime);
            this.slime = slime;
            this.targetYRot = slime.getYRot();
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }
        @Override
        public void setWantedPosition(double x, double y, double z, double speed) {
            super.setWantedPosition(x, y, z, speed); // Важно! TemptGoal вызывает этот метод

            // Вычисляем направление к цели
            double dx = x - this.mob.getX();
            double dz = z - this.mob.getZ();
            this.targetYRot = (float)(Math.atan2(dz, dx) * 57.2957763671875) - 90.0F;
        }

        @Override
        public void tick() {
            // 1. ПОВОРОТ как у кролика
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.targetYRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();

            // 2. ПРЫЖКИ как у слайма (только когда есть цель)
            if (this.operation == Operation.MOVE_TO && this.mob.onGround()) {
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    this.slime.getJumpControl().jump(); // Прыжок слайма
                    this.mob.setSpeed((float)(this.speedModifier * 0.25F));
                } else {
                    // Пауза между прыжками
                    this.mob.setSpeed(0.0F);
                }
            } else {
                // Нет цели движения - стоим
                this.mob.setSpeed(0.0F);
            }
        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final NaturalSlime slime;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(NaturalSlime slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Работает только когда нет других активных целей движения
            return this.slime.getTarget() == null && this.slime.onGround();
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.slime.getRandom().nextInt(60);
                float chosenDegrees = (float) this.slime.getRandom().nextInt(360);

                // Устанавливаем случайное направление через MoveControl
                double x = this.slime.getX() + Math.sin(chosenDegrees * 0.017453292F) * 5.0;
                double z = this.slime.getZ() + Math.cos(chosenDegrees * 0.017453292F) * 5.0;

                this.slime.getMoveControl().setWantedPosition(x, this.slime.getY(), z, 0.5);
            }
        }
    }




    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 39;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }
}
