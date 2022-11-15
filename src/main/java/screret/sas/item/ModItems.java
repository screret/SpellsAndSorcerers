package screret.sas.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.entity.ModEntities;
import screret.sas.item.item.WandCoreItem;
import screret.sas.item.item.WandItem;

public class ModItems {

    // Create a Deferred Register to hold Items which will all be registered under the "sas" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpellsAndSorcerers.MODID);


    //BLOCK ITEMS
    public static final RegistryObject<Item> WAND_TABLE = ITEMS.register("wand_table", () -> new BlockItem(ModBlocks.WAND_TABLE.get(), new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));
    public static final RegistryObject<Item> PALANTIR = ITEMS.register("palantir", () -> new BlockItem(ModBlocks.PALANTIR.get(), new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));


    // WANDS
    public static final RegistryObject<Item> WAND = ITEMS.register("wand", WandItem::new);

    public static final RegistryObject<Item> WAND_HANDLE = ITEMS.register("handle", () -> new Item(new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));


    //OTHER ITEMS
    public static final RegistryObject<Item> WAND_CORE = ITEMS.register("wand_core", WandCoreItem::new);
    public static final RegistryObject<Item> SOUL_BOTTLE = ITEMS.register("soul_bottle", () -> new Item(new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));
    public static final RegistryObject<Item> CTHULHU_EYE = ITEMS.register("cthulhu_eye", () -> new Item(new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));

    public static final RegistryObject<Item> SUMMON_SIGN = ITEMS.register("summon_sign", () -> new BlockItem(ModBlocks.SUMMON_SIGN.get(), new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));

    public static final RegistryObject<Item> WIZARD_SPAWN_EGG = ITEMS.register("wizard_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.WIZARD, 0x002017, 0x959b9b, new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));
    public static final RegistryObject<Item> BOSS_WIZARD_SPAWN_EGG = ITEMS.register("boss_wizard_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.BOSS_WIZARD, 0x9a080f, 0x959b9b, new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB)));

}
