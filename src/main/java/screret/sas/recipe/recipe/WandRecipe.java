package screret.sas.recipe.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import screret.sas.SpellsAndSorcerers;
import screret.sas.recipe.ModRecipes;

public class WandRecipe implements Recipe<CraftingContainer> {

    public static final ResourceLocation TYPE_ID = new ResourceLocation(SpellsAndSorcerers.MODID, "cash_conversion");
    public static final int MAX_SIZE = 1;

    private final ResourceLocation id;
    final String group;
    final ItemStack result;
    final StrictNBTIngredient ingredient;

    public WandRecipe(ResourceLocation id, String group, ItemStack result, StrictNBTIngredient ingredient) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        var stack = container.getItem(0);
        return this.ingredient.test(stack) && stack.getCount() >= ingredient.getItems()[0].getCount();
    }

    public ItemStack getIngredientStack(){
        return this.ingredient.getItems()[0];
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result.copy();
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

    public static class RecipeType implements net.minecraft.world.item.crafting.RecipeType<WandRecipe> {
        @Override
        public String toString(){
            return WandRecipe.TYPE_ID.toString();
        }
    }

    public static class Serializer implements RecipeSerializer<WandRecipe> {

        @Override
        public WandRecipe fromJson(ResourceLocation resourceLocation, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            StrictNBTIngredient ingredient = (StrictNBTIngredient) Ingredient.fromJson(GsonHelper.getAsJsonObject(json,"ingredient"));

            if (ingredient.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                return new WandRecipe(resourceLocation, group, result, ingredient);
            }
        }

        public WandRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            StrictNBTIngredient ingredient = (StrictNBTIngredient) Ingredient.fromNetwork(buf);
            ItemStack itemstack = buf.readItem();
            return new WandRecipe(resourceLocation, group, itemstack, ingredient);
        }

        public void toNetwork(FriendlyByteBuf buf, WandRecipe recipe) {
            buf.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }
}