package screret.sas.entity.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbilityInstance;
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

import java.util.Timer;

public class WizardMob extends SpellcasterIllager implements RangedAttackMob, IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private boolean isAttacking;

    public WizardMob(EntityType<WizardMob> type, Level pLevel) {
        super(type, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, (double) 0.1F).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).add(Attributes.ATTACK_KNOCKBACK).add(net.minecraftforge.common.ForgeMod.ATTACK_RANGE.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ShootEnemyGoal(this, 0.1f, 10, 100, 8*8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(6, new Evoker.EvokerWololoSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    private <E extends IAnimatable> PlayState predicateIdle(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.wizard.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState predicateAttack(AnimationEvent<WizardMob> event) {
        if(event.getAnimatable().isAttacking)
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.wizard.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicateIdle));
        data.addAnimationController(new AnimationController<>(this, "controller_attack", 0, this::predicateAttack));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        if(Util.getMainAbilityFromStack(this.getMainHandItem()).isPresent()){
            this.isAttacking = true;
            Util.getMainAbilityFromStack(this.getMainHandItem()).get().execute(this.level, this, this.getMainHandItem(), new WandAbilityInstance.Vec3Wrapped(this.getEyePosition()), 50);
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.GHAST_SCREAM;
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level.isClientSide) {
            if(this.isAttacking && this.tickCount % 60 == 0){
                this.isAttacking = false;
            }
        }
    }

    public class WizardSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        protected void performSpellCasting() {

        }

        @Override
        protected int getCastingTime() {
            return 0;
        }

        @Override
        protected int getCastingInterval() {
            return 0;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected IllagerSpell getSpell() {
            return null;
        }
    }
}
