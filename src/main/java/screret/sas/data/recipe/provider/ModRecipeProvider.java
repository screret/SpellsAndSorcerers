package screret.sas.data.recipe.provider;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.block.ModBlocks;
import screret.sas.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.WAND_TABLE.get())
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

    }
}
