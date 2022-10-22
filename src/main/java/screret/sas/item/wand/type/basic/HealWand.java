package screret.sas.item.wand.type.basic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.item.wand.Wand;
import screret.sas.item.wand.power.IIsHoldable;
import screret.sas.item.wand.power.IIsRaycaster;

public class HealWand extends Wand implements IIsRaycaster, IIsHoldable {

    public static final int HOLD_TIME = 40;

    public HealWand() {
        super(200, Rarity.COMMON, 100, 0.5f);
    }

    @Override
    public void fire(Level level, LivingEntity user, ItemStack wand, int timeUsed) {
        if(timeUsed > getHoldTime()){
            return;
        }

        if(!level.isClientSide){
            if(user.isCrouching()){
                user.heal(getDamagePerHit(wand));
                return;
            }
            var distance = getMinDistance();
            var distanceSqr = distance * distance;
            var hitResult = getHitResult(level, user, (entity) -> entity != user, distanceSqr);

            var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
            if(hitResult != null){
                var hitEntity = hitResult.getEntity();
                if(hitEntity instanceof LivingEntity entity){
                    entity.heal(getDamagePerHit(wand));
                }

                int distanceToEnd = (int) userPos.distanceTo(hitResult.getLocation());
                spawnParticlesInLine(level, userPos, hitResult.getLocation(), ParticleTypes.HAPPY_VILLAGER, distanceToEnd * 4, Vec3.ZERO, false);
                return;
            }

            var blockHit = getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);
            spawnParticlesInLine(level, userPos, blockHit.getLocation(), ParticleTypes.HAPPY_VILLAGER, distanceSqr * 4, Vec3.ZERO, false);
        }
    }

    public int getHoldTime(){
        return HOLD_TIME;
    }

    @Override
    public int getMinDistance() {
        return 16;
    }
}
