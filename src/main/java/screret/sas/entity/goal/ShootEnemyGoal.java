package screret.sas.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import screret.sas.entity.entity.WizardMob;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ShootEnemyGoal extends Goal {
    private final WizardMob wizard;
    @Nullable
    private LivingEntity target;
    private int attackTime = -1;
    private final double speedModifier;
    private int seeTime;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;

    private int useTime;

    public ShootEnemyGoal(WizardMob pRangedAttackMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius) {
        this.wizard = pRangedAttackMob;
        this.speedModifier = pSpeedModifier;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = pAttackRadius;
        this.attackRadiusSqr = pAttackRadius * pAttackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.wizard.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            this.target = livingentity;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive() && !this.wizard.getNavigation().isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.useTime = this.adjustedTickDelay(40 + this.wizard.getRandom().nextInt(40));
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }
    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        double distanceSqr = this.wizard.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean canSee = this.wizard.getSensing().hasLineOfSight(this.target);
        if (canSee) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(distanceSqr > (double)this.attackRadiusSqr) && this.seeTime >= 5) {
            this.wizard.getNavigation().stop();
        } else {
            this.wizard.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.wizard.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!canSee) {
                return;
            }

            float maxDistance = (float)Math.sqrt(distanceSqr) / this.attackRadius;
            float distanceFactor = Mth.clamp(maxDistance, 0.1F, 1.0F);
            this.wizard.performRangedAttack(this.target, distanceFactor);
            this.attackTime = Mth.floor(maxDistance * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSqr) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }

    }
}
