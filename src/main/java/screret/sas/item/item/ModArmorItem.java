package screret.sas.item.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.config.SASConfig;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ModArmorItem extends GeoArmorItem implements IAnimatable {
    public static final MobEffectInstance SOUL_STEEL_EFFECT = new MobEffectInstance(ModMobEffects.MANA.get(), 200, 1);

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private final MobEffectInstance fullSetEffect;

    public ModArmorItem(ArmorMaterial material, MobEffectInstance fullSetEffect, EquipmentSlot slot, Properties builder) {
        super(material, slot, builder);
        this.fullSetEffect = fullSetEffect;
    }

    private PlayState predicate(AnimationEvent<ModArmorItem> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.%s_armor.idle".formatted(this.material.getName()), ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if(!world.isClientSide()) {
            if(SASConfig.Server.armorGiveEffects.get()){
                if(hasFullSuitOfArmorOn(player)) {
                    evaluateArmorEffects(player);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private void evaluateArmorEffects(Player player) {
        if(hasCorrectArmorOn(this.material, player)) {
            addStatusEffectForMaterial(player, this.material, this.fullSetEffect);
        }
    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmorMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());

        if(hasCorrectArmorOn(mapArmorMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect.getEffect(),
                    mapStatusEffect.getDuration(), mapStatusEffect.getAmplifier()));
        }
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack breastplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        ArmorItem boots = (ArmorItem)player.getInventory().getArmor(0).getItem();
        ArmorItem leggings = (ArmorItem)player.getInventory().getArmor(1).getItem();
        ArmorItem breastplate = (ArmorItem)player.getInventory().getArmor(2).getItem();
        ArmorItem helmet = (ArmorItem)player.getInventory().getArmor(3).getItem();

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }
}
