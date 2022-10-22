package screret.sas.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.item.wand.type.advanced.ExplosionWand;
import screret.sas.item.wand.type.basic.HealWand;
import screret.sas.item.wand.type.basic.RayWand;

public class ModItems {

    // Create a Deferred Register to hold Items which will all be registered under the "sas" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpellsAndSorcerers.MODID);


    //BLOCK ITEMS
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));


    // WANDS
    public static final RegistryObject<Item> RAY_WAND = ITEMS.register("ray_wand", RayWand::new);
    public static final RegistryObject<Item> EXPLOSION_WAND = ITEMS.register("explosion_wand", ExplosionWand::new);
    public static final RegistryObject<Item> HEAL_WAND = ITEMS.register("heal_wand", HealWand::new);


    //MISC ITEMS
}
