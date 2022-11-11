package screret.sas.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<EntityType<WizardEntity>> WIZARD = ENTITY_TYPES.register("wizard", () -> EntityType.Builder.of(WizardEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).build(WizardEntity.class.getSimpleName().toLowerCase()));
    public static final RegistryObject<EntityType<BossWizardEntity>> BOSS_WIZARD = ENTITY_TYPES.register("wizard", () -> EntityType.Builder.of(BossWizardEntity::new, MobCategory.MONSTER).sized(1F, 3F).build(BossWizardEntity.class.getSimpleName().toLowerCase()));
}
