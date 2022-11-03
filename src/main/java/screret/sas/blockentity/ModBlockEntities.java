package screret.sas.blockentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SpellsAndSorcerers.MODID);

    //public static final RegistryObject<BlockEntityType<WandCraftingBE>> WAND_CRAFTING_BE = BLOCK_ENTITIES.register("wand_crafter", () -> BlockEntityType.Builder.of(WandCraftingBE::new, ModBlocks.WAND_CRAFTER.get()).build(null));
}
