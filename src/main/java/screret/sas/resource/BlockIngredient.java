package screret.sas.resource;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockIngredient implements Predicate<Block>  {

    public static final BlockIngredient EMPTY = new BlockIngredient(Stream.empty());
    private final BlockIngredient.Value[] values;
    @Nullable
    private Block[] blocks;

    protected BlockIngredient(Stream<? extends BlockIngredient.Value> pValues) {
        this.values = pValues.toArray(Value[]::new);
    }

    public Block[] getBlocks() {
        this.dissolve();
        return this.blocks;
    }

    private void dissolve() {
        if (this.blocks == null) {
            this.blocks = Arrays.stream(this.values).flatMap((value) -> {
                return value.getItems().stream();
            }).distinct().toArray(Block[]::new);
        }

    }

    public boolean test(@Nullable Block block) {
        if (block != null) {
            this.dissolve();

            for (Block element : this.blocks) {
                if (element == block) {
                    return true;
                }
            }

        }
        return false;
    }

    public final void toNetwork(FriendlyByteBuf pBuffer) {
        this.dissolve();
        pBuffer.writeCollection(Arrays.stream(this.blocks).map(ForgeRegistries.BLOCKS::getKey).toList(), FriendlyByteBuf::writeResourceLocation);
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for(BlockIngredient.Value value : this.values) {
                jsonarray.add(value.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0 && (this.blocks == null || this.blocks.length == 0);
    }

    protected void invalidate() {
        this.blocks = null;
    }

    public boolean isSimple() {
        return true;
    }

    public BlockIngredientSerializer getSerializer() {
        return BlockIngredientSerializer.INSTANCE;
    }

    public static BlockIngredient fromValues(Stream<? extends BlockIngredient.Value> pStream) {
        BlockIngredient BlockIngredient = new BlockIngredient(pStream);
        return BlockIngredient.values.length == 0 ? EMPTY : BlockIngredient;
    }

    public static BlockIngredient of() {
        return EMPTY;
    }

    public static BlockIngredient of(Block... pStacks) {
        return of(Arrays.stream(pStacks));
    }

    public static BlockIngredient of(Stream<Block> pStacks) {
        return fromValues(pStacks.filter(Objects::nonNull).map(BlockIngredient.BlockValue::new));
    }

    public static BlockIngredient of(TagKey<Block> pTag) {
        return fromValues(Stream.of(new BlockIngredient.TagValue(pTag)));
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf pBuffer) {
        var size = pBuffer.readVarInt();
        if (size == -1) return BlockIngredientSerializer.INSTANCE.parse(pBuffer);
        return fromValues(Stream.generate(() -> new BlockIngredient.BlockValue(pBuffer.readResourceLocation())).limit(size));
    }

    public static BlockIngredient fromJson(@Nullable JsonElement pJson) {
        if (pJson != null && !pJson.isJsonNull()) {
            BlockIngredient ret = BlockIngredientSerializer.INSTANCE.parse(pJson.getAsJsonObject());
            if (ret != null) return ret;
            if (pJson.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(pJson.getAsJsonObject())));
            } else if (pJson.isJsonArray()) {
                JsonArray jsonarray = pJson.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((element) -> {
                        return valueFromJson(GsonHelper.convertToJsonObject(element, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static BlockIngredient.Value valueFromJson(JsonObject pJson) {
        if (pJson.has("block") && pJson.has("tag")) {
            throw new JsonParseException("An BlockIngredient entry is either a tag or an item, not both");
        } else if (pJson.has("block")) {
            return new BlockIngredient.BlockValue(blockFromJson(pJson));
        } else if (pJson.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pJson, "tag"));
            TagKey<Block> key = TagKey.create(Registry.BLOCK_REGISTRY, resourcelocation);
            return new BlockIngredient.TagValue(key);
        } else {
            throw new JsonParseException("An BlockIngredient entry needs either a tag or an item");
        }
    }

    //Merges several vanilla BlockIngredients together. As a quirk of how the json is structured, we can't tell if it's a single BlockIngredient type or multiple, so we split per item and re-merge here.
    //Only public for internal use, so we can access a private field in here.
    public static BlockIngredient merge(Collection<BlockIngredient> parts) {
        return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
    }

    public static Block blockFromJson(JsonObject pItemObject) {
        String s = GsonHelper.getAsString(pItemObject, "item");
        Block item = Registry.BLOCK.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (item == Blocks.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        } else {
            return item;
        }
    }

    public static class BlockValue implements BlockIngredient.Value {
        private final Block block;

        public BlockValue(Block block) {
            this.block = block;
        }

        public BlockValue(ResourceLocation location) {
            this.block = ForgeRegistries.BLOCKS.getValue(location);
        }

        public Collection<Block> getItems() {
            return Collections.singleton(this.block);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            return jsonobject;
        }
    }

    public static class TagValue implements BlockIngredient.Value {
        private final TagKey<Block> tag;

        public TagValue(TagKey<Block> pTag) {
            this.tag = pTag;
        }

        public Collection<Block> getItems() {
            List<Block> list = Lists.newArrayList();

            for(Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(this.tag)) {
                list.add(holder.value());
            }

            if (list.size() == 0) {
                list.add(Blocks.BARRIER);
            }
            return list;
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.location().toString());
            return jsonobject;
        }
    }

    public interface Value {
        Collection<Block> getItems();

        JsonObject serialize();
    }
}
