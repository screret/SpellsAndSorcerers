package screret.sas.entity.entity.boss.cthulhu;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import screret.sas.api.capability.cthulhu.CthulhuFightProvider;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.boss.cthulhu.part.CthulhuPart;
import screret.sas.entity.entity.boss.cthulhu.phases.CthulhuPhase;
import screret.sas.entity.entity.boss.cthulhu.phases.CthulhuPhaseManager;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CthulhuEntity extends Mob implements Enemy, IAnimatable {
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (mob) -> mob.getMobType() != MobType.ILLAGER && mob.attackable();
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> INVULNERABLE_TICKS = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.INT);
    private static final int MAX_INVULNERABLE_TICKS = 75;

    private final CthulhuPart[] subEntities;
    public final CthulhuPart head;
    private final CthulhuPart neck;
    private final CthulhuPart body;
    private final CthulhuPart tail1;
    private final CthulhuPart tail2;
    private final CthulhuPart tail3;
    private final CthulhuPart wing1;
    private final CthulhuPart wing2;
    private final CthulhuPhaseManager phaseManager;
    @Nullable
    public EndCrystal nearestCrystal;
    @Nullable
    private final CthulhuFight cthulhuFight;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public CthulhuEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.head = new CthulhuPart(this, "head", 1.0F, 1.0F);
        this.neck = new CthulhuPart(this, "neck", 3.0F, 3.0F);
        this.body = new CthulhuPart(this, "body", 5.0F, 3.0F);
        this.tail1 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.tail2 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.tail3 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.wing1 = new CthulhuPart(this, "wing", 4.0F, 2.0F);
        this.wing2 = new CthulhuPart(this, "wing", 4.0F, 2.0F);
        this.subEntities = new CthulhuPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setHealth(this.getMaxHealth());
        this.noCulling = true;
        if (pLevel instanceof ServerLevel serverLevel) {
            this.cthulhuFight = serverLevel.getCapability(CthulhuFightProvider.CTHULHU_FIGHT).resolve().get().getCurrentFight();
        } else {
            this.cthulhuFight = null;
        }

        this.phaseManager = new CthulhuPhaseManager(this);
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1); // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
    }

    @Override
    public void setId(int pId) {
        super.setId(pId);
        for (int i = 0; i < this.subEntities.length; i++) // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            this.subEntities[i].setId(pId + i + 1);
    }

    public boolean hurt(CthulhuPart pPart, DamageSource pSource, float pDamage) {
        if (this.phaseManager.getCurrentPhase().getPhase() == CthulhuPhase.DYING) {
            return false;
        } else {
            pDamage = this.phaseManager.getCurrentPhase().onHurt(pSource, pDamage);
            if (pPart != this.head) {
                pDamage = pDamage / 4.0F + Math.min(pDamage, 1.0F);
            }

            if (pDamage < 0.01F) {
                return false;
            } else {
                if (pSource.getEntity() instanceof Player || pSource.isExplosion()) {
                    float f = this.getHealth();
                    this.reallyHurt(pSource, pDamage);
                    if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                        this.setHealth(1.0F);
                        this.phaseManager.setPhase(EnderDragonPhase.DYING);
                    }

                    if (this.phaseManager.getCurrentPhase().isSitting()) {
                        this.sittingDamageReceived = this.sittingDamageReceived + f - this.getHealth();
                        if (this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                            this.sittingDamageReceived = 0.0F;
                            this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                        }
                    }
                }

                return true;
            }
        }
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(INVULNERABLE_TICKS);
    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.entityData.set(INVULNERABLE_TICKS, pInvulnerableTicks);
    }

    public boolean isAttacking(){
        return this.entityData.get(IS_ATTACKING);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    protected boolean reallyHurt(DamageSource pDamageSource, float pAmount) {
        return super.hurt(pDamageSource, pAmount);
    }

    private PlayState predicate(AnimationEvent<CthulhuEntity> event) {
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
}
