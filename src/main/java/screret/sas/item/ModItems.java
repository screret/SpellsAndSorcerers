package screret.sas.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.item.item.WandCoreItem;
import screret.sas.item.item.WandItem;

public class ModItems {

    // Create a Deferred Register to hold Items which will all be registered under the "sas" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpellsAndSorcerers.MODID);


    //BLOCK ITEMS
    public static final RegistryObject<Item> WAND_CRAFTER = ITEMS.register("wand_crafter", () -> new BlockItem(ModBlocks.WAND_CRAFTER.get(), new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));


    // WANDS
    public static final RegistryObject<Item> WAND = ITEMS.register("wand", WandItem::new);

    public static final RegistryObject<Item> WAND_HANDLE = ITEMS.register("handle", () -> new Item(new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));


    //WAND CORES
    public static final RegistryObject<Item> WAND_CORE = ITEMS.register("wand_core", WandCoreItem::new);

}
