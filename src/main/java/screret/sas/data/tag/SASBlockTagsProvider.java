package screret.sas.data.tag;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import screret.sas.SpellsAndSorcerers;

public class SASBlockTagsProvider extends BlockTagsProvider {

    public SASBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    protected void addTags() {

    }
}
