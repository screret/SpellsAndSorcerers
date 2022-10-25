package screret.sas.item.wand;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.capability.ModCapabilities;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.item.wand.power.IBase;
import screret.sas.item.wand.power.IIsChargeable;
import screret.sas.item.wand.power.IIsHoldable;

import java.util.concurrent.atomic.AtomicReference;

public class WandItem extends Item implements IBase {

    private static final String WAND_LANG_KEY = "item.sas.wand";

    public final int cooldownTicks;
    public final float baseDamagePerHit;

    public WandItem(int durability, Rarity rarity, int cooldownTicks, float baseDamagePerHit) {
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
        AtomicReference<InteractionResultHolder<ItemStack>> returnValue = new AtomicReference<>(InteractionResultHolder.fail(itemstack));
        if(currentItem instanceof WandItem){
            boolean isCrouching = player.isCrouching();
            itemstack.getCapability(ModCapabilities.WAND_ABILITY).ifPresent((consumer) -> {
                if(isCrouching){
                    returnValue.set(consumer.getCrouchAbility().execute(level, player, hand));
                }
                else {
                    returnValue.set(consumer.getAbility().execute(level, player, hand));
                }
            });

        }
        return returnValue.get();
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

    public void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged){

    }

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
}
