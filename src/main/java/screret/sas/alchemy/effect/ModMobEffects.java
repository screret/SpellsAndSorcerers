package screret.sas.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.alchemy.effect.effect.ManaMobEffect;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SpellsAndSorcerers.MODID);

    public static final RegistryObject<MobEffect> MANA = EFFECTS.register("mana", () -> new ManaMobEffect(MobEffectCategory.BENEFICIAL, 0xFF00e180));
}
