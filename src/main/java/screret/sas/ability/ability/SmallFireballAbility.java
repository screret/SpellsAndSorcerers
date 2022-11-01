package screret.sas.ability.ability;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import screret.sas.Util;

public class SmallFireballAbility extends ProjectileAbility {
    public SmallFireballAbility() {
        super(0, 20, true, 512);
    }

    @Override
    public Projectile spawnProjectile(Level level, LivingEntity user) {
        var lookAngle = user.getLookAngle();
        var distanceSqr = distance * distance;
        var hitResult = Util.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);

        var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
        var distanceToEndSqrtSqrtHalf = Math.sqrt(Math.sqrt(userPos.distanceTo(hitResult.getLocation()))) * 0.5D;
        return new SmallFireball(level, user, level.getRandom().triangle(lookAngle.x, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtSqrtHalf), hitResult.getLocation().y * 0.5 - userPos.y * 0.5, level.getRandom().triangle(lookAngle.z, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtSqrtHalf));
    }
}
