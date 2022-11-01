package screret.sas.api.wand.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import screret.sas.SpellsAndSorcerers;
import screret.sas.enchantment.ModEnchantments;

public class WandAbility implements IWandAbility {

    private final int useDuration, cooldownDuration;
    private final float damagePerHit;
    private final boolean applyEnchants;
    protected final ParticleOptions particle;

    public WandAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle){
        this.useDuration = useDuration;
        this.cooldownDuration = cooldownDuration;
        this.damagePerHit = damagePerHit;
        this.applyEnchants = applyEnchants;
        this.particle = particle;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.Vec3Wrapped currentPosition, int timeCharged) {
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration() {
        return this.useDuration;
    }

    @Override
    public boolean isHoldable() {
        return false;
    }

    public boolean isChargeable(){
        return false;
    }

    @Override
    public int getCooldownDuration() {
        return this.cooldownDuration;
    }

    @Override
    public float getBaseDamagePerHit() {
        return damagePerHit;
    }

    @Override
    public float getDamagePerHit(ItemStack stack){
        return damagePerHit + (damagePerHit / 5) * stack.getEnchantmentLevel(ModEnchantments.POWER.get());
    }

    @Override
    public ResourceLocation getKey() {
        return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getKey(this);
    }

    @Override
    public ResourceLocation getModelLocation() {
        return new ResourceLocation(SpellsAndSorcerers.WAND_ABILIY_PATH.getNamespace(), SpellsAndSorcerers.WAND_ABILIY_PATH.getPath() + getKey().getPath());
    }

    @Override
    public String toString() {
        return getKey().toString();
    }
}
