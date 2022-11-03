package screret.sas.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.block.WandCraftingBlock;

public class ModBlocks {

    // Create a Deferred Register to hold Blocks which will all be registered under the "sas" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SpellsAndSorcerers.MODID);


    // Creates a new Block with the id "sas:example_block", combining the namespace and path
    public static final RegistryObject<Block> WAND_CRAFTER = BLOCKS.register("wand_table", () -> new WandCraftingBlock(BlockBehaviour.Properties.of(Material.AMETHYST)));

}
