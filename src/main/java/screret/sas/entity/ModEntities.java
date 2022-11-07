package screret.sas.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.entity.entity.WizardMob;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<EntityType<WizardMob>> WIZARD_TYPE = ENTITY_TYPES.register("wizard", () -> EntityType.Builder.of(WizardMob::new, MobCategory.MONSTER).sized(0.6F, 1.95F).build(WizardMob.class.getSimpleName().toLowerCase()));
}
