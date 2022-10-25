package screret.sas.item.wand.type.advanced;

import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import screret.sas.item.wand.Wand;
import screret.sas.item.wand.power.IIsExplosive;
import screret.sas.item.wand.power.IIsChargeable;
import screret.sas.item.wand.power.IIsRaycaster;

public class ExplosionWand extends Wand implements IIsChargeable, IIsExplosive, IIsRaycaster {
    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xAA0000)), 1.0F);

    private static final Vec3 RANDOM_DEVIATION = new Vec3(0.25D, 0.25D, 0.25D);

    public ExplosionWand() {
        super(200, Rarity.UNCOMMON, 60, 1);
    }

    @Override
    public void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged) {
        var distance = 4 * timeCharged;
        var distanceSqr = distance * distance;

        if(!level.isClientSide()){
            var explosionPower = getDamagePerHit(wand) * timeCharged / 8;

            var hitResult = getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);
            var pos = hitResult.getLocation();
            spawnParticlesInLine(level, user.getEyePosition().subtract(0.0, 0.35, 0.0), pos, PARTICLE, distanceSqr * 2, RANDOM_DEVIATION, false);
            level.explode(user, pos.x, pos.y, pos.z, explosionPower, true, Explosion.BlockInteraction.BREAK);
        }
    }

    @Override
    public int getChargeTime() {
        return 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getMinDistance() {
        return 16;
    }
}
