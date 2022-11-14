package screret.sas.entity.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.event.ForgeEventFactory;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.blockentity.blockentity.SummonSignBE;
import screret.sas.config.SASConfig;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.entity.goal.ShootEnemyGoal;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.function.Predicate;

public class BossWizardEntity extends Monster implements RangedAttackMob, IAnimatable {
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (mob) -> mob.getMobType() != MobType.ILLAGER && mob.attackable();
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.INT);
    private static final int INVULNERABLE_TICKS = 75;
    public static final WandAbilityInstance DUMMY_SPELL = new WandAbilityInstance(ModWandAbilities.DUMMY.get());


    protected int spellCastingTickCount;
    private WandAbilityInstance currentSpell = DUMMY_SPELL;
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
    private BlockPos spawnPos;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public BossWizardEntity(EntityType<BossWizardEntity> type, Level pLevel) {
        super(type, pLevel);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setHealth(this.getMaxHealth());
        this.setItemSlot(EquipmentSlot.MAINHAND, createBossWand());
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

    public void setSpawningPosition(BlockPos pos){
        this.spawnPos = pos;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ARMOR, 7.5D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(DATA_ID_INV, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BossWizardEntity.WizardDoNothingGoal());
        this.goalSelector.addGoal(2, new ShootEnemyGoal(this, 1.0D, 100, 32.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            if(!(this.level.getBlockEntity(this.spawnPos) instanceof SummonSignBE)){
                this.discard();
            }
            int ticks = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float)ticks / INVULNERABLE_TICKS);
            if (ticks <= 0) {
                //Explosion.BlockInteraction explosion = ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                //this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion);
                this.level.setBlockAndUpdate(this.spawnPos, Blocks.AIR.defaultBlockState());
                this.setInvulnerable(false);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, createBossWand());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (pSource != DamageSource.DROWN && !(pSource.getEntity() instanceof BossWizardEntity)) {
            if (this.getInvulnerableTicks() > 0 && pSource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity1 = pSource.getEntity();
                if (!(entity1 instanceof Player) && entity1 instanceof LivingEntity living && living.getMobType() == this.getMobType()) {
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
    public MobType getMobType() {
        return MobType.ILLAGER;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot pSlot) {
        return 0.0F;
    }

    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(SASConfig.Server.dropWandCores.get()){
            var toDrop = Util.getMainAbilityFromStack(this.getMainHandItem()).get();
            while (toDrop.getChildren() != null && toDrop.getChildren().size() > 0) {
                toDrop = toDrop.getChildren().get(0);
            }
            ItemEntity itementity = this.spawnAtLocation(Util.customWandCores.get(toDrop.getId()).copy());
            if (itementity != null) {
                itementity.setExtendedLifetime();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("InvulTime", this.getInvulnerableTicks());
        pCompound.put("CurrentSpell", this.currentSpell.serializeNBT());
        pCompound.put("SpawnPos", this.newIntList(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setInvulnerableTicks(pCompound.getInt("InvulTime"));
        currentSpell = new WandAbilityInstance(pCompound.getCompound("CurrentSpell"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        var spawnPosTag = pCompound.getList("SpawnPos", Tag.TAG_INT);
        this.spawnPos = new BlockPos(spawnPosTag.getInt(0), spawnPosTag.getInt(1), spawnPosTag.getInt(2));
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(DATA_ID_INV);
    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.entityData.set(DATA_ID_INV, pInvulnerableTicks);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(INVULNERABLE_TICKS);
        this.setInvulnerable(true);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return currentSpell != DUMMY_SPELL;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setCastingSpell(WandAbilityInstance pCurrentSpell) {
        this.currentSpell = pCurrentSpell;
        this.setIsAttacking(true);
    }

    public SoundEvent getCastingSound() {
        return SoundEvents.GHAST_SCREAM;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    private PlayState predicate(AnimationEvent<BossWizardEntity> event) {
        if(event.getAnimatable().getInvulnerableTicks() > 0){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boss_wizard.spawn", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        } else if(event.getAnimatable().isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boss_wizard.attack", ILoopType.EDefaultLoopTypes.LOOP));
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
        currentSpell.execute(this.level, this, this.getMainHandItem(), new WandAbilityInstance.Vec3Wrapped(this.getEyePosition()), 50);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }


    private static ItemStack createBossWand(){
        var wandItem = Util.createWand(ModWandAbilities.LARGE_FIREBALL.get(), ModWandAbilities.HEAL_SELF.get());
        wandItem.enchant(ModEnchantments.POWER.get(), 5);
        wandItem.enchant(ModEnchantments.QUICK_CHARGE.get(), 3);
        return wandItem;
    }

    protected ListTag newIntList(int... pNumbers) {
        ListTag listtag = new ListTag();

        for(int number : pNumbers) {
            listtag.add(IntTag.valueOf(number));
        }

        return listtag;
    }

    class WizardDoNothingGoal extends Goal {
        public WizardDoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return BossWizardEntity.this.getInvulnerableTicks() > 0;
        }
    }
}
