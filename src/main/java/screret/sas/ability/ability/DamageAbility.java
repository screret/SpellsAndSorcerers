package screret.sas.ability.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DamageAbility extends SubAbility {

    public DamageAbility() {
        super(0, 10, 1, true, ParticleTypes.SOUL_FIRE_FLAME, EnumSet.of(HitFlags.ENTITY));
    }

    @Override
    public void doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        hitEnt.hurt(DamageSource.indirectMagic(user, user), getDamagePerHit(usedItem));
    }

    @Override
    public void doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) { }
}