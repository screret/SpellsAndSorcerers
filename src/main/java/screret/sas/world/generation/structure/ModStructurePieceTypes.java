package screret.sas.world.generation.structure;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.world.generation.structure.structure.pieces.RitualSpotPiece;

public class ModStructurePieceTypes {

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, SpellsAndSorcerers.MODID);


    public static final RegistryObject<StructurePieceType> RITUAL_SPOT_PIECE_TYPE = STRUCTURE_PIECE_TYPES.register("ritual_spot", () ->
            (pContext, pTag) -> new RitualSpotPiece(pContext.structureTemplateManager(), pTag));
}
