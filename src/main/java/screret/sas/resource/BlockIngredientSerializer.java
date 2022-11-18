package screret.sas.resource;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Stream;

public class BlockIngredientSerializer
{
    public static final BlockIngredientSerializer INSTANCE  = new BlockIngredientSerializer();

    public BlockIngredient parse(FriendlyByteBuf buffer)
    {
        return BlockIngredient.fromValues(Stream.generate(() -> new BlockIngredient.BlockValue(buffer.readResourceLocation())).limit(buffer.readVarInt()));
    }

    public BlockIngredient parse(JsonObject json)
    {
        return BlockIngredient.fromValues(Stream.of(BlockIngredient.valueFromJson(json)));
    }

    public void write(FriendlyByteBuf buffer, BlockIngredient ingredient)
    {
        Block[] items = ingredient.getBlocks();
        buffer.writeVarInt(items.length);

        for (Block stack : items)
            buffer.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(stack));
    }
}
