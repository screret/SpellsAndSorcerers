package screret.sas.alchemy.potion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.alchemy.effect.ModMobEffects;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, SpellsAndSorcerers.MODID);

    public static final RegistryObject<Potion> MANA = POTIONS.register("mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 3600)));
    public static final RegistryObject<Potion> LONG_MANA = POTIONS.register("long_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_MANA = POTIONS.register("long_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 1800, 1)));
}

