package screret.sas.integration.jei.wand;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.block.ModBlocks;
import screret.sas.recipe.recipe.ShapedWandRecipe;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.List;

public class WandRecipeCategory implements IRecipeCategory<WandRecipe> {

    public static final RecipeType<WandRecipe> JEI_RECIPE_TYPE = RecipeType.create(SpellsAndSorcerers.MODID, "wand", WandRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = Util.resource("textures/gui/jei/jei_wand_gui.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public WandRecipeCategory(IGuiHelper guiHelper)
    {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.WAND_TABLE.get()));
        background = guiHelper.createDrawable(GUI_TEXTURE, 0, 0, 116, 36);
        localizedName = Component.translatable("container.sas.wand_table");
    }

    @Override
    public RecipeType<WandRecipe> getRecipeType() {
        return JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WandRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        ItemStack resultItem = recipe.getResultItem();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 10).addItemStacks(List.of(resultItem));

        var isShaped = recipe instanceof ShapedWandRecipe;
        int width = isShaped ? ShapedWandRecipe.MAX_SIZE_X : 0;
        int height = isShaped ? ShapedWandRecipe.MAX_SIZE_Y : 0;

        if (width <= 0 || height <= 0) {
            builder.setShapeless();
        }

        createAndSetInputs(builder, inputs, width, height);
    }

    @Override
    public ResourceLocation getRegistryName(WandRecipe recipe) {
        return recipe.getId();
    }


    public static void createAndSetInputs(IRecipeLayoutBuilder builder, List<@Nullable List<@Nullable ItemStack>> inputs, int width, int height) {
        if (width <= 0 || height <= 0) {
            width = height = getShapelessSize(inputs.size());
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
                var index = x + y * width;
                if(inputs.size() > index && inputs.get(index) != null){
                    slot.addItemStacks(inputs.get(index));
                }

            }
        }
    }

    private static int getShapelessSize(int total) {
        if (total > 4) {
            return 3;
        } else if (total > 1) {
            return 2;
        } else {
            return 1;
        }
    }
}
