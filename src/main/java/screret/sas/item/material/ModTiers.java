package screret.sas.item.material;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import screret.sas.ModTags;

public class ModTiers {
    public static final ForgeTier SOULSTEEL = new ForgeTier(5, 3417, 10.0F, 5.0F, 18, Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.of(ModTags.Items.SOULSTEEL_INGOTS));
}
