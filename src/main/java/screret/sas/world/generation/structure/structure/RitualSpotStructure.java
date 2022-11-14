package screret.sas.world.generation.structure.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import screret.sas.Util;
import screret.sas.world.generation.structure.ModStructureTypes;
import screret.sas.world.generation.structure.structure.pieces.RitualSpotPiece;

import java.util.Optional;

public class RitualSpotStructure extends Structure {

    public static final Codec<RitualSpotStructure> CODEC = RecordCodecBuilder.create((builder) -> {
        return builder.group(settingsCodec(builder), HeightProvider.CODEC.fieldOf("height").forGetter((structure) -> {
            return structure.height;
        })).apply(builder, RitualSpotStructure::new);
    });
    public final HeightProvider height;

    public RitualSpotStructure(Structure.StructureSettings config, HeightProvider height) {
        super(config);
        this.height = height;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext genContext) {
        ChunkPos chunkpos = genContext.chunkPos();
        WorldgenRandom worldgenrandom = genContext.random();
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), genContext.chunkGenerator().getFirstFreeHeight(chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, genContext.heightAccessor(), genContext.randomState()) - 1, chunkpos.getMinBlockZ());
        Rotation rotation = Rotation.getRandom(worldgenrandom);
        builder.addPiece(new RitualSpotPiece(genContext.structureTemplateManager(), Util.resource("ritual_spot/top"), blockpos, rotation));
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.RITUAL_SPOT.get();
    }
}
