package screret.sas.data.tag;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.item.ModItems;

public class SASBlockTagsProvider extends BlockTagsProvider {

    public SASBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    protected void addTags() {
        tag(ModTags.Blocks.GLINT_ORES).add(ModBlocks.GLINT_ORE.get());
        tag(ModTags.Blocks.SOULSTEEL_BLOCKS).add(ModBlocks.SOULSTEEL_BLOCK.get());

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(ModBlocks.GLINT_ORE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GLINT_ORE.get());
    }
}
