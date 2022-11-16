package screret.sas.data.tag;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.item.ModItems;

public class SASItemTagsProvider extends ItemTagsProvider {

    public SASItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.Items.GLASS_BOTTLES).add(Items.GLASS_BOTTLE);
        tag(ModTags.Items.BOSS_SUMMON_ITEMS).add(ModItems.WAND_CORE.get(), Items.GLOWSTONE_DUST, Items.LAPIS_BLOCK, Items.ZOMBIE_HEAD);
        tag(ModTags.Items.GLINT_ORES).add(ModItems.GLINT_ORE.get());
        tag(ModTags.Items.GLINT_GEMS).add(ModItems.GLINT.get());
        tag(ModTags.Items.SOULSTEEL_INGOTS).add(ModItems.SOULSTEEL_INGOT.get());
        tag(ModTags.Items.SOULSTEEL_BLOCKS).add(ModItems.SOULSTEEL_BLOCK.get());
        tag(ModTags.Items.SOULSTEEL_NUGGETS).add(ModItems.SOULSTEEL_NUGGET.get());
    }
}
