package screret.sas.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import screret.sas.Util;
import screret.sas.block.ModBlocks;
import screret.sas.client.gui.WandTableScreen;
import screret.sas.integration.jei.wand.WandCoreSubtypeInterpreter;
import screret.sas.integration.jei.wand.WandRecipeCategory;
import screret.sas.integration.jei.wand.WandSubtypeInterpreter;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.List;

@JeiPlugin
public class SASJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Util.resource("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WandRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.WAND.get(), WandSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ModItems.WAND_CORE.get(), WandCoreSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(WandTableScreen.class, 89, 35, 22, 15, WandRecipeCategory.JEI_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WAND_TABLE.get()), WandRecipeCategory.JEI_RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<WandRecipe> wandRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipes.WAND_RECIPE_TYPE.get());
        registration.addRecipes(WandRecipeCategory.JEI_RECIPE_TYPE, wandRecipes);
    }
}
