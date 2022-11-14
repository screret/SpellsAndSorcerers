package screret.sas.world.generation.structure;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.entity.ModEntities;
import screret.sas.world.generation.structure.structure.RitualSpotStructure;

import java.util.Map;

public class ModStructures {
    public static final DeferredRegister<Structure> STRUCTURES = DeferredRegister.create(Registry.STRUCTURE_REGISTRY, SpellsAndSorcerers.MODID);


    public static final RegistryObject<Structure> RITUAL_SPOT = STRUCTURES.register("ritual_spot", () -> new RitualSpotStructure(structure(
            ModTags.Biomes.HAS_RITUAL_SPOT,
            Map.of(
                    MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.WIZARD.get(), 1, 1, 3)))
            ),
            GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
            UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.absolute(128))
    ));


    private static Structure.StructureSettings structure(TagKey<Biome> pKey, Map<MobCategory, StructureSpawnOverride> pSpawnOverrides, GenerationStep.Decoration pDecoration, TerrainAdjustment pTerrainAdjustment) {
        return new Structure.StructureSettings(biomes(pKey), pSpawnOverrides, pDecoration, pTerrainAdjustment);
    }

    private static HolderSet<Biome> biomes(TagKey<Biome> pKey) {
        return BuiltinRegistries.BIOME.getOrCreateTag(pKey);
    }

}
