package screret.sas.item.wand.type.basic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import screret.sas.item.wand.Wand;
import screret.sas.item.wand.power.IIsInstantFireable;
import screret.sas.item.wand.power.IIsRaycaster;

public class RayWand extends Wand implements IIsInstantFireable, IIsRaycaster, DyeableLeatherItem {

    public RayWand() {
        super(100, Rarity.COMMON, 10, 1f);
    }

    @Override
    public void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged) {
        var distance = getMinDistance();
        var distanceSqr = distance * distance;
        var hitResult = getHitResult(level, user, (entity) -> entity != user, distanceSqr);
        if(level.isClientSide()){
            var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
            if(hitResult != null){
                int distanceToEnd = (int) userPos.distanceTo(hitResult.getLocation());
                spawnParticlesInLine(level, userPos, hitResult.getLocation(), ParticleTypes.SOUL_FIRE_FLAME, distanceToEnd * 2, Vec3.ZERO, false);
                return;
            }

            var blockHit = getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);
            spawnParticlesInLine(level, userPos, blockHit.getLocation(), ParticleTypes.SOUL_FIRE_FLAME, distanceSqr * 2, Vec3.ZERO, false);
        }else{
            if(hitResult != null) {
                hitResult.getEntity().hurt(DamageSource.indirectMagic(user, user), getDamagePerHit(wand));
            }
        }
    }

    @Override
    public int getMinDistance() {
        return 32;
    }
}
