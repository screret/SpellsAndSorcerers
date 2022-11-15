package screret.sas.alchemy.effect.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import screret.sas.api.capability.mana.CapabilityMana;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.config.SASConfig;

public class ManaMobEffect extends MobEffect {
    public ManaMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            player.getCapability(ManaProvider.MANA).ifPresent((cap) -> {
                cap.setMaxMana(SASConfig.Server.maxDefaultMana.get());
            });
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            player.getCapability(ManaProvider.MANA).ifPresent((cap) -> {
                cap.setMaxMana(cap.getMaxManaStored() + (pAmplifier + 1) * 25);
            });
        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }
}
