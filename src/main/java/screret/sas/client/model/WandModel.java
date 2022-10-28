package screret.sas.client.model;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.*;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.capability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
public class WandModel implements IUnbakedGeometry<WandModel> {
    private final WandAbility ability;

    /** Shared loader instance */
    public static final Loader LOADER = new Loader();

    public WandModel(WandAbility ability)
    {
        this.ability = ability;
    }

    public WandModel withAbility(WandAbility newAbility)
    {
        return new WandModel(newAbility);
    }

    public static RenderTypeGroup getLayerRenderTypes(boolean unlit)
    {
        return new RenderTypeGroup(RenderType.translucent(), unlit ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        Material baseLocation = context.hasMaterial("ability") ? context.getMaterial("ability") : null;

        TextureAtlasSprite baseSprite = baseLocation == null ? null : spriteGetter.apply(baseLocation);

        var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);
        var modelBuilder = new SimpleBakedModel.Builder(false, false, false, context.getTransforms(), new MaterialOverrideHandler(overrides, bakery, itemContext, this));

        if (baseLocation != null && baseSprite != null)
        {
            // Base texture
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, mat -> baseSprite, modelState, modelLocation);
            for(var quad : quads){
                modelBuilder.addUnculledFace(quad);
            }
        }

        modelBuilder.particle(baseSprite);

        return modelBuilder.build(getLayerRenderTypes(false));
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = Sets.newHashSet();
        if (context.hasMaterial("ability")) textures.add(context.getMaterial("ability"));
        return textures;
    }


    private static class Loader implements IGeometryLoader<WandModel> {

        @Override
        public WandModel read(JsonObject contents, JsonDeserializationContext deserializationContext) throws JsonParseException {
            WandAbility ability = null;
            if(contents.has("ability")){
                ability = WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(new ResourceLocation(GsonHelper.getAsString(contents, "ability")));
            }

            return new WandModel(ability);
        }
    }

    private static final class MaterialOverrideHandler extends ItemOverrides {
        // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
        private final Map<String, BakedModel> cache = new ConcurrentHashMap<>();

        private final ItemOverrides nested;
        private final ModelBakery bakery;
        private final IGeometryBakingContext owner;
        private final WandModel parent;

        private MaterialOverrideHandler(ItemOverrides nested, ModelBakery bakery, IGeometryBakingContext owner, WandModel parent) {
            this.nested = nested;
            this.bakery = bakery;
            this.owner = owner;
            this.parent = parent;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            return stack.getCapability(WandAbilityProvider.WAND_ABILITY).map(cap -> {
                var ability = cap.getAbility();
                String id = ability.getId().toString();

                if (!cache.containsKey(id))
                {
                    WandModel unbaked = this.parent.withAbility(ability.getAbility());
                    BakedModel bakedModel = unbaked.bake(owner, bakery, Material::sprite, BlockModelRotation.X0_Y0, this, new ResourceLocation(SpellsAndSorcerers.MODID, "ability_override"));
                    cache.put(id, bakedModel);
                    return bakedModel;
                }

                return cache.get(id);
            }).orElse(originalModel);
        }
    }
}
