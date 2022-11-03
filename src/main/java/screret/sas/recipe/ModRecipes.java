package screret.sas.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.recipe.recipe.WandRecipe;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SpellsAndSorcerers.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<RecipeSerializer<WandRecipe>> WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("wand", WandRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<WandRecipe>> WAND_RECIPE_TYPE = RECIPE_TYPES.register("wand", WandRecipe.RecipeType::new);

}
