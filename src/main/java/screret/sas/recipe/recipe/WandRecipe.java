package screret.sas.recipe.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.items.IItemHandler;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.recipe.ModRecipes;

import java.util.Map;

public class WandRecipe implements Recipe<CraftingContainer> {

    public static final ResourceLocation TYPE_ID = Util.resource("wand");
    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    private final ResourceLocation id;
    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;

    public WandRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        for(int i = 0; i <= pInv.getWidth() - MAX_SIZE_X; ++i) {
            for(int j = 0; j <= pInv.getHeight() - MAX_SIZE_Y; ++j) {
                if (this.matches(pInv, i, j, true)) {
                    return true;
                }

                if (this.matches(pInv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingContainer pCraftingInventory, int pWidth, int pHeight, boolean pMirrored) {
        for(int i = 0; i < pCraftingInventory.getWidth(); ++i) {
            for(int j = 0; j < pCraftingInventory.getHeight(); ++j) {
                int k = i - pWidth;
                int l = j - pHeight;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < MAX_SIZE_X && l < MAX_SIZE_Y) {
                    if (pMirrored) {
                        ingredient = this.ingredients.get(MAX_SIZE_X - k - 1 + l * MAX_SIZE_X);
                    } else {
                        ingredient = this.ingredients.get(k + l * MAX_SIZE_X);
                    }
                }

                if (!ingredient.test(pCraftingInventory.getItem(i + j * pCraftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x <= MAX_SIZE_X && y <= MAX_SIZE_Y;
    }

    @Override
    public ItemStack getResultItem() {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WAND_RECIPE_SERIALIZER.get();
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeType<?> getType() {
        return ModRecipes.WAND_RECIPE_TYPE.get();
    }

    private static String[] patternFromJson(JsonArray jsonArr) {
        var astring = new String[jsonArr.size()];
        for (int i = 0; i < astring.length; ++i) {
            var s = GsonHelper.convertToString(jsonArr.get(i), "pattern[" + i + "]");

            if (i > 0 && astring[0].length() != s.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            astring[i] = s;
        }

        return astring;
    }

    public String toString(){
        return getId().toString();
    }

    public static class RecipeType implements net.minecraft.world.item.crafting.RecipeType<WandRecipe> {
        @Override
        public String toString(){
            return WandRecipe.TYPE_ID.toString();
        }
    }

    public static class Serializer implements RecipeSerializer<WandRecipe> {
        @Override
        public WandRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            var map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            var pattern = ShapedRecipe.shrink(WandRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            var inputs = ShapedRecipe.dissolvePattern(pattern, map, MAX_SIZE_X, MAX_SIZE_Y);
            var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new WandRecipe(recipeId, group, inputs, output);
        }

        @Override
        public WandRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            var inputs = NonNullList.withSize(MAX_SIZE_X * MAX_SIZE_Y, Ingredient.EMPTY);

            inputs.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            var result = buffer.readItem();

            return new WandRecipe(recipeId, group, inputs, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WandRecipe recipe) {
            buffer.writeUtf(recipe.group);
            for (var ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
        }
    }
}