package screret.sas.item.wand;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import screret.sas.SpellsAndSorcerers;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.item.wand.power.IBase;
import screret.sas.item.wand.power.IIsChargeable;
import screret.sas.item.wand.power.IIsHoldable;
import screret.sas.item.wand.power.IIsInstantFireable;

import java.util.function.Predicate;

public abstract class Wand extends Item implements IBase {

    private static final String WAND_LANG_KEY = "item.sas.wand";

    private static final float PI_FLOAT = (float) Math.PI;
    private static final float PI_180 = (PI_FLOAT / 180F);

    public final int cooldownTicks;
    public final float baseDamagePerHit;

    public Wand(int durability, Rarity rarity, int cooldownTicks, float baseDamagePerHit) {
        super(new Properties().durability(durability).rarity(rarity).tab(SpellsAndSorcerers.SAS_TAB));
        this.cooldownTicks = cooldownTicks;
        this.baseDamagePerHit = baseDamagePerHit;
    }

    public int getCooldown(){
        return cooldownTicks;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(WAND_LANG_KEY, Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item currentItem = itemstack.getItem();
        if(currentItem instanceof IIsInstantFireable instantFireable) {
            fire(level, player, itemstack,0);
            player.getCooldowns().addCooldown(currentItem, instantFireable.getCooldown());
            return InteractionResultHolder.pass(itemstack);
        } else if(currentItem instanceof IIsChargeable) {
            if (!player.isUsingItem()) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            }
        } else if(currentItem instanceof IIsHoldable) {
            if (!player.isUsingItem()) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

    public int getUseDuration(ItemStack stack) {
        if(stack.getItem() instanceof IIsChargeable item){
            int quickChangeEnchantLevel = stack.getEnchantmentLevel(ModEnchantments.QUICK_CHARGE.get());
            var chargeTime = item.getChargeTime();
            return quickChangeEnchantLevel == 0 ? chargeTime : chargeTime - (chargeTime / 5) * quickChangeEnchantLevel;
        } else if(stack.getItem() instanceof IIsHoldable item){
            int prolongedUseEnchantLevel = stack.getEnchantmentLevel(ModEnchantments.PROLONGED_USE.get());
            var usageTime = item.getHoldTime();
            return prolongedUseEnchantLevel == 0 ? usageTime : usageTime - (usageTime / 5) * prolongedUseEnchantLevel;
        }
        return 0;
    }


    @Override
    public void onUsingTick(ItemStack stack, LivingEntity user, int usageTicks) {
        Level level = user.level;
        if(!level.isClientSide){
            if(stack.getItem() instanceof IIsHoldable){
                fire(level, user, stack, usageTicks);
            }
        }
    }

    public abstract void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged);

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        var item = stack.getItem();
        if(item instanceof IIsChargeable chargeable){
            fire(level, entity, stack, this.getUseDuration(stack) - timeLeft);
            if(entity instanceof Player player){
                player.getCooldowns().addCooldown(item, chargeable.getCooldown());
            }
        } else if(item instanceof IIsHoldable holdable){
            if(entity instanceof Player player){
                player.getCooldowns().addCooldown(item, holdable.getCooldown());
            }
        }
    }

    @Override
    public float getDamagePerHit(ItemStack stack){
        return baseDamagePerHit + (baseDamagePerHit / 5) * stack.getEnchantmentLevel(ModEnchantments.POWER.get());
    }

    protected static BlockHitResult getHitResult(Level level, LivingEntity entity, ClipContext.Fluid fluidInteractionMode, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return level.clip(new ClipContext(entity.getEyePosition(), entityPosStuff.to, ClipContext.Block.OUTLINE, fluidInteractionMode, entity));
    }

    protected static EntityHitResult getHitResult(Level level, LivingEntity entity, Predicate<Entity> filter, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return ProjectileUtil.getEntityHitResult(entity, entity.getEyePosition(), entityPosStuff.to, AABB.ofSize(entityPosStuff.from, distance, distance, distance), filter, distance);
    }

    protected static EntityPosStuff getEntityPos(LivingEntity entity, double distance){
        EntityPosStuff stuff = new EntityPosStuff();
        stuff.xRot = entity.getXRot();
        stuff.yRot = entity.getYRot();
        stuff.from = entity.position();
        float cosY = Mth.cos(-stuff.yRot * PI_180 - PI_FLOAT);
        float sinY = Mth.sin(-stuff.yRot * PI_180 - PI_FLOAT);
        float cosX = -Mth.cos(-stuff.xRot * PI_180);
        float sinX = Mth.sin(-stuff.xRot * PI_180);
        float sinCos = sinY * cosX;
        float cosCos = cosY * cosX;
        stuff.to = entity.getEyePosition().add((double)sinCos * distance, (double)sinX * distance, (double)cosCos * distance);

        return stuff;
    }

    protected void spawnParticlesInLine(Level level, Vec3 start, Vec3 end, ParticleOptions particle, int pointsPerLine, Vec3 speed, boolean alwaysRender){
        double d = start.distanceTo(end) / pointsPerLine;
        for (int i = 0; i < pointsPerLine; i++) {
            Vec3 pos = new Vec3(start.x, start.y, start.z);
            Vec3 direction = end.subtract(start).normalize();
            Vec3 v = direction.multiply(i * d, i * d, i * d);

            pos = pos.add(v);
            if(level.isClientSide){
                level.addParticle(particle, alwaysRender, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
                continue;
            }
            ((ServerLevel)level).sendParticles(particle, pos.x, pos.y, pos.z, 1, speed.x, speed.y, speed.z, 0);
        }
    }

    protected static class EntityPosStuff {
        public float xRot, yRot;
        public Vec3 from, to;
    }
}
