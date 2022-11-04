package screret.sas.ability.ability;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.Arrays;

public abstract class ProjectileAbility extends WandAbility {

    protected final int distance;


    public ProjectileAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, int distance, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState()), color);
        this.distance = distance;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.Vec3Wrapped currentPosition, int timeCharged) {
        if(!level.isClientSide){
            level.addFreshEntity(spawnProjectile(level, user, stack, timeCharged));
            return InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    public abstract Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged);
}
