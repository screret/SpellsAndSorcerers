package screret.sas.item.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.client.item.WandItemClientExtensions;
import screret.sas.enchantment.ModEnchantments;

import javax.annotation.Nullable;

public class WandItem extends Item {

    private static final String WAND_LANG_KEY = "item.sas.wand";


    public WandItem() {
        super(new Properties().durability(100).rarity(Rarity.UNCOMMON));
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "item.sas.basic";
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()){
            var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
            var current = cap.getAbility();
            while (current.getChildren() != null && current.getChildren().size() > 0) {
                current = cap.getAbility().getChildren().get(0);
            }
            name = "ability.sas." + current.getId().getPath();
        }
        return Component.translatable(WAND_LANG_KEY, Component.translatable(name));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (WandAbilityProvider.WAND_ABILITY != null && nbt != null) {
            var main = new WandAbilityInstance(nbt.getCompound(WandAbility.BASIC_ABILITY_KEY));

            WandAbilityInstance crouch = null;
            if(nbt.contains(WandAbility.CROUCH_ABILITY_KEY, 10)){
                crouch = new WandAbilityInstance(nbt.getCompound(WandAbility.CROUCH_ABILITY_KEY));
            }
            return new WandAbilityProvider(main, crouch);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> reference = InteractionResultHolder.fail(itemstack);
        if( itemstack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()){
            var cap = itemstack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
            if(player.isCrouching() && cap.getCrouchAbility() != null){
                var crouchAbility = cap.getCrouchAbility();
                if((crouchAbility.isChargeable() || crouchAbility.isHoldable()) && !player.isUsingItem()){
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                }else{
                    reference = execute(level, player, itemstack, 0, cap);
                }
            }else{
                var ability = cap.getAbility();
                if((ability.isChargeable() || ability.isHoldable()) && !player.isUsingItem()){
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                }else{
                    reference = execute(level, player, itemstack, 0, cap);
                }
            }
        }
        return reference;
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, int timeCharged, ICapabilityWandAbility cap) {
        Item currentItem = stack.getItem();
        var returnValue = InteractionResultHolder.fail(stack);
        if(currentItem instanceof WandItem){
            if(!deductManaFromUser(user, stack, timeCharged)) return returnValue;

            if(user.isCrouching() && cap.getCrouchAbility() != null){
                returnValue = cap.getCrouchAbility().execute(level, user, stack, new WandAbilityInstance.Vec3Wrapped(user.getEyePosition()), timeCharged);
                if(user instanceof Player player){
                    player.getCooldowns().addCooldown(currentItem, cap.getCrouchAbility().getAbility().getCooldownDuration());
                }
            } else {
                returnValue = cap.getAbility().execute(level, user, stack, new WandAbilityInstance.Vec3Wrapped(user.getEyePosition()), timeCharged);
                if(user instanceof Player player){
                    player.getCooldowns().addCooldown(currentItem, cap.getAbility().getAbility().getCooldownDuration());
                }
            }
        }
        return returnValue;
    }


    public boolean deductManaFromUser(LivingEntity user, ItemStack stack, int timeCharged){
        if(user instanceof Player player && player.isCreative()) return true;
        if(user.getCapability(ManaProvider.MANA).isPresent()){
            var manaCap = user.getCapability(ManaProvider.MANA).resolve().get();
            var manaToDeduct = 1 + timeCharged / 4 * (6 - stack.getEnchantmentLevel(ModEnchantments.MANA_EFFICIENCY.get()));
            if(manaCap.getManaStored() < manaToDeduct) return false;
            manaCap.deductMana(manaToDeduct, false);
        }
        return true;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity user, int usageTicks) {
        Level level = user.level;
        if(!level.isClientSide){
            if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()){
                var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
                if(cap.getAbility().isHoldable() || (cap.getCrouchAbility() != null && cap.getCrouchAbility().isHoldable()))  this.execute(level, user, stack, usageTicks, cap);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        stack.getCapability(WandAbilityProvider.WAND_ABILITY).ifPresent(var -> {
            var useDuration = var.getAbility().getUseDuration();
            if(useDuration > 0){
                if(var.getAbility().isChargeable() || (var.getCrouchAbility() != null && var.getCrouchAbility().isChargeable())) this.execute(level, entity, stack, useDuration - timeLeft, var);
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new WandItemClientExtensions());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()){
            return stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get().getAbility().getUseDuration();
        }
        return 0;
    }
}
