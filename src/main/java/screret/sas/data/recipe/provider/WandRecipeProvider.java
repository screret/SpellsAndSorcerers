package screret.sas.data.recipe.provider;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.Tags;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.data.recipe.builder.WandRecipeBuilder;
import screret.sas.item.ModItems;

import java.util.function.Consumer;

public class WandRecipeProvider extends RecipeProvider {

    public WandRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        Util.addItems();
        getBuilder(ModWandAbilities.DAMAGE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', ModItems.SOUL_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.DAMAGE.get()))
                .save(consumer, Util.resource("damage_wand"));

        getBuilder(ModWandAbilities.EXPLODE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.EXPLODE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.TNT)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.EXPLODE.get()))
                .save(consumer, Util.resource("explosion_wand"));

        getBuilder(ModWandAbilities.LARGE_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIRE_CHARGE)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .save(consumer, Util.resource("large_fireball_wand"));

        getBuilder(ModWandAbilities.SMALL_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIREWORK_STAR)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .save(consumer, Util.resource("small_fireball_wand"));

        getBuilder(ModWandAbilities.HEAL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.HEAL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING))
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.HEAL.get()))
                .save(consumer, Util.resource("heal_wand"));

    }

    public WandRecipeBuilder getBuilder(WandAbility ability){
        return new WandRecipeBuilder(Util.customWands.get(ability.getKey()));
    }

    public ItemStack getWandCore(WandAbility ability){
        return Util.customWandCores.get(ability.getKey());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemStack stack) {
        var saved = stack.serializeNBT();
        var tag = new CompoundTag();
        if(saved.contains("tag")){
            tag = tag.merge(saved.getCompound("tag"));
        }
        if(saved.contains("ForgeCaps")){
            tag.put("ForgeCaps", saved.getCompound("ForgeCaps"));
        }
        if(saved.contains("tag") || saved.contains("ForgeCaps")) return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).hasNbt(tag).build());
        else return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance hasCore(WandAbility ability) {
        return has(Util.customWandCores.get(ability.getKey()));
    }
}
