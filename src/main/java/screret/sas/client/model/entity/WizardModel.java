package screret.sas.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WizardModel extends AnimatedGeoModel<WizardEntity> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/wizard.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/entity/wizard/wizard.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/wizard.animation.json");

    @Override
    public ResourceLocation getModelResource(WizardEntity object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(WizardEntity object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(WizardEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}
