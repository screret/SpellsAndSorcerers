package screret.sas.client.renderer.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomItemOverride<T> {
    private final ResourceLocation model;
    private final List<Predicate<T>> predicates;

    public CustomItemOverride(ResourceLocation pModel, List<Predicate<T>> pPredicates) {
        this.model = pModel;
        this.predicates = ImmutableList.copyOf(pPredicates);
    }

    /**
     * @return the location of the target model
     */
    public ResourceLocation getModel() {
        return this.model;
    }

    public Stream<Predicate<T>> getPredicates() {
        return this.predicates.stream();
    }

    public static class Deserializer<T> implements JsonDeserializer<CustomItemOverride<T>> {
        protected final Type genericType;

        public Deserializer(){
            this.genericType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        public CustomItemOverride<T> deserialize(JsonElement pJson, Type pType, JsonDeserializationContext pContext) throws JsonParseException {
            JsonObject jsonobject = pJson.getAsJsonObject();
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "model"));
            List<Predicate<T>> list = this.getPredicates(jsonobject);
            return new CustomItemOverride<>(resourcelocation, list);
        }

        protected List<Predicate<T>> getPredicates(JsonObject pJson) {
            Map<ResourceLocation, T> map = Maps.newLinkedHashMap();
            JsonObject jsonobject = GsonHelper.getAsJsonObject(pJson, "predicate");

            for(Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                map.put(new ResourceLocation(entry.getKey()), new Gson().fromJson(entry.getValue(), genericType));
            }

            return map.entrySet().stream().map((entry) -> new Predicate<>(entry.getKey(), entry.getValue())).collect(ImmutableList.toImmutableList());
        }
    }

    public static class Predicate<T> {
        private final ResourceLocation property;
        private final T value;

        public Predicate(ResourceLocation pProperty, T pValue) {
            this.property = pProperty;
            this.value = pValue;
        }

        public ResourceLocation getProperty() {
            return this.property;
        }

        public T getValue() {
            return this.value;
        }
    }

    public static class PropertyMatcher<T> {
        public final int index;
        public final T value;

        PropertyMatcher(int pIndex, T pValue) {
            this.index = pIndex;
            this.value = pValue;
        }
    }
}