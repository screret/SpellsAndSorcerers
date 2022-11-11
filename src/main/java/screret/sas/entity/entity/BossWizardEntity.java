package screret.sas.entity.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemStackHandler;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BossWizardEntity extends Monster implements RangedAttackMob, IAnimatable {
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.STRING);
    private static final int INVULNERABLE_TICKS = 220;

    protected int spellCastingTickCount;
    private ResourceLocation currentSpell = ModWandAbilities.DUMMY.getId();

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);


    private final float attackRadius = 64, attackRadiusSqr = attackRadius * attackRadius;

    public BossWizardEntity(EntityType<BossWizardEntity> type, Level pLevel) {
        super(type, pLevel);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(true);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }


    public boolean isAttacking(){
        return this.entityData.get(IS_ATTACKING);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, .5D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.MAX_HEALTH, 200D).add(Attributes.ARMOR, 7.5D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(DATA_ID_INV, 0);
        this.entityData.define(DATA_SPELL_CASTING_ID, ModWandAbilities.SHOOT_RAY.getId().toString());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(100, new WizardCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new WizardSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            int ticks = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float)ticks / INVULNERABLE_TICKS);
            if (ticks <= 0) {
                Explosion.BlockInteraction explosion = ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion);
                if (!this.isSilent()) {
                    this.level.globalLevelEvent(LevelEvent.SOUND_WITHER_BOSS_SPAWN, this.blockPosition(), 0);
                }
            }

            this.setInvulnerableTicks(ticks);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.customServerAiStep();

            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (pSource != DamageSource.DROWN && !(pSource.getEntity() instanceof WitherBoss)) {
            if (this.getInvulnerableTicks() > 0 && pSource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity1 = pSource.getEntity();
                if (!(entity1 instanceof Player) && entity1 instanceof LivingEntity && ((LivingEntity) entity1).getMobType() == this.getMobType()) {
                    return false;
                } else {
                    return super.hurt(pSource, pAmount);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Invul", this.getInvulnerableTicks());
        pCompound.putString("CurrentSpell", this.currentSpell.toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setInvulnerableTicks(pCompound.getInt("Invul"));
        currentSpell = new ResourceLocation(pCompound.getString("CurrentSpell"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }

    }

    public int getInvulnerableTicks() {
        return this.entityData.get(DATA_ID_INV);
    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.entityData.set(DATA_ID_INV, pInvulnerableTicks);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(220);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return !this.entityData.get(DATA_SPELL_CASTING_ID).equals(ModWandAbilities.DUMMY.getId().toString());
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setIsCastingSpell(WandAbilityInstance pCurrentSpell) {
        this.currentSpell = pCurrentSpell.getId();
        this.entityData.set(DATA_SPELL_CASTING_ID, pCurrentSpell.getId().toString());
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.GHAST_SCREAM;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    private PlayState predicate(AnimationEvent<BossWizardEntity> event) {
        if(event.getAnimatable().isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boss_wizard.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        } else if(event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boss_wizard.walk", ILoopType.EDefaultLoopTypes.LOOP));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boss_wizard.idle", ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        if(Util.getMainAbilityFromStack(this.getMainHandItem()).isPresent()){
            Util.getMainAbilityFromStack(this.getMainHandItem()).get().execute(this.level, this, this.getMainHandItem(), new WandAbilityInstance.Vec3Wrapped(this.getEyePosition()), 50);
        }
    }

    protected class WizardCastingSpellGoal extends Goal {
        public WizardCastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return BossWizardEntity.this.getSpellCastingTime() > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            super.start();
            BossWizardEntity.this.navigation.stop();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            BossWizardEntity.this.setIsCastingSpell(new WandAbilityInstance(ModWandAbilities.DUMMY.get()));
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (BossWizardEntity.this.getTarget() != null) {
                BossWizardEntity.this.getLookControl().setLookAt(BossWizardEntity.this.getTarget(), (float)BossWizardEntity.this.getMaxHeadYRot(), (float)BossWizardEntity.this.getMaxHeadXRot());
            }

        }
    }

    public class WizardSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            LivingEntity livingentity = BossWizardEntity.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (BossWizardEntity.this.isCastingSpell()) {
                    return false;
                } else {
                    return BossWizardEntity.this.tickCount >= this.nextAttackTickCount && Util.getMainAbilityFromStack(BossWizardEntity.this.getMainHandItem()).isPresent();
                }
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            LivingEntity livingentity = BossWizardEntity.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            BossWizardEntity.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = BossWizardEntity.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                BossWizardEntity.this.playSound(soundevent, 1.0F, 1.0F);
            }


            BossWizardEntity.this.lookAt(BossWizardEntity.this.getTarget(), BossWizardEntity.this.getMaxHeadYRot(), BossWizardEntity.this.getMaxHeadXRot());
            BossWizardEntity.this.setIsAttacking(true);
            BossWizardEntity.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void stop() {
            super.stop();
            BossWizardEntity.this.setIsAttacking(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                BossWizardEntity.this.playSound(BossWizardEntity.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected void performSpellCasting(){
            var target = BossWizardEntity.this.getTarget();
            double distanceSqr = BossWizardEntity.this.distanceToSqr(target);

            float maxDistance = (float)Math.sqrt(distanceSqr) / BossWizardEntity.this.attackRadiusSqr;
            float distanceFactor = Mth.clamp(maxDistance, 0.1F, 1.0F);
            BossWizardEntity.this.performRangedAttack(target, distanceFactor);
        }

        protected int getCastWarmupTime() {
            return 20;
        }

        protected int getCastingTime(){
            return 10;
        }

        protected int getCastingInterval(){
            return 50;
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound(){
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected WandAbilityInstance getSpell(){
            return new WandAbilityInstance(WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(BossWizardEntity.this.currentSpell));
        }

    }
}
