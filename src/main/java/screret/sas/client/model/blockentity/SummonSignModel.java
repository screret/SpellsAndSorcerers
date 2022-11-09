package screret.sas.client.model.blockentity;

import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.SummonSignBE;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SummonSignModel extends AnimatedGeoModel<SummonSignBE> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/summon_sign.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/block/summon_sign.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/summon_sign.animation.json");

    @Override
    public ResourceLocation getModelResource(SummonSignBE object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(SummonSignBE object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(SummonSignBE animatable) {
        return ANIMATION_RESOURCE;
    }
}
