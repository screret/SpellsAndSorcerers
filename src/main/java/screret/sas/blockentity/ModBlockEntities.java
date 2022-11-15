package screret.sas.blockentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.blockentity.blockentity.SummonSignBE;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<BlockEntityType<SummonSignBE>> SUMMON_SIGN_BE = BLOCK_ENTITIES.register("summon_sign", () -> BlockEntityType.Builder.of(SummonSignBE::new, ModBlocks.SUMMON_SIGN.get()).build(null));
    public static final RegistryObject<BlockEntityType<PalantirBE>> PALANTIR_BE = BLOCK_ENTITIES.register("palantir", () -> BlockEntityType.Builder.of(PalantirBE::new, ModBlocks.PALANTIR.get()).build(null));

}
