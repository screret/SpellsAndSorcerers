package screret.sas.data.recipe.provider;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import screret.sas.ModTags;
import screret.sas.Util;
import screret.sas.block.ModBlocks;
import screret.sas.item.ModItems;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(ModItems.WAND_TABLE.get())
                .define('B', Items.BLAZE_POWDER)
                .define('#', Blocks.END_STONE_BRICKS)
                .define('D', Items.EMERALD)
                .pattern(" B ")
                .pattern("D#D")
                .pattern("###")
                .unlockedBy("has_endstone", has(Blocks.END_STONE))
                .unlockedBy("has_wand_core", has(ModItems.WAND_CORE.get()))
                .unlockedBy("has_wand", has(ModItems.WAND.get()))
                .save(pFinishedRecipeConsumer, Util.resource("wand_table"));

        ShapelessRecipeBuilder.shapeless(ModItems.SOULSTEEL_INGOT.get())
                .requires(ModTags.Items.GLINT_GEMS)
                .requires(ModTags.Items.GLINT_GEMS)
                .requires(ModItems.SOUL_BOTTLE.get(), 2)
                .group("soulsteel_ingot")
                .unlockedBy("has_glint", has(ModItems.GLINT.get()))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModItems.PALANTIR.get())
                .define('E', ModItems.CTHULHU_EYE.get())
                .define('G', Tags.Items.GLASS_TINTED)
                .define('B', Items.POLISHED_BLACKSTONE_BRICKS)
                .pattern("GGG")
                .pattern("GEG")
                .pattern("BBB")
                .unlockedBy("has_eye", has(ModItems.CTHULHU_EYE.get()))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_TINTED))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_BOOTS.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_CHESTPLATE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_HELMET.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_HOE.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_LEGGINGS.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_PICKAXE.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_SHOVEL.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);
        ShapedRecipeBuilder.shaped(ModItems.SOULSTEEL_SWORD.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(pFinishedRecipeConsumer);


        nineBlockStorageRecipesRecipesWithCustomUnpacking(pFinishedRecipeConsumer, ModTags.Items.SOULSTEEL_INGOTS, ModItems.SOULSTEEL_INGOT.get(), ModTags.Items.SOULSTEEL_BLOCKS, ModItems.SOULSTEEL_BLOCK.get(), Util.resource("soulsteel_ingot_from_soulsteel_block"), "soulsteel_ingot");
        nineBlockStorageRecipesWithCustomPacking(pFinishedRecipeConsumer, ModTags.Items.SOULSTEEL_NUGGETS, ModItems.SOULSTEEL_NUGGET.get(), ModTags.Items.SOULSTEEL_INGOTS, ModItems.SOULSTEEL_INGOT.get(), Util.resource("soulsteel_ingot_from_nuggets"), "soulsteel_ingot");
        oreSmelting(pFinishedRecipeConsumer, ModTags.Items.GLINT_ORES, ModItems.GLINT_ORE.get(), ModItems.GLINT.get(), 1.5F, 200, "glint");
        oreBlasting(pFinishedRecipeConsumer, ModTags.Items.GLINT_ORES, ModItems.GLINT_ORE.get(), ModItems.GLINT.get(), 1.5F, 100, "glint");

    }

    protected static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pPackingRecipeName, String pPackingRecipeGroup) {
        nineBlockStorageRecipes(pFinishedRecipeConsumer, pUnpacked, unpackedResult, pPacked, packedResult, pPackingRecipeName, pPackingRecipeGroup, getItemLocation(unpackedResult), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pUnpackingRecipeName, String pUnpackingRecipeGroup) {
        nineBlockStorageRecipes(pFinishedRecipeConsumer, pUnpacked, unpackedResult, pPacked, packedResult, getItemLocation(packedResult), null, pUnpackingRecipeName, pUnpackingRecipeGroup);
    }

    protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pPackingRecipeName, @Nullable String pPackingRecipeGroup, ResourceLocation pUnpackingRecipeName, @Nullable String pUnpackingRecipeGroup) {
        ShapelessRecipeBuilder.shapeless(unpackedResult, 9)
                .requires(pPacked)
                .group(pUnpackingRecipeGroup)
                .unlockedBy(getHasName(pPacked), has(pPacked))
                .save(pFinishedRecipeConsumer, pUnpackingRecipeName);
        ShapedRecipeBuilder.shaped(packedResult)
                .define('#', pUnpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(pPackingRecipeGroup)
                .unlockedBy(getHasName(pUnpacked), has(pUnpacked))
                .save(pFinishedRecipeConsumer, pPackingRecipeName);
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> pIngredients, ItemLike ingredient, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, ingredient, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> pIngredients, ItemLike ingredient, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, ingredient, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, SimpleCookingSerializer<?> pCookingSerializer, TagKey<Item> pIngredients, ItemLike ingredient, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(pIngredients), pResult, pExperience, pCookingTime, pCookingSerializer).group(pGroup).unlockedBy(getHasName(pIngredients), has(pIngredients)).save(pFinishedRecipeConsumer, getItemLocation(pResult) + pRecipeName + "_" + getItemName(ingredient));

    }

    protected static ResourceLocation getItemLocation(ItemLike item){
        return ForgeRegistries.ITEMS.getKey(item.asItem());
    }

    protected static String getHasName(TagKey<Item> tag) {
        return "has_" + tag.location();
    }
}
