package screret.sas.entity.entity.boss.cthulhu.phases.phases;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import screret.sas.entity.entity.boss.cthulhu.phases.AbstractCthulhuPhaseInstance;
import screret.sas.entity.entity.boss.cthulhu.phases.CthulhuPhase;
import screret.sas.entity.entity.boss.cthulhu.phases.CthulhuPhaseInstance;

import javax.annotation.Nullable;

public class CthulhuDeathPhase extends AbstractCthulhuPhaseInstance {
    @Nullable
    private Vec3 targetLocation;
    private int time;

    public CthulhuDeathPhase(CthulhuEntity cthulhu) {
        super(cthulhu);
    }

    /**
     * Generates particle effects appropriate to the phase (or sometimes sounds).
     * Called by cthulhu's onLivingUpdate. Only used when worldObj.isRemote.
     */
    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            float f = (this.cthulhu.getRandom().nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.cthulhu.getRandom().nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.cthulhu.getRandom().nextFloat() - 0.5F) * 8.0F;
            this.cthulhu.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.cthulhu.getX() + (double)f, this.cthulhu.getY() + 2.0D + (double)f1, this.cthulhu.getZ() + (double)f2, 0.0D, 0.0D, 0.0D);
        }

    }

    /**
     * Gives the phase a chance to update its status.
     * Called by cthulhu's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void doServerTick() {
        ++this.time;
        if (this.targetLocation == null) {
            BlockPos blockpos = this.cthulhu.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
            this.targetLocation = Vec3.atBottomCenterOf(blockpos);
        }

        double d0 = this.targetLocation.distanceToSqr(this.cthulhu.getX(), this.cthulhu.getY(), this.cthulhu.getZ());
        if (!(d0 < 100.0D) && !(d0 > 22500.0D) && !this.cthulhu.horizontalCollision && !this.cthulhu.verticalCollision) {
            this.cthulhu.setHealth(1.0F);
        } else {
            this.cthulhu.setHealth(0.0F);
        }

    }

    /**
     * Called when this phase is set to active
     */
    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }

    /**
     * Returns the maximum amount cthulhu may rise or fall during this phase
     */
    public float getFlySpeed() {
        return 3.0F;
    }

    /**
     * Returns the location the cthulhu is flying toward
     */
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public CthulhuPhase<CthulhuDeathPhase> getPhase() {
        return CthulhuPhase.DYING;
    }
}
