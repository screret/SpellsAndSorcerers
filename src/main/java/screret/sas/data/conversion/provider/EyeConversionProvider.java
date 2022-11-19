package screret.sas.data.conversion.provider;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import screret.sas.Util;
import screret.sas.data.conversion.builder.EyeConversionBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class EyeConversionProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final DataGenerator.PathProvider pathProvider;


    public EyeConversionProvider(DataGenerator pGenerator) {
        this.pathProvider = pGenerator.createPathProvider(DataGenerator.Target.DATA_PACK, "eye_conversions");
    }

    protected void buildCraftingRecipes(Consumer<EyeConversionBuilder.Result> finished) {
        addConversion(finished, Blocks.GOLD_BLOCK, Tags.Blocks.STORAGE_BLOCKS_IRON);
        addConversion(finished, Blocks.IRON_BLOCK, Tags.Blocks.STORAGE_BLOCKS_GOLD);
        addConversion(finished, Blocks.EMERALD_BLOCK, Tags.Blocks.STORAGE_BLOCKS_DIAMOND);
        addConversion(finished, Blocks.COAL_BLOCK, Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER);
        addConversion(finished, Blocks.RAW_COPPER_BLOCK, Tags.Blocks.STORAGE_BLOCKS_COAL);
        addConversion(finished, Blocks.COBBLED_DEEPSLATE, Tags.Blocks.COBBLESTONE_NORMAL);
        addConversion(finished, Blocks.COBBLESTONE, Tags.Blocks.COBBLESTONE_DEEPSLATE);

        addConversion(finished, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL);
        addConversion(finished, Blocks.SKELETON_SKULL, Blocks.ZOMBIE_HEAD);
        addConversion(finished, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL);
        addConversion(finished, Blocks.SKELETON_WALL_SKULL, Blocks.ZOMBIE_WALL_HEAD);

    }


    protected void addConversion(Consumer<EyeConversionBuilder.Result> finished, Block result, TagKey<Block> items){
        EyeConversionBuilder.conversion(result)
                .requires(items)
                .save(finished, Util.resource(ForgeRegistries.BLOCKS.getKey(result).getPath()));
    }

    protected void addConversion(Consumer<EyeConversionBuilder.Result> finished, Block result, Block item){
        EyeConversionBuilder.conversion(result)
                .requires(item)
                .save(finished, Util.resource(ForgeRegistries.BLOCKS.getKey(result).getPath()));
    }

    @Override
    public void run(CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        buildCraftingRecipes((result) -> {
            if (!set.add(result.getId())) {
                throw new IllegalStateException("Duplicate recipe " + result.getId());
            } else {
                saveRecipe(pOutput, result.serializeRecipe(), this.pathProvider.json(result.getId()));
            }
        });
    }

    private static void saveRecipe(CachedOutput pOutput, JsonObject pRecipeJson, Path pPath) {
        try {
            DataProvider.saveStable(pOutput, pRecipeJson, pPath);
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't save recipe {}", pPath, ioexception);
        }

    }

        @Override
    public String getName() {
        return "Eye Conversions";
    }
}
