package screret.sas.data.tag;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;

public class SASBiomeTagsProvider extends BiomeTagsProvider {
    public SASBiomeTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    protected void addTags() {
        tag(ModTags.Biomes.HAS_RITUAL_SPOT).add(Biomes.PLAINS).add(Biomes.FROZEN_PEAKS).add(Biomes.MEADOW).add(Biomes.BIRCH_FOREST);
    }
}
