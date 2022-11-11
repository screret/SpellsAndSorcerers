package screret.sas.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<SimpleParticleType> EYE = PARTICLES.register("eye", () -> new SimpleParticleType(true));
}
