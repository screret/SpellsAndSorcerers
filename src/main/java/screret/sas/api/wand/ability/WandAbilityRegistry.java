package screret.sas.api.wand.ability;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import screret.sas.SpellsAndSorcerers;

public class WandAbilityRegistry {
    public static final ResourceKey<Registry<WandAbility>> WAND_ABILITY_REG_KEY = ResourceKey.createRegistryKey(new ResourceLocation(SpellsAndSorcerers.MODID, "wand_abilities"));

    public static final DeferredRegister<WandAbility> WAND_ABILITIES = DeferredRegister.create(WAND_ABILITY_REG_KEY.location(), SpellsAndSorcerers.MODID);

    public static final RegistryBuilder<WandAbility> REGISTRY_BUILDER = new RegistryBuilder<WandAbility>().setName(WAND_ABILITY_REG_KEY.location()).setMaxID(Integer.MAX_VALUE - 1);

    public static IForgeRegistry<WandAbility> WAND_ABILITIES_BUILTIN = WandAbilityRegistry.WAND_ABILITIES.makeRegistry(() -> REGISTRY_BUILDER).get();
}
