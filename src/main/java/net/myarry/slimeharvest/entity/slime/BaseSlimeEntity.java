package net.myarry.slimeharvest.entity.slime;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.myarry.slimeharvest.entity.ModEntities;
import net.myarry.slimeharvest.entity.slime.breeding.BreedingManager;
import net.myarry.slimeharvest.entity.slime.breeding.BreedingRecipe;

import java.util.EnumSet;
import java.util.List;


public class BaseSlimeEntity extends Animal   {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final int BREED_DELAY = 40; // 2 секунды (40 тиков = 20 тиков/сек * 2)
    private final int BREED_COOLDOWN = 6000; // 5 минут (20*60*5)
    public BaseSlimeEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new HybridSlimeControl(this);
    }
    public static class SimpleSlimeBreedGoal extends Goal {
        private final BaseSlimeEntity slime;
        private BaseSlimeEntity partner;
        private int breedTimer = 0;
        private final int BREED_DELAY = 60; // 2 секунды (40 тиков)
        private boolean hasBred = false; // Флаг, что размножение уже произошло

        public SimpleSlimeBreedGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Только если влюблён и взрослый И ещё не размножался
            return this.slime.isInLove() && !this.slime.isBaby() && !hasBred;
        }

        @Override
        public void start() {
            this.partner = null;
            this.breedTimer = 0;
            this.hasBred = false;
        }

        @Override
        public void stop() {
            this.partner = null;
            this.breedTimer = 0;
            this.hasBred = false;
        }

        @Override
        public void tick() {
            // Если уже размножились - выходим
            if (hasBred) {
                return;
            }

            // 1. Ищем партнёра если ещё нет
            if (this.partner == null || !this.partner.isInLove()) {
                this.findPartner();

                if (this.partner == null) {
                    return;
                }
            }

            // 2. Смотрим на партнёра
            this.slime.getLookControl().setLookAt(this.partner, 10.0F,
                    (float)this.slime.getMaxHeadXRot());

            // 3. Идём к партнёру
            double distance = this.slime.distanceToSqr(this.partner);

            if (distance > 9.0) { // Дальше 3 блоков
                // Двигаемся к партнёру
                this.slime.getNavigation().moveTo(this.partner, 2.0);
                this.breedTimer = 0; // Сбрасываем таймер если далеко
            } else {
                // Достаточно близко - останавливаемся и ждём
                this.slime.getNavigation().stop();
                this.breedTimer++;

                // 4. Проверяем прошло ли достаточно времени
                if (this.breedTimer >= BREED_DELAY) {
                    this.breed();
                }
            }
        }

        private void findPartner() {
            List<BaseSlimeEntity> list = this.slime.level()
                    .getEntitiesOfClass(BaseSlimeEntity.class,
                            this.slime.getBoundingBox().inflate(12.0), // 12 блоков радиус
                            this::isValidPartner);

            if (!list.isEmpty()) {
                // Берём ближайшего
                this.partner = list.get(0);
            }
        }

        private boolean isValidPartner(BaseSlimeEntity entity) {
            return entity != this.slime &&
                    entity.isInLove() &&
                    !entity.isBaby() &&
                    entity.getType() != this.slime.getType(); // Можно разные типы
        }

        private void breed() {
            // Проверяем, что партнёр ещё влюблён и ещё не размножался
            if (hasBred || this.partner == null || !this.partner.isInLove()) {
                this.stop();
                return;
            }

            // Устанавливаем флаг, что размножение началось
            hasBred = true;

            ServerLevel level = (ServerLevel)this.slime.level();

            // Вызываем стандартный метод размножения
            AgeableMob baby = this.slime.getBreedOffspring(level, this.partner);

            if (baby != null) {
                // Стандартная логика Animal.spawnChildFromBreeding
                this.slime.setAge(24000);
                this.partner.setAge(24000);
                this.slime.resetLove();
                this.partner.resetLove();

                // Помещаем детёныша между родителями
                baby.setBaby(true);
                double x = (this.slime.getX() + this.partner.getX()) / 2.0;
                double z = (this.slime.getZ() + this.partner.getZ()) / 2.0;
                baby.moveTo(x, this.slime.getY(), z, 0.0F, 0.0F);

                level.addFreshEntityWithPassengers(baby);
                level.broadcastEntityEvent(this.slime, (byte)18); // Частицы любви

                // Также можно вызвать событие для партнёра для визуального эффекта
                level.broadcastEntityEvent(this.partner, (byte)18);
            }

            this.stop();

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public String getSlimeType() {
        return null;
    }


    protected void registerCommonGoals() {


        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 4D));

        this.goalSelector.addGoal(2, new SimpleSlimeBreedGoal(this));

        // ПОНИЖЕННЫЙ ПРИОРИТЕТ для случайных прыжков:
        this.goalSelector.addGoal(4, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SLIME_BALL);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (otherParent instanceof BaseSlimeEntity otherSlime) {
            // Получаем типы
            String type1 = this.getSlimeType();
            String type2 = otherSlime.getSlimeType();

            // Ищем рецепт
            BreedingRecipe recipe = BreedingManager.findRecipe(type1, type2);

            if (recipe != null) {
                // Проверяем шанс
                if (level.random.nextFloat() <= recipe.chance()) {
                    System.out.println("Успешное скрещивание: " + type1 + " + " + type2 + " = " + recipe.result());
                    return createBabyByType(recipe.result(), level);
                } else {
                    System.out.println("Неудачное скрещивание, возвращаем первого родителя");
                    return createBabyByType(type1, level);
                }
            }
        }

        // Нет рецепта - такой же как первый родитель
        return createBabyByType(this.getSlimeType(), level);
    }

    // Простой маппинг тип -> сущность
    private AgeableMob createBabyByType(String type, ServerLevel level) {
        return switch (type) {
            case "natural" -> ModEntities.NATURAL_SLIME.get().create(level);
            case "miner" -> ModEntities.MINE_SLIME.get().create(level);
            case "coal" -> ModEntities.COAL_SLIME.get().create(level);
            default -> ModEntities.NATURAL_SLIME.get().create(level);
        };
    }

    public class SlimeKeepOnJumpingGoal extends Goal {
        private final BaseSlimeEntity slime;

        public SlimeKeepOnJumpingGoal(BaseSlimeEntity slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof BaseSlimeEntity.HybridSlimeControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(2.5F);
            }

        }
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 20;
    }

    public class HybridSlimeControl extends MoveControl {
        private final BaseSlimeEntity slime;
        private float targetYRot;
        private int jumpDelay;

        public HybridSlimeControl(BaseSlimeEntity slime) {
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

    public class SlimeRandomDirectionGoal extends Goal {
        private final BaseSlimeEntity slime;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(BaseSlimeEntity slime) {
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
