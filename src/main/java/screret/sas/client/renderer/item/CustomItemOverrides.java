package screret.sas.client.renderer.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CustomItemOverrides<T> extends ItemOverrides {
    public static final CustomItemOverrides EMPTY = new CustomItemOverrides();
    private final CustomItemOverrides.BakedOverride<T>[] overrides;
    private final ResourceLocation[] properties;

    protected CustomItemOverrides() {
        this.overrides = new CustomItemOverrides.BakedOverride[0];
        this.properties = new ResourceLocation[0];
    }

    public CustomItemOverrides(ModelBakery pBakery, UnbakedModel pModel, Function<ResourceLocation, UnbakedModel> pModelGetter, Function<net.minecraft.client.resources.model.Material, net.minecraft.client.renderer.texture.TextureAtlasSprite> textureGetter, List<CustomItemOverride<T>> pOverrides) {
        this.properties = pOverrides.stream().flatMap(CustomItemOverride::getPredicates).map(CustomItemOverride.Predicate::getProperty).distinct().toArray(ResourceLocation[]::new);
        Object2IntMap<ResourceLocation> map = new Object2IntOpenHashMap<>();

        for(int i = 0; i < this.properties.length; ++i) {
            map.put(this.properties[i], i);
        }

        List<BakedOverride<T>> list = Lists.newArrayList();

        for(int j = pOverrides.size() - 1; j >= 0; --j) {
            CustomItemOverride<T> override = pOverrides.get(j);
            BakedModel bakedmodel = this.bakeModel(pBakery, pModel, pModelGetter, textureGetter, override);
            Object[] matchers = override.getPredicates().map((value) -> {
                int k = map.getInt(value.getProperty());
                return new PropertyMatcher<>(k, value.getValue());
            }).toArray(PropertyMatcher[]::new);
            list.add(new CustomItemOverrides.BakedOverride<T>((CustomItemOverrides.PropertyMatcher<T>[])matchers, bakedmodel));
        }

        this.overrides = (CustomItemOverrides.BakedOverride<T>[])new Object[0];
    }

    @Nullable
    private BakedModel bakeModel(ModelBakery pBakery, UnbakedModel pModel, Function<ResourceLocation, UnbakedModel> pModelGetter, Function<net.minecraft.client.resources.model.Material, net.minecraft.client.renderer.texture.TextureAtlasSprite> textureGetter, CustomItemOverride<T> pOverride) {
        UnbakedModel unbakedmodel = pModelGetter.apply(pOverride.getModel());
        return Objects.equals(unbakedmodel, pModel) ? null : pBakery.bake(pOverride.getModel(), BlockModelRotation.X0_Y0, textureGetter);
    }

    @Nullable
    public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
        if (this.overrides.length != 0) {
            Item item = pStack.getItem();
            int i = this.properties.length;
            T[] array = (T[])new Object[i];

            for(int j = 0; j < i; ++j) {
                ResourceLocation resourcelocation = this.properties[j];
                CustomItemPropertyFunction<T> function = (CustomItemPropertyFunction<T>) ItemProperties.getProperty(item, resourcelocation);
                if (function != null) {
                    array[j] = (T) function.callNew(pStack, pLevel, pEntity, pSeed);
                } else {
                    array[j] = null;
                }
            }

            for(CustomItemOverrides.BakedOverride<T> override : this.overrides) {
                if (override.test(array)) {
                    BakedModel bakedmodel = override.model;
                    if (bakedmodel == null) {
                        return pModel;
                    }

                    return bakedmodel;
                }
            }
        }

        return pModel;
    }

    public ImmutableList<CustomItemOverrides.BakedOverride<T>> getOverridesNew() {
        return ImmutableList.copyOf(overrides);
    }

    public ImmutableList<ItemOverrides.BakedOverride> getOverrides() {
        return ImmutableList.of();
    }

    public static class BakedOverride<T> extends ItemOverrides.BakedOverride {
        public final CustomItemOverrides.PropertyMatcher<T>[] matchersNew;

        public BakedOverride(CustomItemOverrides.PropertyMatcher<T>[] pMatchers, @Nullable BakedModel pModel) {
            super(pMatchers, pModel);
            this.matchersNew = pMatchers;
        }

        public boolean test(T[] pProperties) {
            for(CustomItemOverrides.PropertyMatcher<T> matcher : this.matchersNew) {
                T value = pProperties[matcher.index];
                if (value == matcher.valueNew) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class PropertyMatcher<T> extends ItemOverrides.PropertyMatcher {
        public final T valueNew;

        public PropertyMatcher(int pIndex, T pValue) {
            super(pIndex, 0);
            this.valueNew = pValue;
        }
    }
}
