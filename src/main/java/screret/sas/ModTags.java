package screret.sas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import screret.sas.Util;

public class ModTags {

    public static final class Items {
        public static final TagKey<Item> GLASS_BOTTLES = forgeTag("glass_bottles");




        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(Util.resource(name));
        }
        private static TagKey<Item> forgeTag(String name)
        {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static final class Blocks {


        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(Util.resource(name));
        }
        private static TagKey<Block> forgeTag(String name)
        {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
