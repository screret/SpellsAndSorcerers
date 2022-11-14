package screret.sas.world.generation.structure;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;

public class ModStructureSets {
    public static final DeferredRegister<StructureSet> STRUCTURE_SETS = DeferredRegister.create(Registry.STRUCTURE_SET_REGISTRY, SpellsAndSorcerers.MODID);

    public static final RegistryObject<StructureSet> RITUAL_SPOTS = STRUCTURE_SETS.register("ritual_spots", () -> new StructureSet(ModStructures.RITUAL_SPOT.getHolder().get(), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 17523456)));

}
