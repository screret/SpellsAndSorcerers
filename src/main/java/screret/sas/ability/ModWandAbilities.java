package screret.sas.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.Util;
import screret.sas.ability.ability.*;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;

import static screret.sas.Util.*;
import static screret.sas.api.wand.ability.WandAbilityRegistry.WAND_ABILITIES;


public class ModWandAbilities {
    public static final RegistryObject<WandAbility> DUMMY = WAND_ABILITIES.register("dummy", () -> new WandAbility(0, 0, 0, false, null, 0xFF000000));

    public static final RegistryObject<WandAbility> SHOOT_RAY = WAND_ABILITIES.register("shoot_ray", () -> new ShootAbility(0, 0, 0, true, 32, ParticleTypes.SOUL_FIRE_FLAME, Vec3.ZERO, 0xFF54cbcf));
    public static final RegistryObject<WandAbility> SHOOT_HOLD_DOWN = WAND_ABILITIES.register("shoot_hold_down", () -> new ShootAbility(20, 20, 1, true, 16, ParticleTypes.HAPPY_VILLAGER, Vec3.ZERO, 0xFF00ae2d));
    public static final RegistryObject<WandAbility> SHOOT_ANGRY_RAY = WAND_ABILITIES.register("shoot_angry_ray", () -> new ShootAbility(100, 200, 1, true, 8, ExplodeAbility.PARTICLE, ExplodeAbility.RANDOM_DEVIATION, 0xFFAA0000));

    public static final RegistryObject<WandAbility> DAMAGE = WAND_ABILITIES.register("damage", DamageAbility::new);
    public static final RegistryObject<WandAbility> EXPLODE = WAND_ABILITIES.register("explode", ExplodeAbility::new);
    public static final RegistryObject<WandAbility> HEAL = WAND_ABILITIES.register("heal", HealAbility::new);
    public static final RegistryObject<WandAbility> HEAL_SELF = WAND_ABILITIES.register("heal_self", HealSelfAbility::new);

    public static final RegistryObject<WandAbility> SMALL_FIREBALL = WAND_ABILITIES.register("small_fireball", SmallFireballAbility::new);
    public static final RegistryObject<WandAbility> LARGE_FIREBALL = WAND_ABILITIES.register("large_fireball", LargeFireballAbility::new);

    public static void init() {}

}
