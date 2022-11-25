package screret.sas.entity.entity.boss.cthulhu.phases.phases;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.endercthulhu.EndCrystal;
import net.minecraft.world.entity.boss.endercthulhu.Endercthulhu;
import net.minecraft.world.entity.boss.endercthulhu.phases.cthulhuHoldingPatternPhase;
import net.minecraft.world.entity.boss.endercthulhu.phases.cthulhuPhaseInstance;
import net.minecraft.world.entity.boss.endercthulhu.phases.EndercthulhuPhase;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import screret.sas.entity.entity.boss.cthulhu.phases.AbstractCthulhuPhaseInstance;
import screret.sas.entity.entity.boss.cthulhu.phases.CthulhuPhase;

import javax.annotation.Nullable;

public class CthulhuHoldingPatternPhase extends AbstractCthulhuPhaseInstance {
    private static final TargetingConditions NEW_TARGET_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
    @Nullable
    private Path currentPath;
    @Nullable
    private Vec3 targetLocation;
    private boolean clockwise;

    public CthulhuHoldingPatternPhase(CthulhuEntity cthulhu) {
        super(cthulhu);
    }

    public CthulhuPhase<CthulhuHoldingPatternPhase> getPhase() {
        return CthulhuPhase.HOLDING_PATTERN;
    }

    /**
     * Gives the phase a chance to update its status.
     * Called by cthulhu's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void doServerTick() {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.cthulhu.getX(), this.cthulhu.getY(), this.cthulhu.getZ());
        if (d0 < 100.0D || d0 > 22500.0D || this.cthulhu.horizontalCollision || this.cthulhu.verticalCollision) {
            this.findNewTarget();
        }

    }

    /**
     * Called when this phase is set to active
     */
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    /**
     * Returns the location the cthulhu is flying toward
     */
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (this.currentPath != null && this.currentPath.isDone()) {
            BlockPos blockpos = this.cthulhu.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
            int i = this.cthulhu.getcthulhuFight() == null ? 0 : this.cthulhu.getcthulhuFight().getCrystalsAlive();
            if (this.cthulhu.getRandom().nextInt(i + 3) == 0) {
                this.cthulhu.getPhaseManager().setPhase(EndercthulhuPhase.LANDING_APPROACH);
                return;
            }

            double d0 = 64.0D;
            Player player = this.cthulhu.level.getNearestPlayer(NEW_TARGET_TARGETING, this.cthulhu, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            if (player != null) {
                d0 = blockpos.distToCenterSqr(player.position()) / 512.0D;
            }

            if (player != null && (this.cthulhu.getRandom().nextInt(Mth.abs((int)d0) + 2) == 0 || this.cthulhu.getRandom().nextInt(i + 2) == 0)) {
                this.strafePlayer(player);
                return;
            }
        }

        if (this.currentPath == null || this.currentPath.isDone()) {
            int j = this.cthulhu.findClosestNode();
            int k = j;
            if (this.cthulhu.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
                k = j + 6;
            }

            if (this.clockwise) {
                ++k;
            } else {
                --k;
            }

            if (this.cthulhu.getcthulhuFight() != null && this.cthulhu.getcthulhuFight().getCrystalsAlive() >= 0) {
                k %= 12;
                if (k < 0) {
                    k += 12;
                }
            } else {
                k -= 12;
                k &= 7;
                k += 12;
            }

            this.currentPath = this.cthulhu.findPath(j, k, (Node)null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }

        this.navigateToNextPathNode();
    }

    private void strafePlayer(Player pPlayer) {
        this.cthulhu.getPhaseManager().setPhase(EndercthulhuPhase.STRAFE_PLAYER);
        this.cthulhu.getPhaseManager().getPhase(EndercthulhuPhase.STRAFE_PLAYER).setTarget(pPlayer);
    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            Vec3i vec3i = this.currentPath.getNextNodePos();
            this.currentPath.advance();
            double d0 = (double)vec3i.getX();
            double d1 = (double)vec3i.getZ();

            double d2;
            do {
                d2 = (double)((float)vec3i.getY() + this.cthulhu.getRandom().nextFloat() * 20.0F);
            } while(d2 < (double)vec3i.getY());

            this.targetLocation = new Vec3(d0, d2, d1);
        }

    }

    public void onCrystalDestroyed(EndCrystal pCrystal, BlockPos pPos, DamageSource pDmgSrc, @Nullable Player pPlyr) {
        if (pPlyr != null && this.cthulhu.canAttack(pPlyr)) {
            this.strafePlayer(pPlyr);
        }

    }
}
