package screret.sas.data.recipe.provider;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import screret.sas.SpellsAndSorcerers;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.data.recipe.builder.WandRecipeBuilder;
import screret.sas.item.ModCreativeTab;
import screret.sas.item.ModItems;

import java.util.function.Consumer;

public class WandRecipeProvider extends RecipeProvider {

    public WandRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ModCreativeTab tab = (ModCreativeTab) SpellsAndSorcerers.SAS_TAB;
        for(WandAbility ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()) {
            getBuilder(tab, ability)
                    .pattern(" C ")
                    .pattern("LSL")
                    .define('S', ModItems.WAND_HANDLE.get())
                    .define('C', getWandCore(ability))
                    .define('L', Tags.Items.LEATHER)
                    .save(consumer, ability.getKey());
        }
        /*getBuilder(tab, ModWandAbilities.DAMAGE.get())
                .pattern(" C ")
                .pattern("LSL")
                .define('S', ModItems.WAND_HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHER)
                .save(consumer, new ResourceLocation(SpellsAndSorcerers.MODID, "damage_wand"));
         */
    }

    public WandRecipeBuilder getBuilder(ModCreativeTab tab, WandAbility ability){
        return new WandRecipeBuilder(tab.getCustomWands().get(getLocationFromAbility(ability)));
    }

    public ResourceLocation getLocationFromAbility(WandAbility ability){
        return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getKey(ability);
    }

    public ItemStack getWandCore(WandAbility ability){
        return ((ModCreativeTab) SpellsAndSorcerers.SAS_TAB).customWandCoreAdded.get(getLocationFromAbility(ability));
    }
}
