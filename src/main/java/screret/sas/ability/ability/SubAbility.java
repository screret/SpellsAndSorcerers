package screret.sas.ability.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public abstract class SubAbility extends WandAbility {

    private static final EntityTypeTest<Entity, ?> ANY_ENTITY_TYPE = new EntityTypeTest<>() {
        public Entity tryCast(Entity entity) {
            return entity;
        }

        public Class<? extends Entity> getBaseClass() {
            return LivingEntity.class;
        }
    };

    public SubAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle);
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.Vec3Wrapped currentPosition, int timeCharged) {
        if(level.isClientSide) return InteractionResultHolder.pass(stack);
        AABB bounds = AABB.ofSize(currentPosition.real, 0.01, 0.01, 0.01);
        List<? extends Entity> allHitPossibilities = level.getEntities(SubAbility.ANY_ENTITY_TYPE, bounds, entity -> entity != user);
        if(allHitPossibilities.isEmpty()) {
            doHit(stack, user, currentPosition.real, timeCharged);
            return InteractionResultHolder.pass(stack);
        }
        allHitPossibilities.sort((thisPart, next) -> (int)Math.round(next.position().distanceTo(currentPosition.real) - thisPart.position().distanceTo(currentPosition.real)));
        doHit(stack, user, (LivingEntity) allHitPossibilities.get(0), timeCharged);
        return InteractionResultHolder.pass(stack);
    }

    public abstract void doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged);

    public abstract void doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged);
}
