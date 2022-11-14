package screret.sas.world.generation.structure;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.world.generation.structure.structure.RitualSpotStructure;

public class ModStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, SpellsAndSorcerers.MODID);

    public static final RegistryObject<StructureType<RitualSpotStructure>> RITUAL_SPOT = STRUCTURE_TYPES.register("ritual_spot", () -> () -> RitualSpotStructure.CODEC);
}
